package net.ooder.engine.scene.capability;

import java.util.List;
import java.util.Map;

public interface RuntimeCapabilityProvider {
    String getProviderId();
    String getProviderType();
    List<RuntimeCapability> getCapabilities();
    Map<String, Object> getCapabilitiesMap();
    boolean isAvailable();
}
