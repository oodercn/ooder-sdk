package net.ooder.scene.skills.vfs;

import java.util.List;
import java.util.Set;

/**
 * Folder 文件夹接口
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
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
