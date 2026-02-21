/**
 * $RCSfile: FileIndex.java,v $
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
package net.ooder.vfs.index;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.index.config.type.*;
import net.ooder.vfs.FileInfo;
import org.apache.lucene.document.Field.Store;

@JDocumentType(name = "VfsFileIndex", fsDirectory = @FSDirectoryType(id = "VfsFileIndex"), vfsJson = @VFSJsonType(vfsPath = "doc/log/", fileName = "vfsLog.js"), indexWriter = @JIndexWriterType(id = "vfsLogIndex"))
public class FileIndex implements VFSIndex {
    @JFieldType(store = Store.YES)
    String name;
    @JFieldType(store = Store.YES)
    String userId;
    @JFieldType(store = Store.YES, highlighter = true)
    StringBuffer text = new StringBuffer();

    @JFieldType(store = Store.YES)
    String desc;
    @JFieldType(store = Store.YES)
    String right;

    @JFieldType(store = Store.YES)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    Long createtime;

    @JFieldType(store = Store.YES)
    String docpath;


    String path = "VfsFileIndex";

    String uuid;

    public String getDocpath() {
        return docpath;
    }

    public void setDocpath(String docpath) {
        this.docpath = docpath;
    }

    public FileIndex() {

    }

    public FileIndex(FileInfo fileInfo) {

        this.name = fileInfo.getName();
        this.userId = fileInfo.getPersonId();
        this.docpath = fileInfo.getPath();
        this.createtime = fileInfo.getCreateTime();
        this.right = fileInfo.getRight();
        this.desc = fileInfo.getDescription();


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StringBuffer getText() {
        return text;
    }

    public void setText(StringBuffer text) {
        this.text = text;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }


    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getPath() {
        return path;
    }


}


