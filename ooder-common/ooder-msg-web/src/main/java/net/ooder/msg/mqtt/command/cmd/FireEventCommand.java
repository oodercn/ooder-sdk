/**
 * $RCSfile: FireEventCommand.java,v $
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
package net.ooder.msg.mqtt.command.cmd;

import net.ooder.common.DSMEvent;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.msg.mqtt.command.MQTTCommand;
import net.ooder.msg.mqtt.enums.MQTTCommandEnums;


@EsbBeanAnnotation(id = "FireEvent", name = "执行脚本", expressionArr = "FireEventCommand()", desc = "执行脚本")
public class FireEventCommand extends MQTTCommand {

    DSMEvent event;

    public FireEventCommand() {
        super(MQTTCommandEnums.FireEvent);

    }

    public FireEventCommand(DSMEvent event) {
        super(MQTTCommandEnums.FireEvent);
        this.event = event;

    }

    public DSMEvent getEvent() {
        return event;
    }

    public void setEvent(DSMEvent event) {
        this.event = event;
    }
}


