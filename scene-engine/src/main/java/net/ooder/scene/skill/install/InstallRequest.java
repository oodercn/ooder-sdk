package net.ooder.scene.skill.install;

import java.util.HashMap;
import java.util.Map;

/**
 * 安装请求
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class InstallRequest {

    private String skillId;
    private String version;
    private InstallSource source;
    private String sourceUrl;
    private String targetSceneId;
    private Map<String, Object> installConfig;

    public InstallRequest() {
        this.installConfig = new HashMap<>();
    }

    public static Builder builder() {
        return new Builder();
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

    public InstallSource getSource() {
        return source;
    }

    public void setSource(InstallSource source) {
        this.source = source;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getTargetSceneId() {
        return targetSceneId;
    }

    public void setTargetSceneId(String targetSceneId) {
        this.targetSceneId = targetSceneId;
    }

    public Map<String, Object> getInstallConfig() {
        return installConfig;
    }

    public void setInstallConfig(Map<String, Object> installConfig) {
        this.installConfig = installConfig != null ? installConfig : new HashMap<>();
    }

    /**
     * 安装来源
     */
    public enum InstallSource {
        LOCAL("local", "本地安装"),
        GITEE("gitee", "Gitee"),
        GITHUB("github", "GitHub"),
        GIT("git", "Git仓库"),
        REGISTRY("registry", "技能注册中心");

        private final String code;
        private final String description;

        InstallSource(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Builder 模式
     */
    public static class Builder {
        private final InstallRequest request;

        public Builder() {
            request = new InstallRequest();
        }

        public Builder skillId(String skillId) {
            request.setSkillId(skillId);
            return this;
        }

        public Builder version(String version) {
            request.setVersion(version);
            return this;
        }

        public Builder source(InstallSource source) {
            request.setSource(source);
            return this;
        }

        public Builder sourceUrl(String sourceUrl) {
            request.setSourceUrl(sourceUrl);
            return this;
        }

        public Builder targetSceneId(String targetSceneId) {
            request.setTargetSceneId(targetSceneId);
            return this;
        }

        public Builder config(String key, Object value) {
            request.getInstallConfig().put(key, value);
            return this;
        }

        public Builder config(Map<String, Object> config) {
            request.setInstallConfig(config);
            return this;
        }

        public InstallRequest build() {
            return request;
        }
    }
}
