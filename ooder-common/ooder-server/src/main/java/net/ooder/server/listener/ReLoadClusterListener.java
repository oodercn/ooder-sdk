package net.ooder.server.listener;

import net.ooder.cluster.event.ServerEvent;
import net.ooder.cluster.event.ServerListener;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.JDSException;
import net.ooder.server.JDSServer;

@EsbBeanAnnotation
public class ReLoadClusterListener implements ServerListener {
    @Override
    public void serverStarting(ServerEvent event) throws JDSException {

    }

    @Override
    public void serverStarted(ServerEvent event) throws JDSException {

    }

    @Override
    public void serverStopping(ServerEvent event) throws JDSException, InterruptedException {

    }

    @Override
    public void serverStopped(ServerEvent event) throws JDSException {

    }

    @Override
    public void systemSaving(ServerEvent event) throws JDSException {


    }

    @Override
    public void systemSaved(ServerEvent event) throws JDSException {

        JDSServer.getClusterClient().reLoadConfig();
    }

    @Override
    public void systemDeleting(ServerEvent event) throws JDSException {

    }

    @Override
    public void systemDeleted(ServerEvent event) throws JDSException {
        JDSServer.getClusterClient().reLoadConfig();
    }

    @Override
    public void systemActivating(ServerEvent event) throws JDSException {

    }

    @Override
    public void systemActivated(ServerEvent event) throws JDSException {
        JDSServer.getClusterClient().reLoadConfig();
    }

    @Override
    public void systemFreezing(ServerEvent event) throws JDSException {

    }

    @Override
    public void systemFreezed(ServerEvent event) throws JDSException {

    }
}
