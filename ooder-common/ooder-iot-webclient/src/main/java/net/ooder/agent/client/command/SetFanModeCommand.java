package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "SetFanMode",dataType=ContextType.Command, name = "设置空调风扇模式", expressionArr = "SetFanMode()", desc = "设置空调风扇模式")

public class SetFanModeCommand extends SensorCommand {


    String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public SetFanModeCommand() {
	super(CommandEnums.SetFanMode);
    }

}
