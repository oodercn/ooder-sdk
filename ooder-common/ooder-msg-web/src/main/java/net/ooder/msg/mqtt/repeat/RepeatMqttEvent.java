/**
 * $RCSfile: RepeatMqttEvent.java,v $
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
package net.ooder.msg.mqtt.repeat;

import net.ooder.cluster.ServerNode;
import net.ooder.cluster.udp.ClusterEvent;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.msg.mqtt.event.MQTTEventControl;
import net.ooder.server.JDSServer;
import net.ooder.server.SubSystem;

//@EsbBeanAnnotation(id = "RepeatMqttEvent", name = "转发mqtt事件", flowType = EsbFlowType.msgRepeat, expressionArr = "RepeatMqttEvent(event)", desc = "转发mqtt事件")
public class RepeatMqttEvent extends AbstractFunction {
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, RepeatMqttEvent.class);

    public Boolean perform(final ClusterEvent event) {
        logger.info("client satrt repeat eventId [" + event.getEventId() + "] data:" + event.getSourceJson());
        try {
            final String eventId = event.getEventId();
            final String content = event.getSourceJson();
            final String eventName = event.getEventName();
            final String systemCode = event.getSystemCode();
            final ServerNode currServerBean = JDSServer.getInstance().getCurrServerBean();
            final SubSystem subSystem = JDSServer.getClusterClient().getSystem(currServerBean.getId());
            MQTTEventControl.getInstance().dispatchClusterEvent(content, eventName, eventId, systemCode);

        } catch (final JDSException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;

    }

}


