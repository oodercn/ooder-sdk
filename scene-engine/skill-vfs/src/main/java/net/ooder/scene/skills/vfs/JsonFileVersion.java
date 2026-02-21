package net.ooder.scene.skills.vfs;

/**
 * JsonFileVersion JSON文件版本模型
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public class JsonFileVersion implements FileVersion {

    private String versionId;
    private String fileId;
    private String fileObjectId;
    private Long createTime;
    private String personId;
    private String sourceId;
    private String description;
    
    private transient LocalVfsDataSource dataSource;

    public String getVersionID() {
        return versionId;
    }

    public void setVersionID(String versionId) {
        this.versionId = versionId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileObjectId() {
        return fileObjectId;
    }

    public void setFileObjectId(String fileObjectId) {
        this.fileObjectId = fileObjectId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDataSource(LocalVfsDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public FileObject getFileObject() {
        return null;
    }
}
