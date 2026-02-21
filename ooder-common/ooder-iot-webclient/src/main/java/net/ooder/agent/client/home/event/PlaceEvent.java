package net.ooder.agent.client.home.event;

import net.ooder.agent.client.iot.Place;
import net.ooder.agent.client.iot.enums.PlaceEventEnums;
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
public class PlaceEvent<T extends Place> extends HomeEvent<T> {

    private String sysCode;

    public static final String CONTEXT_DEVICE = "PlaceEvent.CONTEXT_DEVICE";

    public static final String CONTEXT_AREA = "PlaceEvent.CONTEXT_AREA";

    public static final String CONTEXT_ZNODE = "PlaceEvent.CONTEXT_ZNODE";

    /**
     * GetwayEvent
     * 
     * @param path
     * @param eventID
     */
    public PlaceEvent(T place, JDSClientService client, PlaceEventEnums eventID, String sysCode) {
	super(place, null);
	id = eventID;
	this.setClientService(client);
	this.sysCode = sysCode;
	
	

    }

    @Override
    public T getSource() {
        return super.getSource();
    }
    public String getSysCode() {
	return sysCode;
    }


    @Override
    public PlaceEventEnums getID() {
	return (PlaceEventEnums) id;
    }

}
