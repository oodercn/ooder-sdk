package net.ooder.engine.scene.capability;

public interface RuntimeCapabilityListener {
    void onCapabilityRegistered(RuntimeCapability capability);
    void onCapabilityUnregistered(String capId);
    void onCapabilityStatusChanged(String capId, RuntimeCapabilityStatus oldStatus, RuntimeCapabilityStatus newStatus);
}
