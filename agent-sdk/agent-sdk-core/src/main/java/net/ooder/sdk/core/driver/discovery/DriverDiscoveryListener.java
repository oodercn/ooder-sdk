package net.ooder.sdk.core.driver.discovery;

public interface DriverDiscoveryListener {
    
    void onDriverDiscovered(DriverDiscovery.DiscoveredDriver driver);
    
    void onDiscoveryComplete(int driverCount);
    
    void onDiscoveryError(String message, Throwable error);
}
