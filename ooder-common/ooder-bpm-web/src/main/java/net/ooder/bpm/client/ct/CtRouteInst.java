package net.ooder.bpm.client.ct;

import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.client.RouteInst;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.route.RouteDirction;
import net.ooder.bpm.enums.route.RouteInstType;
import net.ooder.common.JDSException;

import java.util.Date;

public class CtRouteInst  implements RouteInst {

    private String routeInstId;

    private String processInstId;

    private String routeName;;

    private String description;

    private String fromActivityId;

    private String toActivityId;

    private RouteDirction routeDirection;

    private RouteInstType routeType;


    private Date routeTime;


    public CtRouteInst(RouteInst routeInst) {
        this.description=routeInst.getDescription();
        this.fromActivityId=routeInst.getFromActivityId();
        this.processInstId=routeInst.getProcessInstId();
        this.toActivityId=routeInst.getToActivityId();
        this.routeDirection=routeInst.getRouteDirection();
        this.routeInstId=routeInst.getRouteInstId();
        this.routeName=routeInst.getRouteName();
        this.routeTime=routeInst.getRouteTime();
        this.routeType=routeInst.getRouteType();
    }

    @Override
    public String getRouteInstId() {
        return this.routeInstId;
    }

    @Override
    public String getProcessInstId() {
        return this.processInstId;
    }

    @Override
    public String getRouteName() {
        return this.routeName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getFromActivityId() {
        return this.fromActivityId;
    }

    @Override
    public String getToActivityId() {
        return this.toActivityId;
    }

    @Override
    public RouteDirction getRouteDirection() {
        return this.routeDirection;
    }

    @Override
    public RouteInstType getRouteType() {
        return this.routeType;
    }

    @Override
    public Date getRouteTime() {
        return this.routeTime;
    }

    @Override
    public ProcessInst getProcessInst() throws BPMException {
        ProcessInst processInst=null;
        try {
            processInst= CtBPMCacheManager.getInstance().getProcessInst(this.getProcessInstId());
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return processInst;
    }
}
