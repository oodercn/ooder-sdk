/*
 *@(#)FileVersionListener.java 2013-7-11
 *
 *Copyright (c) CSS-CA  LTD. All rights reserved. 
 */
package net.ooder.vfs.engine.event;

import  net.ooder.vfs.VFSException;

/**
 * 文件版本监听
 * 
 * @Author zhang xu
 * @Date <2013-7-11>
 * @version <1.0>
 * 
 */
public interface FileVersionListener extends java.util.EventListener {


	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void lockVersion(FileVersionEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void addFileVersion(FileVersionEvent event) throws VFSException;


	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void updateFileVersion(FileVersionEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void deleteFileVersion(FileVersionEvent event) throws VFSException;


	/**
	 * 得到系统Code
	 * 
	 * @return
	 */
	public String getSystemCode();
}
