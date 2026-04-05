/**
 * $RCSfile: ActivityAdapter.java,v $
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



/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动事件监听器适配器
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author lxl,Andy
 * @version 1.0
 */
public class ActivityAdapter implements ActivityListener {

	/**
	 * 活动初始化完毕，进入inactive状态
	 */
	public void activityInited(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动开始执行路由操作
	 */
	public void activityRouting(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动完成路由操作
	 */
	public void activityRouted(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动开始被激活(进入active状态)
	 */
	public void activityActiving(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动完成激活(进入active状态)
	 */
	public void activityActived(ActivityEvent event) throws BPMException {
	}
	

	
	public void activityFormSaveed(ActivityEvent event) throws BPMException {

	}

	public void activityFormSaveing(ActivityEvent event) throws BPMException {
		
	}


	/**
	 * 活动开始执行路由分裂
	 */
	public void activitySpliting(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动已经分裂为多个活动实例
	 */
	public void activitySplited(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动开始执行合并操作
	 */
	public void activityJoining(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动已经完成合并操作
	 */
	public void activityJoined(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动开始跳转到其他流程上
	 */
	public void activityOutFlowing(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动已经跳转到其他流程上
	 */
	public void activityOutFlowed(ActivityEvent event) throws BPMException {
	}

	/**
	 * 外流活动开始返回
	 */
	public void activityOutFlowReturning(ActivityEvent event)
			throws BPMException {
	}

	/**
	 * 外流活动完成返回
	 */
	public void activityOutFlowReturned(ActivityEvent event)
			throws BPMException {
	}

	/**
	 * 活动开始挂起
	 */
	public void activitySuspending(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动已经挂起
	 */
	public void activitySuspended(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动开始恢复
	 */
	public void activityResuming(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动已经恢复
	 */
	public void activityResumed(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动开始完成
	 */
	public void activityCompleting(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动已经完成
	 */
	public void activityCompleted(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动开始收回
	 */
	public void activityTakebacking(ActivityEvent event) throws BPMException {
	}

	/**
	 * 活动已经收回
	 */
	public void activityTakebacked(ActivityEvent event) throws BPMException {
	}
	/**
	 * 活动开始展示
	 */
	public void activityDisplay(ActivityEvent event) throws BPMException {
	
	}

	
	

}
