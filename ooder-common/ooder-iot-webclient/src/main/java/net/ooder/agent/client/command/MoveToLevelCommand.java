package net.ooder.agent.client.command;

import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.ContextType;
import  net.ooder.agent.client.enums.CommandEnums;

@EsbBeanAnnotation(id = "movetolevel",dataType=ContextType.Command, name = "分级调节", expressionArr = "MoveToLevelCommand()", desc = "分级调节")
public class MoveToLevelCommand<T> extends OperatorCommand {
    public MoveToLevelCommand(OperationCommandTypeEnum type) {
        super(type, CommandEnums.movetolevel);
    }
    public MoveToLevelCommand() {
        super(OperationCommandTypeEnum.mainsOutLetOperation, CommandEnums.movetolevel);
    }
    OperationCommandParamEnums operation;

    public OperationCommandParamEnums getOperation() {
        return operation;
    }

    public void setOperation(OperationCommandParamEnums operation) {
        this.operation = operation;
    }

}
