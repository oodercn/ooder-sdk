package net.ooder.bpm.client;

import net.ooder.esd.annotation.RouteToType;

public class RouteBean {

    RouteToType action;

    String activityInstId;

    String nextActivityDefId;

    String activityInstHistoryId;

    Perform performs = new Perform();

    Perform readers = new Perform();

    Perform insteadSigns = new Perform();

    public RouteBean() {

    }

    public Perform getPerforms() {
        return performs;
    }

    public void setPerforms(Perform performs) {
        this.performs = performs;
    }

    public Perform getReaders() {
        return readers;
    }

    public void setReaders(Perform readers) {
        this.readers = readers;
    }

    public Perform getInsteadSigns() {
        return insteadSigns;
    }

    public void setInsteadSigns(Perform insteadSigns) {
        this.insteadSigns = insteadSigns;
    }

    public String getActivityInstHistoryId() {
        return activityInstHistoryId;
    }

    public void setActivityInstHistoryId(String activityInstHistoryId) {
        this.activityInstHistoryId = activityInstHistoryId;
    }


    public String getNextActivityDefId() {
        return nextActivityDefId;
    }

    public void setNextActivityDefId(String nextActivityDefId) {
        this.nextActivityDefId = nextActivityDefId;
    }


    public RouteToType getAction() {
        return action;
    }

    public void setAction(RouteToType action) {
        this.action = action;
    }

    public String getActivityInstId() {
        return activityInstId;
    }

    public void setActivityInstId(String activityInstId) {
        this.activityInstId = activityInstId;
    }


}
