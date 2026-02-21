package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;


@EsbBeanAnnotation(id = "LevelNetWork", dataType=ContextType.Command,name = "设备离网", expressionArr = "LevelNetWorkCommand()", desc = "设备离网")

public class LevelNetWorkCommand extends SensorCommand implements NetCommand{

	public LevelNetWorkCommand() {
		super(CommandEnums.LevelNetWork);
	}

}
