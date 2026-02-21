package net.ooder.agent.client.home.ct;


import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.command.AddSensorCommand;
import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.command.task.TestTimesCommand;
import net.ooder.agent.client.enums.CommandEnums;
import net.ooder.agent.client.iot.*;
import net.ooder.agent.client.iot.ct.CtIotCacheManager;
import net.ooder.agent.client.iot.ct.CtIotFactory;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;
import net.ooder.agent.client.iot.enums.DeviceStatus;
import net.ooder.agent.client.iot.enums.ZNodeZType;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.annotation.JLuceneIndex;
import  net.ooder.client.JDSSessionFactory;
import  net.ooder.common.*;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.config.ErrorListResultModel;
import  net.ooder.config.ListResultModel;
import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.engine.ConnectInfo;
import  net.ooder.engine.ConnectionHandle;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.home.client.AdminClient;
import  net.ooder.agent.client.home.query.IOTConditionKey;
import  net.ooder.jds.core.User;
import  net.ooder.org.Person;
import  net.ooder.org.PersonNotFoundException;
import  net.ooder.server.JDSServer;
import  net.ooder.server.OrgManagerFactory;
import  net.ooder.server.SubSystem;
import  net.ooder.thread.JDSThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@EsbBeanAnnotation(id = "AdminClient", name = "AdminClient", expressionArr = "CtAdminClientImpl()", desc = "AdminClient")
public class CtAdminClientImpl implements AdminClient {

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, CtAdminClientImpl.class);
    private final CtIotCacheManager appEngine;

    static ScheduledExecutorService commandService = Executors.newScheduledThreadPool(20, new JDSThreadFactory("CtAdminClientImpl.sendCommands"));


    private JDSServer jdsServer;
    private JDSSessionHandle sessionHandle;
    private ConfigCode configCode;
    private ConnectionHandle connecionHandel;
    private ConnectInfo connInfo;
    private JDSContext context;


    public CtAdminClientImpl() {
        User user = null;
        try {
            user = JDSServer.getInstance().getAdminUser();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        String sessionId =user.getSessionId();

        JDSSessionHandle sessionHandle = JDSSessionFactory.newSessionHandle(sessionId);

        this.sessionHandle = JDSSessionFactory.newSessionHandle(sessionId);
        this.configCode =user.getConfigName();

        ConnectInfo connectInfo = new ConnectInfo(user.getId(), user.getAccount(), user.getPassword());
        try {
            this.jdsServer = JDSServer.getInstance();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        this.connInfo = connInfo;
        this.appEngine = CtIotCacheManager.getInstance();


    }


    // --------------------------------------------- 登陆注销操作

    public void connect(ConnectInfo connInfo) throws JDSException {
        this.connInfo = connInfo;
        jdsServer.connect(this);

    }

    public ConnectionHandle getConnectionHandle() {
        return connecionHandel;
    }

    public ReturnType disconnect() throws JDSException {
        if (sessionHandle != null) {
            jdsServer.disconnect(sessionHandle);
        }
        // this.getConnectionHandle().disconnect();
        connInfo = null;
        sessionHandle = null;
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ConnectInfo getConnectInfo() {
        return connInfo;
    }


    @Override
    public ConfigCode getConfigCode() {
        return configCode;
    }

    public JDSSessionHandle getSessionHandle() {
        if (sessionHandle == null) {
            JDSSessionFactory factory = new JDSSessionFactory(this.getContext());
            sessionHandle = factory.getSessionHandle();

        }
        return sessionHandle;
    }

    public void setConnectionHandle(ConnectionHandle handle) {
        this.connecionHandel = handle;
    }

    public JDSContext getContext() {
        if (context == null) {
            context = JDSActionContext.getActionContext();
        }
        return context;
    }

    public void setContext(JDSContext context) {
        this.context = context;
    }

    @Override
    public String getSystemCode() {
        return JDSActionContext.getActionContext().getSystemCode();
    }


    @Override
    public Area getAreaById(String areaId) throws HomeException {
        return appEngine.getAreaById(areaId);
    }

    @Override
    public Place getPlaceById(String placeId) throws HomeException {
        return appEngine.getPlaceById(placeId);
    }

    @Override
    public ListResultModel<List<DeviceEndPoint>> findEndPoint(Condition<IOTConditionKey, JLuceneIndex> condition) throws JDSException {
        return appEngine.findEndPoint(condition);
    }

    @Override
    public ListResultModel<List<ZNode>> findZnode(Condition<IOTConditionKey, JLuceneIndex> condition) throws JDSException {
        return appEngine.findZnode(condition);
    }

    @Override
    public ListResultModel<List<Device>> findDevice(Condition<IOTConditionKey, JLuceneIndex> condition) throws JDSException {
        return appEngine.findDevice(condition);
    }


    @Override
    public Area createArea(String name, String placeId) throws HomeException {
        return appEngine.createArea(name, placeId);
    }


    @Override
    public void updateDeviceValue(String epieee, String attributeName, String value) throws HomeException {
        try {
            DeviceEndPoint endPoint = appEngine.getEndPointByIeee(epieee);
            endPoint.updateCurrvalue(DeviceDataTypeKey.fromType(attributeName), value);
            appEngine.updateEndPoint(endPoint, true);

        } catch (JDSException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteZNode(String znodeId) throws HomeException {
        appEngine.deleteZNode(znodeId);
    }

    @Override
    public void login(String deviceId) throws HomeException {
        appEngine.login(deviceId);

    }

    @Override
    public void updateEndPointName(String ieee, String name) throws HomeException {
        try {
            DeviceEndPoint endPoint = appEngine.getEndPointByIeee(ieee);
            endPoint.setName(name);
            appEngine.updateEndPoint(endPoint, true);

        } catch (JDSException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateArea(Area area) throws HomeException {
        appEngine.updateArea(area);
    }


    @Override
    public void updateGatewayStatus(String gwieee, int status) throws HomeException {
        try {
            Device device = appEngine.getDeviceByIeee(gwieee);
            device.setStates(DeviceStatus.fromCode(status));
            appEngine.updateDevice(device);
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteArea(String areaId) throws HomeException {
        appEngine.deleteArea(areaId);
    }

    @Override
    public void deleteDevice(String deviceId) throws HomeException {
        appEngine.deleteDevice(deviceId);
    }

    @Override
    public void clearDevices(List<String> deviceIds) throws HomeException {
        appEngine.clearDevices(deviceIds);
    }

    @Override
    public List<Area> getAllAreaByPlaceId(String placeId) throws HomeException {
        return appEngine.getAllAreaByPlaceId(placeId);
    }


    @Override
    public Place createPlace(String name, String parentId) throws HomeException {
        return appEngine.createPlace(name, parentId);
    }

    @Override
    public void updatePlace(Place place) throws HomeException {
        appEngine.updatePlace(place);
    }


    @Override
    public void deleteSensrotype(Integer sensorno) throws HomeException {
        try {
            appEngine.deleteSensrotype(sensorno);
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSensrotype(Sensortype sensortype) throws HomeException {
        try {
            appEngine.updateSensortype(sensortype);
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    public ZNode createGateway(String ieee, String placeId, String gatewayName, String personId) throws HomeException {

        Place place = this.getPlaceById(placeId);
        Device gwdevice = null;
        try {
            gwdevice = appEngine.getDeviceByIeee(ieee);
        } catch (JDSException e) {
            e.printStackTrace();
        }

        if (gwdevice == null) {
            throw new HomeException("无效的设备！", 6002);
        }

        if (gatewayName != null && gatewayName.equals("")) {

            gwdevice.getDeviceEndPoints().get(0).setName(gatewayName);
        }
        Person person = null;
        try {
            if (personId != null && !personId.equals("")) {
                person = OrgManagerFactory.getOrgManager().getPersonByID(personId);
                gwdevice.setAppaccount(person.getAccount());
                place.setUserid(personId);
            } else {
                person = OrgManagerFactory.getOrgManager().getPersonByAccount(gwdevice.getAppaccount());
            }
        } catch (PersonNotFoundException e) {
            e.printStackTrace();
        }

        gwdevice.setStates(DeviceStatus.ONLINE);
        gwdevice.setDevicetype(HomeConstants.DEVICE_TYPE_GATEWAY);
        gwdevice.setAppaccount(person.getAccount());
        gwdevice.setPlaceid(placeId);
        appEngine.updateDevice(gwdevice);
        ZNode znode = null;
        List<ZNode> gwZNodes = gwdevice.getAllZNodes();

        for (ZNode gwZNode : gwZNodes) {
            if (gwZNode.getZtype().equals(ZNodeZType.SHARE)) {
                this.deleteZNode(gwZNode.getZnodeid());
            } else {
                gwZNode.setCreateuiserid(person.getID());
                gwZNode.setStatus(DeviceStatus.ONLINE);
                appEngine.updateZNode(gwZNode, true);
                znode = gwZNode;
            }
        }

        return znode;
    }


    @Override
    public void deletePlace(String placeId) throws HomeException {
        appEngine.deletePlace(placeId);
    }

    @Override
    public List<Place> getAllPlace() throws HomeException {
        return appEngine.findAllPlace();
    }

    @Override
    public AddSensorCommand addDevice(String gwieee, Integer type, String serialno) throws HomeException {
        AddSensorCommand sensorCommand = null;
        try {
            sensorCommand = CtIotFactory.getCommandClient(serialno).sendAddSensorCommand(serialno, type).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return sensorCommand;

    }


    @Override
    public void bindingSensor(String sonserieee, String areId) throws HomeException {
        appEngine.bindingArea(sonserieee, areId);


    }
//
//       @Override
//    public void bindPlace(String ieee, String placeId) throws HomeException {
//           Device device= null;
//           try {
//               device = appEngine.getDeviceByIeee(ieee);
//               device.setPlaceid(placeId);
//               appEngine.updateDevice(device);
//           } catch (JDSException e) {
//              throw new HomeException(e);
//           }
//
//    }


    @Override
    public Sensortype getSensorTypesByNo(Integer typno) throws HomeException {
        return appEngine.getSensorTypesByNo(typno);
    }

    @Override
    public List<Area> getAreasByIds(List<String> ids) throws HomeException {
        List<Area> areas = new ArrayList<Area>();
        for (String id : ids) {
            Area area = appEngine.getAreaById(id);
            areas.add(area);
        }
        return areas;
    }


    @Override
    public ListResultModel<List<Device>> getAllOnLineGateway() {
        ListResultModel<List<Device>> userStatusInfo = new ListResultModel<List<Device>>();
        try {
            List<Device> devices = new ArrayList<Device>();
            List<ConnectInfo> sessionList = JDSServer.getInstance().getAllConnectInfo();
            for (ConnectInfo connectInfo : sessionList) {
                if (connectInfo == null) {
                    continue;
                }
                if (connectInfo.getLoginName().length() > 35) {
                    Device device = CtIotFactory.getCtIotService().getDeviceById(connectInfo.getLoginName());
                    devices.add(device);
                }
            }
            userStatusInfo.setData(devices);
        } catch (JDSException e) {
            userStatusInfo = new ErrorListResultModel();
            ((ErrorListResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorListResultModel) userStatusInfo).setErrdes(e.getMessage());
        }


        return userStatusInfo;

    }


    @Override
    public ListResultModel<List<Device>> getAllGatewayByFactory(String factoryName) throws JDSException {

        return appEngine.getAllDevicesByFactory(factoryName);
    }


    public Command sendCommand(String commandStr, String gatewayieee) {
        JSONObject jsonobj = JSONObject.parseObject(commandStr);
        Command command = (Command) JSONObject.parseObject(commandStr, CommandEnums.fromByName(jsonobj.getString("command")).getCommand());

        command.setGatewayieee(gatewayieee);
        Device device = null;
        try {
            device = appEngine.getDeviceByIeee(command.getGatewayieee());
            SubSystem system=  JDSServer.getClusterClient().getSystem(device.getSubsyscode());
            CtMsgDataEngine msgEngine = CtMsgDataEngine.getEngine(((SubSystem) system).getConfigname());
            Person currPerson = OrgManagerFactory.getOrgManager().getPersonByAccount(device.getBindingaccount());
            command = msgEngine.sendCommand(command, Integer.valueOf(0)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return command;
    }

    public List<Command> sendCommands(String commandStr, String gatewayieees, Integer num, Integer times) {

        String[] gatewayieeeArr = new String[]{gatewayieees};
        List<Command> msgs = new ArrayList<Command>();
        if (gatewayieees.indexOf(",") > -1) {
            gatewayieeeArr = gatewayieees.split(",");
        }
        if (num == null) {
            num = 1;
        }

        for (String gatewayieee : gatewayieeeArr) {

            if (times == null) {
                times = 1;
            }
            String systemCode = null;
            try {
                systemCode = appEngine.getDeviceByIeee(gatewayieee).getSubsyscode();
            } catch (HomeException e) {
                e.printStackTrace();
            } catch (JDSException e) {
                e.printStackTrace();
            }

            TestTimesCommand testcommand = new TestTimesCommand(commandStr, gatewayieee, systemCode, num, times);
            try {
                commandService.submit(testcommand).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }

        return msgs;
    }


}
