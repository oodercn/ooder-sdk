package net.ooder.vfs.ct;

import  net.ooder.common.JDSException;
import  net.ooder.common.md5.MD5InputStream;
import  net.ooder.vfs.FileObject;

import java.util.List;
import java.util.UUID;

public class CtFileObject implements FileObject {

    private String ID;
    private String hash;
    private String name;
    private long length;
    private String adapter;
    private String rootPath;
    private String path;
    private long createTime;

    public CtFileObject() {
        this.ID=UUID.randomUUID().toString();
    }

    public CtFileObject(FileObject object) {
        this.ID = object.getID();
        this.hash = object.getHash();
        this.name = object.getName();
        this.length = object.getLength();
        this.rootPath = object.getRootPath();
        //this.path = object.getPath();
        this.path = CtVfsFactory.getLocalCachePath()+ this.getHash();
        this.createTime = object.getCreateTime();
        this.adapter = object.getAdapter();
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void setID(String id) {
        this.ID = id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public void setName(String name) {

        this.name = name;
    }

    @Override
    public String getRootPath() {

        return rootPath;
    }

    @Override
    public void setRootPath(String rootPath) {

        this.rootPath = rootPath;
    }

    @Override
    public String getAdapter() {
        return adapter;
    }

    @Override
    public void setAdapter(String adapter) {

        this.adapter = adapter;
    }

    @Override
    public Long getLength() {
        return length;
    }

    @Override
    public void setLength(Long length) {

        this.length = length;
    }

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public void setHash(String hash) {

        this.hash = hash;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {

        //this.path=path;
    }

    @Override
    public Long getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Long createTime) {

        this.createTime = createTime;
    }

    @Override
    public MD5InputStream downLoad() throws JDSException {
        return CtVfsFactory.getCtVfsService().downLoadByObjectId(this.getID());
    }

    @Override
    public Integer writeLine(String str) throws JDSException {
        return CtVfsFactory.getCtVfsService().writeLine(this.ID, str);
    }

    @Override
    public List<String> readLine(List<Integer> lineNums) throws JDSException {
        return CtVfsFactory.getCtVfsService().readLine(this.ID, lineNums);
    }

}
