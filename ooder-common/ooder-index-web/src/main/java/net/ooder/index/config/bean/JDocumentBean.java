/**
 * $RCSfile: JDocumentBean.java,v $
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

public class JDocumentBean implements JDocument {
    String id;
    String name;
    Class clazz;
    boolean vfsValid;
    boolean indexValid;

    VFSJsonBean vfsJson;
        
    JFSDirectory fsDirectory;
    
    JIndexWriter indexWriter;

    @MethodChinaName("获取ID")
    public String getId() {
	return id;
    }

    @MethodChinaName("设置ID")
    public void setId(String id) {
	this.id = id;
    }

    @MethodChinaName("获取文件系统目录")
    public JFSDirectory getFsDirectory() {
	return fsDirectory;
    }

    @MethodChinaName("设置文件系统目录")
    public void setFsDirectory(JFSDirectory fsDirectory) {
	this.fsDirectory = fsDirectory;
    }

    @MethodChinaName("获取索引写入器")
    public JIndexWriter getIndexWriter() {
	return indexWriter;
    }

    @MethodChinaName("设置索引写入器")
    public void setIndexWriter(JIndexWriter indexWriter) {
	this.indexWriter = indexWriter;
    }

    @MethodChinaName("设置类对象")
    public void setClazz(Class clazz) {
	this.clazz = clazz;
    }

    @MethodChinaName("获取名称")
    public String getName() {
	return name;
    }

    @MethodChinaName("设置名称")
    public void setName(String name) {
	this.name = name;
    }

    @MethodChinaName("获取类对象")
    public Class getClazz() {
	return clazz;
    }
    

    @MethodChinaName("设置VFS有效性")
    public void setVfsValid(boolean vfsValid) {
        this.vfsValid = vfsValid;
    }

    @MethodChinaName("设置索引有效性")
    public void setIndexValid(boolean indexValid) {
        this.indexValid = indexValid;
    }
  

    @MethodChinaName("判断VFS是否有效")
    public boolean isVfsValid() {
        return vfsValid;
    }

    @MethodChinaName("判断索引是否有效")
    public boolean isIndexValid() {
        return indexValid;
    }


    @MethodChinaName("获取VFS JSON配置")
    @Override
    public VFSJsonBean getVfsJson() {
	return vfsJson;
    }

    @MethodChinaName("设置VFS JSON配置")
    public void setVfsJson(VFSJsonBean _vfsJson) {
	this.vfsJson = _vfsJson;
    }

}


