package net.ooder.agent.client.home.ct;

import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.iot.*;
import net.ooder.agent.client.iot.ct.CtIotCacheManager;
import net.ooder.agent.client.iot.enums.*;
import net.ooder.agent.client.iot.json.AlarmMessageInfo;
import net.ooder.agent.client.iot.json.SensorDataInfo;
import net.ooder.agent.client.iot.json.SensorInfo;
import  net.ooder.annotation.JoinOperator;
import  net.ooder.annotation.Operator;
import  net.ooder.common.Condition;
import  net.ooder.common.JDSException;
import  net.ooder.common.ReturnType;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.common.util.DateUtility;
import  net.ooder.common.util.StringUtility;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.engine.ConnectInfo;
import  net.ooder.engine.ConnectionHandle;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.home.client.AppClient;
import  net.ooder.agent.client.home.client.CommandClient;
import  net.ooder.agent.client.home.engine.HomeEventControl;
import  net.ooder.agent.client.home.engine.HomeServer;
import  net.ooder.agent.client.home.engine.IOTDataEngine;
import  net.ooder.agent.client.home.event.DeviceEvent;
import  net.ooder.agent.client.home.event.GatewayEvent;
import  net.ooder.agent.client.home.event.PlaceEvent;
import  net.ooder.agent.client.home.event.SensorEvent;
import  net.ooder.agent.client.home.udp.UDPData;
import  net.ooder.jds.core.User;
import  net.ooder.msg.Msg;
import  net.ooder.msg.MsgFactroy;
import  net.ooder.msg.SensorMsg;
import  net.ooder.org.OrgManager;
import  net.ooder.org.Person;
import  net.ooder.org.PersonNotFoundException;
import  net.ooder.org.query.MsgConditionKey;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.JDSServer;
import  net.ooder.server.OrgManagerFactory;
import  net.ooder.common.ConfigCode;

import java.util.*;

public class CtAppClientImpl implements AppClient {

    private JDSServer jdsServer;

    private JDSSessionHandle sessionHandle;

    private ConfigCode configCode;

    private ConnectInfo connInfo;

    private OrgManager orgManager;

    private CtIotCacheManager iotCacheManager;

    private JDSClientService client;

    private IOTDataEngine dataEngine;

    private IOTDataEngine msgEngine;

    private Log logger = LogFactory.getLog(HomeConstants.CONFIG_ENGINE_KEY, CtAppClientImpl.class);

    public CtAppClientImpl() throws JDSException {
        this.jdsServer = JDSServer.getInstance();
        iotCacheManager = HomeServer.getAppEngine();
    }

    public CtAppClientImpl(JDSSessionHandle sessionHandle, ConfigCode configCode) throws JDSException {
        this.sessionHandle = sessionHandle;
        this.configCode = configCode;
        iotCacheManager = HomeServer.getAppEngine();
    }

    public void connect(ConnectInfo connInfo) throws JDSException {
        this.jdsServer.connect(this.client);
        this.connInfo = connInfo;

    }

    public ReturnType disconnect() throws JDSException {
        checkLogin();
        this.jdsServer.disconnect(this.sessionHandle);

        this.connInfo = null;
        this.sessionHandle = null;
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ConnectInfo getConnectInfo() {
        if (connInfo == null) {
            connInfo = this.client.getConnectInfo();
        }
        return this.connInfo;
    }

    public OrgManager getOrgManager() {
        return this.orgManager;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }


    @Override
    public ConfigCode getConfigCode() {
        return configCode;
    }

    public JDSSessionHandle getSessionHandle() {
        return this.sessionHandle;
    }

    public void checkLogin() throws HomeException {

        // if (getConnectInfo() == null){
        // throw new HomeException("用户未登录", 1005);
        // }
    }

    public synchronized ZNode createGateway(String ieee, String placeId) throws HomeException {
        if (ieee.indexOf("|") > -1) {
            ieee = StringUtility.split(ieee, "|")[1];
        }

        ieee = StringUtility.replace(ieee, ":", "");
        this.dataEngine.createGateway(ieee);
        this.msgEngine.createGateway(ieee);

        Place place = iotCacheManager.getPlaceById(placeId);

        Device gwdevice = null;
        try {
            gwdevice = iotCacheManager.getDeviceByIeee(ieee);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        Map eventContext = fillInUserID(null);

        eventContext.put(PlaceEvent.CONTEXT_DEVICE, gwdevice);

        firePlaceEvent(place, PlaceEventEnums.gatewayAdd, eventContext);

        if (gwdevice == null) {
            throw new HomeException("无效的设备！", 6002);
        }

        gwdevice.setStates(DeviceStatus.ONLINE);
        gwdevice.setDevicetype(HomeConstants.DEVICE_TYPE_GATEWAY);
        gwdevice.setAppaccount(getConnectInfo().getLoginName());
        List<Device> devices = gwdevice.getChildDevices();

        for (Device childdevice : devices) {
            childdevice.setStates(DeviceStatus.ONLINE);
            childdevice.setAppaccount(getConnectInfo().getLoginName());
            this.updateDevice(childdevice);
        }

        this.updateDevice(gwdevice);
        ZNode znode = null;
        List<ZNode> gwZNodes = gwdevice.getAllZNodes();

        for (ZNode gwZNode : gwZNodes) {

            if (gwZNode.getZtype().equals(ZNodeZType.SHARE)) {
                this.deleteZNode(gwZNode.getZnodeid());
            } else {
                Set sensorIdSet = new LinkedHashSet();

                gwZNode.setCreateuiserid(getConnectInfo().getUserID());
                gwZNode.setStatus(DeviceStatus.ONLINE);
                iotCacheManager.updateZNode(gwZNode, true);
                znode = gwZNode;
                List<ZNode> childNodes = gwZNode.getChildNodeList();
                for (ZNode childznode : childNodes) {
                    childznode.setCreateuiserid(getConnectInfo().getUserID());
                    childznode.setStatus(DeviceStatus.ONLINE);
                    sensorIdSet.add(childznode.getZnodeid());
                    iotCacheManager.updateZNode(childznode, true);
                }

                addSensor2Index(placeId, (String[]) sensorIdSet.toArray(new String[0]));
                addSensor2Start(placeId, (String[]) sensorIdSet.toArray(new String[0]));
                eventContext.put(PlaceEvent.CONTEXT_ZNODE, gwZNode);
                firePlaceEvent(place, PlaceEventEnums.gatewayAdd, eventContext);
            }

        }

        return znode;
    }

    @Override
    public List<ZNode> getAllGatewayByPlaceId(String placeId) {
        Place place = null;
        try {
            place = this.getPlaceById(placeId);
        } catch (HomeException e) {
            e.printStackTrace();
        }
        return place.getGateways();
    }

    public void deleteZNode(String znodeId) throws HomeException {
        ZNode znode = iotCacheManager.getZNodeById(znodeId);
        List<String> deleteZnodeId = new ArrayList<String>();

        if (znode != null) {
            Map eventContext = fillInUserID(null);
            deleteZnodeId.add(znode.getZnodeid());

            eventContext.put(GatewayEvent.CONTEXT_SENSORS, znode);
            ReturnType dataEngineReturn = this.dataEngine.deleteNode(znode);
            if (!dataEngineReturn.isSucess()) {
                return;
            }
            ReturnType msgEngineReturn = this.msgEngine.deleteNode(znode);
            if (!msgEngineReturn.isSucess()) {
                return;
            }

            if (znode.getZtype().equals(ZNodeZType.GATEWAY)) {
                for (ZNode node : znode.getChildNodeList()) {
                    this.iotCacheManager.deleteDevice(node.getDeviceid());
                    deleteZnodeId.add(node.getZnodeid());
                }

                this.iotCacheManager.deleteDevice(znode.getDeviceid());
            }

            if (znode.getZtype().equals(ZNodeZType.SHARE)) {
                for (ZNode node : znode.getChildNodeList()) {
                    this.iotCacheManager.deleteZNode(node.getZnodeid());
                    deleteZnodeId.add(node.getZnodeid());
                }
                this.iotCacheManager.deleteZNode(znode.getZnodeid());
            }

            if (znode.getZtype().equals(ZNodeZType.Sensor)) {
                if ((znode.getParentNode() != null) && (znode.getParentNode().getZtype().equals(ZNodeZType.SHARE)
                        // 摄像头直接删除
                        || znode.getSensortype() == 11)) {
                    this.iotCacheManager.deleteZNode(znode.getZnodeid());
                } else {
                    try {
                        String personId = this.orgManager.getPersonByAccount(znode.getEndPoint().getDevice().getAppaccount()).getID();
                        znode.setCreateuiserid(personId);
                        znode.setStatus(DeviceStatus.DELETE);
                        this.iotCacheManager.updateZNode(znode, true);
                    } catch (PersonNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public ZNode bindingGateway(String wbaccount, String ieee) throws HomeException {

        try {
            Device device = iotCacheManager.getDeviceByIeee(ieee);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        ZNode znode = iotCacheManager.findZNodeByDeviceId(ieee, this.getConnectInfo().getUserID());

        fireGatewayEvent(znode, GatewayEventEnums.ACCOUNTUNBIND);

        this.dataEngine.bindingGateway(znode, wbaccount);
        this.msgEngine.bindingGateway(znode, wbaccount);

        // 暂不作实现
        // device.setBindingaccount(wbaccount);
        // this.updateDevice(device);
        return znode;
    }


    public ZNode addDevice(String gatewayid, Integer type, String ieee) throws HomeException {
        Map eventContext = new HashMap();
        eventContext = fillInUserID(eventContext);
        ZNode gatewayZNode = iotCacheManager.getZNodeById(gatewayid);

        Device device = this.iotCacheManager.registerDevice(ieee, gatewayZNode.getDeviceid(), type, getConnectInfo().getLoginName(), null);

        DeviceEndPoint endPoint = iotCacheManager.registerEndPoint(ieee, ieee, null, type, device.getSensortype().getName());
        ZNode znode = endPoint.getAllZNodes().get(0);
        // this.updateEndPoint(endPoint);
        // ZNode znode = this.iotCacheManager.createZNode(endPoint.getEndPointId(),
        // gatewayid, getConnectInfo().getUserID());

        this.dataEngine.createZNode(znode, device);
        this.msgEngine.createZNode(znode, device);

        fireDeviceEvent(device, DeviceEventEnums.register, eventContext);

        return znode;
    }

    public Place createPlace(String name) throws HomeException {

        Place place = this.iotCacheManager.createPlace(name, getConnectInfo().getUserID());
        this.dataEngine.createPlace(place);
        this.msgEngine.createPlace(place);

        firePlaceEvent(place, PlaceEventEnums.placeCreate);

        return place;
    }


    public Alarm createAlarm(String sensorId) throws HomeException {
        Alarm alarm = this.iotCacheManager.creatAlarm(sensorId);
        this.dataEngine.createAlarm(alarm);
        this.msgEngine.createAlarm(alarm);
        Map eventContext = fillInUserID(null);
        eventContext.put(SensorEvent.CONTEXT_ALARM, alarm);
        fireSensorEvent(iotCacheManager.getZNodeById(alarm.getSensorid()), SensorEventEnums.addAlarm, eventContext);
        return alarm;
    }

    public Alarm updateAlarm(Alarm alarm) throws HomeException {

        this.dataEngine.updateAlarm(alarm);
        this.msgEngine.updateAlarm(alarm);
        this.iotCacheManager.updateAlarm(alarm);
        Map eventContext = fillInUserID(null);
        eventContext.put(SensorEvent.CONTEXT_ALARM, alarm);

        fireSensorEvent(iotCacheManager.getZNodeById(alarm.getSensorid()), SensorEventEnums.addAlarm, eventContext);
        return iotCacheManager.getAlarmById(alarm.getAlarmid());
    }

    public Device updateDevice(Device device) throws HomeException {

        this.dataEngine.updateDevice(device);
        this.msgEngine.updateDevice(device);
        this.iotCacheManager.updateDevice(device);
        return device;
    }

    public DeviceEndPoint updateEndPoint(DeviceEndPoint endPoint) throws HomeException {
        this.dataEngine.updateDevice(endPoint.getDevice());
        this.msgEngine.updateDevice(endPoint.getDevice());
        this.iotCacheManager.updateDevice(endPoint.getDevice());
        return endPoint;
    }

    public void deleteAlarm(String alarmId) throws HomeException {
        Alarm alarm = iotCacheManager.getAlarmById(alarmId);
        String sceneid = alarm.getSceneid();
        Map eventContext = fillInUserID(null);
        eventContext.put(SensorEvent.CONTEXT_ALARM, alarm);

        fireSensorEvent(iotCacheManager.getZNodeById(alarm.getSensorid()), SensorEventEnums.removeAlarm, eventContext);
        this.dataEngine.deleteAlarm(alarm);
        this.msgEngine.deleteAlarm(alarm);
        this.iotCacheManager.deleteAlarm(alarmId);
    }

    public void deleteArea(String areaId) throws HomeException {
        Area area = iotCacheManager.getAreaById(areaId);
        Map eventContext = fillInUserID(null);
        eventContext.put("PlaceEvent.CONTEXT_AREA", area);
        firePlaceEvent(iotCacheManager.getPlaceById(area.getPlaceid()), PlaceEventEnums.areaRemove, eventContext);

        this.dataEngine.deleteArea(area);
        this.msgEngine.deleteArea(area);

        this.iotCacheManager.deleteArea(areaId);
    }

    public void deletePlace(String placeId) throws HomeException {
        Place place = iotCacheManager.getPlaceById(placeId);
        firePlaceEvent(place, PlaceEventEnums.placeRemove);

        if (place != null) {
            List<ZNode> gateways = place.getGateways();
            List<Area> areas = place.getAreas();
            if (gateways.size() > 0) {
                throw new HomeException("当前家中存在正在工作的网关，请先移除后再试！");
            }

            this.dataEngine.deletePlace(place);
            this.msgEngine.deletePlace(place);
            for (Area area : areas) {
                deleteArea(area.getAreaid());
            }
            for (ZNode gateway : gateways) {
                deleteZNode(gateway.getZnodeid());
            }

            this.iotCacheManager.deletePlace(placeId);
        }
    }

    public Area createArea(String name, String placeId) throws HomeException {
        if ((name == null) || (name.equals(""))) {
            throw new HomeException("房间名称不能为空", 8002);
        }

        Area area = this.iotCacheManager.createArea(name, placeId);

        this.dataEngine.createArea(area);
        this.msgEngine.createArea(area);
        Map eventContext = fillInUserID(null);
        eventContext.put(PlaceEvent.CONTEXT_AREA, area);
        firePlaceEvent(iotCacheManager.getPlaceById(area.getPlaceid()), PlaceEventEnums.areaAdd, eventContext);
        return area;
    }


    public void setClientService(JDSClientService client) {
        this.client = client;
    }


    public List<ZNode> getAllZNodeByPlaceId(String placeId) throws HomeException {
        List<ZNode> gatewayNodes = getAllGatewayByPlaceId(placeId);
        List<ZNode> cnodes = new ArrayList<ZNode>();
        for (ZNode gatewayNode : gatewayNodes) {
            List<ZNode> childNodes = iotCacheManager.getAllChildNode(gatewayNode.getZnodeid());
            for (ZNode childNode : childNodes) {
                if (!cnodes.contains(childNode) && !childNode.getStatus().equals(DeviceStatus.DELETE)) {
                    cnodes.add(childNode);
                }
            }
        }
        // List<ZNode> znodes = filterNode(cnodes);
        return cnodes;
    }

    public List<ZNode> getSensorByIeees(List<String> ieees) throws HomeException {
        List<ZNode> cnodes = new ArrayList<ZNode>();
        for (String ieee : ieees) {

            if (ieee.length() > 16) {
                ieee = ieee.substring(0, 16);
            }

            Device device = null;
            try {
                device = iotCacheManager.getDeviceByIeee(ieee);
            } catch (JDSException e) {
                throw new HomeException(e);
            }
            List<ZNode> nodes = device.getAllZNodes();
            cnodes.addAll(nodes);

        }
        return cnodes;

    }

    public void updateAreaName(String areaId, String name) throws HomeException {
        Area area = iotCacheManager.getAreaById(areaId);
        area.setName(name);
        this.dataEngine.updateAreaName(area, name);
        this.msgEngine.updateAreaName(area, name);
        this.iotCacheManager.updateArea(area);
    }

    public void updatePlace(Place place) throws HomeException {

        this.dataEngine.updatePlace(place);
        this.msgEngine.updatePlace(place);
        this.iotCacheManager.updatePlace(place);
    }

    public void updateGateway(String ieee, String name, String bindingaccount, String gatewayId, String placeId) throws HomeException {
        Device device = null;
        if (ieee != null) {
            try {
                device = iotCacheManager.getDeviceByIeee(ieee);
            } catch (JDSException e) {
                e.printStackTrace();
            }
        } else if (gatewayId != null) {
            ZNode gateway = iotCacheManager.getZNodeById(gatewayId);
            device = gateway.getEndPoint().getDevice();
        }
        if (device == null) {
            throw new HomeException("设备不存在!", 6002);
        }

        List<Device> devices = device.getChildDevices();

        for (Device childdevice : devices) {
            childdevice.setStates(DeviceStatus.ONLINE);
            childdevice.setAppaccount(childdevice.getDeviceid());
            updateDevice(childdevice);
        }

        List<ZNode> znodes = device.getAllZNodes();

        for (ZNode znode : znodes) {
            if (znode.getZtype() != ZNodeZType.SHARE) {
                znode.setCreateuiserid(getConnectInfo().getUserID());
                iotCacheManager.updateZNode(znode, true);

            }
        }

    }

    public ZNode updateSensorName(String sensorId, String name) throws HomeException {
        ZNode znode = iotCacheManager.getZNodeById(sensorId);
        znode.setName(name);
        iotCacheManager.updateZNode(znode, true);
        return znode;
    }

    public SensorInfo updateSensorValue(String sensorId, DeviceDataTypeKey attributeName, String value) throws HomeException {
        ZNode znode = iotCacheManager.getZNodeById(sensorId);
        DeviceEndPoint deviceEndPoint = znode.getEndPoint();
        deviceEndPoint.updateCurrvalue(attributeName, value);
        this.updateEndPoint(deviceEndPoint);
        return new SensorInfo(znode);
    }

    public List<SensorMsg> getAlarmMessageByPlaceId(String placeId) throws HomeException {
        List<SensorMsg> msglist = new ArrayList<SensorMsg>();
        try {


            msglist = (List<SensorMsg>) MsgFactroy.getInstance().getClient(getConnectInfo().getUserID(), SensorMsg.class).getAllSendMsg().get();

        } catch (Exception e) {
            throw new HomeException("数据读取错误！");
        }

        return msglist;
    }

    public List<Area> getAreasByIds(List<String> ids) throws HomeException {
        List<Area> areaInfos = new ArrayList<Area>();

        for (String areaId : ids) {

            areaInfos.add(iotCacheManager.getAreaById(areaId));
        }
        return areaInfos;
    }

    private void checkGateway(String ieee) throws HomeException {

        Device device = null;
        try {
            device = iotCacheManager.getDeviceByIeee(ieee);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        this.dataEngine.checkGateway(device, getConnectInfo().getUserID());
        this.msgEngine.checkGateway(device, getConnectInfo().getUserID());
        if (device == null) {
            throw new HomeException("网关校验失败！", 6002);
        }
    }

    public boolean canCreateGateway(String ieee, String placeId) throws HomeException {

        Device device = null;
        try {
            device = iotCacheManager.getDeviceByIeee(ieee);
        } catch (JDSException e) {
            e.printStackTrace();
        }


        if (device == null) {
            return true;
        }

        checkGateway(ieee);

        if ((device != null) && (device.getAppaccount() == null)) {
            return true;
        }

        // 取DB中值对比不能取缓存
        if ((device != null) && (device.getAppaccount().equals(device.getDeviceid()))) {
            return true;
        }

        Place place;
        if ((device != null) && (device.getAppaccount().equals(getConnectInfo().getLoginName()))) {
            ZNode cNode = iotCacheManager.findZNodeByDeviceId(device.getDeviceid(), getConnectInfo().getUserID());

            if (cNode != null) {
                throw new HomeException("网关已添加！", 6002);
            }

            return true;
        }

        List<Person> persons = this.iotCacheManager.getAllUserByDeviceId(device.getDeviceid());
        for (Person person : persons) {
            if (getConnectInfo().getUserID().equals(person.getID())) {
                throw new HomeException("网关已在共享列表中，请删除后再试！", 6002);
            }
        }

        ReturnType datareturntype = this.dataEngine.canCreateGateway(device);
        ReturnType msgreturntype = this.msgEngine.canCreateGateway(device);
        if ((!datareturntype.isSucess()) || (!msgreturntype.isSucess())) {
            return true;
        }

        return false;
    }

    public Place shareGateway(String ieee, String mainaccount, String mainpassword, String placeId) throws HomeException {

        if (ieee.indexOf("|") > -1) {
            ieee = StringUtility.split(ieee, "|")[1];
        }

        ieee = StringUtility.replace(ieee, ":", "");
        boolean isCan = true;

        Device device = null;
        try {
            device = iotCacheManager.getDeviceByIeee(ieee);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        if ((device.getAppaccount() != null) && (device.getAppaccount().equals(getConnectInfo().getLoginName()))) {
            throw new HomeException("网关已添加！", HomeException.GETWAYIDEXITS);
        }
        if (device.getAppaccount().equals(getConnectInfo().getLoginName())) {
            throw new HomeException("网关校验失败！", HomeException.GETWAYIDINVALID);
        }

        try {
            isCan = OrgManagerFactory.getOrgManager().verifyPerson(mainaccount, mainpassword);
        } catch (PersonNotFoundException e) {
            throw new HomeException("用户不存在！", HomeException.NOTLOGINEDERROR);
        }

        if (!isCan) {
            throw new HomeException("密码错误！", 1003);
        }

        Person person = null;
        try {

            person = OrgManagerFactory.getOrgManager().getPersonByAccount(mainaccount);
        } catch (PersonNotFoundException e) {
            e.printStackTrace();
        }

        ZNode gateway = iotCacheManager.findZNodeByDeviceId(device.getDeviceid(), person.getID());


        // ZNode gateway = getZNodeById(sourceNode.getZnodeid());

        fireGatewayEvent(gateway, GatewayEventEnums.GATEWAYSHARING);

        String copyznodeId = this.iotCacheManager.copyGateway(gateway.getZnodeid(), getConnectInfo().getUserID(), ZNodeZType.SHARE, placeId).getZnodeid();
        ZNode copyZnode = iotCacheManager.getZNodeById(copyznodeId);
        List<ZNode> znodes = copyZnode.getChildNodeList();

        String[] sensorIds = new String[znodes.size()];
        for (int k = 0; k < znodes.size(); k++) {
            ZNode znode = znodes.get(k);
            sensorIds[k] = znode.getZnodeid();
        }

        addSensor2Index(placeId, sensorIds);
        addSensor2Start(placeId, sensorIds);

        ZNode copyGateway = iotCacheManager.getZNodeById(copyznodeId);
        this.dataEngine.shareGateway(gateway, copyGateway);
        this.msgEngine.shareGateway(gateway, copyGateway);

        fireGatewayEvent(gateway, GatewayEventEnums.GATEWAYSHARED);

        return iotCacheManager.getPlaceById(copyGateway.getEndPoint().getDevice().getPlaceid());
    }

    public void addLightSensorScene(Scene info) throws HomeException {
        updateScene(info);
    }

    public void deleteScene(String sensorSceneId) throws HomeException {
        Scene scene = iotCacheManager.getSceneById(sensorSceneId);
        ZNode znode = iotCacheManager.getZNodeById(scene.getSensorid());
        Map eventContext = fillInUserID(null);
        eventContext.put("SensorEvent.CONTEXT_SCENE", scene);
        fireSensorEvent(znode, SensorEventEnums.sceneRemoved, eventContext);
        this.dataEngine.deleteScene(znode, scene);
        this.msgEngine.deleteScene(znode, scene);

        this.iotCacheManager.deleteScene(sensorSceneId);
    }

    public void setScene(String sensorSceneId, Integer lightValue) throws HomeException {
        Scene scene = iotCacheManager.getSceneById(sensorSceneId);
        scene.setIntvalue(lightValue);
        updateScene(scene);
    }

    public void setLightSensor(String sensorId, Integer lightValue) throws HomeException {
        DeviceEndPoint endPoint = iotCacheManager.getZNodeById(sensorId).getEndPoint();
        ZNode znode = iotCacheManager.getZNodeById(sensorId);
        // Map<String, CommandClient> gwClientMap = HomeServer.getMsgEngine(systemCode).getCommandClient(this);
        CommandClient commandClient = null;
        try {
            commandClient = CtIotCacheManager.getInstance().getCommandClientByieee(znode.getParentNode().getEndPoint().getDevice().getSerialno());
        } catch (JDSException e) {
            throw new HomeException(e);
        }
        commandClient.sendLightCommand(endPoint.getIeeeaddress(), lightValue);

        if (lightValue.intValue() == 0) {
            endPoint.updateCurrvalue(DeviceDataTypeKey.StateOnOff, HomeConstants.DEVICE_DATATYPE_STATEONOFF_OFF);
        } else {
            endPoint.updateCurrvalue(DeviceDataTypeKey.StateOnOff, HomeConstants.DEVICE_DATATYPE_STATEONOFF_ON);
        }
        this.updateEndPoint(endPoint);
    }

    private String reSceneName(String name, List<Scene> scenes) {
        boolean hasname = false;
        for (Scene scene : scenes) {
            if (scene.getName().equals(name)) {
                hasname = true;
            }
        }

        if (hasname) {
            if (!name.endsWith(")")) {
                name = name + "(1)";
            }
            Integer index = Integer.valueOf(2);
            String realName = name.substring(0, name.indexOf("("));
            for (Scene scene : scenes) {
                if (scene.getName().startsWith(realName)) {
                    index = Integer.valueOf(index.intValue() + 1);
                }
            }
            name = realName + "(" + index + ")";
        }

        return name;
    }

    public void updateScene(Scene scene) throws HomeException {
        ZNode znode = iotCacheManager.getZNodeById(scene.getSensorid());
        this.dataEngine.updateScene(znode, scene);
        this.msgEngine.updateScene(znode, scene);

        List<Scene> scenes = this.getSceneBySensorId(scene.getSensorid());

        String name = reSceneName(scene.getName(), scenes);
        scene.setName(name);

        this.iotCacheManager.updateScene(this.iotCacheManager.getSceneById(scene.getSceneid()));
    }

    public void updateScene(String sensorSceneId, int status) throws HomeException {
        Scene scene = iotCacheManager.getSceneById(sensorSceneId);
        scene.setStatus(Integer.valueOf(status));
        updateScene(scene);
    }

    public void updateGatewayStatus(String gatewayId, DeviceStatus status) throws HomeException {
        ZNode znode = iotCacheManager.getZNodeById(gatewayId);
        znode.setStatus(status);
        iotCacheManager.updateZNode(znode, true);
    }

    public List<User> getShareUser(String gatewayId) throws HomeException {
        ZNode gateway = iotCacheManager.getZNodeById(gatewayId);
        List<User> users = new ArrayList<User>();

        if (gateway != null && gateway.getZtype().equals(ZNodeZType.GATEWAY)) {
            Device device = iotCacheManager.getDeviceById(gateway.getDeviceid());
            List<Person> persons = this.iotCacheManager.getAllUserByDeviceId(device.getDeviceid());
            for (Person person : persons) {
                users.add(this.getUserById(person.getID()));
            }

        }
        return users;
    }

    public Scene createScene(String sensorId) throws HomeException {

        Scene scene = this.iotCacheManager.createScene(sensorId);
        ZNode znode = iotCacheManager.getZNodeById(scene.getSensorid());
        this.dataEngine.creatScene(znode, scene);
        this.msgEngine.creatScene(znode, scene);
        Map eventContext = fillInUserID(null);
        eventContext.put("SensorEvent.CONTEXT_SCENE", scene);
        fireSensorEvent(znode, SensorEventEnums.sceneAdded, eventContext);

        return scene;
    }

    public User getMainUserInfo(String ieee) throws HomeException {

        Person person = this.iotCacheManager.getMainUserByIeee(ieee);

        return getUserById(person.getID());
    }

    private User getUserById(final String Id) throws HomeException {

        Person person = null;
        try {
            person = OrgManagerFactory.getOrgManager().getPersonByID(Id);
        } catch (final PersonNotFoundException e) {
            throw new HomeException("账户不存在", 1003);
        }
        final User user = new User();
        user.setAccount(person.getAccount());
        user.setId(person.getID());
        user.setEmail(person.getEmail());
        user.setPhone(person.getMobile());
        user.setName(person.getName());
        return user;
    }

    public void addSensor2Index(String placeId, String[] sensorIds) throws HomeException {
        Place place = iotCacheManager.getPlaceById(placeId);
        String indexStr = place.getMemo();
        String[] indexArr = StringUtility.split(indexStr, ",");
        indexStr = mergeArray(indexArr, sensorIds, false);
        place.setMemo(indexStr);
        this.dataEngine.addSensor2Index(place, getSensorListByIds(sensorIds));
        this.msgEngine.addSensor2Index(place, getSensorListByIds(sensorIds));

        this.updatePlace(place);
    }

    public List<ZNode> getSensorListByIds(String[] ids) throws HomeException {
        List sensorList = new ArrayList();
        for (String znodeId : ids) {
            ZNode node = iotCacheManager.getZNodeById(znodeId);
            sensorList.add(node);
        }
        return sensorList;
    }

    public void addSensor2Start(String placeId, String[] sensorIds) throws HomeException {
        Place dbplace = iotCacheManager.getPlaceById(placeId);
        Place place = iotCacheManager.getPlaceById(placeId);
        String startStr = dbplace.getStart();
        String[] startArr = StringUtility.split(startStr, ",");
        startStr = mergeArray(startArr, sensorIds, false);
        dbplace.setStart(startStr);
        this.dataEngine.addSensor2Start(place, getSensorListByIds(sensorIds));
        this.msgEngine.addSensor2Start(place, getSensorListByIds(sensorIds));

        this.updatePlace(dbplace);
    }

    public void removeSensor2Index(String placeId, String[] sensorIds) throws HomeException {
        Place place = iotCacheManager.getPlaceById(placeId);
        String indexStr = place.getMemo();
        String[] indexArr = StringUtility.split(indexStr, ",");
        indexStr = mergeArray(indexArr, sensorIds, true);

        place.setMemo(indexStr);
        this.dataEngine.removeSensor2Index(place, getSensorListByIds(sensorIds));
        this.msgEngine.removeSensor2Index(place, getSensorListByIds(sensorIds));

        this.updatePlace(place);
    }

    public void removeSensor2Start(String placeId, String[] sensorIds) throws HomeException {
        Place place = iotCacheManager.getPlaceById(placeId);
        String startStr = place.getStart();
        String[] startArr = StringUtility.split(startStr, ",");
        startStr = mergeArray(startArr, sensorIds, true);

        place.setStart(startStr);
        this.dataEngine.removeSensor2Start(place, getSensorListByIds(sensorIds));
        this.msgEngine.removeSensor2Start(place, getSensorListByIds(sensorIds));

        this.updatePlace(place);
    }

    // public List<SensorDataInfo> getHistorySennsorData(String sensorId, long starttime, long endtime, int num) throws
    // HomeException {
    // List<SensorDataInfo> msgs = new ArrayList<SensorDataInfo>();
    // try {
    // Person person =
    // OrgManagerFactory.getOrgManager(this.getSystemCode()).getPersonByID(this.getConnectInfo().getUserID());
    //
    // // List<ZNode> nodes = iotCacheManager.getAllSensors();
    // // for (ZNode node : nodes) {
    // List<Msg> msglist = person.getPersonMsgGroupById(sensorId,MsgType.SENSOR.getType()).getHisMsgList();
    //
    // for (Msg msg : msglist) {
    // if (msg != null && msg.getSensorId() != null && msg.getSensorId().equals(sensorId)) {
    //
    // SensorDataInfo info = new SensorDataInfo();
    // info.setId(msg.getId());
    // info.setSensorId(msg.getSensorId());
    // info.setValue(msg.getEvent());
    // info.setIconTemp(msg.getBody());
    // info.setDatetime(DateUtility.formatDate(msg.getArrivedTiem(), "yyyy-MM-dd HH:mm:ss"));
    // info.setHtmlValue(msg.getTitle());
    //
    // msgs.add(info);
    // }
    // }
    // // }
    //
    // } catch (Exception e) {
    // throw new HomeException("历史数据读取错误！");
    // }
    // return msgs;
    // }

    // private List<ZNode> filterNode(List<ZNode> znodes) throws HomeException {
    // List arrayznodes = new ArrayList();
    //
    // List ieees = new ArrayList();
    // for (ZNode info : znodes) {
    // // ZNode info = getZNodeById(znode.getZnodeid());
    //
    // if ((info != null) && (info.getDevice() != null)) {
    // String ieee = info.getDevice().getieee();
    // if (info.getDevice().getDevicetype() !=
    // HomeConstants.DEVICE_TYPE_GATEWAY) {
    // if ((info != null) && (info.getParentNode() != null)) {
    // if (!info.getParentNode().getStatus().equals(
    // ZNodeStatus.DELETED)) {
    // if (info.getSensortype() == null) {
    // continue;
    // }
    // if ((info.getStatus()
    // .equals(ZNodeStatus.DELETED))
    // && (!info.getSensortype().equals("22"))
    // && (!info.getSensortype().equals("11"))
    // && (!info.getSensortype().equals("23"))) {
    // continue;
    // }
    //
    // if ((info.getSensortype()
    // .equals(Integer.valueOf(1)))
    // && (ieee.endsWith("01"))) {
    // continue;
    // }
    // if ((info.getSensortype()
    // .equals(Integer.valueOf(4)))
    // && (ieee.endsWith("02"))) {
    // continue;
    // }
    // if ((info.getSensortype()
    // .equals(Integer.valueOf(4)))
    // && (ieee.endsWith("03"))) {
    // continue;
    // }
    //
    // if (ieees.contains(ieee)) {
    // continue;
    // }
    // arrayznodes.add(info);
    // }
    // }
    // } else {
    //
    // arrayznodes.add(info);
    //
    // ieees.add(info.getDevice().getieee());
    // }
    // }
    //
    // }
    //
    // return arrayznodes;
    // }

    public List<ZNode> getIndexSensorList(String placeId) throws HomeException {
        String startStr = iotCacheManager.getPlaceById(placeId).getMemo();
        String[] startArr = StringUtility.split(startStr, ",");
        List<ZNode> indexSensorList = new ArrayList<ZNode>();

        for (String znodeId : startArr) {
            ZNode node = iotCacheManager.getZNodeById(znodeId);
            indexSensorList.add(node);
        }
        // List<ZNode> znodes = filterNode(indexSensorList);
        List<String> startList = new ArrayList<String>();
        for (String znodeId : startArr) {
            ZNode node = iotCacheManager.getZNodeById(znodeId);
            if (!indexSensorList.contains(node) || node.getStatus().equals(DeviceStatus.DELETE)) {
                startList.add(znodeId);
            }
        }

        if (startList.size() > 0) {
            this.removeSensor2Index(placeId, startList.toArray(new String[]{}));
        }

        return indexSensorList;
    }

    @Override
    public Scene creatScene(String sensorId) throws HomeException {
        Scene scene = this.iotCacheManager.createScene(sensorId);
        ZNode znode = iotCacheManager.getZNodeById(scene.getSensorid());
        this.dataEngine.creatScene(znode, scene);
        this.msgEngine.creatScene(znode, scene);
        Map eventContext = fillInUserID(null);
        eventContext.put("SensorEvent.CONTEXT_SCENE", scene);
        fireSensorEvent(znode, SensorEventEnums.sceneAdded, eventContext);
        return scene;
    }

    private static String mergeArray(String[] stringA, String[] stringB, boolean combin) {
        LinkedHashSet<String> sensorsSet = new LinkedHashSet();
        for (String string : stringA) {
            sensorsSet.add(string);
        }

        for (String string : stringB) {
            if (combin)
                sensorsSet.remove(string);
            else {
                sensorsSet.add(string);
            }
        }
        String strs = "";
        for (String str : sensorsSet) {
            strs = strs + "," + str;
        }

        return strs;
    }

    private void fireDeviceEvent(Device device, DeviceEventEnums eventID) throws HomeException {
        fireDeviceEvent(device, eventID, null);
    }

    private void fireDeviceEvent(Device device, DeviceEventEnums eventID, Map eventContext) throws HomeException {
        eventContext = fillInUserID(eventContext);
        DeviceEvent event = new DeviceEvent(device, this.client, eventID, null);

        event.setContextMap(eventContext);
        try {
            HomeEventControl.getInstance().dispatchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void firePlaceEvent(Place place, PlaceEventEnums eventID) throws HomeException {
        firePlaceEvent(place, eventID, null);
    }

    private void firePlaceEvent(Place place, PlaceEventEnums eventID, Map eventContext) throws HomeException {
        eventContext = fillInUserID(eventContext);
        PlaceEvent event = new PlaceEvent(place, this.client, eventID, null);

        event.setContextMap(eventContext);
        try {
            HomeEventControl.getInstance().dispatchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fireGatewayEvent(ZNode znode, GatewayEventEnums eventID) throws HomeException {
        fireGatewayEvent(znode, eventID, null);
    }

    private void fireGatewayEvent(ZNode znode, GatewayEventEnums eventID, Map eventContext) throws HomeException {
        eventContext = fillInUserID(eventContext);
        GatewayEvent event = new GatewayEvent(znode, this.client, eventID, null);
        event.setContextMap(eventContext);
        try {
            HomeEventControl.getInstance().dispatchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fireSensorEvent(ZNode znode, SensorEventEnums eventID, Map eventContext) throws HomeException {
        eventContext = fillInUserID(eventContext);
        SensorEvent event = new SensorEvent(znode, this.client, eventID, null);

        event.setContextMap(eventContext);
        try {
            HomeEventControl.getInstance().dispatchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fireSensorEvent(ZNode znode, SensorEventEnums eventID) throws HomeException {
        fireSensorEvent(znode, eventID, null);
    }

    private Map fillInUserID(Map ctx) {
        Map result = ctx;
        if (result == null) {
            result = new HashMap();
        }

        result.put("iotCacheManager.USERID", getConnectInfo().getUserID());
        return result;
    }

    public boolean sendAlarmMsg(SensorMsg msg) throws HomeException {
        UDPData udpdata = new UDPData();
        AlarmMessageInfo alarmInfo = new AlarmMessageInfo();
        alarmInfo.setId(msg.getId());
        alarmInfo.setTime(msg.getTitle());
        alarmInfo.setMessage(msg.getBody());
        alarmInfo.setSensorId(msg.getSensorId());
        alarmInfo.setTime(DateUtility.formatDate(new Date(msg.getReceiveTime()), "yyyy-MM-dd HH:mm:ss"));

        boolean isAlarm = true;
        boolean isSend = true;
        // try {
        // ZNode znode=this.getZNodeById(alarmInfo.getSensorId());
        // isAlarm=znode.isAlarm();
        // } catch (Exception e) {
        // //.printStackTrace();
        // }

        udpdata.setCommond(Integer.valueOf(2));
        udpdata.setEvent(Integer.valueOf(2001));
        udpdata.setAlarmMessageInfo(alarmInfo);
        isSend = sendUDPMsg(udpdata);
        return isSend;
    }

    private boolean sendUDPMsg(UDPData udpdata) throws HomeException {
        boolean isSend = true;
        String dataStr = JSONObject.toJSON(udpdata).toString();

        ConnectionHandle handle = this.client.getConnectionHandle();
        // try {
        // if ((handle != null) && (handle.getConnectInfo() == null)) {
        //
        // handle.connect(this.client.getContext());
        // }
        //
        // } catch (JDSException e1) {
        //
        // e1.printStackTrace();
        // }

        if ((handle != null) && (handle.getConnectInfo() != null)) {
            try {
                isSend = handle.send(dataStr);
            } catch (JDSException e) {
                throw new HomeException(e);
            }
        }
        return isSend;
    }

    public boolean sendDataMsg(SensorMsg msg) throws HomeException {
        UDPData udpdata = new UDPData();
        SensorDataInfo sensorinfo = (SensorDataInfo) JSONObject.parseObject(msg.getBody(), SensorDataInfo.class);

        udpdata.setCommond(Integer.valueOf(3));
        // udpdata.setEvent(Integer.valueOf((msg.getEvent() == null || msg.getEvent().equals("")) ? 3001 :
        // Integer.valueOf(msg.getEvent()).intValue()));
        udpdata.setSensorinfo(sensorinfo);
        return sendUDPMsg(udpdata);
    }

    @Override
    public boolean sendSystemMsg(Msg msg) throws HomeException {
        return false;
    }


    public void setOutLetSensor(String sensorId, boolean vlaue) throws HomeException {
        ZNode znode = iotCacheManager.getZNodeById(sensorId);
        DeviceEndPoint endPoint = znode.getEndPoint();
        endPoint.updateCurrvalue(DeviceDataTypeKey.StateOnOff, vlaue ? HomeConstants.DEVICE_DATATYPE_STATEONOFF_ON : HomeConstants.DEVICE_DATATYPE_STATEONOFF_OFF);
        this.dataEngine.setOutLetSensorInfo(znode, vlaue);
        this.msgEngine.setOutLetSensorInfo(znode, vlaue);

        // updateDevice(device);
    }

    public ConnectionHandle getConnectionHandle() {
        return this.client.getConnectionHandle();
    }

    public void setConnectionHandle(ConnectionHandle handle) {
        this.client.setConnectionHandle(handle);
    }

    public JDSContext getContext() {
        return this.client.getContext();
    }

    public void setContext(JDSContext context) {
        this.client.setContext(context);
    }

    @Override
    public String getSystemCode() {
        return JDSActionContext.getActionContext().getSystemCode();
    }

    public void sendAddSensorCommand(String gatewayid, String ieee) throws HomeException {
        // Map<String, CommandClient> gwClientMap = HomeServer.getMsgEngine(systemCode).getCommandClient(this);
        ZNode znode = iotCacheManager.getZNodeById(gatewayid);
        String gatewayiee = znode.getEndPoint().getDevice().getSerialno();
        CommandClient commandClient = null;
        try {
            commandClient = CtIotCacheManager.getInstance().getCommandClientByieee(gatewayiee);
        } catch (JDSException e) {
            throw new HomeException(e);
        }

        // 东胜二维码 1003|00155f86
        if (ieee.indexOf("|") > -1) {
            ieee = StringUtility.split(ieee, "|")[1];
            commandClient.sendAddSensorCommand(ieee, null);
        } else {
            // 爱耳目摄像头
            if (ieee.startsWith("137894")) {
                this.addDevice(gatewayid, HomeConstants.DEVICE_TYPE_CAPTURE, ieee);
            } else {
                commandClient.sendAddSensorCommand(ieee, null);
            }
        }

    }

    public List<SensorMsg> getLastSensorHistoryData(String ieee, Integer currentIndex, Integer pageSize) throws HomeException {
        if (ieee != null && ieee.length() < 20) {
            Device device = null;
            try {
                device = iotCacheManager.getDeviceByIeee(ieee);
            } catch (JDSException e) {
                e.printStackTrace();
            }
            ZNode znode = device.getAllZNodes().get(0);
            ieee = znode.getZnodeid();
        }
        ZNode znode = iotCacheManager.getZNodeById(ieee);
        List<SensorMsg> msglist = new ArrayList<SensorMsg>();
        try {
            Person person = OrgManagerFactory.getOrgManager().getPersonByAccount(znode.getEndPoint().getDevice().getBindingaccount());
            Condition condition = new Condition(MsgConditionKey.MSG_ACTIVITYINSTID, Operator.EQUALS, ieee);

            msglist = (List<SensorMsg>) MsgFactroy.getInstance().getClient(person.getID(), SensorMsg.class).getMsgList(condition).get();

        } catch (Exception e) {
            throw new HomeException("历史数据读取错误！");
        }

        return msglist;
    }

    public static void main(String[] args) {

    }

    public JDSClientService getClient() {
        return client;
    }

    public void setClient(JDSClientService client) {
        this.client = client;
    }

    public Scene geSceneById(String scenId) throws HomeException {

        return iotCacheManager.getSceneById(scenId);
    }

    public List<Alarm> getAlarmBySensorId(String sensorId) throws HomeException {
        return iotCacheManager.getAlarmBySensorId(sensorId);
    }

    public List<Area> getAllAreaByPlaceId(String placeId) throws HomeException {
        return iotCacheManager.getAllAreaByPlaceId(placeId);
    }

    public List<Place> getAllPlace() throws HomeException {
        return iotCacheManager.getAllPlaceByUserId(this.getConnectInfo().getUserID());
    }

    public List<Scene> getSceneBySensorId(String sensorId) throws HomeException {
        return iotCacheManager.getSceneBySensorId(sensorId);
    }

    public List<Sensortype> getSensorTypesByGatewayId(String gatewayId) throws HomeException {
        return iotCacheManager.getSensorTypesByGatewayId(gatewayId);
    }

    public Sensortype getSensorTypesByNo(Integer typno) throws HomeException {

        return iotCacheManager.getSensorTypesByNo(typno);
    }

    public ZNode getZNodeByIeee(String ieee) throws HomeException {
        return iotCacheManager.findZNodeByIeee(ieee, this.getConnectInfo().getUserID());
    }

    public Alarm getAlarmById(String alarmid) throws HomeException {

        return iotCacheManager.getAlarmById(alarmid);
    }

    public Area getAreaById(String areaId) throws HomeException {

        return iotCacheManager.getAreaById(areaId);
    }

    public Place getPlaceById(String placeId) throws HomeException {

        return iotCacheManager.getPlaceById(placeId);
    }

    public Scene getSceneById(String id) throws HomeException {
        return iotCacheManager.getSceneById(id);
    }

    public List<ZNode> getAllChildNode(String ieee) throws HomeException {

        return iotCacheManager.getAllChildNode(ieee);
    }

    public ZNode getZNodeById(String znodeId) throws HomeException {

        return iotCacheManager.getZNodeById(znodeId);
    }

    public List<SensorMsg> getSensorHistoryData(String znodeId, Date startTime, Date endTime, Integer currentIndex, Integer pageSize) throws HomeException {
        if (znodeId != null && znodeId.length() < 20) {
            Device device = null;
            try {
                device = iotCacheManager.getDeviceByIeee(znodeId);
            } catch (JDSException e) {
                e.printStackTrace();
            }
            ZNode znode = device.getAllZNodes().get(0);
            znodeId = znode.getZnodeid();
        }
        ZNode znode = iotCacheManager.getZNodeById(znodeId);

        List<SensorMsg> msglist = new ArrayList<SensorMsg>();
        try {
            Person person = OrgManagerFactory.getOrgManager().getPersonByAccount(znode.getEndPoint().getDevice().getBindingaccount());

            Condition condition = new Condition(MsgConditionKey.MSG_ACTIVITYINSTID, Operator.EQUALS, znodeId);
            if (startTime != null && !startTime.equals("") && endTime != null && !endTime.equals("")) {
                Condition timeCondition = new Condition(MsgConditionKey.MSG_EVENTTIME, Operator.BETWEEN, new Date[]{startTime, endTime});
                condition.addCondition(timeCondition, JoinOperator.JOIN_AND);
            }

            msglist = (List<SensorMsg>) MsgFactroy.getInstance().getClient(person.getID(), SensorMsg.class).getMsgList(condition).get();

        } catch (Exception e) {
            throw new HomeException("历史数据读取错误！");
        }
        return msglist;
    }

    public void beginTransaction() throws HomeException {
        checkLogin();
        // try {
        // Transaction tx =
        // HibernateSessionFactory.getSession().getTransaction();
        // if (!tx.isActive()) {
        // tx.begin();
        // }
        // DbManager.getInstance().beginTransaction();
        // } catch (SQLException sqle) {
        // throw new HomeException(
        // "Failed to beging transaction of client service.", sqle,
        // HomeException.TRANSACTIONBEGINERROR);
        // }
    }

    public void commitTransaction() throws HomeException {
        checkLogin();
        // try {
        // Transaction tx =
        // HibernateSessionFactory.getSession().getTransaction();
        // if (tx.isInitiator()) {
        // tx.commit();
        // }
        // DbManager.getInstance().endTransaction(true);
        // } catch (SQLException sqle) {
        // throw new HomeException(
        // "Failed to commit transaction of client service.", sqle,
        // HomeException.TRANSACTIONCOMMITERROR);
        // }
    }

    public void rollbackTransaction() throws HomeException {
        checkLogin();
        // try {
        // Transaction tx =
        // HibernateSessionFactory.getSession().getTransaction();
        // if (tx.isInitiator()) {
        // tx.rollback();
        // }
        // DbManager.getInstance().endTransaction(false);
        // } catch (SQLException sqle) {
        // throw new HomeException(
        // "Failed to rollback transaction of client service", sqle,
        // HomeException.TRANSACTIONROLLBACKERROR);
        // }
    }
}
