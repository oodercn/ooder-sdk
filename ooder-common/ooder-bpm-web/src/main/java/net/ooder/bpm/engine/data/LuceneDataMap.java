
package net.ooder.bpm.engine.data;

import com.alibaba.fastjson.JSON;
import net.ooder.annotation.JoinOperator;
import net.ooder.annotation.Operator;
import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.ActivityInstHistory;
import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.client.data.DataFactory;
import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.index.ActivityHistoryIndex;
import net.ooder.bpm.index.ActivityInstIndexEnmu;
import net.ooder.bpm.index.CtBPMIndexFactory;
import net.ooder.common.Condition;
import net.ooder.common.JDSException;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.config.ListResultModel;
import net.ooder.index.config.IndexConfigFactroy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LuceneDataMap extends DataMap implements Cacheable {
    private static final long serialVersionUID = 1L;

    private String systemCode;

    public LuceneDataMap() {
        super();
    }

    public LuceneDataMap(Object source, String userId, String systemCode) {
        super();
        this.source = source;
        this.userId = userId;
        this.systemCode = systemCode;

    }

    public Map getActivityInstData(String formId) {
        Map data = null;
        ActivityHistoryIndex index = new ActivityHistoryIndex(formId, userId);
        Condition<ActivityInstIndexEnmu, ActivityHistoryIndex> condition = new Condition<ActivityInstIndexEnmu, ActivityHistoryIndex>(ActivityInstIndexEnmu.formId, index, Operator.EQUALS, formId);
        ActivityInst activityInst = (ActivityInst) source;
        Condition activityInstcondition = new Condition<ActivityInstIndexEnmu, ActivityHistoryIndex>(ActivityInstIndexEnmu.activityInstId, index, Operator.EQUALS, activityInst.getActivityInstId());
        condition.addCondition(activityInstcondition, JoinOperator.JOIN_AND);
        Condition historycondition = new Condition<ActivityInstIndexEnmu, ActivityHistoryIndex>(ActivityInstIndexEnmu.activityhistoryId, index, Operator.NULL);
        condition.addCondition(historycondition, JoinOperator.JOIN_AND);

        condition.addCondition(activityInstcondition, JoinOperator.JOIN_AND);
        ListResultModel<List<ActivityHistoryIndex>> indexs = CtBPMIndexFactory.getInstance().search(condition);

        try {
            if (indexs.get().size() > 0) {
                data = new HashMap();
                ActivityHistoryIndex aindex = indexs.get().get(0);
                String mapStr = aindex.getValueMap().toString();
                data = JSON.parseObject(mapStr, HashMap.class);
                data.put(IndexConfigFactroy.uuid, aindex.getUuid());
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return data;
    }


    public Map getActivityHistoryData(String formId) {
        Map data = null;
        ActivityHistoryIndex index = new ActivityHistoryIndex(formId, userId);
        Condition<ActivityInstIndexEnmu, ActivityHistoryIndex> condition = new Condition<ActivityInstIndexEnmu, ActivityHistoryIndex>(ActivityInstIndexEnmu.formId, index, Operator.EQUALS, formId);
        ActivityInstHistory history = (ActivityInstHistory) source;
        Condition activityInstcondition = new Condition<ActivityInstIndexEnmu, ActivityHistoryIndex>(ActivityInstIndexEnmu.activityhistoryId, index, Operator.EQUALS, history.getActivityHistoryId());
        condition.addCondition(activityInstcondition, JoinOperator.JOIN_AND);

        ListResultModel<List<ActivityHistoryIndex>> indexs = CtBPMIndexFactory.getInstance().search(condition);
        try {
            if (indexs.get().size() > 0) {
                ActivityHistoryIndex aindex = indexs.get().get(0);
                String mapStr = aindex.getValueMap().toString();
                data = JSON.parseObject(mapStr, HashMap.class);
                ((HashMap) data).put(IndexConfigFactroy.uuid, aindex.getUuid());

            } else {
                data = new HashMap<>();
            }

        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map getProcessInstData(String formId) {
        Map data = null;
        ActivityHistoryIndex index = new ActivityHistoryIndex(formId, userId);
        Condition<ActivityInstIndexEnmu, ActivityHistoryIndex> condition = new Condition<ActivityInstIndexEnmu, ActivityHistoryIndex>(ActivityInstIndexEnmu.formId, index, Operator.EQUALS, formId);
        Condition instcondition = new Condition<ActivityInstIndexEnmu, ActivityHistoryIndex>(ActivityInstIndexEnmu.activityInstId, index, Operator.NULL);
        condition.addCondition(instcondition, JoinOperator.JOIN_AND);

        Condition historycondition = new Condition<ActivityInstIndexEnmu, ActivityHistoryIndex>(ActivityInstIndexEnmu.activityhistoryId, index, Operator.NULL);
        condition.addCondition(historycondition, JoinOperator.JOIN_AND);

        ProcessInst processInst = null;
        try {
            if (source instanceof ActivityInst) {
                processInst = ((ActivityInst) source).getProcessInst();
            } else {
                processInst = (ProcessInst) source;
            }

        } catch (BPMException e) {
            e.printStackTrace();
        }
        Condition activityInstcondition = new Condition<ActivityInstIndexEnmu, ActivityHistoryIndex>(ActivityInstIndexEnmu.processInstId, index, Operator.EQUALS, processInst.getProcessInstId());
        condition.addCondition(activityInstcondition, JoinOperator.JOIN_AND);

        ListResultModel<List<ActivityHistoryIndex>> indexs = CtBPMIndexFactory.getInstance().search(condition);
        try {
            if (indexs.get().size() > 0) {
                ActivityHistoryIndex aindex = indexs.get().get(0);

                String mapStr = aindex.getValueMap().toString();

                data = JSON.parseObject(mapStr, HashMap.class);

                ((HashMap) data).put(IndexConfigFactroy.uuid, aindex.getUuid());
            } else {
                data = new HashMap<>();
            }

        } catch (JDSException e) {
            e.printStackTrace();
        }
        return data;
    }


    @Override
    public Object get(Object key) {
        Object data = super.get(key);
        if (data == null) {

            if (source instanceof ActivityInst) {
                data = this.getActivityInstData(key.toString());
                if (data == null) {
                    data = this.getProcessInstData(key.toString());
                }
            } else if (source instanceof ActivityInstHistory) {
                data = this.getActivityHistoryData(key.toString());


            } else if (source instanceof ProcessInst) {
                data = this.getProcessInstData(key.toString());
            }
            this.put(key, data);

        }
        return data;
    }

    public Object put(Object key, Object value) {
        return super.put(key, value);
    }

    @Override
    public DataMap clone(DataMap dataMap) {
        // LuceneDataMap dataMap = new LuceneDataMap(source, userId, this.getSystemCode());
        //DataMap map = new LuceneDataMap();
        for (Iterator<String> it = keySet().iterator(); it.hasNext(); ) {
            String formId = it.next();
            dataMap.put(formId, get(formId));
        }

        return dataMap;
    }

    @Override
    public <T> T getDAO(String key, Class<T> clazz) {
        return null;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public int getCachedSize() {

        return CacheSizes.sizeOfMap(this);
    }

    public DataFactory getDataFactory() {
        // TODO Auto-generated method stub
        return new LuceneDataFactory();
    }
}
