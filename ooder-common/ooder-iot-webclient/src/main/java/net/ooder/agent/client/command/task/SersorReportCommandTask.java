package net.ooder.agent.client.command.task;

import net.ooder.agent.client.command.Command;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.context.RunableActionContextImpl;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.home.client.CommandClient;
import  net.ooder.agent.client.home.ct.CtMsgDataEngine;
import  net.ooder.server.context.MinServerActionContextImpl;
import  net.ooder.common.ConfigCode;

import java.util.concurrent.Callable;

public class SersorReportCommandTask implements Callable<Command> {

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, SersorReportCommandTask.class);

    private MinServerActionContextImpl autoruncontext;
    private ConfigCode configCode;
    private String gatewayieee;

    public SersorReportCommandTask(String gatewayieee, String systemCode) {
        this.gatewayieee = gatewayieee;
        JDSContext context = JDSActionContext.getActionContext();
        this.autoruncontext = new MinServerActionContextImpl(context.getHttpRequest(), context.getOgnlContext());

        autoruncontext.setParamMap(context.getContext());
        this.configCode = autoruncontext.getConfigCode();
        if (context.getSessionId() != null) {
            autoruncontext.setSessionId(context.getSessionId());
            autoruncontext.getSession().put("sessionHandle", context.getSession().get("sessionHandle"));
        }
        autoruncontext.setSessionMap(context.getSession());
    }

    public Command call() {
        JDSActionContext.setContext(autoruncontext);
        Command msg = null;

        try {

            CtMsgDataEngine msgEngine = CtMsgDataEngine.getEngine(configCode);

            JDSSessionHandle handle = (JDSSessionHandle) autoruncontext.getSession().get("sessionHandle");
            JDSActionContext.setContext(new RunableActionContextImpl());
            JDSActionContext.getActionContext().getContext().put(JDSContext.SYSCODE, configCode.getType());

            CommandClient commandClient = msgEngine.getCommandClientByieee(gatewayieee);
            if (commandClient != null) {
                msg = commandClient.sendSensorReportCommand().get();
            }


        } catch (Exception e) {
            logger.error("SersorReportCommand:" + " error:" + e.getMessage());
            e.printStackTrace();
        }
        return msg;
    }

}
