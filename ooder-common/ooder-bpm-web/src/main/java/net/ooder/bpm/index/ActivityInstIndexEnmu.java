package net.ooder.bpm.index;


import net.ooder.common.ConditionKey;

public  enum ActivityInstIndexEnmu implements ConditionKey {
    activityInstId ("activityInstId"),
    processInstId ("processInstId"),
    activityhistoryId ("activityhistoryId"),
    arrivedTime ("arrivedTime"),
    startTime ("startTime"),
    formId ("formId"),
    userId ("userId"),
    valueMap ("valueMap"),
    endTime ("endTime"),
    createtime("createtime");

    private ActivityInstIndexEnmu(String conditionKey) {
        this.conditionKey = conditionKey;
    }

    private String conditionKey;

    public String toString() {
        return conditionKey;
    }

    @Override
    public String getValue() {
        return conditionKey;
    }
}
