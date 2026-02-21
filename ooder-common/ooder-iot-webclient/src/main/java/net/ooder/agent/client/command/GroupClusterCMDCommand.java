package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "GroupClusterCMD", dataType=ContextType.Command,name = "透传组指令", expressionArr = "GroupClusterCMDCommand()", desc = "透传指令")

public class GroupClusterCMDCommand extends Command implements ADCommand {
    @Override
    public String getFactory() {
        return "jds";
    }

    String groupname = "light";
    String groupid = "OA";

    OperatorCommand cmd = new OperatorCommand();

    public SensorCommand getCmd() {
        return cmd;
    }

    public void setCmd(OperatorCommand cmd) {
        this.cmd = cmd;
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

    public GroupClusterCMDCommand() {
        super(CommandEnums.ClusterCMD);
    }

}
