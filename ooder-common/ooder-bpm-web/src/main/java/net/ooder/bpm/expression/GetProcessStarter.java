/**
 * $RCSfile: GetProcessStarter.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:16 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.expression;

import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.process.ProcessInstAtt;
import net.ooder.common.expression.ParseException;
import net.ooder.common.expression.function.Function;
import net.ooder.context.JDSActionContext;
import net.ooder.annotation.EsbBeanAnnotation;

import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.Person;
import net.ooder.server.OrgManagerFactory;

import java.util.Stack;


@EsbBeanAnnotation(id = "processStarter", expressionArr = "GetProcessStarter($BPMC())", desc = "获取流程启动人")
public class GetProcessStarter extends Function {
    public GetProcessStarter() {
        numberOfParameters = 1;
    }

    public void run(Stack stack) throws ParseException {
        checkStack(stack); // check the stack
        stack.push(perform(stack.pop()));
    }

    /**
     * @return
     */
    private Person perform(Object test) {

        Person starter = null;
        String processInstId = (String) JDSActionContext.getActionContext().getParams("processInstId");
        String activityInstId = (String) JDSActionContext.getActionContext().getParams("activityInstId");
        try {
            WorkflowClientService client = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));
            ProcessInst processInst = null;
            if (processInstId != null && !processInstId.equals("")) {
                processInst = client.getProcessInst(processInstId);
            } else if (activityInstId != null && !activityInstId.equals("")) {
                processInst = client.getActivityInst(activityInstId).getProcessInst();
            }
            String personId = (String) processInst.getRightAttribute(ProcessInstAtt.PROCESS_INSTANCE_STARTER);
            if (personId != null) {
                starter = OrgManagerFactory.getOrgManager().getPersonByID(personId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return starter;

    }
}
