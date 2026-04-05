package net.ooder.bpm.client.data;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.bpm.engine.data.DataMapDeserializer;

public class FormData {
    String activityInstId;
    String processInstId;
    String activityHistoryId;

    @JSONField(deserializeUsing = DataMapDeserializer.class)
    DataMap table;

    public DataMap getTable() {
        return table;
    }

    public void setTable(DataMap table) {
        this.table = table;
    }

    public String getActivityInstId() {
        return activityInstId;
    }

    public void setActivityInstId(String activityInstId) {
        this.activityInstId = activityInstId;
    }

    public String getProcessInstId() {
        return processInstId;
    }

    public void setProcessInstId(String processInstId) {
        this.processInstId = processInstId;
    }

    public String getActivityHistoryId() {
        return activityHistoryId;
    }

    public void setActivityHistoryId(String activityHistoryId) {
        this.activityHistoryId = activityHistoryId;
    }

}
