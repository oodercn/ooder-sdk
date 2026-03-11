package net.ooder.scene.capability;

import net.ooder.skills.capability.CapabilityAddress;
import net.ooder.skills.capability.CapabilityCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CapabilityRouter {

    private static final Logger log = LoggerFactory.getLogger(CapabilityRouter.class);

    private final Map<Integer, Object> driverRegistry = new ConcurrentHashMap<>();
    private final Map<Integer, String> bindingRegistry = new ConcurrentHashMap<>();
    private final Map<Integer, String> fallbackRegistry = new ConcurrentHashMap<>();

    public CapabilityRouter() {
    }

    public <T> T getDriver(CapabilityAddress address, Class<T> driverType) {
        Object driver = driverRegistry.get(address.getAddress());
        if (driver != null && driverType.isInstance(driver)) {
            return driverType.cast(driver);
        }
        log.warn("No driver registered for address: {}", address);
        return null;
    }

    public <T> T getDriver(int address, Class<T> driverType) {
        CapabilityAddress capAddr = CapabilityAddress.fromAddress(address);
        if (capAddr == null) {
            log.warn("Invalid address: 0x{}", Integer.toHexString(address));
            return null;
        }
        return getDriver(capAddr, driverType);
    }

    public void registerDriver(CapabilityAddress address, Object driver) {
        if (driver == null) {
            log.warn("Attempted to register null driver for address: {}", address);
            return;
        }
        driverRegistry.put(address.getAddress(), driver);
        log.info("Registered driver for address: {} ({})", address, driver.getClass().getSimpleName());
    }

    public void unregisterDriver(CapabilityAddress address) {
        Object removed = driverRegistry.remove(address.getAddress());
        if (removed != null) {
            log.info("Unregistered driver for address: {}", address);
        }
    }

    public void bind(CapabilityAddress address, String providerId) {
        bindingRegistry.put(address.getAddress(), providerId);
        log.info("Bound address {} to provider: {}", address, providerId);
    }

    public void unbind(CapabilityAddress address) {
        bindingRegistry.remove(address.getAddress());
        log.info("Unbound address: {}", address);
    }

    public String getBinding(CapabilityAddress address) {
        return bindingRegistry.get(address.getAddress());
    }

    public void setFallback(CapabilityAddress address, String providerId) {
        fallbackRegistry.put(address.getAddress(), providerId);
        log.info("Set fallback for address {}: {}", address, providerId);
    }

    public String getFallback(CapabilityAddress address) {
        return fallbackRegistry.get(address.getAddress());
    }

    public String resolveProvider(CapabilityAddress address) {
        String provider = getBinding(address);
        if (provider != null) {
            return provider;
        }
        return getFallback(address);
    }

    public Set<CapabilityAddress> getActiveDrivers(CapabilityCategory category) {
        Set<CapabilityAddress> result = new java.util.HashSet<>();
        for (Integer address : driverRegistry.keySet()) {
            CapabilityAddress capAddr = CapabilityAddress.fromAddress(address);
            if (capAddr != null && capAddr.getCategory() == category) {
                result.add(capAddr);
            }
        }
        return result;
    }

    public boolean hasDriver(CapabilityAddress address) {
        return driverRegistry.containsKey(address.getAddress());
    }

    public int getDriverCount() {
        return driverRegistry.size();
    }

    public void clear() {
        driverRegistry.clear();
        bindingRegistry.clear();
        fallbackRegistry.clear();
        log.info("Cleared all capability router data");
    }
}
