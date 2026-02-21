package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "FindSensor", dataType=ContextType.Command,name = "查找传感器", expressionArr = "FindSensorCommand()", desc = "查找传感器")

public class FindSensorCommand extends SensorCommand implements NetCommand{

	public FindSensorCommand() {
		super(CommandEnums.FindSensor);
	}

}
