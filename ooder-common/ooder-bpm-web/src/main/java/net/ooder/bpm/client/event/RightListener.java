
package net.ooder.bpm.client.event;

import net.ooder.bpm.engine.BPMException;
import net.ooder.common.JDSListener;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动事件监听器接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang
 * @version 1.0
 */
public interface RightListener extends JDSListener {
	
	

	/**
	 * 发送
	 */
	public void routeTo(RightEvent event) throws BPMException;

	/**
	 * 抄送
	 */
	public void copyTo(RightEvent event) throws BPMException;
	
	
	/**
	 * 签收
	 */
	public void signReceive(RightEvent event) throws BPMException;
	
	/**
	 * 阅毕
	 */
	public void endRead(RightEvent event) throws BPMException;
	

	/**
	 * 退回
	 */
	public void routeBack(RightEvent event) throws BPMException;
	
	
	/**
	 * 收回
	 */
	public void tackBack(RightEvent event) throws BPMException;


	/**
	 * 更换办理人
	 */
	public void changePerformer(RightEvent event) throws BPMException;

}
