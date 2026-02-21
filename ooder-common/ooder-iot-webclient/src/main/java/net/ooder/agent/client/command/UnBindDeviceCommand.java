package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "unbindDevice",dataType=ContextType.Command, name = "解除绑定", expressionArr = "UnBindDeviceCommand()", desc = "解除绑定")

public class UnBindDeviceCommand extends BindCommand implements NetCommand, GroupCommand, SceneCommand {

    public UnBindDeviceCommand() {
        super(CommandEnums.unbindDevice);
    }

    String groupname;

    String groupid;

    String scenename;

    String sceneid;

    @Override
    public String getGroupname() {
        return groupname;
    }

    @Override
    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    @Override
    public String getGroupid() {
        return groupid;
    }

    @Override
    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    @Override
    public String getScenename() {
        return scenename;
    }

    @Override
    public void setScenename(String scenename) {
        this.scenename = scenename;
    }

    @Override
    public String getSceneid() {
        return sceneid;
    }

    @Override
    public void setSceneid(String sceneid) {
        this.sceneid = sceneid;
    }

}
