package net.ooder.skill.vfs.api.entity;

import net.ooder.api.core.Identifiable;
import net.ooder.api.core.Named;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文件信息实体类
 * 保持与原有 API 100% 兼容
 */
public class FileInfo implements Identifiable, Named, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String fileId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private Date createTime;
    private Date modifyTime;
    private String creator;
    private String modifier;
    
    public FileInfo() {
    }
    
    @Override
    public String getId() {
        return fileId;
    }
    
    @Override
    public void setId(String id) {
        this.fileId = id;
    }
    
    @Override
    public String getName() {
        return fileName;
    }
    
    @Override
    public void setName(String name) {
        this.fileName = name;
    }
    
    public String getFileId() {
        return fileId;
    }
    
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getModifyTime() {
        return modifyTime;
    }
    
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
    
    public String getCreator() {
        return creator;
    }
    
    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    public String getModifier() {
        return modifier;
    }
    
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
    
    /**
     * 获取文件版本列表
     * 原有 API 兼容方法
     * @return 版本列表
     */
    public List<FileVersion> getFileVersionList() {
        return VfsEntityFacade.getInstance().getFileVersionList(this.fileId);
    }
}
