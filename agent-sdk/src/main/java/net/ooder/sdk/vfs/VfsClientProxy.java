package net.ooder.sdk.vfs;

import java.util.List;

public interface VfsClientProxy {
    
    void init(VfsClientConfig config);
    
    VfsResult upload(String path, byte[] data);
    
    byte[] download(String path);
    
    boolean delete(String path);
    
    List<VfsFileInfo> list(String path);
    
    VfsFileInfo stat(String path);
    
    boolean mkdir(String path);
    
    boolean copy(String srcPath, String destPath);
    
    boolean move(String srcPath, String destPath);
    
    List<VfsVersion> getVersions(String path);
    
    void shutdown();
    
    boolean isConnected();
    
    String getClientId();
}
