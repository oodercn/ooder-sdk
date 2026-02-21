package net.ooder.scene.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.provider.model.network.NetworkSetting;
import net.ooder.scene.provider.model.network.IPAddress;
import net.ooder.scene.provider.model.network.IPBlacklist;

import java.util.List;
import java.util.Map;

public interface NetworkConfigProvider extends BaseProvider {
    
    Result<NetworkSetting> getNetworkSetting(String settingType);
    
    Result<List<NetworkSetting>> getAllNetworkSettings();
    
    Result<NetworkSetting> updateNetworkSetting(String settingType, Map<String, Object> data);
    
    Result<List<IPAddress>> getIPAddresses(String type, String status);
    
    Result<IPAddress> addStaticIPAddress(Map<String, Object> ipData);
    
    Result<IPAddress> deleteIPAddress(String ipId);
    
    Result<List<IPBlacklist>> getIPBlacklist();
    
    Result<IPBlacklist> addIPToBlacklist(Map<String, Object> data);
    
    Result<IPBlacklist> removeIPFromBlacklist(String id);
}
