/*
 *@(#)FileVersionEvent.java 2013-7-11
 *
 *Copyright (c) CSS-CA  LTD. All rights reserved. 
 */
package net.ooder.vfs.engine.event;

import  net.ooder.vfs.enums.FileVersionEventEnums;

/**
 * 文件版本事件对象
 * 
 * @Author zhang xu
 * @Date <2013-7-11>
 * @version <1.0>
 * 
 */
@SuppressWarnings("all")
public class FileVersionEvent extends VFSEvent {



	String path;



	/**
	 * 构造函数

	 * 
	 * @param inst

	 * @param eventID
	 */
	public FileVersionEvent(String path, FileVersionEventEnums eventID, String configCode) {
		super(path, null);
		id = eventID;
		this.path=path;
		this.systemCode=configCode;
	}


	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * 取得触发此文件版本的实例
	 */
	@Override
	public FileVersionEventEnums getID() {
	    return (FileVersionEventEnums) id;
	}

}
