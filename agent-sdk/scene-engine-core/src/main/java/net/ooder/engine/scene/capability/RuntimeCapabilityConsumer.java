package net.ooder.engine.scene.capability;

import java.util.Map;

public interface RuntimeCapabilityConsumer {
    String getConsumerId();
    String getConsumerType();
    Map<String, Object> getRequirements();
    void onCapabilityAvailable(RuntimeCapability capability);
    void onCapabilityUnavailable(String capId);
}
