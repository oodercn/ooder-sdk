package net.ooder.bpm.client;


import net.ooder.bpm.client.data.FormData;
import net.ooder.esd.annotation.RouteToType;

import java.util.HashMap;
import java.util.Map;

public class RouteToBean {

    RouteToType action;

    FormData dbMap;

    String activityInstId;

    Map<String, PerformBean> multiSelect = new HashMap<String, PerformBean>();

    String activityInstHistoryId;

    public String getActivityInstHistoryId() {
        return activityInstHistoryId;
    }

    public void setActivityInstHistoryId(String activityInstHistoryId) {
        this.activityInstHistoryId = activityInstHistoryId;
    }


    public String getActivityInstId() {
        return activityInstId;
    }

    public void setActivityInstId(String activityInstId) {
        this.activityInstId = activityInstId;
    }

    public FormData getDbMap() {
        return dbMap;
    }

    public void setDbMap(FormData dbMap) {
        this.dbMap = dbMap;
    }

    public Map<String, PerformBean> getMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(Map<String, PerformBean> multiSelect) {
        this.multiSelect = multiSelect;
    }

    public RouteToType getAction() {
        return action;

    }

    public void setAction(RouteToType action) {
        this.action = action;
    }

}
