package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "SetSystemMode",dataType=ContextType.Command, name = "设置空调模式", expressionArr = "SetSystemMode()", desc = "设置空调模式")

public class SetSystemModeCommand extends SensorCommand {

    String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public SetSystemModeCommand() {
	super(CommandEnums.SetSystemMode);
    }

}
