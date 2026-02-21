/**
 * $RCSfile: MQTTCommandEvent.java,v $
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

import net.ooder.common.CommandEventEnums;
import net.ooder.msg.mqtt.command.MQTTCommand;
import net.ooder.server.JDSClientService;

/**
 * <p>
 * Title: HOME管理系统
 * </p>
 * <p>
 * Description: 核心网关传感器事"
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
public class MQTTCommandEvent<T extends MQTTCommand> extends MQTTEvent<T> {

    public static final String CONTEXT_COMMAND = "CommandEvent.CONTEXT_COMMAND";


    public static final String RepeatCommandEvent = "$RepeatCommandEvent";


    private Integer errCode;

    /**
     * GetwayEvent
     *
     * @param path
     * @param eventID
     */
    public MQTTCommandEvent(T command, JDSClientService client, CommandEventEnums eventID, String sysCode) {
        super(command, null);
        id = eventID;
        this.systemCode = sysCode;
        this.setClientService(client);

    }

    @Override
    public CommandEventEnums getID() {
        return (CommandEventEnums) id;

    }


    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    @Override
    String getExpression() {
        return RepeatCommandEvent;
    }
}


