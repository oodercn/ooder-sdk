package net.ooder.agent.client.command;

import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.annotation.EsbBeanAnnotation;

import java.util.ArrayList;
import java.util.List;

@EsbBeanAnnotation(id = "CreateScene", dataType=ContextType.Command,name = "创建场景", expressionArr = "CreateSceneCommand()", desc = "创建场景")

public class CreateSceneCommand extends Command implements SceneCommand, ADCommand, NetCommand {
    @Override
    public String getFactory() {
        return "jds";
    }

    String scenename = "openAll";

    String sceneid = "OA";


    CmdRule rule;

    int intvalue;


    List<OperatorCommand> cmds = new ArrayList<OperatorCommand>();
    List<OperatorCommand> groupCmds = new ArrayList<OperatorCommand>();
    List<OperatorCommand> sceneCmds = new ArrayList<OperatorCommand>();


    public int getIntvalue() {
        return intvalue;
    }

    public void setIntvalue(int intvalue) {
        this.intvalue = intvalue;
    }

    public CmdRule getRule() {
        return rule;
    }

    public void setRule(CmdRule rule) {
        this.rule = rule;
    }


    public String getSceneid() {
        return sceneid;
    }

    public void setSceneid(String sceneid) {
        this.sceneid = sceneid;
    }

    public String getScenename() {
        return scenename;
    }


    public void setScenename(String Scenename) {

        this.scenename = Scenename;
    }

    public List<OperatorCommand> getCmds() {
        return cmds;
    }

    public void setCmds(List<OperatorCommand> cmds) {
        this.cmds = cmds;
    }

    public List<OperatorCommand> getGroupCmds() {
        return groupCmds;
    }

    public void setGroupCmds(List<OperatorCommand> groupCmds) {
        this.groupCmds = groupCmds;
    }

    public List<OperatorCommand> getSceneCmds() {
        return sceneCmds;
    }

    public void setSceneCmds(List<OperatorCommand> sceneCmds) {
        this.sceneCmds = sceneCmds;
    }

    public CreateSceneCommand() {
        super(CommandEnums.CreateScene);
    }


}
