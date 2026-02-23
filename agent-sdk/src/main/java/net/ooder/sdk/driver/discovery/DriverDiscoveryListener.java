package net.ooder.sdk.driver.discovery;

public interface DriverDiscoveryListener {
    
    void onDriverDiscovered(DriverDiscovery.DiscoveredDriver driver);
    
    void onDiscoveryStarted(String basePackage);
    
    void onDiscoveryCompleted(int discoveredCount);
    
    void onDiscoveryError(String message, Throwable error);
}
