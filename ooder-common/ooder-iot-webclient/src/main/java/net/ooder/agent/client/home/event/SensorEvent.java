package net.ooder.agent.client.home.event;

import net.ooder.agent.client.iot.ZNode;
import net.ooder.agent.client.iot.enums.SensorEventEnums;
import  net.ooder.server.JDSClientService;

/**
 * <p>
 * Title: HOME管理系统
 * </p>
 * <p>
 * Description: 核心传感器事件
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
public class SensorEvent<T extends ZNode> extends HomeEvent<T> {

    public static final String CONTEXT_ALARM = "SensorEvent.CONTEXT_ALARM";
    public static final String CONTEXT_SCENE = "SensorEvent.CONTEXT_SCENE";

    private String sysCode;

    /**
     * GetwayEvent
     *
     * @param path
     * @param eventID
     */
    public SensorEvent(T znode, JDSClientService client, SensorEventEnums eventID, String sysCode) {
        super(znode, null);
        id = eventID;
        this.sysCode = sysCode;
        this.setClientService(client);

    }


    @Override
    public T getSource() {
        return super.getSource();
    }
    public String getSysCode() {
        return sysCode;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }

    @Override
    public SensorEventEnums getID() {
        return (SensorEventEnums) id;
    }

}
