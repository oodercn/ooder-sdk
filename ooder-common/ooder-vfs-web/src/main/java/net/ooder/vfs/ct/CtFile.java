package net.ooder.vfs.ct;

import com.alibaba.fastjson.annotation.JSONField;
import  net.ooder.common.JDSException;
import  net.ooder.common.md5.MD5InputStream;
import  net.ooder.vfs.*;

import java.util.*;

/**
 * CtFile 实现了 FileInfo 接口，用于表示文件信息。
 */
public class CtFile implements FileInfo {
    private String ID;
    private String name;
    private String path = "";
    private Integer fileType;
    private String personId;
    private Long createTime;
    private String description;
    private String folderId;
    private String right;
    private String oldFolderId;
    private String currentVersonId;
    private String currentVersonFileHash;
    private Set<String> fileIdVersionList;
    private Set<String> fileIdLinkList;
    private Set<String> currViewIdList;
    private Set<String> roleIds;
    public Long updateTime;
    boolean isModified = false;

    /**
     * 构造函数，从 FileInfo 对象创建 CtFile。
     * @param file 原始文件信息对象
     */
    public CtFile(FileInfo file) {
        this.ID = file.getID();
        this.createTime = file.getCreateTime();
        this.name = file.getName();
        this.fileType = file.getFileType();
        this.description = file.getDescription();
        this.folderId = file.getFolderId();
        this.personId = file.getPersonId();
        this.fileIdLinkList = file.getLinkIds();
        this.fileIdVersionList = file.getVersionIds();
        this.currentVersonId = file.getCurrentVersonId();
        this.currentVersonFileHash = file.getCurrentVersonFileHash();
        this.currViewIdList = file.getCurrentViewIds();
        this.path = file.getPath();
        if (path == null) {
            path = getFolder().getPath() + name;
        }
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public int getCachedSize() {
        return 0;
    }

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getPersonId() {

        return this.personId;
    }

    @Override
    public Integer getFileType() {
        return this.fileType;
    }

    @Override
    public Long getCreateTime() {
        return this.createTime;
    }

    @Override
    public String getDescription() {

        return this.description == null ? this.name : this.description;
    }

    @Override
    public String getOldFolderId() {

        return this.oldFolderId;
    }

    public String getRight() {

        return right;
    }

    @Override
    public String getFolderId() {

        return this.folderId;
    }

    @Override
    public Set<String> getVersionIds() {
        return this.fileIdVersionList;
    }

    @Override
    @JSONField(serialize = false)
    public FileVersion getCurrentVersion() {

        try {
            return CtVfsFactory.getCtVfsService().getFileVersionById(this.getCurrentVersonId());
        } catch (JDSException e) {

            e.printStackTrace();
        }
        return null;

    }

    @Override
    @JSONField(serialize = false)
    public List<FileVersion> getVersionList() {

        List<FileVersion> versions = new ArrayList<FileVersion>();
        try {
            versions = CtVfsFactory.getCtVfsService().loadVersionByIds(this.getVersionIds());
        } catch (JDSException e) {

            e.printStackTrace();
        }

        Collections.sort(versions, new Comparator<FileVersion>() {
            public int compare(FileVersion o1, FileVersion o2) {
                return o2.getIndex() - o1.getIndex();
            }
        });

        return versions;

    }

    @Override
    @JSONField(serialize = false)
    public List<FileView> getCurrentViews() {
        List<FileView> versions = new ArrayList<FileView>();
        try {
            Set<String> viewIds = this.getCurrentViewIds();

            for (String viewId : viewIds) {

                versions.add(CtVfsFactory.getCtVfsService().getFileViewById(viewId));
            }

        } catch (JDSException e) {

            e.printStackTrace();
        }
        return versions;
    }

    @Override
    @JSONField(serialize = false)
    public List<FileLink> getLinks() {
        List<FileLink> links = new ArrayList<FileLink>();
        try {
            Set<String> linkIds = this.getLinkIds();

            for (String linkId : linkIds) {

                links.add(CtVfsFactory.getCtVfsService().getFileLinkById(linkId));
            }

        } catch (JDSException e) {

            e.printStackTrace();
        }
        return links;
    }

    @Override

    @JSONField(serialize = false)
    public Folder getFolder() {

        try {
            return CtVfsFactory.getCtVfsService().getFolderById(this.getFolderId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @JSONField(serialize = false)
    public MD5InputStream getCurrentVersonInputStream() {
        try {
            return CtVfsFactory.getCtVfsService().downLoad(this.getPath());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> getCurrentViewIds() {

        return this.currViewIdList;
    }

    @Override
    public Set<String> getLinkIds() {
        return this.fileIdLinkList;
    }

    @Override
    public void setOldFolderId(String oldFolderId) {
        this.oldFolderId = oldFolderId;

    }

    @Override
    public String getCurrentVersonId() {

        return this.currentVersonId;
    }

    @Override
    public String getCurrentVersonFileHash() {

        return this.currentVersonFileHash;
    }

    @Override
    public String toString() {
        return path;
    }
}
