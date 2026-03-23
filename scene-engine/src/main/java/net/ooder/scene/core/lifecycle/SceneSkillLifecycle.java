package net.ooder.scene.core.lifecycle;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 场景技能生命周期管理接口
 *
 * <p>管理场景中技能的完整生命周期，包括安装、激活、停用、卸载等。</p>
 *
 * <h3>生命周期状态：</h3>
 * <pre>
 * DISCOVERED → INSTALLING → INSTALLED → ACTIVATING → ACTIVATED → DEACTIVATING → DEACTIVATED → UNINSTALLING → UNINSTALLED
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface SceneSkillLifecycle {

    /**
     * 安装场景技能
     *
     * @param sceneId 场景ID
     * @param skillId 技能ID
     * @param config 安装配置
     * @return 安装结果
     */
    CompletableFuture<LifecycleInstallResult> installSceneSkill(String sceneId, String skillId, Map<String, Object> config);

    /**
     * 激活场景技能
     *
     * @param sceneId 场景ID
     * @param skillId 技能ID
     * @param role 激活角色
     * @return 激活结果
     */
    CompletableFuture<ActivateResult> activateSceneSkill(String sceneId, String skillId, String role);

    /**
     * 停用场景技能
     *
     * @param sceneId 场景ID
     * @param skillId 技能ID
     * @return 停用结果
     */
    CompletableFuture<DeactivateResult> deactivateSceneSkill(String sceneId, String skillId);

    /**
     * 卸载场景技能
     *
     * @param sceneId 场景ID
     * @param skillId 技能ID
     * @return 卸载结果
     */
    CompletableFuture<UninstallResult> uninstallSceneSkill(String sceneId, String skillId);

    /**
     * 获取技能状态
     *
     * @param sceneId 场景ID
     * @param skillId 技能ID
     * @return 技能状态
     */
    SkillLifecycleState getSkillState(String sceneId, String skillId);

    /**
     * 获取场景所有技能状态
     *
     * @param sceneId 场景ID
     * @return 技能状态列表
     */
    List<SkillStateInfo> getSceneSkillStates(String sceneId);

    /**
     * 订阅状态变更事件
     *
     * @param sceneId 场景ID
     * @param listener 监听器
     * @return 订阅ID
     */
    String subscribeStateChange(String sceneId, StateChangeListener listener);

    /**
     * 取消订阅
     *
     * @param subscriptionId 订阅ID
     */
    void unsubscribeStateChange(String subscriptionId);

    /**
     * 获取安装进度
     *
     * @param installId 安装ID
     * @return 安装进度
     */
    InstallProgress getInstallProgress(String installId);

    /**
     * 取消安装
     *
     * @param installId 安装ID
     * @return 是否成功
     */
    boolean cancelInstall(String installId);

    /**
     * 重试安装
     *
     * @param installId 安装ID
     * @return 新安装ID
     */
    String retryInstall(String installId);

    /**
     * 技能生命周期状态枚举
     * 
     * <h3>完整状态机流程：</h3>
     * <pre>
     * DISCOVERED → PREVIEWING → CONFIGURING → DEP_CHECKING → DEP_CONFIRMING
     *                                                         ↓
     * ACTIVATED ← ACTIVATING ← INSTALLED ←────────────────────┘
     *     ↓
     * DEACTIVATED → UNINSTALLING → UNINSTALLED
     * 
     * 任意状态可转入 ERROR 或 UPDATING
     * </pre>
     */
    enum SkillLifecycleState {
        DISCOVERED("已发现", "技能已发现，等待预览"),
        PREVIEWING("预览中", "正在预览技能详情"),
        CONFIGURING("配置中", "正在配置技能参数"),
        DEP_CHECKING("依赖检查中", "正在检查依赖项"),
        DEP_CONFIRMING("依赖确认中", "等待用户确认依赖"),
        INSTALLING("安装中", "正在安装技能"),
        INSTALLED("已安装", "技能已安装，等待激活"),
        UPDATING("更新中", "正在更新技能配置"),
        ACTIVATING("激活中", "正在激活技能"),
        ACTIVATED("已激活", "技能已激活，可正常使用"),
        DEACTIVATING("停用中", "正在停用技能"),
        DEACTIVATED("已停用", "技能已停用"),
        UNINSTALLING("卸载中", "正在卸载技能"),
        UNINSTALLED("已卸载", "技能已卸载"),
        ERROR("错误", "技能状态异常");

        private final String displayName;
        private final String description;

        SkillLifecycleState(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        
        /**
         * 检查是否可以从当前状态转换到目标状态
         * 
         * @param targetState 目标状态
         * @return 是否可以转换
         */
        public boolean canTransitionTo(SkillLifecycleState targetState) {
            if (targetState == null) {
                return false;
            }
            if (this == targetState) {
                return true;
            }
            if (targetState == ERROR) {
                return true;
            }
            
            switch (this) {
                case DISCOVERED:
                    return targetState == PREVIEWING || targetState == INSTALLING;
                case PREVIEWING:
                    return targetState == CONFIGURING || targetState == DISCOVERED;
                case CONFIGURING:
                    return targetState == DEP_CHECKING || targetState == PREVIEWING;
                case DEP_CHECKING:
                    return targetState == DEP_CONFIRMING || targetState == CONFIGURING;
                case DEP_CONFIRMING:
                    return targetState == INSTALLING || targetState == DEP_CHECKING;
                case INSTALLING:
                    return targetState == INSTALLED;
                case INSTALLED:
                    return targetState == ACTIVATING || targetState == UPDATING || targetState == UNINSTALLING;
                case UPDATING:
                    return targetState == INSTALLED || targetState == ACTIVATED;
                case ACTIVATING:
                    return targetState == ACTIVATED;
                case ACTIVATED:
                    return targetState == DEACTIVATING || targetState == UPDATING;
                case DEACTIVATING:
                    return targetState == DEACTIVATED;
                case DEACTIVATED:
                    return targetState == ACTIVATING || targetState == UNINSTALLING;
                case UNINSTALLING:
                    return targetState == UNINSTALLED;
                case UNINSTALLED:
                    return targetState == DISCOVERED;
                case ERROR:
                    return targetState == DISCOVERED || targetState == INSTALLED || 
                           targetState == ACTIVATED || targetState == UNINSTALLED;
                default:
                    return false;
            }
        }
        
        /**
         * 检查是否处于活动状态（可接受调用）
         */
        public boolean isActive() {
            return this == ACTIVATED || this == UPDATING;
        }
        
        /**
         * 检查是否处于安装流程中
         */
        public boolean isInInstallFlow() {
            return this == DISCOVERED || this == PREVIEWING || this == CONFIGURING ||
                   this == DEP_CHECKING || this == DEP_CONFIRMING || this == INSTALLING;
        }
        
        /**
         * 检查是否处于激活流程中
         */
        public boolean isInActivationFlow() {
            return this == INSTALLED || this == ACTIVATING;
        }
        
        /**
         * 检查是否处于终止状态
         */
        public boolean isTerminal() {
            return this == UNINSTALLED;
        }
    }

    /**
     * 状态变更监听器
     */
    interface StateChangeListener {
        void onStateChange(StateChangeEvent event);
    }

    /**
     * 状态变更事件
     */
    class StateChangeEvent {
        private String sceneId;
        private String skillId;
        private SkillLifecycleState oldState;
        private SkillLifecycleState newState;
        private long timestamp;
        private String message;

        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public SkillLifecycleState getOldState() { return oldState; }
        public void setOldState(SkillLifecycleState oldState) { this.oldState = oldState; }
        public SkillLifecycleState getNewState() { return newState; }
        public void setNewState(SkillLifecycleState newState) { this.newState = newState; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
