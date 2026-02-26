package net.ooder.skill.vfs.api.entity;

import net.ooder.api.core.Identifiable;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件版本实体类
 */
public class FileVersion implements Identifiable, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String versionId;
    private String fileId;
    private Integer versionNumber;
    private Long fileSize;
    private String checksum;
    private Date createTime;
    private String creator;
    private String comment;
    private VersionStatus status;
    
    public FileVersion() {
    }
    
    @Override
    public String getId() {
        return versionId;
    }
    
    @Override
    public void setId(String id) {
        this.versionId = id;
    }
    
    public String getVersionId() {
        return versionId;
    }
    
    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }
    
    public String getFileId() {
        return fileId;
    }
    
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    
    public Integer getVersionNumber() {
        return versionNumber;
    }
    
    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getChecksum() {
        return checksum;
    }
    
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public String getCreator() {
        return creator;
    }
    
    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public VersionStatus getStatus() {
        return status;
    }
    
    public void setStatus(VersionStatus status) {
        this.status = status;
    }
    
    /**
     * 是否是最新版本
     * @return 是否最新
     */
    public boolean isLatest() {
        return status == VersionStatus.NORMAL;
    }
    
    /**
     * 是否可以恢复
     * @return 是否可以恢复
     */
    public boolean canRestore() {
        return status != VersionStatus.DELETED;
    }
}
