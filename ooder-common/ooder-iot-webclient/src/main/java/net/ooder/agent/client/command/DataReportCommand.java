package net.ooder.agent.client.command;

import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;

@EsbBeanAnnotation(id = "DataReport",dataType=ContextType.Command, name = "上报数据", expressionArr = "DataReportCommand()", desc = "上报数据")

public class DataReportCommand extends SensorCommand {

	public DataReportCommand() {
		super(CommandEnums.DataReport);
		
	}

}
