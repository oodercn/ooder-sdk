/**
 * $RCSfile: JLucene.java,v $
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

import java.util.List;

public interface JLucene {


    @MethodChinaName("获取UUID")
    public String getUuid();

    @MethodChinaName("设置UUID")
    public void setUuid(String uuid);

    @MethodChinaName("获取JSON配置")
    public String getJson();

    @MethodChinaName("设置JSON配置")
    public void setJson(String json);

    @MethodChinaName("获取用户ID")
    public String getUserId();

    @MethodChinaName("设置用户ID")
    public void setUserId(String userId);

    @MethodChinaName("获取ID")
    public String getId();

    @MethodChinaName("设置ID")
    public void setId(String id);

    @MethodChinaName("获取名称")
    public String getName();

    @MethodChinaName("设置名称")
    public void setName(String name);

    @MethodChinaName("获取文件系统目录")
    public JFSDirectory getFsDirectory();

    @MethodChinaName("设置文件系统目录")
    public void setFsDirectory(JFSDirectory jFsDirectory);

    @MethodChinaName("获取索引写入器")
    public JIndexWriter getIndexWriter();

    @MethodChinaName("设置索引写入器")
    public void setIndexWriter(JIndexWriter jIndexWriter);

    @MethodChinaName("获取字段列表")
    public List<JField> getFields();

    @MethodChinaName("设置字段列表")
    public void setFields(List<JField> fields);


    @MethodChinaName("获取VFS文件路径列表")
    public List<String> getVfsFilePaths();

    @MethodChinaName("设置VFS文件路径列表")
    public void setVfsFilePaths(List<String> filePaths);

    @MethodChinaName("获取VFS JSON配置")
    public VFSJson getVfsJson();
    
    @MethodChinaName("设置VFS JSON配置")
    public void setVfsJson(VFSJson vfsJson);

    @MethodChinaName("设置VFS有效性")
    public void setVfsValid(boolean vfsValid);

    @MethodChinaName("设置索引有效性")
    public void setIndexValid(boolean indexValid);

    @MethodChinaName("判断VFS是否有效")
    public boolean isVfsValid();

    @MethodChinaName("判断索引是否有效")
    public boolean isIndexValid();

    @MethodChinaName("获取高亮字段列表")
    public List<String> getHighFields();

    @MethodChinaName("设置高亮字段列表")
    public void setHighFields(List<String> hightFields);

}


