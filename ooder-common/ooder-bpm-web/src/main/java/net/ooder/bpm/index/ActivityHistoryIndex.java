package net.ooder.bpm.index;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.JLuceneIndex;
import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.ActivityInstHistory;
import net.ooder.bpm.client.ProcessInst;
import net.ooder.index.config.IndexConfigFactroy;
import net.ooder.index.config.type.*;
import org.apache.lucene.document.Field.Store;

import java.util.Map;

@JDocumentType(name = "ActivityInstHistoryIndex", fsDirectory = @FSDirectoryType(id = "ActivityInstHistoryIndex"), vfsJson = @VFSJsonType(vfsPath = "doc/log/", fileName = "vfsLog.js"), indexWriter = @JIndexWriterType(id = "ActivityInstHistory"))
public class ActivityHistoryIndex implements JLuceneIndex {
    @JFieldType(store = Store.YES)
    String activityInstId;

    @JFieldType(store = Store.YES)
    String userId;
    @JFieldType(store = Store.YES)
    String activityHistoryId;
    @JFieldType(store = Store.YES)
    String processInstId;
    @JFieldType(store = Store.YES, highlighter = true)
    StringBuffer valueMap = new StringBuffer();


    @JFieldType(store = Store.YES)

    String formId;

    @JFieldType(store = Store.YES)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    Long arrivedTime;


    @JFieldType(store = Store.YES)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    Long startTime;


    @JFieldType(store = Store.YES)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    Long endTime;

    String uuid;


    String path = "bpmindex";

    public ActivityHistoryIndex() {

    }

    public ActivityHistoryIndex(String formId, String userId) {
        this.formId = formId;
        this.userId = userId;
        this.path="bpmindex/"+formId;
    }


    public ActivityHistoryIndex(Object object, String formId, String userId, Map map) {
        this.formId = formId;
        this.userId = userId;
        this.path="bpmindex/"+formId;


        if (map.get(IndexConfigFactroy.uuid)!=null){
            this.uuid= (String) map.get(IndexConfigFactroy.uuid);
        }

        this.valueMap = new StringBuffer(JSONObject.toJSONString(map));

        if (object instanceof ProcessInst) {

            ProcessInst processInst = (ProcessInst) object;
            this.processInstId = processInst.getProcessInstId();

            if (processInst.getEndTime()!=null){
                this.arrivedTime = processInst.getEndTime().getTime();
                this.endTime = processInst.getEndTime().getTime();
            }

            this.startTime = processInst.getStartTime().getTime();

        } else if (object instanceof ActivityInst) {
            ActivityInst activityInst = (ActivityInst) object;
            this.activityInstId = activityInst.getActivityInstId();
            this.processInstId = activityInst.getProcessInstId();
            this.arrivedTime = activityInst.getArrivedTime().getTime();
            this.startTime = activityInst.getStartTime()==null?null:activityInst.getStartTime().getTime();

        } else if (object instanceof ActivityInstHistory) {
            ActivityInstHistory activityInstHistory = (ActivityInstHistory) object;
            this.activityInstId = activityInstHistory.getActivityInstId();
            this.activityHistoryId = activityInstHistory.getActivityHistoryId();
            this.processInstId = activityInstHistory.getProcessInstId();
            this.arrivedTime = activityInstHistory.getArrivedTime().getTime();
            this.endTime = activityInstHistory.getEndTime().getTime();
            this.startTime = activityInstHistory.getStartTime().getTime();
            this.valueMap = new StringBuffer(JSONObject.toJSONString(map));
        }

    }


    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getActivityHistoryId() {
        return activityHistoryId;
    }

    public void setActivityHistoryId(String activityHistoryId) {
        this.activityHistoryId = activityHistoryId;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
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

    public StringBuffer getValueMap() {
        return valueMap;
    }

    public void setValueMap(StringBuffer valueMap) {
        this.valueMap = valueMap;
    }


    public Long getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(Long arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getUuid() {
          return  uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid=uuid;
    }

    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
