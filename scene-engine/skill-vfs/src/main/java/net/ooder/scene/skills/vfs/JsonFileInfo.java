package net.ooder.scene.skills.vfs;

import java.util.ArrayList;
import java.util.List;

/**
 * JsonFileInfo JSON文件信息模型
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public class JsonFileInfo implements FileInfo {

    private String id;
    private String name;
    private String path;
    private Long length;
    private String hash;
    private Long createTime;
    private Long updateTime;
    private String folderId;
    private String personId;
    private String description;
    private String fileType;
    private String mimeType;
    
    private transient LocalVfsDataSource dataSource;

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setDataSource(LocalVfsDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public FileVersion getCurrentVersion() {
        if (dataSource != null) {
            List<FileVersion> versions = dataSource.getFileVersions(id);
            if (versions != null && !versions.isEmpty()) {
                return versions.get(versions.size() - 1);
            }
        }
        return null;
    }

    public List<FileVersion> getVersions() {
        if (dataSource != null) {
            return dataSource.getFileVersions(id);
        }
        return new ArrayList<FileVersion>();
    }
}
