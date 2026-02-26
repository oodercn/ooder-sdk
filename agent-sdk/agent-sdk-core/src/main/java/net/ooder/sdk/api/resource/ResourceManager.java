package net.ooder.sdk.api.resource;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 资源管理器接口
 *
 * 管理系统级资源：存储、计算、网络
 */
public interface ResourceManager {

    /**
     * 获取存储资源管理器
     */
    StorageResource getStorageResource();

    /**
     * 获取计算资源管理器
     */
    ComputeResource getComputeResource();

    /**
     * 获取网络资源管理器
     */
    NetworkResource getNetworkResource();

    /**
     * 获取资源使用统计
     */
    ResourceStatistics getStatistics();

    /**
     * 申请资源
     */
    CompletableFuture<ResourceAllocation> allocate(ResourceRequest request);

    /**
     * 释放资源
     */
    void release(String allocationId);

    /**
     * 存储资源
     */
    interface StorageResource {
        /**
         * 获取总容量
         */
        long getTotalCapacity();

        /**
         * 获取已使用容量
         */
        long getUsedCapacity();

        /**
         * 获取可用容量
         */
        long getAvailableCapacity();

        /**
         * 申请存储空间
         */
        StorageAllocation allocate(long size, String purpose);

        /**
         * 释放存储空间
         */
        void release(String allocationId);
    }

    /**
     * 计算资源
     */
    interface ComputeResource {
        /**
         * 获取 CPU 核心数
         */
        int getCpuCores();

        /**
         * 获取 CPU 使用率
         */
        double getCpuUsage();

        /**
         * 获取总内存
         */
        long getTotalMemory();

        /**
         * 获取已使用内存
         */
        long getUsedMemory();

        /**
         * 申请计算资源
         */
        ComputeAllocation allocate(int cpuCores, long memory, String purpose);

        /**
         * 释放计算资源
         */
        void release(String allocationId);
    }

    /**
     * 网络资源
     */
    interface NetworkResource {
        /**
         * 获取上传带宽
         */
        long getUploadBandwidth();

        /**
         * 获取下载带宽
         */
        long getDownloadBandwidth();

        /**
         * 获取当前连接数
         */
        int getActiveConnections();

        /**
         * 申请网络资源
         */
        NetworkAllocation allocate(long bandwidth, int connections, String purpose);

        /**
         * 释放网络资源
         */
        void release(String allocationId);
    }

    /**
     * 资源申请
     */
    class ResourceRequest {
        private String requesterId;
        private long storageSize;
        private int cpuCores;
        private long memory;
        private long bandwidth;
        private int connections;
        private String purpose;
        private long duration;

        public String getRequesterId() {
            return requesterId;
        }

        public void setRequesterId(String requesterId) {
            this.requesterId = requesterId;
        }

        public long getStorageSize() {
            return storageSize;
        }

        public void setStorageSize(long storageSize) {
            this.storageSize = storageSize;
        }

        public int getCpuCores() {
            return cpuCores;
        }

        public void setCpuCores(int cpuCores) {
            this.cpuCores = cpuCores;
        }

        public long getMemory() {
            return memory;
        }

        public void setMemory(long memory) {
            this.memory = memory;
        }

        public long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(long bandwidth) {
            this.bandwidth = bandwidth;
        }

        public int getConnections() {
            return connections;
        }

        public void setConnections(int connections) {
            this.connections = connections;
        }

        public String getPurpose() {
            return purpose;
        }

        public void setPurpose(String purpose) {
            this.purpose = purpose;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
    }

    /**
     * 资源分配结果
     */
    class ResourceAllocation {
        private String allocationId;
        private boolean success;
        private String message;
        private StorageAllocation storage;
        private ComputeAllocation compute;
        private NetworkAllocation network;

        public String getAllocationId() {
            return allocationId;
        }

        public void setAllocationId(String allocationId) {
            this.allocationId = allocationId;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public StorageAllocation getStorage() {
            return storage;
        }

        public void setStorage(StorageAllocation storage) {
            this.storage = storage;
        }

        public ComputeAllocation getCompute() {
            return compute;
        }

        public void setCompute(ComputeAllocation compute) {
            this.compute = compute;
        }

        public NetworkAllocation getNetwork() {
            return network;
        }

        public void setNetwork(NetworkAllocation network) {
            this.network = network;
        }
    }

    /**
     * 存储分配
     */
    class StorageAllocation {
        private String allocationId;
        private long size;
        private String path;
        private String purpose;

        public String getAllocationId() {
            return allocationId;
        }

        public void setAllocationId(String allocationId) {
            this.allocationId = allocationId;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPurpose() {
            return purpose;
        }

        public void setPurpose(String purpose) {
            this.purpose = purpose;
        }
    }

    /**
     * 计算分配
     */
    class ComputeAllocation {
        private String allocationId;
        private int cpuCores;
        private long memory;
        private String purpose;

        public String getAllocationId() {
            return allocationId;
        }

        public void setAllocationId(String allocationId) {
            this.allocationId = allocationId;
        }

        public int getCpuCores() {
            return cpuCores;
        }

        public void setCpuCores(int cpuCores) {
            this.cpuCores = cpuCores;
        }

        public long getMemory() {
            return memory;
        }

        public void setMemory(long memory) {
            this.memory = memory;
        }

        public String getPurpose() {
            return purpose;
        }

        public void setPurpose(String purpose) {
            this.purpose = purpose;
        }
    }

    /**
     * 网络分配
     */
    class NetworkAllocation {
        private String allocationId;
        private long bandwidth;
        private int connections;
        private String purpose;

        public String getAllocationId() {
            return allocationId;
        }

        public void setAllocationId(String allocationId) {
            this.allocationId = allocationId;
        }

        public long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(long bandwidth) {
            this.bandwidth = bandwidth;
        }

        public int getConnections() {
            return connections;
        }

        public void setConnections(int connections) {
            this.connections = connections;
        }

        public String getPurpose() {
            return purpose;
        }

        public void setPurpose(String purpose) {
            this.purpose = purpose;
        }
    }

    /**
     * 资源统计
     */
    class ResourceStatistics {
        private long totalStorage;
        private long usedStorage;
        private int totalCpuCores;
        private double cpuUsage;
        private long totalMemory;
        private long usedMemory;
        private long totalBandwidth;
        private long usedBandwidth;
        private int activeConnections;

        public long getTotalStorage() {
            return totalStorage;
        }

        public void setTotalStorage(long totalStorage) {
            this.totalStorage = totalStorage;
        }

        public long getUsedStorage() {
            return usedStorage;
        }

        public void setUsedStorage(long usedStorage) {
            this.usedStorage = usedStorage;
        }

        public int getTotalCpuCores() {
            return totalCpuCores;
        }

        public void setTotalCpuCores(int totalCpuCores) {
            this.totalCpuCores = totalCpuCores;
        }

        public double getCpuUsage() {
            return cpuUsage;
        }

        public void setCpuUsage(double cpuUsage) {
            this.cpuUsage = cpuUsage;
        }

        public long getTotalMemory() {
            return totalMemory;
        }

        public void setTotalMemory(long totalMemory) {
            this.totalMemory = totalMemory;
        }

        public long getUsedMemory() {
            return usedMemory;
        }

        public void setUsedMemory(long usedMemory) {
            this.usedMemory = usedMemory;
        }

        public long getTotalBandwidth() {
            return totalBandwidth;
        }

        public void setTotalBandwidth(long totalBandwidth) {
            this.totalBandwidth = totalBandwidth;
        }

        public long getUsedBandwidth() {
            return usedBandwidth;
        }

        public void setUsedBandwidth(long usedBandwidth) {
            this.usedBandwidth = usedBandwidth;
        }

        public int getActiveConnections() {
            return activeConnections;
        }

        public void setActiveConnections(int activeConnections) {
            this.activeConnections = activeConnections;
        }
    }
}
