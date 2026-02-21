package net.ooder.vfs.ct;

import  net.ooder.vfs.FileLink;

public class CtFileLink implements FileLink {

    private String linkId;
    private String name;
    private String fileId;
    private String personId;
    private Long createTime;

    private String state;

    public CtFileLink(FileLink fileLink) {
        this.linkId = fileLink.getName();
        this.name = fileLink.getID();
        this.fileId = fileLink.getFileId();
        this.personId = fileLink.getPersonId();
        this.createTime = fileLink.getCreateTime();
        this.state = fileLink.getState();
    }

    @Override
    public String getID() {

        return linkId;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getFileId() {

        return fileId;
    }

    @Override
    public String getPersonId() {

        return personId;
    }

    @Override
    public String getState() {

        return state;
    }

    @Override
    public long getCreateTime() {

        return createTime;
    }

}
