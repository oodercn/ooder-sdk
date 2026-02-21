package net.ooder.scene.drivers.vfs;

import java.util.List;
import java.util.Set;

public interface Folder {

    String getID();
    
    String getName();
    
    String getPath();
    
    String getParentId();
    
    String getDescription();
    
    String getPersonId();
    
    Long getCreateTime();
    
    List<FileInfo> getFileList();
    
    List<Folder> getChildrenList();
    
    Set<String> getFileIdList();
    
    Set<String> getChildrenIdList();
    
    Folder createChildFolder(String name, String personId);
    
    FileInfo createFile(String name, String personId);
    
    FolderType getFolderType();
    
    FolderState getState();
}
