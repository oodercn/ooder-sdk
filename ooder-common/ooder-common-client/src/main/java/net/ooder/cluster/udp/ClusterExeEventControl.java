/**
 * $RCSfile: ClusterExeEventControl.java,v $
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
package net.ooder.cluster.udp;
import net.ooder.common.MsgStatus;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.server.JDSServer;
import net.ooder.web.ConnectionLogFactory;
import net.ooder.web.RuntimeLog;

import java.util.concurrent.Callable;

public class ClusterExeEventControl implements Runnable {

    private final ClusterEvent event;

    ClusterExeEventControl(ClusterEvent event) {
        this.event = event;
    }

    @Override
    public void run() {
        ActionContext.getContext().getContextMap().put("event", event);
        if (event != null) {
            String msgId = event.getMsgId();
            String token = event.getToken();
            RuntimeLog log = ConnectionLogFactory.getInstance().createLog(token, "udp://" + JDSServer.getClusterClient().getUDPClient().getUser().getUdpIP() + ":[" + event.getSystemCode() + "]", event.getEventName() + "[" + event.getEventId() + "]", msgId);
            log.setStartTime(event.getSendTime());
            log.setBodyJson(event.getSourceJson());
            log.setArrivedTime(System.currentTimeMillis());
            log.setTime(log.getArrivedTime() - log.getStartTime());
            log.setStatus(MsgStatus.UPDATE);
            Object obj = EsbUtil.parExpression(event.getExpression());
            log.setEndTime(System.currentTimeMillis());
            if (obj != null && Boolean.valueOf(obj.toString())) {
                log.setStatus(MsgStatus.READED);
                log.setExetime(log.getEndTime() - log.getArrivedTime());
                UDPClient.getInstance().updateEventStatus(event.getToken(), MsgStatus.UPDATE);
            } else {
                log.setStatus(MsgStatus.ERROR);
                log.setExetime(log.getEndTime() - log.getArrivedTime());
                UDPClient.getInstance().updateEventStatus(event.getToken(), MsgStatus.ERROR);
            }


        }


    }
}
