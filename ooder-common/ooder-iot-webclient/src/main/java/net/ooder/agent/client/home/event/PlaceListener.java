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
public interface PlaceListener extends java.util.EventListener {



	/**
	 * 结束添加住所
	 * @param event 
	 * @throws HomeException
	 */
	public void placeCreate(PlaceEvent event) throws HomeException;

	/**
	 * 开始移除住所
	 * @param event 
	 * @throws HomeException
	 */
	public void placeRemove(PlaceEvent event) throws HomeException;


	
	/**
	 * 添加房间
	 * @param event
	 * @throws HomeException
	 */
	public void areaAdd(PlaceEvent event) throws HomeException;



	
	/**
	 * 移除房间
	 * @param event
	 * @throws HomeException
	 */
	public void areaRemove(PlaceEvent event) throws HomeException;
	
	/**
	 * 网关添加
	 * @param event
	 * @throws HomeException
	 */
	public void gatewayAdd(PlaceEvent event) throws HomeException;
	
	

	/**
	 * 移除网关
	 * @param event 
	 * @throws HomeException
	 */
	public void gatewayRemove(PlaceEvent event) throws HomeException;


}
