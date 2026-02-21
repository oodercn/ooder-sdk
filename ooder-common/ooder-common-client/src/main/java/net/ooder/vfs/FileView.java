/**
 * $RCSfile: FileView.java,v $
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

/**
 * 文件视图
 */
public interface FileView extends java.io.Serializable {

    @MethodChinaName(cname = "获取文件视图Id")
    public String getID();

    @MethodChinaName(cname = "取得文件视图名称")
    public String getName();

    public void setName(String name);

    @MethodChinaName(cname = "取得文件视图类型")
    public int getFileType();

    public void setFileType(int fileType);

    @MethodChinaName(cname = "获取文件段排序")
    public int getFileIndex();

    public void setFileIndex(int fileIndex);

    @MethodChinaName(cname = "获取版本Id")
    public String getVersionId();

    public void setVersionId(String versionId);

    @MethodChinaName(cname = "获取源文件")
    public FileObject getFileObject();

    public void setFileObjectId(String objId);

    public String getFileObjectId();

    @MethodChinaName(cname = "获取视图文件地址")
    public String getPath();


}
