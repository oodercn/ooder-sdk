package net.ooder.scene.skill.install;

import java.util.HashMap;
import java.util.Map;

/**
 * 安装信息
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class InstallInfo {

    private String skillId;
    private String version;
    private long installTime;
    private String source;
    private String targetSceneId;
    private String configId;
    private String status;
    private Map<String, Object> metadata;

    public InstallInfo() {
        this.metadata = new HashMap<>();
        this.status = "installed";
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

    public long getInstallTime() {
        return installTime;
    }

    public void setInstallTime(long installTime) {
        this.installTime = installTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTargetSceneId() {
        return targetSceneId;
    }

    public void setTargetSceneId(String targetSceneId) {
        this.targetSceneId = targetSceneId;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
}
