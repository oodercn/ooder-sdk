package net.ooder.agent.client.iot.ct;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.command.CommandFactory;
import net.ooder.agent.client.command.DebugCommand;
import net.ooder.agent.client.command.InitGatewayCommand;
import net.ooder.agent.client.enums.CommandEnums;
import net.ooder.agent.client.home.client.CommandClient;
import net.ooder.agent.client.home.ct.CtMsgDataEngine;
import net.ooder.agent.client.home.engine.HomeServer;
import net.ooder.agent.client.home.query.IOTConditionKey;
import net.ooder.agent.client.iot.*;
import net.ooder.agent.client.iot.api.inner.DeviceSearchService;
import net.ooder.agent.client.iot.api.inner.HomeService;
import net.ooder.agent.client.iot.api.inner.IotService;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;
import net.ooder.agent.client.iot.enums.DeviceStatus;
import net.ooder.agent.client.iot.enums.ZNodeZType;
import net.ooder.agent.client.iot.json.device.Gateway;
import net.ooder.agent.client.iot.json.device.GatewayErrorReport;
import  net.ooder.annotation.JLuceneIndex;
import  net.ooder.cluster.ServerNode;
import  net.ooder.common.*;
import  net.ooder.common.cache.Cache;
import  net.ooder.common.cache.CacheManagerFactory;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.common.util.StringUtility;
import  net.ooder.config.ListResultModel;
import  net.ooder.jds.core.esb.EsbUtil;
import  net.ooder.msg.*;
import  net.ooder.org.OrgManager;
import  net.ooder.org.Person;
import  net.ooder.org.PersonNotFoundException;
import  net.ooder.org.conf.OrgConstants;
import  net.ooder.server.JDSServer;
import  net.ooder.server.OrgManagerFactory;
import  net.ooder.web.RemoteConnectionManager;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class CtIotCacheManager implements Serializable {
    private static CtIotCacheManager cacheManager;

    private static final Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), CtIotCacheManager.class);


    private static final String placeCacheName = "CtPlaceCache";
    private static final String sensorTypeName = "CtSensorTypeCache";
    private static final String areaCacheName = "CtAreaCache";
    private static final String deviceCacheName = "CtDeviceCache";
    private static final String deviceIeeeCacheName = "CtDeviceIeeeCache";
    private static final String endpointCacheName = "CtEndPointCache";
    private static final String endpointIeeeCacheName = "CtEndPointIeeeCache";
    private static final String znodeCacheName = "CtZnodeCache";
    private static final String sceneCacheName = "CtSceneCache";

    private final static SerializeConfig config = new SerializeConfig();

    static {
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }


    public static CtIotCacheManager getInstance() {
        if (cacheManager == null) {
            cacheManager = new CtIotCacheManager();
        }
        return cacheManager;
    }

    /* TODO here */
    private boolean cacheEnabled = true;

    protected Cache<String, Device> deviceCache;

    protected Cache<String, String> deviceIeeeCache;
    protected Cache<String, CommandMsg> commandCache;
    protected Cache<String, Sensortype> sensorTypeCache;

    protected Cache<String, DeviceEndPoint> endpointCache;
    protected Cache<String, String> endpointIeeeCache;

    protected Cache<String, ZNode> znodeCache;
    protected Cache<String, Place> placeCache;
    protected Cache<String, Area> areaCache;
    protected Cache<String, Alarm> alarmCache;

    protected Cache<String, Scene> sceneCache;

    /**
     * Creates a new cache manager.
     */
    public CtIotCacheManager() {
        initCache();
    }

    /**
     * Initializes all caches with the correct size and expiration time.
     */
    private void initCache() {


        placeCache = CacheManagerFactory.createCache(OrgConstants.CONFIG_KEY.getType(), placeCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        sensorTypeCache = CacheManagerFactory.createCache(OrgConstants.CONFIG_KEY.getType(), sensorTypeName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        areaCache = CacheManagerFactory.createCache(OrgConstants.CONFIG_KEY.getType(), areaCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        endpointCache = CacheManagerFactory.createCache(OrgConstants.CONFIG_KEY.getType(), endpointCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        endpointIeeeCache = CacheManagerFactory.createCache(OrgConstants.CONFIG_KEY.getType(), endpointIeeeCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        deviceIeeeCache = CacheManagerFactory.createCache(OrgConstants.CONFIG_KEY.getType(), deviceIeeeCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        //全局属性
        // commandCache = CacheManagerFactory.createCache(OrgConstants.CONFIG_KEY.getType(), "Commandmsg");
        commandCache = CacheManagerFactory.createCache(JDSConstants.CONFIG_KEY, "CtCommandmsg", 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        deviceCache = CacheManagerFactory.createCache(OrgConstants.CONFIG_KEY.getType(), deviceCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        znodeCache = CacheManagerFactory.createCache(OrgConstants.CONFIG_KEY.getType(), znodeCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

        sceneCache = CacheManagerFactory.createCache(OrgConstants.CONFIG_KEY.getType(), sceneCacheName, 10 * 1024 * 1024, 1000 * 60 * 60 * 24);

    }

    public void clearCache(String deviceId, boolean hasChiled) throws JDSException {
        Device device = this.getDeviceById(deviceId);
        if (device != null) {
            List<ZNode> znodes = device.getAllZNodes();
            for (ZNode znode : znodes) {
                if (znode != null) {
                    znodeCache.remove(znode.getZnodeid());
                }
            }


            List<Device> devices = device.getChildDevices();
            for (Device cdevice : devices) {
                if (cdevice != null && !cdevice.getDeviceid().equals(device.getDeviceid())) {
                    if (hasChiled) {
                        clearCache(cdevice.getDeviceid(), false);
                    } else {
                        deviceCache.remove(cdevice.getDeviceid());
                    }
                }
            }


            List<DeviceEndPoint> eps = device.getDeviceEndPoints();
            for (DeviceEndPoint ep : eps) {
                if (ep != null) {
                    endpointCache.remove(ep.getEndPointId());
                    endpointIeeeCache.remove(ep.getIeeeaddress());
                }
            }
        }
        deviceCache.remove(deviceId);
    }

    public void clearCache(String deviceId) throws JDSException {

        clearCache(deviceId, true);

    }


    /**
     * @param endpointId
     * @return
     * @throws JDSException
     */
    public DeviceEndPoint getEndPointById(String endpointId) throws JDSException {
        DeviceEndPoint endpoint = null;

        if (!cacheEnabled) {

            endpoint = getIotService().getEndPointById(endpointId).get();
            endpoint = new CtDeviceEndPoint(endpoint);
        } else { // cache enabled
            endpoint = (DeviceEndPoint) endpointCache.get(endpointId);
            if (endpoint == null) {
                endpoint = getIotService().getEndPointById(endpointId).get();
                if (endpoint != null) {
                    endpoint = new CtDeviceEndPoint(endpoint);
                    endpointCache.put(endpointId, endpoint);
                }
            }
        }

        return endpoint;
    }

    /**
     * @param znodeId
     * @return
     * @throws JDSException
     */
    public ZNode getZNodeById(String znodeId) throws HomeException {
        ZNode znode = null;
        try {
            if (!cacheEnabled) {
                znode = getIotService().getZNodeById(znodeId).get();
                if (znode != null) {
                    znode = new CtZNode(znode);
                }

            } else { // cache enabled
                znode = (ZNode) znodeCache.get(znodeId);
                if (znode == null) {
                    znode = getIotService().getZNodeById(znodeId).get();
                    if (znode != null) {
                        znode = new CtZNode(znode);
                        znodeCache.put(znodeId, znode);
                    }

                }
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return znode;
    }

    /**
     * @param deviceId
     * @return
     * @throws JDSException
     */
    public Device getDeviceById(String deviceId) throws HomeException {
        Device device = null;

        try {

            if (!cacheEnabled) {
                device = getIotService().getDeviceById(deviceId).get();
                device = new CtDevice(device);
            } else { // cache enabled
                device = (Device) deviceCache.get(deviceId);
                if (device == null) {
                    device = getIotService().getDeviceById(deviceId).get();
                    if (device != null) {
                        device = new CtDevice(device);
                        deviceCache.put(deviceId, device);
                    }
                }
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }

        return device;
    }

    /**
     * @param areaId
     * @return
     * @throws JDSException
     */
    public Area getAreaById(String areaId) throws HomeException {
        Area area = null;

        try {
            if (!cacheEnabled) {

                area = this.getHomeService().getAreaById(areaId).get();
                area = new CtArea(area);

            } else { // cache enabled
                area = (Area) areaCache.get(areaId);
                if (area == null) {
                    area = this.getHomeService().getAreaById(areaId).get();
                    if (area != null) {
                        area = new CtArea(area);
                        areaCache.put(areaId, area);
                    }

                }
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return area;
    }

    /**
     * @param placeId
     * @return
     * @throws JDSException
     */
    public Place getPlaceById(String placeId) throws HomeException {
        Place place = null;
        try {
            if (!cacheEnabled) {
                place = this.getHomeService().getPlaceById(placeId).get();
                place = new CtPlace(place);
            } else { // cache enabled
                place = (Place) placeCache.get(placeId);
                if (place == null) {
                    place = this.getHomeService().getPlaceById(placeId).get();
                    if (place != null) {
                        place = new CtPlace(place);

                        placeCache.put(placeId, place);
                    }


                }
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return place;
    }


    /**
     * @return Returns the cacheEnabled.
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }


    public List<Sensortype> getAllSensorType() throws JDSException {
        List<Sensortype> sensorTypes = new ArrayList<Sensortype>();

        List<Sensortype> rsensorTypes = getIotService().getSensorsType().get();

        for (Sensortype sensortype : rsensorTypes) {
            Sensortype ctSensontype = sensorTypeCache.get(sensortype.getType().toString());
            if (ctSensontype == null) {
                ctSensontype = new CtSensortype(sensortype);
                sensorTypeCache.put(sensortype.getType().toString(), ctSensontype);
            }
            sensorTypes.add(ctSensontype);
        }


        Collections.sort(sensorTypes, new Comparator<Sensortype>() {
            public int compare(Sensortype o1, Sensortype o2) {
                return o1.getType() - o2.getType();
            }
        });

        return sensorTypes;
    }

    /**
     * @param epieee
     * @return
     */
    public DeviceEndPoint getEndPointByIeee(String epieee) throws JDSException {
        DeviceEndPoint endpoint = null;

        if (!cacheEnabled) {
            endpoint = getIotService().getEndPointByIeee(epieee).get();
            endpoint = new CtDeviceEndPoint(endpoint);
        } else { // cache enabled
            String endpointid = endpointIeeeCache.get(epieee);
            if (endpointid == null || !endpointCache.containsKey(endpointid)) {
                endpoint = getIotService().getEndPointByIeee(epieee).get();
                endpoint = new CtDeviceEndPoint(endpoint);
                endpointCache.put(endpoint.getEndPointId(), endpoint);
                endpointIeeeCache.put(epieee, endpoint.getEndPointId());
            } else {

                endpoint = this.getEndPointById(endpointid);
            }
        }

        return endpoint;
    }

    /**
     * @param commandId
     * @return
     * @throws JDSException
     */
    public <T extends Command> T getCommand(String commandId) throws JDSException {

        T command = null;
        CommandMsg msg = null;
        MsgClient<CommandMsg> client = MsgFactroy.getInstance().getClient(null, CommandMsg.class);
        if (!cacheEnabled) {
            msg = client.getMsgById(commandId);

        } else { // cache enabled
            msg = this.commandCache.get(commandId);
            if (command == null) {
                msg = client.getMsgById(commandId);
                commandCache.put(commandId, msg);
            } else {
                msg = commandCache.get(commandId);
            }
        }

        if (msg != null) {
            String msgbody = msg.getBody();
            JSONObject jsonobj = JSONObject.parseObject(msgbody);
            command = (T) JSONObject.parseObject(msgbody, CommandEnums.fromByName(jsonobj.getString("command")).getCommand());
            command.setCommandId(msg.getId());
            command.setResultCode(msg.getResultCode());
        }


        return command;
    }

    /**
     * @param deviceieee
     * @return
     * @throws JDSException
     */
    public Device getDeviceByIeee(String deviceieee) throws JDSException {
        Device device = null;

        if (!cacheEnabled) {
            device = getIotService().getDeviceByIeee(deviceieee).get();
            device = new CtDevice(device);
        } else { // cache enabled
            String deviceid = deviceIeeeCache.get(deviceieee);
            if (deviceid == null) {
                device = getIotService().getDeviceByIeee(deviceieee).get();
                device = new CtDevice(device);
                this.deviceCache.put(deviceid, device);
                deviceIeeeCache.put(deviceieee, device.getDeviceid());
            } else {
                device = this.getDeviceById(deviceid);
            }
        }

        return device;
    }

    public Sensortype getSensorTypesByNo(Integer devicetype) throws HomeException {
        Sensortype sensortype = this.sensorTypeCache.get(devicetype.toString());
        if (sensortype == null) {
            try {
                List<Sensortype> sensortypes = getAllSensorType();
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }
        sensortype = this.sensorTypeCache.get(devicetype.toString());
        return sensortype;
    }

    public Future<Command> sendCommand(Command command, Integer delayTime) {

        ConfigCode configCode = JDSServer.getClusterClient().getSystem(command.getSystemCode()).getConfigname();

        return CtMsgDataEngine.getEngine(configCode).sendCommand(command, delayTime);

    }


    public ZNode findZNodeByDeviceId(String deviceid, String personId) throws HomeException {
        Device device = getDeviceById(deviceid);
        final List<ZNode> znodes = device.getAllZNodes();
        ZNode znode = null;
        for (final ZNode childZNode : znodes) {
            if (childZNode.getCreateuiserid().equals(personId)) {
                return getZNodeById(childZNode.getZnodeid());
            }
        }
        if (znodes.size() > 0) {
            znode = znodes.get(0);
        }

        return znode;
    }

    public Device registerGateway(final String deviceid, final String ieee, final String macaddress, final String factoryName, final String version) throws HomeException {

        if (ieee == null || ieee.equals("")) {
            throw new HomeException("ieee is null!");
        }
        DeviceEndPoint endPoint = null;
        try {
            Device device = this.getIotService().createRootDevice(deviceid, ieee, macaddress, factoryName, version).get();
            device = new CtDevice(device);
            this.deviceCache.put(device.getDeviceid(), device);
            if (device.getDeviceEndPoints().isEmpty()) {
                try {
                    endPoint = registerEndPoint(ieee, device.getSerialno() + "01", "01", 0, device.getName());
                } catch (final HomeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                endPoint = device.getDeviceEndPoints().get(0);
            }

        } catch (JDSException e) {
            e.printStackTrace();
        }


        if (endPoint != null && endPoint.getDevice() != null) {

            Person person = OrgManagerFactory.getOrgManager().registerPerson(endPoint.getDevice().getDeviceid(), endPoint.getDevice().getSerialno(), factoryName);
        }

        return getDeviceById(endPoint.getDevice().getDeviceid());
    }

    public ZNode createGateway(String ieee, String parentId) throws HomeException {

        final Place place = createPlace(ieee, parentId);
        ZNode znode = null;
        try {
            Device device = this.getDeviceByIeee(ieee);
            Gateway gateway = new Gateway();
            gateway.setDeviceId(device.getDeviceid());
            gateway.setSerialno(ieee);
            gateway = activateGateway(gateway);
            znode = this.getDeviceByIeee(ieee).getAllZNodes().get(0);
        } catch (JDSException e) {
            throw new HomeException(e);
        }
        return znode;
    }


    public void updateEndPoint(DeviceEndPoint endPoint, boolean isIndb) {
        if (isIndb) {
            this.getIotService().updateEndPoint(endPoint);
        }
    }


    public DeviceEndPoint registerEndPoint(String sensorieee, String epieee, String ep, Integer sensorType, String name) throws HomeException {

        DeviceEndPoint defaultPoint = null;
        try {
            defaultPoint = this.getEndPointByIeee(epieee);
        } catch (JDSException e) {
            //throw new HomeException(e);
        }

        if (defaultPoint == null) {
            try {
                defaultPoint = this.getIotService().createEndPoint(sensorieee, epieee, ep, sensorType, name).get();
                defaultPoint = new CtDeviceEndPoint(defaultPoint);
                defaultPoint.getDevice().getDeviceEndPointIds().add(defaultPoint.getEndPointId());
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }

//        if (defaultPoint != null) {
//            defaultPoint.setEp(ep);
//            Set<String> znodeIds = defaultPoint.getAllZNodeIds();
//            for (final String znodeId : znodeIds) {
//                deleteZNode(znodeId);
//            }
//        }
//
//        DeviceEndPoint epPoint = null;
//        try {
//            epPoint = this.getEndPointByIeee(epieee);
//        } catch (JDSException e) {
//            e.printStackTrace();
//        }
//        if (epPoint == null) {
//            try {
//                epPoint = this.getIotService().createEndPoint(sensorieee, epieee, ep, sensorType, name).get();
//                epPoint = new CtDeviceEndPoint(epPoint);
//                this.deviceCache.remove(epPoint.getDeviceId());
//            } catch (JDSException e) {
//                e.printStackTrace();
//            }
//        }

        return defaultPoint;
    }

    public ZNode findZNodeByEndPointId(String epId, String createuiserid) throws HomeException {
        DeviceEndPoint endPoint = null;
        try {
            endPoint = this.getEndPointById(epId);
        } catch (JDSException e) {
            throw new HomeException(e);
        }

        final List<ZNode> znodes = endPoint.getAllZNodes();
        final ZNode znode = null;
        for (final ZNode childZNode : znodes) {
            if (childZNode.getCreateuiserid().equals(createuiserid)) {
                getZNodeById(childZNode.getZnodeid());
            }
        }
        return znode;
    }


    public ZNode createChildZNode(String znodeid, String endPointId) {
        ZNode znode = null;
        try {
            znode = this.getIotService().createChildZNode(znodeid, endPointId).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        if (znode != null) {
            znode = new CtZNode(znode);
            this.znodeCache.put(znode.getZnodeid(), znode);
        }

        return znode;
    }

    public Device registerDevice(String ieee, String gwDeviceId, Integer deviceType, String appaccount, String factoryName) {
        Device device = null;
        try {
            device = this.getIotService().createDevice(ieee, ieee, deviceType, factoryName, gwDeviceId).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }

        if (device != null) {
            device = new CtDevice(device);
            this.deviceCache.put(device.getDeviceid(), device);
        }

        return device;
    }

    public void removeChildDevice(String bindingAccount, String deviceid) throws HomeException {
        Device device = getDeviceById(deviceid);
        List<DeviceEndPoint> points = device.getDeviceEndPoints();

        for (DeviceEndPoint endpoint : points) {
            this.endpointCache.remove(endpoint.getEndPointId());
        }

        this.deviceCache.remove(bindingAccount);
        this.deviceCache.remove(deviceid);
        this.getIotService().removeChildDevice(bindingAccount, deviceid).execute();

    }

    public void updateNodeStatus(String znodeid, DeviceStatus status) {
        try {
            this.getIotService().updateNodeStatus(znodeid, status).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    public void deleteZNode(String nodeId) throws HomeException {
        ZNode znode = getZNodeById(nodeId);
        if (znode != null) {
            this.endpointCache.remove(znode.getEndPointid());
            this.deviceCache.remove(znode.getDeviceid());
            this.znodeCache.remove(nodeId);
            this.getIotService().deleteNode(nodeId).execute();
        }
    }

    public void updateZNode(ZNode znode, boolean indb) {
        try {
            if (indb) {
                this.getIotService().updateZNode(znode).get();
            }

        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    public void login(String deviceId) throws HomeException {
        Device device = this.getDeviceById(deviceId);
        device.setLastlogintime(System.currentTimeMillis());
        device.setStates(DeviceStatus.ONLINE);
        this.getIotService().updateDevice(device).execute();
    }

    public void updateDevice(Device device) {
        updateDevice(device, true);
    }

    public void updateDevice(Device device, Boolean isindb) {
        if (device != null) {
            this.deviceCache.put(device.getDeviceid(), device);
            this.getIotService().updateDevice(device);
            for (final DeviceEndPoint endPoint : device.getDeviceEndPoints()) {
                updateEndPoint(endPoint, isindb);
            }
        }

    }

    public void updateEndPoint(DeviceEndPoint endPoint) {
        this.getIotService().updateEndPoint(endPoint);
    }

    public void updateCurrvalue(String endPointId, String type, String currvalue) {
        try {
            DeviceEndPoint endPoint = this.getEndPointById(endPointId);
            String oldValue = endPoint.getCurrvalue().get(type);

            if (type.equals(DeviceDataTypeKey.battery.getType()) && currvalue.equals("0")) {
                return;
            }
            if (type.equals(DeviceDataTypeKey.lqi.getType()) && currvalue.equals("0")) {
                return;
            }


            if (oldValue == null && currvalue != null) {
                this.getIotService().updateCurrvalue(endPointId, type, currvalue);
            } else if (oldValue != null && currvalue == null) {
                this.getIotService().updateCurrvalue(endPointId, type, currvalue);
            } else if (currvalue != null && oldValue != null && !oldValue.equals(currvalue)) {
                this.getIotService().updateCurrvalue(endPointId, type, currvalue);
            }

        } catch (JDSException e) {
            e.printStackTrace();
        }


    }

    public void deleteDevice(String deviceid) throws HomeException {
        Device device = getDeviceById(deviceid);

        List<DeviceEndPoint> points = device.getDeviceEndPoints();

        for (DeviceEndPoint endpoint : points) {
            this.endpointCache.remove(endpoint.getEndPointId());
        }

        this.deviceCache.remove(deviceid);
        this.getIotService().deleteDevice(deviceid).execute();
    }


    public Alarm creatAlarm(String sensorId) throws HomeException {
        Alarm alarm = null;
        try {
            alarm = this.getHomeService().creatAlarm(sensorId).get();
        } catch (JDSException e) {
            throw new HomeException(e);
        }
        return alarm;
    }

    public void updateAlarm(Alarm alarm) throws HomeException {
        try {
            this.getHomeService().updateAlarm(alarm).get();
        } catch (JDSException e) {
            throw new HomeException(e);
        }
    }

    public Alarm getAlarmById(String alarmid) throws HomeException {

        Alarm alarm = null;
        try {
            alarm = this.getIotService().getAlarmById(alarmid).get();
            if (alarm != null) {
                alarm = new CtAlarm(alarm);
                this.alarmCache.put(alarmid, alarm);
            }
        } catch (JDSException e) {
            throw new HomeException(e);
        }
        return alarm;
    }

    public void deleteAlarm(String alarmId) throws HomeException {
        this.getHomeService().deleteAlarm(alarmId).execute();
        this.alarmCache.remove(alarmId);
    }

    public void deleteArea(String areaId) throws HomeException {
        Area area = this.getAreaById(areaId);
        if (area != null) {
            this.getHomeService().deleteArea(areaId).execute();
            this.areaCache.remove(areaId);
            Place place = area.getPlace();
            place.getAreaIds().remove(areaId);
            this.placeCache.put(area.getPlaceid(), place);
        }

    }

    public void deletePlace(String placeId) throws HomeException {
        Place place = this.getPlaceById(placeId);
        if (place != null) {
            if (place.getParent() != null) {
                place.getParent().getChildIds().remove(placeId);
            }

            Set<String> areaIds = place.getAreaIds();
            this.getHomeService().deletePlace(placeId).execute();
            for (String areaId : areaIds) {
                this.areaCache.remove(areaId);
            }
        }
        this.placeCache.remove(placeId);
    }


    public Area createArea(String name, String placeId) throws HomeException {
        Area area = null;
        try {
            Place place = this.getPlaceById(placeId);

            if (place != null) {
                area = this.getHomeService().createArea(name, placeId).get();
                area = new CtArea(area);
                place.getAreaIds().add(area.getAreaid());
                this.placeCache.put(placeId, place);

                this.areaCache.put(area.getAreaid(), area);
            }


        } catch (JDSException e) {
            e.printStackTrace();
        }
        return area;
    }

    public List<Device> loadAllDevice(Set<String> deviceIds) throws HomeException {


        List<Device> devices = new ArrayList<Device>();

        Set<String> loadIds = new LinkedHashSet<>();

        for (String DeviceId : deviceIds) {
            Device device = null;
            if (!cacheEnabled) {
                loadIds.add(DeviceId);
            } else { // cache enabled
                device = (Device) this.deviceCache.get(DeviceId);
                if (device == null) {
                    loadIds.add(DeviceId);
                }
            }
        }

        if (loadIds.size() > 0) {
            List<Device> loadDevices = null;
            try {
                loadDevices = this.getDeviceSearchService().loadDeviceList(loadIds.toArray(new String[loadIds.size()])).get();
            } catch (JDSException e) {
                e.printStackTrace();
            }
            if (loadDevices != null && loadDevices.size() > 0) {
                for (Device loadDevice : loadDevices) {
                    if (loadDevice != null) {
                        loadDevice = new CtDevice(loadDevice);
                        deviceCache.put(loadDevice.getDeviceid(), loadDevice);
                    }
                }
            }

        }

        for (String deviceId : deviceIds) {
            Device device = this.getDeviceById(deviceId);
            devices.add(device);
        }

        return devices;
    }


    public List<Area> loadAllArea(Set<String> areaIds) throws HomeException {


        List<Area> areas = new ArrayList<Area>();

        Set<String> loadIds = new LinkedHashSet<>();

        for (String areaId : areaIds) {
            Area Area = null;
            if (!cacheEnabled) {
                loadIds.add(areaId);
            } else { // cache enabled
                Area = (Area) this.areaCache.get(areaId);
                if (Area == null) {
                    loadIds.add(areaId);
                }

            }
        }

        if (loadIds.size() > 0) {
            List<Area> loadAreas = null;
            try {
                loadAreas = this.getHomeService().loadAreaList(loadIds.toArray(new String[loadIds.size()])).get();
            } catch (JDSException e) {
                e.printStackTrace();
            }
            if (loadAreas != null && loadAreas.size() > 0) {
                for (Area loadArea : loadAreas) {
                    if (loadArea != null) {
                        loadArea = new CtArea(loadArea);
                        areaCache.put(loadArea.getAreaid(), loadArea);
                    }
                }
            }

        }

        for (String deviceId : areaIds) {
            Area area = this.getAreaById(deviceId);
            areas.add(area);
        }


        return areas;
    }

    public List<Alarm> loadAllAlarm(List<String> alarmIds) throws HomeException {
        List<Alarm> alarms = new ArrayList<Alarm>();

        Set<String> loadIds = new LinkedHashSet<>();

        for (String alarmId : alarmIds) {
            Alarm Alarm = null;
            if (!cacheEnabled) {
                loadIds.add(alarmId);
            } else { // cache enabled
                Alarm = (Alarm) this.alarmCache.get(alarmId);
                if (Alarm == null) {
                    loadIds.add(alarmId);
                }

            }
        }

        if (loadIds.size() > 0) {
            List<Alarm> loadAlarms = null;
            try {
                loadAlarms = this.getHomeService().loadAlarmList(loadIds.toArray(new String[loadIds.size()])).get();
            } catch (JDSException e) {
                e.printStackTrace();
            }
            if (loadAlarms != null && loadAlarms.size() > 0) {
                for (Alarm loadAlarm : loadAlarms) {
                    if (loadAlarm != null) {
                        loadAlarm = new CtAlarm(loadAlarm);

                        alarmCache.put(loadAlarm.getAlarmid(), loadAlarm);
                    }
                }
            }

        }

        for (String alarmId : alarmIds) {
            Alarm alarm = this.getAlarmById(alarmId);
            alarms.add(alarm);
        }


        return alarms;
    }


    public List<DeviceEndPoint> loadAllEndPoint(Set<String> deviceEndPointIds) throws HomeException {

        List<DeviceEndPoint> deviceEndPoints = new ArrayList<DeviceEndPoint>();

        Set<String> loadIds = new LinkedHashSet<>();

        for (String deviceEndPointId : deviceEndPointIds) {
            DeviceEndPoint deviceEndPoint = null;
            if (!cacheEnabled) {
                loadIds.add(deviceEndPointId);
            } else { // cache enabled
                deviceEndPoint = (DeviceEndPoint) this.endpointCache.get(deviceEndPointId);
                if (deviceEndPoint == null) {
                    loadIds.add(deviceEndPointId);
                }

            }
        }

        if (loadIds.size() > 0) {
            List<DeviceEndPoint> loadDeviceEndPoints = null;
            try {
                loadDeviceEndPoints = this.getDeviceSearchService().loadDeviceEndPointList(loadIds.toArray(new String[loadIds.size()])).get();
            } catch (JDSException e) {
                e.printStackTrace();
            }
            if (loadDeviceEndPoints != null && loadDeviceEndPoints.size() > 0) {
                for (DeviceEndPoint loadDeviceEndPoint : loadDeviceEndPoints) {
                    if (loadDeviceEndPoint != null) {
                        loadDeviceEndPoint = new CtDeviceEndPoint(loadDeviceEndPoint);
                        endpointCache.put(loadDeviceEndPoint.getEndPointId(), loadDeviceEndPoint);
                    }
                }
            }

        }

        for (String enpointId : deviceEndPointIds) {
            try {
                deviceEndPoints.add(this.getEndPointById(enpointId));
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }
        return deviceEndPoints;
    }


    public List<ZNode> loadAllZNode(Set<String> znodeIds) throws HomeException {

        List<ZNode> znodes = new ArrayList<ZNode>();

        Set<String> loadIds = new LinkedHashSet<>();

        for (String znodeId : znodeIds) {
            ZNode znode = null;
            if (!cacheEnabled) {
                loadIds.add(znodeId);
            } else { // cache enabled
                znode = (ZNode) this.znodeCache.get(znodeId);
                if (znode == null) {
                    loadIds.add(znodeId);
                }

            }
        }

        if (loadIds.size() > 0) {
            List<ZNode> loadznodes = null;
            try {
                loadznodes = this.getDeviceSearchService().loadZNodeList(loadIds.toArray(new String[loadIds.size()])).get();
            } catch (JDSException e) {
                e.printStackTrace();
            }
            if (loadznodes != null && loadznodes.size() > 0) {
                for (ZNode loadznode : loadznodes) {
                    if (loadznode != null) {
                        loadznode = new CtZNode(loadznode);
                        znodeCache.put(loadznode.getZnodeid(), loadznode);
                    }
                }
            }

        }

        for (String znodeId : znodeIds) {
            znodes.add(this.getZNodeById(znodeId));
        }

        return znodes;
    }


    public List<ZNode> getAllChildNode(String znodeid) throws HomeException {
        ZNode znode = this.getZNodeById(znodeid);
        Set<String> znodeIds = znode.getChildNodeIdList();
        return loadAllZNode(znodeIds);

    }

    public void updateArea(Area area) {
        this.areaCache.put(area.getAreaid(), area);
        this.getHomeService().updateArea(area).execute();

    }

    public List<Person> getAllUserByDeviceId(String deviceid) throws HomeException {
        List<Person> persons = new ArrayList<Person>();
        Set<String> userIds = new LinkedHashSet<>();
        try {
            userIds = this.getHomeService().getAllUserByDeviceId(deviceid).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }

        for (String personId : userIds) {
            try {
                persons.add(OrgManagerFactory.getOrgManager().getPersonByID(personId));
            } catch (PersonNotFoundException e) {
                e.printStackTrace();
            }
        }
        return persons;
    }

    public ZNode copyGateway(String znodeid, String userID, ZNodeZType share, String placeId) throws HomeException {
        ZNode znode = null;
        try {
            final ZNode sourceNode = getZNodeById(znodeid);

            final DeviceEndPoint endPoint = this.getEndPointById(sourceNode.getEndPointid());

            ZNode copyPnode = this.getIotService().createRootZNode(endPoint.getEndPointId(), endPoint.getDevice().getPlaceid(), userID, ZNodeZType.SHARE).get();

            final List<ZNode> sourceSensors = this.getAllChildNode(sourceNode.getZnodeid());

            for (final ZNode cnode : sourceSensors) {

                ZNode copyCnode = this.getIotService().createChildZNode(cnode.getParentid(), endPoint.getEndPointId()).get();
                copyCnode = new CtZNode(copyCnode);
                this.znodeCache.put(copyCnode.getZnodeid(), copyCnode);
                final List<Alarm> alarms = getAlarmBySensorId(cnode.getZnodeid());
                for (final Alarm alarm : alarms) {
                    final Alarm copyAlarm = alarm.clone(cnode.getZnodeid());
                    updateAlarm(copyAlarm);
                }
            }

            znode = this.copyGateway(znodeid, userID, share, placeId);
            if (znode != null) {
                znode = new CtZNode(znode);
            }
            this.znodeCache.put(znode.getZnodeid(), znode);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return znode;

    }

    public boolean checkAreaName(final String name, final String placeId) throws HomeException {
        if ((name == null) || (name.equals(""))) {
            throw new HomeException("房间名称不能为空", 8002);
        }
        final Place place = getPlaceById(placeId);

        final List<Area> areas = place.getAreas();

        for (final Area dbarea : areas) {
            if (dbarea.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Scene getSceneById(String sensorSceneId) throws HomeException {
        Scene scene = sceneCache.get(sensorSceneId);
        try {
            if (scene == null) {
                scene = this.getHomeService().getSceneById(sensorSceneId).get();
                sceneCache.put(scene.getSceneid(), scene);
            }

        } catch (JDSException e) {
            e.printStackTrace();
        }
        return scene;
    }

    public void deleteScene(String sensorSceneId) {
        sceneCache.remove(sensorSceneId);
        this.getHomeService().deleteScene(sensorSceneId);
    }

    public void updateScene(Scene scene) {
        sceneCache.put(scene.getSceneid(), scene);
        this.getHomeService().updateScene(scene).execute();
    }

    public Person getMainUserByIeee(String ieee) throws HomeException {
        Person person = null;
        try {
            String personId = this.getHomeService().getMainUserByDeviceId(ieee).get();
            try {
                person = OrgManagerFactory.getOrgManager().getPersonByID(personId);
            } catch (PersonNotFoundException e) {
                throw new HomeException(e);
            }
        } catch (JDSException e) {
            throw new HomeException(e);
        }
        return person;
    }

    public Scene createScene(String sensorId) {
        Scene scene = new CtScene(sensorId);
        sceneCache.put(scene.getSceneid(), scene);
        this.updateScene(scene);
        return scene;
    }

    public List<Alarm> getAlarmBySensorId(String sensorId) {
        List<Alarm> alarms = new ArrayList<Alarm>();
        try {
            Set<String> alarmIds = this.getHomeService().getAllAlarmsBySensorId(sensorId).get();

            for (String alarmId : alarmIds) {
                alarms.add(this.getAlarmById(alarmId));
            }

        } catch (JDSException e) {
            e.printStackTrace();
        }
        return alarms;
    }

    public List<Area> getAllAreaByPlaceId(String placeId) {
        List<Area> areas = new ArrayList<Area>();
        try {
            Set<String> areaIds = this.getHomeService().getAllAreaByPlaceId(placeId).get();
            areas = this.loadAllArea(areaIds);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return areas;
    }

    public List<Place> loadPlaces(Set<String> placesIds) throws JDSException {
        List<Place> places = new ArrayList<Place>();
        Set<String> loadIds = new LinkedHashSet<>();
        try {
            for (String placeId : placesIds) {
                Place place = null;
                if (!cacheEnabled) {
                    loadIds.add(placeId);
                } else { // cache enabled
                    place = (Place) placeCache.get(placeId);
                    if (place == null) {
                        loadIds.add(placeId);
                    } else {
                        places.add(place);
                    }

                }
            }

            if (loadIds.size() > 0) {
                List<Place> loadplaces = this.getHomeService().loadPlaceList(placesIds.toArray(new String[placesIds.size()])).get();
                if (loadplaces != null && loadplaces.size() > 0) {
                    for (Place place : loadplaces) {
                        if (place != null) {
                            place = new CtPlace(place);
                            places.add(place);
                            placeCache.put(place.getPlaceid(), place);
                        }
                    }
                }

            }
        } catch (JDSException e) {
            e.printStackTrace();
        }

        for (String placeId : placesIds) {
            places.add(this.getPlaceById(placeId));
        }

        return places;
    }

    public List<Place> getAllPlaceByUserId(String userID) {
        List<Place> places = new ArrayList<Place>();
        try {
            Set<String> placesIds = this.getHomeService().getAllPlaceByUserId(userID).get();
            places = this.loadPlaces(placesIds);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return places;
    }


    public List<Place> findAllPlace() {
        List<Place> places = new ArrayList<Place>();
        try {
            Set<String> placesIds = this.getHomeService().getAllPlaceIds().get();
            places = this.loadPlaces(placesIds);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return places;
    }

    public List<Scene> getSceneBySensorId(String sensorId) {
        List<Scene> scenes = new ArrayList<Scene>();
        Set<String> sceneIds = new LinkedHashSet<>();
        try {
            sceneIds = this.getHomeService().getSceneBySensorId(sensorId).get();
            for (String sceneId : sceneIds) {
                scenes.add(this.getSceneById(sceneId));
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return scenes;
    }


    public Place createPlace(String name, String parentId) {
        Place place = null;
        try {

            place = this.getHomeService().createPlace(name, parentId, JDSServer.getInstance().getAdminUser().getId()).get();
            if (place != null) {
                place = new CtPlace(place);
                if (place.getParent() != null) {
                    place.getParent().getChildIds().add(place.getPlaceid());
                }
                this.placeCache.put(place.getPlaceid(), place);
            }
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return place;
    }

    public void updatePlace(Place place) {
        try {
            this.getHomeService().savePlace(place).get();
        } catch (JDSException e) {
            e.printStackTrace();
        }
        placeCache.remove(place.getParentId());
        this.placeCache.put(place.getPlaceid(), place);
    }

    public CommandClient getCommandClientByieee(String ieee) throws JDSException {
        CommandClient client = CommandFactory.getInstance().getCommandClientByieee(ieee);
        return client;
    }


    public ZNode findZNodeByIeee(String ieee, String userID) throws HomeException {
        try {
            ZNode znode = this.getDeviceSearchService().findZNodeByIeee(ieee, userID).get();
            znode = new CtZNode(znode);
            this.znodeCache.put(znode.getZnodeid(), znode);
            return znode;
        } catch (JDSException e) {
            throw new HomeException(e);
        }


    }

    public List<Sensortype> getSensorTypesByGatewayId(String gatewayId) throws HomeException {
        List<Sensortype> sensortypes = new ArrayList<Sensortype>();
        try {
            return this.getHomeService().getSensorTypesByGatewayId(gatewayId).get();
        } catch (JDSException e) {
            throw new HomeException(e);
        }
    }

    public Area bindingArea(String sensorieee, String areaId) throws HomeException {
        Area area = this.getAreaById(areaId);
        try {
            this.getHomeService().bindingArea(sensorieee, areaId).get();
            Device device = getDeviceByIeee(sensorieee);
            device.setAreaid(areaId);
            this.deviceCache.put(device.getDeviceid(), device);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return area;
    }

    public Place bindingPlace(String gwieee, String placeId) throws HomeException {
        Place place = this.getPlaceById(placeId);

        try {
            this.getHomeService().bindingPlace(gwieee, placeId).get();
            Device device = getDeviceByIeee(gwieee);

            device.setPlaceid(placeId);
            this.deviceCache.put(device.getDeviceid(), device);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return place;

    }

    public Gateway activateGateway(Gateway gateway) throws HomeException {
        DeviceEndPoint endPoint = null;
        try {
            Device device = this.getDeviceById(gateway.getDeviceId());
            if (device == null) {
                device = this.getDeviceByIeee(gateway.getSerialno());
            }

            if (device == null) {
                throw new HomeException("GatewayId 不存在，请先完成注册！ ", HomeException.USERNAMEONTEXITS);
            }

            //更新版本号


            final String accountname = device.getBindingaccount();
            if (accountname == null) {
                throw new HomeException("网关未完成用户绑定 ", HomeException.USERNAMEONTEXITS);
            }

            if (device.getDeviceEndPoints().isEmpty()) {
                endPoint = registerEndPoint(device.getSerialno(), device.getSerialno() + 01, "01", 0, this.getSensorTypesByNo(0).getName());
            } else {
                endPoint = device.getDeviceEndPoints().get(0);
            }

            String sysCode = device.getSubsyscode();
            if (sysCode == null) {
                sysCode = device.getFactory();
                device.setSubsyscode(sysCode);
            }


            Person gatewayAccount = null;
            try {
                gatewayAccount = OrgManagerFactory.getOrgManager().getPersonByAccount(device.getBindingaccount());
            } catch (final PersonNotFoundException e1) {
                OrgManagerFactory.getOrgManager().registerPerson(device.getDeviceid(), device.getSerialno(), device.getFactory());
            }

            List<ZNode> nodes = endPoint.getAllZNodes();
            if (nodes.isEmpty()) {
                // 创建家
                Place place = this.getHomeService().createPlace(endPoint.getIeeeaddress(), gatewayAccount.getID(), JDSServer.getInstance().getAdminUser().getId()).get();
                // 创建默认节点
                ZNode znode = this.getIotService().createRootZNode(endPoint.getEndPointId(), place.getPlaceid(), gatewayAccount.getID(), ZNodeZType.GATEWAY).get();
                endPoint.getAllZNodes().add(znode);
                device.setPlaceid(place.getPlaceid());
                this.endpointCache.put(endPoint.getEndPointId(), endPoint);
            }


            ServerNode serverBean = JDSServer.getClusterClient().getAllServerMap().get(sysCode);
            if (serverBean == null) {
                serverBean = JDSServer.getInstance().getCurrServerBean();
                if (serverBean != null) {
                    device.setSubsyscode(serverBean.getId());
                }
            }

            if (serverBean == null) {
                serverBean = JDSServer.getClusterClient().getAllServerMap().get(device.getFactory());
                device.setSubsyscode(device.getFactory());
            }
            gateway.setSerialno(device.getSerialno());
            gateway.setMacno(device.getMacaddress());
            gateway.setFactory(device.getFactory());
            gateway.setDeviceId(device.getDeviceid());
            gateway.setGatewayAccount(device.getBindingaccount());
            gateway.setKeyword(gatewayAccount.getID());
            gateway.setMainServerUrl(serverBean.getUrl());
            String commandServerUrl = StringUtility.replace(serverBean.getUrl(), "http://gw", "http://comet");
            gateway.setCommandServerUrl(commandServerUrl + HomeConstants.SYSTEM_COMMANDSERVERURL);
            log.info("active返回结果:" + JSONObject.toJSONString(gateway,config));

            if (gateway.getVersion() != null) {
                device.setBatch(gateway.getVersion());
            }

            HomeServer.getAppEngine().updateDevice(device);

        } catch (JDSException e) {
            e.printStackTrace();
        }


        return gateway;
    }

    public List<Command> gatewayErrorReport(GatewayErrorReport errorReport) {
        String account = errorReport.getGatewayAccount();
        List<Command> commandList = new ArrayList<Command>();

        Device device = null;
        String ieee = errorReport.getSerialno();
        if (ieee != null) {
            if (errorReport.getDeviceId() != null && !errorReport.getDeviceId().equals("")) {
                try {
                    device = HomeServer.getAppEngine().getDeviceById(errorReport.getDeviceId());
                } catch (final HomeException e) {
                    log.error(e);
                    //e.printStackTrace();
                }
            }

            if (device == null && errorReport.getSerialno() != null) {

                try {
                    device = HomeServer.getAppEngine().getDeviceByIeee(errorReport.getSerialno());
                } catch (Exception e) {
                    log.error(e);
                }

                if (device != null) {
                    log.info("delete: " + device.getDeviceid());
                    this.getHomeService().deleteGateway(device.getDeviceid());
                }
                if (errorReport.getSerialno() != null && !errorReport.getSerialno().equals("")) {
                    try {
                        Device rdevice = this.getIotService().createRootDevice(errorReport.getDeviceId(), errorReport.getSerialno(), errorReport.getMacno(), errorReport.getSystemCode(), errorReport.getCurrVersion()).get();
                        rdevice = new CtDevice(rdevice);
                        this.deviceCache.put(rdevice.getDeviceid(), rdevice);
                    } catch (JDSException e) {
                        e.printStackTrace();
                    }
                }

            }

        }

        if (account == null || account.equals("")) {
            account = errorReport.getDeviceId();
        }

        if (account == null || account.equals("")) {
            if (errorReport.getSerialno() != null && !errorReport.getSerialno().equals("")) {
                try {
                    device = this.getDeviceByIeee(errorReport.getSerialno());
                } catch (JDSException e) {
                    log.error(e);
                }
                account = device.getDeviceid();

            }
        }


        try {
            if (device != null) {
                final OrgManager accountManager = OrgManagerFactory.getOrgManager();
                Person currPerson = OrgManagerFactory.getOrgManager().registerPerson(account, errorReport.getSerialno(), errorReport.getSystemCode());
                registerGateway(device.getDeviceid(), device.getSerialno(), device.getSerialno(), errorReport.getSystemCode(), errorReport.getCurrVersion());
                InitGatewayCommand command = new InitGatewayCommand();
                command.setCommandId(UUID.randomUUID().toString());
                commandList.add(command);
                if (ieee != null) {
                    // 版本号
                    device.setBatch(errorReport.getCurrVersion());
                    this.updateDevice(device);
                }
            } else {
                DebugCommand command = new DebugCommand();
                command.setCommandId(UUID.randomUUID().toString());
                commandList.add(command);
            }

        } catch (HomeException e1) {
            log.error(e1);
        }


        return commandList;

    }

    void logSend(final String account, final String body, final String title, final String event, final String gwieee, final String sensorieee) {
        ExecutorService service = RemoteConnectionManager.getConntctionService("logginng");
        service.execute(new Runnable() {
            @Override
            public void run() {
                Person currPerson = null;
                try {
                    currPerson = OrgManagerFactory.getOrgManager().getPersonByAccount(account);
                    SensorMsg msg = null;
                    if (currPerson != null) {
                        currPerson = OrgManagerFactory.getOrgManager().getPersonByID(JDSServer.getInstance().getAdminUser().getId());
                        msg = (SensorMsg) MsgFactroy.getInstance().getClient(currPerson.getID(), SensorMsg.class).creatMsg2Person(currPerson.getID());
                        msg.setStatus(MsgStatus.NORMAL);
                        msg.setType(MsgType.LOG.getType());

                    } else {
                        msg = (SensorMsg) MsgFactroy.getInstance().getClient(currPerson.getID(), SensorMsg.class).creatMsg2Person(currPerson.getID());
                        msg.setStatus(MsgStatus.NORMAL);
                        msg.setType(MsgType.ERRORREPORT.getType());

                    }
                    msg.setEventTime(System.currentTimeMillis());
                    msg.setTitle(title);
                    msg.setEvent(event);
                    msg.setBody(body);
                    msg.setSensorId(sensorieee);
                    msg.setGatewayId(gwieee);
                    msg.setFrom(currPerson.getID());
                    msg.setReceiver(currPerson.getID());

                    MsgFactroy.getInstance().getClient(currPerson.getID(), SensorMsg.class).updateMsg(msg);
                    // msgGroup.update();
                } catch (final PersonNotFoundException localPersonNotFoundException) {

                } catch (JDSException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void logging(final String account, final String body, String event, String sensorieee) {
        logging(account, body, event, event, sensorieee);
    }

    public void logging(final String account, final String body, String title, final String event, String sensorieee) {


        Device device = null;
        try {
            device = CtIotCacheManager.getInstance().getDeviceById(account);
            if (device != null) {
                if (sensorieee == null) {
                    sensorieee = device.getSerialno();
                }
                logSend(account, body, title, event, device.getSerialno(), sensorieee);
            } else {
                logSend(account, body, title, event, sensorieee, sensorieee);
            }


        } catch (HomeException e) {
            e.printStackTrace();
        }


    }


    public IotService getIotService() {
        IotService service = (IotService) EsbUtil.parExpression(IotService.class);
        return service;
    }

    public DeviceSearchService getDeviceSearchService() {
        DeviceSearchService service = (DeviceSearchService) EsbUtil.parExpression(DeviceSearchService.class);
        return service;
    }


    public HomeService getHomeService() {
        HomeService service = (HomeService) EsbUtil.parExpression(HomeService.class);
        return service;
    }


    public ListResultModel<List<Device>> getAllDevicesByFactory(String factoryName) throws JDSException {

        ListResultModel<List<Device>> resultModel = new ListResultModel<List<Device>>();
        ListResultModel<Set<String>> listResultModel = getDeviceSearchService().findGWDevicesByFactory(factoryName);
        Set<String> deviceIds = listResultModel.get();
        List<Device> devices = this.loadAllDevice(deviceIds);
        resultModel.setData(devices);
        resultModel.setSize(listResultModel.getSize());
        return resultModel;

    }


    public void updateSensortype(Sensortype sensortype) throws JDSException {
        if (sensortype != null) {
            this.getIotService().updateSensorType(sensortype).get();
            if (sensortype instanceof CtSensortype) {
                sensorTypeCache.put(sensortype.getType().toString(), sensortype);
            } else {
                sensorTypeCache.put(sensortype.getType().toString(), new CtSensortype(sensortype));
            }


        }

    }


    public void deleteSensrotype(Integer sensorno) throws JDSException {
        this.getIotService().deleteSensorType(sensorno).get();
        sensorTypeCache.remove(sensorno.toString());
    }

    public void clearDevices(List<String> deviceids) throws HomeException {
        for (String deviceid : deviceids) {
            Device device = getDeviceById(deviceid);

            List<DeviceEndPoint> points = device.getDeviceEndPoints();

            for (DeviceEndPoint endpoint : points) {
                Set<String> znodeIds = endpoint.getAllZNodeIds();
                for (String znodeId : znodeIds) {
                    this.znodeCache.remove(znodeId);
                }
                this.endpointCache.remove(endpoint.getEndPointId());
            }

            this.deviceCache.remove(deviceid);
        }
        try {

            this.getIotService().clearDevices(deviceids.toArray(new String[deviceids.size()])).get();
        } catch (JDSException e) {
            throw new HomeException(e);
        }


    }

    public ListResultModel<List<DeviceEndPoint>> findEndPoint(Condition<IOTConditionKey, JLuceneIndex> condition) throws JDSException {
        ListResultModel<List<DeviceEndPoint>> resultModel = new ListResultModel<List<DeviceEndPoint>>();
        ListResultModel<Set<String>> listResultModel = this.getDeviceSearchService().findEndPoint(condition);
        Set<String> epIds = listResultModel.get();
        List<DeviceEndPoint> deviceEndPoints = this.loadAllEndPoint(epIds);
        resultModel.setData(deviceEndPoints);
        resultModel.setSize(listResultModel.getSize());
        return resultModel;

    }

    public ListResultModel<List<Device>> findDevice(Condition<IOTConditionKey, JLuceneIndex> condition) throws JDSException {
        ListResultModel<List<Device>> resultModel = new ListResultModel<List<Device>>();
        ListResultModel<Set<String>> listResultModel = this.getDeviceSearchService().findDevice(condition);
        Set<String> deviceIds = listResultModel.get();
        List<Device> devices = this.loadAllDevice(deviceIds);
        resultModel.setData(devices);
        resultModel.setSize(listResultModel.getSize());
        return resultModel;
    }

    public ListResultModel<List<ZNode>> findZnode(Condition<IOTConditionKey, JLuceneIndex> condition) throws JDSException {
        ListResultModel<List<ZNode>> resultModel = new ListResultModel<List<ZNode>>();
        ListResultModel<Set<String>> listResultModel = this.getDeviceSearchService().findZNode(condition);
        Set<String> nodeIds = listResultModel.get();
        List<ZNode> znodes = this.loadAllZNode(nodeIds);
        resultModel.setData(znodes);
        resultModel.setSize(listResultModel.getSize());
        return resultModel;
    }
}
