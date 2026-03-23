package net.ooder.scene.skill.install;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安装结果
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class InstallResult {

    private boolean success;
    private String skillId;
    private String configId;
    private String kbBindingId;
    private String promptKbId;
    private List<String> capabilities;
    private List<String> warnings;
    private String errorMessage;
    private long installTime;
    private Map<String, Object> metadata;

    public InstallResult() {
        this.capabilities = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.metadata = new HashMap<>();
    }

    public static InstallResult success(String skillId) {
        InstallResult result = new InstallResult();
        result.setSuccess(true);
        result.setSkillId(skillId);
        result.setInstallTime(System.currentTimeMillis());
        return result;
    }

    public static InstallResult failure(String skillId, String errorMessage) {
        InstallResult result = new InstallResult();
        result.setSuccess(false);
        result.setSkillId(skillId);
        result.setErrorMessage(errorMessage);
        result.setInstallTime(System.currentTimeMillis());
        return result;
    }

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

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getKbBindingId() {
        return kbBindingId;
    }

    public void setKbBindingId(String kbBindingId) {
        this.kbBindingId = kbBindingId;
    }

    public String getPromptKbId() {
        return promptKbId;
    }

    public void setPromptKbId(String promptKbId) {
        this.promptKbId = promptKbId;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities != null ? capabilities : new ArrayList<>();
    }

    public void addCapability(String capability) {
        this.capabilities.add(capability);
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings != null ? warnings : new ArrayList<>();
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getInstallTime() {
        return installTime;
    }

    public void setInstallTime(long installTime) {
        this.installTime = installTime;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
}
