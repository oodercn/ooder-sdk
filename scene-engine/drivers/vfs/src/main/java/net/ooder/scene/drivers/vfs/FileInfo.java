package net.ooder.scene.drivers.vfs;

import java.util.List;

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
