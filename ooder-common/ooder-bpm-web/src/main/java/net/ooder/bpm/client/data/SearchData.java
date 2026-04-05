package net.ooder.bpm.client.data;

import net.ooder.bpm.enums.right.RightConditionEnums;

import java.util.Map;

public class SearchData {


    String processDefId;
    RightConditionEnums conditionEnums;
    int pageIndex;
    int pageSize;
    Long startTime;
    Long endTime;
    String title;
    String fullSearch;
    Map<String,String> formMap;

    String projectId;


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }


    public String getProcessDefId() {
        return processDefId;
    }

    public void setProcessDefId(String processDefId) {
        this.processDefId = processDefId;
    }

    public RightConditionEnums getConditionEnums() {
        return conditionEnums;
    }

    public void setConditionEnums(RightConditionEnums conditionEnums) {
        this.conditionEnums = conditionEnums;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullSearch() {
        return fullSearch;
    }

    public void setFullSearch(String fullSearch) {
        this.fullSearch = fullSearch;
    }

    public Map<String, String> getFormMap() {
        return formMap;
    }

    public void setFormMap(Map<String, String> formMap) {
        this.formMap = formMap;
    }

    public Long getStartTime() {

        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
}
