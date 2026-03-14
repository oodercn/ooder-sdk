package net.ooder.agent.client.home.event;

import net.ooder.agent.client.iot.ZNode;
import net.ooder.agent.client.iot.enums.GatewayEventEnums;
import  net.ooder.server.JDSClientService;

/**
 * <p>
 * Title: HOME管理系统
 * </p>
 * <p>
 * Description: 核心网关传感器事件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author  ooder
 * @version 1.0
 */
@SuppressWarnings("all")
public class GatewayEvent<T extends ZNode> extends HomeEvent<T> {
	
	public static final String CONTEXT_SENSORS = "GatewayEvent.CONTEXT_SENSOR";
	
	public static final String CONTEXT_COMMAND = "GatewayEvent.CONTEXT_COMMAND";


	/**
	 * GetwayEvent
	 * 
	 * @param path
	 * @param eventID
	 */
	public GatewayEvent(T znode, JDSClientService client, GatewayEventEnums eventID, String sysCode) {
		super(znode, null);
		id = eventID;
		this.systemCode = sysCode;
		this.setClientService(client);
		
	}


	@Override
	public T getSource() {
		return super.getSource();
	}




	@Override
	public GatewayEventEnums getID() {
	    return (GatewayEventEnums) id;
	}

}
