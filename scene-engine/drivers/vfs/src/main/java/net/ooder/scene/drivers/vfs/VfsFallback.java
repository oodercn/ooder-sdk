package net.ooder.scene.drivers.vfs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class VfsFallback implements VfsManager {
    
    private static final Logger logger = LoggerFactory.getLogger(VfsFallback.class);
    
    private String storagePath;
    private Path rootPath;
    private Map<String, FallbackFolder> folderMap = new HashMap<String, FallbackFolder>();
    private Map<String, FallbackFileInfo> fileMap = new HashMap<String, FallbackFileInfo>();
    
    public VfsFallback() {
        this.storagePath = "data/vfs";
    }
    
    public VfsFallback(String storagePath) {
        this.storagePath = storagePath;
    }
    
    public void initialize() {
        logger.info("Initializing VFS Fallback with storage: {}", storagePath);
        
        rootPath = Paths.get(storagePath);
        try {
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            logger.error("Failed to create storage directory", e);
        }
        
        FallbackFolder root = new FallbackFolder();
        root.setID("root");
        root.setName("Root");
        root.setPath("/");
        root.setParentId(null);
        folderMap.put(root.getID(), root);
        
        logger.info("VFS Fallback initialized with root folder");
    }
    
    public void shutdown() {
        logger.info("VFS Fallback shutdown completed");
    }
    
    @Override
    public void init(Object configCode) {
        initialize();
    }
    
    @Override
    public FileInfo getFileInfoByID(String fileId) throws VfsException {
        return fileMap.get(fileId);
    }
    
    @Override
    public FileInfo createFile(String folderId, String name) throws VfsException {
        FallbackFolder folder = folderMap.get(folderId);
        if (folder == null) {
            throw new VfsException("Folder not found: " + folderId);
        }
        
        FallbackFileInfo file = new FallbackFileInfo();
        file.setID("file-" + System.currentTimeMillis());
        file.setName(name);
        file.setFolderId(folderId);
        file.setCreateTime(System.currentTimeMillis());
        
        fileMap.put(file.getID(), file);
        
        return file;
    }
    
    @Override
    public boolean deleteFile(String fileId) throws VfsException {
        FallbackFileInfo file = fileMap.remove(fileId);
        if (file == null) {
            return false;
        }
        
        try {
            Path filePath = rootPath.resolve(file.getPath());
            Files.deleteIfExists(filePath);
            return true;
        } catch (IOException e) {
            throw new VfsException("Failed to delete file", e);
        }
    }
    
    @Override
    public List<FileInfo> listFiles(String folderId) throws VfsException {
        List<FileInfo> result = new ArrayList<FileInfo>();
        for (FallbackFileInfo file : fileMap.values()) {
            if (folderId.equals(file.getFolderId())) {
                result.add(file);
            }
        }
        return result;
    }
    
    @Override
    public Folder getFolderByID(String folderId) throws VfsException {
        return folderMap.get(folderId);
    }
    
    @Override
    public Folder createFolder(String parentId, String name) throws VfsException {
        FallbackFolder parent = folderMap.get(parentId);
        if (parent == null) {
            throw new VfsException("Parent folder not found: " + parentId);
        }
        
        FallbackFolder folder = new FallbackFolder();
        folder.setID("folder-" + System.currentTimeMillis());
        folder.setName(name);
        folder.setParentId(parentId);
        folder.setPath(parent.getPath() + "/" + name);
        folder.setCreateTime(System.currentTimeMillis());
        
        folderMap.put(folder.getID(), folder);
        
        try {
            Path folderPath = rootPath.resolve(folder.getPath());
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            throw new VfsException("Failed to create folder", e);
        }
        
        return folder;
    }
    
    @Override
    public boolean deleteFolder(String folderId) throws VfsException {
        FallbackFolder folder = folderMap.remove(folderId);
        if (folder == null) {
            return false;
        }
        
        try {
            Path folderPath = rootPath.resolve(folder.getPath());
            Files.walkFileTree(folderPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            return true;
        } catch (IOException e) {
            throw new VfsException("Failed to delete folder", e);
        }
    }
    
    @Override
    public List<Folder> listFolders(String parentId) throws VfsException {
        List<Folder> result = new ArrayList<Folder>();
        for (FallbackFolder folder : folderMap.values()) {
            if (parentId != null && parentId.equals(folder.getParentId())) {
                result.add(folder);
            } else if (parentId == null && folder.getParentId() == null) {
                result.add(folder);
            }
        }
        return result;
    }
    
    @Override
    public FileObject getFileObject(String fileId) throws VfsException {
        FallbackFileInfo file = fileMap.get(fileId);
        if (file == null) {
            return null;
        }
        
        return new FallbackFileObject(file, rootPath);
    }
    
    @Override
    public FileInfo uploadFile(String folderId, String name, InputStream content) throws VfsException {
        FallbackFolder folder = folderMap.get(folderId);
        if (folder == null) {
            throw new VfsException("Folder not found: " + folderId);
        }
        
        FallbackFileInfo file = new FallbackFileInfo();
        file.setID("file-" + System.currentTimeMillis());
        file.setName(name);
        file.setFolderId(folderId);
        file.setPath(folder.getPath() + "/" + name);
        file.setCreateTime(System.currentTimeMillis());
        
        try {
            Path filePath = rootPath.resolve(file.getPath());
            Files.createDirectories(filePath.getParent());
            Files.copy(content, filePath, StandardCopyOption.REPLACE_EXISTING);
            file.setLength(Files.size(filePath));
        } catch (IOException e) {
            throw new VfsException("Failed to upload file", e);
        }
        
        fileMap.put(file.getID(), file);
        return file;
    }
    
    @Override
    public InputStream downloadFile(String fileId) throws VfsException {
        FallbackFileInfo file = fileMap.get(fileId);
        if (file == null) {
            throw new VfsException("File not found: " + fileId);
        }
        
        try {
            Path filePath = rootPath.resolve(file.getPath());
            return new FileInputStream(filePath.toFile());
        } catch (IOException e) {
            throw new VfsException("Failed to download file", e);
        }
    }
    
    @Override
    public boolean isSupportVersioning() { return false; }
    
    @Override
    public boolean isSupportSharing() { return false; }
    
    @Override
    public boolean isSupportStreaming() { return true; }
    
    @Override
    public boolean isSupportAcl() { return false; }
    
    @Override
    public boolean isSupportMetadata() { return false; }
    
    @Override
    public boolean isSupportCompression() { return false; }
    
    @Override
    public void reloadAll() {
        folderMap.clear();
        fileMap.clear();
        initialize();
    }
    
    static class FallbackFolder implements Folder {
        private String ID;
        private String name;
        private String path;
        private String parentId;
        private String description;
        private String personId;
        private Long createTime;
        private FolderType folderType = FolderType.NORMAL;
        private FolderState state = FolderState.NORMAL;
        
        public String getID() { return ID; }
        public void setID(String ID) { this.ID = ID; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public String getParentId() { return parentId; }
        public void setParentId(String parentId) { this.parentId = parentId; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getPersonId() { return personId; }
        public void setPersonId(String personId) { this.personId = personId; }
        public Long getCreateTime() { return createTime; }
        public void setCreateTime(Long createTime) { this.createTime = createTime; }
        public FolderType getFolderType() { return folderType; }
        public FolderState getState() { return state; }
        public List<FileInfo> getFileList() { return new ArrayList<FileInfo>(); }
        public List<Folder> getChildrenList() { return new ArrayList<Folder>(); }
        public Set<String> getFileIdList() { return new HashSet<String>(); }
        public Set<String> getChildrenIdList() { return new HashSet<String>(); }
        public Folder createChildFolder(String name, String personId) { return null; }
        public FileInfo createFile(String name, String personId) { return null; }
    }
    
    static class FallbackFileInfo implements FileInfo {
        private String ID;
        private String name;
        private String path;
        private Long length;
        private String hash;
        private Long createTime;
        private Long updateTime;
        private String folderId;
        private String personId;
        private String description;
        
        public String getID() { return ID; }
        public void setID(String ID) { this.ID = ID; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public Long getLength() { return length; }
        public void setLength(Long length) { this.length = length; }
        public String getHash() { return hash; }
        public void setHash(String hash) { this.hash = hash; }
        public Long getCreateTime() { return createTime; }
        public void setCreateTime(Long createTime) { this.createTime = createTime; }
        public Long getUpdateTime() { return updateTime; }
        public void setUpdateTime(Long updateTime) { this.updateTime = updateTime; }
        public String getFolderId() { return folderId; }
        public void setFolderId(String folderId) { this.folderId = folderId; }
        public String getPersonId() { return personId; }
        public void setPersonId(String personId) { this.personId = personId; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public FileVersion getCurrentVersion() { return null; }
        public List<FileVersion> getVersions() { return new ArrayList<FileVersion>(); }
        public String getFileType() { return ""; }
        public String getMimeType() { return "application/octet-stream"; }
    }
    
    static class FallbackFileObject implements FileObject {
        private FallbackFileInfo fileInfo;
        private Path rootPath;
        
        FallbackFileObject(FallbackFileInfo fileInfo, Path rootPath) {
            this.fileInfo = fileInfo;
            this.rootPath = rootPath;
        }
        
        public String getID() { return fileInfo.getID(); }
        public String getName() { return fileInfo.getName(); }
        public String getRootPath() { return rootPath.toString(); }
        public String getAdapter() { return "local"; }
        public Long getLength() { return fileInfo.getLength(); }
        public String getHash() { return fileInfo.getHash(); }
        public String getPath() { return fileInfo.getPath(); }
        public Long getCreateTime() { return fileInfo.getCreateTime(); }
        
        public InputStream downLoad() throws VfsException {
            try {
                Path filePath = rootPath.resolve(fileInfo.getPath());
                return new FileInputStream(filePath.toFile());
            } catch (IOException e) {
                throw new VfsException("Failed to download file", e);
            }
        }
        
        public Integer writeLine(String str) throws VfsException {
            try {
                Path filePath = rootPath.resolve(fileInfo.getPath());
                Files.write(filePath, str.getBytes(), StandardOpenOption.APPEND);
                return str.length();
            } catch (IOException e) {
                throw new VfsException("Failed to write line", e);
            }
        }
        
        public List<String> readLine(List<Integer> lineNums) throws VfsException {
            try {
                Path filePath = rootPath.resolve(fileInfo.getPath());
                List<String> allLines = Files.readAllLines(filePath);
                List<String> result = new ArrayList<String>();
                for (Integer num : lineNums) {
                    if (num > 0 && num <= allLines.size()) {
                        result.add(allLines.get(num - 1));
                    }
                }
                return result;
            } catch (IOException e) {
                throw new VfsException("Failed to read lines", e);
            }
        }
    }
}
