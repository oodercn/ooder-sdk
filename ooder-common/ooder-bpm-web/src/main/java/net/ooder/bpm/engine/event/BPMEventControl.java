/**
 * $RCSfile: BPMEventControl.java,v $
 * $Revision: 1.2 $
 * $Date: 2016/01/23 16:29:55 $
 * <p>
 * Copyright (C) 2008 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.Listener;
import net.ooder.bpm.client.ProcessDefVersion;
import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.client.ct.CtBPMCacheManager;
import net.ooder.bpm.client.event.*;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.event.*;
import net.ooder.cluster.udp.ClusterEvent;
import net.ooder.common.*;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.config.JDSConfig;
import net.ooder.context.JDSActionContext;
import net.ooder.engine.event.JDSEventDispatcher;
import net.ooder.esb.config.manager.EsbBeanFactory;
import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.esb.config.manager.ServiceBean;
import net.ooder.server.JDSServer;
import net.ooder.web.JSONGenSetInvocationHandler;
import net.ooder.web.client.ListenerTempAnnotationProxy;
import net.ooder.web.util.JSONGenUtil;
import net.sf.cglib.proxy.Enhancer;

import java.util.*;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 工作流系统事件控制核心，所有引擎事件都在这里中转处理
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhang li
 * @version 2.0
 */
public class BPMEventControl implements JDSEventDispatcher {

    private static final Log logger = LogFactory.getLog(BPMConstants.CONFIG_KEY, BPMEventControl.class);

    public static Map<Class<? extends EventListener>, List<ExpressionTempBean>> listenerMap = new HashMap<Class<? extends EventListener>, List<ExpressionTempBean>>();
    public Map<String, ExpressionTempBean> listenerBeanMap = new HashMap<String, ExpressionTempBean>();

    public static Map<String, Long> dataEventMap = CacheManagerFactory.createCache(JDSConstants.ORGCONFIG_KEY, "dataEventTiemMap");


    // singleton instance
    private static BPMEventControl instance = null;

    public static BPMEventControl getInstance() {
        if (instance == null) {
            synchronized (BPMEventControl.class) {
                if (instance == null) {
                    instance = new BPMEventControl();
                }
            }
        }
        return instance;
    }

    protected BPMEventControl() {
        // 初始化核心事件监听器
        initCoreListeners();
    }

    protected void initCoreListeners() {

        String[] processListeners = JDSConfig.getValues("event.ProcessEventListeners.listener");
        String[] activityListeners = JDSConfig.getValues("event.ActivityEventListeners.listener");


        listenerBeanMap.putAll(ListenerTempAnnotationProxy.getListenerBeanMap());
        List<? extends ServiceBean> esbBeans = EsbBeanFactory.getInstance().loadAllServiceBean();
        for (ServiceBean esbBean : esbBeans) {
            if (esbBean instanceof ExpressionTempBean) {
                listenerBeanMap.put(esbBean.getId(), (ExpressionTempBean) esbBean);
            }

        }
        getListenerByType(ProcessListener.class);
        getListenerByType(ActivityListener.class);
        getListenerByType(RightListener.class);
        getListenerByType(BPDListener.class);

    }

    public <T> void dispatchClusterEvent(String objStr, String eventName, String event, String systemCode) throws JDSException {
        BPMEventTypeEnums type = BPMEventTypeEnums.fromName(eventName);


        Class activityInstClasss = JSONGenUtil.fillSetMethod(ActivityInst.class);
        switch (type) {
            case ProcessEvent:
                Class jsonClass = JSONGenUtil.fillSetMethod(ProcessInst.class);
                Object inst = JSONObject.parseObject(objStr, jsonClass);
                ProcessInst proxyObj = (ProcessInst) Enhancer.create(Object.class/* superClass */,
                        new Class[]{ProcessInst.class} /* interface to implement */,
                        new JSONGenSetInvocationHandler(inst)/* callbackMethod to proxy real call */
                );

                ProcessInst processInst = CtBPMCacheManager.getInstance().getProcessInst(proxyObj.getProcessInstId());
                ProcessEvent processEvent = new ProcessEvent(processInst, ProcessEventEnums.fromMethod(event), null, new HashMap());
                // ProcessEvent processEvent = new ProcessEvent(new CtProcessInst(proxyObj), ProcessEventEnums.fromMethod(event), null, new HashMap());
                this.dispatchProcessEvent(processEvent);
                break;

            case ActivityEvent:
                List josnobj = JSONArray.parseArray(objStr, activityInstClasss);
                List activityInstList = new ArrayList();
                for (Object json : josnobj) {
                    ActivityInst activityInst = (ActivityInst) Enhancer.create(Object.class/* superClass */,
                            new Class[]{ActivityInst.class} /* interface to implement */,
                            new JSONGenSetInvocationHandler(json)/* callbackMethod to proxy real call */
                    );
                    ActivityInst ctivityInst = CtBPMCacheManager.getInstance().getActivityInst(activityInst.getActivityInstId());
                    activityInstList.add(ctivityInst);
                }

                ActivityEvent activityevent = new ActivityEvent(activityInstList, ActivityEventEnums.fromMethod(event), null, new HashMap());
                this.dispatchActivityEvent(activityevent);

                break;

            case RightEvent:
                Object josnObj = JSONObject.parseObject(objStr, activityInstClasss);
                ActivityInst activityInst = (ActivityInst) Enhancer.create(Object.class/* superClass */,
                        new Class[]{ActivityInst.class} /* interface to implement */,
                        new JSONGenSetInvocationHandler(josnObj)/* callbackMethod to proxy real call */
                );

                ActivityInst ctivityInst = CtBPMCacheManager.getInstance().getActivityInst(activityInst.getActivityInstId());
                RightEvent rightEvent = new RightEvent(ctivityInst, RightEventEnums.fromMethod(event), null, new HashMap());

                //  RightEvent rightEvent = new RightEvent(new CtActivityInst(activityInst), RightEventEnums.fromMethod(event), null, new HashMap());
                this.dispatchRightEvent(rightEvent);

                break;
            case BPDEvent:
                Class processClass = JSONGenUtil.fillSetMethod(ProcessDefVersion.class);
                Object processVersionObject = JSONObject.parseObject(objStr, processClass);
                ProcessDefVersion processVersionObj = (ProcessDefVersion) Enhancer.create(Object.class/* superClass */,
                        new Class[]{ProcessDefVersion.class} /* interface to implement */,
                        new JSONGenSetInvocationHandler(processVersionObject)/* callbackMethod to proxy real call */
                );
                ProcessDefVersion processVersion = CtBPMCacheManager.getInstance().getProcessDefVersion(processVersionObj.getProcessDefVersionId());
                BPDEvent bpdEvent = new BPDEvent(processVersion, BPDEventEnums.fromMethod(event), new HashMap());

                // BPDEvent bpdEvent = new BPDEvent(new CtProcessDefVersion(processVersionObj), BPDEventEnums.fromMethod(event), new HashMap());
                this.dispatchBPDEvent(bpdEvent);

                break;
            default:
                break;
        }

    }

    public boolean repeatEvent(BPMEvent event, String msgId) throws JDSException {
        Boolean isSend = false;

        ConfigCode configCode = JDSServer.getInstance().getCurrServerBean().getConfigCode();
        if (configCode.equals(ConfigCode.scene) || configCode.equals(ConfigCode.cluster)) {

            BPMEventTypeEnums type = BPMEventTypeEnums.fromEventClass(event.getClass());
            ClusterEvent clusterEvent = new ClusterEvent();
            clusterEvent.setEventId(event.getID().getMethod());

            String source = JSON.toJSONString(event.getSource());
            clusterEvent.setSourceJson(source);
            if (event.getClientService() != null) {
                clusterEvent.setSessionId(event.getClientService().getSessionHandle().getSessionID());
                clusterEvent.setSystemCode(event.getClientService().getSystemCode());
            } else {
                clusterEvent.setSessionId(JDSServer.getInstance().getAdminUser().getSessionId());
                clusterEvent.setSystemCode(JDSServer.getInstance().getAdminUser().getSystemCode());
            }

            clusterEvent.setMsgId(msgId);

            clusterEvent.setEventName(type.getEventName());
            clusterEvent.setExpression("$RepeatBPMEvent");
            String eventStr = JSON.toJSONString(clusterEvent);

            isSend = JDSServer.getClusterClient().getUDPClient().send(eventStr);
            logger.info("success repeatBPMEvent [" + isSend + "]" + event.getID());
        }

        return isSend;

    }


    private synchronized List<JDSListener> getListenerByType(Class<? extends EventListener> listenerClass) {
        List<JDSListener> listeners = new ArrayList<JDSListener>();
        Set<Map.Entry<String, ExpressionTempBean>> tempEntry = listenerBeanMap.entrySet();

        List<ExpressionTempBean> tempLst = listenerMap.get(listenerClass);

        if (tempLst == null || tempLst.isEmpty()) {
            tempLst = new ArrayList<ExpressionTempBean>();
            for (Map.Entry<String, ExpressionTempBean> entry : tempEntry) {
                ExpressionTempBean bean = entry.getValue();
                String classType = bean.getClazz();
                if (classType != null) {
                    Class clazz = null;
                    try {
                        clazz = ClassUtility.loadClass(classType);
                    } catch (ClassNotFoundException e) {
                        continue;
                    }
                    if (listenerClass.isAssignableFrom(clazz)) {
                        tempLst.add(bean);
                    }
                }


                ;
            }
            listenerMap.put(listenerClass, tempLst);

        }

        for (ExpressionTempBean tempBean : tempLst) {

            JDSListener listener = (JDSListener) JDSActionContext.getActionContext().Par("$" + tempBean.getId());
            if (listener != null) {
                listeners.add(listener);
            }

        }

        return listeners;
    }


    /**
     * 事件分发方法，所有的BPM事件都通过该方法进行分发。
     *
     * @param event BPM事件
     */
    public void dispatchEvent(JDSEvent event) throws JDSException {

        if (event != null) {

            if (event instanceof ProcessEvent) {
                dispatchProcessEvent((ProcessEvent) event);
            } else if (event instanceof ActivityEvent) {
                dispatchActivityEvent((ActivityEvent) event);
            } else if (event instanceof BPDEvent) {
                dispatchBPDEvent((BPDEvent) event);
            } else if (event instanceof RightEvent) {
                dispatchRightEvent((RightEvent) event);
            }
        }
    }


    /**
     * 分发流程事件
     *
     * @param event 核心活动事件
     */
    private void dispatchProcessEvent(final ProcessEvent event) throws JDSException {
        ProcessEvent pe = event;
        ProcessInst processInst = pe.getProcessInst();
        if (processInst != null) {

            String key = event.getID() + processInst.getProcessInstId();

            Long checkOutTime = dataEventMap.get(key);

            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }

            // 取得流程定义监听器列表
            List processListenerList = null;
            try {
                processListenerList = processInst.getProcessDefVersion().getListeners();
            } catch (BPMException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            for (int i = 0; i < processListenerList.size(); i++) {
                Listener listener = (Listener) processListenerList.get(i);

                // 判断监听器类型为流程监听器
                if (ListenerEnums.PROCESS_LISTENER_EVENT.equals(listener.getListenerEvent())) {
                    // 增加对表达式的处理能力

                    event.setListener(listener);
                    String listenerClass = listener.getRealizeClass();
                    try {
                        ProcessListener processListener = (ProcessListener) ClassUtility.loadClass(listenerClass).newInstance();
                        dispatchProcessEvent(pe, processListener);
                    } catch (Exception e) {
                        logger.error("Error occured when dispatch process event to listener: " + listenerClass, e);
                    }
                }
            }
        }
    }

    private void dispatchProcessEvent(final ProcessEvent event, final ProcessListener listener) {
        try {
            switch ((ProcessEventEnums) event.getID()) {
                case STARTING:
                    // 流程实例正在被启动
                    listener.processStarting(event);
                    break;
                case STARTED:
                    // 流程实例已经被启动
                    listener.processStarted(event);
                    break;
                case SAVING:
                    // 流程实例正在被保存
                    listener.processSaving(event);
                    break;
                case SAVED:
                    // 流程实例已经被保存
                    listener.processSaved(event);
                    break;
                case SUSPENDING:
                    // 流程实例正在被挂起
                    listener.processSuspending(event);
                    break;
                case SUSPENDED:
                    // 流程实例已经被挂起
                    listener.processSuspended(event);
                    break;
                case RESUMING:
                    // 流程实例正在被恢复（从挂起状态）
                    listener.processResuming(event);
                    break;
                case RESUMED:
                    // 流程实例已经被恢复（从挂起状态）
                    listener.processResumed(event);
                    break;
                case ABORTING:
                    // 流程实例正在被取消
                    listener.processAborting(event);
                    break;
                case ABORTED:
                    // 流程实例已经被取消
                    listener.processAborted(event);
                    break;
                case COMPLETING:
                    // 流程实例正在被完成
                    listener.processCompleting(event);
                    break;
                case COMPLETED:
                    // 流程实例已经被完成
                    listener.processCompleted(event);
                    break;
                case DELETING:
                    // 流程实例正在被删除
                    listener.processDeleting(event);
                    break;
                case DELETED:
                    // 流程实例已经被删除
                    listener.processDeleted(event);
                    break;

                default:
                    throw new JDSException("Unsupport process event type: " + event.getID(), JDSException.UNSUPPORTPROCESSEVENTERROR);
            }
        } catch (Throwable e) {
            logger.warn("Listener execute failed!", e);
        }
    }


    /**
     * 分发流程定义事件
     *
     * @param event 核心活动事件
     */
    private void dispatchBPDEvent(final BPDEvent event) throws JDSException {
        BPDEvent ae = event;
        ProcessDefVersion processDefVersion = (ProcessDefVersion) ae.getSource();


        String key = event.getID() + processDefVersion.getProcessDefVersionId();

        Long checkOutTime = dataEventMap.get(key);

        //服务端分发
        if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
            dataEventMap.put(key, System.currentTimeMillis());
            repeatEvent(event, key);
        }

        //客户端端分发
        List<JDSListener> bpdListenerList = this.getListenerByType(BPDListener.class);


        for (int j = 0; j < bpdListenerList.size(); j++) {
            BPDListener listener = (BPDListener) bpdListenerList.get(j);
            event.setListener(listener);
            dispatchBPDEvent(ae, listener);
        }

    }

    ;


    private void dispatchBPDEvent(final BPDEvent event, final BPDListener listener) {
        try {
            switch (event.getID()) {
                case PROCESSDEFCREATED:

                    listener.ProcessDefCreaded(event);
                    break;
                case PROCESSDEFDELETED:

                    listener.ProcessDefDeleted(event);
                    break;
                case PROCESSDEFFREEZED:

                    listener.ProcessDefFreezed(event);
                    break;
                case PROCESSDEFUPDATE:

                    listener.ProcessDefUpdated(event);
                    break;
                case PROCESSDEFACTIVATED:

                    listener.ProcessDefActivaed(event);
                    break;
                default:
                    throw new JDSException("Unsupport BPD event type: " + event.getID(), JDSException.UNSUPPORTACTIVITYEVENTERROR);

            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }

    }

    /**
     * 分发权限事件
     *
     * @param event 核心活动事件
     */
    private void dispatchRightEvent(final RightEvent event) throws JDSException {
        RightEvent ae = event;
        ActivityInst activityInst = (ActivityInst) ae.getSource();


        String key = event.getID() + activityInst.getProcessInstId();

        Long checkOutTime = dataEventMap.get(key);

        if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
            dataEventMap.put(key, System.currentTimeMillis());
            repeatEvent(event, key);
        }


        List rightListenerList = null;
        try {
            rightListenerList = activityInst.getActivityDef().getListeners();
        } catch (BPMException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        for (int j = 0; j < rightListenerList.size(); j++) {
            Listener listener = (Listener) rightListenerList.get(j);
            event.setListener(listener);


            // 判断监听器类型为活动监听器
            if (ListenerEnums.RIGHT_LISTENER_EVENT.equals(listener.getListenerEvent())) {

                String listenerClass = listener.getRealizeClass();
                try {
                    RightListener rightListener = (RightListener) ClassUtility.loadClass(listenerClass).newInstance();

                    dispatchRightEvent(ae, rightListener);
                } catch (Throwable e) {
                    logger.error("Error occured when dispatch right event to listener: " + listenerClass, e);
                }
            }

        }
    }

    /**
     * 分发活动事件
     *
     * @param event 核心活动事件
     */
    private void dispatchActivityEvent(final ActivityEvent event) throws JDSException {
        ActivityEvent ae = event;
        List<ActivityInst> activityInsts = ae.getActivityInsts();


        String key = event.getID().getMethod() + activityInsts.get(0).getActivityInstId();
        Long checkOutTime = dataEventMap.get(key);

        if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
            dataEventMap.put(key, System.currentTimeMillis());
            repeatEvent(event, key);
        }
        if (activityInsts != null) {

            // 事件中的活动实例数组并不代表需要多次调用事件，而是一个事件中涉及的多个对象，所以仅对第一个就可以了
            // for (int i = 0; i < activityInsts.length; i++) {
            ActivityInst activityInst = activityInsts.get(0);
            // 1. 将该活动事件分发到所属流程配置的默认活动监听器
            // 取得当前活动所属流程定义的监听器列表
            // List processListenerList = activityInst.getProcessInst()
            // .getProcessDefVersion().getListeners();
            // for (int j = 0; j < processListenerList.size(); j++) {
            // Listener listener = (Listener) processListenerList.get(j);
            // event.setListener(listener);
            //
            // // 判断监听器类型为活动监听器
            // if (Listener.ACTIVITY_LISTENER_EVENT.equalsIgnoreCase(listener
            // .getListenerEvent())) {
            // String listenerClass = listener.getRealizeClass();
            // try {
            // ActivityListener activityListener = (ActivityListener) ClassUtility
            // .loadClass(listenerClass).newInstance();
            // dispatchActivityEvent(ae, activityListener);
            // } catch (Exception e) {
            // logger.error(
            // "Error occured when dispatch activity event to listener: "
            // + listenerClass, e);
            // }
            // }
            // }


            //分发系统事件
            List<JDSListener> activityCoreListenerList = this.getListenerByType(ActivityListener.class);

            for (int j = 0; j < activityCoreListenerList.size(); j++) {
                ActivityListener listener = (ActivityListener) activityCoreListenerList.get(j);
                event.setListener(listener);
                dispatchActivityEvent(ae, listener);

            }


            //分发自定义事件
            // 2. 分发活动事件到该活动配置的事件监听器
            List activityListenerList = null;
            try {
                activityListenerList = activityInst.getActivityDef().getListeners();
            } catch (BPMException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            for (int j = 0; j < activityListenerList.size(); j++) {
                Listener listener = (Listener) activityListenerList.get(j);
                event.setListener(listener);
                // 判断监听器类型为活动监听器
                if (ListenerEnums.ACTIVITY_LISTENER_EVENT.equals(listener.getListenerEvent())) {

                    String listenerClass = listener.getRealizeClass();
                    try {
                        ActivityListener activityListener = (ActivityListener) ClassUtility.loadClass(listenerClass).newInstance();
                        dispatchActivityEvent(ae, activityListener);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        logger.error("Error occured when dispatch activity event to listener: " + listenerClass, e);
                    }
                }

            }
            // }
        }
    }

    private void dispatchRightEvent(final RightEvent event, final RightListener listener) {
        try {
            switch (event.getID()) {
                case ROUTETO:

                    listener.routeTo(event);
                    break;
                case COPYTO:

                    listener.copyTo(event);
                    break;
                case SIGNRECEIVE:

                    listener.signReceive(event);
                    break;
                case ENDREAD:

                    listener.endRead(event);
                    break;
                case ROUTBACK:

                    listener.routeBack(event);
                    break;
                case TACKBACK:

                    listener.tackBack(event);
                    break;
                case CHANGEPERFORMER:

                    listener.changePerformer(event);
                    break;

                default:
                    throw new JDSException("Unsupport right event type: " + event.getID(), JDSException.UNSUPPORTACTIVITYEVENTERROR);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }

    }


    private void dispatchActivityEvent(final ActivityEvent event, final ActivityListener listener) {
        try {
            switch (event.getID()) {
                case INITED:
                    // 活动初始化完毕，进入inactive状态
                    listener.activityInited(event);
                    break;
                case ROUTING:
                    // 活动开始执行路由操作
                    listener.activityRouting(event);
                    break;
                case ROUTED:
                    // 活动完成路由操作
                    listener.activityRouted(event);
                    break;
                case ACTIVING:
                    // 活动开始被激活(进入active状态)
                    listener.activityActiving(event);
                    break;
                case ACTIVED:
                    // 活动完成激活(进入active状态)
                    listener.activityActived(event);
                    break;
                case SAVEING:
                    // 表单开始保存
                    listener.activityFormSaveing(event);
                    break;
                case SAVEED:
                    // 表单已经保存
                    listener.activityFormSaveed(event);
                    break;
                case SPLITING:
                    // 活动开始执行路由分裂
                    listener.activitySpliting(event);
                    break;
                case SPLITED:
                    // 活动已经分裂为多个活动实例
                    listener.activitySplited(event);
                    break;
                case JOINING:
                    // 活动开始执行合并操作
                    listener.activityJoining(event);
                    break;
                case JOINED:
                    // 活动已经完成合并操作
                    listener.activityJoined(event);
                    break;
                case OUTFLOWING:
                    // 活动开始跳转到其他流程上
                    listener.activityOutFlowing(event);
                    break;
                case OUTFLOWED:
                    // 活动已经跳转到其他流程上
                    listener.activityOutFlowed(event);
                    break;
                case OUTFLOWRETURNING:
                    // 外流活动开始返回
                    listener.activityOutFlowReturning(event);
                    break;
                case OUTFLOWRETURNED:
                    // 外流活动完成返回
                    listener.activityOutFlowReturned(event);
                    break;
                case SUSPENDING:
                    // 活动开始挂起
                    listener.activitySuspending(event);
                    break;
                case SUSPENDED:
                    // 活动已经挂起
                    listener.activitySuspending(event);
                    break;
                case RESUMING:
                    // 活动开始恢复
                    listener.activityResuming(event);
                    break;
                case RESUMED:
                    // 活动已经恢复
                    listener.activityResumed(event);
                    break;
                case COMPLETING:
                    // 活动开始完成
                    listener.activityCompleting(event);
                    break;
                case COMPLETED:
                    // 活动已经完成
                    listener.activityCompleted(event);
                    break;
                case TAKEBACKING:
                    // 活动开始收回
                    listener.activityTakebacking(event);

                    break;
                case TAKEBACKED:
                    // 活动已经收回
                    listener.activityTakebacked(event);
                    break;
                case DISP:
                    // 展示活动表单
                    listener.activityDisplay(event);
                    break;

                default:
                    throw new JDSException("Unsupport activity event type: " + event.getID(), JDSException.UNSUPPORTACTIVITYEVENTERROR);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }

    }

}
