package net.ooder.bpm.client.ct;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.Attributetype;
import net.ooder.annotation.DurationUnit;
import net.ooder.bpm.client.*;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.activitydef.*;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.common.JDSException;
import net.ooder.config.ActivityDefImpl;
import net.ooder.jds.core.esb.EsbUtil;


import java.util.ArrayList;
import java.util.List;

public class CtActivityDef  implements ActivityDef , java.io.Serializable{

    private  List<String> inRouteIds;

    private String activityDefId;

    private String processDefId;

    private String processDefVersionId;

    private String name;

    private String description;

    private String execClass;

    private int limit;

    private int alertTime;

    private  String subFlowId;

    private ActivityDefPosition position;

    private ActivityDefImpl implementation;

    private DurationUnit durationUnit;

    private ActivityDefDeadLineOperation deadlineOperation;

    private CommonYesNoEnum canRouteBack;

    private CommonYesNoEnum canSpecialSend;

    private ActivityDefJoin join;

    private ActivityDefSplit split;

    private CommonYesNoEnum iswaitreturn;

    private CommonYesNoEnum canReSend;

    private List<String> outRouteIds;

    private List<Listener> listeners=new ArrayList<Listener>();

    private ActivityDefRouteBackMethod routeBackMethod;

    public CtActivityDef(ActivityDef activityDef) {
        this.name = activityDef.getName();
        this.activityDefId = activityDef.getActivityDefId();
        this.alertTime = activityDef.getAlertTime();
        this.position = activityDef.getPosition();
        this.canReSend = activityDef.getCanReSend();
        this.canRouteBack = activityDef.getCanRouteBack();
        this.canSpecialSend = activityDef.getCanSpecialSend();
        this.deadlineOperation = activityDef.getDeadlineOperation();
        this.description = activityDef.getDescription();
        this.durationUnit = activityDef.getDurationUnit();
        this.execClass = activityDef.getExecClass();
        this.join = activityDef.getJoin();
        this.implementation = activityDef.getImplementation();
        this.limit = activityDef.getLimit();
        this.split = activityDef.getSplit();
        this.subFlowId=activityDef.getSubFlowId();


        this.routeBackMethod = activityDef.getRouteBackMethod();

        try {
            if (activityDef.getListeners()!=null){
                this.listeners=activityDef.getListeners();
            }

            this.inRouteIds = activityDef.getInRouteIds();
            this.outRouteIds = activityDef.getOutRouteIds();
            this.iswaitreturn = activityDef.getIswaitreturn();
        } catch (BPMException e) {
            e.printStackTrace();
        }

        this.processDefId = activityDef.getProcessDefId();
        this.processDefVersionId = activityDef.getProcessDefVersionId();


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
    public String getActivityDefId() {
        return this.activityDefId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public ActivityDefPosition getPosition() {
        return this.position;
    }

    @Override
    public ActivityDefImpl getImplementation() {
        return implementation;
    }

    @Override
    public String getExecClass() {
        return this.execClass;
    }

    @Override
    public CommonYesNoEnum getIswaitreturn() throws BPMException {
        return this.iswaitreturn;
    }

    @Override
 
    @JSONField(serialize = false)
    public ProcessDefVersion getSubFlow() throws BPMException {
        ProcessDefVersion processDefVersion=null;
        try {
            processDefVersion= CtBPMCacheManager.getInstance().getProcessDef(this.getSubFlowId()).getActiveProcessDefVersion();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return processDefVersion;
    }

    @Override
    public int getLimit() {
        return this.limit;
    }

    @Override
    public int getAlertTime() {
        return this.alertTime;
    }

    @Override
    public DurationUnit getDurationUnit() {
        return this.durationUnit;
    }

    @Override
    public ActivityDefDeadLineOperation getDeadlineOperation() {
        return this.deadlineOperation;
    }

    @Override
    public CommonYesNoEnum getCanRouteBack() {
        return this.canRouteBack;
    }

    @Override
    public ActivityDefRouteBackMethod getRouteBackMethod() {
        return this.routeBackMethod;
    }

    @Override
    public CommonYesNoEnum getCanSpecialSend() {
        return this.canSpecialSend;
    }

    @Override
    public CommonYesNoEnum getCanReSend() {
        return this.canReSend;
    }

    @Override
    public ActivityDefJoin getJoin() {
        return this.join;
    }

    @Override
    public ActivityDefSplit getSplit() {
        return this.split;
    }

    @Override
    public List<String> getOutRouteIds() throws BPMException {
        return outRouteIds;
    }

    @Override
    public List<String> getInRouteIds() throws BPMException {
        return inRouteIds;
    }

    @Override
    public List<AttributeDef> getAllAttribute() {
        List<String> attributeDefIds=new ArrayList<String>();
        try {
            return  getClient().getActivityDefAttributes(this.getActivityDefId());
        } catch (BPMException e) {
            e.printStackTrace();
        }
        return new ArrayList<AttributeDef>();

    }


    @Override
 
    @JSONField(serialize = false)
    public ActivityDefRight getRightAttribute() {
        try {
            return  CtBPMCacheManager.getInstance().getActivityDefRight(this.getActivityDefId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Listener> getListeners() {

       return  this.listeners;
      //  return CtBPMCacheManager.getInstance().getActivityListeners(this.getActivityDefId());
    }

    @Override
 
    @JSONField(serialize = false)
    public ProcessDefVersion getProcessDefVersion() throws BPMException {
        ProcessDefVersion version = null;
        try {
            version = CtBPMCacheManager.getInstance().getProcessDefVersion(this.getProcessDefVersionId());
        } catch (JDSException e) {
            throw  new BPMException(e);
        }

        return version;

    }
    public String getSubFlowId() {
        return subFlowId;
    }
    @Override
 
    @JSONField(serialize = false)
    public ProcessDef getProcessDef() throws BPMException {
        ProcessDef processDef = null;
        try {
            processDef = CtBPMCacheManager.getInstance().getProcessDef(this.getProcessDefId());
        } catch (JDSException e) {
            throw  new BPMException(e);
        }

        return processDef;
    }
    @Override
 
    @JSONField(serialize = false)
    public String getWorkflowAttribute(String name) {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef> ();
        try {
            attributeDefs = getClient().getActivityDefAttributes(activityDefId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeDef attributeDef : attributeDefs) {
            if (attributeDef.getType().equals(Attributetype.ADVANCE) && attributeDef.getName().equals(name)) {

                return attributeDef.getValue();
            }
        }
        return null;
    }
    @Override
 
    @JSONField(serialize = false)
    public String getAttribute( String name) {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef> ();
        try {
            attributeDefs = getClient().getActivityDefAttributes(activityDefId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeDef attributeDef : attributeDefs) {
            if (attributeDef.getType().equals(Attributetype.CUSTOMIZE) && attributeDef.getName().equals(name)) {
                return attributeDef.getValue();
            }
        }
        return null;
    }

    @Override
 
    @JSONField(serialize = false)
    public Object getAppAttribute( String name)  {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef> ();
        try {
            attributeDefs = getClient().getActivityDefAttributes(activityDefId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeDef attributeDef : attributeDefs) {
            if (attributeDef.getType().equals(Attributetype.APPLICATION) && attributeDef.getName().equals(name)) {

                return attributeDef.getInterpretedValue();
            }
        }
        return null;
    }

    @Override
    @JSONField(serialize = false)
    public Object getAttribute(Attributetype attributetype, String name) {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef> ();
        try {
            attributeDefs = getClient().getActivityDefAttributes(activityDefId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeDef attributeDef : attributeDefs) {
            if (attributeDef.getType().equals(attributetype) && attributeDef.getName().equals(name)) {

                return attributeDef.getInterpretedValue();
            }
        }
        return null;
    }


    @JSONField(serialize = false)
 
    /**
     * @return
     */
    public WorkflowClientService getClient() {

        WorkflowClientService client = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));

        return client;
    }



//

}
