
package net.ooder.vfs.engine.event;

import  net.ooder.vfs.enums.FolderEventEnums;

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
public class FolderEvent extends VFSEvent {

	public FolderEvent(String path, FolderEventEnums eventID, String sysCode) {
		super(path,null);
		id = eventID;
		this.systemCode=sysCode;
	}


	/**
	 * 取得触发此文件事件的实例
	 */
	public String getFolderPath() {
		return (String) getSource();
	}

	@Override
	public FolderEventEnums getID() {

	return (FolderEventEnums) id;
	}
}
