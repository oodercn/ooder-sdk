package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "InitFactory",dataType=ContextType.Command, name = "运行出厂设置", expressionArr = "InitFactoryCommand()", desc = "运行出厂设置")

public class InitFactoryCommand extends Command {

    public InitFactoryCommand() {
	super(CommandEnums.InitFactory);
    }

}
