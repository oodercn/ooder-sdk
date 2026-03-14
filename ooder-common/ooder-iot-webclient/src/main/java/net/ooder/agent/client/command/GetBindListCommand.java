package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "GetBindList",dataType=ContextType.Command, name = "获取绑定列表", expressionArr = "GetBindListCommand()", desc = "获取绑定列表")
public class GetBindListCommand extends SensorCommand implements NetCommand{

	public GetBindListCommand() {
		super(CommandEnums.GetBindList);
	}

}
