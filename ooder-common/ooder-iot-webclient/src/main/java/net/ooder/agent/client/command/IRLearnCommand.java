package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "IRLearn",dataType=ContextType.Command, name = "红外学习", expressionArr = "IRLearnCommand()", desc = "红外学习")

public class IRLearnCommand extends SensorCommand {

	public String modeId="12";

	public String passId;

	public String getPassId() {
		return passId;
	}

	public void setPassId(String passId) {
		this.passId = passId;
	}

	public String getModeId() {
		return modeId;
	}

	public void setModeId(String modeId) {
		this.modeId = modeId;
	}

	public IRLearnCommand() {
		super(CommandEnums.IRLearn);
	}

}
