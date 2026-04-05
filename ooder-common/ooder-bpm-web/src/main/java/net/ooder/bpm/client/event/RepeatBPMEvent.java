package net.ooder.bpm.client.event;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.bpm.engine.event.BPMEventControl;
import net.ooder.cluster.ServerNode;
import net.ooder.cluster.udp.ClusterEvent;
import net.ooder.common.ConfigCode;
import net.ooder.common.EsbFlowType;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;
import net.ooder.server.JDSServer;
import net.ooder.server.SubSystem;

@EsbBeanAnnotation(id = "RepeatBPMEvent", name = "转发流程集群事件", expressionArr = "RepeatBPMEvent(event)", flowType = EsbFlowType.msgRepeat, desc = "转发流程事件集群事件")
public class RepeatBPMEvent extends AbstractFunction {
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, RepeatBPMEvent.class);

    public Boolean perform(final ClusterEvent event) {
        logger.info("client satrt repeat event" + event.getSourceJson());
        try {
            final String eventId = event.getEventId();
            final String content = event.getSourceJson();
            final String eventName = event.getEventName();
            final String systemCode = event.getSystemCode();
            final ServerNode currServerBean = JDSServer.getInstance().getCurrServerBean();
            final SubSystem subSystem = JDSServer.getClusterClient().getSystem(currServerBean.getId());

            if (JDSServer.getInstance().getAdminUser() != null && !JDSServer.getInstance().getAdminUser().getConfigName().equals(ConfigCode.scene)) {
                JDSActionContext.getActionContext().getContext().put(JDSActionContext.SYSCODE, JDSServer.getInstance().getAdminUser().getCode());
                JDSActionContext.getActionContext().getContext().put(JDSActionContext.JSESSIONID, JDSServer.getInstance().getAdminUser().getSessionId());
                JDSActionContext.getActionContext().getContext().put(JDSContext.JDSUSERID, JDSServer.getInstance().getAdminUser().getId());
                BPMEventControl.getInstance().dispatchClusterEvent(content, eventName, eventId, systemCode);
            }


        } catch (final JDSException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;

    }

}
