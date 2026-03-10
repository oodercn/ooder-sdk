package net.ooder.scene.skill.install;

/**
 * 安装结果
 *
 * @author ooder
 * @since 2.3.2
 */
public class InstallResult {

    private boolean success;
    private String installId;
    private String capabilityId;
    private String message;
    private long duration;

    public InstallResult() {}

    public InstallResult(boolean success, String capabilityId) {
        this.success = success;
        this.capabilityId = capabilityId;
    }

    public static InstallResult success(String capabilityId) {
        return new InstallResult(true, capabilityId);
    }

    public static InstallResult failure(String capabilityId, String message) {
        InstallResult result = new InstallResult(false, capabilityId);
        result.setMessage(message);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getInstallId() {
        return installId;
    }

    public void setInstallId(String installId) {
        this.installId = installId;
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
