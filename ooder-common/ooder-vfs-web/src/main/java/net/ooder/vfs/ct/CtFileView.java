package net.ooder.vfs.ct;

import  net.ooder.common.JDSException;
import  net.ooder.vfs.FileObject;
import  net.ooder.vfs.FileView;

public class CtFileView implements FileView {

    private String viewId;
    private String versionId;
    private int fileType;
    private int fileIndex;
    private String fileObjectId;
    private String name;
    private String path;

    public CtFileView(FileView fileView) {
        viewId = fileView.getID();
        versionId = fileView.getVersionId();
        fileType = fileView.getFileType();
        fileObjectId = fileView.getFileObjectId();
        name = fileView.getName();
        path = fileView.getPath();
        this.fileIndex = fileView.getFileIndex();
    }

    @Override
    public String getID() {

        return viewId;
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
    public int getFileType() {

        return fileType;
    }

    @Override
    public void setFileType(int fileType) {
        this.fileType = fileType;

    }

    @Override
    public int getFileIndex() {
        return fileIndex;
    }

    @Override
    public void setFileIndex(int fileIndex) {

        this.fileIndex = fileIndex;
    }

    @Override
    public String getVersionId() {

        return versionId;
    }

    @Override
    public void setVersionId(String versionId) {
        this.versionId = versionId;

    }

    @Override
    public FileObject getFileObject() {
        FileObject fileObject = null;
        try {
            fileObject = CtVfsFactory.getCtVfsService().getFileObjectById(fileObjectId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return fileObject;

    }

    @Override
    public void setFileObjectId(String fileObjectId) {

        this.fileObjectId = fileObjectId;
    }

    @Override
    public String getFileObjectId() {
        return fileObjectId;
    }

    @Override
    public String getPath() {
        if (this.getFileObject() != null) {
            this.path = this.getFileObject().getPath();
        }

        return this.path;
    }


}
