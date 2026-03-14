package net.ooder.agent.client.home.ct;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.command.HAAttribute;
import net.ooder.agent.client.command.SensorCommand;
import net.ooder.agent.client.iot.*;
import net.ooder.agent.client.iot.ct.CtIotCacheManager;
import net.ooder.agent.client.iot.enums.*;
import net.ooder.agent.client.iot.json.NetworkInfo;
import net.ooder.agent.client.iot.json.device.EndPoint;
import  net.ooder.common.*;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.common.util.DateUtility;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.engine.ConnectInfo;
import  net.ooder.engine.ConnectionHandle;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.agent.client.home.client.CommandClient;
import  net.ooder.agent.client.home.client.GWClient;
import  net.ooder.agent.client.home.engine.HomeEventControl;
import  net.ooder.agent.client.home.engine.HomeServer;
import  net.ooder.agent.client.home.engine.IOTDataEngine;
import  net.ooder.agent.client.home.event.*;
import  net.ooder.msg.*;
import  net.ooder.msg.index.DataIndex;
import  net.ooder.org.OrgManager;
import  net.ooder.org.Person;
import  net.ooder.org.PersonNotFoundException;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.JDSServer;
import  net.ooder.server.OrgManagerFactory;
import  net.ooder.common.ConfigCode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class CtGWClientImpl implements GWClient {

    public static final String USERID = "AdminClient.USERID";

    private JDSServer jdsServer;

    private ConfigCode configCode;

    private ConnectInfo connInfo;

    private OrgManager orgManager;

    private JDSClientService client;

    private IOTDataEngine msgEngine;

    private CtIotCacheManager cacheManager;

    public static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, GWClient.class);

    public CtGWClientImpl() throws JDSException {
        jdsServer = JDSServer.getInstance();
        cacheManager = CtIotCacheManager.getInstance();
        this.msgEngine = HomeServer.getMsgEngine(JDSActionContext.getActionContext().getConfigCode());

    }

    @Override
    public void connect(final ConnectInfo connInfo) throws JDSException {
        this.connInfo = connInfo;
        jdsServer.connect(client);
        this.configCode = client.getConfigCode();

        orgManager = OrgManagerFactory.getOrgManager(client.getConfigCode());
        jdsServer = JDSServer.getInstance();

    }

    @Override
    public ReturnType disconnect() throws JDSException {
        checkLogin();
        jdsServer.disconnect(client.getSessionHandle());
        connInfo = null;
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    @Override
    public ConnectInfo getConnectInfo() {
        return connInfo;
    }

    public OrgManager getOrgManager() {
        return orgManager;
    }

    public void setOrgManager(final OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    @Override
    public ConfigCode getConfigCode() {
        return configCode;
    }

    @Override
    public JDSSessionHandle getSessionHandle() {
        return client.getSessionHandle();
    }

    @Override
    public Device activateDevice(final String deviceId) throws HomeException {
        final Device device = getDeviceById(deviceId);

        device.setStates(DeviceStatus.ONLINE);

        updateDevice(device);

        fireDeviceEvent(device, DeviceEventEnums.deviceActivt);

        return device;
    }

    @Override
    public void addAlarm(final String epId, final DeviceDataTypeKey valuetype, final String value, final String time) throws HomeException {

        Long eventtime = System.currentTimeMillis();

        // 部分传感器上报没有时间值
        if (time != null) {
            if (DateUtility.getDate(time) == null) {
                eventtime = Long.valueOf(time);
            } else {
                eventtime = DateUtility.getDate(time).getTime();
            }
        }

        final DeviceEndPoint endPoint = getEndPointById(epId);

        sensorOnLine(endPoint.getDevice().getDeviceid(), false);


        endPoint.updateCurrvalue(valuetype, value);
        endPoint.updateCurrvalue(DeviceDataTypeKey.Time, eventtime.toString());


        // 离线告警
        if ((valuetype.equals(DeviceDataTypeKey.Status)) && (value.equals(DeviceStatus.OFFLINE.toString()))) {
            sensorOffLine(epId);
            // 故障报送
        } else if ((valuetype.equals(DeviceDataTypeKey.Trouble)) && (value.equals(DeviceDataTypeKey.Trouble))) {
            endPoint.updateCurrvalue(DeviceDataTypeKey.Zone_Status, DeviceZoneStatus.DEVICETROUBLE.getCode().toString());
            // 水紧急报警
        } else if ((valuetype.equals(DeviceDataTypeKey.Emergency)) && (value.equals(DeviceDataTypeKey.Emergency))) {
            endPoint.updateCurrvalue(DeviceDataTypeKey.Zone_Status, DeviceZoneStatus.EMERGENCY.getCode().toString());
            // 火紧急报警
        } else if ((valuetype.equals(DeviceDataTypeKey.Fire) && (value.equals(DeviceDataTypeKey.Fire)))) {
            endPoint.updateCurrvalue(DeviceDataTypeKey.Zone_Status, DeviceZoneStatus.FIRE.getCode().toString());
            // 防盗报警
        } else if ((valuetype.equals(DeviceDataTypeKey.Burglar) && (value.equals(DeviceDataTypeKey.Burglar)))) {
            endPoint.updateCurrvalue(DeviceDataTypeKey.Zone_Status, DeviceZoneStatus.BURGLAR.getCode().toString());
        } else if ((valuetype.equals(DeviceDataTypeKey.lowbattery)) || (value.equals(DeviceDataTypeKey.battery))) {
            endPoint.updateCurrvalue(DeviceDataTypeKey.Zone_Status, DeviceZoneStatus.DEVICETROUBLE.getCode().toString());
            endPoint.getDevice().setBattery(value);
            this.updateDevice(endPoint.getDevice());
        } else {
            endPoint.updateCurrvalue(DeviceDataTypeKey.Zone_Status, DeviceZoneStatus.ON.getCode().toString());
        }
        //    updateEndPoint(endPoint);

        msgEngine.addAlarm(endPoint, endPoint.getCurrvalue());
        final DataIndex dataIndex = new DataIndex(endPoint.getEndPointId());
        dataIndex.setUserId(getConnectInfo().getUserID());
        dataIndex.setValue(value);
        dataIndex.setGwSN(endPoint.getDevice().getRootDevice().getSerialno());
        dataIndex.setEventtime(eventtime);
        dataIndex.setValuetype(valuetype.getType());
        dataIndex.setEvent(DataEventEnums.AlarmReport.getMethod());
        dataIndex.setSn(endPoint.getIeeeaddress());
        endPoint.updateCurrvalue(DeviceDataTypeKey.Time, eventtime.toString());

        fireDataEvent(dataIndex, DataEventEnums.AlarmReport);

    }

    @Override
    public void addData(final String epId, DeviceDataTypeKey valuetype, String value, final String time) throws HomeException {

        Long eventtime = System.currentTimeMillis();
        final DeviceEndPoint endPoint = getEndPointById(epId);
        final Device device = endPoint.getDevice();
        final Map eventMap = new HashMap();

        // 部分传感器上报没有时间值
        if (time != null) {
            if (DateUtility.getDate(time) == null) {
                eventtime = Long.valueOf(time);
            } else {
                eventtime = DateUtility.getDate(time).getTime();
            }

        }


        eventMap.put("valuetype", valuetype);
        eventMap.put("value", value);
        eventMap.put("eventtime", eventtime);
        eventMap.put("currValue", endPoint.getCurrvalue());

        final DataIndex dataIndex = new DataIndex(endPoint.getEndPointId());
        dataIndex.setMsgId(UUID.randomUUID().toString());
        dataIndex.setUserId(getConnectInfo().getUserID());
        dataIndex.setValue(value);
        dataIndex.setEventtime(eventtime);
        dataIndex.setValuetype(valuetype.getType());
        dataIndex.setEvent(DataEventEnums.DataReport.getMethod());
        dataIndex.setGwSN(endPoint.getDevice().getRootDevice().getSerialno());
        dataIndex.setSn(endPoint.getIeeeaddress());
        endPoint.updateCurrvalue(DeviceDataTypeKey.Time, eventtime.toString());


        if ((valuetype.equals(DeviceDataTypeKey.Zone_Status)) && (value.equals(DeviceStatus.OFFLINE.getCode()))) {
            sensorOffLine(endPoint.getDevice().getDeviceid());
        } else {
            sensorOnLine(endPoint.getDevice().getDeviceid(), false);
            endPoint.updateCurrvalue(valuetype, value);
            if ((value.equals(DeviceDataTypeKey.battery))) {
                endPoint.getDevice().setBattery(value);
                this.updateDevice(endPoint.getDevice());
            } else {
                endPoint.updateCurrvalue(DeviceDataTypeKey.Zone_Status, DeviceZoneStatus.OFF.getCode().toString());
                // 解除紧急报警
                endPoint.updateCurrvalue(DeviceDataTypeKey.Emergency, null);
                endPoint.updateCurrvalue(DeviceDataTypeKey.Burglar, null);
                endPoint.updateCurrvalue(DeviceDataTypeKey.Fire, null);
            }
            msgEngine.addData(endPoint, eventMap);
            fireDataEvent(dataIndex, DataEventEnums.DataReport);
        }
    }

    @Override
    public void updateDevice(final Device device) throws HomeException {
        msgEngine.updateDevice(device);
        cacheManager.updateDevice(device);
    }

    public void updateDevice(final Device device, final Boolean indb) throws HomeException {
        msgEngine.updateDevice(device);
        cacheManager.updateDevice(device, indb);
    }


    @Override
    public DeviceEndPoint registerEndPonit(final String sensorieee, final EndPoint ep) throws JDSException {
        final DeviceEndPoint endpoint = cacheManager
                .registerEndPoint(sensorieee, ep.getIeee(), ep.getEp(), ep.getSensorType(), ep.getName());
        endpoint.setNwkAddress(ep.getNwkAddress());
        endpoint.setProfileid(ep.getProfileid());
        endpoint.setHadeviceid(ep.getDeviceid());
        endpoint.setSensortype(cacheManager.getSensorTypesByNo(ep.getSensorType()));
        endpoint.setEp(ep.getEp());


        if (endpoint.getDevice().getBattery() != null && endpoint.getCurrvalue().get(DeviceDataTypeKey.battery) != null && !endpoint.getDevice().getBattery().equals(endpoint.getCurrvalue().get(DeviceDataTypeKey.battery))) {
            this.addData(endpoint.getEndPointId(), DeviceDataTypeKey.battery, endpoint.getDevice().getBattery(), Long.valueOf(System.currentTimeMillis()).toString());
        }


        // endpoint.setName(ep.getName());
        final List<HAAttribute> haAttributes = ep.getAttributes();
        // 存储属性
        if (haAttributes != null && haAttributes.size() > 0) {
            for (final HAAttribute haAttribute : haAttributes) {
                if (haAttribute != null && DeviceDataTypeKey.fromType(haAttribute.getAttributename()) != null) {
                    endpoint.updateCurrvalue(DeviceDataTypeKey.fromType(haAttribute.getAttributename()), haAttribute.getValue());
                }
            }
        }

        Device gatewaydevice = null;
        try {
            gatewaydevice = getDeviceById(getConnectInfo().getLoginName());
        } catch (final HomeException e) {
            e.printStackTrace();
            throw new HomeException("账户信息错误，请尝试重新登录！", HomeException.NOTLOGINEDERROR);
        }

        // 删除其他错误传感器数据
        final List<ZNode> znodes = endpoint.getAllZNodes();
        for (final ZNode znode : znodes) {
            ZNode gatewayNode = null;
            try {
                gatewayNode = cacheManager.getZNodeById(znode.getParentid());
            } catch (final HomeException e) {
                e.printStackTrace();
            }

            if (gatewayNode != null) {
                final Device zgatewaydevice = getDeviceById(gatewayNode.getDeviceid());
                // 未知类型处理
                if (znode.getSensortype().equals(100)) {
                    znode.setSensortype(Integer.valueOf(endpoint.getSensortype().getType().intValue()));
                    znode.setName(endpoint.getSensortype().getName());
                    updateZNode(znode, true);
                }
                if (!zgatewaydevice.getBindingaccount().equals(endpoint.getDevice().getBindingaccount())) {
                    removeSensor(znode.getZnodeid());
                }
            } else {
                removeSensor(znode.getZnodeid());
            }

        }

        // 完成节点添加
        final List<ZNode> gatewayznodes = gatewaydevice.getDeviceEndPoints().get(0).getAllZNodes();

        for (final ZNode gateway : gatewayznodes) {

            if (!gateway.getStatus().equals(DeviceStatus.DELETE)) {
                ZNode currznode = cacheManager.findZNodeByEndPointId(endpoint.getEndPointId(), gateway.getCreateuiserid());
                if (currznode == null) {
                    final ZNode znode = cacheManager.createChildZNode(gateway.getZnodeid(), endpoint.getEndPointId());
                } else {
                    final ZNode znode = cacheManager.getZNodeById(currznode.getZnodeid());
                    znode.setStatus(DeviceStatus.ONLINE);

                }
            }
        }


        // fireDeviceEvent(endpoint.getDevice(), DeviceEventEnums.register);
        updateEndPoint(endpoint);
        return endpoint;
    }

    @Override
    public Device registerSensor(final String serialno, final Integer type, final String factoryName) throws HomeException {
        if (serialno == null) {
            throw new HomeException("serialno 不能为空！", HomeException.SENSORIDINVALID);
        }
        Device gatewaydevice = null;
        synchronized (serialno) {
            Device device = null;
            try {
                gatewaydevice = getDeviceById(getConnectInfo().getLoginName());

                try {
                    device = cacheManager.getDeviceByIeee(serialno);
                } catch (JDSException e) {
                    // e.printStackTrace();
                }
                logger.info("gwclient registerSensor device= " + device == null ? "null" : JSONObject.toJSONString(device) + "");
                //这段逻辑有问题，需要慎重处理
                if (device == null || device.getStates().equals(DeviceStatus.DELETE) || (type != null && type != 100 && !device.getDevicetype().equals(type))) {
                    device = cacheManager.registerDevice(serialno, gatewaydevice.getDeviceid(), type, gatewaydevice.getAppaccount(), factoryName);
                    msgEngine.registerSensor(device);
                    fireDeviceEvent(device, DeviceEventEnums.register);
                }
                sensorOnLine(device.getDeviceid(), false);


            } catch (final HomeException e) {
                logger.info("HomeException : " + JSONObject.toJSONString(e));
                throw new HomeException("账户信息错误，请尝试重新登录！", HomeException.NOTLOGINEDERROR);
            }

            return device;

        }

    }

    @Override
    public void syncSensor(final List<Device> devices) throws HomeException {

        final Device gatewaydevice = getDeviceById(getConnectInfo().getLoginName());
        // 清除设备列表缓存
        // MEMManagerImpl.getInstance().getAllChildIdCache().remove(gatewaydevice.getDeviceid());
        Person person = null;
        try {
            person = OrgManagerFactory.getOrgManager().getPersonByAccount(gatewaydevice.getBindingaccount());

        } catch (final PersonNotFoundException e) {
            e.printStackTrace();
            try {
                person = OrgManagerFactory.getOrgManager().getPersonByAccount(gatewaydevice.getAppaccount());
            } catch (final PersonNotFoundException e1) {
                return;
            }
        }

        ZNode gateway = cacheManager.findZNodeByDeviceId(gatewaydevice.getDeviceid(), person.getID());

        cacheManager.registerGateway(gatewaydevice.getDeviceid(), gatewaydevice.getSerialno(), gatewaydevice.getMacaddress(), gatewaydevice.getFactory(), gatewaydevice.getBatch());

        if (gateway == null) {
            gateway = cacheManager.createGateway(gatewaydevice.getSerialno(), gatewaydevice.getFactory());
        }

        final List<Device> dbdeviceList = gatewaydevice.getChildDevices();

        for (final Device device : dbdeviceList) {
            if (device.getFactory() == null) {
                removeDevice(device.getDeviceid());
            } else if (!devices.contains(device) && device.getFactory().equals(gatewaydevice.getFactory()) && !device.getStates().equals(DeviceStatus.DELETE)) {
                removeDevice(device.getDeviceid());
            }
        }

        for (final Device device : devices)
            try {
                if (device != null) {
                    //新设备
                    if (!dbdeviceList.contains(device)) {
                        device.setBindingaccount(gatewaydevice.getBindingaccount());
                        updateDevice(device);
                        final DeviceStatus states = device.getStates();
                        if (!states.equals(DeviceStatus.ONLINE)) {
                            sensorOnLine(device.getDeviceid(), false);
                        }

                    } else if (device.getStates().equals(DeviceStatus.OFFLINE)) {
                        sensorOffLine(device.getDeviceid());
                    } else {
                        sensorOnLine(device.getDeviceid(), false);
                    }
                }
            } catch (final HomeException e) {
                e.printStackTrace();
            }

    }

    private void removeDevice(final String deviceId) throws HomeException {
        Device device = getDeviceById(deviceId);
        if (device == null) {
            device = getDeviceByIeee(deviceId);
        }

        cacheManager.removeChildDevice(device.getBindingaccount(), device.getDeviceid());
        List<ZNode> znodes = device.getAllZNodes();
        device.setStates(DeviceStatus.DELETE);
        final List<DeviceEndPoint> endPoints = device.getDeviceEndPoints();
        for (final DeviceEndPoint endPoint : endPoints) {
            endPoint.updateCurrvalue(DeviceDataTypeKey.Status, DeviceStatus.DELETE.toString());
            updateEndPoint(endPoint);
        }

        for (final ZNode znode : znodes) {
            if (znode.getParentid() != null) {
                ZNode currgateway = null;
                try {
                    currgateway = cacheManager.getZNodeById(znode.getParentid());
                } catch (JDSException e) {
                    e.printStackTrace();
                }
                // 同一网关下的设备则逻辑删除 否则物理删除
                if (currgateway != null && znode.getEndPoint() != null && znode.getEndPoint().getDevice().getBindingaccount().equals(currgateway.getDeviceid())) {
                    znode.setStatus(DeviceStatus.DELETE);
//                    if (znode.getParentNode().getStatus().equals(DeviceStatus.ONLINE)) {
//                        // dataEngine.sensorOffLine(znode.getEndPoint().getDevice());
//                        msgEngine.sensorOffLine(znode.getEndPoint().getDevice());
//                    }
                    cacheManager.updateNodeStatus(znode.getZnodeid(), DeviceStatus.DELETE);
                } else {
                    cacheManager.deleteZNode(znode.getZnodeid());
                }
            } else {
                cacheManager.deleteZNode(znode.getZnodeid());
            }

        }
        updateDevice(device);
        fireDeviceEvent(device, DeviceEventEnums.deleteing);
    }

    private void updateZNode(final ZNode znode, final boolean bindb) throws HomeException {
        cacheManager.updateZNode(znode, bindb);
    }

    @Override
    public void gatewayOffLine(final String deviceId) throws HomeException {
        final List<ZNode> gatewayZNodes = this.getDeviceById(deviceId).getAllZNodes();
        fireDeviceEvent(getDeviceById(deviceId), DeviceEventEnums.offLine);
        for (final ZNode gatewayNode : gatewayZNodes) {
            if (gatewayNode.getSensortype().equals(0)) {
                fireGatewayEvent(gatewayNode, GatewayEventEnums.GATEWAYOFFINE);
                gatewayNode.setStatus(DeviceStatus.OFFLINE);
                updateZNode(gatewayNode, false);
            }

        }

    }

    @Override
    public void gatewayOnLine(final String deviceId) throws HomeException {
        final List<ZNode> gatewayznodes = this.getDeviceById(deviceId).getAllZNodes();
        fireDeviceEvent(getDeviceById(deviceId), DeviceEventEnums.onLine);
        for (final ZNode gateway : gatewayznodes) {
            logger.info(JSONObject.toJSON(gateway));
            if (gateway.getSensortype().equals(0)) {
                fireGatewayEvent(gateway, GatewayEventEnums.GATEWAYONLINE);
                gateway.setStatus(DeviceStatus.ONLINE);
                final List<ZNode> childznodes = gateway.getChildNodeList();
                for (final ZNode zchildnode : childznodes) {
                    zchildnode.setStatus(DeviceStatus.ONLINE);
                }
                updateZNode(gateway, false);
            }
        }

    }

    @Override
    public void sensorOffLine(final String deviceId) throws HomeException {

        Device device = getDeviceById(deviceId);
        if (device == null) {
            device = getDeviceByIeee(deviceId);
        }
        if (!device.getStates().equals(DeviceStatus.OFFLINE)) {
            device.setStates(DeviceStatus.OFFLINE);
            final List<DeviceEndPoint> endPoints = device.getDeviceEndPoints();
            for (final DeviceEndPoint endPoint : endPoints) {

                endPoint.updateCurrvalue(DeviceDataTypeKey.Status, DeviceStatus.OFFLINE.getCode().toString());
                cacheManager.updateEndPoint(endPoint, false);
            }

            final List<ZNode> znodes = device.getAllZNodes();

            for (final ZNode znode : znodes) {
                if (!znode.getStatus().equals(DeviceStatus.OFFLINE)) {
                    znode.setStatus(DeviceStatus.OFFLINE);
                }

            }
            // dataEngine.sensorOffLine(device);
            msgEngine.sensorOffLine(device);
            updateDevice(device);
            fireDeviceEvent(device, DeviceEventEnums.offLine);
        }

    }

    @Override
    public void sensorOnLine(final String deviceId) throws HomeException {
        sensorOnLine(deviceId, true);
    }

    public void sensorOnLine(final String deviceId, final boolean sendMsg) throws HomeException {
        // if (!device.getStates().equals(DeviceStatus.ONLINE)) {

        Device device = this.getDeviceById(deviceId);
        final List<DeviceEndPoint> endPoints = device.getDeviceEndPoints();
        for (final DeviceEndPoint endPoint : endPoints) {


            // 修正故障报警信息新
            if (endPoint.getCurrvalue().containsKey(DeviceDataTypeKey.Trouble)) {
                endPoint.updateCurrvalue(DeviceDataTypeKey.Trouble, null);
                endPoint.updateCurrvalue(DeviceDataTypeKey.Zone_Status, DeviceZoneStatus.OFF.getCode().toString());
            }
//
//            // 修正紧急报警状态
//            if (endPoint.getCurrvalue().containsKey(DeviceDataTypeKey.Emergency)) {
//                endPoint.updateCurrvalue(DeviceDataTypeKey.Emergency, null);
//                endPoint.updateCurrvalue(DeviceDataTypeKey.Zone_Status, DeviceZoneStatus.OFF.getCode().toString());
//            }
//
//            // 修正防盗报警状态
//            if (endPoint.getCurrvalue().containsKey(DeviceDataTypeKey.Burglar)) {
//                endPoint.updateCurrvalue(DeviceDataTypeKey.Burglar, null);
//                endPoint.updateCurrvalue(DeviceDataTypeKey.Zone_Status, DeviceZoneStatus.OFF.getCode().toString());
//            }

            endPoint.updateCurrvalue(DeviceDataTypeKey.Status, DeviceStatus.ONLINE.getCode().toString());
            cacheManager.updateEndPoint(endPoint, false);
        }

        if (sendMsg) {
            msgEngine.sensorOnLine(device);
            //if (!device.getStates().equals(DeviceStatus.ONLINE)) {
            device.setStates(DeviceStatus.ONLINE);
            updateDevice(device, false);
            fireDeviceEvent(device, DeviceEventEnums.onLine);
            //}
        }


    }

    @Override
    public Device getDeviceById(final String deviceId) throws HomeException {
        Device device = null;
        try {
            device = cacheManager.getDeviceById(deviceId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return device;
    }


    public void checkLogin() throws HomeException {
        // if (getConnectInfo() == null)
        // throw new HomeException("用户未登录", HomeException.NOTLOGIN);
    }

    @Override
    public void setClientService(final JDSClientService client) {
        this.client = client;
    }

    private void fireDataEvent(final DataIndex data, final DataEventEnums eventID) throws HomeException {
        fireDataEvent(data, eventID, null);
    }

    private void fireDataEvent(final DataIndex data, final DataEventEnums eventID, Map eventContext) throws HomeException {
        eventContext = fillInUserID(eventContext);
        final DataEvent event = new DataEvent(data, client, eventID, null);
        event.setContextMap(eventContext);
        try {
            HomeEventControl.getInstance().dispatchEvent(event);
        } catch (final JDSException e) {
            throw new HomeException(e);
        }
    }


    private void fireDeviceEndPointEvent(final DeviceEndPoint endPoint, final DeviceEndPointEventEnums eventID, Map eventContext) throws HomeException {
        eventContext = fillInUserID(eventContext);
        final DeviceEndPointEvent event = new DeviceEndPointEvent(endPoint, client, eventID, null);

        event.setContextMap(eventContext);
        new Thread() {
            @Override
            public void run() {
                try {
                    HomeEventControl.getInstance().dispatchEvent(event);
                } catch (JDSException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    private void fireDeviceEndPointEvent(final DeviceEndPoint endPoint, final DeviceEndPointEventEnums eventID) throws HomeException {
        if (endPoint != null) {
            fireDeviceEndPointEvent(endPoint, eventID, null);
        }

    }

    private void fireDeviceEvent(final Device device, final DeviceEventEnums eventID) throws HomeException {
        if (device != null) {
            fireDeviceEvent(device, eventID, null);
        }

    }

    private void fireDeviceEvent(final Device device, final DeviceEventEnums eventID, Map eventContext) throws HomeException {
        eventContext = fillInUserID(eventContext);
        final DeviceEvent event = new DeviceEvent(device, client, eventID, null);
        event.setContextMap(eventContext);
        new Thread() {
            @Override
            public void run() {
                try {
                    HomeEventControl.getInstance().dispatchEvent(event);
                } catch (JDSException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

//    private void fireZNodeEvent(final ZNode znode, final ZnodeEventEnums eventID) throws HomeException {
//        fireZNodeEvent(znode, eventID, null);
//    }
//
//    private void fireZNodeEvent(final ZNode znode, final ZnodeEventEnums eventID, Map eventContext) throws HomeException {
//        eventContext = fillInUserID(eventContext);
//        final ZNodeEvent event = new ZNodeEvent(znode, client, eventID, getSystemCode());
//        event.setContextMap(eventContext);
//
//        try {
//            HomeEventControl.getInstance().dispatchEvent(event);
//        } catch (final JDSException e) {
//            throw new HomeException(e);
//        }
//    }

    private void fireGatewayEvent(final ZNode znode, final GatewayEventEnums eventID) throws HomeException {
        fireGatewayEvent(znode, eventID, null);
    }

    private void fireGatewayEvent(final ZNode znode, final GatewayEventEnums eventID, Map eventContext) throws HomeException {
        eventContext = fillInUserID(eventContext);
        final GatewayEvent event = new GatewayEvent(znode, client, eventID, null);
        event.setContextMap(eventContext);
        try {
            HomeEventControl.getInstance().dispatchEvent(event);
        } catch (final JDSException e) {
            throw new HomeException(e);
        }
    }

    @Override
    public ConnectionHandle getConnectionHandle() {
        return client.getConnectionHandle();
    }

    @Override
    public JDSContext getContext() {
        return client.getContext();
    }

    @Override
    public void setConnectionHandle(final ConnectionHandle handle) {
        client.setConnectionHandle(handle);
    }

    @Override
    public void setContext(final JDSContext context) {
        client.setContext(context);
    }

    @Override
    public String getSystemCode() {
        return JDSActionContext.getActionContext().getSystemCode();
    }

    @Override
    public void bindSuccess(final String sensorieee) throws HomeException {
        Map eventContext = new HashMap();
        eventContext = fillInUserID(eventContext);

        DeviceEndPoint endPoint = null;
        try {
            endPoint = this.getEndPointByIeee(sensorieee);
        } catch (final HomeException e) {
            e.printStackTrace();
        }

        final List<ZNode> znodes = endPoint.getAllZNodes();
        for (final ZNode znode : znodes) {
            if (znode.getEndPoint().getSensortype().equals(5)) {
                final CommandClient commandClient = msgEngine.getCommandClientByieee(znode.getParentNode().getEndPoint().getDevice().getSerialno());
                commandClient.sendIdentifyDeviceCommand(znode.getDeviceid(), 0);
            }

        }
        fireDeviceEndPointEvent(endPoint, DeviceEndPointEventEnums.bindSuccess, eventContext);
    }

    @Override
    @Autowired(required = false)
    public void unbindSuccess(final String sensorieee) throws HomeException {
        Map eventContext = new HashMap();
        eventContext = fillInUserID(eventContext);
        final DeviceEndPoint endPoint = getEndPointByIeee(sensorieee);
        if (endPoint.getSensortype().equals(5)) {
            final CommandClient commandClient = msgEngine.getCommandClientByieee(endPoint.getDevice().getRootDevice().getSerialno());
            commandClient.sendIdentifyDeviceCommand(endPoint.getDevice().getDeviceid(), 0);
        }

        //fireDeviceEvent(endPoint.getDevice(), DeviceEventEnums.UNBINDSUCCESS, eventContext);

    }

    @Override
    public void bind(final String sensorieee, final Integer type) throws HomeException {

        final Device gwdevice = getDeviceByIeee(getConnectInfo().getLoginName());

        final CommandClient commandClient = msgEngine.getCommandClientByieee(gwdevice.getSerialno());

        commandClient.sendAddSensorCommand(sensorieee, type);
    }

    @Override
    public void unbind(final String sensorieee, final Integer type) throws HomeException {

        final Device gwdevice = getDeviceByIeee(getConnectInfo().getLoginName());

        final CommandClient commandClient = msgEngine.getCommandClientByieee(gwdevice.getSerialno());

        commandClient.sendRemoveSensorCommand(sensorieee, type);
    }

    @Override
    public void changeGatewayNetwork(final NetworkInfo networkInfo) throws HomeException {
        final String deviceId = getConnectInfo().getUserID();
        final Device device = getDeviceById(deviceId);
        final List<DeviceEndPoint> endPoints = device.getDeviceEndPoints();
        final DeviceEndPoint ep = (DeviceEndPoint) endPoints.get(0);
        ep.updateCurrvalue(DeviceDataTypeKey.NetworkInfo, JSONArray.toJSON(networkInfo).toString());
        final CommandClient commandClient = msgEngine.getCommandClientByieee(device.getSerialno());
        commandClient.sendChannelNegotiateCommand(networkInfo);
        updateDevice(device);

    }

    @Override
    public void removeSensor(final String sensorId) throws HomeException {
        ZNode znode = null;
        try {
            znode = cacheManager.getZNodeById(sensorId);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        msgEngine.deleteNode(znode);
        cacheManager.deleteZNode(sensorId);
        //fireZNodeEvent(znode, ZnodeEventEnums.znodeMoved);
    }

    @Override
    public NetworkInfo getGatewayNetwork() throws HomeException {
        final String deviceId = getConnectInfo().getUserID();
        final Device device = getDeviceById(deviceId);
        final List<DeviceEndPoint> endPoints = device.getDeviceEndPoints();
        final DeviceEndPoint ep = endPoints.get(0);
        final String json = (String) ep.getCurrvalue().get(HomeConstants.GATEWAYNETWORKINFO);
        NetworkInfo info = new NetworkInfo();
        if (json != null) {

            info = (NetworkInfo) JSONObject.parseObject(json, NetworkInfo.class);
        }
        return info;
    }

    @Override
    public void commandReport(final String commandId, final Integer status, final String modeId, final CommandEventEnums code) throws HomeException {
        CommandMsg msg = null;
        Map eventContext = new HashMap();
        eventContext = fillInUserID(eventContext);
        MsgClient<CommandMsg> client = MsgFactroy.getInstance().getClient(null, CommandMsg.class);
        if (commandId == null) {
            throw new HomeException("命令不不存在！");
        }
        msg = client.getMsgById(commandId);
        if (msg == null) {
            throw new HomeException("命令不不存在！");
        }

        if (code.equals(CommandEventEnums.COMMANDSENDING)) {
            if (!msg.getResultCode().equals(CommandEventEnums.COMMANDINIT) && !msg.getResultCode().equals(CommandEventEnums.COMMANDROUTING)) {
                throw new HomeException("指令顺序错误！");
            }
        }

        if (!msg.getBody().equals("")) {
            final JSONObject jsonobj = JSONObject.parseObject(msg.getBody());
            final Command command = (Command) JSONObject.parseObject(msg.getBody(), CommandEnums.fromByName(jsonobj.getString("command")).getCommand());

            Device gatewaydevice = getDeviceById(getConnectInfo().getLoginName());
            if (gatewaydevice != null) {
                String gwIeee = command.getGatewayieee();
                if (gwIeee != null && !gwIeee.equals(gatewaydevice.getSerialno())) {
                    HomeServer.getAppEngine().logging(this.getConnectInfo().getLoginName(), msg.getBody(), "网关命令与发送不对应！", code.getMethod(), gwIeee);

                    throw new HomeException("网关命令与发送不对应！");
                }
            } else {
                HomeServer.getAppEngine().logging(this.getConnectInfo().getLoginName(), msg.getBody(), "网关账号错误！", code.getMethod(), getConnectInfo().getLoginName());
                throw new HomeException("网关账号错误！");
            }


            command.setCommandId(commandId);
            command.setResultCode(code);

            String gwieee = command.getGatewayieee();


            String ieee = null;

            if ((status != null) && code.equals(CommandEventEnums.COMMANDSENDING) && code.equals(CommandEventEnums.COMMANDSENDWAITE) && code.equals(CommandEventEnums.COMMANDTIMEOUT) && code.equals(CommandEventEnums.COMMANDLINKFAIL)) {
                msg.setStatus(MsgStatus.ERROR);
            } else {
                msg.setStatus(MsgStatus.READED);
            }

            if ((command != null) && command instanceof SensorCommand) {
                final SensorCommand sensorCommand = (SensorCommand) command;
                ieee = sensorCommand.getSensorieee();
            }

            HomeServer.getAppEngine().logging(this.getConnectInfo().getLoginName(), msg.getBody(), command.getCommand().getName() + "[" + code.getName() + "]", code.getMethod(), ieee);

            eventContext.put(GatewayEvent.CONTEXT_COMMAND, command);
            msg.setBody(JSONObject.toJSONString(command));
            msg.setResultCode(code);

            msg.setEventTime(System.currentTimeMillis());
            // 网关返回码
            fireCommandEvent(command, code, eventContext);

            if (code.equals(CommandEventEnums.COMMANDSENDING) || code.equals(CommandEventEnums.COMMANDSENDWAITE)) {
                msg.setTitle(code.getName());
                msg.setResultCode(code);
                msg.setStatus(MsgStatus.READED);
                msg.setEventTime(System.currentTimeMillis());
                msg.setReceiveTime(System.currentTimeMillis());
                client.updateMsg(msg);

            } else {
                msg.setEventTime(System.currentTimeMillis());
                msg.setTitle(code.getName());
                msg.setResultCode(code);
                msg.setStatus(MsgStatus.READED);
                msg.setReceiveTime(System.currentTimeMillis());
                client.updateMsg(msg);
                MsgClient<LogMsg> logclient = MsgFactroy.getInstance().getClient(null, LogMsg.class);
                LogMsg cloneMsg = logclient.cloneMsg(msg);
                cloneMsg.setType(MsgType.LOG.getType());
                cloneMsg.setEventTime(System.currentTimeMillis());
                logclient.updateMsg(cloneMsg);

            }

        }


    }


    private void fireCommandEvent(final Command command, final CommandEventEnums eventID, Map eventContext) throws HomeException {
        try {
            eventContext = fillInUserID(eventContext);
            final CommandEvent event = new CommandEvent(command, client, eventID, null);
            event.setContextMap(eventContext);
            HomeEventControl.getInstance().dispatchEvent(event);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setConnInfo(final ConnectInfo connInfo) {
        this.connInfo = connInfo;
    }

    private Map fillInUserID(final Map ctx) {
        Map result = ctx;
        if (result == null) {
            result = new HashMap();
        }

        if (getConnectInfo() != null) {
            result.put(USERID, getConnectInfo().getUserID());
        }

        return result;
    }

    @Override
    public JDSClientService getJDSClient() {
        return client;
    }

    @Override
    public Boolean isOnLine() throws JDSException {
        if (connInfo != null) {
            final Set<JDSSessionHandle> sessionHandleList = JDSServer.getInstance().getSessionHandleList(connInfo);
            if ((sessionHandleList == null) || (sessionHandleList.size() == 0)) {
                return true;
            }
        }
        return false;

    }

    @Override
    public Device getDeviceByIeee(final String serialno) throws HomeException {

        try {
            return cacheManager.getDeviceByIeee(serialno);
        } catch (JDSException e) {
            throw new HomeException(e);
        }
    }

    @Override
    public DeviceEndPoint getEndPointById(final String epId) throws HomeException {
        try {
            return cacheManager.getEndPointById(epId);
        } catch (JDSException e) {
            throw new HomeException(e);
        }
    }

    @Override
    public DeviceEndPoint getEndPointByIeee(final String serialno) throws HomeException {

        try {
            return cacheManager.getEndPointByIeee(serialno);
        } catch (JDSException e) {
            throw new HomeException(e);
        }
    }

    // --------------------------------------------- 事务控制方法

    @Override
    public void beginTransaction() throws HomeException {
        checkLogin();
        // this.tx = HibernateSessionFactory.getSession().getTransaction();
        // if (!tx.isActive()) {
        // tx.begin();
        // }
        // try {
        //
        // DbManager.getInstance().beginTransaction();
        // } catch (SQLException sqle) {
        // throw new HomeException(
        // "Failed to beging transaction of client service.", sqle,
        // HomeException.TRANSACTIONBEGINERROR);
        // }
    }

    @Override
    public void commitTransaction() throws HomeException {
        checkLogin();

    }

    @Override
    public void rollbackTransaction() throws HomeException {
        checkLogin();

    }

    @Override
    public void updateEndPoint(DeviceEndPoint endPoint) throws HomeException {
        cacheManager.updateEndPoint(endPoint);

    }


}
