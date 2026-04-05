package net.ooder.bpm.client.ct;

import net.ooder.annotation.Attributetype;
import net.ooder.bpm.client.*;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.process.ProcessInstAtt;
import net.ooder.bpm.enums.process.ProcessInstStatus;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.jds.core.esb.EsbUtil;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CtProcessInst implements ProcessInst {

    private String processInstId;

    private String processDefId;

    private String processDefVersionId;

    private String name;

    private String urgency;

    private ProcessInstStatus state;

    private int copyNumber;

    private Date startTime;

    private Date limitTime;

    private Date endTime;

    private ProcessInstStatus runState;


    public CtProcessInst(ProcessInst processInst) {
        this.copyNumber = processInst.getCopyNumber();
        this.endTime = processInst.getEndTime();
        this.limitTime = processInst.getLimitTime();
        this.name = processInst.getName();
        this.urgency = processInst.getUrgency();
        this.startTime = processInst.getStartTime();
        this.state = processInst.getState();
        this.processDefId = processInst.getProcessDefId();
        this.processDefVersionId = processInst.getProcessDefVersionId();
        this.processInstId = processInst.getProcessInstId();
        this.runState = processInst.getRunStatus();

    }

    @Override
    public String getProcessInstId() {
        return this.processInstId;
    }

    @Override
    public String getProcessDefId() {
        return this.processDefId;
    }

    @Override
    public String getProcessDefVersionId() {
        return this.processDefVersionId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getUrgency() {
        return this.urgency;
    }

    @Override
    public ProcessInstStatus getState() {
        return this.state;
    }

    @Override
    public int getCopyNumber() {
        return copyNumber;
    }

    @Override
    public Date getStartTime() {
        return this.startTime;
    }

    @Override
    public Date getEndTime() {
        return this.endTime;
    }

    @Override
    public Date getLimitTime() {
        return this.limitTime;
    }

    @Override
    public ProcessInstStatus getRunStatus() {
        return this.runState;
    }

    @Override
    public ProcessDefVersion getProcessDefVersion() throws BPMException {

        ProcessDefVersion processDefVersion = null;

        try {
            processDefVersion = CtBPMCacheManager.getInstance().getProcessDefVersion(processDefVersionId);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return processDefVersion;
    }

    @Override
    public ProcessDef getProcessDef() throws BPMException {
        ProcessDef processDef = null;
        try {
            processDef = CtBPMCacheManager.getInstance().getProcessDef(processDefId);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return processDef;
    }

    @Override
    public List<ActivityInst> getActivityInstList() throws BPMException {
        List<ActivityInst> activityInsts = new ArrayList<ActivityInst>();
        try {
            activityInsts = CtBPMCacheManager.getInstance().getActivityInstList(this.getProcessInstId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return activityInsts;
    }

    @Override
    public Object getWorkflowAttribute(ProcessInstAtt name) {
        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();
        try {
            attributeInsts = CtBPMCacheManager.getInstance().getProcessInstAttributes(processInstId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeInst attributeInst : attributeInsts) {
            if (attributeInst.getType().equals(Attributetype.ADVANCE) && attributeInst.getName().equals(name)) {

                return attributeInst.getValue();
            }
        }
        return null;
    }

    @Override
    public Object getRightAttribute(ProcessInstAtt name) {
        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();
        try {
            attributeInsts = CtBPMCacheManager.getInstance().getProcessInstAttributes(processInstId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeInst attributeInst : attributeInsts) {
            if (attributeInst.getType().equals(Attributetype.RIGHT) && attributeInst.getName().equals(name.getType())) {

                return attributeInst.getValue();
            }
        }
        return null;
    }

    @Override
    public Object getAppAttribute(ProcessInstAtt name) {
        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();
        try {
            attributeInsts = CtBPMCacheManager.getInstance().getProcessInstAttributes(processInstId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeInst attributeInst : attributeInsts) {
            if (attributeInst.getType().equals(Attributetype.APPLICATION) && attributeInst.getName().equals(name)) {

                return attributeInst.getValue();
            }
        }
        return null;
    }

    @Override
    public String getAttribute(String name) {
        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();
        try {
            attributeInsts = CtBPMCacheManager.getInstance().getProcessInstAttributes(processInstId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeInst attributeInst : attributeInsts) {
            if (attributeInst.getName().equals(name)) {

                return attributeInst.getValue();
            }
        }
        return null;
    }

    @Override
    public List<AttributeInst> getAllAttribute() {
        try {
            return CtBPMCacheManager.getInstance().getProcessInstAttributes(this.getProcessInstId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return new ArrayList<AttributeInst>();
    }

    @Override
    public String getPersonAttribute(String personId, String name) {
        return CtBPMCacheManager.getInstance().getProcessInstPersonAttribute(this.getProcessInstId(), personId, name);
    }

    @Override
    public void setAttribute(String name, String value) throws BPMException {
        CtBPMCacheManager.getInstance().setProcessInstAttribute(this.getProcessInstId(), name, value);
    }

    @Override
    public void setPersonAttribute(String personId, String name, String value) throws BPMException {
        CtBPMCacheManager.getInstance().setProcessInstPersonAttribute(this.getProcessInstId(), personId, name, value);
    }

    @Override
    public ReturnType updateProcessInstName(String name) throws BPMException {
        return CtBPMCacheManager.getInstance().updateProcessInstName(this.getProcessInstId(), name);
    }

    @Override
    public ReturnType updateProcessInstUrgency(String urgency) throws BPMException {
        return CtBPMCacheManager.getInstance().updateProcessInstUrgency(this.getProcessInstId(), urgency);
    }

    @Override
    public ReturnType suspendProcessInst() throws BPMException {
        return CtBPMCacheManager.getInstance().suspendProcessInst(this.getProcessInstId());
    }

    @Override
    public ReturnType resumeProcessInst() throws BPMException {
        return CtBPMCacheManager.getInstance().resumeProcessInst(this.getProcessInstId());
    }

    @Override
    public List<ActivityInstHistory> getActivityInstHistoryListByProcessInst() throws BPMException {
        try {
            return CtBPMCacheManager.getInstance().getActivityInstHistoryListByProcessInst(this.getProcessInstId()).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ReturnType abortProcessInst() throws BPMException {
        return CtBPMCacheManager.getInstance().abortProcessInst(this.getProcessInstId());
    }

    @Override
    public ReturnType completeProcessInst() throws BPMException {
        return CtBPMCacheManager.getInstance().completeProcessInst(this.getProcessInstId());
    }

    @Override
    public ReturnType deleteProcessInst() throws BPMException {
        return CtBPMCacheManager.getInstance().deleteProcessInst(this.getProcessInstId());
    }

    @Override
    public DataMap getFormValues() throws BPMException {

        WorkflowClientService clientService = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));
        return clientService.getProcessInstFormValues(this.getProcessInstId());

    }

    @Override
    public void updateFormValues(DataMap dataMap) throws BPMException {

        WorkflowClientService clientService = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));
        clientService.updateProcessInstFormValues(this.getProcessInstId(), dataMap);
    }


}
