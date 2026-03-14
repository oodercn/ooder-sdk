package net.ooder.agent.client.command;

import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;

@EsbBeanAnnotation(id = "IdentifyDevice", dataType=ContextType.Command,name = "设备定位", expressionArr = "IdentifyCommand()", desc = "设备定位")
public class IdentifyCommand extends SensorCommand {

    String operation;
    String commandType;
    String value;

    public IdentifyCommand() {
	super(CommandEnums.IdentifyDevice);
    }

    public String getCommandType() {
	return commandType;
    }

    public void setCommandType(String commandType) {
	this.commandType = commandType;
    }

    public String getOperation() {
	return operation;
    }

    public void setOperation(String operation) {
	this.operation = operation;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }
}
