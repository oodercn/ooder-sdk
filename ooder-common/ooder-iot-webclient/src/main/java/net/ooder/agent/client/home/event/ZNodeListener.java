package net.ooder.agent.client.home.event;

import net.ooder.agent.client.iot.HomeException;

/**
 * <p>
 * Title: HOME管理系统
 * </p>
 * <p>
 * Description: zigbee网络事件监听器接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author  ooder
 * @version 2.0
 */
public interface ZNodeListener extends java.util.EventListener {



	/**
	 * 创建成功
	 * @param event
	 * @throws HomeException
	 */
	public void sensorCreated(ZNodeEvent event) throws HomeException;
	
	
	/**
	 * 移除成功
	 * @param event
	 * @throws HomeException
	 */
	public void znodeMoved(ZNodeEvent event) throws HomeException;
	
	/**
	 * 锁定
	 * @param event
	 * @throws HomeException
	 */
	public void sensorLocked(ZNodeEvent event) throws HomeException;
	
	
	/**
	 * 解锁
	 * @param event
	 * @throws HomeException
	 */
	public void sensorUnLocked(ZNodeEvent event) throws HomeException;
	

	/**
	 * 添加场景
	 * 
	 * @param event
	 * @throws HomeException
	 */
	public void sceneAdded(ZNodeEvent event) throws HomeException;


	/**
	 * 移除场景
	 * @param event
	 */
	public void sceneRemoved(ZNodeEvent event)throws HomeException;



}
