/**
 * $RCSfile: CtMsgIndexFactory.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.msg.index;

import net.ooder.annotation.JLuceneIndex;
import net.ooder.common.Condition;
import net.ooder.common.JDSException;
import net.ooder.index.config.IndexConfigFactroy;
import net.ooder.index.service.IndexService;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.query.MsgConditionKey;

import java.util.ArrayList;
import java.util.List;

public class CtMsgIndexFactory {
    static CtMsgIndexFactory cacheManager;

    public static final String THREAD_LOCK = "Thread Lock";

    private final IndexConfigFactroy configFactory;

    public static CtMsgIndexFactory getInstance() {
        if (cacheManager == null) {
            synchronized (THREAD_LOCK) {
                cacheManager = new CtMsgIndexFactory();
            }
        }
        return cacheManager;
    }

    CtMsgIndexFactory() {
        this.configFactory = IndexConfigFactroy.getInstance();
    }


    public List<DataIndex> searchDataIndex(Condition<MsgConditionKey, DataIndex> condition) {
        List<DataIndex> indexs = new ArrayList<>();
        try {
            indexs = getService().search(condition).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return indexs;
    }

    public List<SensorMsgIndex> searchSensorIndex(Condition<MsgConditionKey, SensorMsgIndex> condition) {
        List<SensorMsgIndex> indexs = new ArrayList<>();
        try {
            indexs = getService().search(condition).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return indexs;
    }

    public List<AlarmMsgIndex> searchAlarmIndex(Condition<MsgConditionKey, AlarmMsgIndex> condition) {
        List<AlarmMsgIndex> indexs = new ArrayList<>();
        try {
            indexs = getService().search(condition).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return indexs;
    }

    public List<EventIndex> searchEventIndex(Condition<MsgConditionKey, EventIndex> condition) {
        List<EventIndex> indexs = new ArrayList<>();
        try {
            indexs = getService().search(condition).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return indexs;
    }

    public List<LogIndex> searchLogIndex(Condition<MsgConditionKey, LogIndex> condition) {
        List<LogIndex> indexs = new ArrayList<>();
        try {
            indexs = getService().search(condition).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return indexs;
    }

    public void addIndex(JLuceneIndex index) {
        try {
            if (getService()!=null){
                getService().addIndex(configFactory.getJLuceneConfig(index));
            }

        } catch (Throwable e) {

            //e.printStackTrace();
        }
    }

    public IndexService getService() {
        IndexService service = EsbUtil.parExpression(IndexService.class);
        return service;
    }
}


