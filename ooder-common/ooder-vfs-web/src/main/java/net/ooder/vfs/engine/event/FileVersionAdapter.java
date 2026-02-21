/*
 *@(#)FileVersionAdapter.java 2013-7-11
 *
 *Copyright (c) CSS-CA  LTD. All rights reserved. 
 */
package net.ooder.vfs.engine.event;

import  net.ooder.vfs.VFSException;

/**
 * 文件版本适配器
 * 
 * @Author zhang xu
 * @Date <2013-7-11>
 * @version <1.0>
 * 
 */


public class FileVersionAdapter implements FileVersionListener {


	@Override
	public void lockVersion(FileVersionEvent event) throws VFSException {

	}

	@Override
	public void addFileVersion(FileVersionEvent event) throws VFSException {

	}

	@Override
	public void updateFileVersion(FileVersionEvent event) throws VFSException {

	}

	@Override
	public void deleteFileVersion(FileVersionEvent event) throws VFSException {

	}

	/**
	 * 得到系统Code
	 * 
	 * @return
	 */
	public String getSystemCode() {
		return "";
	}

}
