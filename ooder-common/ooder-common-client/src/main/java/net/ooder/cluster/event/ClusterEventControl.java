
/**
 * $RCSfile: ClusterEventControl.java,v $
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
package net.ooder.cluster.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.cluster.udp.ClusterEvent;
import net.ooder.common.*;
import net.ooder.common.cache.CacheManagerFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.context.JDSActionContext;
import net.ooder.engine.event.JDSEventDispatcher;
import net.ooder.esb.config.manager.EsbBeanFactory;
import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.esb.config.manager.ServiceBean;
import net.ooder.server.JDSServer;
import net.ooder.server.SubSystem;
import net.ooder.server.ct.CtSubSystem;
import net.ooder.vfs.VFSException;
import net.ooder.web.JSONGenSetInvocationHandler;
import net.ooder.web.util.JSONGenUtil;
import net.sf.cglib.proxy.Enhancer;

import java.util.*;
import java.util.Map.Entry;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 服务系统事件控制核心，所有引擎事件都在这里中转处理。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2020
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 4.0
 */
public class ClusterEventControl implements JDSEventDispatcher {

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, ClusterEventControl.class);

    // singleton instance
    private static ClusterEventControl instance = null;

    public static Map<Class, List<ExpressionTempBean>> listenerMap = new HashMap<Class, List<ExpressionTempBean>>();

    public static Map<String, Long> dataEventMap = CacheManagerFactory.createCache(JDSConstants.ORGCONFIG_KEY, "ClusterEventTiemMap", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);


    public static Map<String, ExpressionTempBean> listenerBeanMap = new HashMap<String, ExpressionTempBean>();

    public static ClusterEventControl getInstance() {
        if (instance == null) {
            synchronized (ClusterEventControl.class) {
                if (instance == null) {
                    instance = new ClusterEventControl();
                }
            }
        }
        return instance;
    }

    protected ClusterEventControl() {
        listenerBeanMap.putAll(net.ooder.web.client.ListenerTempAnnotationProxy.getListenerBeanMap());
        List<? extends ServiceBean> esbBeans = EsbBeanFactory.getInstance().loadAllServiceBean();
        for (ServiceBean esbBean : esbBeans) {
            if (esbBean instanceof ExpressionTempBean) {

                listenerBeanMap.put(esbBean.getId(), (ExpressionTempBean) esbBean);
            }

        }
        getListenerByType(ServiceListener.class);
        getListenerByType(ServerListener.class);

    }

    @Override
    public void dispatchEvent(JDSEvent event) throws JDSException {


        if (event instanceof ServerEvent) {
            dispatchServerEvent((ServerEvent) event);
        } else if (event instanceof ServiceEvent) {
            dispatchServiceEvent((ServiceEvent) event);
        }

    }

    /**
     * 分发服务器事件
     *
     * @param event 核心活动事件
     */
    public void dispatchServerEvent(final ServerEvent event) throws JDSException {
        ServerEvent fe = event;
        SubSystem server = fe.getSource();

        if (server != null) {
            String key = event.getID().getMethod() + server.getSysId();
            Long checkOutTime = dataEventMap.get(key);
            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }
            List<JDSListener> listeners = getListenerByType(ServerListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                ServerListener serverListener = (ServerListener) listeners.get(k);
                dispatchServerEvent(event, (ServerListener) listeners.get(k));
            }
        }
    }

    public void dispatchServiceEvent(final ServiceEvent event) throws JDSException {
        //   ServiceEvent fe = event;

        if (event.getSource() != null) {
            String key = event.getID().getMethod() + event.getSource().getId();
            Long checkOutTime = dataEventMap.get(key);
            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 500) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }
            List<JDSListener> listeners = getListenerByType(ServiceListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                ServiceListener ServiceListener = (net.ooder.cluster.event.ServiceListener) listeners.get(k);

                dispatchServiceEvent(event, (net.ooder.cluster.event.ServiceListener) listeners.get(k));

            }
            ServiceListener customerServiceListener = (ServiceListener) event.getContextMap().get("ServiceListener");
            if (customerServiceListener != null) {
                dispatchServiceEvent(event, customerServiceListener);
            }
        }
    }


    public boolean repeatEvent(JDSEvent event, String msgId) throws JDSException {
        Boolean isSend = false;
        if (JDSServer.getInstance().getCurrServerBean().getId().equals(event.getSystemCode())) {

            ClusterEventTypeEnums type = ClusterEventTypeEnums.fromEventClass(event.getClass());
            ClusterEvent clusterEvent = new ClusterEvent();
            clusterEvent.setEventId(event.getID().getMethod());

            if (event.getSource() instanceof String || event.getSource() instanceof Integer || event.getSource() instanceof Double) {
                clusterEvent.setSourceJson(event.getSource().toString());
            } else {
                String source = JSON.toJSONString(event.getSource());
                clusterEvent.setSourceJson(source);
            }
            clusterEvent.setMsgId(msgId);
            clusterEvent.setSessionId(JDSServer.getInstance().getAdminUser().getSessionId());
            clusterEvent.setSystemCode(JDSServer.getInstance().getCurrServerBean().getId());
            clusterEvent.setEventName(type.getEventName());
            clusterEvent.setExpression("$RepeatClusterEvent");
            String eventStr = JSON.toJSONString(clusterEvent);

            isSend = JDSServer.getClusterClient().getUDPClient().send(eventStr);
            logger.info("success repeatEvent [" + isSend + "]" + event.getID());
        }

        return isSend;

    }

    public <T> void dispatchClusterEvent(String objStr, String eventName, String event, String systemCode) throws JDSException {
        ClusterEventTypeEnums type = ClusterEventTypeEnums.fromName(eventName);
        switch (type) {
            case ServiceEvent:
                Class jsonClass = JSONGenUtil.fillSetMethod(ServiceBean.class);
                Object inst = JSONObject.parseObject(objStr, jsonClass);
                ServiceBean proxyObj = (ServiceBean) Enhancer.create(Object.class/* superClass */,
                        new Class[]{ServiceBean.class} /* interface to implement */,
                        new JSONGenSetInvocationHandler(inst)/* callbackMethod to proxy real call */
                );

                ServiceEvent ServiceEvent = new ServiceEvent(proxyObj, ServiceEventEnums.fromMethod(event), systemCode);
                this.dispatchServiceEvent(ServiceEvent);
                break;

            case ServerEvent:
                jsonClass = JSONGenUtil.fillSetMethod(SubSystem.class);
                inst = JSONObject.parseObject(objStr, jsonClass);
                SubSystem systemObj = (SubSystem) Enhancer.create(Object.class/* superClass */,
                        new Class[]{SubSystem.class} /* interface to implement */,
                        new JSONGenSetInvocationHandler(inst)/* callbackMethod to proxy real call */
                );

                CtSubSystem system = new CtSubSystem(systemObj);
                ServerEvent ServerEvent = new ServerEvent(system, ServerEventEnums.fromMethod(event), systemCode);
                this.dispatchServerEvent(ServerEvent);

                break;


            default:
                break;
        }

    }

    public static List<ExpressionTempBean> getListenerTempBeanByType(Class<? extends EventListener> listenerClass) {

        List<JDSListener> listeners = new ArrayList<JDSListener>();
        Set<Entry<String, ExpressionTempBean>> tempEntry = listenerBeanMap.entrySet();

        List<ExpressionTempBean> tempLst = listenerMap.get(listenerClass);

        if (tempLst == null || tempLst.isEmpty()) {
            tempLst = new ArrayList<ExpressionTempBean>();
            for (Entry<String, ExpressionTempBean> entry : tempEntry) {
                ExpressionTempBean bean = entry.getValue();
                String classType = bean.getClazz();
                Class clazz = null;
                try {
                    clazz = ClassUtility.loadClass(classType);
                } catch (ClassNotFoundException e) {
                    continue;
                }

                if (listenerClass.isAssignableFrom(clazz)) {
                    tempLst.add(bean);
                }
                ;
            }
            listenerMap.put(listenerClass, tempLst);

        }
        return tempLst;
        //}
    }

    ;

    private static List<JDSListener> getListenerByType(Class<? extends EventListener> listenerClass) {
        List<ExpressionTempBean> tempLst = getListenerTempBeanByType(listenerClass);
        List<JDSListener> listeners = new ArrayList<JDSListener>();
        for (ExpressionTempBean tempBean : tempLst) {
            JDSListener listener = (JDSListener) JDSActionContext.getActionContext().Par("$" + tempBean.getId());
            if (listener != null) {
                listeners.add(listener);
            }
        }
        return listeners;


    }


    private static void dispatchServiceEvent(final ServiceEvent event, final ServiceListener listener) {
        try {
            switch (event.getID()) {

                case addService:
                    listener.addService(event);
                    break;
                case delService:
                    listener.delService(event);
                    break;
                case updateService:
                    listener.updateService(event);
                    break;
                case addJar:
                    listener.addJar(event);
                    break;

                default:
                    throw new JDSException("Unsupport file event type: " + event.getID(), VFSException.UNSUPPORTPROCESSEVENTERROR);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }

    private static void dispatchServerEvent(final ServerEvent event, final ServerListener listener) {
        try {
            switch (event.getID()) {

                case serverStarting:
                    listener.serverStarting(event);
                    break;
                case serverStarted:
                    listener.serverStarted(event);
                    break;
                case serverStopping:
                    listener.serverStopping(event);
                    break;
                case serverStopped:
                    listener.serverStopped(event);
                    break;
                case systemActivating:
                    listener.systemActivating(event);
                    break;
                case systemActivated:
                    listener.systemActivated(event);
                    break;
                case systemFreezing:
                    listener.systemFreezing(event);
                    break;
                case systemFreezed:
                    listener.systemFreezed(event);
                    break;
                case systemSaving:
                    listener.systemSaving(event);
                    break;
                case systemSaved:
                    listener.systemSaved(event);
                    break;

                case systemDeleting:
                    listener.systemDeleting(event);
                    break;
                case systemDeleted:
                    listener.systemDeleted(event);

                    break;
                default:
                    throw new VFSException("Unsupport folder event type: " + event.getID(), VFSException.UNSUPPORTPROCESSEVENTERROR);
            }
        } catch (Throwable e) {
            logger.warn("Listener execute failed!", e);
        }
    }

}
