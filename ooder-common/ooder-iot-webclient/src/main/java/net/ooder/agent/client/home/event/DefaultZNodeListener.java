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
public class DefaultZNodeListener implements ZNodeListener {

	@Override
	public void sensorCreated(ZNodeEvent event) throws HomeException {

	}

	@Override
	public void znodeMoved(ZNodeEvent event) throws HomeException {

	}

	@Override
	public void sensorLocked(ZNodeEvent event) throws HomeException {

	}

	@Override
	public void sensorUnLocked(ZNodeEvent event) throws HomeException {

	}

	@Override
	public void sceneAdded(ZNodeEvent event) throws HomeException {

	}

	@Override
	public void sceneRemoved(ZNodeEvent event) throws HomeException {

	}
}
