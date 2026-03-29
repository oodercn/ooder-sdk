package net.ooder.scene.log;

import java.util.Map;

public class LogStorageConfig {

    private String storageType;
    private String connectionString;
    private String database;
    private String table;
    private int batchSize = 100;
    private int flushIntervalMs = 1000;
    private boolean asyncEnabled = true;
    private Map<String, Object> properties;

    public String getStorageType() { return storageType; }
    public void setStorageType(String storageType) { this.storageType = storageType; }

    public String getConnectionString() { return connectionString; }
    public void setConnectionString(String connectionString) { this.connectionString = connectionString; }

    public String getDatabase() { return database; }
    public void setDatabase(String database) { this.database = database; }

    public String getTable() { return table; }
    public void setTable(String table) { this.table = table; }

    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }

    public int getFlushIntervalMs() { return flushIntervalMs; }
    public void setFlushIntervalMs(int flushIntervalMs) { this.flushIntervalMs = flushIntervalMs; }

    public boolean isAsyncEnabled() { return asyncEnabled; }
    public void setAsyncEnabled(boolean asyncEnabled) { this.asyncEnabled = asyncEnabled; }

    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }
}
