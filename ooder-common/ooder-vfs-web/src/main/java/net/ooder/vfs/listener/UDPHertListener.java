package net.ooder.vfs.listener;

import  net.ooder.cluster.udp.ClusterClient;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.common.EsbFlowType;
import  net.ooder.common.JDSException;
import  net.ooder.engine.event.EIServerAdapter;
import  net.ooder.engine.event.EIServerEvent;
import  net.ooder.server.JDSServer;

@EsbBeanAnnotation(id = "UDPHertListener", name = "UDP客户端启动", expressionArr = "UDPHertListener()", flowType = EsbFlowType.listener, desc = "UDP客户端启动")

public class UDPHertListener extends EIServerAdapter {


    @Override
    public void serverStopped(EIServerEvent event) throws JDSException {
        ClusterClient client = JDSServer.getClusterClient();
        if (!client.getUDPClient().isClient) {
            client.stop();
        }

    }

    @Override
    public void serverStarted(EIServerEvent event) throws JDSException {
        ClusterClient client = JDSServer.getClusterClient();
        if (!client.getUDPClient().isClient) {
            client.login();
        }

    }
}
