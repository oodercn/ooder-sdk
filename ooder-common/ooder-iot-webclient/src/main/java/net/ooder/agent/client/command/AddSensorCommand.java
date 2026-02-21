package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "AddSensor",dataType=ContextType.Command, name = "添加传感器", expressionArr = "AddSensorCommand()", desc = "添加传感器")

public class AddSensorCommand extends SensorCommand implements NetCommand{

    public AddSensorCommand() {
	super(CommandEnums.AddSensor);
    }

}
