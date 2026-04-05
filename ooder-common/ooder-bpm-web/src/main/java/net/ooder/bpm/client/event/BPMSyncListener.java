package net.ooder.bpm.client.event;

import net.ooder.bpm.client.ProcessDefVersion;
import net.ooder.bpm.client.ct.CtBPMCacheManager;
import net.ooder.bpm.engine.BPMException;
import net.ooder.common.ConfigCode;
import net.ooder.common.JDSException;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.server.JDSServer;


@EsbBeanAnnotation(id = "BPMSyncListener", name = "流程定义本地同步", expressionArr = "BPMSyncListener()", desc = "流程定义本地同步")
public class BPMSyncListener implements BPDListener {

    @Override
    public void ProcessDefUpdated(BPDEvent event) throws BPMException {
        try {
            if (JDSServer.getInstance().getCurrServerBean().getConfigCode().equals(ConfigCode.app)){
                ProcessDefVersion processDefVersion = (ProcessDefVersion) event.getSource();
                try {
                    CtBPMCacheManager.getInstance().clearProcessDefCache(processDefVersion.getProcessDefVersionId());
                } catch (JDSException e) {
                    e.printStackTrace();
                }
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void ProcessDefCreaded(BPDEvent event) throws BPMException {

    }

    @Override
    public void ProcessDefDeleted(BPDEvent event) throws BPMException {

    }

    @Override
    public void ProcessDefFreezed(BPDEvent event) throws BPMException {
        ProcessDefVersion processDefVersion = (ProcessDefVersion) event.getSource();
        try {
            CtBPMCacheManager.getInstance().clearProcessDefCache(processDefVersion.getProcessDefVersionId());
        } catch (JDSException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void ProcessDefActivaed(BPDEvent event) throws BPMException {
        ProcessDefVersion processDefVersion = (ProcessDefVersion) event.getSource();
        try {
            CtBPMCacheManager.getInstance().clearProcessDefCache(processDefVersion.getProcessDefVersionId());
        } catch (JDSException e) {
            e.printStackTrace();
        }

    }
}