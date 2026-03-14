package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "ClusterCMD",dataType=ContextType.Command, name = "透传指令", expressionArr = "ClusterCMDCommand()", desc = "透传指令")

public class ClusterCMDCommand extends SensorCommand implements ADCommand, NetCommand{
	@Override
	public String getFactory() {
		return "jds";
	}
	String cmd="{\"command\":\"ClusterCMD\"}";

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public ClusterCMDCommand() {
		super(CommandEnums.ClusterCMD);
	}

}
