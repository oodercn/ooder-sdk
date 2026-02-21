/**
 * $RCSfile: JDSLocalServer.java,v $
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
package net.ooder.server;

import net.ooder.client.JDSSessionFactory;
import net.ooder.common.ConfigCode;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.context.JDSContext;
import net.ooder.context.RunableActionContextImpl;
import net.ooder.engine.ConnectionHandle;

public class JDSLocalServer {
    static JDSLocalServer instance;

    public static final String THREAD_LOCK = "Thread Lock";

    private static final Log logger = LogFactory.getLog(
            JDSConstants.CONFIG_KEY, JDSLocalServer.class);


    /**
     * 取得JDSServer服务器的单例实现
     *
     * @return
     * @throws JDSException
     */
    public static JDSLocalServer getInstance() throws JDSException {
        if (instance == null) {
            synchronized (THREAD_LOCK) {
                if (instance == null) {
                    instance = new JDSLocalServer();
                }
            }
        }

        return instance;
    }


    public void connect(RunableActionContextImpl context)
            throws JDSException {
        try {


            JDSLocalServer.getInstance().getClient(context);


            JDSClientService client = this.getClient(context);

            String sessionId = (String) context.getSessionId();
            if (sessionId != null) {
                if (client != null) {
                    ConnectionHandle handle = client.getConnectionHandle();
                    handle.connect(context);
                }
            } else {
                throw new JDSException("sessionId is  null,  place login frist!");

            }

        } catch (JDSException e1) {
            logger.info(e1);
            throw new JDSException(
                    "sessionId is invalidate  url:[" + "] place login again!");


        }

    }

    public JDSClientService getClient(JDSContext context) throws JDSException {

        JDSSessionFactory factory = new JDSSessionFactory(context);
        String sessionId = (String) context.getSessionId();

        if (sessionId == null) {
            sessionId = (String) context.getParams(context.JSESSIONID);
        }
        if (sessionId != null) {
            context.getContext().put(context.JSESSIONID, sessionId);
        }

        String systemCode = JDSServer.getInstance().getCurrServerBean().getId();

        ConfigCode configCode = JDSServer.getInstance().getCurrServerBean().getConfigCode();

        context.getContext().put(context.SYSCODE, systemCode);

        JDSClientService appClient = null;
        try {
            appClient = factory.getJDSClientBySessionId(sessionId, configCode);

        } catch (JDSException e) {
            logger.error("sessionId is invalidate sessionId=[" + sessionId + "] ieee=[+" + context.getParams("ieee") + "] place login again!");
        }

        if (appClient != null) {
            appClient.setContext(context);
        }

        return appClient;
    }


}
