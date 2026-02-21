package net.ooder.vfs.engine.event;

import  net.ooder.vfs.enums.FileObjectEventEnums;

/**
 * <p>
 * Title: VFS管理系统
 * </p>
 * <p>
 * Description: 核心文件事件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2020
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author  ooder
 * @version 4.0
 */
@SuppressWarnings("all")
public class FileObjectEvent extends VFSEvent {



    private String hash;

    /**
     * FileEvent
     *
     * @param path
     * @param eventID
     */
    public FileObjectEvent(String hash, FileObjectEventEnums eventID, String sysCode) {
        super(hash, null);
        id = eventID;
        this.systemCode = sysCode;
    }


    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }


    @Override
    public FileObjectEventEnums getID() {
        return (FileObjectEventEnums) id;
    }

    /**
     * 取得触发此文件事件的实例
     */
    public String getFilePath() {
        return (String) getSource();
    }


}
