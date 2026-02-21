package net.ooder.agent.client.command;

import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;


@EsbBeanAnnotation(id = "OpenAlarm",dataType=ContextType.Command, name = "開啟报警", expressionArr = "OpenAlarmCommand()", desc = "開啟报警")
public class OpenAlarmCommand extends SensorCommand {
	public OpenAlarmCommand() {
		super(CommandEnums.ReleaseAlarm);
	}

}
