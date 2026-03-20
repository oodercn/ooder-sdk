package net.ooder.scene.bridge;

import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SeSceneGroup;
import net.ooder.scene.group.SceneGroupManager;
import net.ooder.scene.participant.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * SDK-SE SceneGroup 桥接实现
 * 
 * <p>本实现为轻量级适配层，核心同步逻辑由 SDK 侧 BidirectionalSyncCoordinator 提供。</p>
 * 
 * <h3>职责划分：</h3>
 * <ul>
 *   <li>SE 侧：提供 Participant 映射、健康检查、事件转发</li>
 *   <li>SDK 侧：双向同步、事件监听、熔断降级</li>
 * </ul>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneGroupBridgeImpl implements SceneGroupBridge {
    
    private static final Logger logger = LoggerFactory.getLogger(SceneGroupBridgeImpl.class);
    
    private final SceneGroupManager seSceneGroupManager;
    private final SdkSceneGroupProvider sdkProvider;
    private final Map<String, Set<SceneGroupEventListener>> listeners = new ConcurrentHashMap<>();
    private final Map<String, String> sdkToSeMapping = new ConcurrentHashMap<>();
    private final Map<String, String> seToSdkMapping = new ConcurrentHashMap<>();
    
    private volatile boolean available = true;
    private volatile long lastHealthCheckTime = 0;
    private volatile String healthMessage = "OK";
    
    public SceneGroupBridgeImpl(SceneGroupManager seSceneGroupManager, SdkSceneGroupProvider sdkProvider) {
        this.seSceneGroupManager = seSceneGroupManager;
        this.sdkProvider = sdkProvider;
    }
    
    @Override
    public Participant createParticipantFromMember(SceneMemberInfo member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        
        String participantId = member.getSceneGroupId() + "-p-" + member.getAgentId();
        
        Participant participant = new Participant(
            participantId,
            member.getAgentId(),
            member.getAgentId(),
            Participant.Type.USER
        );
        
        Participant.Role role = mapSdkRoleToSe(member.getRole());
        participant.setRole(role);
        
        return participant;
    }
    
    @Override
    public SceneMemberConfig createMemberConfigFromParticipant(Participant participant, String endpoint) {
        if (participant == null) {
            throw new IllegalArgumentException("Participant cannot be null");
        }
        
        return new ParticipantMemberConfig(participant, endpoint);
    }
    
    @Override
    public void syncFromSdkToSe(String sceneGroupId) {
        String seId = sdkToSeMapping.get(sceneGroupId);
        if (seId == null) {
            logger.warn("No SE SceneGroup found for SDK SceneGroup: {}", sceneGroupId);
            return;
        }
        
        SceneGroup seGroup = seSceneGroupManager.getSceneGroup(seId);
        if (seGroup == null) {
            logger.warn("SE SceneGroup not found: {}", seId);
            return;
        }
        
        List<SceneMemberInfo> sdkMembers = sdkProvider.getMembers(sceneGroupId);
        
        for (SceneMemberInfo member : sdkMembers) {
            Participant existing = seGroup.getParticipant(member.getAgentId());
            if (existing == null) {
                Participant participant = createParticipantFromMember(member);
                seGroup.addParticipant(participant);
            } else {
                Participant.Role newRole = mapSdkRoleToSe(member.getRole());
                if (existing.getRole() != newRole) {
                    existing.setRole(newRole);
                }
            }
        }
        
        logger.debug("Synced SDK -> SE for SceneGroup: {}", sceneGroupId);
    }
    
    @Override
    public void syncFromSeToSdk(String sceneGroupId) {
        SceneGroup seGroup = seSceneGroupManager.getSceneGroup(sceneGroupId);
        if (seGroup == null) {
            logger.warn("SE SceneGroup not found: {}", sceneGroupId);
            return;
        }
        
        String sdkId = seToSdkMapping.get(sceneGroupId);
        if (sdkId == null) {
            logger.warn("No SDK SceneGroup found for SE SceneGroup: {}", sceneGroupId);
            return;
        }
        
        List<Participant> participants = seGroup.getAllParticipants();
        
        for (Participant participant : participants) {
            String agentId = participant.getUserId();
            String sdkRole = mapSeRoleToSdk(participant.getRole());
            
            sdkProvider.updateMemberRole(sdkId, agentId, sdkRole);
        }
        
        logger.debug("Synced SE -> SDK for SceneGroup: {}", sceneGroupId);
    }
    
    @Override
    public Object getSdkSceneGroup(String sceneGroupId) {
        return sdkProvider.getSceneGroup(sceneGroupId);
    }
    
    @Override
    public SceneGroup getSeSceneGroup(String sceneGroupId) {
        return seSceneGroupManager.getSceneGroup(sceneGroupId);
    }
    
    @Override
    public SeSceneGroup getSeSceneGroupRef(String sceneGroupId) {
        SceneGroup group = seSceneGroupManager.getSceneGroup(sceneGroupId);
        if (group == null) {
            return null;
        }
        return seSceneGroupManager.getSeSceneGroupRef(sceneGroupId);
    }
    
    @Override
    public SeSceneGroup getOrCreateSeSceneGroup(String sdkSceneGroupId, String sceneId) {
        String seId = sdkToSeMapping.get(sdkSceneGroupId);
        if (seId == null) {
            seId = sdkSceneGroupId;
            registerMapping(sdkSceneGroupId, seId);
        }
        return seSceneGroupManager.getOrCreateSeSceneGroup(seId, sceneId);
    }
    
    @Override
    public void registerEventListener(SceneGroupEventListener listener) {
        if (listener == null) {
            return;
        }
        
        Set<SceneGroupEventListener> listenerSet = listeners.computeIfAbsent(
            "global", k -> new CopyOnWriteArraySet<>()
        );
        listenerSet.add(listener);
        
        logger.debug("Registered event listener: {}", listener.getClass().getSimpleName());
    }
    
    @Override
    public void unregisterEventListener(SceneGroupEventListener listener) {
        if (listener == null) {
            return;
        }
        
        for (Set<SceneGroupEventListener> listenerSet : listeners.values()) {
            listenerSet.remove(listener);
        }
        
        logger.debug("Unregistered event listener: {}", listener.getClass().getSimpleName());
    }
    
    @Override
    public BridgeHealthStatus healthCheck() {
        long startTime = System.currentTimeMillis();
        
        try {
            boolean sdkAvailable = sdkProvider != null;
            boolean seAvailable = seSceneGroupManager != null;
            
            if (sdkProvider != null) {
                sdkProvider.healthCheck();
            }
            
            available = sdkAvailable && seAvailable;
            healthMessage = available ? "OK" : "SDK or SE manager not available";
            
            long responseTime = System.currentTimeMillis() - startTime;
            lastHealthCheckTime = System.currentTimeMillis();
            
            Map<String, Object> details = new HashMap<>();
            details.put("sdkAvailable", sdkAvailable);
            details.put("seAvailable", seAvailable);
            details.put("mappingCount", sdkToSeMapping.size());
            
            return new BridgeHealthStatusImpl(
                available,
                available ? "UP" : "DOWN",
                healthMessage,
                responseTime,
                details
            );
            
        } catch (Exception e) {
            available = false;
            healthMessage = "Health check failed: " + e.getMessage();
            
            long responseTime = System.currentTimeMillis() - startTime;
            lastHealthCheckTime = System.currentTimeMillis();
            
            Map<String, Object> details = new HashMap<>();
            details.put("error", e.getMessage());
            
            return new BridgeHealthStatusImpl(
                false,
                "DOWN",
                healthMessage,
                responseTime,
                details
            );
        }
    }
    
    public void registerMapping(String sdkSceneGroupId, String seSceneGroupId) {
        sdkToSeMapping.put(sdkSceneGroupId, seSceneGroupId);
        seToSdkMapping.put(seSceneGroupId, sdkSceneGroupId);
        
        logger.info("Registered SceneGroup mapping: SDK={} <-> SE={}", sdkSceneGroupId, seSceneGroupId);
    }
    
    public void unregisterMapping(String sceneGroupId) {
        String sdkId = sdkToSeMapping.remove(sceneGroupId);
        if (sdkId != null) {
            seToSdkMapping.remove(sdkId);
        }
        
        logger.info("Unregistered SceneGroup mapping: {}", sceneGroupId);
    }
    
    private Participant.Role mapSdkRoleToSe(String sdkRole) {
        if (sdkRole == null) {
            return Participant.Role.OBSERVER;
        }
        
        switch (sdkRole.toUpperCase()) {
            case "PRIMARY":
                return Participant.Role.OWNER;
            case "BACKUP":
                return Participant.Role.EMPLOYEE;
            default:
                return Participant.Role.OBSERVER;
        }
    }
    
    private String mapSeRoleToSdk(Participant.Role seRole) {
        if (seRole == null) {
            return "MEMBER";
        }
        
        switch (seRole) {
            case OWNER:
            case MANAGER:
                return "PRIMARY";
            case EMPLOYEE:
            case OBSERVER:
                return "BACKUP";
            default:
                return "MEMBER";
        }
    }
    
    /**
     * SDK SceneGroup 提供者接口
     * 
     * <p>由 SDK 侧实现，提供场景组操作能力。</p>
     */
    public interface SdkSceneGroupProvider {
        Object getSceneGroup(String sceneGroupId);
        List<SceneMemberInfo> getMembers(String sceneGroupId);
        void updateMemberRole(String sceneGroupId, String agentId, String role);
        void healthCheck();
    }
    
    private static class ParticipantMemberConfig implements SceneMemberConfig {
        private final Participant participant;
        private final String endpoint;
        
        ParticipantMemberConfig(Participant participant, String endpoint) {
            this.participant = participant;
            this.endpoint = endpoint;
        }
        
        @Override
        public String getAgentId() {
            return participant.getUserId();
        }
        
        @Override
        public String getRole() {
            return participant.getRole() == Participant.Role.OWNER ? "PRIMARY" : "BACKUP";
        }
        
        @Override
        public String getEndpoint() {
            return endpoint;
        }
        
        @Override
        public Map<String, Object> getConfig() {
            return null;
        }
    }
    
    private static class BridgeHealthStatusImpl implements BridgeHealthStatus {
        private final boolean available;
        private final String status;
        private final String message;
        private final long responseTime;
        private final Map<String, Object> details;
        
        BridgeHealthStatusImpl(boolean available, String status, String message, 
                               long responseTime, Map<String, Object> details) {
            this.available = available;
            this.status = status;
            this.message = message;
            this.responseTime = responseTime;
            this.details = details;
        }
        
        @Override
        public boolean isAvailable() { return available; }
        
        @Override
        public String getStatus() { return status; }
        
        @Override
        public String getMessage() { return message; }
        
        @Override
        public long getResponseTime() { return responseTime; }
        
        @Override
        public Map<String, Object> getDetails() { return details; }
    }
}
