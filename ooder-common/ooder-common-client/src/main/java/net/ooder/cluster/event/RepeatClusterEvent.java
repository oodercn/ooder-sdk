/**
 * $RCSfile: RepeatClusterEvent.java,v $
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
package net.ooder.cluster.event;

import net.ooder.cluster.ServerNode;
import net.ooder.cluster.udp.ClusterEvent;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.EsbFlowType;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.server.JDSServer;

@EsbBeanAnnotation(id = "RepeatClusterEvent", name = "转发Cluster集群事件",  flowType = EsbFlowType.msgRepeat,expressionArr = "RepeatClusterEvent(event)", desc = "转发Cluster集群事件")
public class RepeatClusterEvent extends AbstractFunction {
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, RepeatClusterEvent.class);

    public Boolean perform(final ClusterEvent event) {

        logger.info("client satrt repeat event" + event.getSourceJson());

        try {

            final String eventId = event.getEventId();

            final String content = event.getSourceJson();

            final String eventName = event.getEventName();

            final String systemCode = event.getSystemCode();

            final ServerNode currServerBean = JDSServer.getInstance().getCurrServerBean();

            ClusterEventControl.getInstance().dispatchClusterEvent(content, eventName, eventId, systemCode);

        } catch (final JDSException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;

    }

}
