package net.ooder.vfs.engine.event;

import  net.ooder.vfs.enums.FileEventEnums;

/**
 * <p>
 * Title: VFS管理系统
 * </p>
 * <p>
 * Description: 核心文件事件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author  ooder
 * @version 2.0
 */
@SuppressWarnings("all")
public class FileEvent extends VFSEvent {




    /**
     * FileEvent
     * 
     * @param path
     * @param eventID
     */
    public FileEvent(String path, FileEventEnums eventID, String sysCode) {
	super(path, null);
	id = eventID;
	this.systemCode = sysCode;
    }

    @Override
    public FileEventEnums getID() {
	return (FileEventEnums) id;
    }

    /**
     * 取得触发此文件事件的实例
     */
    public String getFilePath() {
	return (String) getSource();
    }


}
