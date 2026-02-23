package net.ooder.sdk.msg;

import java.io.Serializable;

public class MsgResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private String msgId;
    private String errorCode;
    private String errorMessage;
    private long timestamp;
    
    public MsgResult() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public static MsgResult success(String msgId) {
        MsgResult result = new MsgResult();
        result.setSuccess(true);
        result.setMsgId(msgId);
        return result;
    }
    
    public static MsgResult failure(String errorCode, String errorMessage) {
        MsgResult result = new MsgResult();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMsgId() { return msgId; }
    public void setMsgId(String msgId) { this.msgId = msgId; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
