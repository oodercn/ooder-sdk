package net.ooder.scene.skills.vfs;

import java.util.List;

/**
 * FileInfo 文件信息接口
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface FileInfo {

    String getID();
    
    String getName();
    
    String getPath();
    
    Long getLength();
    
    String getHash();
    
    Long getCreateTime();
    
    Long getUpdateTime();
    
    String getFolderId();
    
    String getPersonId();
    
    String getDescription();
    
    FileVersion getCurrentVersion();
    
    List<FileVersion> getVersions();
    
    String getFileType();
    
    String getMimeType();
}
