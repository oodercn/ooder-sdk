package net.ooder.scene.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.provider.model.network.ConnectionStatus;
import net.ooder.scene.provider.model.network.CommandResult;
import net.ooder.scene.provider.model.network.SystemStatus;

import java.util.Map;

public interface DeviceManagementProvider extends BaseProvider {
    
    Result<Boolean> connect(Map<String, Object> connectionData);
    
    Result<Boolean> disconnect();
    
    Result<ConnectionStatus> getConnectionStatus();
    
    Result<CommandResult> executeCommand(String command);
    
    Result<CommandResult> executeCommand(String command, Map<String, Object> params);
    
    Result<SystemStatus> getSystemStatus();
    
    Result<Boolean> restart();
    
    Result<Boolean> shutdown();
}
