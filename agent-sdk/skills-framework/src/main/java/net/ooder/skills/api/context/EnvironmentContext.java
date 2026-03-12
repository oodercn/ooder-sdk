package net.ooder.skills.api.context;

import java.util.Map;

/**
 * 环境上下文
 *
 * <p>描述安装环境的运行时信息</p>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public class EnvironmentContext {

    private String environmentId;
    private String environmentType;
    private String version;
    private RuntimeInfo runtimeInfo;
    private DatabaseInfo databaseInfo;
    private CacheInfo cacheInfo;
    private MessageQueueInfo messageQueueInfo;
    private StorageInfo storageInfo;
    private NetworkInfo networkInfo;
    private Map<String, Object> extensions;

    public String getEnvironmentId() { return environmentId; }
    public void setEnvironmentId(String environmentId) { this.environmentId = environmentId; }

    public String getEnvironmentType() { return environmentType; }
    public void setEnvironmentType(String environmentType) { this.environmentType = environmentType; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public RuntimeInfo getRuntimeInfo() { return runtimeInfo; }
    public void setRuntimeInfo(RuntimeInfo runtimeInfo) { this.runtimeInfo = runtimeInfo; }

    public DatabaseInfo getDatabaseInfo() { return databaseInfo; }
    public void setDatabaseInfo(DatabaseInfo databaseInfo) { this.databaseInfo = databaseInfo; }

    public CacheInfo getCacheInfo() { return cacheInfo; }
    public void setCacheInfo(CacheInfo cacheInfo) { this.cacheInfo = cacheInfo; }

    public MessageQueueInfo getMessageQueueInfo() { return messageQueueInfo; }
    public void setMessageQueueInfo(MessageQueueInfo messageQueueInfo) { this.messageQueueInfo = messageQueueInfo; }

    public StorageInfo getStorageInfo() { return storageInfo; }
    public void setStorageInfo(StorageInfo storageInfo) { this.storageInfo = storageInfo; }

    public NetworkInfo getNetworkInfo() { return networkInfo; }
    public void setNetworkInfo(NetworkInfo networkInfo) { this.networkInfo = networkInfo; }

    public Map<String, Object> getExtensions() { return extensions; }
    public void setExtensions(Map<String, Object> extensions) { this.extensions = extensions; }

    /**
     * 运行时信息
     */
    public static class RuntimeInfo {
        private String javaVersion;
        private String osName;
        private String osVersion;
        private int availableProcessors;
        private long maxMemory;
        private long freeMemory;

        public String getJavaVersion() { return javaVersion; }
        public void setJavaVersion(String javaVersion) { this.javaVersion = javaVersion; }

        public String getOsName() { return osName; }
        public void setOsName(String osName) { this.osName = osName; }

        public String getOsVersion() { return osVersion; }
        public void setOsVersion(String osVersion) { this.osVersion = osVersion; }

        public int getAvailableProcessors() { return availableProcessors; }
        public void setAvailableProcessors(int availableProcessors) { this.availableProcessors = availableProcessors; }

        public long getMaxMemory() { return maxMemory; }
        public void setMaxMemory(long maxMemory) { this.maxMemory = maxMemory; }

        public long getFreeMemory() { return freeMemory; }
        public void setFreeMemory(long freeMemory) { this.freeMemory = freeMemory; }
    }

    /**
     * 数据库信息
     */
    public static class DatabaseInfo {
        private String type;
        private String version;
        private String url;
        private boolean available;
        private long connectionPoolSize;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }

        public long getConnectionPoolSize() { return connectionPoolSize; }
        public void setConnectionPoolSize(long connectionPoolSize) { this.connectionPoolSize = connectionPoolSize; }
    }

    /**
     * 缓存信息
     */
    public static class CacheInfo {
        private String type;
        private String version;
        private boolean available;
        private long maxSize;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }

        public long getMaxSize() { return maxSize; }
        public void setMaxSize(long maxSize) { this.maxSize = maxSize; }
    }

    /**
     * 消息队列信息
     */
    public static class MessageQueueInfo {
        private String type;
        private String version;
        private boolean available;
        private int consumerCount;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }

        public int getConsumerCount() { return consumerCount; }
        public void setConsumerCount(int consumerCount) { this.consumerCount = consumerCount; }
    }

    /**
     * 存储信息
     */
    public static class StorageInfo {
        private String type;
        private boolean available;
        private long totalSpace;
        private long freeSpace;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }

        public long getTotalSpace() { return totalSpace; }
        public void setTotalSpace(long totalSpace) { this.totalSpace = totalSpace; }

        public long getFreeSpace() { return freeSpace; }
        public void setFreeSpace(long freeSpace) { this.freeSpace = freeSpace; }
    }

    /**
     * 网络信息
     */
    public static class NetworkInfo {
        private String hostName;
        private String ipAddress;
        private int port;
        private boolean sslEnabled;

        public String getHostName() { return hostName; }
        public void setHostName(String hostName) { this.hostName = hostName; }

        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }

        public boolean isSslEnabled() { return sslEnabled; }
        public void setSslEnabled(boolean sslEnabled) { this.sslEnabled = sslEnabled; }
    }
}
