package net.ooder.agent.client.home.engine;

import com.alibaba.fastjson.JSONObject;
import  net.ooder.cluster.udp.ClusterCommand;
import  net.ooder.common.JDSCommand;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.expression.function.AbstractFunction;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.JDSServer;
import  net.ooder.common.ConfigCode;

//@EsbBeanAnnotation(id = "RepeatCommand", name = "转发UDP命令", flowType = EsbFlowType.msgRepeat, expressionArr = "RepeatCommand(command)", desc = "转发UDP命令", dataType = "action")
public class RepeatCommand extends AbstractFunction {
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, RepeatCommand.class);
    public Boolean perform(ClusterCommand command) {
        try {
            String commandStr = command.getCommand();
            if (CommandEnums.fromByName(commandStr) != null) {
                JDSCommand jdscommand = (JDSCommand) JSONObject.parseObject(command.getCommandJson(), CommandEnums.fromByName(commandStr).getCommand());
                JDSSessionHandle handle = command.getSessionHandle();
                ConfigCode systemCode = JDSServer.getInstance().getCurrServerBean().getConfigCode();
                JDSClientService client = JDSServer.getInstance().getJDSClientService(handle, systemCode);
                if (client.getConnectInfo() != null && client.getConnectionHandle().isconnect()) {
                    client.getConnectionHandle().send(jdscommand);
                }
            } else {
                logger.error("[" + command.getCommand() + "] not a suport command!");
            }

        } catch (JDSException e) {
            e.printStackTrace();
        }
        return true;

    }

}
