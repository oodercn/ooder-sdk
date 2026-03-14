/**
 * $RCSfile: JSyncListenerBean.java,v $
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

public class JSyncListenerBean implements JSyncListener{
    

    // VFS 云端VFS对应路径
    String vfsPath;

    // 临时文件路径
    String tempPath;
    
    
    // VFS 云端VFS对应路径
    String vfsRootPath;

    // 临时文件路径
    String tempRootPath;

    // 文件同步监听器
    Class syncListener;
    
    Class clazz;
    
    String id;
    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setClazz(Class clazz) {
	this.clazz=clazz;
    }

    @Override
    public Class getClazz(){
	return clazz;
    }
    

    public String getVfsPath() {
        return vfsPath;
    }

    public void setVfsPath(String vfsPath) {
        this.vfsPath = vfsPath;
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }

    public String getVfsRootPath() {
        return vfsRootPath;
    }

    public void setVfsRootPath(String vfsRootPath) {
        this.vfsRootPath = vfsRootPath;
    }

    public String getTempRootPath() {
        return tempRootPath;
    }

    public void setTempRootPath(String tempRootPath) {
        this.tempRootPath = tempRootPath;
    }

    public Class getSyncListener() {
        return syncListener;
    }

    public void setSyncListener(Class syncListener) {
        this.syncListener = syncListener;
    }

}


