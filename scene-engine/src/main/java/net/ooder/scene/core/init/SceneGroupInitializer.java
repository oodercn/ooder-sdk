package net.ooder.scene.core.init;

import net.ooder.scene.core.*;
import net.ooder.scene.core.impl.SceneAgentBridge;
import net.ooder.scene.discovery.UnifiedSkillRegistry;
import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.scene.SceneAgentEvent;
import net.ooder.scene.group.persistence.SceneGroupPersistence;
import net.ooder.scene.group.persistence.SceneGroupPersistenceImpl;
import net.ooder.scene.participant.Participant;
import net.ooder.scene.skill.model.SceneType;
import net.ooder.sdk.api.capability.CapRegistry;
import net.ooder.sdk.api.capability.Capability;
import net.ooder.sdk.api.scene.SceneMember;
import net.ooder.sdk.common.enums.MemberRole;
import net.ooder.skills.api.SkillPackage;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 场景组初始化器
 *
 * <p>执行场景组的完整初始化流程，包括 6 个步骤：</p>
 *
 * <h3>初始化流程：</h3>
 * <ol>
 *   <li><b>场景加载</b> - 加载场景定义和配置</li>
 *   <li><b>Agent 初始化</b> - 创建 SceneAgent，分配角色</li>
 *   <li><b>CAP 解析</b> - 解析所需能力</li>
 *   <li><b>Skill 发现</b> - 查询匹配的 Skill</li>
 *   <li><b>Skill 挂载</b> - 创建连接器并挂载</li>
 *   <li><b>场景激活</b> - 启动场景组</li>
 * </ol>
 *
 * <p><b>2.3.1 新增：</b> 同步创建 SE SceneGroup 并持久化。</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.0
 */
public class SceneGroupInitializer {

    private static final Logger logger = LoggerFactory.getLogger(SceneGroupInitializer.class);
    
    private final net.ooder.sdk.api.scene.SceneGroupManager sdkSceneGroupManager;
    private final CapRegistry capRegistry;
    private final SceneEventPublisher eventPublisher;
    private final UnifiedSkillRegistry skillRegistry;
    private final Map<String, InitContext> initContexts = new ConcurrentHashMap<>();
    
    private SceneGroupPersistence sceneGroupPersistence;
    private net.ooder.scene.group.SceneGroupManager seSceneGroupManager;
    private SceneTypeHandler sceneTypeHandler;
    
    public SceneGroupInitializer(net.ooder.sdk.api.scene.SceneGroupManager sdkSceneGroupManager,
                                  CapRegistry capRegistry,
                                  SceneEventPublisher eventPublisher) {
        this(sdkSceneGroupManager, capRegistry, eventPublisher, null);
    }

    public SceneGroupInitializer(net.ooder.sdk.api.scene.SceneGroupManager sdkSceneGroupManager,
                                  CapRegistry capRegistry,
                                  SceneEventPublisher eventPublisher,
                                  UnifiedSkillRegistry skillRegistry) {
        this.sdkSceneGroupManager = sdkSceneGroupManager;
        this.capRegistry = capRegistry;
        this.eventPublisher = eventPublisher;
        this.skillRegistry = skillRegistry;
        
        initPersistence();
    }
    
    private void initPersistence() {
        try {
            this.sceneGroupPersistence = new SceneGroupPersistenceImpl();
        } catch (Exception e) {
            logger.warn("Failed to initialize persistence: {}", e.getMessage());
        }
    }
    
    public void setSeSceneGroupManager(net.ooder.scene.group.SceneGroupManager seManager) {
        this.seSceneGroupManager = seManager;
        this.sceneTypeHandler = new SceneTypeHandler(seManager);
    }

    public CompletableFuture<InitResult> initialize(InitRequest request) {
        InitContext context = new InitContext(request);
        initContexts.put(context.getInitId(), context);

        return CompletableFuture.supplyAsync(() -> {
            try {
                loadScene(context);
                initializeAgents(context);
                parseCapabilities(context);
                discoverSkills(context);
                mountSkills(context);
                activate(context);

                context.setStatus(InitStatus.COMPLETED);
                return InitResult.success(context);

            } catch (Exception e) {
                context.setStatus(InitStatus.FAILED);
                context.setErrorMessage(e.getMessage());
                return InitResult.failure(context, e.getMessage());
            }
        });
    }

    private void loadScene(InitContext context) {
        context.setStatus(InitStatus.LOADING_SCENE);

        InitRequest request = context.getRequest();

        net.ooder.sdk.api.scene.SceneGroupManager.SceneGroupConfig config = 
            new net.ooder.sdk.api.scene.SceneGroupManager.SceneGroupConfig();
        config.setSceneId(request.getSceneId());
        config.setMinMembers(request.getMinMembers());
        config.setMaxMembers(request.getMaxMembers());
        config.setHeartbeatInterval(request.getHeartbeatInterval());
        config.setHeartbeatTimeout(request.getHeartbeatTimeout());
        config.setKeyThreshold(request.getKeyThreshold());
        config.setProperties(request.getProperties());

        context.setGroupConfig(config);

        publishEvent(SceneAgentEvent.created(
            context.getInitId(),
            request.getSceneId(),
            request.getSceneName(),
            request.getUserId()
        ));
    }

    private void initializeAgents(InitContext context) {
        context.setStatus(InitStatus.INITIALIZING_AGENTS);

        InitRequest request = context.getRequest();
        List<AgentConfig> agentConfigs = request.getAgentConfigs();

        if (agentConfigs == null || agentConfigs.isEmpty()) {
            agentConfigs = Collections.singletonList(
                new AgentConfig(MemberRole.PRIMARY, request.getUserId())
            );
        }

        for (AgentConfig agentConfig : agentConfigs) {
            SceneAgentCore agent = createAgent(agentConfig);
            context.addAgent(agent);

            SceneMemberInfo member = new SceneMemberInfo();
            member.setMemberId(agent.getAgentId());
            member.setRole(agentConfig.getRole());
            member.setUserId(agentConfig.getUserId());
            context.addMember(member);
        }
    }

    private SceneAgentCore createAgent(AgentConfig config) {
        SceneAgentBridge agent = new SceneAgentBridge();
        agent.setMemberRole(config.getRole());
        return agent;
    }

    private void parseCapabilities(InitContext context) {
        context.setStatus(InitStatus.PARSING_CAPS);

        InitRequest request = context.getRequest();
        List<String> requiredCaps = request.getRequiredCapabilities();
        List<String> optionalCaps = request.getOptionalCapabilities();

        if (requiredCaps != null) {
            for (String capId : requiredCaps) {
                Capability cap = capRegistry.findById(capId);
                if (cap != null) {
                    context.addRequiredCapability(cap);
                } else {
                    throw new InitException("Required capability not found: " + capId);
                }
            }
        }

        if (optionalCaps != null) {
            for (String capId : optionalCaps) {
                Capability cap = capRegistry.findById(capId);
                if (cap != null) {
                    context.addOptionalCapability(cap);
                }
            }
        }
    }

    private void discoverSkills(InitContext context) {
        context.setStatus(InitStatus.DISCOVERING_SKILLS);

        for (Capability cap : context.getRequiredCapabilities()) {
            List<SkillMatch> matches = findMatchingSkills(cap);
            context.addSkillMatches(cap.getCapId(), matches);
        }

        for (Capability cap : context.getOptionalCapabilities()) {
            List<SkillMatch> matches = findMatchingSkills(cap);
            context.addSkillMatches(cap.getCapId(), matches);
        }
    }

    private List<SkillMatch> findMatchingSkills(Capability capability) {
        List<SkillMatch> matches = new ArrayList<>();
        
        if (skillRegistry == null) {
            return matches;
        }

        try {
            String capId = capability.getCapId();
            List<SkillPackage> allSkills = skillRegistry.getAllSkills().join();
            
            for (SkillPackage pkg : allSkills) {
                if (pkg.getCapabilities() != null && 
                    pkg.getCapabilities().stream().anyMatch(c -> c.getCapId().equals(capId))) {
                    
                    SkillMatch match = new SkillMatch();
                    match.setSkillId(pkg.getSkillId());
                    match.setSkillName(pkg.getName());
                    match.setConnectorType(determineConnectorType(pkg));
                    match.setEndpoint(getMetadataString(pkg, "endpoint"));
                    match.setPriority(getMetadataInt(pkg, "priority", 0));
                    match.setScore(calculateMatchScore(capability, pkg));
                    
                    matches.add(match);
                }
            }
            
            matches.sort((a, b) -> {
                int priorityCompare = Integer.compare(b.getPriority(), a.getPriority());
                if (priorityCompare != 0) return priorityCompare;
                return Double.compare(b.getScore(), a.getScore());
            });
            
        } catch (Exception e) {
            logger.warn("Error finding matching skills: {}", e.getMessage());
        }
        
        return matches;
    }

    private String getMetadataString(SkillPackage pkg, String key) {
        if (pkg.getMetadata() != null) {
            Object value = pkg.getMetadata().get(key);
            return value != null ? value.toString() : null;
        }
        return null;
    }

    private int getMetadataInt(SkillPackage pkg, String key, int defaultValue) {
        if (pkg.getMetadata() != null) {
            Object value = pkg.getMetadata().get(key);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
        }
        return defaultValue;
    }

    private String determineConnectorType(SkillPackage pkg) {
        String runtime = getMetadataString(pkg, "runtime");
        if (runtime == null) {
            return "HTTP";
        }
        
        switch (runtime.toLowerCase()) {
            case "http":
            case "rest":
                return "HTTP";
            case "grpc":
                return "GRPC";
            case "websocket":
            case "ws":
                return "WEBSOCKET";
            case "udp":
                return "UDP";
            case "local":
            case "jar":
                return "LOCAL_JAR";
            default:
                return "HTTP";
        }
    }

    private double calculateMatchScore(Capability capability, SkillPackage pkg) {
        double score = 1.0;
        
        if (pkg.getTags() != null && capability.getTags() != null) {
            long matchingTags = pkg.getTags().stream()
                .filter(tag -> capability.getTags().contains(tag))
                .count();
            score += matchingTags * 0.1;
        }
        
        Double rating = getMetadataDouble(pkg, "rating");
        if (rating != null) {
            score += rating * 0.2;
        }
        
        return score;
    }

    private Double getMetadataDouble(SkillPackage pkg, String key) {
        if (pkg.getMetadata() != null) {
            Object value = pkg.getMetadata().get(key);
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        }
        return null;
    }

    private void mountSkills(InitContext context) {
        context.setStatus(InitStatus.MOUNTING_SKILLS);

        for (SceneAgentCore agent : context.getAgents()) {
            for (Map.Entry<String, List<SkillMatch>> entry : context.getSkillMatches().entrySet()) {
                String capId = entry.getKey();
                List<SkillMatch> matches = entry.getValue();

                if (!matches.isEmpty()) {
                    SkillMatch best = matches.get(0);

                    SceneConfig skillConfig = new SceneConfig(best.getSkillId());
                    skillConfig.setProperty("capId", capId);
                    skillConfig.setProperty("connectorType", best.getConnectorType());
                    skillConfig.setProperty("endpoint", best.getEndpoint());
                    skillConfig.setProperty("priority", best.getPriority());

                    agent.mountSkill(best.getSkillId(), skillConfig);

                    SkillBinding binding = new SkillBinding(best.getSkillId(), capId, best.getPriority());
                    context.addSkillBinding(capId, binding);
                }
            }
        }
    }

    private void activate(InitContext context) {
        context.setStatus(InitStatus.ACTIVATING);

        InitRequest request = context.getRequest();

        try {
            net.ooder.sdk.api.scene.SceneGroup sdkGroup = sdkSceneGroupManager.create(
                request.getSceneId(),
                context.getGroupConfig()
            ).join();

            context.setSceneGroup(sdkGroup);

            for (SceneAgentCore agent : context.getAgents()) {
                MemberRole role = agent.getMemberRole();
                if (role == null) {
                    role = MemberRole.MEMBER;
                }
                sdkSceneGroupManager.join(sdkGroup.getSceneGroupId(), agent.getAgentId(), role).join();
                agent.setGroupId(sdkGroup.getSceneGroupId());
            }

            for (SceneAgentCore agent : context.getAgents()) {
                SceneConfig config = new SceneConfig(agent.getAgentId());
                config.setProperty("sceneId", request.getSceneId());
                config.setProperty("groupId", sdkGroup.getSceneGroupId());
                agent.initialize(config);
            }

            sdkSceneGroupManager.startHeartbeat(sdkGroup.getSceneGroupId());
            
            syncCreateSeSceneGroup(context, sdkGroup);

            publishEvent(SceneAgentEvent.activated(
                context.getInitId(),
                request.getSceneId(),
                request.getSceneName(),
                request.getUserId()
            ));

        } catch (Exception e) {
            throw new InitException("Failed to activate scene group: " + e.getMessage(), e);
        }
    }
    
    private void syncCreateSeSceneGroup(InitContext context, net.ooder.sdk.api.scene.SceneGroup sdkGroup) {
        if (seSceneGroupManager == null) {
            logger.warn("SE SceneGroupManager not set, skipping SE SceneGroup creation");
            return;
        }
        
        try {
            InitRequest request = context.getRequest();
            String sceneGroupId = sdkGroup.getSceneGroupId();
            String templateId = request.getSceneId();
            String creatorId = request.getUserId();
            net.ooder.scene.group.SceneGroup.CreatorType creatorType = net.ooder.scene.group.SceneGroup.CreatorType.USER;
            
            net.ooder.scene.group.SceneGroup seGroup = seSceneGroupManager.createSceneGroup(
                sceneGroupId,
                templateId,
                creatorId,
                creatorType
            );
            
            seGroup.setName(request.getSceneName());
            
            syncParticipants(context, seGroup);
            
            if (sceneTypeHandler != null) {
                SceneType sceneType = detectSceneType(request);
                sceneTypeHandler.applyBehavior(seGroup, sceneType);
            }
            
            if (sceneGroupPersistence != null) {
                sceneGroupPersistence.save(seGroup);
            }
            
            logger.info("Synced SE SceneGroup: {}", sceneGroupId);
            
        } catch (Exception e) {
            logger.error("Failed to sync SE SceneGroup: " + e.getMessage(), e);
        }
    }
    
    private void syncParticipants(InitContext context, net.ooder.scene.group.SceneGroup seGroup) {
        for (SceneAgentCore agent : context.getAgents()) {
            Participant participant = createParticipantFromAgent(agent, seGroup.getSceneGroupId());
            seGroup.addParticipant(participant);
        }
        
        String creatorId = context.getRequest().getUserId();
        if (creatorId != null) {
            Participant creator = new Participant(
                seGroup.getSceneGroupId() + "-p-creator",
                creatorId,
                creatorId,
                Participant.Type.USER
            );
            creator.setRole(Participant.Role.OWNER);
            seGroup.addParticipant(creator);
        }
    }
    
    private Participant createParticipantFromAgent(SceneAgentCore agent, String sceneGroupId) {
        String participantId = sceneGroupId + "-p-" + agent.getAgentId();
        String userId = agent.getAgentId();
        
        Participant.Role role;
        MemberRole memberRole = agent.getMemberRole();
        if (memberRole == MemberRole.PRIMARY) {
            role = Participant.Role.OWNER;
        } else if (memberRole == MemberRole.BACKUP) {
            role = Participant.Role.EMPLOYEE;
        } else {
            role = Participant.Role.OBSERVER;
        }
        
        Participant.Type type = Participant.Type.USER;
        
        Participant participant = new Participant(participantId, userId, userId, type);
        participant.setRole(role);
        return participant;
    }
    
    private SceneType detectSceneType(InitRequest request) {
        Object sceneTypeObj = request.getProperties().get("sceneType");
        if (sceneTypeObj != null) {
            String sceneTypeStr = sceneTypeObj.toString().toUpperCase();
            try {
                return SceneType.valueOf(sceneTypeStr);
            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }
        
        return SceneType.HYBRID;
    }

    private void publishEvent(SceneAgentEvent event) {
        if (eventPublisher != null) {
            eventPublisher.publish(event);
        }
    }

    public InitContext getInitContext(String initId) {
        return initContexts.get(initId);
    }

    public CompletableFuture<Boolean> cancel(String initId) {
        return CompletableFuture.supplyAsync(() -> {
            InitContext context = initContexts.get(initId);
            if (context == null) {
                return false;
            }

            context.setStatus(InitStatus.CANCELLED);

            for (SceneAgentCore agent : context.getAgents()) {
                try {
                    agent.shutdown();
                } catch (Exception e) {
                    // Ignore
                }
            }

            return true;
        });
    }

    public static class InitContext {
        private final String initId;
        private final InitRequest request;
        private volatile InitStatus status = InitStatus.CREATED;
        private String errorMessage;

        private net.ooder.sdk.api.scene.SceneGroupManager.SceneGroupConfig groupConfig;
        private net.ooder.sdk.api.scene.SceneGroup sceneGroup;

        private final List<SceneAgentCore> agents = new ArrayList<>();
        private final List<SceneMemberInfo> members = new ArrayList<>();
        private final List<Capability> requiredCapabilities = new ArrayList<>();
        private final List<Capability> optionalCapabilities = new ArrayList<>();
        private final Map<String, List<SkillMatch>> skillMatches = new ConcurrentHashMap<>();
        private final List<SkillBinding> skillBindings = new ArrayList<>();

        public InitContext(InitRequest request) {
            this.initId = "init-" + UUID.randomUUID().toString();
            this.request = request;
        }

        public String getInitId() { return initId; }
        public InitRequest getRequest() { return request; }
        public InitStatus getStatus() { return status; }
        public void setStatus(InitStatus status) { this.status = status; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public net.ooder.sdk.api.scene.SceneGroupManager.SceneGroupConfig getGroupConfig() { return groupConfig; }
        public void setGroupConfig(net.ooder.sdk.api.scene.SceneGroupManager.SceneGroupConfig groupConfig) { this.groupConfig = groupConfig; }
        public net.ooder.sdk.api.scene.SceneGroup getSceneGroup() { return sceneGroup; }
        public void setSceneGroup(net.ooder.sdk.api.scene.SceneGroup sceneGroup) { this.sceneGroup = sceneGroup; }
        public List<SceneAgentCore> getAgents() { return agents; }
        public void addAgent(SceneAgentCore agent) { agents.add(agent); }
        public List<SceneMemberInfo> getMembers() { return members; }
        public void addMember(SceneMemberInfo member) { members.add(member); }
        public List<Capability> getRequiredCapabilities() { return requiredCapabilities; }
        public void addRequiredCapability(Capability cap) { requiredCapabilities.add(cap); }
        public List<Capability> getOptionalCapabilities() { return optionalCapabilities; }
        public void addOptionalCapability(Capability cap) { optionalCapabilities.add(cap); }
        public Map<String, List<SkillMatch>> getSkillMatches() { return skillMatches; }
        public void addSkillMatches(String capId, List<SkillMatch> matches) { skillMatches.put(capId, matches); }
        public List<SkillBinding> getSkillBindings() { return skillBindings; }
        public void addSkillBinding(String capId, SkillBinding binding) { skillBindings.add(binding); }
    }

    public enum InitStatus {
        CREATED,
        LOADING_SCENE,
        INITIALIZING_AGENTS,
        PARSING_CAPS,
        DISCOVERING_SKILLS,
        MOUNTING_SKILLS,
        ACTIVATING,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    public static class InitRequest {
        private String sceneId;
        private String sceneName;
        private String userId;
        private int minMembers = 1;
        private int maxMembers = 10;
        private int heartbeatInterval = 5000;
        private int heartbeatTimeout = 15000;
        private int keyThreshold = 2;
        private Map<String, Object> properties = new HashMap<>();
        private List<AgentConfig> agentConfigs = new ArrayList<>();
        private List<String> requiredCapabilities = new ArrayList<>();
        private List<String> optionalCapabilities = new ArrayList<>();

        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getSceneName() { return sceneName; }
        public void setSceneName(String sceneName) { this.sceneName = sceneName; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public int getMinMembers() { return minMembers; }
        public void setMinMembers(int minMembers) { this.minMembers = minMembers; }
        public int getMaxMembers() { return maxMembers; }
        public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }
        public int getHeartbeatInterval() { return heartbeatInterval; }
        public void setHeartbeatInterval(int heartbeatInterval) { this.heartbeatInterval = heartbeatInterval; }
        public int getHeartbeatTimeout() { return heartbeatTimeout; }
        public void setHeartbeatTimeout(int heartbeatTimeout) { this.heartbeatTimeout = heartbeatTimeout; }
        public int getKeyThreshold() { return keyThreshold; }
        public void setKeyThreshold(int keyThreshold) { this.keyThreshold = keyThreshold; }
        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
        public List<AgentConfig> getAgentConfigs() { return agentConfigs; }
        public void setAgentConfigs(List<AgentConfig> agentConfigs) { this.agentConfigs = agentConfigs; }
        public List<String> getRequiredCapabilities() { return requiredCapabilities; }
        public void setRequiredCapabilities(List<String> requiredCapabilities) { this.requiredCapabilities = requiredCapabilities; }
        public List<String> getOptionalCapabilities() { return optionalCapabilities; }
        public void setOptionalCapabilities(List<String> optionalCapabilities) { this.optionalCapabilities = optionalCapabilities; }
    }

    public static class AgentConfig {
        private MemberRole role;
        private String userId;
        private String domainId;
        private Map<String, Object> config = new HashMap<>();

        public AgentConfig(MemberRole role, String userId) {
            this.role = role;
            this.userId = userId;
        }

        public MemberRole getRole() { return role; }
        public void setRole(MemberRole role) { this.role = role; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getDomainId() { return domainId; }
        public void setDomainId(String domainId) { this.domainId = domainId; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
    }

    public static class SkillMatch {
        private String skillId;
        private String skillName;
        private String connectorType;
        private String endpoint;
        private int priority;
        private double score;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getSkillName() { return skillName; }
        public void setSkillName(String skillName) { this.skillName = skillName; }
        public String getConnectorType() { return connectorType; }
        public void setConnectorType(String connectorType) { this.connectorType = connectorType; }
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
    }

    public static class InitResult {
        private final boolean success;
        private final InitContext context;
        private final String errorMessage;

        private InitResult(boolean success, InitContext context, String errorMessage) {
            this.success = success;
            this.context = context;
            this.errorMessage = errorMessage;
        }

        public static InitResult success(InitContext context) {
            return new InitResult(true, context, null);
        }

        public static InitResult failure(InitContext context, String errorMessage) {
            return new InitResult(false, context, errorMessage);
        }

        public boolean isSuccess() { return success; }
        public InitContext getContext() { return context; }
        public String getErrorMessage() { return errorMessage; }
    }

    public static class InitException extends RuntimeException {
        public InitException(String message) {
            super(message);
        }

        public InitException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
