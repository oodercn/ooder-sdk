package net.ooder.vfs.engine.event;

import  net.ooder.vfs.VFSException;

/**
 * <p>
 * Title: VFS文件管理系统
 * </p>
 * <p>
 * Description: 核心文件事件监听器接口
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
public interface EIFolderListener extends java.util.EventListener {

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void create(FolderEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void lock(FolderEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void beforeReName(FolderEvent event) throws VFSException;


	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void reNameEnd(FolderEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void beforeDelete(FolderEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void deleteing(FolderEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void deleteEnd(FolderEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void save(FolderEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void beforeCopy(FolderEvent event) throws VFSException;


	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void copying(FolderEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void copyEnd(FolderEvent event) throws VFSException;


	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void beforeMove(FolderEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void moving(FolderEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void moveEnd(FolderEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void beforeClean(FolderEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void cleanEnd(FolderEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void reStore(FolderEvent event) throws VFSException;




	/**
	 * 得到系统Code
	 * 
	 * @return
	 */
	public String getSystemCode();

}
