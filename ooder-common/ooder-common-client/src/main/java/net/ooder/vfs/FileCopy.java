/**
 * $RCSfile: FileCopy.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.vfs;


import net.ooder.annotation.MethodChinaName;
import net.ooder.annotation.Uid;

/**
 * 文件发送时候副本
 * 
 */
public interface FileCopy extends java.io.Serializable {

    @MethodChinaName(cname = "副本ID")
    @Uid
    public String getID();

    public void setID(String id);

    @MethodChinaName(cname = "版本ID")
    public String getVersionId();

    public void setVersionId(String versionId);

    @MethodChinaName(cname = "名称")
    public String getName();

    public void setName(String name);

    @MethodChinaName(cname = "状态")
    public int getState();

    public void setState(int state);

    @MethodChinaName(cname = "创建时间")
    public long getCreateTime();

    public void setCreateTime(long createTime);

    @MethodChinaName(cname = "权限")
    public void setMaxRight(int maxRight);

    public int getMaxRight();

    @MethodChinaName(cname = "文件夹")
    public void setFolderId(String folderId);

    public String getFolderId();

    @MethodChinaName(cname = "人员")
    public void setPersonId(String personId);

    public String getPersonId();


    public int getCachedSize();

    @MethodChinaName(cname = "文件")
    public void setFileId(String fileId);

    public String getFileId();

}
