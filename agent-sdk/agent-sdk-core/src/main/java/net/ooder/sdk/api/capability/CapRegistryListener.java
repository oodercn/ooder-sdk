package net.ooder.sdk.api.capability;

/**
 * CAP 注册表监听器
 *
 * @author Ooder Team
 * @version 2.3
 */
public interface CapRegistryListener {

    void onCapabilityRegistered(Capability capability);

    void onCapabilityUnregistered(String capId);

    void onCapabilityStatusChanged(String capId, CapabilityStatus oldStatus, CapabilityStatus newStatus);

    void onAddressAllocated(CapAddress address);

    void onAddressReleased(CapAddress address);
}
