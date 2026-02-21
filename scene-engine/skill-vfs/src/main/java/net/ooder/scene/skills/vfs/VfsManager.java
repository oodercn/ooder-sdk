package net.ooder.scene.skills.vfs;

import java.io.InputStream;
import java.util.List;

/**
 * VfsManager VFS管理器接口
 * 
 * <p>定义虚拟文件系统的核心操作，与 ooder-vfs 的 VfsManager 接口兼容。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface VfsManager {
    
    void init(Object configCode);
    
    FileInfo getFileInfoByID(String fileId) throws VfsException;
    
    FileInfo createFile(String folderId, String name) throws VfsException;
    
    boolean deleteFile(String fileId) throws VfsException;
    
    List<FileInfo> listFiles(String folderId) throws VfsException;
    
    Folder getFolderByID(String folderId) throws VfsException;
    
    Folder createFolder(String parentId, String name) throws VfsException;
    
    boolean deleteFolder(String folderId) throws VfsException;
    
    List<Folder> listFolders(String parentId) throws VfsException;
    
    FileObject getFileObject(String fileId) throws VfsException;
    
    FileInfo uploadFile(String folderId, String name, InputStream content) throws VfsException;
    
    InputStream downloadFile(String fileId) throws VfsException;
    
    boolean isSupportVersioning();
    
    boolean isSupportSharing();
    
    boolean isSupportStreaming();
    
    boolean isSupportAcl();
    
    boolean isSupportMetadata();
    
    boolean isSupportCompression();
    
    void reloadAll();
}
