package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

import java.util.List;

@EsbBeanAnnotation(id = "CreateGroup",dataType=ContextType.Command, name = "创建分组", expressionArr = "CreateGroupCommand()", desc = "创建分租")

public class CreateGroupCommand extends Command implements GroupCommand, ADCommand, NetCommand {
    @Override
    public String getFactory() {
        return "jds";
    }

    String groupname = "Light";
    String groupid = "OA";
    List<String> sensorieees;
    public List<String> getSensorieees() {
        return sensorieees;
    }

    public void setSensorieees(final List<String> sensorieees) {
        this.sensorieees = sensorieees;
    }

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

    public CreateGroupCommand() {
        super(CommandEnums.CreateGroup);
    }

}
