package net.ooder.scene.skills.vfs;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VfsSkillImpl VFS技能实现
 * 
 * <p>作为中间服务层，具有双重职责：</p>
 * <ul>
 *   <li>方向1: 接受应用层远程调用，提供文件系统操作</li>
 *   <li>方向2: 通过 agentSDK 对接多数据源 skills</li>
 * </ul>
 * 
 * <p>能力路由逻辑：</p>
 * <ul>
 *   <li>读取 scene 配置中的 vfsCapabilities</li>
 *   <li>判断 isSupport* 方法返回值</li>
 *   <li>路由到外部 skills 或本地降级实现</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public class VfsSkillImpl implements VfsSkill {

    private static final Logger logger = LoggerFactory.getLogger(VfsSkillImpl.class);

    private static final String SKILL_ID = "skill-vfs";
    private static final String SKILL_NAME = "Virtual File System Skill";
    private static final String SKILL_VERSION = "0.7.3";

    private VfsCapabilities capabilities;
    private VfsManager remoteVfsManager;
    private LocalVfsDataSource localDataSource;
    
    private boolean initialized = false;

    public VfsSkillImpl() {
        this.capabilities = VfsCapabilities.forLocal();
        this.localDataSource = new LocalVfsDataSource();
    }

    public void initialize(VfsCapabilities capabilities) {
        this.capabilities = capabilities;
        this.localDataSource = new LocalVfsDataSource();
        this.localDataSource.initialize();
        this.initialized = true;
        
        logger.info("VfsSkill initialized with provider: {}", capabilities.getProviderType());
        if (capabilities.requiresFallback()) {
            logger.info("Fallback required for: {}", capabilities.getUnsupportedCapabilities());
        }
    }

    public void initialize(VfsCapabilities capabilities, VfsManager remoteVfsManager) {
        this.capabilities = capabilities;
        this.remoteVfsManager = remoteVfsManager;
        this.localDataSource = new LocalVfsDataSource();
        this.localDataSource.initialize();
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
        caps.add("file.upload");
        caps.add("file.download");
        caps.add("file.version");
        caps.add("file.share");
        caps.add("file.search");
        caps.add("folder.create");
        caps.add("folder.delete");
        caps.add("folder.list");
        return caps;
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
            case "file.rename": return capabilities.isSupportFileRename();
            case "file.upload": return capabilities.isSupportStreamUpload();
            case "file.download": return capabilities.isSupportStreamDownload();
            case "file.version": return capabilities.isSupportFileVersion();
            case "file.share": return capabilities.isSupportFileShare();
            case "file.search": return capabilities.isSupportFileSearch();
            case "folder.create": return capabilities.isSupportFolderCreate();
            case "folder.delete": return capabilities.isSupportFolderDelete();
            case "folder.list": return capabilities.isSupportFolderList();
            default: return false;
        }
    }

    @Override
    public FileInfo getFileInfo(String fileId) {
        FileInfo fileInfo = null;
        
        if (capabilities.isSupportFileRead() && remoteVfsManager != null) {
            try {
                fileInfo = remoteVfsManager.getFileInfoByID(fileId);
            } catch (Exception e) {
                logger.warn("Remote getFileInfo failed, fallback to local: {}", e.getMessage());
            }
        }
        
        if (fileInfo == null) {
            fileInfo = localDataSource.getFileInfoByID(fileId);
        }
        
        return fileInfo;
    }

    @Override
    public FileInfo createFile(String folderId, String name, String personId) {
        if (capabilities.isSupportFileWrite() && remoteVfsManager != null) {
            try {
                return remoteVfsManager.createFile(folderId, name);
            } catch (Exception e) {
                logger.warn("Remote createFile failed, fallback to local: {}", e.getMessage());
            }
        }
        return localDataSource.createFile(folderId, name, personId);
    }

    @Override
    public boolean deleteFile(String fileId) {
        if (capabilities.isSupportFileDelete() && remoteVfsManager != null) {
            try {
                return remoteVfsManager.deleteFile(fileId);
            } catch (Exception e) {
                logger.warn("Remote deleteFile failed, fallback to local: {}", e.getMessage());
            }
        }
        return localDataSource.deleteFile(fileId);
    }

    @Override
    public List<FileInfo> listFiles(String folderId) {
        if (capabilities.isSupportFolderList() && remoteVfsManager != null) {
            try {
                return remoteVfsManager.listFiles(folderId);
            } catch (Exception e) {
                logger.warn("Remote listFiles failed, fallback to local: {}", e.getMessage());
            }
        }
        return localDataSource.listFiles(folderId);
    }

    @Override
    public Folder getFolder(String folderId) {
        Folder folder = null;
        
        if (capabilities.isSupportFolderList() && remoteVfsManager != null) {
            try {
                folder = remoteVfsManager.getFolderByID(folderId);
            } catch (Exception e) {
                logger.warn("Remote getFolder failed, fallback to local: {}", e.getMessage());
            }
        }
        
        if (folder == null) {
            folder = localDataSource.getFolderByID(folderId);
        }
        
        return folder;
    }

    @Override
    public Folder createFolder(String parentId, String name, String personId) {
        if (capabilities.isSupportFolderCreate() && remoteVfsManager != null) {
            try {
                return remoteVfsManager.createFolder(parentId, name);
            } catch (Exception e) {
                logger.warn("Remote createFolder failed, fallback to local: {}", e.getMessage());
            }
        }
        return localDataSource.createFolder(parentId, name, personId);
    }

    @Override
    public boolean deleteFolder(String folderId) {
        if (capabilities.isSupportFolderDelete() && remoteVfsManager != null) {
            try {
                return remoteVfsManager.deleteFolder(folderId);
            } catch (Exception e) {
                logger.warn("Remote deleteFolder failed, fallback to local: {}", e.getMessage());
            }
        }
        return localDataSource.deleteFolder(folderId);
    }

    @Override
    public List<Folder> listFolders(String parentId) {
        if (capabilities.isSupportFolderList() && remoteVfsManager != null) {
            try {
                return remoteVfsManager.listFolders(parentId);
            } catch (Exception e) {
                logger.warn("Remote listFolders failed, fallback to local: {}", e.getMessage());
            }
        }
        return localDataSource.listFolders(parentId);
    }

    @Override
    public InputStream downloadFile(String fileId) {
        if (capabilities.isSupportStreamDownload() && remoteVfsManager != null) {
            try {
                return remoteVfsManager.downloadFile(fileId);
            } catch (Exception e) {
                logger.warn("Remote downloadFile failed, fallback to local: {}", e.getMessage());
            }
        }
        return localDataSource.downloadFile(fileId);
    }

    @Override
    public FileInfo uploadFile(String folderId, String name, InputStream content, String personId) {
        if (capabilities.isSupportStreamUpload() && remoteVfsManager != null) {
            try {
                return remoteVfsManager.uploadFile(folderId, name, content);
            } catch (Exception e) {
                logger.warn("Remote uploadFile failed, fallback to local: {}", e.getMessage());
            }
        }
        return localDataSource.uploadFile(folderId, name, content, personId);
    }

    @Override
    public FileInfo copyFile(String fileId, String targetFolderId) {
        logger.warn("copyFile not fully implemented for fileId: {}", fileId);
        return getFileInfo(fileId);
    }

    @Override
    public FileInfo moveFile(String fileId, String targetFolderId) {
        logger.warn("moveFile not fully implemented for fileId: {}", fileId);
        return getFileInfo(fileId);
    }

    @Override
    public boolean renameFile(String fileId, String newName) {
        logger.warn("renameFile not fully implemented for fileId: {}", fileId);
        return false;
    }

    @Override
    public FileInfo shareFile(String fileId, long expireTime) {
        if (!capabilities.isSupportFileShare()) {
            logger.warn("File share not supported by provider: {}", capabilities.getProviderType());
            return null;
        }
        logger.warn("shareFile not fully implemented for fileId: {}", fileId);
        return getFileInfo(fileId);
    }

    @Override
    public List<FileVersion> getFileVersions(String fileId) {
        if (capabilities.isSupportFileVersion() && remoteVfsManager != null) {
            logger.info("Getting file versions from remote for fileId: {}", fileId);
        }
        return localDataSource.getFileVersions(fileId);
    }

    @Override
    public FileVersion createVersion(String fileId, String description, String personId) {
        if (!capabilities.isSupportFileVersion()) {
            logger.info("Using local fallback for file versioning");
        }
        return localDataSource.createVersion(fileId, description, personId);
    }

    @Override
    public List<FileInfo> searchFiles(String keyword, String folderId) {
        if (capabilities.isSupportFileSearch() && remoteVfsManager != null) {
            logger.info("Searching files from remote with keyword: {}", keyword);
        }
        return localDataSource.searchFiles(keyword, folderId);
    }

    @Override
    public Object invoke(String capability, Map<String, Object> params) {
        logger.info("Invoking capability: {}", capability);
        
        try {
            switch (capability) {
                case "file.read":
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
                    
                case "file.upload":
                    return uploadFile(
                        (String) params.get("folderId"),
                        (String) params.get("name"),
                        (InputStream) params.get("content"),
                        (String) params.get("personId")
                    );
                    
                case "file.download":
                    return downloadFile((String) params.get("fileId"));
                    
                case "file.copy":
                    return copyFile(
                        (String) params.get("fileId"),
                        (String) params.get("targetFolderId")
                    );
                    
                case "file.move":
                    return moveFile(
                        (String) params.get("fileId"),
                        (String) params.get("targetFolderId")
                    );
                    
                case "file.rename":
                    return renameFile(
                        (String) params.get("fileId"),
                        (String) params.get("newName")
                    );
                    
                case "file.share":
                    return shareFile(
                        (String) params.get("fileId"),
                        params.get("expireTime") != null ? ((Number) params.get("expireTime")).longValue() : 0
                    );
                    
                case "file.version.list":
                    return getFileVersions((String) params.get("fileId"));
                    
                case "file.version.create":
                    return createVersion(
                        (String) params.get("fileId"),
                        (String) params.get("description"),
                        (String) params.get("personId")
                    );
                    
                case "file.search":
                    return searchFiles(
                        (String) params.get("keyword"),
                        (String) params.get("folderId")
                    );
                    
                case "folder.info":
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
