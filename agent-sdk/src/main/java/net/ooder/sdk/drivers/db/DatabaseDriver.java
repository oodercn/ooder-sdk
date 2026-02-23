package net.ooder.sdk.drivers.db;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface DatabaseDriver {
    
    void init(DatabaseConfig config);
    
    CompletableFuture<Integer> executeUpdate(String sql, Object... params);
    
    CompletableFuture<Map<String, Object>> queryOne(String sql, Object... params);
    
    CompletableFuture<List<Map<String, Object>>> queryList(String sql, Object... params);
    
    CompletableFuture<Long> insert(String table, Map<String, Object> data);
    
    CompletableFuture<Integer> update(String table, Map<String, Object> data, String whereClause, Object... whereParams);
    
    CompletableFuture<Integer> delete(String table, String whereClause, Object... whereParams);
    
    CompletableFuture<Long> count(String table, String whereClause, Object... whereParams);
    
    CompletableFuture<Boolean> tableExists(String tableName);
    
    CompletableFuture<Void> createTable(String tableName, Map<String, String> columns);
    
    CompletableFuture<Void> executeScript(String script);
    
    void beginTransaction();
    
    void commit();
    
    void rollback();
    
    void close();
    
    boolean isConnected();
    
    String getDriverName();
    
    String getDriverVersion();
    
    class DatabaseConfig {
        private String url;
        private String username;
        private String password;
        private String database;
        private int maxConnections = 10;
        private int connectionTimeout = 30000;
        private boolean autoCommit = true;
        private Map<String, Object> properties = new java.util.concurrent.ConcurrentHashMap<>();
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getDatabase() { return database; }
        public void setDatabase(String database) { this.database = database; }
        
        public int getMaxConnections() { return maxConnections; }
        public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }
        
        public int getConnectionTimeout() { return connectionTimeout; }
        public void setConnectionTimeout(int connectionTimeout) { this.connectionTimeout = connectionTimeout; }
        
        public boolean isAutoCommit() { return autoCommit; }
        public void setAutoCommit(boolean autoCommit) { this.autoCommit = autoCommit; }
        
        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }
}
