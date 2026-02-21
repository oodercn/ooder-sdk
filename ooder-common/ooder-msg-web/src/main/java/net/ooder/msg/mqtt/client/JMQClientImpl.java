/**
 * $RCSfile: JMQClientImpl.java,v $
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
package net.ooder.msg.mqtt.client;

import com.alibaba.fastjson.JSONObject;
import net.ooder.cluster.ServerNode;
import net.ooder.cluster.ServerNodeList;
import net.ooder.common.ConfigCode;
import net.ooder.common.JDSException;
import net.ooder.common.ReturnType;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.ConnectionHandle;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.common.CommandEventEnums;
import net.ooder.jmq.JMQConfig;
import net.ooder.jmq.JMQUser;
import net.ooder.msg.*;
import net.ooder.msg.ct.CtTopicMsg;
import net.ooder.msg.mqtt.JMQException;
import net.ooder.msg.mqtt.command.MQTTCommand;
import net.ooder.msg.mqtt.command.cmd.CreateTopicCommand;
import net.ooder.msg.mqtt.engine.CtMQTTCacheManager;
import net.ooder.msg.mqtt.enums.P2PEnums;
import net.ooder.msg.mqtt.enums.TopicEnums;
import net.ooder.msg.mqtt.event.MQTTCommandEvent;
import net.ooder.msg.mqtt.event.MQTTEventControl;
import net.ooder.msg.mqtt.event.P2PEvent;
import net.ooder.msg.mqtt.event.TopicEvent;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;
import net.ooder.server.context.MinServerActionContextImpl;
import net.ooder.thread.JDSThreadFactory;
import net.sf.cglib.beans.BeanMap;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

public class JMQClientImpl implements JMQClient {

    private static ScheduledExecutorService commandpool = Executors.newScheduledThreadPool(150, new JDSThreadFactory("commandpool"));


    JDSClientService clientService;

    MQTTEventControl control;

    ConnectInfo connectInfo;

    ConfigCode configCode;

    ConnectionHandle connectionHandle;

    JDSSessionHandle sessionHandle;

    JDSContext context;


    public JMQClientImpl() {
        this.connectInfo = clientService.getConnectInfo();
        this.control = MQTTEventControl.getInstance();
    }

    public JMQClientImpl(JDSClientService clientService) {
        init(clientService);

    }

    void init(JDSClientService clientService) {
        this.clientService = clientService;
        this.connectInfo = clientService.getConnectInfo();
        this.control = MQTTEventControl.getInstance();
        this.configCode = clientService.getConfigCode();
        this.connectionHandle = clientService.getConnectionHandle();
        this.context = clientService.getContext();
        this.sessionHandle = clientService.getSessionHandle();
    }

    @Override
    public void subscriptTopic(String topic) throws JMQException {
        try {
            getMqttClient().subscribe(topic);
        } catch (Exception e) {
            throw new JMQException(e);
        }
    }

    @Override
    public void unSubscriptTopic(String topic) throws JMQException {
        try {
            getMqttClient().unsubscribe(topic);
        } catch (Exception e) {
            throw new JMQException(e);
        }
    }

    @Override
    public CreateTopicCommand createTopic(String topic, boolean retained, int qos) throws JMQException {

        return null;
    }

    @Override
    public void deleteTopic(String topic) throws JMQException {

    }

    @Override
    public Boolean isOnLine() throws JMQException {
        return null;
    }

    @Override
    public void clientOffLine(String deviceId) throws JMQException {

    }

    @Override
    public void clientOnLine(String deviceId) throws JMQException {

    }

    @Override
    public TopicMsg sendMsg(TopicMsg topicMsg) {

        return null;
    }

    @Override
    public <T extends MQTTCommand> T getCommandById(String commandId) throws JMQException {
        T command = null;
        try {
            command = CtMQTTCacheManager.getInstance().getCommand(commandId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return command;

    }


    class MQTTCommandTask implements Callable<MQTTCommand> {
        private MinServerActionContextImpl autoruncontext;
        private MQTTCommand command;

        public MQTTCommandTask(MQTTCommand command) {
            this.command=command;
            JDSContext context = JDSActionContext.getActionContext();
            this.autoruncontext = new MinServerActionContextImpl(context.getHttpRequest(), context.getOgnlContext());
            autoruncontext.setParamMap(context.getContext());

            if (context.getSessionId() != null) {
                autoruncontext.setSessionId(context.getSessionId());
                autoruncontext.getSession().put("sessionHandle", context.getSession().get("sessionHandle"));
            }
            autoruncontext.setSessionMap(context.getSession());
            configCode = autoruncontext.getConfigCode();
        }

        @Override
        public MQTTCommand call() throws Exception {
            JDSActionContext.setContext(autoruncontext);
            CommandMsg emsg = createCommandMsg(command, connectInfo.getUserID());
            command.setCommandId(emsg.getId());
            MQTTCommandEvent commandEvent = new MQTTCommandEvent(command, clientService, CommandEventEnums.COMMANDSENDING, JDSServer.getInstance().getCurrServerBean().getId());
            MQTTEventControl.getInstance().dispatchEvent(commandEvent);
            return command;
        }
    }


    @Override
    public Future<MQTTCommand> sendCommand(MQTTCommand command, Integer delayTime) throws JMQException {
        Future<MQTTCommand> future = commandpool.schedule(new MQTTCommandTask(command), 0, TimeUnit.MILLISECONDS);
        return future;
    }

    public CommandMsg createCommandMsg(MQTTCommand command, String personId) throws JDSException {
        MsgClient<CommandMsg> client = MsgFactroy.getInstance().getClient(connectInfo.getUserID(), CommandMsg.class);
        CommandMsg msg = null;
        try {
            Person person = OrgManagerFactory.getOrgManager().getPersonByID(personId);
            msg = client.creatMsg2Person(personId);
            msg.setType(MsgType.COMMAND.getType());
            msg.setGatewayId(person.getAccount());
            msg.setEvent(command.getCommand().getType());
            msg.setResultCode(CommandEventEnums.COMMANDINIT);
            command.setCommandId(msg.getId());
            Map map = BeanMap.create(command);
            Iterator keyit = map.keySet().iterator();
            Map valueMap = new HashMap();
            while (keyit.hasNext()) {
                String key = (String) keyit.next();
                Object value = map.get(key);
                if ((value != null) && (!value.equals(""))) {
                    valueMap.put(key, value);
                }
            }
            msg.setBody(JSONObject.toJSON(valueMap).toString());
            MsgFactroy.getInstance().getClient(connectInfo.getUserID(), CommandMsg.class).updateMsg(msg);
        } catch (PersonNotFoundException e) {
            e.printStackTrace();
        }

        return msg;
    }

    @Override
    public JMQUser getUserInfo() throws JMQException {
        JMQUser jmqUser = new JMQUser();
        String[] topics = new String[]{
                "P2P/test",
                "testTopic",
                "broadcast"};
        try {
            Person person = OrgManagerFactory.getOrgManager().getPersonByID(connectInfo.getUserID());
            jmqUser.setName(person.getName());
            jmqUser.setAccount(connectInfo.getLoginName());
            jmqUser.setId(connectInfo.getUserID());
            jmqUser.setSessionId(sessionHandle.getSessionID());
            JMQConfig config = jmqUser.getJmqConfig();
            config.setSubscribers(Arrays.asList(topics));
            config.setClientId(jmqUser.getSessionId());
            ServerNodeList serverNodeList = JDSServer.getClusterClient().getServerNodeListByConfigCode(ConfigCode.mqtt);
            if (serverNodeList.getServerNodeList().size() > 0) {
                ServerNode serverNode = serverNodeList.getServerNodeList().get(0);
                try {
                    URL url = new URL(serverNode.getUrl());
                    config.setServer(url.getHost());
                    config.setPort(Integer.valueOf(url.getPort()).toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {

        }

        return jmqUser;
    }


    @Override
    public TopicMsg sendMsgToClient(String sessionId, String msg) throws JMQException {
        MsgClient<TopicMsg> msgClient = MsgFactroy.getInstance().getClient(clientService.getConnectInfo().getUserID(), TopicMsg.class);
        TopicMsg emsg = null;
        try {
            emsg = msgClient.creatMsg();
            emsg.setBody(msg);
            emsg.setReceiver(sessionId);
            msgClient.updateMsg(emsg);
            P2PEvent p2PEvent = new P2PEvent(emsg, clientService, P2PEnums.send2Client, null);
            control.dispatchEvent(p2PEvent);
        } catch (JDSException e) {
            throw new JMQException(e);
        }
        return emsg;

    }

    @Override
    public TopicMsg broadcast(String topic, String msg) throws JMQException {
        MsgClient<TopicMsg> msgClient = MsgFactroy.getInstance().getClient(connectInfo.getUserID(), TopicMsg.class);
        TopicMsg emsg = null;
        try {
            emsg = msgClient.creatMsg();
            emsg.setQos(0);
            emsg.setId(UUID.randomUUID().toString());
            emsg.setBody(msg);
            emsg.setTopic(topic);
            emsg.setRetained(true);
            msgClient.updateMsg(emsg);
            TopicEvent p2PEvent = new TopicEvent(emsg, clientService, TopicEnums.publicTopicMsg, null);
            MQTTEventControl.getInstance().dispatchEvent(p2PEvent);
        } catch (JDSException e) {
            throw new JMQException(e);
        }
        return emsg;

    }

    @Override
    public TopicMsg sendMsgToPerson(String personId, String msg) throws JMQException {
        MsgClient<CtTopicMsg> msgClient = MsgFactroy.getInstance().getClient(clientService.getConnectInfo().getUserID(), CtTopicMsg.class);
        CtTopicMsg emsg = null;
        try {
            emsg = msgClient.creatMsg2Person(personId);
            emsg.setBody(msg);
            msgClient.updateMsg(emsg);
            P2PEvent p2PEvent = new P2PEvent(emsg, clientService, P2PEnums.send2Person, null);
            MQTTEventControl.getInstance().dispatchEvent(p2PEvent);
        } catch (JDSException e) {
            throw new JMQException(e);
        }
        return emsg;
    }

    MqttClient getMqttClient() throws JDSException {
        MqttClient mqttClient = MsgSelfClient.getInstance(JDSServer.getInstance().getAdminUser().getSessionId()).getClient();
        return mqttClient;
    }

    @Override
    public boolean sendSystemMsg(Msg msg) throws JMQException {
        return false;
    }

    @Override
    public void beginTransaction() throws JMQException {

    }

    @Override
    public void commitTransaction() throws JMQException {

    }

    @Override
    public void rollbackTransaction() throws JMQException {

    }

    @Override
    public void setSystemCode(String systemCode) {

    }


    @Override
    public void setClientService(JDSClientService client) {
        this.clientService = client;
        this.init(clientService);

    }


    @Override
    public JDSSessionHandle getSessionHandle() {
        return sessionHandle;
    }

    @Override
    public void connect(ConnectInfo connInfo) throws JDSException {
        this.clientService.connect(connInfo);
        this.connectInfo = connInfo;

    }

    @Override
    public ReturnType disconnect() throws JDSException {
        clientService.disconnect();
        this.connectInfo = null;
        this.context = null;
        this.sessionHandle = null;
        this.connectionHandle = null;
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ConnectInfo getConnectInfo() {
        return connectInfo;
    }

    @Override
    public ConnectionHandle getConnectionHandle() {
        return connectionHandle;
    }

    @Override
    public void setConnectionHandle(ConnectionHandle handle) {
        this.connectionHandle = handle;
    }

    @Override
    public JDSContext getContext() {
        return context;
    }

    @Override
    public void setContext(JDSContext context) {
        this.context = context;

    }

    @Override
    public ConfigCode getConfigCode() {
        return configCode;
    }

    @Override
    public String getSystemCode() {
        return configCode.getType();
    }
}


