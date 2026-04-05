/**
 * $RCSfile: ProcessListener.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:00 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.client.event;

import net.ooder.bpm.engine.BPMException;
import net.ooder.common.JDSListener;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程事件监听器接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang li
 * @version 1.0
 */
public interface ProcessListener extends JDSListener {

	/**
	 * 流程实例正在被启动
	 */
	public void processStarting(ProcessEvent event) throws BPMException;

	/**
	 * 流程实例已经被启动
	 */
	public void processStarted(ProcessEvent event) throws BPMException;

	/**
	 * 流程实例正在被保存
	 */
	public void processSaving(ProcessEvent event) throws BPMException;

	/**
	 * 流程实例已经被保存
	 */
	public void processSaved(ProcessEvent event) throws BPMException;

	/**
	 * 流程实例正在被挂起
	 */
	public void processSuspending(ProcessEvent event) throws BPMException;

	/**
	 * 流程实例已经被挂起
	 */
	public void processSuspended(ProcessEvent event) throws BPMException;

	/**
	 * 流程实例正在被恢复（从挂起状态）
	 */
	public void processResuming(ProcessEvent event) throws BPMException;

	/**
	 * 流程实例已经被恢复（从挂起状态）
	 */
	public void processResumed(ProcessEvent event) throws BPMException;

	/**
	 * 流程实例正在被取消
	 */
	public void processAborting(ProcessEvent event) throws BPMException;

	/**
	 * 流程实例已经被取消
	 */
	public void processAborted(ProcessEvent event) throws BPMException;

	/**
	 * 流程实例正在被完成
	 */
	public void processCompleting(ProcessEvent event) throws BPMException;

	/**
	 * 流程实例已经被完成
	 */
	public void processCompleted(ProcessEvent event) throws BPMException;

	/**
	 * 流程实例正在被删除
	 */
	public void processDeleting(ProcessEvent event) throws BPMException;

	/**
	 * 流程实例已经被删除
	 */
	public void processDeleted(ProcessEvent event) throws BPMException;
	
	
}
