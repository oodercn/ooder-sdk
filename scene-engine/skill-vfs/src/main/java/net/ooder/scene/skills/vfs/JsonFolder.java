package net.ooder.scene.skills.vfs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JsonFolder JSON文件夹模型
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public class JsonFolder implements Folder {

    private String id;
    private String name;
    private String path;
    private String parentId;
    private String description;
    private String personId;
    private Long createTime;
    private FolderType folderType = FolderType.NORMAL;
    private FolderState folderState = FolderState.NORMAL;
    
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public FolderType getFolderType() {
        return folderType;
    }

    public void setFolderType(FolderType folderType) {
        this.folderType = folderType;
    }

    public FolderState getState() {
        return folderState;
    }

    public void setFolderState(FolderState folderState) {
        this.folderState = folderState;
    }

    public void setDataSource(LocalVfsDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<FileInfo> getFileList() {
        if (dataSource != null) {
            return dataSource.listFiles(id);
        }
        return new ArrayList<FileInfo>();
    }

    public List<Folder> getChildrenList() {
        if (dataSource != null) {
            return dataSource.listFolders(id);
        }
        return new ArrayList<Folder>();
    }

    public Set<String> getFileIdList() {
        Set<String> result = new HashSet<String>();
        for (FileInfo fi : getFileList()) {
            result.add(fi.getID());
        }
        return result;
    }

    public Set<String> getChildrenIdList() {
        Set<String> result = new HashSet<String>();
        for (Folder f : getChildrenList()) {
            result.add(f.getID());
        }
        return result;
    }

    public Folder createChildFolder(String name, String personId) {
        if (dataSource != null) {
            return dataSource.createFolder(id, name, personId);
        }
        return null;
    }

    public FileInfo createFile(String name, String personId) {
        if (dataSource != null) {
            return dataSource.createFile(id, name, personId);
        }
        return null;
    }
}
