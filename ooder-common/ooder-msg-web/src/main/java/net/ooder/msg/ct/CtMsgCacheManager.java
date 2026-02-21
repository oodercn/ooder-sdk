/**
 * $RCSfile: CtMsgCacheManager.java,v $
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
package net.ooder.msg.ct;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import net.ooder.annotation.JLuceneIndex;
import net.ooder.annotation.JoinOperator;
import net.ooder.annotation.Operator;
import net.ooder.common.Condition;
import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.cache.Cache;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.ListResultModel;
import net.ooder.esb.util.EsbFactory;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.msg.Msg;
import net.ooder.msg.MsgAdapter;
import net.ooder.msg.MsgType;
import net.ooder.msg.RMsg;
import net.ooder.msg.client.MsgWebService;
import net.ooder.org.conf.OrgConstants;
import net.ooder.org.query.MsgConditionKey;
import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.*;

public class CtMsgCacheManager implements Serializable {
    private static CtMsgCacheManager cacheManager;

    private static final Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), CtMsgCacheManager.class);

    public static final String msgCacheName = "ctMsg";

    private boolean cacheEnabled = true;

    private Map cacheMap = new HashMap();

    public static CtMsgCacheManager getInstance() {
        if (cacheManager == null) {
            cacheManager = new CtMsgCacheManager();
            cacheManager.initCache();
        }
        return cacheManager;
    }

    private Cache<String, Msg> msgCache;

    private Cache<String, Msg> commandCache;


    /**
     * Initializes all caches with the correct size and expiration time.
     */
    private void initCache() {
        cacheEnabled = CacheManagerFactory.getInstance().getCacheManager(JDSConstants.ORGCONFIG_KEY).isCacheEnabled();
        msgCache = CacheManagerFactory.createCache(JDSConstants.ORGCONFIG_KEY, msgCacheName, 5 * 1024 * 1024, 10 * 60 * 1000);
        commandCache = CacheManagerFactory.createCache(JDSConstants.CONFIG_KEY, "CtCommandmsg", 5 * 1024 * 1024, 10 * 60 * 1000);
        cacheMap = CacheManagerFactory.getInstance().getCacheManagerMap();
    }

    public <T extends Msg> void deleteMsg(String msgId, Class<T> clazz) {


        if (msgId.indexOf("||") == -1) {
            msgId = MsgType.fromClass(clazz).getType() + "||" + msgId;
        }
        this.getService().deleteMsg(msgId);
        msgCache.remove(msgId);


    }

    class RMsgInvocationHandler implements InvocationHandler, Serializable {

        private final Msg rmsg;

        public RMsgInvocationHandler(Msg rmsg) {
            this.rmsg = rmsg;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            for (Class<?> clazz = rmsg.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                try {
                    method = clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());

                    return method.invoke(rmsg, args);
                } catch (Exception e) {
                    // e.printStackTrace();
                    clazz = clazz.getSuperclass();
                }
            }
            return null;
        }
    }


    private <T extends Msg> T full(Msg rmsg, Class<T> classType) {
        List<Class> classes = new ArrayList<Class>();
        classes.addAll(Arrays.asList(classType.getInterfaces()));
        if (classType.isInterface()) {
            classes.add(classType);
        }

        T realType = (T) Enhancer.create(classType, classes.toArray(new Class[classes.size()]), new RMsgInvocationHandler(rmsg));
        return realType;
    }

    /**
     * @param msgId
     * @return
     * @throws JDSException
     */
    public <T extends Msg> T getMsgById(String msgId, Class<T> classType) throws JDSException {

        RMsg rmsg = null;
        if (classType.isAssignableFrom(MsgType.COMMAND.getClazz())) {
            rmsg = (RMsg) commandCache.get(msgId);
            log.info(JSONObject.toJSONString(rmsg));
        }
        if (rmsg == null) {
            if (msgId.indexOf("||") == -1) {
                msgId = MsgType.fromClass(classType).getType() + "||" + msgId;
            }
            rmsg = (RMsg) msgCache.get(msgId);
            if (rmsg == null) {
                rmsg = this.getService().getMsgById(msgId).get();
                msgCache.put(msgId, rmsg);
            }
        }

        T msg = full(rmsg, classType);
        return msg;
    }


    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    /**
     */
    public void invalidate() {
        for (Iterator it = cacheMap.values().iterator(); it.hasNext(); ) {
            Cache cache = (Cache) it.next();
            if (cache != null) {
                cache.clear();
            }
        }
        initCache();
    }

    private <V extends Msg> List<V> getMsgList(List<String> msgIds, Class<V> clazz) throws JDSException {
        List<V> msgs = new ArrayList<V>();
        ArrayList<String> loadIds = new ArrayList<String>();
        for (String msgId : msgIds) {
            Msg msg = null;
            if (!cacheEnabled) {
                loadIds.add(msgId);
            } else {
                msg = msgCache.get(msgId);
                if (msg == null) {
                    loadIds.add(msgId);
                }

            }
        }
        if (loadIds.size() > 0) {
            List<RMsg> loadMsgs = getService().loadMsgs(loadIds.toArray(new String[loadIds.size()])).getData();
            if (loadMsgs != null && loadMsgs.size() > 0) {
                for (RMsg loadMsg : loadMsgs) {
                    if (loadMsg != null) {
                        V msg = full(loadMsg, clazz);
                        msgCache.put(msg.getType() + "||" + loadMsg.getId(), msg);
                    }
                }
            }

        }

        //维持原有排序
        for (String msgId : msgIds) {
            V msg = (V) msgCache.get(msgId);
            if (msg != null) {
                msgs.add(msg);
            }


        }

        return msgs;
    }
//
//
//    //延迟加载
//    class LazyIndex<T extends Msg> extends RemoteObjectList<T> {
//
//        Condition<MsgConditionKey, JLuceneIndex> condition;
//        Class<T> tClass;
//        private List<T> realList;
//        private Integer realsize = 0;
//
//        LazyIndex(Condition<MsgConditionKey, JLuceneIndex> condition, Class<T> tClass) {
//            this.condition = condition;
//            this.tClass = tClass;
//            Condition typecondition = new Condition(MsgConditionKey.MSG_TYPE, Operator.EQUALS, MsgType.fromClass(tClass));
//            condition.addCondition(typecondition, JoinOperator.JOIN_AND);
//            load();
//            size = realsize;
//            elementData = (T[]) new Object[size];
//
//        }
//
//        public void load() {
//            ListResultModel<List<String>> model = getService().findMsgIds(condition);
//            try {
//                realList = getMsgList(model.getData(), tClass);
//                realsize = model.getSize();
//                System.arraycopy(realList, 0, elementData, 0, size);
//            } catch (JDSException e) {
//                e.printStackTrace();
//            }
//        }
//
//        ;
//
//
//        @Override
//        public T get(int index) {
//            Integer realIndex = index;
//            Integer pageIndex = condition.getPage().getPageIndex();
//            Integer pageSize = condition.getPage().getPageSize();
//
//            if (pageIndex == null) {
//                pageIndex = 1;
//            }
//            if (pageSize == null) {
//                pageSize = 100;
//            }
//
//            if (pageIndex < 1) {
//                pageIndex = 1;
//            }
//            int start = (pageIndex - 1) * pageSize;
//            int end = pageSize * pageIndex;
//            if (end > realsize) {
//                end = realsize;
//            }
//
//            //在已读取的区间内
//            if (index < start || index > end) {
//                pageIndex = 0;
//                while (end < pageSize * pageIndex) {
//                    start = pageSize * pageIndex;
//                    pageIndex = pageIndex + 1;
//                }
//                condition.getPage().setPageIndex(pageIndex);
//                load();
//            }
//
//            realIndex = index - start;
//
//            return realList.get(realIndex);
//        }
//
//        @Override
//        public void forEach(Consumer<? super T> action) {
//
//        }
//
//        @Override
//        public Spliterator<T> spliterator() {
//            return null;
//        }
//
//        @Override
//        public Stream<T> stream() {
//            return null;
//        }
//
//        @Override
//        public Stream<T> parallelStream() {
//            return null;
//        }
//
//        @Override
//        protected T getObject(Object obj) {
//            return null;
//        }
//
//        @Override
//        public int size() {
//            return realsize;
//        }
//
//        @Override
//        public boolean removeIf(Predicate<? super T> filter) {
//            return false;
//        }
//
//        @Override
//        public void replaceAll(UnaryOperator<T> operator) {
//
//        }
//
//        @Override
//        public void sort(Comparator<? super T> c) {
//
//        }
//
//    }

    public <V extends Msg, T extends List<V>> ListResultModel<T> findMsgs(Condition<MsgConditionKey, JLuceneIndex> condition, Class<V> tClass) throws JDSException {

        ListResultModel<T> objs = new ListResultModel<T>();
        Condition typecondition = new Condition(MsgConditionKey.MSG_TYPE, Operator.EQUALS, MsgType.fromClass(tClass));
        condition.addCondition(typecondition, JoinOperator.JOIN_AND);
        ListResultModel<List<String>> resultModel = getService().findMsgIds(condition);
        List<String> ids = resultModel.get();
        List<V> msgList = this.getMsgList(ids, tClass);
        objs.setData((T) msgList);
        objs.setSize(resultModel.getSize());

        return objs;

        //  return new LazyIndex(condition,tClass);
    }

//    public <T extends Msg> List<T> findMsgs(Condition<MsgConditionKey> condition, Class<T> tClass) throws JDSException {
//        Condition typecondition = new Condition(MsgConditionKey.MSG_TYPE, Operator.EQUALS, MsgType.fromClass(tClass));
//        condition.addCondition(typecondition, JoinOperator.JOIN_AND);
//
//        List<String> ids = getService().findMsgIds(condition).get();
//        return this.getMsgList(ids, tClass);
//    }

    public <T extends Msg> T creatMsg(Class<T> tClass) {

        RMsg rmsg = JSONObject.parseObject("{}", RMsg.class);
        rmsg.setId(UUID.randomUUID().toString());
        T cmsg = this.full(rmsg, tClass);
        cmsg.setType(MsgType.fromClass(tClass).getType());
        this.msgCache.put(cmsg.getId(), cmsg);
        return cmsg;
    }


    class MsgInvocationHandler implements InvocationHandler, Serializable {

        private final Msg rmsg;

        public MsgInvocationHandler(Msg rmsg) {
            this.rmsg = rmsg;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            for (Class<?> clazz = rmsg.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                try {
                    method = clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());

                    return method.invoke(rmsg, args);
                } catch (Exception e) {
                    clazz = clazz.getSuperclass();
                }
            }
            return null;
        }
    }


    public <T extends Msg> T cloneMsg(Msg msg, Class<T> tClass) {

        List<Class> classes = new ArrayList<Class>();
        classes.addAll(Arrays.asList(tClass.getInterfaces()));
        if (tClass.isInterface()) {
            classes.add(tClass);
        }

        T realMsg = (T) Enhancer.create(tClass, classes.toArray(new Class[classes.size()]), new MsgInvocationHandler(msg));


        realMsg.setId(UUID.randomUUID().toString());
        this.msgCache.put(realMsg.getId(), realMsg);
        return realMsg;
    }

    public void updateMsg(Msg msg) {


        CtRMsg realMsg = new CtRMsg();
        BeanMap dataMap = BeanMap.create(msg);
        BeanMap realMap = BeanMap.create(realMsg);
        Set<String> keySet = dataMap.keySet();

        // 过滤空值

        for (String key : keySet) {
            final Object value = dataMap.get(key);
            if (value != null && !value.equals("")) {
                realMap.put(key, value);
            }
        }
        if (msg.getType().equals(MsgType.COMMAND.getType())) {

            commandCache.put(msg.getId(), realMsg);
        }

        String body = realMsg.getBody();
        try {
            body = java.net.URLEncoder.encode(body, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        realMsg.setBody(body);

        //命令事件需要同步处理
        if (msg.getType().equals(MsgType.COMMAND.getType())) {

            try {
                this.getService().updateMsg(realMsg).get();
            } catch (JDSException e) {
                e.printStackTrace();
            }
        } else {
            this.getService().updateMsg(realMsg).execute();
        }
    }

    public void sendMassMsg(Msg msg, List<String> personIds) {
        int k = 0;
        for (String personId : personIds) {
            //当值大于0时分裂消息
            if (k > 0) {
                msg.setId(UUID.randomUUID().toString());//this.cloneMsg(msg, msg.getClass());
            }
            msg.setReceiver(personId);
            MsgAdapter adapter = EsbFactory.par("$MsgAdapter", MsgAdapter.class);
            if (adapter != null) {
                adapter.submit(msg);
            }
            // updateMsg(msg);
            k++;
        }

    }

    MsgWebService getService() {

        return (MsgWebService) EsbUtil.parExpression(MsgWebService.class);
    }

    public static void main(String[] args) throws IOException {


        String text = "{\"times\":1,\"receiver\":\"29c2badc-0d52-48f4-95e4-22b36449e4a9\",\"systemCode\":\"org\",\"arrivedTime\":1552716967176,\"resultCode\":\"COMMANDINIT\",\"id\":\"cb25f7c8-1fad-479e-a04c-42c903d675ee\",\"type\":\"COMMAND\",\"event\":\"InitGateway\",\"gatewayId\":\"00D6FFFFE6AA528\"}";
        RMsg rmsg = JSONObject.parseObject(text, RMsg.class, Feature.DisableSpecialKeyDetect);
        System.out.println(JSONObject.toJSON(rmsg) + rmsg.getModeId());
    }
}


