package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "ChannelNegotiateReport",dataType=ContextType.Command, name = "上报组网信息", expressionArr = "ChannelNegotiateReportCommand()", desc = "上报组网信息")

public class ChannelNegotiateReportCommand extends Command implements ADCommand {

    public ChannelNegotiateReportCommand() {
	super(CommandEnums.ChannelNegotiateReport);
    }

    @Override
    public String getFactory() {
        return "jds";
    }
}
