package net.ooder.sdk.msg;

import java.util.List;

public interface MsgClientProxy {
    
    void init(MsgClientConfig config);
    
    MsgResult sendToUser(String userId, String message);
    
    MsgResult sendToGroup(String groupId, String message);
    
    void subscribe(String topic, MsgListener listener);
    
    void unsubscribe(String topic);
    
    MsgResult publish(String topic, String message);
    
    List<MsgRecord> getOfflineMessages(String userId);
    
    void ackMessage(String msgId);
    
    boolean recallMessage(String msgId);
    
    void shutdown();
    
    boolean isConnected();
    
    String getClientId();
}
