package net.ooder.sdk.cmd;

import java.util.List;
import java.util.Map;

public interface CmdClientProxy {
    
    void init(CmdClientConfig config);
    
    CommandBuilder newCommand();
    
    CmdResponse send(Command command);
    
    void sendAsync(Command command, CmdCallback callback);
    
    boolean cancel(String commandId);
    
    CmdStatus getStatus(String commandId);
    
    List<CmdRecord> getHistory(long startTime, long endTime);
    
    void shutdown();
    
    boolean isConnected();
    
    String getClientId();
}
