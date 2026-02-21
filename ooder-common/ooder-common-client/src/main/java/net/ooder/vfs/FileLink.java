/**
 * $RCSfile: FileLink.java,v $
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
import net.ooder.annotation.Pid;

/**
 * 文件链接
 */
public interface FileLink extends java.io.Serializable {
    @MethodChinaName(cname = "获取文件链接Id")
    @Pid
    public String getID();

    @MethodChinaName(cname = "取得文件名称")
    public String getName();

    @MethodChinaName(cname = "获取副本Id")
    public String getFileId();

    @MethodChinaName(cname = "获取人员Id")
    public String getPersonId();
    
    @MethodChinaName(cname = "获取人员Id")
    public long getCreateTime();

    @MethodChinaName(cname = "获取文件状态")
    public String getState();


}
