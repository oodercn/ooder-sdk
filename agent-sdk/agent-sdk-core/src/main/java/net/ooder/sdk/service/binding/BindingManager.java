package net.ooder.sdk.service.binding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BindingManager {

    private static final Logger log = LoggerFactory.getLogger(BindingManager.class);

    private final Map<String, DeviceBinding> bindings = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> deviceBindings = new ConcurrentHashMap<>();

    public DeviceBinding createBinding(String sourceDevice, String sourceCap,
                                       String targetDevice, String targetCap,
                                       DeviceBinding.BindingType bindingType) {
        DeviceBinding binding = new DeviceBinding(
            sourceDevice, sourceCap, targetDevice, targetCap, bindingType);

        bindings.put(binding.getBindingId(), binding);

        deviceBindings.computeIfAbsent(sourceDevice, k -> ConcurrentHashMap.newKeySet())
            .add(binding.getBindingId());
        deviceBindings.computeIfAbsent(targetDevice, k -> ConcurrentHashMap.newKeySet())
            .add(binding.getBindingId());

        log.info("Created binding: {} ({} -> {})",
            binding.getBindingId(), sourceDevice, targetDevice);

        return binding;
    }

    public void removeBinding(String bindingId) {
        DeviceBinding binding = bindings.remove(bindingId);
        if (binding != null) {
            Set<String> sourceBindings = deviceBindings.get(binding.getSourceDevice());
            if (sourceBindings != null) {
                sourceBindings.remove(bindingId);
            }

            Set<String> targetBindings = deviceBindings.get(binding.getTargetDevice());
            if (targetBindings != null) {
                targetBindings.remove(bindingId);
            }

            binding.setStatus(DeviceBinding.BindingStatus.REMOVED);
            log.info("Removed binding: {}", bindingId);
        }
    }

    public Optional<DeviceBinding> getBinding(String bindingId) {
        return Optional.ofNullable(bindings.get(bindingId));
    }

    public List<DeviceBinding> getAllBindings() {
        return new ArrayList<>(bindings.values());
    }

    public List<DeviceBinding> getBindingsByDevice(String deviceId) {
        Set<String> bindingIds = deviceBindings.get(deviceId);
        if (bindingIds == null || bindingIds.isEmpty()) {
            return Collections.emptyList();
        }

        return bindingIds.stream()
            .map(bindings::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public List<DeviceBinding> getBindingsBySource(String sourceDevice) {
        return bindings.values().stream()
            .filter(b -> sourceDevice.equals(b.getSourceDevice()))
            .collect(Collectors.toList());
    }

    public List<DeviceBinding> getBindingsByTarget(String targetDevice) {
        return bindings.values().stream()
            .filter(b -> targetDevice.equals(b.getTargetDevice()))
            .collect(Collectors.toList());
    }

    public List<DeviceBinding> getActiveBindings() {
        return bindings.values().stream()
            .filter(DeviceBinding::isActive)
            .collect(Collectors.toList());
    }

    public List<DeviceBinding> getFailedBindings() {
        return bindings.values().stream()
            .filter(DeviceBinding::isFailed)
            .collect(Collectors.toList());
    }

    public List<DeviceBinding> getStrongBindings() {
        return bindings.values().stream()
            .filter(DeviceBinding::isStrongBinding)
            .collect(Collectors.toList());
    }

    public List<DeviceBinding> getWeakBindings() {
        return bindings.values().stream()
            .filter(DeviceBinding::isWeakBinding)
            .collect(Collectors.toList());
    }

    public void suspendBinding(String bindingId) {
        DeviceBinding binding = bindings.get(bindingId);
        if (binding != null) {
            binding.setStatus(DeviceBinding.BindingStatus.SUSPENDED);
            log.info("Suspended binding: {}", bindingId);
        }
    }

    public void resumeBinding(String bindingId) {
        DeviceBinding binding = bindings.get(bindingId);
        if (binding != null && binding.isSuspended()) {
            binding.setStatus(DeviceBinding.BindingStatus.ACTIVE);
            log.info("Resumed binding: {}", bindingId);
        }
    }

    public void markBindingFailed(String bindingId, String reason) {
        DeviceBinding binding = bindings.get(bindingId);
        if (binding != null) {
            binding.setStatus(DeviceBinding.BindingStatus.FAILED);
            binding.addHistory("FAILURE", reason);
            log.warn("Marked binding as failed: {} - {}", bindingId, reason);
        }
    }

    public boolean replaceDevice(String oldDeviceId, String newDeviceId) {
        List<DeviceBinding> affectedBindings = getBindingsByDevice(oldDeviceId);
        boolean success = true;

        for (DeviceBinding binding : affectedBindings) {
            if (binding.isStrongBinding()) {
                log.warn("Cannot replace device in strong binding: {}", binding.getBindingId());
                success = false;
                continue;
            }

            if (oldDeviceId.equals(binding.getSourceDevice())) {
                DeviceBinding newBinding = createBinding(
                    newDeviceId, binding.getSourceCap(),
                    binding.getTargetDevice(), binding.getTargetCap(),
                    binding.getBindingType());
                Map<String, Object> bindingConfig = binding.getConfig();
                newBinding.setConfig(bindingConfig);
                removeBinding(binding.getBindingId());
                log.info("Replaced device in binding: {} -> {}",
                    binding.getBindingId(), newBinding.getBindingId());
            } else if (oldDeviceId.equals(binding.getTargetDevice())) {
                DeviceBinding newBinding = createBinding(
                    binding.getSourceDevice(), binding.getSourceCap(),
                    newDeviceId, binding.getTargetCap(),
                    binding.getBindingType());
                Map<String, Object> bindingConfig = binding.getConfig();
                newBinding.setConfig(bindingConfig);
                removeBinding(binding.getBindingId());
                log.info("Replaced device in binding: {} -> {}",
                    binding.getBindingId(), newBinding.getBindingId());
            }
        }

        return success;
    }

    public void handleDeviceFailure(String deviceId, String reason) {
        List<DeviceBinding> affectedBindings = getBindingsByDevice(deviceId);

        for (DeviceBinding binding : affectedBindings) {
            markBindingFailed(binding.getBindingId(),
                "Device failure: " + deviceId + " - " + reason);
        }

        log.info("Handled device failure for device: {} ({} bindings affected)",
            deviceId, affectedBindings.size());
    }

    public BindingStatistics getStatistics() {
        BindingStatistics stats = new BindingStatistics();

        for (DeviceBinding binding : bindings.values()) {
            stats.totalBindings++;

            if (binding.isActive()) stats.activeBindings++;
            else if (binding.isSuspended()) stats.suspendedBindings++;
            else if (binding.isFailed()) stats.failedBindings++;

            if (binding.isStrongBinding()) stats.strongBindings++;
            else stats.weakBindings++;
        }

        stats.deviceCount = deviceBindings.size();

        return stats;
    }

    public static class BindingStatistics {
        public int totalBindings = 0;
        public int activeBindings = 0;
        public int suspendedBindings = 0;
        public int failedBindings = 0;
        public int strongBindings = 0;
        public int weakBindings = 0;
        public int deviceCount = 0;

        @Override
        public String toString() {
            return String.format(
                "BindingStatistics{total=%d, active=%d, suspended=%d, failed=%d, strong=%d, weak=%d, devices=%d}",
                totalBindings, activeBindings, suspendedBindings, failedBindings,
                strongBindings, weakBindings, deviceCount);
        }
    }

    public void clear() {
        bindings.clear();
        deviceBindings.clear();
        log.info("All bindings cleared");
    }
}
