package net.ooder.scene.skills.vfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * LocalVfsDataSource 本地文件数据源
 * 
 * <p>提供本地文件系统存储，用于：
 * <ul>
 *   <li>默认数据源（无外部 skills 时）</li>
 *   <li>降级实现（外部 skills 不支持某些能力时）</li>
 * </ul>
 * </p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public class LocalVfsDataSource {

    private String storagePath;
    private String dataPath;
    private JSONObject vfsData;
    private Map<String, JsonFileInfo> fileInfoMap = new HashMap<String, JsonFileInfo>();
    private Map<String, JsonFolder> folderMap = new HashMap<String, JsonFolder>();
    private Map<String, JsonFileVersion> versionMap = new HashMap<String, JsonFileVersion>();
    
    private Map<String, List<String>> folderFileMap = new HashMap<String, List<String>>();
    private Map<String, List<String>> folderChildMap = new HashMap<String, List<String>>();
    private Map<String, String> fileFolderMap = new HashMap<String, String>();
    private Map<String, List<String>> fileVersionMap = new HashMap<String, List<String>>();

    public LocalVfsDataSource() {
        this.storagePath = "data/vfs";
        this.dataPath = "data/vfs/files";
    }

    public LocalVfsDataSource(String storagePath, String dataPath) {
        this.storagePath = storagePath;
        this.dataPath = dataPath;
    }

    public void initialize() {
        File dataDir = new File(dataPath);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        loadFromJson();
    }

    private void loadFromJson() {
        File file = new File(storagePath, "vfs.json");
        if (!file.exists()) {
            createDefaultData();
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            
            String jsonStr = new String(data, "UTF-8");
            vfsData = JSON.parseObject(jsonStr);
            parseVfsData();
        } catch (IOException e) {
            createDefaultData();
        }
    }

    private void createDefaultData() {
        vfsData = new JSONObject();
        vfsData.put("files", new JSONArray());
        vfsData.put("folders", new JSONArray());
        vfsData.put("versions", new JSONArray());
        
        JsonFolder rootFolder = new JsonFolder();
        rootFolder.setID("folder-root");
        rootFolder.setName("根目录");
        rootFolder.setPath("/");
        rootFolder.setFolderType(FolderType.SYSTEM);
        folderMap.put(rootFolder.getID(), rootFolder);
        
        saveToJson();
    }

    private void parseVfsData() {
        JSONArray files = vfsData.getJSONArray("files");
        if (files != null) {
            for (int i = 0; i < files.size(); i++) {
                JSONObject f = files.getJSONObject(i);
                JsonFileInfo fileInfo = new JsonFileInfo();
                fileInfo.setID(f.getString("fileId"));
                fileInfo.setName(f.getString("name"));
                fileInfo.setPath(f.getString("path"));
                fileInfo.setLength(f.getLong("length"));
                fileInfo.setHash(f.getString("hash"));
                fileInfo.setCreateTime(f.getLong("createTime"));
                fileInfo.setUpdateTime(f.getLong("updateTime"));
                fileInfo.setFolderId(f.getString("folderId"));
                fileInfo.setPersonId(f.getString("personId"));
                fileInfo.setFileType(f.getString("fileType"));
                fileInfo.setMimeType(f.getString("mimeType"));
                fileInfoMap.put(fileInfo.getID(), fileInfo);
                
                String folderId = f.getString("folderId");
                if (folderId != null) {
                    fileFolderMap.put(fileInfo.getID(), folderId);
                    if (!folderFileMap.containsKey(folderId)) {
                        folderFileMap.put(folderId, new ArrayList<String>());
                    }
                    folderFileMap.get(folderId).add(fileInfo.getID());
                }
            }
        }

        JSONArray folders = vfsData.getJSONArray("folders");
        if (folders != null) {
            for (int i = 0; i < folders.size(); i++) {
                JSONObject fo = folders.getJSONObject(i);
                JsonFolder folder = new JsonFolder();
                folder.setID(fo.getString("folderId"));
                folder.setName(fo.getString("name"));
                folder.setPath(fo.getString("path"));
                folder.setParentId(fo.getString("parentId"));
                folder.setCreateTime(fo.getLong("createTime"));
                folder.setPersonId(fo.getString("personId"));
                String typeStr = fo.getString("folderType");
                if ("SHARED".equals(typeStr)) {
                    folder.setFolderType(FolderType.SHARED);
                } else if ("SYSTEM".equals(typeStr)) {
                    folder.setFolderType(FolderType.SYSTEM);
                } else if ("TEMPORARY".equals(typeStr)) {
                    folder.setFolderType(FolderType.TEMPORARY);
                } else if ("PROJECT".equals(typeStr)) {
                    folder.setFolderType(FolderType.PROJECT);
                } else {
                    folder.setFolderType(FolderType.NORMAL);
                }
                folderMap.put(folder.getID(), folder);
                
                String parentId = fo.getString("parentId");
                if (parentId != null) {
                    if (!folderChildMap.containsKey(parentId)) {
                        folderChildMap.put(parentId, new ArrayList<String>());
                    }
                    folderChildMap.get(parentId).add(folder.getID());
                }
            }
        }

        JSONArray versions = vfsData.getJSONArray("versions");
        if (versions != null) {
            for (int i = 0; i < versions.size(); i++) {
                JSONObject v = versions.getJSONObject(i);
                JsonFileVersion version = new JsonFileVersion();
                version.setVersionID(v.getString("versionId"));
                version.setFileId(v.getString("fileId"));
                version.setFileObjectId(v.getString("fileObjectId"));
                version.setCreateTime(v.getLong("createTime"));
                version.setPersonId(v.getString("personId"));
                version.setDescription(v.getString("description"));
                versionMap.put(version.getVersionID(), version);
                
                String fileId = v.getString("fileId");
                if (!fileVersionMap.containsKey(fileId)) {
                    fileVersionMap.put(fileId, new ArrayList<String>());
                }
                fileVersionMap.get(fileId).add(version.getVersionID());
            }
        }
    }

    public void saveToJson() {
        try {
            JSONObject data = new JSONObject();
            
            JSONArray files = new JSONArray();
            for (JsonFileInfo fileInfo : fileInfoMap.values()) {
                JSONObject f = new JSONObject();
                f.put("fileId", fileInfo.getID());
                f.put("name", fileInfo.getName());
                f.put("path", fileInfo.getPath());
                f.put("length", fileInfo.getLength());
                f.put("hash", fileInfo.getHash());
                f.put("createTime", fileInfo.getCreateTime());
                f.put("updateTime", fileInfo.getUpdateTime());
                f.put("folderId", fileInfo.getFolderId());
                f.put("personId", fileInfo.getPersonId());
                f.put("fileType", fileInfo.getFileType());
                f.put("mimeType", fileInfo.getMimeType());
                files.add(f);
            }
            data.put("files", files);

            JSONArray folders = new JSONArray();
            for (JsonFolder folder : folderMap.values()) {
                JSONObject fo = new JSONObject();
                fo.put("folderId", folder.getID());
                fo.put("name", folder.getName());
                fo.put("path", folder.getPath());
                fo.put("parentId", folder.getParentId());
                fo.put("createTime", folder.getCreateTime());
                fo.put("personId", folder.getPersonId());
                fo.put("folderType", folder.getFolderType().name());
                folders.add(fo);
            }
            data.put("folders", folders);

            JSONArray versions = new JSONArray();
            for (JsonFileVersion version : versionMap.values()) {
                JSONObject v = new JSONObject();
                v.put("versionId", version.getVersionID());
                v.put("fileId", version.getFileId());
                v.put("fileObjectId", version.getFileObjectId());
                v.put("createTime", version.getCreateTime());
                v.put("personId", version.getPersonId());
                v.put("description", version.getDescription());
                versions.add(v);
            }
            data.put("versions", versions);

            File file = new File(storagePath, "vfs.json");
            file.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(file);
            writer.write(data.toJSONString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileInfo getFileInfoByID(String fileId) {
        return fileInfoMap.get(fileId);
    }

    public FileInfo createFile(String folderId, String name, String personId) {
        JsonFileInfo fileInfo = new JsonFileInfo();
        fileInfo.setID("file-" + System.currentTimeMillis());
        fileInfo.setName(name);
        fileInfo.setFolderId(folderId);
        fileInfo.setPersonId(personId);
        fileInfo.setCreateTime(System.currentTimeMillis());
        fileInfo.setUpdateTime(System.currentTimeMillis());
        
        JsonFolder folder = folderMap.get(folderId);
        if (folder != null) {
            fileInfo.setPath(folder.getPath() + "/" + name);
        }
        
        fileInfoMap.put(fileInfo.getID(), fileInfo);
        fileFolderMap.put(fileInfo.getID(), folderId);
        
        if (!folderFileMap.containsKey(folderId)) {
            folderFileMap.put(folderId, new ArrayList<String>());
        }
        folderFileMap.get(folderId).add(fileInfo.getID());
        
        saveToJson();
        return fileInfo;
    }

    public boolean deleteFile(String fileId) {
        JsonFileInfo fileInfo = fileInfoMap.remove(fileId);
        if (fileInfo == null) {
            return false;
        }
        
        String folderId = fileFolderMap.remove(fileId);
        if (folderId != null && folderFileMap.containsKey(folderId)) {
            folderFileMap.get(folderId).remove(fileId);
        }
        
        List<String> versions = fileVersionMap.remove(fileId);
        if (versions != null) {
            for (String versionId : versions) {
                versionMap.remove(versionId);
            }
        }
        
        File physicalFile = new File(dataPath, fileId);
        if (physicalFile.exists()) {
            physicalFile.delete();
        }
        
        saveToJson();
        return true;
    }

    public List<FileInfo> listFiles(String folderId) {
        List<FileInfo> result = new ArrayList<FileInfo>();
        List<String> fileIds = folderFileMap.get(folderId);
        if (fileIds != null) {
            for (String fid : fileIds) {
                FileInfo fi = fileInfoMap.get(fid);
                if (fi != null) {
                    result.add(fi);
                }
            }
        }
        return result;
    }

    public Folder getFolderByID(String folderId) {
        return folderMap.get(folderId);
    }

    public Folder createFolder(String parentId, String name, String personId) {
        JsonFolder folder = new JsonFolder();
        folder.setID("folder-" + System.currentTimeMillis());
        folder.setName(name);
        folder.setParentId(parentId);
        folder.setPersonId(personId);
        folder.setCreateTime(System.currentTimeMillis());
        folder.setFolderType(FolderType.NORMAL);
        
        JsonFolder parent = folderMap.get(parentId);
        if (parent != null) {
            folder.setPath(parent.getPath() + "/" + name);
        } else {
            folder.setPath("/" + name);
        }
        
        folderMap.put(folder.getID(), folder);
        
        if (parentId != null) {
            if (!folderChildMap.containsKey(parentId)) {
                folderChildMap.put(parentId, new ArrayList<String>());
            }
            folderChildMap.get(parentId).add(folder.getID());
        }
        
        saveToJson();
        return folder;
    }

    public boolean deleteFolder(String folderId) {
        if ("folder-root".equals(folderId)) {
            return false;
        }
        
        JsonFolder folder = folderMap.remove(folderId);
        if (folder == null) {
            return false;
        }
        
        String parentId = folder.getParentId();
        if (parentId != null && folderChildMap.containsKey(parentId)) {
            folderChildMap.get(parentId).remove(folderId);
        }
        
        List<String> childIds = folderChildMap.remove(folderId);
        if (childIds != null) {
            for (String childId : childIds) {
                deleteFolder(childId);
            }
        }
        
        List<String> fileIds = folderFileMap.remove(folderId);
        if (fileIds != null) {
            for (String fileId : fileIds) {
                deleteFile(fileId);
            }
        }
        
        saveToJson();
        return true;
    }

    public List<Folder> listFolders(String parentId) {
        List<Folder> result = new ArrayList<Folder>();
        List<String> childIds = folderChildMap.get(parentId);
        if (childIds != null) {
            for (String cid : childIds) {
                Folder f = folderMap.get(cid);
                if (f != null) {
                    result.add(f);
                }
            }
        }
        return result;
    }

    public InputStream downloadFile(String fileId) {
        File file = new File(dataPath, fileId);
        if (!file.exists()) {
            return null;
        }
        try {
            return new FileInputStream(file);
        } catch (IOException e) {
            return null;
        }
    }

    public FileInfo uploadFile(String folderId, String name, InputStream content, String personId) {
        JsonFileInfo fileInfo = (JsonFileInfo) createFile(folderId, name, personId);
        
        try {
            File file = new File(dataPath, fileInfo.getID());
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalLength = 0;
            while ((bytesRead = content.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalLength += bytesRead;
            }
            fos.close();
            
            fileInfo.setLength(totalLength);
            fileInfo.setUpdateTime(System.currentTimeMillis());
            saveToJson();
            
        } catch (IOException e) {
            deleteFile(fileInfo.getID());
            return null;
        }
        
        return fileInfo;
    }

    public List<FileVersion> getFileVersions(String fileId) {
        List<FileVersion> result = new ArrayList<FileVersion>();
        List<String> versionIds = fileVersionMap.get(fileId);
        if (versionIds != null) {
            for (String vid : versionIds) {
                FileVersion v = versionMap.get(vid);
                if (v != null) {
                    result.add(v);
                }
            }
        }
        return result;
    }

    public FileVersion createVersion(String fileId, String description, String personId) {
        JsonFileVersion version = new JsonFileVersion();
        version.setVersionID("version-" + System.currentTimeMillis());
        version.setFileId(fileId);
        version.setFileObjectId(fileId);
        version.setCreateTime(System.currentTimeMillis());
        version.setPersonId(personId);
        version.setDescription(description);
        
        versionMap.put(version.getVersionID(), version);
        
        if (!fileVersionMap.containsKey(fileId)) {
            fileVersionMap.put(fileId, new ArrayList<String>());
        }
        fileVersionMap.get(fileId).add(version.getVersionID());
        
        saveToJson();
        return version;
    }

    public List<FileInfo> searchFiles(String keyword, String folderId) {
        List<FileInfo> result = new ArrayList<FileInfo>();
        for (JsonFileInfo fileInfo : fileInfoMap.values()) {
            if (fileInfo.getName().toLowerCase().contains(keyword.toLowerCase())) {
                if (folderId == null || folderId.equals(fileInfo.getFolderId())) {
                    result.add(fileInfo);
                }
            }
        }
        return result;
    }

    public void reloadAll() {
        fileInfoMap.clear();
        folderMap.clear();
        versionMap.clear();
        folderFileMap.clear();
        folderChildMap.clear();
        fileFolderMap.clear();
        fileVersionMap.clear();
        loadFromJson();
    }
}
