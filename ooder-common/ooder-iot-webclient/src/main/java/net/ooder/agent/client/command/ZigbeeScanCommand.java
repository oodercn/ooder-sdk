package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "ZigbeeScan",dataType=ContextType.Command, name = "查找设备", expressionArr = "ZigbeeScanCommand()", desc = "打开加网查找")

public class ZigbeeScanCommand extends Command implements NetCommand {

    public ZigbeeScanCommand() {
	super(CommandEnums.ZigbeeScan);
    }

}
