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
public interface GatewayListener extends java.util.EventListener {

	
	

	/**
	 * 开始添加传感器
	 * @param event
	 * @return
	 * @throws HomeException
	 */
	public void gatewayOnLine(GatewayEvent event) throws HomeException;
	
	/**
	 * 结束添加传感器
	 * @param event
	 * @return
	 * @throws HomeException
	 */
	public void gatewayOffLine(GatewayEvent event) throws HomeException;

	
	
	/**
	 * 开始添加传感器
	 * @param event
	 * @return
	 * @throws HomeException
	 */
	public void sensorAdding(GatewayEvent event) throws HomeException;
	
	/**
	 * 结束添加传感器
	 * @param event
	 * @return
	 * @throws HomeException
	 */
	public void sensorAdded(GatewayEvent event) throws HomeException;

	
	
	
	/**
	 * 结束分享网关
	 * @param event
	 * @return
	 * @throws HomeException
	 */
	public void gatewaySharing(GatewayEvent event) throws HomeException;

	

	/**
	 * 结束分享网关
	 * @param event
	 * @return
	 * @throws HomeException
	 */
	public void gatewayShared(GatewayEvent event) throws HomeException;

	/**
	 * 暂停分享网关
	 * @param event
	 * @return
	 * @throws HomeException
	 */
	public void stopGatewayShared(GatewayEvent event) throws HomeException;


	/**
	 * 开始删除传感器
	 * 
	 * @param events
	 * @throws HomeException
	 */
	public void sensorRemoving(GatewayEvent event) throws HomeException;


	/**
	 * 传感器完成删除
	 * 
	 * @param event
	 * @throws HomeException
	 */
	public void sensorRemoved(GatewayEvent event) throws HomeException;

	
	/**
	 * 网关锁定
	 * 
	 * @param event
	 * @throws HomeException
	 */
	public void gatewayLocked(GatewayEvent event) throws HomeException;

	/**
	 * 网关解锁
	 * 
	 * @param event
	 * @throws HomeException
	 */
	public void gatewayUnLocked(GatewayEvent event) throws HomeException;

	

	/**
	 * 网关账户绑定
	 * 
	 * @param event
	 * @throws HomeException
	 */
	public void accountBind(GatewayEvent event) throws HomeException;

	/**
	 * 网关账户解绑
	 * 
	 * @param event
	 * @throws HomeException
	 */
	public void accountUNBind(GatewayEvent event) throws HomeException;


	/**
	 * 得到系统Code
	 * 
	 * @return
	 */
	public String getSystemCode();

}
