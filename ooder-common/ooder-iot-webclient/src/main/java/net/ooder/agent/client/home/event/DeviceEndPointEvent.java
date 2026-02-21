package net.ooder.agent.client.home.event;

import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.enums.DeviceEndPointEventEnums;
import  net.ooder.server.JDSClientService;

/**
 * <p>
 * Title: HOME管理系统
 * </p>
 * <p>
 * Description: 设备传感器事件 包含，网关以及传感器相关的事件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author  ooder
 * @version 1.0
 */
@SuppressWarnings("all")
public class DeviceEndPointEvent<T extends DeviceEndPoint> extends HomeEvent<T> {

    public static final String CONTEXT_BINDSENSORS = "DEVICEEVENT.CONTEXT_SENSORS";

    public static final String CONTEXT_MSG = "DEVICEEVENT.CONTEXT_MSG";

    public static final String CONTEXT_ATT_KEY = "DEVICEEVENT.CONTEXT_ATT_KEY";

    public static final String CONTEXT_ATT_VALUE = "DEVICEEVENT.CONTEXT_ATT_VALUE";


    /**
     * DeviceEvent
     *
     * @param path
     * @param eventID
     */
    public DeviceEndPointEvent(T device, JDSClientService client, DeviceEndPointEventEnums eventID, String sysCode) {
        super(device, null);
        id = eventID;
        this.systemCode = sysCode;
        this.setClientService(client);


    }

    @Override
    public T getSource() {
        return super.getSource();
    }




    @Override
    public DeviceEndPointEventEnums getID() {
        return (DeviceEndPointEventEnums) id;
    }

}
