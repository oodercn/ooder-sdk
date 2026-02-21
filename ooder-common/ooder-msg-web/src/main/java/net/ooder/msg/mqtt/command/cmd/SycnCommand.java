/**
 * $RCSfile: SycnCommand.java,v $
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


import net.ooder.msg.mqtt.command.MQTTCommand;
import net.ooder.msg.mqtt.enums.MQTTCommandEnums;

public class SycnCommand extends MQTTCommand {

    String value="SycnCommand";

    public SycnCommand(MQTTCommandEnums command) {
	super(command);
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

}


