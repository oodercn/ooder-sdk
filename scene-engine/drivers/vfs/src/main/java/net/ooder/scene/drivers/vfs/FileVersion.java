package net.ooder.scene.drivers.vfs;

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
