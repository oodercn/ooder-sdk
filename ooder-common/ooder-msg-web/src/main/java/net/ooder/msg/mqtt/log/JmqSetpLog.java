/**
 * $RCSfile: JmqSetpLog.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.msg.mqtt.log;

import net.ooder.client.JDSSessionFactory;
import net.ooder.common.JDSConstants;
import net.ooder.common.logging.ChromeProxy;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.LogSetpLog;
import net.ooder.common.util.StringUtility;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.msg.mqtt.client.JMQClient;
import net.ooder.msg.mqtt.command.cmd.ExecScriptCommand;
import net.ooder.server.JDSServer;

import java.net.URL;
import java.util.concurrent.ExecutionException;

public class JmqSetpLog implements ChromeProxy {
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, LogSetpLog.class);

    String sessionId;

    public JmqSetpLog(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void sendDownLoadCommand(String path) {
        String script = " ood.create(\"ood.UI.Div\").setLeft(\"0\").setTop(\"0\").setWidth(\"0\").setHeight(\"0\").setIframeAutoLoad('RAD/vfs/download?path=" + path + "').show()";
        execScript(script);
    }

    @Override
    public void printLog(String msg, boolean ismsg) {
        logger.info(msg);
        msg = StringUtility.escapeJSSpecial(msg);
        String script = "console.log('" + msg + "');";
        if (ismsg) {
            script = script + "ood.Debugger.log('" + msg + "');";
        }
        execScript(script);
    }

    @Override
    public void printError(String msg) {
        logger.error(msg);
        msg = StringUtility.escapeJSSpecial(msg);
        String script = "console.error('" + msg + "');";
        script = script + "ood.Debugger.err('" + msg + "');";
        execScript(script);
    }

    @Override
    public void printWarn(String msg) {
        logger.warn(msg);
        String script = "console.warn('" + msg + "');";
        script = script + "ood.Debugger.log('" + msg + "');";
        execScript(script);
    }

    @Override
    public void sendMsg(String msg) {
        logger.info(msg);
        String script = "ood.Debugger.log('" + msg + "');";
        execScript(script);
    }

    @Override
    public void execScript(String script) {
        JDSSessionHandle jdsSessionHandle = JDSSessionFactory.newSessionHandle(sessionId);

        try {
            ConnectInfo connectInfo = JDSServer.getInstance().getConnectInfo(jdsSessionHandle);
            if (connectInfo == null) {
                connectInfo = JDSServer.getInstance().getAdminClient().getConnectInfo();
            }
            JMQClient jmqClient = EsbUtil.parExpression(JMQClient.class);
            ExecScriptCommand execScript = new ExecScriptCommand(script);
            execScript.setGatewayieee(connectInfo.getLoginName());
            execScript = (ExecScriptCommand) jmqClient.sendCommand(execScript, 0).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void screenModule(String projectVersionName, String className, Integer deploy) {

    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void screenPage(URL url, Integer deploy) {

    }
}


