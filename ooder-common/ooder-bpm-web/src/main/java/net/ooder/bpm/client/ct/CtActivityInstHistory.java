package net.ooder.bpm.client.ct;

import net.ooder.annotation.Attributetype;
import net.ooder.bpm.client.*;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.activityinst.ActivityInstDealMethod;
import net.ooder.bpm.enums.activityinst.ActivityInstReceiveMethod;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryStatus;
import net.ooder.common.JDSException;


import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.Person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CtActivityInstHistory implements ActivityInstHistory {

    private String activityHistoryId;

    private String processInstId;

    private String activityDefId;

    private String activityInstId;

    private String urgencyType;

    private Date arrivedTime;

    private Date limitTime;

    private Date startTime;

    private Date endTime;

    private ActivityInstReceiveMethod recieveState;

    private ActivityInstDealMethod dealState;

    private ActivityInstHistoryStatus runState;


    public CtActivityInstHistory(ActivityInstHistory history) {
        this.activityDefId = history.getActivityDefId();
        this.activityHistoryId = history.getActivityHistoryId();
        this.activityInstId = history.getActivityInstId();
        this.arrivedTime = history.getArrivedTime();
        this.activityDefId = history.getActivityDefId();
        this.dealState = history.getDealMethod();
        this.endTime = history.getEndTime();
        this.limitTime = history.getLimitTime();
        this.processInstId = history.getProcessInstId();
        this.recieveState = history.getReceiveMethod();
        this.runState = history.getRunStatus();
        this.startTime = history.getStartTime();
        this.urgencyType = history.getUrgency();


    }

    @Override
    public String getActivityHistoryId() {
        return this.activityHistoryId;
    }

    @Override
    public String getProcessInstId() {
        return this.processInstId;
    }

    @Override
    public String getActivityDefId() {
        return this.activityDefId;
    }

    @Override
    public String getUrgency() {
        return this.urgencyType;
    }

    @Override
    public Date getArrivedTime() {
        return this.arrivedTime;
    }

    @Override
    public Date getLimitTime() {
        return this.limitTime;
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
    public ActivityInstReceiveMethod getReceiveMethod() {
        return this.recieveState;
    }

    @Override
    public ActivityInstDealMethod getDealMethod() {
        return this.dealState;
    }

    @Override
    public ActivityInstHistoryStatus getRunStatus() {
        return this.runState;
    }

    @Override
    public ProcessInst getProcessInst() throws BPMException {
        ProcessInst processInst = null;
        try {
            processInst = CtBPMCacheManager.getInstance().getProcessInst(this.getProcessInstId());
        } catch (JDSException e) {
            throw new BPMException(e);
        }

        return processInst;
    }

    @Override
    public ActivityInst getActivityInst() throws BPMException {
        ActivityInst activityInst = null;
        try {
            activityInst = CtBPMCacheManager.getInstance().getActivityInst(this.getActivityInstId());
        } catch (JDSException e) {
            throw new BPMException(e);
        }

        return activityInst;
    }

    @Override
    public String getActivityInstId() {
        return this.activityInstId;
    }

    @Override
    public ActivityDef getActivityDef() throws BPMException {
        ActivityDef activityDef = null;
        try {
            activityDef = CtBPMCacheManager.getInstance().getActivityDef(this.getActivityDefId());
        } catch (JDSException e) {
            throw new BPMException(e);
        }

        return activityDef;
    }


    @Override
    public List<Person> getRightAttribute(ActivityInstHistoryAtt name) {
        List<Person> persons = new ArrayList<Person>();
        try {
            persons = CtRightEngine.getInstance().getActivityInstHistoryPerson(this.getActivityHistoryId(), name);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return persons;
    }


    @Override
    public String getWorkflowAttribute(String name) {
        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();
        try {
            attributeInsts = CtBPMCacheManager.getInstance().getActivityInstHistoryAttributes(this.activityHistoryId);
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
    public String getAttribute(String name) {
        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();
        try {
            attributeInsts = CtBPMCacheManager.getInstance().getActivityInstHistoryAttributes(this.activityHistoryId);
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
    public String getAppAttribute(String name) {
        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();
        try {
            attributeInsts = CtBPMCacheManager.getInstance().getActivityInstHistoryAttributes(this.activityHistoryId);
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
    public void setAttribute(String name, String value) throws BPMException {
        CtBPMCacheManager.getInstance().setActivityHistoryAttribute(this.getActivityHistoryId(), name, value);
    }

    @Override
    public String getPersonAttribute(String personId, String name) {
        return CtBPMCacheManager.getInstance().getActivityHistoryPersonAttribute(this.getActivityHistoryId(), personId, name);
    }

    @Override
    public void setPersonAttribute(String personId, String name, String value) throws BPMException {
        CtBPMCacheManager.getInstance().setActivityHistoryPersonAttribute(this.getActivityHistoryId(), personId, name, value);

    }

    @Override
    public DataMap getFormValues() throws BPMException {
        WorkflowClientService clientService = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));
        return clientService.getActivityHistoryFormValues(this.getActivityHistoryId());
    }

    @Override
    public void updateFormValues(DataMap dataMap) throws BPMException {
        WorkflowClientService clientService = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));
        clientService.updateActivityHistoryFormValues(this.getActivityHistoryId(), dataMap);
    }
}
