package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "Start",dataType=ContextType.Command, name = "开启", expressionArr = "Start()", desc = "开始")

public class StartCommand extends SensorCommand {

	public Integer step=1;

	public StartCommand() {
		super(CommandEnums.Start);
	}

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}
}
