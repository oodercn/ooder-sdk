package net.ooder.bpm.client.ct;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.Attributetype;
import net.ooder.bpm.client.*;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.process.ProcessDefAccess;
import net.ooder.bpm.enums.process.ProcessDefVersionStatus;
import net.ooder.common.JDSException;
import net.ooder.annotation.DurationUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CtProcessDefVersion implements ProcessDefVersion {


    private String defDescription;
    private String systemCode;
    private List<String> activityDefIds;

    private List<String> routeDefIds;
    private List<Listener> listeners = new ArrayList<Listener>();

    private String processDefVersionId;

    private String processDefId;

    private int version;

    private String processDefName;

    private String description;

    private ProcessDefVersionStatus publicationStatus;

    private java.util.Date activeTime;

    private java.util.Date freezeTime;

    private String creatorId;

    private String creatorName;

    private java.util.Date created;

    private String modifierId;

    private String modifierName;

    private java.util.Date modifyTime;

    private int limit;

    private DurationUnit durationUnit;
    private String classification;
    private ProcessDefAccess accessLevel;


    public CtProcessDefVersion(ProcessDefVersion processDefVersion) {
        this.accessLevel = processDefVersion.getAccessLevel();
        this.routeDefIds = processDefVersion.getRouteDefIds();
        this.classification = processDefVersion.getClassification();
        this.defDescription = processDefVersion.getDefDescription();
        this.processDefName = processDefVersion.getProcessDefName();
        this.processDefId = processDefVersion.getProcessDefId();
        this.description = processDefVersion.getDescription();
        this.activeTime = processDefVersion.getActiveTime();
        this.created = processDefVersion.getCreated();
        this.creatorId = processDefVersion.getCreatorId();
        this.creatorName = processDefVersion.getCreatorName();
        this.durationUnit = processDefVersion.getDurationUnit();
        this.freezeTime = processDefVersion.getFreezeTime();
        this.limit = processDefVersion.getLimit();
        this.modifierId = processDefVersion.getModifierId();
        this.modifierName = processDefVersion.getModifierName();
        this.modifyTime = processDefVersion.getModifyTime();
        this.processDefVersionId = processDefVersion.getProcessDefVersionId();
        this.publicationStatus = processDefVersion.getPublicationStatus();
        this.version = processDefVersion.getVersion();
        this.systemCode = processDefVersion.getSystemCode();
        this.activityDefIds = processDefVersion.getActivityDefIds();
        this.routeDefIds = processDefVersion.getRouteDefIds();
        this.activityDefIds = processDefVersion.getActivityDefIds();
        this.systemCode = processDefVersion.getSystemCode();
        this.defDescription = processDefVersion.getDefDescription();
        if (processDefVersion.getListeners() != null) {
            this.listeners = processDefVersion.getListeners();
        }


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
    public int getVersion() {
        return this.version;
    }

    @Override
    public ProcessDefVersionStatus getPublicationStatus() {
        return this.publicationStatus;
    }

    @Override
    public String getProcessDefName() {

        return processDefName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getClassification() {
        return this.classification;
    }

    @Override
    public String getDefDescription() {
        return this.defDescription;
    }

    @Override
    public String getSystemCode() {
        return this.systemCode;
    }

    @Override
    public ProcessDefAccess getAccessLevel() {
        return this.accessLevel;
    }

    @Override
    public Date getActiveTime() {
        return this.activeTime;
    }

    @Override
    public Date getFreezeTime() {
        return this.freezeTime;
    }

    @Override
    public String getCreatorId() {
        return this.creatorId;
    }

    @Override
    public String getCreatorName() {
        return this.creatorName;
    }

    @Override
    public Date getCreated() {
        return this.created;
    }

    @Override
    public String getModifierId() {
        return this.modifierId;
    }

    @Override
    public String getModifierName() {
        return this.modifierName;
    }

    @Override
    public Date getModifyTime() {
        return this.modifyTime;
    }

    @Override
    public int getLimit() {
        return this.limit;
    }

    @Override
    public DurationUnit getDurationUnit() {
        return this.durationUnit;
    }

    @Override

    @JSONField(serialize = false)
    public List<ActivityDef> getAllActivityDefs() {
        List<ActivityDef> activityDefs = new ArrayList<ActivityDef>();

        for (String activityDefId : this.getActivityDefIds()) {
            try {
                ActivityDef activityDef = CtBPMCacheManager.getInstance().getActivityDef(activityDefId);
                if (activityDef != null) {
                    activityDefs.add(activityDef);
                }

            } catch (JDSException e) {
                e.printStackTrace();
            }
        }
        return activityDefs;
    }

    @Override

    @JSONField(serialize = false)
    public List<RouteDef> getAllRouteDefs() throws BPMException {
        List<RouteDef> routeDefs = new ArrayList<RouteDef>();

        for (String routeId : this.getRouteDefIds()) {
            try {
                RouteDef routeDef = CtBPMCacheManager.getInstance().getRouteDef(routeId);
                routeDefs.add(routeDef);
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }
        return routeDefs;
    }


    @Override
    public List<String> getActivityDefIds() {
        return this.activityDefIds;
    }

    @Override
    public List<String> getRouteDefIds() {
        return this.routeDefIds;
    }


    @Override
    @JSONField(serialize = false)
    public Object getAppAttribute(String name) {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef>();
        try {
            attributeDefs = CtBPMCacheManager.getInstance().getProcessDefAttributes(processDefVersionId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeDef attributeDef : attributeDefs) {
            if (attributeDef.getType().equals(Attributetype.APPLICATION) && attributeDef.getName().equals(name)) {

                return attributeDef.getValue();
            }
        }
        return null;

    }


    @Override

    @JSONField(serialize = false)
    public String getAttribute(String name) {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef>();
        try {
            attributeDefs = CtBPMCacheManager.getInstance().getProcessDefAttributes(processDefVersionId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeDef attributeDef : attributeDefs) {
            if (attributeDef.getName().equals(name)) {

                return attributeDef.getValue();
            }
        }
        return null;
    }


    @Override

    @JSONField(serialize = false)
    public Object getWorkflowAttribute(String name) {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef>();
        try {
            attributeDefs = CtBPMCacheManager.getInstance().getProcessDefAttributes(processDefVersionId);
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
    public Object getAttribute(Attributetype attributetype, String name) {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef>();
        try {
            attributeDefs = CtBPMCacheManager.getInstance().getProcessDefAttributes(processDefVersionId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeDef attributeDef : attributeDefs) {
            if (attributeDef.getType().equals(attributetype) && attributeDef.getName().equals(name)) {
                return attributeDef.getValue();
            }
        }
        return null;
    }

    @Override
    @JSONField(serialize = false)
    public Object getRightAttribute(String name) {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef>();
        try {
            attributeDefs = CtBPMCacheManager.getInstance().getProcessDefAttributes(processDefVersionId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeDef attributeDef : attributeDefs) {
            if (attributeDef.getType().equals(Attributetype.RIGHT) && attributeDef.getName().equals(name)) {
                return attributeDef.getInterpretedValue();
            }
        }
        return null;
    }



    @Override
    @JSONField(serialize = false)
    public List<AttributeDef> getAllAttribute() {
        try {
            return CtBPMCacheManager.getInstance().getProcessDefAttributes(this.getProcessDefVersionId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return new ArrayList<AttributeDef>();
    }


    @Override
    public List<Listener> getListeners() {
        return this.listeners;
    }

    @Override
    @JSONField(serialize = false)
    public ProcessDefForm getFormDef() throws BPMException {
        return CtBPMCacheManager.getInstance().getProcessFormDef(this.processDefVersionId);
    }
}
