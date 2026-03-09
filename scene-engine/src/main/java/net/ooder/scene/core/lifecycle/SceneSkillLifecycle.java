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
    CompletableFuture<InstallResult> installSceneSkill(String sceneId, String skillId, Map<String, Object> config);

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
     */
    enum SkillLifecycleState {
        DISCOVERED("已发现"),
        INSTALLING("安装中"),
        INSTALLED("已安装"),
        ACTIVATING("激活中"),
        ACTIVATED("已激活"),
        DEACTIVATING("停用中"),
        DEACTIVATED("已停用"),
        UNINSTALLING("卸载中"),
        UNINSTALLED("已卸载"),
        ERROR("错误");

        private final String description;

        SkillLifecycleState(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
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
