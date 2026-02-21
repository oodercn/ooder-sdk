package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;


@EsbBeanAnnotation(id = "ClearData",dataType=ContextType.Command, name = "清空数据", expressionArr = "ClearDataCommand()", desc = "清空数据")

public class ClearDataCommand extends SensorCommand {

	public ClearDataCommand() {
		super(CommandEnums.ClearData);
	}

}
