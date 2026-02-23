package net.ooder.sdk.cmd;

public interface CmdCallback {
    
    void onSuccess(CmdResponse response);
    
    void onError(Throwable error);
    
    void onProgress(int progress);
}
