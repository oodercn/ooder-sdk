package net.ooder.vfs.query;


import  net.ooder.common.ConditionKey;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 可以进行查询或排序的数据库字段。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @see VFSConditionKey
 * @author wenzhang li
 * @version 2.0
 */
public enum VFSConditionKey implements ConditionKey {


	FOLDER_ID ("VFS_FOLDER.ID"),

	FOLDER_UPDATETIME ("VFS_FOLDER.UPDATETIME"),

	FOLDER_NAME ("VFS_FOLDER.NAME"),

	FOLDER_FOLDERTYPE ("VFS_FOLDER.FOLDERTYPE"),

	FOLDER_DESCRIPTION ("VFS_FOLDER.DESCRIPTION"),

	FOLDER_ACTIVITY ("VFS_FOLDER.ACTIVITY"),

	FOLDER_PROCESSINSTID ("VFS_FOLDER.PROCESSINSTID"),

	FOLDER_PARENTID ("VFS_FOLDER.PARENTID"),

	FOLDER_PERSONID ("VFS_FOLDER.PERSONID"),

	FOLDER_CREATTIME ("VFS_FOLDER.CREATTIME"),



	FILEINFO_ID ("VFS_FILEINFO.ID"),

	FILEINFO_UPDATETIME ("VFS_FILEINFO.UPDATETIME"),

	FILEINFO_NAME ("VFS_FILEINFO.NAME"),

	FILEINFO_FILEINFOTYPE ("VFS_FILEINFO.FILEINFOTYPE"),

	FILEINFO_DESCRITION ("VFS_FILEINFO.DESCRITION"),

	FILEINFO_ACTIVITY ("VFS_FILEINFO.ACTIVITY"),

	FILEINFO_PROCESSINSTID ("VFS_FILEINFO.PROCESSINSTID"),

	FILEINFO_FOLDERID ("VFS_FILEINFO.FOLDERID"),

	FILEINFO_PERSONID ("VFS_FILEINFO.PERSONID"),

	FILEINFO_CREATTIME ("VFS_FILEINFO.CREATTIME");






	private VFSConditionKey(String conditionKey) {
		this.conditionKey = conditionKey;
	}

	private String conditionKey;

	public String toString() {
		return conditionKey;
	}

	@Override
	public String getValue() {
		return conditionKey;
	}
}
