package net.ooder.common.org.event;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.cluster.udp.ClusterEvent;
import net.ooder.common.ContextType;
import net.ooder.common.EsbFlowType;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;

@EsbBeanAnnotation(id = "RepeatORGEvent", name = "转发ORG集群事件", expressionArr = "RepeatORGEvent(event)",flowType = EsbFlowType.msgRepeat, desc = "转发ORG事件集群事件", dataType = ContextType.Action)
public class RepeatORGEvent extends AbstractFunction {
    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, RepeatORGEvent.class);

    public Boolean perform(final ClusterEvent event) {

	logger.info("client satrt repeat event" + event.getSourceJson());

	try {

	    final String eventId = event.getEventId();

	    final String content = event.getSourceJson();

	    final String eventName = event.getEventName();

	    final String systemCode = event.getSystemCode();


		OrgEventControl.getInstance().dispatchClusterEvent(content, eventName, eventId, systemCode);


	} catch (final JDSException e) {
	    e.printStackTrace();
	} catch (final Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return true;

    }

}
