package net.ooder.scene.skills.vfs;

/**
 * FileVersion 文件版本接口
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public interface FileVersion {

    String getVersionID();
    
    String getFileId();
    
    String getFileObjectId();
    
    FileObject getFileObject();
    
    Long getCreateTime();
    
    String getPersonId();
    
    String getSourceId();
    
    String getDescription();
}
