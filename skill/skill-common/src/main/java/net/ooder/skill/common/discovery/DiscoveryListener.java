package net.ooder.skill.common.discovery;

public interface DiscoveryListener {
    
    default void onBeforeDiscovery(DiscoveryRequest request) {}
    
    default void onCapabilityDiscovered(CapabilityDTO capability) {}
    
    default void onSceneDiscovered(CapabilityDTO scene) {}
    
    default void onAfterDiscovery(DiscoveryResult result) {}
}
