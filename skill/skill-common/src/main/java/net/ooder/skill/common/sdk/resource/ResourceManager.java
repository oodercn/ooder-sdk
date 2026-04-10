package net.ooder.skill.common.sdk.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ResourceManager {

    private static final Logger log = LoggerFactory.getLogger(ResourceManager.class);

    private MemoryMXBean memoryMXBean;
    private OperatingSystemMXBean osMXBean;

    private final ConcurrentMap<String, ResourceQuota> quotas = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, ResourceUsage> usageStats = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        memoryMXBean = ManagementFactory.getMemoryMXBean();
        osMXBean = ManagementFactory.getOperatingSystemMXBean();
        log.info("ResourceManager initialized");
    }

    public StorageResource getStorageResource(Path path) {
        try {
            FileStore store = Files.getFileStore(path);
            return new StorageResource(
                    store.getTotalSpace(),
                    store.getUsableSpace(),
                    store.getUnallocatedSpace(),
                    path.toString()
            );
        } catch (Exception e) {
            log.error("Failed to get storage resource for: {}", path, e);
            return null;
        }
    }

    public List<StorageResource> getAllStorageResources() {
        List<StorageResource> resources = new ArrayList<>();
        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            StorageResource resource = getStorageResource(root);
            if (resource != null) {
                resources.add(resource);
            }
        }
        return resources;
    }

    public boolean allocateStorageQuota(String skillId, long quotaBytes) {
        StorageResource systemResource = getStorageResource(FileSystems.getDefault().getPath("."));
        if (systemResource == null || systemResource.getUsableSpace() < quotaBytes) {
            log.warn("Insufficient storage for skill: {}, requested: {}", skillId, quotaBytes);
            return false;
        }

        ResourceQuota quota = quotas.computeIfAbsent(skillId, k -> new ResourceQuota());
        quota.setStorageQuota(quotaBytes);
        log.info("Allocated storage quota for skill: {} = {} bytes", skillId, quotaBytes);
        return true;
    }

    public ComputeResource getComputeResource() {
        return new ComputeResource(
                osMXBean.getAvailableProcessors(),
                osMXBean.getSystemLoadAverage(),
                memoryMXBean.getHeapMemoryUsage().getUsed(),
                memoryMXBean.getHeapMemoryUsage().getMax(),
                memoryMXBean.getNonHeapMemoryUsage().getUsed()
        );
    }

    public boolean allocateComputeQuota(String skillId, int maxCpuPercent, long maxMemoryBytes) {
        ComputeResource systemResource = getComputeResource();

        if (maxCpuPercent > 100 || maxMemoryBytes > systemResource.getHeapMemoryMax()) {
            log.warn("Invalid compute quota for skill: {}", skillId);
            return false;
        }

        ResourceQuota quota = quotas.computeIfAbsent(skillId, k -> new ResourceQuota());
        quota.setMaxCpuPercent(maxCpuPercent);
        quota.setMaxMemoryBytes(maxMemoryBytes);
        log.info("Allocated compute quota for skill: {} = CPU: {}%, Memory: {} bytes",
                skillId, maxCpuPercent, maxMemoryBytes);
        return true;
    }

    public NetworkResource getNetworkResource() {
        return new NetworkResource(
                getHostname(),
                10000,
                20000
        );
    }

    public boolean allocateNetworkQuota(String skillId, int maxConnections, int maxBandwidthKbps) {
        ResourceQuota quota = quotas.computeIfAbsent(skillId, k -> new ResourceQuota());
        quota.setMaxConnections(maxConnections);
        quota.setMaxBandwidthKbps(maxBandwidthKbps);
        log.info("Allocated network quota for skill: {} = Connections: {}, Bandwidth: {} Kbps",
                skillId, maxConnections, maxBandwidthKbps);
        return true;
    }

    public ResourceQuota getQuota(String skillId) {
        return quotas.get(skillId);
    }

    public ConcurrentMap<String, ResourceQuota> getAllQuotas() {
        return new ConcurrentHashMap<>(quotas);
    }

    public void releaseQuota(String skillId) {
        quotas.remove(skillId);
        usageStats.remove(skillId);
        log.info("Released quota for skill: {}", skillId);
    }

    public void recordUsage(String skillId, ResourceUsage usage) {
        usageStats.put(skillId, usage);
    }

    public ResourceUsage getUsage(String skillId) {
        return usageStats.get(skillId);
    }

    private String getHostname() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static class StorageResource {
        private long totalSpace;
        private long usableSpace;
        private long unallocatedSpace;
        private String path;

        public StorageResource() {
        }

        public StorageResource(long totalSpace, long usableSpace, long unallocatedSpace, String path) {
            this.totalSpace = totalSpace;
            this.usableSpace = usableSpace;
            this.unallocatedSpace = unallocatedSpace;
            this.path = path;
        }

        public double getUsagePercent() {
            if (totalSpace == 0) return 0;
            return (double) (totalSpace - usableSpace) / totalSpace * 100;
        }

        public long getTotalSpace() {
            return totalSpace;
        }

        public void setTotalSpace(long totalSpace) {
            this.totalSpace = totalSpace;
        }

        public long getUsableSpace() {
            return usableSpace;
        }

        public void setUsableSpace(long usableSpace) {
            this.usableSpace = usableSpace;
        }

        public long getUnallocatedSpace() {
            return unallocatedSpace;
        }

        public void setUnallocatedSpace(long unallocatedSpace) {
            this.unallocatedSpace = unallocatedSpace;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class ComputeResource {
        private int availableProcessors;
        private double systemLoadAverage;
        private long heapMemoryUsed;
        private long heapMemoryMax;
        private long nonHeapMemoryUsed;

        public ComputeResource() {
        }

        public ComputeResource(int availableProcessors, double systemLoadAverage, long heapMemoryUsed, long heapMemoryMax, long nonHeapMemoryUsed) {
            this.availableProcessors = availableProcessors;
            this.systemLoadAverage = systemLoadAverage;
            this.heapMemoryUsed = heapMemoryUsed;
            this.heapMemoryMax = heapMemoryMax;
            this.nonHeapMemoryUsed = nonHeapMemoryUsed;
        }

        public double getMemoryUsagePercent() {
            if (heapMemoryMax == 0) return 0;
            return (double) heapMemoryUsed / heapMemoryMax * 100;
        }

        public int getAvailableProcessors() {
            return availableProcessors;
        }

        public void setAvailableProcessors(int availableProcessors) {
            this.availableProcessors = availableProcessors;
        }

        public double getSystemLoadAverage() {
            return systemLoadAverage;
        }

        public void setSystemLoadAverage(double systemLoadAverage) {
            this.systemLoadAverage = systemLoadAverage;
        }

        public long getHeapMemoryUsed() {
            return heapMemoryUsed;
        }

        public void setHeapMemoryUsed(long heapMemoryUsed) {
            this.heapMemoryUsed = heapMemoryUsed;
        }

        public long getHeapMemoryMax() {
            return heapMemoryMax;
        }

        public void setHeapMemoryMax(long heapMemoryMax) {
            this.heapMemoryMax = heapMemoryMax;
        }

        public long getNonHeapMemoryUsed() {
            return nonHeapMemoryUsed;
        }

        public void setNonHeapMemoryUsed(long nonHeapMemoryUsed) {
            this.nonHeapMemoryUsed = nonHeapMemoryUsed;
        }
    }

    public static class NetworkResource {
        private String hostname;
        private int portRangeStart;
        private int portRangeEnd;

        public NetworkResource() {
        }

        public NetworkResource(String hostname, int portRangeStart, int portRangeEnd) {
            this.hostname = hostname;
            this.portRangeStart = portRangeStart;
            this.portRangeEnd = portRangeEnd;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public int getPortRangeStart() {
            return portRangeStart;
        }

        public void setPortRangeStart(int portRangeStart) {
            this.portRangeStart = portRangeStart;
        }

        public int getPortRangeEnd() {
            return portRangeEnd;
        }

        public void setPortRangeEnd(int portRangeEnd) {
            this.portRangeEnd = portRangeEnd;
        }
    }

    public static class ResourceQuota {
        private long storageQuota;
        private int maxCpuPercent;
        private long maxMemoryBytes;
        private int maxConnections;
        private int maxBandwidthKbps;

        public long getStorageQuota() {
            return storageQuota;
        }

        public void setStorageQuota(long storageQuota) {
            this.storageQuota = storageQuota;
        }

        public int getMaxCpuPercent() {
            return maxCpuPercent;
        }

        public void setMaxCpuPercent(int maxCpuPercent) {
            this.maxCpuPercent = maxCpuPercent;
        }

        public long getMaxMemoryBytes() {
            return maxMemoryBytes;
        }

        public void setMaxMemoryBytes(long maxMemoryBytes) {
            this.maxMemoryBytes = maxMemoryBytes;
        }

        public int getMaxConnections() {
            return maxConnections;
        }

        public void setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
        }

        public int getMaxBandwidthKbps() {
            return maxBandwidthKbps;
        }

        public void setMaxBandwidthKbps(int maxBandwidthKbps) {
            this.maxBandwidthKbps = maxBandwidthKbps;
        }
    }

    public static class ResourceUsage {
        private String skillId;
        private long storageUsed;
        private long memoryUsed;
        private int cpuPercent;
        private int activeConnections;
        private long timestamp;

        public ResourceUsage() {
        }

        public ResourceUsage(String skillId, long storageUsed, long memoryUsed, int cpuPercent, int activeConnections, long timestamp) {
            this.skillId = skillId;
            this.storageUsed = storageUsed;
            this.memoryUsed = memoryUsed;
            this.cpuPercent = cpuPercent;
            this.activeConnections = activeConnections;
            this.timestamp = timestamp;
        }

        public String getSkillId() {
            return skillId;
        }

        public void setSkillId(String skillId) {
            this.skillId = skillId;
        }

        public long getStorageUsed() {
            return storageUsed;
        }

        public void setStorageUsed(long storageUsed) {
            this.storageUsed = storageUsed;
        }

        public long getMemoryUsed() {
            return memoryUsed;
        }

        public void setMemoryUsed(long memoryUsed) {
            this.memoryUsed = memoryUsed;
        }

        public int getCpuPercent() {
            return cpuPercent;
        }

        public void setCpuPercent(int cpuPercent) {
            this.cpuPercent = cpuPercent;
        }

        public int getActiveConnections() {
            return activeConnections;
        }

        public void setActiveConnections(int activeConnections) {
            this.activeConnections = activeConnections;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
