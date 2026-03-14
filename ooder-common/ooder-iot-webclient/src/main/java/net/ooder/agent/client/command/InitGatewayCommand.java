/**
 * 
 */
package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

/**
 * @author wenzhang
 *
 */
@EsbBeanAnnotation(id = "InitGateway",dataType=ContextType.Command, name = "重新激活设备", expressionArr = "InitGatewayCommand()", desc = "重新激活设备")

public class InitGatewayCommand extends Command {

    public InitGatewayCommand() {
	super(CommandEnums.InitGateway);
    }

}
