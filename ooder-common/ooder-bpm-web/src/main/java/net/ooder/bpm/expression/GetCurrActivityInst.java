package net.ooder.bpm.expression;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.context.JDSActionContext;
import net.ooder.annotation.EsbBeanAnnotation;

@EsbBeanAnnotation(id = "currActivityInst", expressionArr = "GetCurrActivityInst($BPMC())", desc = "获取当前活动实例")
public class GetCurrActivityInst extends AbstractFunction {
    public ActivityInst perform(WorkflowClientService clientService) throws BPMException {
        ActivityInst activityInst = null;
        String activityInstId = (String) JDSActionContext.getActionContext().getParams("activityInstId");
        if (activityInstId != null && !activityInstId.equals("")) {
            activityInst = clientService.getActivityInst(activityInstId);
        }
        return activityInst;
    }
}
