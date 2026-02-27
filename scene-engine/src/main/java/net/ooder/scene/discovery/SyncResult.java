package net.ooder.scene.discovery;

public class SyncResult {
    private boolean success;
    private int sceneCount;
    private int capabilityCount;
    private int skillCount;
    private long syncTime;
    private String errorMessage;

    public SyncResult() {
        this.success = true;
        this.sceneCount = 0;
        this.capabilityCount = 0;
        this.skillCount = 0;
        this.syncTime = System.currentTimeMillis();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getSceneCount() {
        return sceneCount;
    }

    public void setSceneCount(int sceneCount) {
        this.sceneCount = sceneCount;
    }

    public int getCapabilityCount() {
        return capabilityCount;
    }

    public void setCapabilityCount(int capabilityCount) {
        this.capabilityCount = capabilityCount;
    }

    public int getSkillCount() {
        return skillCount;
    }

    public void setSkillCount(int skillCount) {
        this.skillCount = skillCount;
    }

    public long getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(long syncTime) {
        this.syncTime = syncTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.success = false;
    }

    public static SyncResult success(int sceneCount, int capabilityCount, int skillCount) {
        SyncResult result = new SyncResult();
        result.setSceneCount(sceneCount);
        result.setCapabilityCount(capabilityCount);
        result.setSkillCount(skillCount);
        return result;
    }

    public static SyncResult failure(String errorMessage) {
        SyncResult result = new SyncResult();
        result.setErrorMessage(errorMessage);
        return result;
    }
}
