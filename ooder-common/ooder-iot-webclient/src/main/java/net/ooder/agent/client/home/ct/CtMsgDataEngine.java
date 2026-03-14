package net.ooder.agent.client.home.ct;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.command.DelaySendCommand;
import net.ooder.agent.client.iot.*;
import net.ooder.agent.client.iot.ct.CtIotCacheManager;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;
import net.ooder.agent.client.iot.enums.DeviceStatus;
import net.ooder.agent.client.iot.json.SensorDataInfo;
import  net.ooder.annotation.Operator;
import  net.ooder.common.*;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.common.util.DateUtility;
import  net.ooder.context.JDSActionContext;
import  net.ooder.engine.ConnectInfo;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.home.client.CommandClient;
import  net.ooder.agent.client.home.engine.IOTDataEngine;
import  net.ooder.msg.Msg;
import  net.ooder.msg.MsgFactroy;
import  net.ooder.msg.MsgType;
import  net.ooder.msg.SensorMsg;
import  net.ooder.org.Person;
import  net.ooder.org.PersonNotFoundException;
import  net.ooder.org.query.MsgConditionKey;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.JDSServer;
import  net.ooder.server.OrgManagerFactory;
import  net.ooder.server.SubSystem;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CtMsgDataEngine implements IOTDataEngine {

    public ConfigCode configCode;


    private CtIotCacheManager iotCacheManager;

    static ScheduledExecutorService commandService = Executors.newScheduledThreadPool(100);

    private static Map<ConfigCode, CtMsgDataEngine> engineMap = new HashMap<ConfigCode, CtMsgDataEngine>();

    public static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, CtMsgDataEngine.class);
    private final static SerializeConfig config = new SerializeConfig();

    static {
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }

    public static CtMsgDataEngine getEngine(ConfigCode configCode) {
        CtMsgDataEngine engine = (CtMsgDataEngine) engineMap.get(configCode);
        if (engine == null) {
            synchronized (CtMsgDataEngine.class) {
                engine = new CtMsgDataEngine(configCode);
                engineMap.put(configCode, engine);
            }
        }
        return engine;
    }

    public CtMsgDataEngine(ConfigCode configCode) {
        this.configCode = configCode;
        this.iotCacheManager = CtIotCacheManager.getInstance();
    }

    public <T extends Msg> T creatMsg(String personId, String topersonId, Class<T> clazz) throws HomeException {
        T msg = null;
        try {
            msg = (T) MsgFactroy.getInstance().getClient(topersonId, clazz).creatMsg2Person(topersonId);
        } catch (JDSException e) {
            new HomeException(e);
        }

        return msg;
    }

    public Person getCurrPerson() throws PersonNotFoundException {
        Person currPerson = OrgManagerFactory.getOrgManager().getPersonByID(getClient().getConnectInfo().getUserID());
        return currPerson;
    }

    public JDSClientService getClient() {
        JDSClientService client = JDSActionContext.getActionContext().Par("$JDSC", JDSClientService.class);
        return client;
    }

    public void sendMassMsg(SensorMsg msg, ZNode znode) throws HomeException {
        try {

            // PersonMsgGroup msgGroup = MsgFactroy.getInstance().getClient(msg.getFrom(),SensorMsg.class).getPersonMassMsgGroupByType(msg.getType());


            List personIds = new ArrayList();
            msg.setSensorId(znode.getZnodeid());
            personIds.add(znode.getCreateuiserid());
            MsgFactroy.getInstance().getClient(msg.getFrom(), SensorMsg.class).sendMassMsg(msg, personIds);
            //  msgGroup.massSendMsg(msg, personIds);
        } catch (Exception e) {
            e.printStackTrace();
            new JDSException(e);
        }
    }


    public <T extends Command> Future<T> sendCommand(Command command, Integer delayTime) {
        Future<T> future = null;
        try {
            String account = iotCacheManager.getDeviceByIeee(command.getGatewayieee()).getBindingaccount();
            //  CommandMsg msg = createCommandMsg(command, personId, account);
            Person currPerson = OrgManagerFactory.getOrgManager().getPersonByAccount(account);
            String personId = null;
            if (currPerson != null) {
                personId = currPerson.getID();
            }
            DelaySendCommand delayCommand = new DelaySendCommand(personId, command, command.getGatewayieee());

            if (delayTime > 0) {
                future = commandService.schedule(delayCommand, delayTime.intValue(), TimeUnit.SECONDS);
            } else {
                future = commandService.submit(delayCommand);
            }

        } catch (Exception e) {

        }
        return future;
    }

    public SensorMsg createAlarmMsg(ZNode znode, Map value) throws HomeException {
        DeviceEndPoint endPoint = znode.getEndPoint();
        Person devicePerson = null;
        try {
            devicePerson = OrgManagerFactory.getOrgManager().getPersonByAccount(endPoint.getDevice().getBindingaccount());
        } catch (PersonNotFoundException e) {
            e.printStackTrace();
        }

        SensorMsg msg = creatMsg(devicePerson.getID(), znode.getCreateuiserid(), SensorMsg.class);
        msg.setType(MsgType.ALARM.getType());
        msg.setGatewayId(znode.getParentid());
        JDSActionContext.getActionContext().getContext().put("value", value);
        String htmlValue = endPoint.getSensortype().getHtmltemp();
        String iconTemp = endPoint.getSensortype().getIcontemp();
        JDSActionContext.getActionContext().getContext().remove("value");
        msg.setBody(znode.getName() + "[" + htmlValue + "]");

        if (value.containsKey(DeviceDataTypeKey.Time)) {

            String time = value.get(DeviceDataTypeKey.Time).toString();
            Date eventTime = new Date();
            if (time.indexOf(":") == -1) {
                eventTime = new Date(Long.valueOf(time));
            } else {
                eventTime = DateUtility.getDayD(time);
            }
            msg.setEventTime(eventTime.getTime());
        }

        // msg.setEvent(CommandEnums.AddAlarm);

        msg.setTitle(htmlValue);
        msg.setSensorId(znode.getZnodeid());

        return msg;
    }

    public SensorMsg createOfflineMsg(ZNode znode, Map value) throws HomeException {
        DeviceEndPoint endPoint = znode.getEndPoint();
        Person devicePerson = null;
        try {
            devicePerson = OrgManagerFactory.getOrgManager().getPersonByAccount(endPoint.getDevice().getBindingaccount());
        } catch (PersonNotFoundException e) {
            e.printStackTrace();
        }

        SensorMsg msg = creatMsg(devicePerson.getID(), znode.getCreateuiserid(), SensorMsg.class);

        msg.setGatewayId(znode.getParentid());

        JDSActionContext.getActionContext().getContext().put("value", value);
        String htmlValue = endPoint.getSensortype().getHtmltemp();
        String iconTemp = endPoint.getSensortype().getIcontemp();
        JDSActionContext.getActionContext().getContext().remove("value");

        msg.setBody(znode.getName() + "[" + htmlValue + "]");
        // msg.setEvent(iconTemp);
        msg.setTitle(htmlValue);
        msg.setSensorId(znode.getZnodeid());
        return msg;
    }

    public SensorMsg createDataMsg(ZNode znode, Map value) throws HomeException {
        DeviceEndPoint endPoint = znode.getEndPoint();
        Person devicePerson = null;
        try {
            devicePerson = OrgManagerFactory.getOrgManager().getPersonByAccount(endPoint.getDevice().getBindingaccount());
        } catch (PersonNotFoundException e) {
            e.printStackTrace();
        }

        SensorMsg msg = creatMsg(devicePerson.getID(), znode.getCreateuiserid(), SensorMsg.class);

        JDSActionContext.getActionContext().getContext().put("value", value);
        String htmlValue = endPoint.getSensortype().getHtmltemp();
        String iconTemp = endPoint.getSensortype().getIcontemp();
        JDSActionContext.getActionContext().getContext().remove("value");

        SensorDataInfo datainfo = new SensorDataInfo();
        String datetime = DateUtility.formatDate(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm:ss");
        if (msg.getArrivedTime() != null) {
            datetime = DateUtility.formatDate(new Date(msg.getArrivedTime()), "yyyy-MM-dd HH:mm:ss");
        }


        datainfo.setDatetime(datetime);
        datainfo.setIconTemp(iconTemp);
        datainfo.setSensorId(znode.getZnodeid());
        datainfo.setHtmlValue(htmlValue);
        datainfo.setValue(JSONObject.toJSON(value,config).toString());
        String info = JSONObject.toJSON(datainfo,config).toString();


//        if (value.containsKey("passId")) {
//            msg.setPassId(value.get("passId").toString());
//        }
//
//        if (value.containsKey("modeid")) {
//            msg.setModeId(value.get("modeid").toString());
//        }

        if (value.containsKey(DeviceDataTypeKey.Time)) {
            Date eventTime = new Date();
            String time = value.get(DeviceDataTypeKey.Time).toString();

            if (time.indexOf(":") == -1) {
                eventTime = new Date(Long.valueOf(time));
            } else {
                eventTime = DateUtility.getDayD(time);

            }
            msg.setEventTime(eventTime.getTime());
        }
        //msg.setEvent(CommandEnums.AddData);
        msg.setBody(info);

        msg.setTitle(htmlValue);
        msg.setSensorId(znode.getZnodeid());
        msg.setGatewayId(znode.getParentid());

        return msg;
    }

    public ReturnType activateDevice(Device device) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType addAlarm(DeviceEndPoint enPoint, Map value) throws HomeException {

        List<ZNode> shareNodes = enPoint.getAllZNodes();

        for (ZNode node : shareNodes) {
            if (!node.getStatus().equals(DeviceStatus.DELETE)) {
                SensorMsg alarmmsg = createAlarmMsg(node, value);
                sendMassMsg(alarmmsg, node);

                if (node.getEndPoint().getDevice().getSensortype().getType() != 30) {
                    SensorMsg datamsg = createDataMsg(node, value);
                    sendMassMsg(datamsg, node);
                }

            }
        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType addData(DeviceEndPoint enPoint, Map eventMap) throws HomeException {
        List<ZNode> shareNodes = enPoint.getAllZNodes();
        Map value = (Map) eventMap.get("currValue");
        if (shareNodes != null && shareNodes.size() > 0) {
            for (ZNode node : shareNodes) {
                if (node.getParentNode() != null) {
                    DeviceStatus status = node.getParentNode().getStatus();

                    if (!node.getStatus().equals(DeviceStatus.DELETE)) {
                        SensorMsg datamsg = createDataMsg(node, value);
                        datamsg = createDataMsg(node, value);
                        sendMassMsg(datamsg, node);
                    }
                }

            }
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType deleteAlarm(Alarm alarm) throws HomeException {
        if (alarm.getScene() != null) {
            DeviceEndPoint sendPoint = iotCacheManager.getZNodeById(alarm.getSensorid()).getEndPoint();
            DeviceEndPoint tendPoint = alarm.getScene().getZnode().getEndPoint();
            String gatewayieee = alarm.getScene().getZnode().getParentNode().getEndPoint().getDevice().getSerialno();

            CommandClient commandClient = this.getCommandClientByieee(gatewayieee);

            commandClient.sendIdentifyDeviceCommand(alarm.getScene().getZnode().getEndPoint().getIeeeaddress(), 60);
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType registerSensor(Device device) {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType removeSensor2Index(Place place, List<ZNode> sensorListByIds) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType removeSensor2Start(Place place, List<ZNode> sensorListByIds) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType sensorOffLine(Device device) throws HomeException {
        // List<ZNode> shareNodes = device.getAllZNodes();
        //
        // for (ZNode node : shareNodes) {
        //
        // Msg datamsg = createDataMsg(node, node.getEndPoint().getCurrvalue());
        // datamsg.setEvent(Integer.valueOf(UDPData.SENSOROFFLINE).toString());
        // sendMassMsg(datamsg, node);
        // }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType sensorOnLine(Device device) throws HomeException {
        // List<ZNode> shareNodes = device.getAllZNodes();
        // for (ZNode node : shareNodes) {
        // Msg datamsg = createDataMsg(node, node.getEndPoint().getCurrvalue());
        // datamsg.setEvent(Integer.valueOf(UDPData.SENSORONLINE).toString());
        // sendMassMsg(datamsg, node);
        // }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType setLightSensorInfo(ZNode znode, Integer lightValue) throws HomeException {
        Device device = znode.getEndPoint().getDevice().getRootDevice();

        CommandClient commandClient = this.getCommandClientByieee(device.getSerialno());

        commandClient.sendLightCommand(znode.getEndPoint().getDevice().getSerialno(), lightValue);
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }


    public ReturnType shareGateway(ZNode gateway, ZNode copyGateway) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType updateAlarm(Alarm alarm) throws HomeException {
        if (alarm.getScene() != null) {

            String gatewayieee = alarm.getScene().getZnode().getEndPoint().getDevice().getSerialno();
            CommandClient commandClient = this.getCommandClientByieee(gatewayieee);

            DeviceEndPoint sEndPoint = iotCacheManager.getZNodeById(alarm.getSensorid()).getEndPoint();
            DeviceEndPoint tEndPoint = alarm.getScene().getZnode().getEndPoint();
            commandClient.sendIdentifyDeviceCommand(alarm.getScene().getZnode().getEndPoint().getIeeeaddress(), 60);

            // commandService.submit(new
            // IdentifyCommand(alarm.getScene().getZnode().getDevice().getSerialno(),
            // systemCode));

        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType updateAreaName(Area area, String name) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType updatePlace(Place place) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType updateScene(ZNode znode, Scene scene) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType createGateway(String serialno) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public void setOutLetSensorInfo(ZNode znode, boolean vlaue) throws HomeException {
        CommandClient commandClient = this.getCommandClientByieee(znode.getEndPoint().getDevice().getRootDevice().getSerialno());

        commandClient.sendOnOutLetCommand(znode.getEndPoint().getIeeeaddress(), vlaue);

    }

    public ReturnType updateDevice(Device device) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType updateEndPoint(DeviceEndPoint endPoint) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public List<Msg> getSensorSetData(String sensorId, Long startTime, Long endTime, Integer timeunit) throws HomeException {
        List<Long> times = new ArrayList();
        times.add(startTime);
        times.add(endTime);
        Condition condition = new Condition(MsgConditionKey.MSG_SENDTIME, Operator.BETWEEN, times);

        List<Msg> msgList = new ArrayList<Msg>();
        try {
            msgList = loadMsg(condition, null, MsgType.SENSOR);
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return msgList;
    }

    public Msg updateCommandMsgStatus(Msg msg) throws HomeException {
        MsgFactroy.getInstance().getClient(null, MsgType.fromType(msg.getType()).getClazz()).updateMsg(msg);
        return msg;
    }

    public ReturnType deleteArea(Area area) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType deleteDevice(Device device) {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType deleteNode(ZNode znode) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType deletePlace(Place place) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType deleteScene(ZNode znode, Scene scene) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType addSensor2Index(Place place, List<ZNode> sensorListByIds) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType addSensor2Start(Place place, List<ZNode> sensorListByIds) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType bindingGateway(ZNode znode, String wbaccount) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType bindingSensor(ZNode znode) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType canCreateGateway(Device device) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType checkGateway(Device device, String userId) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType creatScene(ZNode znode, Scene scene) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType createAlarm(Alarm alarm) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType createArea(Area area) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType createPlace(Place place) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType createZNode(ZNode znode, Device device) throws HomeException {
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public List<Msg> loadMsg(Condition condition, Filter filter, MsgType msgType) throws JDSException {

        return (List<Msg>) MsgFactroy.getInstance().getClient(null, msgType.getClazz()).getMsgList(condition).get();
    }

    public CommandClient getCommandClientByieee(String ieee) throws HomeException {
        CommandClient commandclient = null;// commandClientMap.get(ieee);
        Device device = null;
        try {
            device = iotCacheManager.getDeviceByIeee(ieee);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        Person person = null;
        try {
            person = OrgManagerFactory.getOrgManager().getPersonByAccount(device.getBindingaccount());
            ConnectInfo connectInfo = new ConnectInfo(person.getID(), person.getAccount(), person.getPassword());
            Set<JDSSessionHandle> sessionHandleList = JDSServer.getInstance().getSessionHandleList(connectInfo);

            for (JDSSessionHandle sessionHandle : sessionHandleList) {
                JDSClientService client = JDSServer.getInstance().getJDSClientService(sessionHandle, configCode);
                if (client.getConnectInfo() == null) {
                    client.connect(connectInfo);
                }
                String subsystemCode = JDSServer.getSessionhandleSystemCodeCache().get(sessionHandle.toString());
                // 是否连接成功
                if (subsystemCode == null) {
                    subsystemCode = client.getConfigCode().getType();
                }
                SubSystem subSystem = JDSServer.getClusterClient().getSystem(subsystemCode);

                // if (subSystem != null) {
                commandclient = new CtCommandClientImpl(client, ieee);
                //}
            }
        } catch (PersonNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return commandclient;
    }

    // public Map<String, CommandClient> getCommandClient(JDSClientService client) throws HomeException {
    // Map<String, CommandClient> clientMap = new HashMap<String, CommandClient>();
    // if (client != null) {
    // if (client instanceof GWClient) {
    // return this.getGWCommandClient((GWClient) client);
    // } else if (client instanceof AppClient) {
    // return this.getAppCommandClient((AppClient) client);
    // } else if (client instanceof AdminClient) {
    // return this.getAdminCommandClient(client);
    // }
    // } else {
    // throw new HomeException("JDSClientService is null", HomeException.NOTLOGIN);
    // }
    // return commandClientMap;
    // }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis() - 3600000);
    }

}