package net.ooder.sdk.api.scene;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.ooder.sdk.api.scene.model.SceneConfig;
import net.ooder.sdk.api.scene.model.SceneLifecycleStats;
import net.ooder.sdk.api.scene.model.SceneState;
import net.ooder.sdk.api.capability.Capability;
import net.ooder.skills.sync.UserSceneGroup;

/**
 * 场景管理器接口
 * 
 * <p>提供场景的创建、删除、激活、停用等生命周期管理功能，
 * 以及场景能力管理和工作流管理功能。</p>
 * 
 * <p>场景是Ooder平台的核心概念，代表一个独立的运行环境，
 * 可以包含多个智能体、能力和工作流。</p>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2.3
 */
public interface SceneManager {
    
    /**
     * 创建场景
     * 
     * @param definition 场景定义，包含场景的基本信息和配置
     * @return 创建后的场景定义，包含生成的场景ID
     */
    CompletableFuture<SceneDefinition> create(SceneDefinition definition);
    
    /**
     * 删除场景
     * 
     * @param sceneId 场景ID
     * @return 完成信号
     */
    CompletableFuture<Void> delete(String sceneId);
    
    /**
     * 获取场景定义
     * 
     * @param sceneId 场景ID
     * @return 场景定义
     */
    CompletableFuture<SceneDefinition> get(String sceneId);
    
    /**
     * 获取所有场景列表
     * 
     * @return 场景定义列表
     */
    CompletableFuture<List<SceneDefinition>> listAll();
    
    /**
     * 激活场景
     * 
     * @param sceneId 场景ID
     * @return 完成信号
     */
    CompletableFuture<Void> activate(String sceneId);
    
    /**
     * 停用场景
     * 
     * @param sceneId 场景ID
     * @return 完成信号
     */
    CompletableFuture<Void> deactivate(String sceneId);
    
    /**
     * 获取场景状态
     * 
     * @param sceneId 场景ID
     * @return 场景状态
     */
    CompletableFuture<SceneState> getState(String sceneId);
    
    /**
     * 向场景添加能力
     * 
     * @param sceneId 场景ID
     * @param capability 能力对象
     * @return 完成信号
     */
    CompletableFuture<Void> addCapability(String sceneId, Capability capability);
    
    /**
     * 从场景移除能力
     * 
     * @param sceneId 场景ID
     * @param capId 能力ID
     * @return 完成信号
     */
    CompletableFuture<Void> removeCapability(String sceneId, String capId);
    
    /**
     * 获取场景的所有能力
     * 
     * @param sceneId 场景ID
     * @return 能力列表
     */
    CompletableFuture<List<Capability>> listCapabilities(String sceneId);
    
    /**
     * 获取场景的特定能力
     * 
     * @param sceneId 场景ID
     * @param capId 能力ID
     * @return 能力对象
     */
    CompletableFuture<Capability> getCapability(String sceneId, String capId);
    
    /**
     * 添加协作场景
     * 
     * @param sceneId 场景ID
     * @param collaborativeSceneId 协作场景ID
     * @return 完成信号
     */
    CompletableFuture<Void> addCollaborativeScene(String sceneId, String collaborativeSceneId);
    
    /**
     * 移除协作场景
     * 
     * @param sceneId 场景ID
     * @param collaborativeSceneId 协作场景ID
     * @return 完成信号
     */
    CompletableFuture<Void> removeCollaborativeScene(String sceneId, String collaborativeSceneId);
    
    /**
     * 获取协作场景列表
     * 
     * @param sceneId 场景ID
     * @return 协作场景ID列表
     */
    CompletableFuture<List<String>> listCollaborativeScenes(String sceneId);
    
    /**
     * 更新场景配置
     * 
     * @param sceneId 场景ID
     * @param config 配置信息
     * @return 完成信号
     */
    CompletableFuture<Void> updateConfig(String sceneId, Map<String, Object> config);
    
    /**
     * 获取场景配置
     * 
     * @param sceneId 场景ID
     * @return 配置信息
     */
    CompletableFuture<Map<String, Object>> getConfig(String sceneId);
    
    /**
     * 创建场景快照
     * 
     * @param sceneId 场景ID
     * @return 场景快照
     */
    CompletableFuture<SceneSnapshot> createSnapshot(String sceneId);
    
    /**
     * 恢复快照
     * 
     * @param sceneId 场景ID
     * @param snapshot 场景快照
     * @return 完成信号
     */
    CompletableFuture<Void> restoreSnapshot(String sceneId, SceneSnapshot snapshot);
    
    /**
     * 启动工作流
     * 
     * @param sceneId 场景ID
     * @param workflowId 工作流ID
     * @return 工作流实例ID
     */
    CompletableFuture<String> startWorkflow(String sceneId, String workflowId);
    
    /**
     * 停止工作流
     * 
     * @param sceneId 场景ID
     * @return 完成信号
     */
    CompletableFuture<Void> stopWorkflow(String sceneId);
    
    /**
     * 获取工作流状态
     * 
     * @param sceneId 场景ID
     * @return 工作流状态
     */
    CompletableFuture<String> getWorkflowStatus(String sceneId);
    
    /**
     * 初始化场景
     * 
     * @param sceneId 场景ID
     * @param config 场景配置
     * @return 完成信号
     */
    CompletableFuture<Void> initializeScene(String sceneId, SceneConfig config);
    
    /**
     * 启动场景
     * 
     * @param sceneId 场景ID
     * @return 完成信号
     */
    CompletableFuture<Void> startScene(String sceneId);
    
    /**
     * 停止场景
     * 
     * @param sceneId 场景ID
     * @return 完成信号
     */
    CompletableFuture<Void> stopScene(String sceneId);
    
    /**
     * 暂停场景
     * 
     * @param sceneId 场景ID
     * @return 完成信号
     */
    CompletableFuture<Void> pauseScene(String sceneId);
    
    /**
     * 恢复场景
     * 
     * @param sceneId 场景ID
     * @return 完成信号
     */
    CompletableFuture<Void> resumeScene(String sceneId);
    
    /**
     * 销毁场景
     * 
     * @param sceneId 场景ID
     * @return 完成信号
     */
    CompletableFuture<Void> destroyScene(String sceneId);
    
    /**
     * 判断场景是否活跃
     * 
     * @param sceneId 场景ID
     * @return true表示活跃
     */
    boolean isSceneActive(String sceneId);
    
    /**
     * 判断场景是否暂停
     * 
     * @param sceneId 场景ID
     * @return true表示暂停
     */
    boolean isScenePaused(String sceneId);
    
    /**
     * 重新加载场景
     * 
     * @param sceneId 场景ID
     * @return 完成信号
     */
    CompletableFuture<Void> reloadScene(String sceneId);
    
    /**
     * 重启场景
     * 
     * @param sceneId 场景ID
     * @return 完成信号
     */
    CompletableFuture<Void> restartScene(String sceneId);
    
    /**
     * 获取活跃场景列表
     * 
     * @return 场景ID列表
     */
    List<String> getActiveScenes();
    
    /**
     * 获取暂停场景列表
     * 
     * @return 场景ID列表
     */
    List<String> getPausedScenes();
    
    /**
     * 获取场景生命周期统计
     * 
     * @param sceneId 场景ID
     * @return 生命周期统计信息
     */
    SceneLifecycleStats getStats(String sceneId);
    
    /**
     * 添加生命周期监听器
     * 
     * @param listener 生命周期监听器
     */
    void addLifecycleListener(SceneLifecycleListener listener);
    
    /**
     * 移除生命周期监听器
     * 
     * @param listener 生命周期监听器
     */
    void removeLifecycleListener(SceneLifecycleListener listener);
    
    /**
     * 获取场景的运行时实例 (SceneGroup)
     * 
     * <p>场景激活后，返回 SceneGroup 实例。</p>
     * 
     * @param sceneId 场景ID
     * @return SceneGroup 实例，未激活返回 null
     */
    default CompletableFuture<SceneGroup> getSceneGroup(String sceneId) {
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * 获取用户场景组
     * 
     * @param sceneGroupId 场景组ID
     * @param userId 用户ID
     * @return UserSceneGroup 实例
     */
    default CompletableFuture<UserSceneGroup> getUserSceneGroup(String sceneGroupId, String userId) {
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * 获取用户参与的所有场景组
     * 
     * @param userId 用户ID
     * @return UserSceneGroup 列表
     */
    default CompletableFuture<List<UserSceneGroup>> getUserSceneGroups(String userId) {
        return CompletableFuture.completedFuture(java.util.Collections.emptyList());
    }
    
    /**
     * 场景生命周期监听器接口
     * 
     * <p>用于监听场景生命周期事件的回调接口。</p>
     * 
     * @author Ooder Team
     * @version 2.3
     * @since 2.3
     */
    interface SceneLifecycleListener {
        
        /**
         * 场景创建回调
         * 
         * @param sceneId 场景ID
         */
        void onSceneCreated(String sceneId);
        
        /**
         * 场景初始化回调
         * 
         * @param sceneId 场景ID
         */
        void onSceneInitialized(String sceneId);
        
        /**
         * 场景启动回调
         * 
         * @param sceneId 场景ID
         */
        void onSceneStarted(String sceneId);
        
        /**
         * 场景暂停回调
         * 
         * @param sceneId 场景ID
         */
        void onScenePaused(String sceneId);
        
        /**
         * 场景恢复回调
         * 
         * @param sceneId 场景ID
         */
        void onSceneResumed(String sceneId);
        
        /**
         * 场景停止回调
         * 
         * @param sceneId 场景ID
         */
        void onSceneStopped(String sceneId);
        
        /**
         * 场景销毁回调
         * 
         * @param sceneId 场景ID
         */
        void onSceneDestroyed(String sceneId);
        
        /**
         * 场景错误回调
         * 
         * @param sceneId 场景ID
         * @param error 错误信息
         */
        void onSceneError(String sceneId, Throwable error);
    }
    
    /**
     * 场景状态信息类
     * 
     * <p>封装场景的运行状态信息，包括活跃状态、成员数量、
     * 已安装技能等信息。</p>
     * 
     * @author Ooder Team
     * @version 2.3
     * @since 2.3
     */
    class SceneStatusInfo {
        
        /** 场景ID */
        private String sceneId;
        
        /** 是否活跃 */
        private boolean active;
        
        /** 成员数量 */
        private int memberCount;
        
        /** 已安装技能列表 */
        private List<String> installedSkills;
        
        /** 创建时间 */
        private long createTime;
        
        /** 最后更新时间 */
        private long lastUpdateTime;
        
        /** 当前工作流ID */
        private String currentWorkflowId;
        
        /** 工作流状态 */
        private String workflowStatus;
        
        /**
         * 获取场景ID
         * 
         * @return 场景ID
         */
        public String getSceneId() { return sceneId; }
        
        /**
         * 设置场景ID
         * 
         * @param sceneId 场景ID
         */
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        
        /**
         * 判断是否活跃
         * 
         * @return true表示活跃
         */
        public boolean isActive() { return active; }
        
        /**
         * 设置活跃状态
         * 
         * @param active 活跃状态
         */
        public void setActive(boolean active) { this.active = active; }
        
        /**
         * 获取成员数量
         * 
         * @return 成员数量
         */
        public int getMemberCount() { return memberCount; }
        
        /**
         * 设置成员数量
         * 
         * @param memberCount 成员数量
         */
        public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
        
        /**
         * 获取已安装技能列表
         * 
         * @return 技能ID列表
         */
        public List<String> getInstalledSkills() { return installedSkills; }
        
        /**
         * 设置已安装技能列表
         * 
         * @param installedSkills 技能ID列表
         */
        public void setInstalledSkills(List<String> installedSkills) { this.installedSkills = installedSkills; }
        
        /**
         * 获取创建时间
         * 
         * @return 创建时间戳
         */
        public long getCreateTime() { return createTime; }
        
        /**
         * 设置创建时间
         * 
         * @param createTime 创建时间戳
         */
        public void setCreateTime(long createTime) { this.createTime = createTime; }
        
        /**
         * 获取最后更新时间
         * 
         * @return 最后更新时间戳
         */
        public long getLastUpdateTime() { return lastUpdateTime; }
        
        /**
         * 设置最后更新时间
         * 
         * @param lastUpdateTime 最后更新时间戳
         */
        public void setLastUpdateTime(long lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
        
        /**
         * 获取当前工作流ID
         * 
         * @return 工作流ID
         */
        public String getCurrentWorkflowId() { return currentWorkflowId; }
        
        /**
         * 设置当前工作流ID
         * 
         * @param currentWorkflowId 工作流ID
         */
        public void setCurrentWorkflowId(String currentWorkflowId) { this.currentWorkflowId = currentWorkflowId; }
        
        /**
         * 获取工作流状态
         * 
         * @return 工作流状态
         */
        public String getWorkflowStatus() { return workflowStatus; }
        
        /**
         * 设置工作流状态
         * 
         * @param workflowStatus 工作流状态
         */
        public void setWorkflowStatus(String workflowStatus) { this.workflowStatus = workflowStatus; }
    }
}
