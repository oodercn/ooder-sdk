package net.ooder.scene.skill.install;

import java.util.ArrayList;
import java.util.List;

/**
 * 卸载结果
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class UninstallResult {

    private boolean success;
    private String skillId;
    private List<String> removedCapabilities;
    private List<String> removedTools;
    private boolean configRemoved;
    private boolean promptIndexRemoved;
    private String errorMessage;

    public UninstallResult() {
        this.removedCapabilities = new ArrayList<>();
        this.removedTools = new ArrayList<>();
    }

    public static UninstallResult success(String skillId) {
        UninstallResult result = new UninstallResult();
        result.setSuccess(true);
        result.setSkillId(skillId);
        return result;
    }

    public static UninstallResult failure(String skillId, String errorMessage) {
        UninstallResult result = new UninstallResult();
        result.setSuccess(false);
        result.setSkillId(skillId);
        result.setErrorMessage(errorMessage);
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

    public List<String> getRemovedCapabilities() {
        return removedCapabilities;
    }

    public void setRemovedCapabilities(List<String> removedCapabilities) {
        this.removedCapabilities = removedCapabilities != null ? removedCapabilities : new ArrayList<>();
    }

    public void addRemovedCapability(String capability) {
        this.removedCapabilities.add(capability);
    }

    public List<String> getRemovedTools() {
        return removedTools;
    }

    public void setRemovedTools(List<String> removedTools) {
        this.removedTools = removedTools != null ? removedTools : new ArrayList<>();
    }

    public void addRemovedTool(String tool) {
        this.removedTools.add(tool);
    }

    public boolean isConfigRemoved() {
        return configRemoved;
    }

    public void setConfigRemoved(boolean configRemoved) {
        this.configRemoved = configRemoved;
    }

    public boolean isPromptIndexRemoved() {
        return promptIndexRemoved;
    }

    public void setPromptIndexRemoved(boolean promptIndexRemoved) {
        this.promptIndexRemoved = promptIndexRemoved;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
