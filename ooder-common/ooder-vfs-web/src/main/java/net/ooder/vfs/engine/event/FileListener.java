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
public interface FileListener extends java.util.EventListener {

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void beforeCopy(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void create(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void upLoadError(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void upLoadEnd(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void upLoading(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void beforeUpLoad(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void beforeDownLoad(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void downLoading(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void downLoadEnd(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void beforeReName(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void reNameEnd(FileEvent event) throws VFSException;



	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void reStore(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void share(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void clear(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void open(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void send(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void deleteEnd(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void beforeDelete(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void moveEnd(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void beforeMove(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void updateEnd(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void beforeUpdate(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void save(FileEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void copyEnd(FileEvent event) throws VFSException;







	/**
	 * 得到系统Code
	 * 
	 * @return
	 */
	public String getSystemCode();

}
