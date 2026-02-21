package net.ooder.scene.drivers.vfs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VfsSkillImpl implements VfsSkill {

    private static final Logger logger = LoggerFactory.getLogger(VfsSkillImpl.class);

    private static final String SKILL_ID = "skill-vfs";
    private static final String SKILL_NAME = "Virtual File System Skill";
    private static final String SKILL_VERSION = "0.7.3";

    private VfsCapabilities capabilities;
    private VfsManager remoteVfsManager;
    private VfsFallback fallback;
    
    private boolean initialized = false;

    public VfsSkillImpl() {
        this.capabilities = VfsCapabilities.forLocal();
        this.fallback = new VfsFallback();
    }

    @Override
    public void initialize(VfsCapabilities capabilities) {
        this.capabilities = capabilities;
        this.fallback = new VfsFallback();
        this.fallback.initialize();
        this.initialized = true;
        
        logger.info("VfsSkill initialized with provider: {}", capabilities.getProviderType());
        if (capabilities.requiresFallback()) {
            logger.info("Fallback required for: {}", capabilities.getUnsupportedCapabilities());
        }
    }

    @Override
    public void initialize(VfsCapabilities capabilities, VfsManager remoteVfsManager) {
        this.capabilities = capabilities;
        this.remoteVfsManager = remoteVfsManager;
        this.fallback = new VfsFallback();
        this.fallback.initialize();
        this.initialized = true;
        
        logger.info("VfsSkill initialized with remote provider: {}", capabilities.getProviderType());
    }

    @Override
    public String getSkillId() {
        return SKILL_ID;
    }

    @Override
    public String getSkillName() {
        return SKILL_NAME;
    }

    @Override
    public String getSkillVersion() {
        return SKILL_VERSION;
    }

    @Override
    public List<String> getCapabilities() {
        List<String> caps = new ArrayList<String>();
        caps.add("file.read");
        caps.add("file.write");
        caps.add("file.delete");
        caps.add("file.copy");
        caps.add("file.move");
        caps.add("file.rename");
        caps.add("folder.create");
        caps.add("folder.delete");
        caps.add("folder.list");
        caps.add("stream.upload");
        caps.add("stream.download");
        return caps;
    }

    @Override
    public FileInfo getFileInfo(String fileId) {
        try {
            if (capabilities.isSupportFileRead() && remoteVfsManager != null) {
                return remoteVfsManager.getFileInfoByID(fileId);
            }
            return fallback.getFileInfoByID(fileId);
        } catch (VfsException e) {
            logger.warn("Failed to get file info: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public FileInfo createFile(String folderId, String name, String personId) {
        try {
            if (capabilities.isSupportFileWrite() && remoteVfsManager != null) {
                return remoteVfsManager.createFile(folderId, name);
            }
            return fallback.createFile(folderId, name);
        } catch (VfsException e) {
            logger.warn("Failed to create file: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean deleteFile(String fileId) {
        try {
            if (capabilities.isSupportFileDelete() && remoteVfsManager != null) {
                return remoteVfsManager.deleteFile(fileId);
            }
            return fallback.deleteFile(fileId);
        } catch (VfsException e) {
            logger.warn("Failed to delete file: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<FileInfo> listFiles(String folderId) {
        try {
            if (capabilities.isSupportFolderList() && remoteVfsManager != null) {
                return remoteVfsManager.listFiles(folderId);
            }
            return fallback.listFiles(folderId);
        } catch (VfsException e) {
            logger.warn("Failed to list files: {}", e.getMessage());
            return new ArrayList<FileInfo>();
        }
    }

    @Override
    public Folder getFolder(String folderId) {
        try {
            if (capabilities.isSupportFolderList() && remoteVfsManager != null) {
                return remoteVfsManager.getFolderByID(folderId);
            }
            return fallback.getFolderByID(folderId);
        } catch (VfsException e) {
            logger.warn("Failed to get folder: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Folder createFolder(String parentId, String name, String personId) {
        try {
            if (capabilities.isSupportFolderCreate() && remoteVfsManager != null) {
                return remoteVfsManager.createFolder(parentId, name);
            }
            return fallback.createFolder(parentId, name);
        } catch (VfsException e) {
            logger.warn("Failed to create folder: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean deleteFolder(String folderId) {
        try {
            if (capabilities.isSupportFolderDelete() && remoteVfsManager != null) {
                return remoteVfsManager.deleteFolder(folderId);
            }
            return fallback.deleteFolder(folderId);
        } catch (VfsException e) {
            logger.warn("Failed to delete folder: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<Folder> listFolders(String parentId) {
        try {
            if (capabilities.isSupportFolderList() && remoteVfsManager != null) {
                return remoteVfsManager.listFolders(parentId);
            }
            return fallback.listFolders(parentId);
        } catch (VfsException e) {
            logger.warn("Failed to list folders: {}", e.getMessage());
            return new ArrayList<Folder>();
        }
    }

    @Override
    public InputStream downloadFile(String fileId) {
        try {
            if (capabilities.isSupportStreamDownload() && remoteVfsManager != null) {
                return remoteVfsManager.downloadFile(fileId);
            }
            return fallback.downloadFile(fileId);
        } catch (VfsException e) {
            logger.warn("Failed to download file: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public FileInfo uploadFile(String folderId, String name, InputStream content, String personId) {
        try {
            if (capabilities.isSupportStreamUpload() && remoteVfsManager != null) {
                return remoteVfsManager.uploadFile(folderId, name, content);
            }
            return fallback.uploadFile(folderId, name, content);
        } catch (VfsException e) {
            logger.warn("Failed to upload file: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public FileInfo copyFile(String fileId, String targetFolderId) {
        logger.warn("Copy file not fully implemented");
        return null;
    }

    @Override
    public FileInfo moveFile(String fileId, String targetFolderId) {
        logger.warn("Move file not fully implemented");
        return null;
    }

    @Override
    public boolean renameFile(String fileId, String newName) {
        logger.warn("Rename file not fully implemented");
        return false;
    }

    @Override
    public FileInfo shareFile(String fileId, long expireTime) {
        if (!capabilities.isSupportFileShare()) {
            logger.warn("File sharing not supported");
            return null;
        }
        logger.warn("Share file not fully implemented");
        return null;
    }

    @Override
    public List<FileVersion> getFileVersions(String fileId) {
        if (!capabilities.isSupportFileVersion()) {
            logger.warn("File versioning not supported");
            return new ArrayList<FileVersion>();
        }
        logger.warn("Get file versions not fully implemented");
        return new ArrayList<FileVersion>();
    }

    @Override
    public FileVersion createVersion(String fileId, String description, String personId) {
        if (!capabilities.isSupportFileVersion()) {
            logger.warn("File versioning not supported");
            return null;
        }
        logger.warn("Create version not fully implemented");
        return null;
    }

    @Override
    public List<FileInfo> searchFiles(String keyword, String folderId) {
        if (!capabilities.isSupportFileSearch()) {
            logger.warn("File search not supported");
            return new ArrayList<FileInfo>();
        }
        logger.warn("Search files not fully implemented");
        return new ArrayList<FileInfo>();
    }

    @Override
    public VfsCapabilities getVfsCapabilities() {
        return capabilities;
    }

    @Override
    public boolean isSupport(String capability) {
        switch (capability) {
            case "file.read": return capabilities.isSupportFileRead();
            case "file.write": return capabilities.isSupportFileWrite();
            case "file.delete": return capabilities.isSupportFileDelete();
            case "file.copy": return capabilities.isSupportFileCopy();
            case "file.move": return capabilities.isSupportFileMove();
            case "file.version": return capabilities.isSupportFileVersion();
            case "file.share": return capabilities.isSupportFileShare();
            case "folder.create": return capabilities.isSupportFolderCreate();
            case "folder.delete": return capabilities.isSupportFolderDelete();
            case "stream.upload": return capabilities.isSupportStreamUpload();
            case "stream.download": return capabilities.isSupportStreamDownload();
            default: return false;
        }
    }

    @Override
    public Object invoke(String capability, Map<String, Object> params) {
        logger.info("Invoking capability: {}", capability);
        
        try {
            switch (capability) {
                case "file.info":
                    return getFileInfo((String) params.get("fileId"));
                    
                case "file.create":
                    return createFile(
                        (String) params.get("folderId"),
                        (String) params.get("name"),
                        (String) params.get("personId")
                    );
                    
                case "file.delete":
                    return deleteFile((String) params.get("fileId"));
                    
                case "file.list":
                    return listFiles((String) params.get("folderId"));
                    
                case "folder.get":
                    return getFolder((String) params.get("folderId"));
                    
                case "folder.create":
                    return createFolder(
                        (String) params.get("parentId"),
                        (String) params.get("name"),
                        (String) params.get("personId")
                    );
                    
                case "folder.delete":
                    return deleteFolder((String) params.get("folderId"));
                    
                case "folder.list":
                    return listFolders((String) params.get("parentId"));
                    
                case "file.upload":
                    return uploadFile(
                        (String) params.get("folderId"),
                        (String) params.get("name"),
                        (InputStream) params.get("content"),
                        (String) params.get("personId")
                    );
                    
                case "file.download":
                    return downloadFile((String) params.get("fileId"));
                    
                default:
                    logger.warn("Unknown capability: {}", capability);
                    return null;
            }
        } catch (Exception e) {
            logger.error("Failed to invoke capability: {}", capability, e);
            return null;
        }
    }
}
