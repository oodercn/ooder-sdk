package net.ooder.agent.client.home.engine;

import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.cluster.ServerNode;
import  net.ooder.cluster.udp.ClusterEvent;
import  net.ooder.common.EsbFlowType;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.expression.function.AbstractFunction;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.server.JDSServer;
import  net.ooder.server.SubSystem;


@EsbBeanAnnotation(id = "RepeatHomeEvent", name = "转发IOT集群事件", flowType = EsbFlowType.msgRepeat, expressionArr = "RepeatHomeEvent(event)", desc = "转发IOT集群事件")
public class RepeatHomeEvent extends AbstractFunction {
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, RepeatHomeEvent.class);

    public Boolean perform(final ClusterEvent event) {

        logger.info("client satrt repeat eventId [" + event.getEventId() + "] data:" + event.getSourceJson());

        try {

            final String eventId = event.getEventId();
            final String content = event.getSourceJson();
            final String eventName = event.getEventName();
            final String systemCode = event.getSystemCode();
            final ServerNode currServerBean = JDSServer.getInstance().getCurrServerBean();
            final SubSystem subSystem = JDSServer.getClusterClient().getSystem(currServerBean.getId());
            HomeEventControl.getInstance().dispatchClusterEvent(content, eventName, eventId, systemCode);

        } catch (final JDSException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;

    }

}
