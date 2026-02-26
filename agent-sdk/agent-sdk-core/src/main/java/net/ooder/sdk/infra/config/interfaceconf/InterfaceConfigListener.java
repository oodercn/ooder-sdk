package net.ooder.sdk.infra.config.interfaceconf;

public interface InterfaceConfigListener {

    void onConfigChanged(String interfaceId, String key, Object oldValue, Object newValue);

    void onConfigAdded(String interfaceId);

    void onConfigRemoved(String interfaceId);

    void onConfigReset(String interfaceId);
}
