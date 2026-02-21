
/**
 * $RCSfile: MQTTEventControl.java,v $
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
package net.ooder.msg.mqtt.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.cluster.udp.ClusterCommand;
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
import net.ooder.msg.Msg;
import net.ooder.msg.TopicMsg;
import net.ooder.msg.ct.CtRMsg;
import net.ooder.msg.index.DataIndex;
import net.ooder.msg.mqtt.JMQException;
import net.ooder.msg.mqtt.command.MQTTCommand;
import net.ooder.msg.mqtt.enums.MQTTCommandEnums;
import net.ooder.msg.mqtt.enums.P2PEnums;
import net.ooder.msg.mqtt.enums.TopicEnums;
import net.ooder.server.JDSServer;
import net.ooder.web.RemoteConnectionManager;
import net.ooder.web.client.ListenerTempAnnotationProxy;
import net.sf.cglib.proxy.Enhancer;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

/**
 * <p>
 * Title: im系统管理系统
 * </p>
 * <p>
 * Description: 系统事件控制核心，所有引擎事件都在这里中转处理
 * </p>
 * 支持分布式事件分发
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
public class MQTTEventControl implements JDSEventDispatcher {

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, MQTTEventControl.class);

    public static Map<String, Long> dataEventMap = CacheManagerFactory.createCache(JDSConstants.ORGCONFIG_KEY, "dataEventTiemMap");

    private static MQTTEventControl instance = null;

    public static Map<Class<? extends EventListener>, List<ExpressionTempBean>> listenerMap = new HashMap<Class<? extends EventListener>, List<ExpressionTempBean>>();

    public static MQTTEventControl getInstance() {
        if (instance == null) {
            synchronized (MQTTEventControl.class) {
                if (instance == null) {
                    instance = new MQTTEventControl();
                }
            }
        }
        return instance;
    }

    public Map<String, ExpressionTempBean> listenerBeanMap = new HashMap<String, ExpressionTempBean>();

    public MQTTEventControl() {
        listenerBeanMap.putAll(ListenerTempAnnotationProxy.getListenerBeanMap());
        List<? extends ServiceBean> esbBeans = EsbBeanFactory.getInstance().loadAllServiceBean();
        for (ServiceBean esbBean : esbBeans) {
            if (esbBean instanceof ExpressionTempBean) {
                listenerBeanMap.put(esbBean.getId(), (ExpressionTempBean) esbBean);
            }

        }
        getListenerByType(CommandListener.class);
        getListenerByType(TopicListener.class);
        getListenerByType(P2PListener.class);
    }

    public <T> void dispatchClusterEvent(String objStr, String eventName, String event, String systemCode) throws JDSException {
        EventTypeEnums type = EventTypeEnums.fromName(eventName);
        switch (type) {
            case MQTTCommandEvent:
                JSONObject obj = JSONObject.parseObject(objStr);
                String commandStr = obj.getString("command");
                MQTTCommand command = JSONObject.toJavaObject(obj, MQTTCommandEnums.fromByName(commandStr).getCommand());
                MQTTCommandEvent commandEvent = new MQTTCommandEvent(command, null, CommandEventEnums.fromMethod(event), systemCode);
                this.dispatchCommandEvent(commandEvent);
                break;
            case P2PEvent:
                obj = JSONObject.parseObject(objStr);
                CtRMsg msg = JSONObject.toJavaObject(obj, CtRMsg.class);
                P2PEvent p2pEvent = new P2PEvent<Msg>(msg, null, P2PEnums.fromMethod(event), systemCode);
                this.dispatchP2PEvent(p2pEvent);
                break;
            case TopicEvent:
                obj = JSONObject.parseObject(objStr);
                CtRMsg topic = JSONObject.toJavaObject(obj, CtRMsg.class);
                TopicEvent topicEvent = new TopicEvent<TopicMsg>(topic, null, TopicEnums.fromMethod(event), systemCode);
                this.dispatchTopicEvent(topicEvent);
            default:
                break;
        }

    }

    public boolean repeatCommandEvent(MQTTCommandEvent event, String msgId) throws JDSException {
        Boolean isSend = false;
        if (JDSServer.getInstance().getCurrServerBean().getId().equals(event.getSystemCode())) {
            ExecutorService service = RemoteConnectionManager.getConntctionService("repeatCommandEvent");
            service.execute(new Runnable() {
                @Override
                public void run() {
                    ClusterCommand commandEvent = new ClusterCommand();
                    commandEvent.setMsgId(msgId);
                    commandEvent.setEventId(event.getID().getMethod());
                    String source = "{}";
                    if (event.getSource() != null) {
                        if (!Enhancer.isEnhanced(event.getSource().getClass())) {
                            source = JSON.toJSONString(event.getSource());
                        } else {
                            source = event.getSource().toString();
                        }
                    }
                    commandEvent.setCommandJson(source);
                    commandEvent.setSystemCode(ConfigCode.mqtt.getType());
                    if (event.getClientService() != null) {
                        commandEvent.setSessionId(event.getClientService().getSessionHandle().getSessionID());

                    } else {
                        try {
                            commandEvent.setSessionId(JDSServer.getInstance().getAdminUser().getSessionId());
                        } catch (JDSException e) {
                            e.printStackTrace();
                        }

                    }
                    commandEvent.setExpression(event.getExpression());
                    String eventStr = JSON.toJSONString(commandEvent);
                    boolean isSend = JDSServer.getClusterClient().send(eventStr);
                    logger.info("success repeatMqttEvent [" + isSend + "]" + event.getID());
                }

            });
        }
        return isSend;

    }

    public boolean repeatEvent(MQTTEvent event, String msgId) throws JDSException {
        Boolean isSend = false;
        if (JDSServer.getInstance().getCurrServerBean().getId().equals(event.getSystemCode())) {
            ExecutorService service = RemoteConnectionManager.getConntctionService("repeatEvent");
            service.execute(new Runnable() {
                @Override
                public void run() {
                    EventTypeEnums type = EventTypeEnums.fromEventClass(event.getClass());
                    ClusterEvent clusterEvent = new ClusterEvent();
                    clusterEvent.setMsgId(msgId);
                    clusterEvent.setEventId(event.getID().getMethod());
                    String source = "{}";
                    if (event.getSource() != null) {
                        if (!Enhancer.isEnhanced(event.getSource().getClass())) {
                            source = JSON.toJSONString(event.getSource());
                        } else {
                            source = event.getSource().toString();
                        }
                    }
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
                    clusterEvent.setExpression(event.getExpression());
                    String eventStr = JSON.toJSONString(clusterEvent);
                    boolean isSend = JDSServer.getClusterClient().send(eventStr);
                    logger.info("success repeatMqttEvent [" + isSend + "]" + event.getID());
                }

            });
        }
        return isSend;

    }

    @Override
    public <T> void dispatchEvent(JDSEvent<T> event) throws JDSException {
        //
        if (event.getSystemCode() == null) {
            event.setSystemCode(JDSServer.getInstance().getCurrServerBean().getId());
        }

        if (event instanceof MQTTCommandEvent) {
            dispatchCommandEvent((MQTTCommandEvent) event);
        }

        if (event instanceof P2PEvent) {
            dispatchP2PEvent((P2PEvent) event);
        }

        if (event instanceof TopicEvent) {
            dispatchTopicEvent((TopicEvent) event);
        }

    }

    /**
     * 分发主题事件
     *
     * @param event 分发主题事件
     */

    private void dispatchTopicEvent(final TopicEvent<TopicMsg> event) throws JDSException {
        TopicMsg topic = event.getSource();
        String systemCode = event.getSystemCode();
        if (topic != null) {

            String key = topic.getTopic();
            Long checkOutTime = dataEventMap.get(key);
            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }

            List<JDSListener> listeners = getListenerByType(TopicListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                logger.info("**********************************************");
                TopicListener commandListener = (TopicListener) listeners.get(k);
                logger.info("topicListener=" + commandListener.toString() + "systemCode==" + systemCode + "TopicListener.getSystemCode() " + commandListener.getSystemCode());
                logger.info("**********************************************");
                // if (commandListener.getSystemCode() == null || commandListener.getSystemCode().equals(systemCode)) {
                dispatchTopicEvent(event, (TopicListener) listeners.get(k));
                //}
            }
        }

    }


    /**
     * 分发主题事件
     *
     * @param event 分发主题事件
     */

    private void dispatchP2PEvent(final P2PEvent<Msg> event) throws JDSException {
        Msg topic = (Msg) event.getSource();
        String systemCode = event.getSystemCode();
        if (topic != null) {

            String key = topic.getId();
            Long checkOutTime = dataEventMap.get(key);
            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatEvent(event, key);
            }

            List<JDSListener> listeners = getListenerByType(P2PListener.class);
            for (int k = 0; k < listeners.size(); k++) {
                logger.info("**********************************************");
                P2PListener p2pListener = (P2PListener) listeners.get(k);
                logger.info("p2pListener=" + p2pListener.toString() + "systemCode==" + systemCode + "p2pListener.getSystemCode() " + p2pListener.getSystemCode());
                logger.info("**********************************************");
                // if (commandListener.getSystemCode() == null || commandListener.getSystemCode().equals(systemCode)) {
                dispatchP2PEvent(event, (P2PListener) listeners.get(k));
                //}
            }
        }

    }


    /**
     * 分发网关命令事件
     *
     * @param event 分发网关命令事件
     */

    private void dispatchCommandEvent(final MQTTCommandEvent event) throws JDSException {
        //Map eventMap = JDSActionContext.getActionContext().getContext();

        MQTTCommand command = (MQTTCommand) event.getSource();
        String systemCode = event.getSystemCode();
        if (command != null) {

            String key = command.getGatewayieee() + "[" + command.getCommandId() + "]" + (command.getResultCode() == null ? CommandEventEnums.COMMANDSENDING : command.getResultCode());
            Long checkOutTime = dataEventMap.get(key);
            if (checkOutTime == null || System.currentTimeMillis() - checkOutTime > 1000) {
                dataEventMap.put(key, System.currentTimeMillis());
                repeatCommandEvent(event, key);
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

            if (listener instanceof TopicListener) {
                listeners = this.getListenerByType(TopicListener.class);
            } else if (listener instanceof CommandListener) {
                listeners = this.getListenerByType(CommandListener.class);
            } else if (listener instanceof P2PListener) {
                listeners = this.getListenerByType(P2PListener.class);
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


    private static void dispatchTopicEvent(final TopicEvent<TopicMsg> event, final TopicListener listener) {
        try {
            switch (event.getID()) {
                case subscriptTopic:
                    listener.subscriptTopic(event);
                    break;
                case unSubscriptTopic:
                    listener.unSubscriptTopic(event);
                    break;
                case createTopic:
                    listener.createTopic(event);
                    break;
                case deleteTopic:
                    listener.deleteTopic(event);
                    break;
                case clearTopic:
                    listener.clearTopic(event);
                    break;
                case publicTopicMsg:
                    listener.publicTopicMsg(event);
                    break;


                default:
                    throw new JMQException("Unsupport TopicEvent event type: " + event.getID());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }


    private static void dispatchP2PEvent(final P2PEvent event, final P2PListener listener) {
        try {
            switch (event.getID()) {
                case send2Client:
                    listener.send2Client(event);
                    break;
                case send2PersonMsg:
                    listener.send2PersonMsg(event);
                    break;
                case send2Person:
                    listener.send2Person(event);
                    break;
                default:
                    throw new JMQException("Unsupport P2PEvent event type: " + event.getID());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }


    private static void dispatchCommandEvent(final MQTTCommandEvent event, final CommandListener listener) {
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

                default:
                    throw new JMQException("Unsupport Command event type: " + event.getID());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.warn("Listener execute failed!", e);
        }
    }


    public static void main(String[] args) {
        String aaa = "{\"event\":\"DataReport\",\"eventtime\":1541849743,\"msgId\":\"ea0aa77d-3b3d-44d1-8665-f809c782d4bc\",\"path\":\"38128ff7-eab1-4a91-8228-ffbeadd6378c\",\"sn\":\"38128ff7-eab1-4a91-8228-ffbeadd6378c\",\"userId\":\"04254ac9-5540-428e-a529-93076b96d211\",\"value\":\"0\",\"valuetype\":\"Zone_Status\"}";
        DataIndex data = JSONObject.parseObject(aaa, DataIndex.class);

        System.out.println(data.getEvent());
    }

}


