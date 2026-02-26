package net.ooder.sdk.drivers.storage.impl;

import net.ooder.sdk.api.driver.annotation.DriverImplementation;
import net.ooder.sdk.drivers.storage.StorageDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@DriverImplementation(value = "StorageDriver", skillId = "skill-storage-minio")
public class MinioStorageDriver implements StorageDriver {
    
    private static final Logger log = LoggerFactory.getLogger(MinioStorageDriver.class);
    
    private StorageConfig config;
    private final Map<String, byte[]> objectStore = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> metadataStore = new ConcurrentHashMap<>();
    private final AtomicBoolean connected = new AtomicBoolean(false);
    
    private Path basePath;
    
    @Override
    public void init(StorageConfig config) {
        this.config = config;
        
        String basePathStr = System.getProperty("java.io.tmpdir") + "/ooder-minio/" + 
            (config.getBucket() != null ? config.getBucket() : "default");
        this.basePath = Paths.get(basePathStr);
        
        try {
            Files.createDirectories(basePath);
        } catch (IOException e) {
            log.warn("Failed to create base directory", e);
        }
        
        connected.set(true);
        log.info("MinIO storage initialized (simulated mode) at {}", basePath);
    }
    
    @Override
    public CompletableFuture<String> upload(String path, byte[] data) {
        return upload(path, data, null);
    }
    
    @Override
    public CompletableFuture<String> upload(String path, byte[] data, Map<String, String> metadata) {
        return CompletableFuture.supplyAsync(() -> {
            if (path == null || data == null) {
                throw new IllegalArgumentException("Path and data cannot be null");
            }
            
            if (data.length > config.getMaxFileSize()) {
                throw new RuntimeException("File size exceeds maximum: " + config.getMaxFileSize());
            }
            
            objectStore.put(path, Arrays.copyOf(data, data.length));
            
            if (metadata != null) {
                metadataStore.put(path, new HashMap<>(metadata));
            }
            
            log.debug("Object uploaded: {} ({} bytes)", path, data.length);
            return path;
        });
    }
    
    @Override
    public CompletableFuture<byte[]> download(String path) {
        return CompletableFuture.supplyAsync(() -> {
            if (path == null) return null;
            
            byte[] data = objectStore.get(path);
            if (data != null) {
                log.debug("Object downloaded: {} ({} bytes)", path, data.length);
            } else {
                log.warn("Object not found: {}", path);
            }
            
            return data;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> delete(String path) {
        return CompletableFuture.supplyAsync(() -> {
            if (path == null) return false;
            
            byte[] removed = objectStore.remove(path);
            metadataStore.remove(path);
            
            if (removed != null) {
                log.debug("Object deleted: {}", path);
                return true;
            }
            return false;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> deleteBatch(List<String> paths) {
        return CompletableFuture.supplyAsync(() -> {
            boolean allSuccess = true;
            for (String path : paths) {
                if (objectStore.remove(path) == null) {
                    allSuccess = false;
                }
                metadataStore.remove(path);
            }
            log.debug("Batch deleted {} objects", paths.size());
            return allSuccess;
        });
    }
    
    @Override
    public CompletableFuture<List<StorageObject>> list(String prefix) {
        return CompletableFuture.supplyAsync(() -> {
            List<StorageObject> result = new ArrayList<>();
            
            for (Map.Entry<String, byte[]> entry : objectStore.entrySet()) {
                String path = entry.getKey();
                if (prefix == null || path.startsWith(prefix)) {
                    StorageObject obj = new StorageObject();
                    obj.setPath(path);
                    obj.setName(getFileName(path));
                    obj.setSize(entry.getValue().length);
                    obj.setContentType(detectContentType(path));
                    obj.setLastModified(System.currentTimeMillis());
                    obj.setMetadata(metadataStore.get(path));
                    result.add(obj);
                }
            }
            
            return result;
        });
    }
    
    @Override
    public CompletableFuture<StorageObject> stat(String path) {
        return CompletableFuture.supplyAsync(() -> {
            byte[] data = objectStore.get(path);
            if (data == null) return null;
            
            StorageObject obj = new StorageObject();
            obj.setPath(path);
            obj.setName(getFileName(path));
            obj.setSize(data.length);
            obj.setContentType(detectContentType(path));
            obj.setEtag(calculateEtag(data));
            obj.setLastModified(System.currentTimeMillis());
            obj.setMetadata(metadataStore.get(path));
            
            return obj;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> copy(String sourcePath, String destPath) {
        return CompletableFuture.supplyAsync(() -> {
            byte[] data = objectStore.get(sourcePath);
            if (data == null) return false;
            
            objectStore.put(destPath, Arrays.copyOf(data, data.length));
            
            Map<String, String> metadata = metadataStore.get(sourcePath);
            if (metadata != null) {
                metadataStore.put(destPath, new HashMap<>(metadata));
            }
            
            log.debug("Object copied: {} -> {}", sourcePath, destPath);
            return true;
        });
    }
    
    @Override
    public CompletableFuture<String> getSignedUrl(String path, long expirySeconds) {
        return CompletableFuture.supplyAsync(() -> {
            if (!objectStore.containsKey(path)) return null;
            
            String baseUrl = config.getEndpoint() != null ? config.getEndpoint() : "http://localhost:9000";
            String bucket = config.getBucket() != null ? config.getBucket() : "default";
            
            return String.format("%s/%s/%s?expires=%d&signature=mock", baseUrl, bucket, path, expirySeconds);
        });
    }
    
    @Override
    public CompletableFuture<String> getPublicUrl(String path) {
        return CompletableFuture.supplyAsync(() -> {
            if (!objectStore.containsKey(path)) return null;
            
            String baseUrl = config.getEndpoint() != null ? config.getEndpoint() : "http://localhost:9000";
            String bucket = config.getBucket() != null ? config.getBucket() : "default";
            
            return String.format("%s/%s/%s", baseUrl, bucket, path);
        });
    }
    
    @Override
    public CompletableFuture<Boolean> setMetadata(String path, Map<String, String> metadata) {
        return CompletableFuture.supplyAsync(() -> {
            if (!objectStore.containsKey(path)) return false;
            
            metadataStore.put(path, new HashMap<>(metadata));
            return true;
        });
    }
    
    @Override
    public CompletableFuture<Map<String, String>> getMetadata(String path) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, String> metadata = metadataStore.get(path);
            return metadata != null ? new HashMap<>(metadata) : null;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> exists(String path) {
        return CompletableFuture.supplyAsync(() -> objectStore.containsKey(path));
    }
    
    @Override
    public CompletableFuture<Long> getTotalSize() {
        return CompletableFuture.supplyAsync(() -> {
            long total = 0;
            for (byte[] data : objectStore.values()) {
                total += data.length;
            }
            return total;
        });
    }
    
    @Override
    public CompletableFuture<Long> getObjectCount() {
        return CompletableFuture.supplyAsync(() -> (long) objectStore.size());
    }
    
    @Override
    public void close() {
        objectStore.clear();
        metadataStore.clear();
        connected.set(false);
        log.info("MinIO storage closed");
    }
    
    @Override
    public boolean isConnected() {
        return connected.get();
    }
    
    @Override
    public String getDriverName() {
        return "MinIO";
    }
    
    @Override
    public String getDriverVersion() {
        return "1.0.0";
    }
    
    private String getFileName(String path) {
        int lastSlash = path.lastIndexOf('/');
        return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
    }
    
    private String detectContentType(String path) {
        String name = getFileName(path).toLowerCase();
        if (name.endsWith(".txt")) return "text/plain";
        if (name.endsWith(".json")) return "application/json";
        if (name.endsWith(".xml")) return "application/xml";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".pdf")) return "application/pdf";
        if (name.endsWith(".zip")) return "application/zip";
        return "application/octet-stream";
    }
    
    private String calculateEtag(byte[] data) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
