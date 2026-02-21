package net.ooder.vfs.engine.event;

import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.cluster.ServerNode;
import  net.ooder.cluster.udp.ClusterEvent;
import  net.ooder.common.ContextType;
import  net.ooder.common.EsbFlowType;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.expression.function.AbstractFunction;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.server.JDSServer;

@EsbBeanAnnotation(id = "RepeatVFSEvent", name = "转发vfs集群事件", flowType = EsbFlowType.msgRepeat, expressionArr = "RepeatVFSEvent(event)", desc = "转发VFS事件集群事件", dataType = ContextType.Action)
public class RepeatVFSEvent extends AbstractFunction {
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, RepeatVFSEvent.class);

    public Boolean perform(final ClusterEvent event) {

        logger.info("client satrt repeat event" + event.getSourceJson());

        try {

            final String eventId = event.getEventId();

            final String content = event.getSourceJson();

            final String eventName = event.getEventName();

            final String systemCode = event.getSystemCode();

            final ServerNode currServerBean = JDSServer.getInstance().getCurrServerBean();

            VFSEventControl.getInstance().dispatchClusterEvent(content, eventName, eventId, systemCode);

        } catch (final JDSException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;

    }

}
