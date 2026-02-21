package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "Stop",dataType=ContextType.Command, name = "停止", expressionArr = "Stop()", desc = "停止")

public class StopCommand extends SensorCommand {

	public StopCommand() {
		super(CommandEnums.Stop);
	}


}
