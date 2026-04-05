package net.ooder.bpm.client.listener;

import net.ooder.bpm.client.ct.CtBPMCacheManager;
import net.ooder.bpm.client.event.ActivityEvent;
import net.ooder.bpm.client.event.ActivityListener;
import net.ooder.bpm.engine.BPMException;
import net.ooder.common.EsbFlowType;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.UserBean;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.server.JDSServer;


@EsbBeanAnnotation(id = "ProcessInstCacheSyncListener", name = "流程实例本地同步", expressionArr = "ProcessInstCacheSyncListener()", flowType = EsbFlowType.listener, desc = "流程实例本地同步")
public class ProcessInstCacheSyncListener implements ActivityListener {


    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, ProcessInstCacheSyncListener.class);


    private void clearCache(ActivityEvent event) {
        try {
            if (JDSServer.getInstance().getAdminUser() != null && !UserBean.getInstance().getConfigName().equals("scene")) {
                if (event.getActivityInsts().size() > 0) {
                    CtBPMCacheManager.getInstance().clearActivityInstCache(event.getActivityInsts().get(0).getActivityInstId());
                }
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void activityInited(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activityRouting(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activityRouted(ActivityEvent event) throws BPMException {
        clearCache(event);
    }

    @Override
    public void activityActiving(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activityActived(ActivityEvent event) throws BPMException {
        clearCache(event);
    }

    @Override
    public void activityFormSaveing(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activityFormSaveed(ActivityEvent event) throws BPMException {
        clearCache(event);
    }

    @Override
    public void activitySpliting(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activitySplited(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activityJoining(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activityJoined(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activityOutFlowing(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activityOutFlowed(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activityOutFlowReturning(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activityOutFlowReturned(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activitySuspending(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activitySuspended(ActivityEvent event) throws BPMException {
        clearCache(event);
    }

    @Override
    public void activityResuming(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activityResumed(ActivityEvent event) throws BPMException {
        clearCache(event);
    }

    @Override
    public void activityCompleting(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activityCompleted(ActivityEvent event) throws BPMException {
        clearCache(event);
    }

    @Override
    public void activityTakebacking(ActivityEvent event) throws BPMException {

    }

    @Override
    public void activityTakebacked(ActivityEvent event) throws BPMException {
        clearCache(event);
    }

    @Override
    public void activityDisplay(ActivityEvent event) throws BPMException {

    }
}