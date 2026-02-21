/**
 * $RCSfile: P2PEvent.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.msg.mqtt.event;

import net.ooder.msg.Msg;
import net.ooder.msg.mqtt.enums.P2PEnums;
import net.ooder.server.JDSClientService;

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
public class P2PEvent<T extends Msg> extends MQTTEvent<T> {

    public static final String CONTEXT_P2P = "P2PEvent.CONTEXT_P2P";

    public static final String  RepeatMqttEvent = "$RepeatMqttEvent";

    private Integer errCode;

    /**
     * GetwayEvent
     *
     * @param path
     * @param eventID
     */
    public P2PEvent(T msg, JDSClientService client, P2PEnums eventID, String sysCode) {
        super(msg, null);
        id = eventID;
        this.systemCode = sysCode;
        this.setClientService(client);

    }

    @Override
    public P2PEnums getID() {
        return (P2PEnums) id;

    }


    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    @Override
    String getExpression() {
        return RepeatMqttEvent;
    }
}


