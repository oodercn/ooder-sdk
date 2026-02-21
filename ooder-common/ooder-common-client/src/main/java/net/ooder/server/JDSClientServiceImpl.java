/**
 * $RCSfile: JDSClientServiceImpl.java,v $
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
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.ConnectionHandle;
import net.ooder.engine.JDSSessionHandle;

public class JDSClientServiceImpl implements JDSClientService {

    private JDSServer jdsServer;
    private JDSSessionHandle sessionHandle;
    private ConfigCode configCode;
    private ConnectionHandle connecionHandel;
    private ConnectInfo connInfo;
    private JDSContext context;

    JDSClientServiceImpl(JDSSessionHandle sessionHandle, ConfigCode configCode) throws JDSException {
        this.sessionHandle = sessionHandle;
        this.configCode = configCode;
        this.jdsServer = JDSServer.getInstance();


    }

    // --------------------------------------------- 登陆注销操作

    public void connect(ConnectInfo connInfo) throws JDSException {
        this.connInfo = connInfo;
        jdsServer.connect(this);


    }

    public ConnectionHandle getConnectionHandle() {
        return connecionHandel;
    }

    public ReturnType disconnect() throws JDSException {
        if (sessionHandle != null) {
            jdsServer.disconnect(sessionHandle);
        }

        connInfo = null;
        sessionHandle = null;
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ConnectInfo getConnectInfo() {
        return connInfo;
    }


    @Override
    public ConfigCode getConfigCode() {
        return configCode;
    }

    public JDSSessionHandle getSessionHandle() {
        if (sessionHandle == null) {
            JDSSessionFactory factory = new JDSSessionFactory(this.getContext());
            sessionHandle = factory.getSessionHandle();

        }
        return sessionHandle;
    }

    public void setConnectionHandle(ConnectionHandle handle) {
        this.connecionHandel = handle;
    }

    public JDSContext getContext() {
        if (context == null) {
            context = JDSActionContext.getActionContext();
        }
        return context;
    }

    public void setContext(JDSContext context) {
        this.context = context;
    }

    @Override
    public String getSystemCode() {
        return JDSActionContext.getActionContext().getSystemCode();
    }

}
