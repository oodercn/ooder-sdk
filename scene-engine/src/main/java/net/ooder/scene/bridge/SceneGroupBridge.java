package net.ooder.scene.bridge;

import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SeSceneGroup;
import net.ooder.scene.participant.Participant;

import java.util.List;
import java.util.Map;

/**
 * SDK-SE SceneGroup 桥接接口
 * 
 * <p>提供 SDK SceneGroup 与 SE SceneGroup 之间的双向映射和同步能力。</p>
 * 
 * <h3>职责划分：</h3>
 * <ul>
 *   <li>SE 侧：提供 Participant 映射、健康检查、事件转发</li>
 *   <li>SDK 侧：双向同步、事件监听、熔断降级（由 BidirectionalSyncCoordinator 提供）</li>
 * </ul>
 * 
 * <h3>架构说明：</h3>
 * <ul>
 *   <li>SDK SceneGroup: Agent 集群管理、用户场景组、技能/能力/知识库绑定</li>
 *   <li>SE SceneGroup: 业务上下文、工作流状态、审计日志</li>
 * </ul>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface SceneGroupBridge {
    
    Participant createParticipantFromMember(SceneMemberInfo member);
    
    SceneMemberConfig createMemberConfigFromParticipant(Participant participant, String endpoint);
    
    void syncFromSdkToSe(String sceneGroupId);
    
    void syncFromSeToSdk(String sceneGroupId);
    
    /**
     * 获取 SDK SceneGroup
     */
    Object getSdkSceneGroup(String sceneGroupId);
    
    /**
     * 获取 SE SceneGroup（原有接口，保留兼容）
     */
    SceneGroup getSeSceneGroup(String sceneGroupId);
    
    /**
     * 获取 SE 简化版 SceneGroup
     */
    SeSceneGroup getSeSceneGroupRef(String sceneGroupId);
    
    /**
     * 获取或创建 SE 简化版 SceneGroup
     */
    SeSceneGroup getOrCreateSeSceneGroup(String sdkSceneGroupId, String sceneId);
    
    void registerEventListener(SceneGroupEventListener listener);
    
    void unregisterEventListener(SceneGroupEventListener listener);
    
    /**
     * 健康检查
     * 
     * <p>检查 SDK 连接状态和 SE SceneGroup 管理器状态。</p>
     * 
     * @return 健康状态
     */
    BridgeHealthStatus healthCheck();
    
    /**
     * 桥接健康状态
     */
    interface BridgeHealthStatus {
        
        /**
         * 是否可用
         */
        boolean isAvailable();
        
        /**
         * 获取状态
         * 
         * @return UP, DOWN, DEGRADED
         */
        String getStatus();
        
        /**
         * 获取消息
         */
        String getMessage();
        
        /**
         * 获取响应时间（毫秒）
         */
        long getResponseTime();
        
        /**
         * 获取详细信息
         */
        Map<String, Object> getDetails();
    }
    
    interface SceneMemberInfo {
        String getMemberId();
        String getSceneGroupId();
        String getAgentId();
        String getRole();
        String getEndpoint();
        String getStatus();
        long getJoinTime();
        long getLastHeartbeatTime();
    }
    
    interface SceneMemberConfig {
        String getAgentId();
        String getRole();
        String getEndpoint();
        Map<String, Object> getConfig();
    }
    
    interface SceneGroupEventListener {
        void onMemberJoined(SceneMemberEvent event);
        void onMemberLeft(SceneMemberEvent event);
        void onRoleChanged(SceneMemberEvent event);
        void onStatusChanged(SceneGroupStatusEvent event);
        void onFailover(FailoverEvent event);
    }
    
    interface SceneMemberEvent {
        String getSceneGroupId();
        String getAgentId();
        String getOldRole();
        String getNewRole();
        long getTimestamp();
    }
    
    interface SceneGroupStatusEvent {
        String getSceneGroupId();
        String getOldStatus();
        String getNewStatus();
        long getTimestamp();
    }
    
    interface FailoverEvent {
        String getSceneGroupId();
        String getOldPrimary();
        String getNewPrimary();
        long getTimestamp();
    }
}
