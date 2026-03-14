package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "RemoveGroup", dataType=ContextType.Command,name = "移除分租", expressionArr = "RemoveGroupCommand()", desc = "移除分租")

public class RemoveGroupCommand extends Command implements ADCommand, GroupCommand, NetCommand {
    @Override
    public String getFactory() {
        return "jds";
    }

    String groupname = "light";

    String groupid = "OA";

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public RemoveGroupCommand() {
        super(CommandEnums.RemoveGroup);
    }

}
