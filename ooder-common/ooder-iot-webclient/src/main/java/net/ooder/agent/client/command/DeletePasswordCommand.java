package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "DelPassword",dataType=ContextType.Command, name = "删除密码", expressionArr = "DeletePasswordCommand()", desc = "删除密码")
public class DeletePasswordCommand extends PasswordCommand {

    String clearAll;

    public DeletePasswordCommand() {
        super(CommandEnums.DelPassword);
        this.setStartTime(null);
        this.setEndTime(null);
    }

    public String getClearAll() {
        return clearAll;
    }

    public void setClearAll(String clearAll) {
        this.clearAll = clearAll;
    }
}
