package net.ooder.agent.client.command;

import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;

@EsbBeanAnnotation(id = "operation",dataType=ContextType.Command, name = "开关切换", expressionArr = "OperatorCommand()", desc = "开关切换")
public class OperatorCommand extends SensorCommand implements SceneCommand, GroupCommand {



    OperationCommandParamEnums operation = OperationCommandParamEnums.on;

    OperationCommandTypeEnum commandType;

    String value;

    String groupname;

    String groupid;

    String scenename;

    String sceneid;


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

    public OperatorCommand(OperationCommandTypeEnum operation) {
        super(CommandEnums.operation);
        this.commandType = operation;
    }

    public   OperatorCommand(){
        super(CommandEnums.operation);
        this.commandType =  OperationCommandTypeEnum.dimmableLightOperation;

    }

    public OperatorCommand(OperationCommandTypeEnum operation, CommandEnums command) {
        super(command);
        this.commandType = operation;
    }


    public OperationCommandTypeEnum getCommandType() {
        return commandType;
    }

    public void setCommandType(OperationCommandTypeEnum commandType) {
        this.commandType = commandType;
    }

    public OperationCommandParamEnums getOperation() {
        return operation;
    }

    public void setOperation(OperationCommandParamEnums operation) {
        this.operation = operation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
