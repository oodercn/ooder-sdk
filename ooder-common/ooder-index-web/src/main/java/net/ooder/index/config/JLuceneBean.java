/**
 * $RCSfile: JLuceneBean.java,v $
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
package net.ooder.index.config;

import net.ooder.index.config.bean.JFSDirectory;
import net.ooder.index.config.bean.JField;
import net.ooder.index.config.bean.JIndexWriter;
import net.ooder.index.config.bean.VFSJson;
import net.ooder.annotation.MethodChinaName;

import java.util.ArrayList;
import java.util.List;

public class JLuceneBean implements JLucene {

    String name;
    String json;
    String id;
    String uuid;


    String userId;
    Class clazz;
    boolean vfsValid;
    boolean indexValid;

    JFSDirectory fsDirectory;
    JIndexWriter indexWriter;

    VFSJson vfsJson;

    List<String> vfsFilePaths=new ArrayList<>();

    List<JField> fields;

    List<String> highFields=new ArrayList<String>();

    @MethodChinaName("获取VFS JSON配置")
    public VFSJson getVfsJson() {
        return vfsJson;
    }

    @MethodChinaName("设置VFS JSON配置")
    public void setVfsJson(VFSJson vfsJson) {
        this.vfsJson = vfsJson;
    }

    @MethodChinaName("设置VFS有效性")
    public void setVfsValid(boolean vfsValid) {
        this.vfsValid = vfsValid;
    }


    @MethodChinaName("获取UUID")
    @Override
    public String getUuid() {
        return uuid;
    }

    @MethodChinaName("设置UUID")
    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @MethodChinaName("设置索引有效性")
    public void setIndexValid(boolean indexValid) {
        this.indexValid = indexValid;
    }
    @MethodChinaName("获取VFS文件路径列表")
    @Override
    public List<String> getVfsFilePaths() {
        return vfsFilePaths;
    }

    @MethodChinaName("设置VFS文件路径列表")
    @Override
    public void setVfsFilePaths(List<String> vfsFilePaths) {
        this.vfsFilePaths = vfsFilePaths;
    }

    @MethodChinaName("判断VFS是否有效")
    public boolean isVfsValid() {
        return vfsValid;
    }

    @MethodChinaName("判断索引是否有效")
    public boolean isIndexValid() {
        return indexValid;
    }

    @MethodChinaName("获取高亮字段列表")
    @Override
    public List<String> getHighFields() {
        return highFields;
    }

    @MethodChinaName("设置高亮字段列表")
    @Override
    public void setHighFields(List<String> hightFields) {
        this.highFields = highFields;
    }

    @MethodChinaName("获取JSON配置")
    @Override
    public String getJson() {
        return json;
    }

    @MethodChinaName("设置JSON配置")
    @Override
    public void setJson(String json) {
         this.json = json;
    }

    @MethodChinaName("获取用户ID")
    @Override
    public String getUserId() {
        return userId;
    }

    @MethodChinaName("设置用户ID")
    @Override
    public void setUserId(String userId) {

        this.userId = userId;
    }

    @MethodChinaName("获取ID")
    @Override
    public String getId() {
        return id;
    }

    @MethodChinaName("设置ID")
    @Override
    public void setId(String id) {
        this.id = id;
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

    @MethodChinaName("设置类对象")
    public void setClazz(Class clazz) {
        this.clazz = clazz;
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

    @MethodChinaName("获取字段列表")
    public List<JField> getFields() {
        return fields;
    }

    @MethodChinaName("设置字段列表")
    public void setFields(List<JField> fields) {
        this.fields = fields;
    }

}


