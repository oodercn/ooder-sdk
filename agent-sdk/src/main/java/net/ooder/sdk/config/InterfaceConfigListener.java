package net.ooder.sdk.config;

public interface InterfaceConfigListener {
    
    void onConfigChanged(String interfaceId, String key, Object oldValue, Object newValue);
    
    void onConfigAdded(String interfaceId);
    
    void onConfigRemoved(String interfaceId);
    
    void onConfigReset(String interfaceId);
}
