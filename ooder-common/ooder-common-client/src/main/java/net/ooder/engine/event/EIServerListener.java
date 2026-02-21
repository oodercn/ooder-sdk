/**
 * $RCSfile: EIServerListener.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:26:02 $
 *
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 *
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: EIServerListener.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.engine.event;

import net.ooder.common.JDSException;

/**
 * <p>
 * Title: JDS管理系统
 * </p>
 * <p>
 * Description: 核心服务器事件监听器接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author wenzhang li
 * @version 1.0
 */
public interface EIServerListener extends java.util.EventListener {

	/**
	 * 服务器正在启动
	 */
	public void serverStarting(EIServerEvent event) throws JDSException;

	/**
	 * 服务器已启动
	 */
	public void serverStarted(EIServerEvent event) throws JDSException;

	/**
	 * 服务器正在停止
	 */
	public void serverStopping(EIServerEvent event) throws JDSException, InterruptedException;

	/**
	 * 服务器已停止
	 */
	public void serverStopped(EIServerEvent event) throws JDSException;
	

	
	/**
	 * 正在保存子系统
	 */
	public void systemSaving(EIServerEvent event) throws JDSException;
	
	/**
	 * 保存子系统完成
	 */
	public void systemSaved(EIServerEvent event) throws JDSException;
	
	/**
	 * 正在删除子系统
	 */
	public void systemDeleting(EIServerEvent event) throws JDSException;
	
	
	/**
	 * 子系统删除完成
	 */
	public void systemDeleted(EIServerEvent event) throws JDSException;

	/**
	 * 正在激活子系统
	 */
	public void systemActivating(EIServerEvent event) throws JDSException;
	

	/**
	 * 子系统激活完成
	 */
	public void systemActivated(EIServerEvent event) throws JDSException;
	
	/**
	 * 正在冻结子系统
	 */
	public void systemFreezing(EIServerEvent event) throws JDSException;
	
	/**
	 * 冻结子系统完成
	 */
	public void systemFreezed(EIServerEvent event) throws JDSException;
	
}
