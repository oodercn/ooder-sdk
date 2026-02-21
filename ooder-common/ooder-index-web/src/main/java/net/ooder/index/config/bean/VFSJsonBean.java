/**
 * $RCSfile: VFSJsonBean.java,v $
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

public class VFSJsonBean implements VFSJson {

    String vfsPath;
    String fileName;
    String path;
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
	this.clazz = clazz;
    }

    @Override
    public Class getClazz() {
	return clazz;
    }


    public String getPath() {
	return path;
    }

    public String getVfsPath() {
	return vfsPath;
    }

    public void setVfsPath(String vfsPath) {
	this.vfsPath = vfsPath;
    }

    public void setPath(String path) {
	this.path = path;
    }

    @Override
    public String getFileName() {
	return fileName;
    }

    @Override
    public void setFileName(String fileName) {
	this.fileName=fileName;
    }

 

}


