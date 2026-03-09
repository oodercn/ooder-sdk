package net.ooder.scene.skill.coordinator;

import java.util.HashMap;
import java.util.Map;

/**
 * 安装选项
 *
 * <p>用于配置技能安装过程的参数</p>
 *
 * @author Ooder Team
 * @since 2.3
 */
public class InstallOptions {

    private String installId;
    private String skillId;
    private String version;
    private boolean autoStart = true;
    private boolean verifySignature = true;
    private Map<String, Object> customParams = new HashMap<>();

    public InstallOptions() {
    }

    public String getInstallId() {
        return installId;
    }

    public void setInstallId(String installId) {
        this.installId = installId;
    }

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

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public boolean isVerifySignature() {
        return verifySignature;
    }

    public void setVerifySignature(boolean verifySignature) {
        this.verifySignature = verifySignature;
    }

    public Map<String, Object> getCustomParams() {
        return customParams;
    }

    public void setCustomParams(Map<String, Object> customParams) {
        this.customParams = customParams;
    }

    /**
     * 添加自定义参数
     */
    public InstallOptions addParam(String key, Object value) {
        this.customParams.put(key, value);
        return this;
    }

    /**
     * 获取自定义参数
     */
    public Object getParam(String key) {
        return this.customParams.get(key);
    }

    /**
     * 构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private InstallOptions options = new InstallOptions();

        public Builder installId(String installId) {
            options.setInstallId(installId);
            return this;
        }

        public Builder skillId(String skillId) {
            options.setSkillId(skillId);
            return this;
        }

        public Builder version(String version) {
            options.setVersion(version);
            return this;
        }

        public Builder autoStart(boolean autoStart) {
            options.setAutoStart(autoStart);
            return this;
        }

        public Builder verifySignature(boolean verifySignature) {
            options.setVerifySignature(verifySignature);
            return this;
        }

        public Builder customParams(Map<String, Object> customParams) {
            options.setCustomParams(customParams);
            return this;
        }

        public Builder addParam(String key, Object value) {
            options.addParam(key, value);
            return this;
        }

        public InstallOptions build() {
            return options;
        }
    }
}
