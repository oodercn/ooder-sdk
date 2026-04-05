package net.ooder.bpm.client.ct;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.Attributetype;
import net.ooder.bpm.client.*;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.activityinst.*;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;

import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.Person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CtActivityInst implements ActivityInst {

    private String activityInstId;

    private String processInstId;

    private String activityDefId;

    private String processDefVersionId;

    private String processDefId;

    private String urgency;

    private Date arrivedTime;

    private Date limitTime;

    private Date alertTime;

    private Date startTime;

    private ActivityInstStatus state;

    private ActivityInstReceiveMethod recieveState;

    private ActivityInstDealMethod dealState;

    private ActivityInstRunStatus runState;

    private CommonYesNoEnum canTakeBack = CommonYesNoEnum.YES;

    public CtActivityInst(ActivityInst activityInst) {

        this.activityInstId = activityInst.getActivityInstId();
        this.activityDefId = activityInst.getActivityDefId();
        this.alertTime = activityInst.getAlertTime();
        this.arrivedTime = activityInst.getArrivedTime();
        if (activityInst.getCanTakeBack() != null) {
            this.canTakeBack = activityInst.getCanTakeBack();
        }

        this.dealState = activityInst.getDealMethod();
        this.limitTime = activityInst.getLimitTime();
        this.recieveState = activityInst.getReceiveMethod();
        this.processDefId = activityInst.getProcessDefId();
        this.processDefVersionId = activityInst.getProcessDefVersionId();
        this.processInstId = activityInst.getProcessInstId();
        this.runState = activityInst.getRunStatus();
        this.startTime = activityInst.getStartTime();
        this.state = activityInst.getState();
        this.urgency = activityInst.getUrgency();

    }

    @Override
    public String getActivityInstId() {
        return this.activityInstId;
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
    public String getProcessDefId() {
        return this.processDefId;
    }

    @Override
    @JSONField(serialize = false)
    public ProcessDef getProcessDef() throws BPMException {
        ProcessDef processDef = null;
        try {
            return CtBPMCacheManager.getInstance().getProcessDef(this.getProcessDefId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return processDef;
    }

    @Override
    public ActivityInstStatus getState() {
        return this.state;
    }

    @Override
    public String getUrgency() {
        return this.urgency;
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
    public Date getAlertTime() {
        return this.alertTime;
    }

    @Override
    public Date getStartTime() {
        return this.startTime;
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
    public ActivityInstRunStatus getRunStatus() {
        return this.runState;
    }

    @Override
    public CommonYesNoEnum getCanTakeBack() {
        return this.canTakeBack;
    }

    @Override
    @JSONField(serialize = false)
    public DataMap getFormValues() throws BPMException {
        WorkflowClientService client = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));
        return client.getActivityInstFormValues(this.getActivityInstId());
    }

    @Override
    public void updateFormValues(DataMap dataMap) throws BPMException {
        WorkflowClientService clientService = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));
        clientService.updateActivityInstFormValues(this.getActivityInstId(), dataMap);
    }


    @Override
    @JSONField(serialize = false)
    public List<Person> getRightAttribute(ActivityInstRightAtt group) {
        try {
            return CtRightEngine.getInstance().getActivityInstPerson(this.getActivityInstId(), group);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @JSONField(serialize = false)
    public String getWorkflowAttribute(String name) {
        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();
        try {
            attributeInsts = CtBPMCacheManager.getInstance().getActivityInstAttributes(activityInstId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeInst attributeInst : attributeInsts) {
            if (attributeInst.getType().equals(Attributetype.ADVANCE) && attributeInst.getName().equals(name)) {
                attributeInst = new CtAttributeInst(attributeInst);
                return attributeInst.getValue();
            }
        }
        return null;
    }

    @Override
    public List<AttributeInst> loadAllAttribute() {
        try {
            return CtBPMCacheManager.getInstance().getActivityInstAttributes(activityInstId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return new ArrayList<AttributeInst>();
    }

    @Override
    @JSONField(serialize = false)
    public String getAppAttribute(String name) {
        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();
        try {
            attributeInsts = CtBPMCacheManager.getInstance().getActivityInstAttributes(activityInstId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeInst attributeInst : attributeInsts) {
            if (attributeInst.getType().equals(Attributetype.APPLICATION) && attributeInst.getName().equals(name)) {
                attributeInst = new CtAttributeInst(attributeInst);
                return attributeInst.getValue();
            }
        }
        return null;
    }
//    @Override
//    
//    @JSONField(serialize = false)
//    public AttributeInst getAttributeByName(String name) {
//        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();
//        try {
//            attributeInsts = CtBPMCacheManager.getInstance().getActivityInstAttributes(activityInstId);
//        } catch (JDSException e) {
//            e.printStackTrace();
//        }
//        for (AttributeInst attributeInst : attributeInsts) {
//            if (attributeInst.getType().equals(Attributetype.APPLICATION) && attributeInst.getName().equals(name)) {
//                attributeInst = new CtAttributeInst(attributeInst);
//                return attributeInst;
//            }
//        }
//        return null;
//    }

    @Override

    @JSONField(serialize = false)
    public String getAttribute(String name) {
        List<AttributeInst> attributeInsts = new ArrayList<AttributeInst>();
        try {
            attributeInsts = CtBPMCacheManager.getInstance().getActivityInstAttributes(activityInstId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeInst attributeInst : attributeInsts) {
            if (attributeInst.getType().equals(Attributetype.APPLICATION) && attributeInst.getName().equals(name)) {
                attributeInst = new CtAttributeInst(attributeInst);
                return attributeInst.getValue();
            }
        }
        return null;
    }


    @Override
    @JSONField(serialize = false)
    public void setAttribute(String name, String value) throws BPMException {
        try {
            CtBPMCacheManager.getInstance().setActivityInstAttribute(this.getActivityInstId(), name, value);
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPersonAttribute(String personId, String name, String value) throws BPMException {
        CtBPMCacheManager.getInstance().setActivityInstPersonAttribute(this.getActivityInstId(), name, value);
    }

    @Override
    @JSONField(serialize = false)
    public ProcessInst getProcessInst() throws BPMException {

        ProcessInst processInst = null;
        try {
            processInst = CtBPMCacheManager.getInstance().getProcessInst(this.getProcessInstId());
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return processInst;
    }

    @Override

    @JSONField(serialize = false)
    public ProcessDefVersion getProcessDefVersion() throws BPMException {

        ProcessDefVersion processDefVersion = null;
        try {
            processDefVersion = CtBPMCacheManager.getInstance().getProcessDefVersion(this.getProcessDefVersionId());
        } catch (JDSException e) {
            throw new BPMException(e);
        }

        return processDefVersion;
    }

    @Override
    public String getProcessDefVersionId() {
        return processDefVersionId;
    }

    @Override
    @JSONField(serialize = false)
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
    @JSONField(serialize = false)
    public List<RouteDef> getNextRoutes() throws BPMException {
        List<RouteDef> routeDefs = new ArrayList<RouteDef>();
        try {
            routeDefs = CtBPMCacheManager.getInstance().getNextRoutes(this.activityInstId);
            ;
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return routeDefs;
    }


    @Override
    @JSONField(serialize = false)
    public Boolean isCanTakeBack() throws BPMException {
        return this.canTakeBack.getType().equals(CommonYesNoEnum.YES) ? true : false;
    }

    @Override
    public Boolean isCanSpecialSend() throws BPMException {
        return this.getActivityDef().getCanSpecialSend().getType().equals(CommonYesNoEnum.YES) ? true : false;
    }

    @Override
    @JSONField(serialize = false)
    public Boolean isCanEndRead() throws BPMException {

        try {
            return CtBPMCacheManager.getInstance().canEndRead(this.getActivityInstId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ReturnType endRead() throws BPMException {
        return CtBPMCacheManager.getInstance().endRead(this.getActivityInstId());
    }

    @Override
    @JSONField(serialize = false)
    public Boolean isCanPerform() throws BPMException {

        return CtBPMCacheManager.getInstance().canPerform(this.getActivityInstId());
        //return this.canPerform;
    }

    @Override
    public ReturnType takeBack() throws BPMException {
        return CtBPMCacheManager.getInstance().takeBack(this.getActivityInstId());
    }

    @Override
    @JSONField(serialize = false)
    public Boolean isCanRouteBack() throws BPMException {

        return CtBPMCacheManager.getInstance().canRouteBack(this.getActivityInstId());
        // return this.canRouteBack;
    }

    @Override
    @JSONField(serialize = false)
    public List<ActivityInstHistory> getRouteBackActivityHistoryInstList() throws BPMException {
        try {
            return CtBPMCacheManager.getInstance().getRouteBackActivityHistoryInstList(this.getActivityInstId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @JSONField(serialize = false)
    public ReturnType routeBack(String toActivityInstHistoryID) throws BPMException {
        return CtBPMCacheManager.getInstance().routeBack(this.getActivityInstId(), toActivityInstHistoryID);
    }

    @Override
    @JSONField(serialize = false)
    public Boolean isCanSignReceive() throws BPMException {

        return CtBPMCacheManager.getInstance().canSignReceive(this.getActivityInstId());
    }

    @Override
    public Boolean isCanCompleteProcessInst() throws BPMException {
        List<RouteDef> routes = this.getNextRoutes();
        for (RouteDef route : routes) {
            if (route.getToActivityDefId().equals("LAST")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean isCanReSend() throws BPMException {
        return this.getActivityDef().getCanReSend().equals(CommonYesNoEnum.YES);
    }

    @Override
    public ReturnType signReceive() throws BPMException {
        return CtBPMCacheManager.getInstance().signReceive(this.getActivityInstId());
    }

    @Override

    @JSONField(serialize = false)
    public ReturnType suspendActivityInst() throws BPMException {
        return CtBPMCacheManager.getInstance().suspendActivityInst(this.getActivityInstId());
    }

    @Override
    @JSONField(serialize = false)
    public ReturnType resumeActivityInst() throws BPMException {
        return CtBPMCacheManager.getInstance().resumeActivityInst(this.getActivityInstId());
    }

    @Override
    @JSONField(serialize = false)
    public List<ActivityInstHistory> getActivityInstHistoryListByActvityInst() throws BPMException {
        try {
            return CtBPMCacheManager.getInstance().getActivityInstHistoryListByActvityInst(this.getActivityInstId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }
}
