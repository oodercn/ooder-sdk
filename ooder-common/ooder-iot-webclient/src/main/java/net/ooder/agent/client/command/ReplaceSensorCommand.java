package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "ReplaceSensor",dataType=ContextType.Command, name = "替换传感器", expressionArr = "ReplaceSensorCommand()", desc = "替换传感器")

public class ReplaceSensorCommand extends SensorCommand implements ADCommand,NetCommand{
    @Override
    public String getFactory() {
        return "jds";
    }
    String oldsensorieee="008600000000000A";
    String newsensorieee="008600000000000B";

    public ReplaceSensorCommand() {
	super(CommandEnums.ReplaceSensor);
    }

    public String getNewsensorieee() {
	return newsensorieee;
    }

    public void setNewsensorieee(String newsensorieee) {
	this.newsensorieee = newsensorieee;
    }

    public String getOldsensorieee() {
	return oldsensorieee;
    }

    public void setOldsensorieee(String oldsensorieee) {
	this.oldsensorieee = oldsensorieee;
    }
}
