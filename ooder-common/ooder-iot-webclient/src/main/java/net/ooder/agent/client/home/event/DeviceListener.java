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
public interface DeviceListener extends java.util.EventListener {




	/**
	 * 开始注册
	 * @throws HomeException
	 */
	public void register(DeviceEvent event) throws HomeException;


	/**
	 *
	 * @param event
	 */
	public void deviceActivt(DeviceEvent event);


	/**
	 * 开始删除
	 * @param event
	 * @throws HomeException
	 */
	public void deleteing(DeviceEvent event) throws HomeException;
	
	/**
	 * 删除失败
	 * @param event
	 * @throws HomeException
	 */
	public void deleteFail(DeviceEvent event) throws HomeException;

	/**
	 * 正在绑定房间
	 * @param event
	 * @throws HomeException
	 */
	public void areaBind(DeviceEvent event) throws HomeException;

	/**
	 * 正在解绑房间
	 * @param event
	 * @throws HomeException
	 */
	public void areaUnBind(DeviceEvent event) throws HomeException;


	/**
	 * 上线通知
	 * @param event
	 * @throws HomeException
	 */
	public void onLine(DeviceEvent event) throws HomeException;
	
	/**
	 * 离线通知
	 * @param event
	 * @throws HomeException
	 */
	public void offLine(DeviceEvent event) throws HomeException;






}
