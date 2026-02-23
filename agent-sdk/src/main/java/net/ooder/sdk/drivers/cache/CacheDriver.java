package net.ooder.sdk.drivers.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface CacheDriver {
    
    void init(CacheConfig config);
    
    CompletableFuture<Void> set(String key, Object value);
    
    CompletableFuture<Void> set(String key, Object value, long ttl, TimeUnit unit);
    
    CompletableFuture<T> get(String key, Class type);
    
    CompletableFuture<Object> get(String key);
    
    CompletableFuture<Void> delete(String key);
    
    CompletableFuture<Void> deleteBatch(List<String> keys);
    
    CompletableFuture<Boolean> exists(String key);
    
    CompletableFuture<Boolean> expire(String key, long ttl, TimeUnit unit);
    
    CompletableFuture<Long> getTtl(String key);
    
    CompletableFuture<Long> increment(String key);
    
    CompletableFuture<Long> increment(String key, long delta);
    
    CompletableFuture<Long> decrement(String key);
    
    CompletableFuture<Long> decrement(String key, long delta);
    
    CompletableFuture<Void> setMap(String key, Map<String, Object> map);
    
    CompletableFuture<Map<String, Object>> getMap(String key);
    
    CompletableFuture<Void> setMapField(String key, String field, Object value);
    
    CompletableFuture<Object> getMapField(String key, String field);
    
    CompletableFuture<Void> addToSet(String key, String... members);
    
    CompletableFuture<List<String>> getSetMembers(String key);
    
    CompletableFuture<Boolean> isSetMember(String key, String member);
    
    CompletableFuture<Void> pushToList(String key, String... values);
    
    CompletableFuture<List<String>> getListRange(String key, long start, long end);
    
    CompletableFuture<Long> getListSize(String key);
    
    CompletableFuture<Void> clear();
    
    CompletableFuture<Long> size();
    
    CompletableFuture<List<String>> keys(String pattern);
    
    void close();
    
    boolean isConnected();
    
    String getDriverName();
    
    String getDriverVersion();
    
    class CacheConfig {
        private String host;
        private int port;
        private String password;
        private int database;
        private long defaultTtl = 3600;
        private int maxConnections = 10;
        private int connectionTimeout = 30000;
        private Map<String, Object> properties = new java.util.concurrent.ConcurrentHashMap<>();
        
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public int getDatabase() { return database; }
        public void setDatabase(int database) { this.database = database; }
        
        public long getDefaultTtl() { return defaultTtl; }
        public void setDefaultTtl(long defaultTtl) { this.defaultTtl = defaultTtl; }
        
        public int getMaxConnections() { return maxConnections; }
        public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }
        
        public int getConnectionTimeout() { return connectionTimeout; }
        public void setConnectionTimeout(int connectionTimeout) { this.connectionTimeout = connectionTimeout; }
        
        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }
}
