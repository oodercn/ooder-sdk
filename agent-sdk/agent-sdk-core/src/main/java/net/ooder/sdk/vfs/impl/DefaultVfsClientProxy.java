package net.ooder.sdk.vfs.impl;

import net.ooder.sdk.vfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultVfsClientProxy implements VfsClientProxy {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultVfsClientProxy.class);
    
    private final String clientId;
    private final VfsClientConfig config;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    
    private final Map<String, byte[]> fileStore = new ConcurrentHashMap<>();
    private final Map<String, List<VfsVersion>> versionStore = new ConcurrentHashMap<>();
    private final Map<String, VfsFileInfo> fileInfoStore = new ConcurrentHashMap<>();
    
    private Path basePath;
    
    public DefaultVfsClientProxy() {
        this.config = new VfsClientConfig();
        this.clientId = "vfs-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    public DefaultVfsClientProxy(VfsClientConfig config) {
        this.config = config != null ? config : new VfsClientConfig();
        this.clientId = this.config.getClientId() != null ? 
            this.config.getClientId() : "vfs-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @Override
    public void init(VfsClientConfig config) {
        log.info("Initializing VfsClientProxy: {}", clientId);
        
        if (config != null) {
            this.config.setEndpoint(config.getEndpoint());
            this.config.setBucket(config.getBucket());
            this.config.setAccessKey(config.getAccessKey());
            this.config.setSecretKey(config.getSecretKey());
            this.config.setMaxFileSize(config.getMaxFileSize());
            this.config.setVersioningEnabled(config.isVersioningEnabled());
        }
        
        String basePathStr = System.getProperty("java.io.tmpdir") + "/ooder-vfs/" + clientId;
        this.basePath = Paths.get(basePathStr);
        
        try {
            Files.createDirectories(basePath);
        } catch (IOException e) {
            log.warn("Failed to create base directory", e);
        }
        
        connected.set(true);
        
        log.info("VfsClientProxy initialized: {} at {}", clientId, basePath);
    }
    
    @Override
    public VfsResult upload(String path, byte[] data) {
        if (!connected.get()) {
            return VfsResult.failure("NOT_CONNECTED", "Client not connected");
        }
        
        if (path == null || data == null) {
            return VfsResult.failure("INVALID_INPUT", "Path and data cannot be null");
        }
        
        if (data.length > config.getMaxFileSize()) {
            return VfsResult.failure("FILE_TOO_LARGE", "File size exceeds maximum: " + config.getMaxFileSize());
        }
        
        try {
            fileStore.put(path, data);
            
            VfsFileInfo info = new VfsFileInfo();
            info.setPath(path);
            info.setName(getFileName(path));
            info.setDirectory(false);
            info.setSize(data.length);
            info.setContentType(detectContentType(path));
            info.setChecksum(calculateChecksum(data));
            info.setCreatedTime(System.currentTimeMillis());
            info.setModifiedTime(System.currentTimeMillis());
            
            fileInfoStore.put(path, info);
            
            if (config.isVersioningEnabled()) {
                addVersion(path, data);
            }
            
            log.debug("File uploaded: {} ({} bytes)", path, data.length);
            
            return VfsResult.success(path);
            
        } catch (Exception e) {
            log.error("Upload failed: {}", path, e);
            return VfsResult.failure("UPLOAD_ERROR", e.getMessage());
        }
    }
    
    @Override
    public byte[] download(String path) {
        if (!connected.get() || path == null) {
            return null;
        }
        
        byte[] data = fileStore.get(path);
        if (data != null) {
            log.debug("File downloaded: {} ({} bytes)", path, data.length);
        } else {
            log.warn("File not found: {}", path);
        }
        
        return data;
    }
    
    @Override
    public boolean delete(String path) {
        if (!connected.get() || path == null) {
            return false;
        }
        
        byte[] removed = fileStore.remove(path);
        fileInfoStore.remove(path);
        
        if (removed != null) {
            log.debug("File deleted: {}", path);
            return true;
        }
        
        return false;
    }
    
    @Override
    public List<VfsFileInfo> list(String path) {
        if (!connected.get() || path == null) {
            return Collections.emptyList();
        }
        
        List<VfsFileInfo> result = new ArrayList<>();
        
        for (Map.Entry<String, VfsFileInfo> entry : fileInfoStore.entrySet()) {
            String filePath = entry.getKey();
            if (filePath.startsWith(path)) {
                result.add(entry.getValue());
            }
        }
        
        return result;
    }
    
    @Override
    public VfsFileInfo stat(String path) {
        if (!connected.get() || path == null) {
            return null;
        }
        return fileInfoStore.get(path);
    }
    
    @Override
    public boolean mkdir(String path) {
        if (!connected.get() || path == null) {
            return false;
        }
        
        VfsFileInfo info = new VfsFileInfo();
        info.setPath(path);
        info.setName(getFileName(path));
        info.setDirectory(true);
        info.setSize(0);
        info.setCreatedTime(System.currentTimeMillis());
        info.setModifiedTime(System.currentTimeMillis());
        
        fileInfoStore.put(path, info);
        
        log.debug("Directory created: {}", path);
        return true;
    }
    
    @Override
    public boolean copy(String srcPath, String destPath) {
        if (!connected.get() || srcPath == null || destPath == null) {
            return false;
        }
        
        byte[] data = fileStore.get(srcPath);
        if (data == null) {
            return false;
        }
        
        fileStore.put(destPath, Arrays.copyOf(data, data.length));
        
        VfsFileInfo srcInfo = fileInfoStore.get(srcPath);
        if (srcInfo != null) {
            VfsFileInfo destInfo = new VfsFileInfo();
            destInfo.setPath(destPath);
            destInfo.setName(getFileName(destPath));
            destInfo.setDirectory(false);
            destInfo.setSize(srcInfo.getSize());
            destInfo.setContentType(srcInfo.getContentType());
            destInfo.setChecksum(srcInfo.getChecksum());
            destInfo.setCreatedTime(System.currentTimeMillis());
            destInfo.setModifiedTime(System.currentTimeMillis());
            
            fileInfoStore.put(destPath, destInfo);
        }
        
        log.debug("File copied: {} -> {}", srcPath, destPath);
        return true;
    }
    
    @Override
    public boolean move(String srcPath, String destPath) {
        if (copy(srcPath, destPath)) {
            return delete(srcPath);
        }
        return false;
    }
    
    @Override
    public List<VfsVersion> getVersions(String path) {
        if (!connected.get() || path == null) {
            return Collections.emptyList();
        }
        
        List<VfsVersion> versions = versionStore.get(path);
        return versions != null ? new ArrayList<>(versions) : Collections.emptyList();
    }
    
    @Override
    public void shutdown() {
        log.info("Shutting down VfsClientProxy: {}", clientId);
        connected.set(false);
        fileStore.clear();
        fileInfoStore.clear();
        versionStore.clear();
        log.info("VfsClientProxy shutdown complete: {}", clientId);
    }
    
    @Override
    public boolean isConnected() {
        return connected.get();
    }
    
    @Override
    public String getClientId() {
        return clientId;
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
        return "application/octet-stream";
    }
    
    private String calculateChecksum(byte[] data) {
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
    
    private void addVersion(String path, byte[] data) {
        List<VfsVersion> versions = versionStore.computeIfAbsent(path, k -> new CopyOnWriteArrayList<>());
        
        for (VfsVersion v : versions) {
            v.setLatest(false);
        }
        
        VfsVersion version = new VfsVersion();
        version.setVersionId("v" + System.currentTimeMillis());
        version.setPath(path);
        version.setSize(data.length);
        version.setChecksum(calculateChecksum(data));
        version.setCreatedTime(System.currentTimeMillis());
        version.setLatest(true);
        version.setDeleted(false);
        
        versions.add(version);
    }
}
