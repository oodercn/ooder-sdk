package net.ooder.skills.container.api;

import java.util.List;
import java.util.Map;

/**
 * Skills 容器 - Skills 的独立运行时环境
 *
 * 职责：
 * 1. Skill 的发现、加载、卸载
 * 2. Skill 生命周期管理（启动、停止、更新）
 * 3. 能力注册与发现
 * 4. 版本管理与自动更新
 * 5. 蓝绿部署支持
 */
public interface SkillContainer {

    /**
     * 初始化容器
     */
    void initialize(ContainerConfig config);

    /**
     * 加载 Skill
     */
    SkillHandle loadSkill(SkillPackage skillPackage);

    /**
     * 卸载 Skill
     */
    void unloadSkill(String skillId);

    /**
     * 启动 Skill
     */
    void startSkill(String skillId);

    /**
     * 停止 Skill
     */
    void stopSkill(String skillId);

    /**
     * 更新 Skill（支持蓝绿部署）
     */
    UpdateResult updateSkill(String skillId, String newVersion, UpdateOptions options);

    /**
     * 获取已加载的 Skills
     */
    List<SkillHandle> getLoadedSkills();

    /**
     * 获取能力注册中心
     */
    CapabilityRegistry getCapabilityRegistry();

    /**
     * 关闭容器
     */
    void shutdown();

    /**
     * 容器配置
     */
    class ContainerConfig {
        private String workspace;
        private boolean autoStart;
        private long healthCheckInterval;
        private boolean enableBlueGreenDeployment;

        public String getWorkspace() {
            return workspace;
        }

        public void setWorkspace(String workspace) {
            this.workspace = workspace;
        }

        public boolean isAutoStart() {
            return autoStart;
        }

        public void setAutoStart(boolean autoStart) {
            this.autoStart = autoStart;
        }

        public long getHealthCheckInterval() {
            return healthCheckInterval;
        }

        public void setHealthCheckInterval(long healthCheckInterval) {
            this.healthCheckInterval = healthCheckInterval;
        }

        public boolean isEnableBlueGreenDeployment() {
            return enableBlueGreenDeployment;
        }

        public void setEnableBlueGreenDeployment(boolean enableBlueGreenDeployment) {
            this.enableBlueGreenDeployment = enableBlueGreenDeployment;
        }
    }

    /**
     * Skill 包
     */
    class SkillPackage {
        private String skillId;
        private String version;
        private String location;
        private Map<String, Object> metadata;

        public String getSkillId() {
            return skillId;
        }

        public void setSkillId(String skillId) {
            this.skillId = skillId;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
        }
    }

    /**
     * 更新选项
     */
    class UpdateOptions {
        private boolean blueGreen;
        private boolean autoRollback;
        private long healthCheckTimeout;

        public boolean isBlueGreen() {
            return blueGreen;
        }

        public void setBlueGreen(boolean blueGreen) {
            this.blueGreen = blueGreen;
        }

        public boolean isAutoRollback() {
            return autoRollback;
        }

        public void setAutoRollback(boolean autoRollback) {
            this.autoRollback = autoRollback;
        }

        public long getHealthCheckTimeout() {
            return healthCheckTimeout;
        }

        public void setHealthCheckTimeout(long healthCheckTimeout) {
            this.healthCheckTimeout = healthCheckTimeout;
        }
    }

    /**
     * 更新结果
     */
    class UpdateResult {
        private boolean success;
        private String skillId;
        private String oldVersion;
        private String newVersion;
        private String message;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getSkillId() {
            return skillId;
        }

        public void setSkillId(String skillId) {
            this.skillId = skillId;
        }

        public String getOldVersion() {
            return oldVersion;
        }

        public void setOldVersion(String oldVersion) {
            this.oldVersion = oldVersion;
        }

        public String getNewVersion() {
            return newVersion;
        }

        public void setNewVersion(String newVersion) {
            this.newVersion = newVersion;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
