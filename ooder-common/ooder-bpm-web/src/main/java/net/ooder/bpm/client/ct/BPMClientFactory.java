package net.ooder.bpm.client.ct;

import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.common.JDSException;
import net.ooder.server.JDSClientService;
import java.util.HashMap;
import java.util.Map;

public class BPMClientFactory {
    static Map<JDSClientService,WorkflowClientService> clientMap=new HashMap<JDSClientService,WorkflowClientService> ();

    public static WorkflowClientService getWorkflowClient(JDSClientService client) throws JDSException {


        WorkflowClientService workflowClientService=clientMap.get(client);
        if (workflowClientService==null){
            workflowClientService=new CtWorkflowClientServiceImpl(client);
            clientMap.put(client,workflowClientService);
        }

        return workflowClientService;
    }
}
