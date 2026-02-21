
package net.ooder.agent.client.home.engine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.iot.*;
import net.ooder.agent.client.iot.ct.CtDevice;
import net.ooder.agent.client.iot.ct.CtDeviceEndPoint;
import net.ooder.agent.client.iot.ct.CtPlace;
import net.ooder.agent.client.iot.ct.CtZNode;
import net.ooder.agent.client.iot.enums.*;
import  net.ooder.cluster.udp.ClusterEvent;
import  net.ooder.common.*;
import  net.ooder.common.cache.CacheManagerFactory;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.common.util.ClassUtility;
import  net.ooder.context.JDSActionContext;
import  net.ooder.engine.event.JDSEventDispatcher;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.esb.config.manager.EsbBeanFactory;
import  net.ooder.esb.config.manager.ExpressionTempBean;
import  net.ooder.esb.config.manager.ServiceBean;
import  net.ooder.agent.client.home.event.*;
import  net.ooder.msg.index.DataIndex;
import  net.ooder.server.JDSServer;
import  net.ooder.web.JSONGenSetInvocationHandler;
import  net.ooder.web.RemoteConnectionManager;
import  net.ooder.web.client.ListenerTempAnnotationProxy;
import  net.ooder.web.util.JSONGenUtil;
import net.sf.cglib.proxy.Enhancer;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

/**
 * <p>
 * Title: Home系统管理系统
 * </p>
 * <p>
 * Description: 系统事件控制核心，所有引擎事件都在这里中转处理
 * </p>
 * 支持分布式事件分发
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 4.0
 */
public class HomeEventControl implements JDSEventDispatcher {

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, HomeEventControl.class);

    public static Map<String, Long> dataEventMap = CacheManagerFactory.createCache(JDSConstants.ORGCONFIG_KEY, "dataEventTiemMap",5 * 1024 * 1024, 60 * 60 * 1000);


    // singleton instance
    private static HomeEventControl instance = null;

    public static Map<Class<? extends EventListener>, List<ExpressionTempBean>> listenerMap = new HashMap<Class<? extends EventListener>, List<ExpressionTempBean>>();

    public static HomeEventControl getInstance() {
        if (instance == null) {
            synchronized (HomeEventControl.class) {
                if (instance == null) {
                    instance = new HomeEventControl();
                }
            }
        }
        return instance;
    }

    public Map<String, ExpressionTempBean> listenerBeanMap = new HashMap<String, ExpressionTempBean>();

    public HomeEventControl() {

        listenerBeanMap.putAll(ListenerTempAnnotationProxy.getListenerBeanMap());
        List<? extends ServiceBean> esbBeans = EsbBeanFactory.getInstance().loadAllServiceBean();
        for (ServiceBean esbBean : esbBeans) {
            if (esbBean instanceof ExpressionTempBean) {
                listenerBeanMap.put(esbBean.getId(), (ExpressionTempBean) esbBean);
            }
        }
        getListenerByType(DataListener.class);
        getListenerByType(DeviceEndPointListener.class);
        getListenerByType(ZNodeListener.class);
        getListenerByType(DeviceListener.class);
        getListenerByType(CommandListener.class);
        getListenerByType(PlaceListener.class);
        getListenerByType(SensorListener.class);
        getListenerByType(GatewayListener.class);

    }

    public <T> void dispatchClusterEvent(String objStr, String eventName, String event, String systemCode) throws JDSException {
        EventTypeEnums type = EventTypeEnums.fromName(eventName);

        switch (type) {
            case SensorEvent:
                Class jsonClass = JSONGenUtil.fillSetMethod(ZNode.class);
                Object inst = JSONObject.parseObject(objStr, jsonClass);
                ZNode proxyObj = (ZNode) Enhancer.create(Object.class/* superClass */,
                        new Class[]{ZNode.class} /* interface to implement */,
                        new JSONGenSetInvocationHandler(inst)/* callbackMethod to proxy real call */
                );

                ZNode sensorznode = new CtZNode(proxyObj);

                SensorEvent sensorevent = new SensorEvent(sensorznode, null, SensorEventEnums.fromMethod(event), systemCode);
                dispatchSensorEvent(sensorevent);

                break;

            case DataEvent:

                DataIndex data = JSONObject.parseObject(objStr, DataIndex.class);
                DataEvent dataevent = new DataEvent(data, null, DataEventEnums.fromMethod(event), systemCode);
                dispatchDataEvent(dataevent);
                break;

            case GatewayEvent:
                jsonClass = JSONGenUtil.fillSetMethod(ZNode.class);
                inst = JSONObject.parseObject(objStr, jsonClass);
                proxyObj = (ZNode) Enhancer.create(Object.class/* superClass */,
                        new Class[]{ZNode.class} /* interface to implement */,
                        new JSONGenSetInvocationHandler(inst)/* callbackMethod to proxy real call */
                );

                ZNode gznode = new CtZNode(proxyObj);

                GatewayEvent gatewayevent = new GatewayEvent(gznode, null, GatewayEventEnums.fromMethod(event), systemCode);
                dispatchGatewayEvent(gatewayevent);
                break;

            case ZNodeEvent:
                ZNode znode = new CtZNode(JSONObject.parseObject(objStr, ZNode.class));
                jsonClass = JSONGenUtil.fillSetMethod(ZNode.class);
                inst = JSONObject.parseObject(objStr, jsonClass);
                proxyObj = (ZNode) Enhancer.create(Object.class/* superClass */,
                        new Class[]{ZNode.class} /* interface to implement */,
                        new JSONGenSetInvocationHandler(inst)/* callbackMethod to proxy real call */
                );

                ZNode zznode = new CtZNode(proxyObj);

                ZNodeEvent znodeevent = new ZNodeEvent(zznode, null, ZnodeEventEnums.fromMethod(event), systemCode);
                this.dispatchZNodeEvent(znodeevent);
                break;

            case PlaceEvent:

                jsonClass = JSONGenUtil.fillSetMethod(Place.class);
                inst = JSONObject.parseObject(objStr, jsonClass);
                Place palceproxyObj = (Place) Enhancer.create(Object.class/* superClass */,
                        new Class[]{Place.class} /* interface to implement */,
                        new JSONGenSetInvocationHandler(inst)/* callbackMethod to proxy real call */
                );

                Place place = new CtPlace(palceproxyObj);
                PlaceEvent placeEvent = new PlaceEvent(place, null, PlaceEventEnums.fromMethod(event), systemCode);
                this.dispatchPlaceEvent(placeEvent);
                break;
            case DeviceEndPointEvent:

                jsonClass = JSONGenUtil.fillSetMethod(DeviceEndPoint.class);
                inst = JSONObject.parseObject(objStr, jsonClass);
                DeviceEndPoint deviceEndPointProxy = (DeviceEndPoint) Enhancer.create(Object.class/* superClass */,
                        new Class[]{DeviceEndPoint.class} /* interface to implement */,
                        new JSONGenSetInvocationHandler(inst)/* callbackMethod to proxy real call */
                );

                DeviceEndPoint endPoint = new CtDeviceEndPoint(deviceEndPointProxy);
                DeviceEndPointEvent endPointEvent = new DeviceEndPointEvent(endPoint, null, DeviceEndPointEventEnums.fromMethod(event), systemCode);
                this.dispatchDeviceEndPointEvent(endPointEvent);
                break;
            case DeviceEvent:

                jsonClass = JSONGenUtil.fillSetMethod(Device.class);
                inst = JSONObject.parseObject(objStr, jsonClass);
                Device deviceProxy = (Device) Enhancer.create(Object.class/* superClass */,
                        new Class[]{Device.class} /* interface to implement */,
                        new JSONGenSetInvocationHandler(inst)/* callbackMethod to proxy real call */
                );


                Device device = new CtDevice(deviceProxy);
                DeviceEvent deviceEvent = new DeviceEvent(device, null, DeviceEventEnums.fromMethod(event), systemCode);
                this.dispatchDeviceEvent(deviceEvent);
                break;
            case CommandEvent:
                JSONObject obj = JSONObject.parseObject(objStr);
                String commandStr = obj.getString("command");
                Command command = JSONObject.toJavaObject(obj, CommandEnums.fromByName(commandStr).getCommand());
                CommandEvent commandEvent = new CommandEvent(command, null, CommandEventEnums.fromMethod(event), systemCode);
                this.dispatchCommandEvent(commandEvent);

            default:
                break;
        }

    }

    public boolean repeatEvent(HomeEvent event, String msgId) throws JDSException {
        Boolean isSend = false;
        if (JDSServer.getInstance().getCurrServerBean().getId().equals(event.getSystemCode())) {
            ExecutorService service = RemoteConnectionManager.getConntctionService(event.getID().getMethod() + ":event");
            service.execute(new Runnable() {
                @Override
                public void run() {
                    EventTypeEnums type = EventTypeEnums.fromEventClass(event.getClass());
                    ClusterEvent clusterEvent = new ClusterEvent();
                    clusterEvent.setMsgId(msgId);
                    clusterEvent.setEventId(event.getID().getMethod());
                    String source = JSON.toJSONString(event.getSource());
                    clusterEvent.setSourceJson(source);
                    if (event.getClientService() != null) {
                        clusterEvent.setSessionId(event.getClientService().getSessionHandle().getSessionID());
                        clusterEvent.setSystemCode(event.getClientService().getSystemCode());
                    } else {
                        try {
                            clusterEvent.setSessionId(JDSServer.getInstance().getAdminUser().getSessionId());
                            clusterEvent.setSystemCode(JDSServer.getInstance().getCurrServerBean().getId());
                        } catch (JDSException e) {
                            e.printStackTrace();
                        }

                    }


                    clusterEvent.setEventName(type.getEventName());
                    clusterEvent.setExpression("$RepeatHomeEvent");
                    String eventStr = JSON.toJSONString(clusterEvent);
                    boolean isSend = JDSServer.getClusterClient().getUDPClient().send(eventStr);
                    logger.info("success repeatEvent [" + isSend + "]" + event.getID());


                    //                    MsgClient<SensorMsg> client = MsgFactroy.getInstance().getClient(event.getClientService().getConnectInfo().getUserID(), SensorMsg.class);
                    //                    SensorMsg msg = null;
                    //                    try {
                    //                        msg = client.creatMsg();
                    //                    } catch (JDSException e) {
                    //                        e.printStackTrace();
                    //                    }
                    //                    msg.setType(MsgType.EVENT.getType());
                    //                    msg.setReceiver(event.getClientService().getConnectInfo().getUserID());
                    //                    msg.setBody(eventStr);
                    //                    msg.setEventTime(new Date(System.currentTimeMillis()));
                    //                    msg.setFrom(event.getClientService().getConnectInfo().getUserID());
                    //                    msg.setSystemCode(UserBean.getInstance().getSystemCode());
                    //                    client.updateMsg(msg);

                }

            });

        }
        return isSend;

    }

    @Override
    public <T> void dispatchEvent(JDSEvent<T> event) throws JDSException {
        //
        // repeatEvent(event);

        if (event.getSystemCode() == null) {
            event.setSystemCode(JDSServer.getInstance().getCurrServerBean().getId());
        }

        if (event instanceof SensorEvent) {
            dispatchSensorEvent((SensorEvent) event);
        } else if (event instanceof DeviceEndPointEvent) {
            dispatchDeviceEndPointEvent((DeviceEndPointEvent) event);
        } else if (event instanceof GatewayEvent) {
            dispatchGatewayEvent((GatewayEvent) event);
        } else if (event instanceof ZNodeEvent) {
            dispatchZNodeEvent((ZNodeEvent) event);
        } else if (event instanceof DeviceEvent) {
            dispatchDeviceEvent((DeviceEvent) event);
        } else if (event instanceof PlaceEvent) {
            dispatchPlaceEvent((PlaceEvent) event);
        } else if (event instanceof CommandEvent) {
            dispatchCommandEvent((CommandEvent) event);
        } else if (event instanceof DataEvent) {
            dispatchDataEvent((DataEvent) event);
        }

    }

    /**
     * 分发网关事件
     *
     * @param event 核心网关事件
     */
    private void dispatchGatewayEvent(final GatewayEvent event) throws JDSException {
        //Map eventMap = JDSActionContext.getActionContext().getContext();

        GatewayEvent fe = event;
        ZNode znode = (ZNode) fe.getSource();
        String systemCode = event.getSystemCode();
        if (znode != null) {
            String key = event.getID() + znode.getZnodeid();
            Long checkOutTime = dataEventMap.get(key);
            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }

            List<JDSListener> listeners = getListenerByType(GatewayListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                GatewayListener getwayListener = (GatewayListener) listeners.get(k);
                if (getwayListener.getSystemCode() == null || getwayListener.getSystemCode().equals(systemCode)) {
                    dispatchGatewayEvent(event, (GatewayListener) listeners.get(k));
                }
            }
        }
    }

    /**
     * 分发网关命令事件
     *
     * @param event 分发网关命令事件
     */

    private void dispatchCommandEvent(final CommandEvent event) throws JDSException {
        //Map eventMap = JDSActionContext.getActionContext().getContext();

        Command command = (Command) event.getSource();
        String systemCode = event.getSystemCode();
        if (command != null) {

            String key = command.getGatewayieee() + "[" + command.getCommandId() + "]" + (command.getResultCode() == null ? CommandEventEnums.COMMANDINIT : command.getResultCode());
            Long checkOutTime = dataEventMap.get(key);
            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }

            List<JDSListener> listeners = getListenerByType(CommandListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                logger.info("**********************************************");
                CommandListener commandListener = (CommandListener) listeners.get(k);
                logger.info("commandListener=" + commandListener.toString() + "systemCode==" + systemCode + "commandListener.getSystemCode() " + commandListener.getSystemCode());
                logger.info("**********************************************");
                // if (commandListener.getSystemCode() == null || commandListener.getSystemCode().equals(systemCode)) {
                dispatchCommandEvent(event, (CommandListener) listeners.get(k));
                //}
            }
        }

    }

    /**
     * 分发住所事件
     *
     * @param event 应用事件
     */
    private void dispatchPlaceEvent(final PlaceEvent event) throws JDSException {
        PlaceEvent fe = event;
        Place place = (Place) fe.getSource();
        String systemCode = event.getSysCode();
        if (place != null) {
            String key = event.getID().getMethod() + "[" + place.getPlaceid() + "]";
            Long checkOutTime = dataEventMap.get(key);
            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }


            List<JDSListener> listeners = getListenerByType(PlaceListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                PlaceListener placeListener = (PlaceListener) listeners.get(k);
                dispatchPlaceEvent(event, (PlaceListener) listeners.get(k));
            }
        }
    }

    /**
     * 分发传感器事件
     *
     * @param event 核心事件
     */
    private void dispatchDataEvent(final DataEvent event) throws JDSException {
        DataIndex data = (DataIndex) event.getSource();

        String systemCode = event.getSystemCode();
        if (data != null) {
            String key = data.getValuetype() + ":" + data.getValue() + "[" + data.getSn() + "]";
            Long checkOutTime = dataEventMap.get(key);
            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }


            List<JDSListener> listeners = getListenerByType(DataListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                DataListener dataListener = (DataListener) listeners.get(k);
                if (dataListener.getSystemCode() == null || dataListener.getSystemCode().equals(systemCode)) {
                    dispatchDataEvent(event, (DataListener) listeners.get(k));
                }
            }
        }
    }

    /**
     * 分发传感器事件
     *
     * @param event 核心事件
     */
    private void dispatchSensorEvent(final SensorEvent event) throws JDSException {


        SensorEvent fe = event;
        ZNode znode = (ZNode) fe.getSource();
        String systemCode = event.getSysCode();
        if (znode != null) {


            String key = event.getID().getMethod() + "[" + znode.getZnodeid() + "]";
            Long checkOutTime = dataEventMap.get(key);
            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }


            List<JDSListener> listeners = getListenerByType(SensorListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                SensorListener sensorListener = (SensorListener) listeners.get(k);
                dispatchSensorEvent(event, (SensorListener) listeners.get(k));
            }
        }
    }

    /**
     * 分发zigbee网络事件
     *
     * @param event 核心事件
     */
    private void dispatchZNodeEvent(final ZNodeEvent event) throws JDSException {
        ZNodeEvent fe = event;
        ZNode znode = (ZNode) fe.getSource();
        String systemCode = event.getSystemCode();
        if (znode != null) {

            String key = event.getID().getMethod() + "[" + znode.getZnodeid() + "]";
            Long checkOutTime = dataEventMap.get(key);
            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }

            List<JDSListener> listeners = getListenerByType(ZNodeListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                ZNodeListener znodelistenter = (ZNodeListener) listeners.get(k);

                dispatchZNodeEvent(event, (ZNodeListener) listeners.get(k));

            }
        }
    }


    /**
     * 分发zigbee网络事件
     *
     * @param event 核心事件
     */
    private void dispatchDeviceEndPointEvent(final DeviceEndPointEvent event) throws JDSException {
        DeviceEndPointEvent fe = event;
        DeviceEndPoint endPoint = (DeviceEndPoint) fe.getSource();
        String systemCode = event.getSystemCode();
        if (endPoint != null) {
            String key = event.getID().getMethod() + "[" + endPoint.getIeeeaddress() + "]";
            Long checkOutTime = dataEventMap.get(key);
            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }

            List<JDSListener> listeners = getListenerByType(DeviceEndPointListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                DeviceEndPointListener deviceEndPointListener = (DeviceEndPointListener) listeners.get(k);
                dispatchDeviceEndPointEvent(event, deviceEndPointListener);

            }
        }
    }


    /**
     * 分发设备事件
     *
     * @param event
     * @throws JDSException
     */
    private void dispatchDeviceEvent(final DeviceEvent event) throws JDSException {
        Map deviceeventMap = JDSActionContext.getActionContext().getContext();

        Device device = (Device) event.getSource();
        String systemCode = event.getSystemCode();
        if (device != null) {

            String key = event.getID().getMethod() + "[" + device.getSerialno() + "]";
            Long checkOutTime = dataEventMap.get(key);
            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }


            List<JDSListener> listeners = getListenerByType(DeviceListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                DeviceListener deviceListener = (DeviceListener) listeners.get(k);

                dispatchDeviceEvent(event, (DeviceListener) listeners.get(k));

            }

        }
    }

    public synchronized void removeListener(JDSListener listener) {
        if (listener != null) {
            List<JDSListener> listeners = getListenersByListener(listener);
            if (listeners != null) {
                listeners.remove(listener);
            }
        }
    }

    private List<JDSListener> getListenersByListener(JDSListener listener) {
        List<JDSListener> listeners = new ArrayList<JDSListener>();
        if (listener != null) {

            if (listener instanceof ZNodeListener) {
                listeners = this.getListenerByType(ZNodeListener.class);
            } else if (listener instanceof DeviceEndPointListener) {
                listeners = this.getListenerByType(DeviceEndPointListener.class);
            } else if (listener instanceof DeviceListener) {
                listeners = this.getListenerByType(DeviceListener.class);
            } else if (listener instanceof PlaceListener) {
                listeners = this.getListenerByType(PlaceListener.class);
            } else if (listener instanceof SensorListener) {
                listeners = this.getListenerByType(SensorListener.class);
            } else if (listener instanceof GatewayListener) {
                listeners = this.getListenerByType(GatewayListener.class);
            } else if (listener instanceof CommandListener) {
                listeners = this.getListenerByType(CommandListener.class);
            }

        }
        return listeners;

    }

    public synchronized void addListener(JDSListener listener) {
        if (listener != null) {
            List<JDSListener> listeners = getListenersByListener(listener);
            if (listeners != null) {
                listeners.add(listener);
            }
        }

    }

    private List<JDSListener> getListenerByType(Class<? extends EventListener> listenerClass) {
        //  synchronized(listenerClass.getName().intern()){
        List<JDSListener> listeners = new ArrayList<JDSListener>();
        Set<Entry<String, ExpressionTempBean>> tempEntry = listenerBeanMap.entrySet();

        List<ExpressionTempBean> tempLst = listenerMap.get(listenerClass);

        if (tempLst == null || tempLst.isEmpty()) {
            tempLst = new ArrayList<ExpressionTempBean>();
            for (Entry<String, ExpressionTempBean> entry : tempEntry) {
                ExpressionTempBean bean = entry.getValue();
                if (!bean.getDataType().equals(ContextType.Server)) {
                    String classType = bean.getClazz();
                    if (classType == null) {
                        logger.warn("classType is null  beanId" + bean.getId() + " expression:" + bean.getExpressionArr());
                    }
                    Class clazz = null;
                    try {
                        clazz = ClassUtility.loadClass(classType);
                    } catch (ClassNotFoundException e) {
                        logger.warn("ClassNotFoundException:" + e.getMessage() + " beanId" + bean.getId() + " expression:" + bean.getExpressionArr());
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
        //   }

    }

    private static void dispatchPlaceEvent(final PlaceEvent event, final PlaceListener listener) {
        try {
            switch (event.getID()) {

                case placeCreate:
                    listener.placeCreate(event);
                    break;

                case placeRemove:
                    listener.placeRemove(event);
                    break;

                case areaAdd:
                    listener.areaAdd(event);
                    break;

                case areaRemove:
                    listener.areaRemove(event);
                    break;

                case gatewayAdd:
                    listener.gatewayAdd(event);
                    break;

                case gatewayRemove:
                    listener.gatewayRemove(event);
                    break;


                default:
                    throw new HomeException("Unsupport App event type: " + event.getID());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }

    private static void dispatchDataEvent(final DataEvent event, final DataListener listener) {
        try {
            switch (event.getID()) {

                case DataReport:
                    listener.dataReport(event);
                    break;

                case AlarmReport:
                    listener.alarmReport(event);
                    break;

                case AttributeReport:
                    listener.attributeReport(event);
                    break;

                default:
                    throw new HomeException("Unsupport DataEvent event type: " + event.getID());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }

    private static void dispatchSensorEvent(final SensorEvent event, final SensorListener listener) {
        try {
            switch (event.getID()) {

                case addDesktop:
                    listener.addDesktop(event);
                    break;

                case removeDesktop:
                    listener.removeDesktop(event);
                    break;

                case addAlarm:
                    listener.addAlarm(event);
                    break;

                case removeAlarm:
                    listener.removeAlarm(event);
                    break;

                case close:
                    listener.close(event);
                    break;

                case start:
                    listener.start(event);
                    break;
                default:
                    throw new HomeException("Unsupport App event type: " + event.getID());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }

    private static void dispatchGatewayEvent(final GatewayEvent event, final GatewayListener listener) {
        try {
            switch (event.getID()) {

                case GATEWAYONLINE:
                    listener.gatewayOnLine(event);
                    break;
                case GATEWAYOFFINE:
                    listener.gatewayOffLine(event);
                    break;

                case SENSORADDING:
                    listener.sensorAdding(event);
                    break;

                case SENSORADDED:
                    listener.sensorAdded(event);
                    break;

                case SENSORREMOVING:
                    listener.sensorRemoving(event);
                    break;

                case SENSORREMOVED:
                    listener.sensorRemoved(event);
                    break;

                case GATEWAYSHARING:
                    listener.gatewaySharing(event);
                    break;

                case GATEWAYSHARED:
                    listener.gatewayShared(event);
                    break;

                case GATEWAYLOCKED:
                    listener.gatewayLocked(event);
                    break;

                case GATEWAYUNLOCKED:
                    listener.gatewayUnLocked(event);
                    break;

                case ACCOUNTBIND:
                    listener.accountBind(event);
                    break;

                case ACCOUNTUNBIND:
                    listener.accountUNBind(event);
                    break;
                default:
                    throw new HomeException("Unsupport Gateway event type: " + event.getID());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }

    private static void dispatchCommandEvent(final CommandEvent event, final CommandListener listener) {
        try {
            switch (event.getID()) {

                case COMMANDINIT:

                    break;

                case COMMANDSENDTIMEOUT:
                    listener.commandSendTimeOut(event);
                    break;
                case COMMANDSENDING:
                    listener.commandSendIng(event);
                    break;

                case COMMANDSENDWAITE:
                    listener.commandSended(event);
                    break;
                case COMMANDROUTEERROR:
                    listener.commandSendFail(event);
                    break;

                case COMMANDERROR:
                    listener.commandExecuteFail(event);
                    break;

                case COMMANDVERSORERROR:
                    listener.commandExecuteFail(event);
                    break;
                case COMMANDLINKFAIL:
                    listener.commandSendFail(event);
                    break;
                case COMMANDSENDSUCCESS:
                    listener.commandExecuteSuccess(event);
                    break;

                case COMMANDROUTEFAIL:
                    listener.commandExecuteFail(event);
                    break;
                case COMMANDEXECUTEFAIL:
                    listener.commandExecuteFail(event);
                    break;

                case PASSWORDALREADYEXISTS:
                    listener.commandExecuteFail(event);
                    break;
                case PASSWORDNOTEXISTS:
                    listener.commandExecuteFail(event);
                    break;
                case PASSWORDFULL:
                    listener.commandExecuteFail(event);
                    break;
                case UNKNOWCOMMAND:
                    listener.commandExecuteFail(event);
                    break;

                case COMMANDROUTING:
                    listener.commandRouteing(event);
                    break;
                case COMMANDROUTED:
                    listener.commandRouted(event);
                    break;
                case COMMANDOUTERERROR:
                    listener.commandExecuteFail(event);
                    break;

                default:
                    throw new HomeException("Unsupport Command event type: " + event.getID());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }

    private static void dispatchDeviceEndPointEvent(final DeviceEndPointEvent event, final DeviceEndPointListener listener) {
        try {
            switch (event.getID()) {
                case bind:
                    listener.bind(event);
                    break;

                case bindSuccess:
                    listener.bindSuccess(event);
                    break;

                case createEndPoint:
                    listener.createEndPoint(event);
                    break;
                case removeEndPoint:
                    listener.removeEndPoint(event);
                    break;

                case bindFail:
                    listener.bindFail(event);
                    break;
                case unbind:
                    listener.unbind(event);
                    break;
                case unbindSuccess:
                    listener.unbindSuccess(event);
                    break;

                case unbindFail:
                    listener.unbindFail(event);
                    break;
                case locked:
                    listener.locked(event);
                    break;
                case updateInfo:
                    listener.updateInfo(event);
                    break;
                case unLocked:
                    listener.unLocked(event);
                    break;
                default:
                    throw new HomeException("Unsupport Sensor event type: " + event.getID());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }

    private static void dispatchDeviceEvent(final DeviceEvent event, final DeviceListener listener) {
        try {
            switch (event.getID()) {


                case register:
                    listener.register(event);
                    break;
                case deviceActivt:
                    listener.deviceActivt(event);
                    break;

                case deleteing:
                    listener.deleteing(event);
                    break;
                case deleteFail:
                    listener.deleteFail(event);
                    break;
                case areaUnBind:
                    listener.areaUnBind(event);
                    break;

                case areaBind:
                    listener.areaBind(event);
                    break;
                case onLine:
                    listener.onLine(event);
                    break;
                case offLine:
                    listener.offLine(event);
                    break;
                default:
                    throw new HomeException("Unsupport Sensor event type: " + event.getID());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }

    private static void dispatchZNodeEvent(final ZNodeEvent event, final ZNodeListener listener) {

        try {
            switch (event.getID()) {


                case sensorCreated:
                    listener.sensorCreated(event);
                    break;
                case znodeMoved:
                    listener.znodeMoved(event);
                    break;

                case sensorLocked:
                    listener.sensorLocked(event);
                    break;
                case sensorUnLocked:
                    listener.sensorUnLocked(event);
                    break;
                case sceneAdded:
                    listener.sceneAdded(event);
                    break;
                case sceneRemoved:
                    listener.sceneRemoved(event);
                    break;
                default:
                    throw new HomeException("Unsupport GETWAY event type: " + event.getID());
            }
        } catch (Throwable e) {
            logger.warn("Listener execute failed!", e);
        }
    }

    public static void main(String[] args) {
        String aaa = "{\"event\":\"DataReport\",\"eventtime\":1541849743,\"msgId\":\"ea0aa77d-3b3d-44d1-8665-f809c782d4bc\",\"path\":\"38128ff7-eab1-4a91-8228-ffbeadd6378c\",\"sn\":\"38128ff7-eab1-4a91-8228-ffbeadd6378c\",\"userId\":\"04254ac9-5540-428e-a529-93076b96d211\",\"value\":\"0\",\"valuetype\":\"Zone_Status\"}";
        DataIndex data = JSONObject.parseObject(aaa, DataIndex.class);

        System.out.println(data.getEvent());
    }

}
