package net.ooder.agent.client.home.event;


import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.HomeException;

/**
 * <p>
 * Title: HOME管理系统
 * </p>
 * <p>
 * Description:  传感器监听器接口
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
public interface SensorListener extends java.util.EventListener {



	/**
	 * 添加到桌面
	 * @param event
	 * @return
	 * @throws HomeException
	 */
	public void addDesktop(SensorEvent event) throws HomeException;

	/**
	 * 移除桌面
	 * @param event
	 * @return
	 * @throws HomeException
	 */
	public void removeDesktop(SensorEvent event) throws HomeException;

	/**
	 * 添加到报警列表
	 *
	 * @param event
	 * @throws HomeException
	 */
	public void addAlarm(SensorEvent event) throws HomeException;


	/**
	 * 移除报警列表
	 * @param event
	 * @return
	 * @throws HomeException
	 */
	public void removeAlarm(SensorEvent event) throws HomeException;

	/**
	 * start
	 *
	 * @param event
	 * @throws HomeException
	 */
	public void start(SensorEvent event) throws HomeException;


	/**
	 * 关闭
	 *
	 * @param event
	 * @throws HomeException
	 */
	public void close(SensorEvent event) throws HomeException;

	/**
	 * 添加场景
	 * @param event
	 * @throws HomeException
	 */
	public void sceneAdded(DeviceEndPoint event) throws HomeException;

	/**
	 * 移除场景
	 * @param event
	 * @throws HomeException
	 */
	public void sceneRemoved(DeviceEndPoint event) throws HomeException;



}
