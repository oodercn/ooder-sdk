package net.ooder.agent.client.command;

import  net.ooder.common.JDSCommand;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.common.CommandEventEnums;

public class Command implements JDSCommand {

    CommandEnums command;

    String commandId;
    String gatewayieee;
    String systemCode;
    CommandEventEnums resultCode;

    public Command() {

    }

    public Command(CommandEnums command) {
        this.command = command;
    }

    public CommandEventEnums getResultCode() {
        return resultCode;
    }

    public void setResultCode(CommandEventEnums resultCode) {
        this.resultCode = resultCode;
    }

    public CommandEnums getCommand() {
        return command;
    }


    public void setCommand(CommandEnums command) {
        this.command = (CommandEnums) command;
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
