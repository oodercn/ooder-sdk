package net.ooder.scene.capability;

import java.util.List;
import java.util.Optional;

public interface CapabilityRegistry {

    CapabilityInfo getCapability(int address);

    CapabilityInfo getCapability(String code);

    List<ProviderInfo> getProviders(int address);

    List<ProviderInfo> getProviders(String code);

    String getDefaultProvider(int address);

    String getFallbackProvider(int address);

    List<CapabilityInfo> getAllCapabilities();

    void register(CapabilityInfo capability);

    void unregister(String code);

    boolean hasCapability(String code);
}
