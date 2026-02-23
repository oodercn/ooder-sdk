package net.ooder.sdk.msg;

public interface MsgListener {
    
    void onMessage(MsgRecord message);
    
    void onConnectionChanged(boolean connected);
    
    void onError(Throwable error);
}
