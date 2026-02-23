package net.ooder.sdk.msg;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MsgRecord implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String msgId;
    private String topic;
    private String fromUserId;
    private String toUserId;
    private String groupId;
    private String content;
    private long timestamp;
    private int qos;
    private boolean retained;
    private Map<String, Object> headers = new ConcurrentHashMap<>();
    
    public MsgRecord() {}
    
    public String getMsgId() { return msgId; }
    public void setMsgId(String msgId) { this.msgId = msgId; }
    
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
    public String getFromUserId() { return fromUserId; }
    public void setFromUserId(String fromUserId) { this.fromUserId = fromUserId; }
    
    public String getToUserId() { return toUserId; }
    public void setToUserId(String toUserId) { this.toUserId = toUserId; }
    
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public int getQos() { return qos; }
    public void setQos(int qos) { this.qos = qos; }
    
    public boolean isRetained() { return retained; }
    public void setRetained(boolean retained) { this.retained = retained; }
    
    public Map<String, Object> getHeaders() { return headers; }
    public void setHeaders(Map<String, Object> headers) { 
        this.headers = headers != null ? headers : new ConcurrentHashMap<>(); 
    }
    
    public boolean isP2P() {
        return toUserId != null && !toUserId.isEmpty();
    }
    
    public boolean isGroup() {
        return groupId != null && !groupId.isEmpty();
    }
    
    public boolean isTopic() {
        return topic != null && !topic.isEmpty();
    }
}
