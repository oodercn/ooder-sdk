package net.ooder.scene.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.provider.model.protocol.ProtocolHandler;
import net.ooder.scene.provider.model.protocol.ProtocolCommandResult;

import java.util.List;
import java.util.Map;

public interface ProtocolProvider extends BaseProvider {
    
    Result<List<ProtocolHandler>> getProtocolHandlers();
    
    Result<ProtocolHandler> registerProtocolHandler(Map<String, Object> handlerData);
    
    Result<Boolean> removeProtocolHandler(String handlerType);
    
    Result<ProtocolCommandResult> handleProtocolCommand(Map<String, Object> commandData);
    
    Result<Boolean> refreshProtocolHandlers();
}
