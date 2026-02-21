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
public interface EIFileObjectListener extends java.util.EventListener {


	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void befaultUpLoad(FileObjectEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void upLoading(FileObjectEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void upLoadEnd(FileObjectEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void upLoadError(FileObjectEvent event) throws VFSException;


	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void share(FileObjectEvent event) throws VFSException;

	/**
	 * @param event
	 * @throws VFSException
	 */
	public void append(FileObjectEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void beforDownLoad(FileObjectEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void downLoading(FileObjectEvent event) throws VFSException;

	/**
	 *
	 * @param event
	 * @throws VFSException
	 */
	public void downLoadEnd(FileObjectEvent event) throws VFSException;


	/**
	 * 得到系统Code
	 * 
	 * @return
	 */
	public String getSystemCode();

}
