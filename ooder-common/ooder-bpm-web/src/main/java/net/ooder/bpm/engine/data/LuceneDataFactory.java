package net.ooder.bpm.engine.data;

import com.alibaba.fastjson.JSON;

import net.ooder.bpm.client.data.DataFactory;
import net.ooder.bpm.client.data.DataMap;

import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.index.ActivityHistoryIndex;

import net.ooder.bpm.index.CtBPMIndexFactory;
import net.ooder.common.JDSException;

import net.ooder.index.config.IndexConfigFactroy;
import net.ooder.jds.core.esb.EsbUtil;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LuceneDataFactory implements DataFactory {

    public LuceneDataFactory() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public DataMap getFormMap() {

        return new LuceneDataMap();
    }

    @Override
    public Object getDataByFormId(String formId) {
        return getFormMap().get(formId);
    }

    @Override
    public void update(DataMap formdata) {
        DataMap map = new LuceneDataMap();

        WorkflowClientService client = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));

        String userId = client.getConnectInfo().getUserID();
        for (Iterator<String> it = formdata.keySet().iterator(); it.hasNext(); ) {
            String formId = it.next();
            ActivityHistoryIndex index = new ActivityHistoryIndex(formdata.getSource(), formId, userId, (Map) formdata.get(formId));
            try {
                index = CtBPMIndexFactory.getInstance().addIndex(index);
                String mapStr = index.getValueMap().toString();
                Map data = JSON.parseObject(mapStr, HashMap.class);
                ((HashMap) data).put(IndexConfigFactroy.uuid, index.getUuid());
                formdata.put(formId, data);
            } catch (JDSException e) {
                e.printStackTrace();

            }

        }

    }


    @Override
    public void delete(DataMap map) {
        WorkflowClientService client = ((WorkflowClientService) EsbUtil.parExpression("$BPMC"));

        String userId = client.getConnectInfo().getUserID();
        for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); ) {
            String formId = it.next();
            ActivityHistoryIndex index = new ActivityHistoryIndex(map.getSource(), formId, userId, (Map) map.get(formId));
            CtBPMIndexFactory.getInstance().deleteIndex(index);
        }


    }

}
