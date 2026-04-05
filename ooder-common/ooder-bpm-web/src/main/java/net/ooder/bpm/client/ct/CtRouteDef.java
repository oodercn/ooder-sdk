package net.ooder.bpm.client.ct;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.Attributetype;
import net.ooder.bpm.client.*;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.activitydef.ActivityDefPosition;
import net.ooder.bpm.enums.route.RouteCondition;
import net.ooder.bpm.enums.route.RouteDirction;
import net.ooder.common.JDSException;

import java.util.ArrayList;
import java.util.List;

public class CtRouteDef implements RouteDef {

    private final List<Listener> listeners;
    private String routeDefId;

    private String processDefId;

    private String processDefVersionId;

    private String name;

    private String description;

    private String fromActivityDefId;

    private String toActivityDefId;

    private int routeOrder;

    private RouteDirction routeDirection;

    private String routeCondition;


    private RouteCondition routeConditionType;


    public CtRouteDef(RouteDef routeDef) {
        this.description = routeDef.getDescription();
        this.fromActivityDefId = routeDef.getFromActivityDefId();
        this.toActivityDefId = routeDef.getToActivityDefId();
        this.name = routeDef.getName();
        this.routeCondition = routeDef.getRouteCondition();
        this.processDefId = routeDef.getProcessDefId();
        this.processDefVersionId = routeDef.getProcessDefVersionId();
        this.routeConditionType = routeDef.getRouteConditionType();
        this.routeDefId = routeDef.getRouteDefId();
        this.routeDirection = routeDef.getRouteDirection();
        this.routeOrder = routeDef.getRouteOrder();
        this.listeners = routeDef.getListeners();

    }

    @Override
    public String getRouteDefId() {
        return this.routeDefId;
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
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getFromActivityDefId() {
        return this.fromActivityDefId;
    }

    @Override
    public String getToActivityDefId() {
        return this.toActivityDefId;
    }

    @Override
    public int getRouteOrder() {
        return this.routeOrder;
    }

    @Override
    public RouteDirction getRouteDirection() {
        return this.routeDirection;
    }

    @Override
    public String getRouteCondition() {
        return this.routeCondition;
    }

    @Override
    public RouteCondition getRouteConditionType() {
        return this.routeConditionType;
    }

    @Override

    @JSONField(serialize = false)
    public Object getWorkflowAttribute(String name) {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef>();
        try {
            attributeDefs = CtBPMCacheManager.getInstance().getRouteDefAttributes(this.getRouteDefId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        for (AttributeDef attributeDef : attributeDefs) {
            if (attributeDef.getType().equals(Attributetype.ADVANCE) && attributeDef.getName().equals(name)) {
                return attributeDef.getInterpretedValue();
            }
        }
        return null;
    }

    @Override

    @JSONField(serialize = false)
    public Object getRightAttribute(String name) {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef>();
        try {
            attributeDefs = CtBPMCacheManager.getInstance().getRouteDefAttributes(this.getRouteDefId());
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
    public String getAttribute(String name) {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef>();
        try {
            attributeDefs = CtBPMCacheManager.getInstance().getRouteDefAttributes(this.getRouteDefId());
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
    public Object getAttribute(Attributetype attributetype, String name) {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef>();
        try {
            attributeDefs = CtBPMCacheManager.getInstance().getRouteDefAttributes(this.getRouteDefId());
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
    public List<AttributeDef> getAllAttribute() {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef>();
        try {
            attributeDefs = CtBPMCacheManager.getInstance().getRouteDefAttributes(this.getRouteDefId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return attributeDefs;
    }

    @Override

    @JSONField(serialize = false)
    public Object getAppAttribute(String name) {
        List<AttributeDef> attributeDefs = new ArrayList<AttributeDef>();
        try {
            attributeDefs = CtBPMCacheManager.getInstance().getRouteDefAttributes(this.getRouteDefId());
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
    public List<Listener> getListeners() {
        return listeners;
    }

    @Override
    public ActivityDef getFromActivityDef() throws BPMException {
        ActivityDef activityDef = null;
        try {
            activityDef = CtBPMCacheManager.getInstance().getActivityDef(this.getFromActivityDefId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return activityDef;
    }

    @Override

    @JSONField(serialize = false)
    public ActivityDef getToActivityDef() throws BPMException {
        ActivityDef activityDef = null;
        try {
            activityDef = CtBPMCacheManager.getInstance().getActivityDef(this.getToActivityDefId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return activityDef;
    }

    @Override

    @JSONField(serialize = false)
    public ProcessDefVersion getProcessDefVersion() throws BPMException {
        ProcessDefVersion processDefVersion = null;
        try {
            processDefVersion = CtBPMCacheManager.getInstance().getProcessDefVersion(this.getProcessDefVersionId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return processDefVersion;
    }

    @Override

    @JSONField(serialize = false)
    public ProcessDef getProcessDef() throws BPMException {
        ProcessDef processDef = null;
        try {
            processDef = CtBPMCacheManager.getInstance().getProcessDef(this.getProcessDefId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return processDef;
    }

    @Override
    public boolean isToEnd() {
        if (ActivityDefPosition.VIRTUAL_LAST_DEF.getType().equals(this.getToActivityDefId())) {
            return true;
        }
        return false;
    }
}
