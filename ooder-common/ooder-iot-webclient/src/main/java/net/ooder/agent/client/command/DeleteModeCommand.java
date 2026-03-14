package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "DelMode",dataType=ContextType.Command, name = "删除MODEID", expressionArr = "DeleteModeCommand()", desc = "删除MODEID")

public class DeleteModeCommand extends SensorCommand {

	public String modeId="009BB";

	public String passId;

	public String getModeId() {
		return modeId;
	}

	public void setModeId(String modeId) {
		this.modeId = modeId;
	}

	public DeleteModeCommand() {
		super(CommandEnums.IRLearn);
	}

}
