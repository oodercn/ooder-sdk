package net.ooder.scene.message.reliability;

/**
 * 投递尝试记录
 *
 * <p>记录单次投递尝试的详细信息</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class DeliveryAttempt {
    
    private int attemptNumber;
    private long attemptTime;
    private boolean success;
    private String errorMessage;
    private long responseTime;
    private String targetId;
    
    public DeliveryAttempt() {
        this.attemptTime = System.currentTimeMillis();
    }
    
    public int getAttemptNumber() {
        return attemptNumber;
    }
    
    public void setAttemptNumber(int attemptNumber) {
        this.attemptNumber = attemptNumber;
    }
    
    public long getAttemptTime() {
        return attemptTime;
    }
    
    public void setAttemptTime(long attemptTime) {
        this.attemptTime = attemptTime;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public long getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
    
    public String getTargetId() {
        return targetId;
    }
    
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
    
    public static DeliveryAttempt success(int attemptNumber, long responseTime) {
        DeliveryAttempt attempt = new DeliveryAttempt();
        attempt.setAttemptNumber(attemptNumber);
        attempt.setSuccess(true);
        attempt.setResponseTime(responseTime);
        return attempt;
    }
    
    public static DeliveryAttempt failure(int attemptNumber, String errorMessage) {
        DeliveryAttempt attempt = new DeliveryAttempt();
        attempt.setAttemptNumber(attemptNumber);
        attempt.setSuccess(false);
        attempt.setErrorMessage(errorMessage);
        return attempt;
    }
    
    @Override
    public String toString() {
        return "DeliveryAttempt{" +
                "attemptNumber=" + attemptNumber +
                ", success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                ", responseTime=" + responseTime + "ms" +
                '}';
    }
}
