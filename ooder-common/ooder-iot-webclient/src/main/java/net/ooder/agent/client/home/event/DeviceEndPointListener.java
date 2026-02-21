package net.ooder.agent.client.home.event;


import net.ooder.agent.client.iot.HomeException;

/**
 * <p>
 * Title: HOME管理系统
 * </p>
 * <p>
 * Description: 核心事件监听器接口
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
public interface DeviceEndPointListener extends java.util.EventListener {


	/**
	 * 更新EP信息
	 * @throws HomeException
	 */
	public void updateInfo(DeviceEndPointEvent event) throws HomeException;


	/**
	 * 开始绑定
	 * @throws HomeException
	 */
	public void bind(DeviceEndPointEvent event) throws HomeException;
	

	/**
	 * 绑定成功
	 * @throws HomeException
	 */
	public void bindSuccess(DeviceEndPointEvent event) throws HomeException;


	/**
	 * 失败
	 * @param event
	 * @throws HomeException
	 */
	public void bindFail(DeviceEndPointEvent event) throws HomeException;
	/**
	 * 绑定失败
	 * @param event
	 * @throws HomeException
	 */
	public void unbind(DeviceEndPointEvent event) throws HomeException;

	
	/**
	 * 开始解绑
	 * @param event
	 * @throws HomeException
	 */
	public void unbindSuccess(DeviceEndPointEvent event) throws HomeException;
	
	/**
	 * 解绑失败
	 * @param event
	 * @throws HomeException
	 */
	public void unbindFail(DeviceEndPointEvent event) throws HomeException;
	




	/**
	 * 锁定
	 * @param event
	 * @throws HomeException
	 */
	public void locked(DeviceEndPointEvent event) throws HomeException;

	/**
	 * 解锁
	 * @param event
	 * @throws HomeException
	 */
	public void unLocked(DeviceEndPointEvent event) throws HomeException;

	/**
	 * 新增节点应用
	 * @param event
	 * @throws HomeException
	 */
	public void createEndPoint(DeviceEndPointEvent event) throws HomeException;

	/**
	 * 移除节点应用
	 * @param event
	 * @throws HomeException
	 */
	public void removeEndPoint(DeviceEndPointEvent event) throws HomeException;
}
