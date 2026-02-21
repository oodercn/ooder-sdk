package net.ooder.agent.client.command;

import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;


@EsbBeanAnnotation(id = "ClearPassword",dataType=ContextType.Command, name = "清空密码", expressionArr = "ClearPasswordCommand()", desc = "清空密码")
public class ClearPasswordCommand extends PasswordCommand {
    
    
	public ClearPasswordCommand(){
		super(CommandEnums.ClearPassword);
		this.setPassId(null);
		this.setStartTime(null);
		this.setEndTime(null);
	}
}
