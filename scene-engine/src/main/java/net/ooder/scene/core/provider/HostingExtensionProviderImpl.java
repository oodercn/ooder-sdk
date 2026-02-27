package net.ooder.scene.core.provider;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * HostingExtension Provider Implementation
 *
 * <p>Implements HostingExtensionProvider interface, provides auto-scaling, service discovery, and volume management capabilities</p>
 */
public class HostingExtensionProviderImpl implements HostingExtensionProvider {

    private static final String PROVIDER_NAME = "hosting-extension-provider";
    private static final String VERSION = "1.0.0";

    private SceneEngine engine;
    private boolean initialized = false;
    private boolean running = false;

    private final Map<String, AutoScalePolicy> autoScalePolicies = new ConcurrentHashMap<>();
    private final Map<String, ServiceEndpoint> services = new ConcurrentHashMap<>();
    private final Map<String, Volume> volumes = new ConcurrentHashMap<>();
    private final AtomicLong policyIdCounter = new AtomicLong(1);
    private final AtomicLong serviceIdCounter = new AtomicLong(1);
    private final AtomicLong volumeIdCounter = new AtomicLong(1);

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        initializeDefaultData();
        this.initialized = true;
    }

    @Override
    public void start() {
        if (!initialized) {
            throw new IllegalStateException("Provider not initialized");
        }
        this.running = true;
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    private void initializeDefaultData() {
        createDefaultAutoScalePolicies();
        createDefaultServices();
        createDefaultVolumes();
    }

    private void createDefaultAutoScalePolicies() {
        String[] metricTypes = {"CPU", "MEMORY", "REQUEST_COUNT", "RESPONSE_TIME"};
        for (int i = 0; i < 5; i++) {
            AutoScalePolicy policy = new AutoScalePolicy();
            policy.setPolicyId("policy-" + policyIdCounter.getAndIncrement());
            policy.setPolicyName("Auto Scale Policy " + (i + 1));
            policy.setInstanceId("instance-" + (i + 1));
            policy.setMetricType(metricTypes[i % metricTypes.length]);
            policy.setMinThreshold(30.0);
            policy.setMaxThreshold(80.0);
            policy.setMinReplicas(1);
            policy.setMaxReplicas(10);
            policy.setEnabled(true);
            policy.setStatus("ACTIVE");
            policy.setCreatedAt(System.currentTimeMillis() - 86400000 * (i + 1));
            policy.setUpdatedAt(System.currentTimeMillis());
            autoScalePolicies.put(policy.getPolicyId(), policy);
        }
    }

    private void createDefaultServices() {
        String[] serviceNames = {"api-gateway", "user-service", "skill-service", "scene-service", "storage-service"};
        String[] protocols = {"HTTP", "GRPC", "TCP"};
        for (int i = 0; i < 10; i++) {
            ServiceEndpoint service = new ServiceEndpoint();
            service.setServiceId("service-" + serviceIdCounter.getAndIncrement());
            service.setServiceName(serviceNames[i % serviceNames.length]);
            service.setInstanceId("instance-" + ((i % 5) + 1));
            service.setProtocol(protocols[i % protocols.length]);
            service.setHost("10.0." + ((i / 256) % 256) + "." + (i % 256));
            service.setPort(8080 + (i % 100));
            service.setPath("/api/v1");
            Map<String, String> metadata = new HashMap<>();
            metadata.put("version", "1.0." + (i % 10));
            metadata.put("environment", "production");
            service.setMetadata(metadata);
            service.setHealthy(true);
            service.setRegisteredAt(System.currentTimeMillis() - 3600000 * (i + 1));
            service.setLastHeartbeat(System.currentTimeMillis() - 60000 * (i + 1));
            services.put(service.getServiceId(), service);
        }
    }

    private void createDefaultVolumes() {
        String[] volumeTypes = {"SSD", "HDD", "NAS"};
        for (int i = 0; i < 8; i++) {
            Volume volume = new Volume();
            volume.setVolumeId("volume-" + volumeIdCounter.getAndIncrement());
            volume.setVolumeName("Volume " + (i + 1));
            volume.setVolumeType(volumeTypes[i % volumeTypes.length]);
            volume.setSizeGB(100 * ((i % 10) + 1));
            volume.setStatus("AVAILABLE");
            volume.setMounted(i % 3 == 0);
            if (volume.isMounted()) {
                volume.setInstanceId("instance-" + ((i % 5) + 1));
                volume.setMountPath("/data/volume" + (i + 1));
            }
            Map<String, String> labels = new HashMap<>();
            labels.put("environment", "production");
            volume.setLabels(labels);
            volume.setCreatedAt(System.currentTimeMillis() - 86400000 * (i + 1));
            volume.setUpdatedAt(System.currentTimeMillis());
            volumes.put(volume.getVolumeId(), volume);
        }
    }

    @Override
    public boolean checkCompatibility(String instanceId, Map<String, Object> requirements) {
        if (instanceId == null || instanceId.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public AutoScalePolicy getAutoScalePolicy(String instanceId) {
        return autoScalePolicies.values().stream()
            .filter(p -> instanceId.equals(p.getInstanceId()))
            .findFirst()
            .orElse(null);
    }

    @Override
    public AutoScalePolicy createAutoScalePolicy(AutoScalePolicy policy) {
        if (policy.getPolicyId() == null || policy.getPolicyId().isEmpty()) {
            policy.setPolicyId("policy-" + policyIdCounter.getAndIncrement());
        }
        policy.setCreatedAt(System.currentTimeMillis());
        policy.setUpdatedAt(System.currentTimeMillis());
        policy.setStatus("ACTIVE");
        autoScalePolicies.put(policy.getPolicyId(), policy);
        return policy;
    }

    @Override
    public boolean updateAutoScalePolicy(String policyId, AutoScalePolicy policy) {
        if (!autoScalePolicies.containsKey(policyId)) {
            return false;
        }
        policy.setPolicyId(policyId);
        policy.setUpdatedAt(System.currentTimeMillis());
        autoScalePolicies.put(policyId, policy);
        return true;
    }

    @Override
    public boolean deleteAutoScalePolicy(String policyId) {
        return autoScalePolicies.remove(policyId) != null;
    }

    @Override
    public boolean enableAutoScalePolicy(String policyId) {
        AutoScalePolicy policy = autoScalePolicies.get(policyId);
        if (policy == null) {
            return false;
        }
        policy.setEnabled(true);
        policy.setStatus("ACTIVE");
        policy.setUpdatedAt(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean disableAutoScalePolicy(String policyId) {
        AutoScalePolicy policy = autoScalePolicies.get(policyId);
        if (policy == null) {
            return false;
        }
        policy.setEnabled(false);
        policy.setStatus("DISABLED");
        policy.setUpdatedAt(System.currentTimeMillis());
        return true;
    }

    @Override
    public ServiceEndpoint registerService(ServiceEndpoint service) {
        if (service.getServiceId() == null || service.getServiceId().isEmpty()) {
            service.setServiceId("service-" + serviceIdCounter.getAndIncrement());
        }
        service.setRegisteredAt(System.currentTimeMillis());
        service.setLastHeartbeat(System.currentTimeMillis());
        service.setHealthy(true);
        services.put(service.getServiceId(), service);
        return service;
    }

    @Override
    public boolean unregisterService(String serviceId) {
        return services.remove(serviceId) != null;
    }

    @Override
    public ServiceEndpoint getService(String serviceId) {
        return services.get(serviceId);
    }

    @Override
    public List<ServiceEndpoint> getServicesByInstance(String instanceId) {
        return services.values().stream()
            .filter(s -> instanceId.equals(s.getInstanceId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<ServiceEndpoint> discoverService(String serviceName) {
        return services.values().stream()
            .filter(s -> serviceName.equals(s.getServiceName()) && s.isHealthy())
            .collect(Collectors.toList());
    }

    @Override
    public Volume createVolume(Volume volume) {
        if (volume.getVolumeId() == null || volume.getVolumeId().isEmpty()) {
            volume.setVolumeId("volume-" + volumeIdCounter.getAndIncrement());
        }
        volume.setCreatedAt(System.currentTimeMillis());
        volume.setUpdatedAt(System.currentTimeMillis());
        volume.setStatus("AVAILABLE");
        volume.setMounted(false);
        volumes.put(volume.getVolumeId(), volume);
        return volume;
    }

    @Override
    public Volume getVolume(String volumeId) {
        return volumes.get(volumeId);
    }

    @Override
    public boolean deleteVolume(String volumeId) {
        Volume volume = volumes.get(volumeId);
        if (volume == null) {
            return false;
        }
        if (volume.isMounted()) {
            return false;
        }
        volumes.remove(volumeId);
        return true;
    }

    @Override
    public boolean mountVolume(String volumeId, String instanceId, String mountPath) {
        Volume volume = volumes.get(volumeId);
        if (volume == null) {
            return false;
        }
        volume.setInstanceId(instanceId);
        volume.setMountPath(mountPath);
        volume.setMounted(true);
        volume.setStatus("MOUNTED");
        volume.setUpdatedAt(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean unmountVolume(String volumeId, String instanceId) {
        Volume volume = volumes.get(volumeId);
        if (volume == null || !instanceId.equals(volume.getInstanceId())) {
            return false;
        }
        volume.setInstanceId(null);
        volume.setMountPath(null);
        volume.setMounted(false);
        volume.setStatus("AVAILABLE");
        volume.setUpdatedAt(System.currentTimeMillis());
        return true;
    }

    @Override
    public PageResult<Volume> listVolumes(int page, int size) {
        List<Volume> allVolumes = new ArrayList<>(volumes.values());
        int start = (page - 1) * size;
        int end = Math.min(start + size, allVolumes.size());
        List<Volume> items = start < allVolumes.size() 
            ? allVolumes.subList(start, end) 
            : new ArrayList<>();
        return PageResult.of(items, allVolumes.size(), page, size);
    }

    @Override
    public List<Volume> getVolumesByInstance(String instanceId) {
        return volumes.values().stream()
            .filter(v -> instanceId.equals(v.getInstanceId()))
            .collect(Collectors.toList());
    }
}
