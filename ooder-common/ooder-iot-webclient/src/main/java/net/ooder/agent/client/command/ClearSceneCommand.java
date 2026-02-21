package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "ClearScene",dataType=ContextType.Command, name = "清空场景", expressionArr = "ClearSceneCommand()", desc = "清空场景")

public class ClearSceneCommand extends Command implements SceneCommand, ADCommand, NetCommand {
    @Override
    public String getFactory() {
        return "jds";
    }

    String scenename;

    String sceneid;

    @Override
    public String getSceneid() {
        return sceneid;
    }

    @Override
    public void setSceneid(String sceneid) {
        this.sceneid = sceneid;
    }

    public String getScenename() {
        return scenename;
    }

    public void setScenename(String Scenename) {
        this.scenename = Scenename;
    }

    public ClearSceneCommand() {
        super(CommandEnums.ClearScene);
    }

}
