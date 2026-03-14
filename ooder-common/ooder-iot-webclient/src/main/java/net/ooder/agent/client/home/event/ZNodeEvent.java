package net.ooder.agent.client.home.event;

import net.ooder.agent.client.iot.ZNode;
import net.ooder.agent.client.iot.enums.ZnodeEventEnums;
import  net.ooder.server.JDSClientService;

/**
 * <p>
 * Title: GW管理系统
 * </p>
 * <p>
 * Description: 核心Zigbee事件
 * </p>
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * <p>
 * Company: www.bjgreenlive.com
 * </p>
 *
 * @author  ooder
 * @version 1.0
 */
@SuppressWarnings("all")
public class ZNodeEvent<T extends ZNode> extends HomeEvent<T> {

    public static final String CONTEXT_SENSOR = "GatewayEvent.CONTEXT_SENSOR";

    public static final String CONTEXT_CTX = "GatewayEvent.CONTEXT_CTX";

    /**
     * GetwayEvent
     *
     * @param path
     * @param eventID
     */
    public ZNodeEvent(T znode, JDSClientService client, ZnodeEventEnums eventID, String sysCode) {
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
    public ZnodeEventEnums getID() {
        return (ZnodeEventEnums) id;
    }

}
