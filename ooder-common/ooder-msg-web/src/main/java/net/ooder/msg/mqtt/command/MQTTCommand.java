/**
 * $RCSfile: MQTTCommand.java,v $
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
package net.ooder.msg.mqtt.command;

import net.ooder.common.CommandEventEnums;
import net.ooder.common.JDSCommand;
import net.ooder.msg.mqtt.enums.MQTTCommandEnums;

public class MQTTCommand implements JDSCommand {

    MQTTCommandEnums command;
    String commandId;
    String gatewayieee;
    String systemCode;
    CommandEventEnums resultCode;

    public MQTTCommand() {

    }

    public MQTTCommand(MQTTCommandEnums command) {
        this.command = command;
    }

    public CommandEventEnums getResultCode() {
        return resultCode;
    }

    public void setResultCode(CommandEventEnums resultCode) {
        this.resultCode = resultCode;
    }

    public MQTTCommandEnums getCommand() {
        return command;
    }


    public void setCommand(MQTTCommandEnums command) {
        this.command = command;
    }


    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getGatewayieee() {
        return gatewayieee;
    }

    public void setGatewayieee(String gatewayieee) {
        this.gatewayieee = gatewayieee;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

}


