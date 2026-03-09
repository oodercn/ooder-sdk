package net.ooder.scene.service.push;

import java.util.ArrayList;
import java.util.List;

/**
 * 推送结果
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class PushResult {

    private String pushId;
    private String sceneId;
    private int totalCount;
    private int successCount;
    private int failedCount;
    private List<String> successUserIds = new ArrayList<>();
    private List<String> failedUserIds = new ArrayList<>();
    private List<String> errorMessages = new ArrayList<>();
    private long pushTime;

    public PushResult() {
        this.pushTime = System.currentTimeMillis();
    }

    public static PushResult success(String pushId, String sceneId, List<String> successUserIds) {
        PushResult result = new PushResult();
        result.setPushId(pushId);
        result.setSceneId(sceneId);
        result.setSuccessUserIds(successUserIds);
        result.setTotalCount(successUserIds.size());
        result.setSuccessCount(successUserIds.size());
        return result;
    }

    public static PushResult partial(String pushId, String sceneId, 
                                      List<String> successUserIds, List<String> failedUserIds) {
        PushResult result = new PushResult();
        result.setPushId(pushId);
        result.setSceneId(sceneId);
        result.setSuccessUserIds(successUserIds);
        result.setFailedUserIds(failedUserIds);
        result.setTotalCount(successUserIds.size() + failedUserIds.size());
        result.setSuccessCount(successUserIds.size());
        result.setFailedCount(failedUserIds.size());
        return result;
    }

    public static PushResult failure(String sceneId, List<String> failedUserIds, List<String> errors) {
        PushResult result = new PushResult();
        result.setSceneId(sceneId);
        result.setFailedUserIds(failedUserIds);
        result.setErrorMessages(errors);
        result.setTotalCount(failedUserIds.size());
        result.setFailedCount(failedUserIds.size());
        return result;
    }

    public String getPushId() { return pushId; }
    public void setPushId(String pushId) { this.pushId = pushId; }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }

    public int getFailedCount() { return failedCount; }
    public void setFailedCount(int failedCount) { this.failedCount = failedCount; }

    public List<String> getSuccessUserIds() { return successUserIds; }
    public void setSuccessUserIds(List<String> successUserIds) { this.successUserIds = successUserIds; }

    public List<String> getFailedUserIds() { return failedUserIds; }
    public void setFailedUserIds(List<String> failedUserIds) { this.failedUserIds = failedUserIds; }

    public List<String> getErrorMessages() { return errorMessages; }
    public void setErrorMessages(List<String> errorMessages) { this.errorMessages = errorMessages; }

    public long getPushTime() { return pushTime; }
    public void setPushTime(long pushTime) { this.pushTime = pushTime; }

    public boolean isAllSuccess() {
        return failedCount == 0 && successCount > 0;
    }

    public boolean isAllFailed() {
        return successCount == 0 && failedCount > 0;
    }
}
