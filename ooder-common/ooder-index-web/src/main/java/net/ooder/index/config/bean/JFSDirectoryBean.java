/**
 * $RCSfile: JFSDirectoryBean.java,v $
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
package net.ooder.index.config.bean;

import net.ooder.annotation.MethodChinaName;

public class JFSDirectoryBean implements JFSDirectory {

    Class fsDirectoryClass;
    
  //文件同步监听器
    JSyncListener syncListener;
    
    
    //延时执行时间
    long syncDelayTime;
    
   
    //最大任务数量
    int maxTaskSize;
    
    String vfsPath;
    String tempPath;
    String path="demo";
    Class clazz;
    String id;
    
  
    
    @MethodChinaName("获取ID")
    public String getId() {
        return id;
    }

    @MethodChinaName("设置ID")
    public void setId(String id) {
        this.id = id;
    }

    @MethodChinaName("设置类对象")
    @Override
    public void setClazz(Class clazz) {
	this.clazz=clazz;
    }

    @MethodChinaName("获取类对象")
    @Override
    public Class getClazz(){
	return clazz;
    }
    
    @MethodChinaName("获取文件系统目录类")
    public Class getFsDirectoryClass() {
        return fsDirectoryClass;
    }
    @MethodChinaName("设置文件系统目录类")
    public void setFsDirectoryClass(Class fsDirectoryClass) {
        this.fsDirectoryClass = fsDirectoryClass;
    }
  
    @MethodChinaName("获取临时路径")
    public String getTempPath() {
        return tempPath;
    }
    @MethodChinaName("设置临时路径")
    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }
    @MethodChinaName("获取路径")
    public String getPath() {
        return path;
    }
    @MethodChinaName("获取VFS路径")
    public String getVfsPath() {
        return vfsPath;
    }
    @MethodChinaName("设置VFS路径")
    public void setVfsPath(String vfsPath) {
        this.vfsPath = vfsPath;
    }
    @MethodChinaName("设置路径")
    public void setPath(String path) {
        this.path = path;
    }

    @MethodChinaName("获取同步监听器")
    public JSyncListener getSyncListener() {
        return syncListener;
    }

    @MethodChinaName("设置同步监听器")
    public void setSyncListener(JSyncListener syncListener) {
        this.syncListener = syncListener;
    }
   
    @MethodChinaName("获取同步延迟时间")
    public long getSyncDelayTime() {
        return syncDelayTime;
    }

    @MethodChinaName("设置同步延迟时间")
    public void setSyncDelayTime(long syncDelayTime) {
        this.syncDelayTime = syncDelayTime;
    }

    @MethodChinaName("获取最大任务数量")
    public int getMaxTaskSize() {
        return maxTaskSize;
    }

    @MethodChinaName("设置最大任务数量")
    public void setMaxTaskSize(int maxTaskSize) {
        this.maxTaskSize = maxTaskSize;
    }


}


