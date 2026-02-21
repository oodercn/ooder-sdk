package net.ooder.scene.drivers.vfs;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface VfsSkill {

    String getSkillId();
    
    String getSkillName();
    
    String getSkillVersion();
    
    List<String> getCapabilities();
    
    void initialize(VfsCapabilities capabilities);
    
    void initialize(VfsCapabilities capabilities, VfsManager remoteVfsManager);
    
    FileInfo getFileInfo(String fileId);
    
    FileInfo createFile(String folderId, String name, String personId);
    
    boolean deleteFile(String fileId);
    
    List<FileInfo> listFiles(String folderId);
    
    Folder getFolder(String folderId);
    
    Folder createFolder(String parentId, String name, String personId);
    
    boolean deleteFolder(String folderId);
    
    List<Folder> listFolders(String parentId);
    
    InputStream downloadFile(String fileId);
    
    FileInfo uploadFile(String folderId, String name, InputStream content, String personId);
    
    FileInfo copyFile(String fileId, String targetFolderId);
    
    FileInfo moveFile(String fileId, String targetFolderId);
    
    boolean renameFile(String fileId, String newName);
    
    FileInfo shareFile(String fileId, long expireTime);
    
    List<FileVersion> getFileVersions(String fileId);
    
    FileVersion createVersion(String fileId, String description, String personId);
    
    List<FileInfo> searchFiles(String keyword, String folderId);
    
    VfsCapabilities getVfsCapabilities();
    
    boolean isSupport(String capability);
    
    Object invoke(String capability, Map<String, Object> params);
}
