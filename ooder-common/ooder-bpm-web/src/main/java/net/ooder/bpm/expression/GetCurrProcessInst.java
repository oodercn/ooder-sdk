package net.ooder.bpm.expression;

import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.context.JDSActionContext;
import net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "currProcessInst", expressionArr = "GetCurrProcessInst($BPMC())", desc = "获取当前流程实例")
public class GetCurrProcessInst extends AbstractFunction {
    public ProcessInst perform(WorkflowClientService clientService) throws BPMException {
        ProcessInst processInst = null;
        String processInstId = (String) JDSActionContext.getActionContext().getParams("processInstId");
        if (processInstId != null && !processInstId.equals("")) {
            processInst = clientService.getProcessInst(processInstId);
        }else {
            String activityInstId = (String) JDSActionContext.getActionContext().getParams("activityInstId");
            processInst = clientService.getActivityInst(activityInstId).getProcessInst();
        }
        return processInst;
    }
}
