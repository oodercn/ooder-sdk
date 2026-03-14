package net.ooder.agent.client.iot.client.impl;

import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.iot.*;
import net.ooder.agent.client.iot.client.GatewayWebService;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;
import net.ooder.agent.client.iot.enums.DeviceStatus;
import net.ooder.agent.client.iot.json.*;
import net.ooder.agent.client.iot.json.device.EndPoint;
import net.ooder.agent.client.iot.json.device.Gateway;
import net.ooder.agent.client.iot.json.device.Sensor;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.config.ErrorListResultModel;
import  net.ooder.config.ErrorResultModel;
import  net.ooder.config.ListResultModel;
import  net.ooder.config.ResultModel;
import  net.ooder.agent.client.home.client.AppClient;
import  net.ooder.agent.client.home.client.GWClient;
import  net.ooder.agent.client.home.engine.HomeServer;
import  net.ooder.jds.core.User;
import  net.ooder.jds.core.esb.EsbUtil;
import  net.ooder.server.JDSClientService;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@EsbBeanAnnotation(id = "GatewayWebService", name = "网关服务", expressionArr = "GatewayWebServiceImpl()", desc = "网关服务")
public class GatewayWebServiceImpl implements GatewayWebService {

    private GWClient gwClient;

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, GatewayWebServiceImpl.class);

    void logging(Object obj, String methodName) {
        logging(obj, methodName, null);
    }

    void logging(Object obj, String methodName, String sensorieee) {

        try {
            if (getGWClient().getConnectInfo() != null && getGWClient().getConnectInfo().getLoginName() != null) {
                HomeServer.getAppEngine().logging(getGWClient().getConnectInfo().getLoginName(), JSONObject.toJSONString(obj).toString(), methodName, methodName, sensorieee);
            }
        } catch (HomeException e) {
            e.printStackTrace();
        }


    }

    public ResultModel<List<Sensor>> addSensors(List<Sensor> sensors) {
        ResultModel<List<Sensor>> userStatusInfo = new ResultModel<List<Sensor>>();

        logging(sensors, "addSensors");
        try {
            getGWClient().beginTransaction();
            for (Sensor sensor : sensors) {
                try {
                    registerSensor(sensor);
                } catch (HomeException e) {

                }
            }
            getGWClient().commitTransaction();
            userStatusInfo.setData(sensors);
        } catch (HomeException e) {
            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    public ResultModel<Boolean> gatewayOffLine(@RequestBody Gateway gateway) {
        logging(gateway, "gatewayOffLine");
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            getGWClient().beginTransaction();
            getGWClient().gatewayOffLine(gateway.getDeviceId());
            getGWClient().commitTransaction();
        } catch (HomeException e) {
            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            result = new ErrorResultModel();
            result.setData(false);
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    public ResultModel<Boolean> gatewayOnLine(Gateway gateway) {
        logging(gateway, "gatewayOnLine");
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            getGWClient().beginTransaction();

            String deviceId = gateway.getDeviceId();
            if (gateway.getDeviceId() == null) {
                deviceId = getGWClient().getDeviceByIeee(gateway.getSerialno()).getDeviceid();
            }
            getGWClient().gatewayOnLine(deviceId);
            getGWClient().commitTransaction();
        } catch (HomeException e) {
            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            result = new ErrorResultModel();
            result.setData(false);
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    public ResultModel<Boolean> removeSensor(Sensor sensor) {

        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();

        try {
            getGWClient().beginTransaction();

            Device device = getGWClient().getDeviceByIeee(sensor.getSerialno());
            HomeServer.getAppEngine().logging(device.getBindingaccount(), JSONObject.toJSONString(sensor).toString(), "removeSensor", "removeSensor", sensor.getSerialno());
            getGWClient().removeSensor(sensor.getSerialno());

            getGWClient().commitTransaction();
        } catch (HomeException e) {
            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    public ResultModel<Boolean> sensorOffLine(Sensor sensor) {

        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();

        try {
            getGWClient().beginTransaction();
            Device device = getGWClient().getDeviceByIeee(sensor.getSerialno());
            HomeServer.getAppEngine().logging(device.getBindingaccount(), JSONObject.toJSONString(sensor).toString(), "sensorOffLine", "sensorOffLine", sensor.getSerialno());

            if (device != null) {
                getGWClient().sensorOffLine(device.getDeviceid());
            }
            getGWClient().commitTransaction();
        } catch (HomeException e) {
            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    public ResultModel<Boolean> sensorOnLine(Sensor sensor) {

        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();

        try {
            getGWClient().beginTransaction();
            Device device = getGWClient().getDeviceByIeee(sensor.getSerialno());
            HomeServer.getAppEngine().logging(device.getBindingaccount(), JSONObject.toJSONString(sensor).toString(), "sensorOnLine", "sensorOnLine", sensor.getSerialno());
            if (device != null) {
                getGWClient().sensorOnLine(device.getDeviceid());
                if (sensor.getBattery() != null) {
                    device.setBattery(sensor.getBattery().toString());
                    getGWClient().updateDevice(device);
                    List<DeviceEndPoint> eps = device.getDeviceEndPoints();
                    for (DeviceEndPoint ep : eps) {
                        if (ep != null) {
                            // 未知设备
                            getGWClient().addData(ep.getEndPointId(), DeviceDataTypeKey.battery, sensor.getBattery().toString(), null);
                        }
                    }
                }
            }
            getGWClient().commitTransaction();
        } catch (HomeException e) {
            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    public ResultModel<List<Sensor>> syncSensor(List<Sensor> sensors) {
        ResultModel<List<Sensor>> userStatusInfo = new ResultModel<List<Sensor>>();

        try {

            List devices = new ArrayList();
            for (Sensor sensor : sensors) {
                if (sensor.getSerialno() != null && sensor.getSensorType() != null && !sensor.getSensorType().equals(0)) {
                    Device device = this.registerSensor(sensor);
                    devices.add(device);
                }

            }
            logger.info("start syncSensor device= " + JSONObject.toJSONString(devices));
            getGWClient().syncSensor(devices);
            userStatusInfo.setData(sensors);
            HomeServer.getAppEngine().logging(getGWClient().getConnectInfo().getLoginName(), JSONObject.toJSONString(sensors).toString(), "syncSensor", "syncSensor", null);

        } catch (HomeException e) {

            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }

            logger.error("syncSensor e.getMessage()=" + e.getMessage());
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    private Device registerSensor(Sensor sensor) throws HomeException {
        logging(sensor, "registerSensor", sensor.getSerialno());
        List<EndPoint> epList = sensor.getEpList();
        String epieee = sensor.getSerialno();

        String sn = epieee;

        logger.info("start registerSensor getGWClient= " + getGWClient());
        Device device = null;
        try {
            device = getGWClient().registerSensor(sn, sensor.getSensorType(), null);
        } catch (JDSException e) {
            throw new HomeException(e);
        }
        device.setStates(DeviceStatus.ONLINE);
        device.setMacaddress(sn);
        device.setName(sensor.getDeviceName());
        if (sensor.getStatus() != null) {
            device.setStates(DeviceStatus.fromCode(Integer.valueOf(sensor.getStatus())));
        }

        if (sensor.getBattery() != null) {
            device.setBattery(sensor.getBattery().toString());

        }
        // 同一设备类型可能变化

        if (sensor.getSensorType() != null && sensor.getSensorType() != 100 && !sensor.getSensorType().equals(sensor.getSensorType())) {
            device.setDevicetype(sensor.getSensorType());
        }

        // 同一设备类型可能变化
        if (sensor.getCurrVersion() != null && sensor.getSensorType().equals(0)) {
            device.setBatch(sensor.getCurrVersion());
        }

        if (epList == null || epList.size() == 0) {
            epList = new ArrayList<EndPoint>();
            EndPoint ep = new EndPoint();
            if (epieee.length() > 16) {
                String defaultep = sensor.getSerialno().substring(16, sensor.getSerialno().length());
                ep.setEp(defaultep);
                ep.setIeee(epieee);
            } else {
                epieee = device.getSerialno() + HomeConstants.DEVICE_DEFAULT_EP;
                ep.setEp(HomeConstants.DEVICE_DEFAULT_EP);
                ep.setIeee(epieee);

            }

            ep.setSensorType(device.getSensortype().getType());
            epList.add(ep);
        }

        for (EndPoint endPoint : epList) {
            try {

                String ep = endPoint.getEp();
                String ieee = endPoint.getIeee();

                if (ep == null || ep.equals("")) {
                    ep = HomeConstants.DEVICE_DEFAULT_EP;
                }

                if (ieee == null || ieee.equals("")) {
                    ieee = device.getSerialno() + ep;
                }

                endPoint.setEp(ep);
                endPoint.setIeee(ieee);
                getGWClient().registerEndPonit(sn, endPoint);
            } catch (JDSException e) {
                e.printStackTrace();
            }
        }

        getGWClient().updateDevice(device);

        return device;

    }

    public ResultModel<Boolean> setOutLetSensorInfo(String sensorId, Boolean vlaue) {

        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
        try {
            getGWClient().beginTransaction();
            getAppClient().setOutLetSensor(sensorId, vlaue);
            getGWClient().commitTransaction();
        } catch (HomeException e) {

            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    public ResultModel<PlaceInfo> shareGateway(String serialno, String mainaccount, String mainpassword, String placeId) {

        ResultModel<PlaceInfo> userStatusInfo = new ResultModel<PlaceInfo>();
        try {
            getGWClient().beginTransaction();
            Place data = getAppClient().shareGateway(serialno, mainaccount, mainpassword, placeId);
            userStatusInfo.setData(new PlaceInfo(data));
            getGWClient().commitTransaction();
        } catch (HomeException e) {
            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;

    }

    public ResultModel<Boolean> stopShareGateway(String gatewayid) {

        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
        try {
            getGWClient().beginTransaction();
            getAppClient().updateGatewayStatus(gatewayid, DeviceStatus.DELETE);
            getGWClient().commitTransaction();
        } catch (HomeException e) {
            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;

    }

    public ResultModel<SensorInfo> addSensorByType(String gatewayid, Integer type, String serialno) {

        ResultModel<SensorInfo> userStatusInfo = new ResultModel<SensorInfo>();
        try {
            getGWClient().beginTransaction();
            this.getAppClient().sendAddSensorCommand(gatewayid, serialno);
            ZNode znode = getAppClient().addDevice(gatewayid, type, serialno);
            SensorInfo sensorInfo = new SensorInfo(znode);
            // getGWClient().commitTransaction();
            userStatusInfo.setData(sensorInfo);
        } catch (HomeException e) {
            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    public ResultModel<Boolean> addSensor(String gatewayid, String serialno) {

        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
        try {
            getGWClient().beginTransaction();
            this.getAppClient().sendAddSensorCommand(gatewayid, serialno);
        } catch (HomeException e) {
            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }

        return userStatusInfo;
    }

    public ResultModel<Boolean> canCreateGateway(String serialno, String placeId) {
        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
        try {

            Boolean isCan = getAppClient().canCreateGateway(serialno, placeId);
            userStatusInfo.setData(isCan);
        } catch (HomeException e) {
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    public ResultModel<Boolean> deleteSensor(String sensorId) {
        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
        try {
            getGWClient().beginTransaction();
            getAppClient().deleteZNode(sensorId);
            getGWClient().commitTransaction();
        } catch (HomeException e) {
            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    public ResultModel<List<SensorInfo>> getAllSensorByGatewayId(String gatewayId) {
        ResultModel userStatusInfo = new ResultModel<List<SensorInfo>>();
        try {

            List<ZNode> znodes = getAppClient().getAllChildNode(gatewayId);
            List<SensorInfo> sensors = new ArrayList<SensorInfo>();
            for (ZNode znode : znodes) {
                SensorInfo sensorInfo = new SensorInfo(znode);
                sensors.add(sensorInfo);
            }

            userStatusInfo.setData(sensors);
        } catch (HomeException e) {

            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    public ResultModel<List<SensorInfo>> getAllSensorByPlaceId(String placeId) {
        ResultModel userStatusInfo = new ResultModel<List<SensorInfo>>();
        try {

            List<ZNode> znodes = getAppClient().getAllZNodeByPlaceId(placeId);
            List<SensorInfo> sensors = new ArrayList<SensorInfo>();
            for (ZNode znode : znodes) {
                SensorInfo sensorInfo = new SensorInfo(znode);
                sensors.add(sensorInfo);
            }

            userStatusInfo.setData(sensors);
        } catch (HomeException e) {
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    public ResultModel<SensorInfo> getSensorById(String znodeId) {
        ResultModel<SensorInfo> userStatusInfo = new ResultModel<SensorInfo>();
        try {
            ZNode znode = getAppClient().getZNodeById(znodeId);
            userStatusInfo.setData(new SensorInfo(znode));
        } catch (HomeException e) {
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    public ResultModel<List<SensorInfo>> getSensorByIeees(List<String> sensoriees) {
        ResultModel<List<SensorInfo>> userStatusInfo = new ResultModel<List<SensorInfo>>();

        try {

            List<ZNode> znodes = getAppClient().getSensorByIeees(sensoriees);
            List<SensorInfo> sensors = new ArrayList<SensorInfo>();
            for (ZNode znode : znodes) {
                if (znode.getParentNode() != null) {
                    SensorInfo sensorInfo = new SensorInfo(znode);
                    sensors.add(sensorInfo);
                }

            }

            userStatusInfo.setData(sensors);
        } catch (HomeException e) {

            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;

    }

    public ListResultModel<List<PMSSensorInfo>> searchLock(PMSSensorSearch search) {
        ListResultModel<List<PMSSensorInfo>> userStatusInfo = new ListResultModel<List<PMSSensorInfo>>();

        try {

            List<ZNode> znodes = getAppClient().getSensorByIeees(search.getLockIds());
            List<ZNode> sensors = new ArrayList<ZNode>();

            List<PMSSensorInfo> searchSensors = new ArrayList<PMSSensorInfo>();

            for (ZNode znode : znodes) {
                Integer lockStatus = search.getLockStatus();

                String power = znode.getEndPoint().getDevice().getBattery();

                if (lockStatus.equals(2) || lockStatus.equals(znode.getStatus())) {

                    if (power == null || (Integer.valueOf(power) >= search.getMinpower() && Integer.valueOf(power) <= search.getMaxpower())) {

                        sensors.add(znode);

                    }

                }
            }

            Integer pageSize = search.getPageSize();
            Integer pageIndex = search.getPageIndex();

            int start = 0;
            int end = sensors.size();
            if (pageSize > -1) {
                start = pageIndex * pageSize;
                end = pageIndex * pageSize + pageSize;
            }

            if (sensors.size() > start) {
                if (sensors.size() < end) {
                    end = sensors.size();
                }
                for (int k = start; k < end; k++) {
                    ZNode node = sensors.get(k);
                    PMSSensorInfo info = new PMSSensorInfo(node);
                    searchSensors.add(info);

                }
            }

            userStatusInfo.setData(searchSensors);
            userStatusInfo.setSize(sensors.size());
        } catch (HomeException e) {

            userStatusInfo = new ErrorListResultModel();
            ((ErrorListResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorListResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    public ResultModel<NetworkInfo> changeNetworkResponse(NetworkInfo networkInfo) {
        ResultModel<NetworkInfo> userStatusInfo = new ResultModel<NetworkInfo>();
        try {
            getGWClient().beginTransaction();
            getGWClient().changeGatewayNetwork(networkInfo);
            getGWClient().commitTransaction();
            userStatusInfo.setData(networkInfo);

        } catch (HomeException e) {
            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    public ResultModel<List<SensorTypeInfo>> getSensorTypesByGatewayId(String gatewayId) {
        ResultModel<List<SensorTypeInfo>> userStatusInfo = new ResultModel<List<SensorTypeInfo>>();
        try {
            List<Sensortype> sensorTypes = getAppClient().getSensorTypesByGatewayId(gatewayId);

            List<SensorTypeInfo> sensortypeInfos = new ArrayList<SensorTypeInfo>();
            for (Sensortype sensorType : sensorTypes) {
                SensorTypeInfo sensortypeInfo = new SensorTypeInfo(sensorType);
                sensortypeInfos.add(sensortypeInfo);
            }
            userStatusInfo.setData(sensortypeInfos);
        } catch (HomeException e) {
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    public ResultModel<SensorTypeInfo> getSensorTypesByNo(Integer typno) {
        ResultModel<SensorTypeInfo> userStatusInfo = new ResultModel<SensorTypeInfo>();
        try {
            Sensortype sensorType = getAppClient().getSensorTypesByNo(typno);
            userStatusInfo.setData(new SensorTypeInfo(sensorType));
        } catch (HomeException e) {
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    public ResultModel<List<UserInfo>> getShareUserByGwId(String gatewayId) {
        ResultModel<List<UserInfo>> userStatusInfo = new ResultModel<List<UserInfo>>();
        try {
            List<User> users = getAppClient().getShareUser(gatewayId);
            List<UserInfo> userinfos = new ArrayList<UserInfo>();

            for (User user : users) {
                UserInfo userinfo = new UserInfo(user);
                userinfos.add(userinfo);
            }

            userStatusInfo.setData(userinfos);
        } catch (HomeException e) {
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    public ResultModel<Boolean> openShareGateway(String gatewayid) {
        ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
        try {
            getGWClient().beginTransaction();
            getAppClient().updateGatewayStatus(gatewayid, DeviceStatus.SHARE);
            getGWClient().commitTransaction();

        } catch (HomeException e) {
            try {
                getGWClient().rollbackTransaction();
            } catch (HomeException robacke) {

            }
            userStatusInfo = new ErrorResultModel();
            ((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    public ResultModel<GatewayInfo> addGateway(String serialno, String placeid) {
        ResultModel<GatewayInfo> result = new ResultModel<GatewayInfo>();
        AppClient client = null;
        try {
            client = this.getAppClient();
            if (serialno.length() > 16) {
                serialno = serialno.substring(0, 16);
            }

            boolean canCreate = client.canCreateGateway(serialno, placeid);

            if (!canCreate) {
                result = new ErrorResultModel();
                ((ErrorResultModel) result).setErrcode(HomeException.GETWAYIDINVALID);
                ((ErrorResultModel) result).setErrdes("网关已绑定");
            } else {
                ZNode znode = client.createGateway(serialno, placeid);

                GatewayInfo info = new GatewayInfo(znode);
                result.setData(info);
            }

        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    public ResultModel<GatewayInfo> bindingGateway(String wbaccount, String serialno) {
        ResultModel<GatewayInfo> result = new ResultModel<GatewayInfo>();
        AppClient client = null;
        try {
            client = this.getAppClient();
            ZNode znode = client.bindingGateway(wbaccount, serialno);
            GatewayInfo info = new GatewayInfo(znode);
            result.setData(info);

        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    public ResultModel<GatewayInfo> createGateway(GatewayInfo gatewayInfo) {
        ResultModel<GatewayInfo> result = new ResultModel<GatewayInfo>();
        AppClient client = null;
        try {
            client = this.getAppClient();

            ZNode znode = client.createGateway(gatewayInfo.getWbaccount(), gatewayInfo.getSerialno());
            GatewayInfo info = new GatewayInfo(znode);
            result.setData(info);

        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    public ResultModel<Boolean> deleteGateway(String gatewayId) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        AppClient client = null;
        try {
            client = this.getAppClient();

            client.deleteZNode(gatewayId);

        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    public ResultModel<List<GatewayInfo>> getAllGatewayInfos(String placeId) {
        ResultModel<List<GatewayInfo>> result = new ResultModel<List<GatewayInfo>>();
        List<GatewayInfo> gwList = new ArrayList<GatewayInfo>();
        AppClient client = null;
        try {
            client = this.getAppClient();
            List<ZNode> gwznodes = client.getAllGatewayByPlaceId(placeId);
            for (ZNode znode : gwznodes) {
                GatewayInfo info = new GatewayInfo(znode);
                gwList.add(info);
            }
            result.setData(gwList);

        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }

        return result;
    }

    public ResultModel<String> getDeviceValue(String ieee, DeviceDataTypeKey attributeName) {
        ResultModel<String> result = new ResultModel<String>();

        AppClient client = null;
        try {
            client = this.getAppClient();
            String value = (String) client.getZNodeByIeee(ieee).getEndPoint().getCurrvalue().get(attributeName);
            result.setData(value);

        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }

        return result;
    }

    public ResultModel<GatewayInfo> getGatewayById(String gatewayId) {
        ResultModel<GatewayInfo> result = new ResultModel<GatewayInfo>();
        AppClient client = null;
        try {
            client = this.getAppClient();

            ZNode znode = client.getZNodeById(gatewayId);
            GatewayInfo info = new GatewayInfo(znode);
            result.setData(info);

        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    public ResultModel<GatewayInfo> getGatewayInfoBySN(String gatewaySN) {
        ResultModel<GatewayInfo> result = new ResultModel<GatewayInfo>();
        AppClient client = null;
        try {
            client = this.getAppClient();

            ZNode znode = client.getZNodeByIeee(gatewaySN);
            GatewayInfo info = new GatewayInfo(znode);
            result.setData(info);

        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    public ResultModel<List<SensorInfo>> getSensorInfoByIds(List<String> sensorIds) {
        ResultModel<List<SensorInfo>> result = new ResultModel<List<SensorInfo>>();
        List<SensorInfo> sensors = new ArrayList<SensorInfo>();
        AppClient client = null;
        try {
            client = this.getAppClient();

            for (String sensorId : sensorIds) {
                ZNode znode = client.getZNodeById(sensorId);
                SensorInfo info = new SensorInfo(znode);
                sensors.add(info);
            }

            result.setData(sensors);

        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    public ResultModel<List<ShareUserInfo>> getShareUser() {
        ResultModel<List<ShareUserInfo>> result = new ResultModel<List<ShareUserInfo>>();
        List<ShareUserInfo> sharUsreList = new ArrayList<ShareUserInfo>();

        AppClient client = null;
        try {
            client = this.getAppClient();
            List<Place> places = client.getAllPlace();

            for (Place place : places) {
                List<ZNode> gateways = client.getAllGatewayByPlaceId(place.getPlaceid());

                for (ZNode info : gateways) {

                    List<User> userInfos = client.getShareUser(info.getZnodeid());
                    for (User userinfo : userInfos) {
                        if (info.getStatus() != null) {
                            ShareUserInfo shareUserInfo = new ShareUserInfo();
                            GatewayInfo gateway = new GatewayInfo(info);

                            shareUserInfo.setAccount(userinfo.getAccount());
                            shareUserInfo.setMainaccount(userinfo.getAccount());
                            shareUserInfo.setName(userinfo.getName());
                            shareUserInfo.setGatewayid(gateway.getId());
                            shareUserInfo.setGatewayname(gateway.getAlias());
                            shareUserInfo.setStatus(gateway.getStatus());
                            sharUsreList.add(shareUserInfo);
                        }
                    }

                }

            }

        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }

        return result;
    }

    public ResultModel<SensorInfo> updateSensorName(String sensorId, String name) {

        ResultModel<SensorInfo> result = new ResultModel<SensorInfo>();

        AppClient client = null;
        try {
            client = this.getAppClient();
            client.updateSensorName(sensorId, name);
            ZNode znode = client.getZNodeById(sensorId);
            SensorInfo info = new SensorInfo(znode);
            result.setData(info);

        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }

        return result;
    }

    public ResultModel<Boolean> bindDevice(String sensorieee, Integer type) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();

        try {
            this.getGWClient().bind(sensorieee, type);
        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }

        return result;
    }

    public ResultModel<Boolean> unbindDevice(String sensorieee, Integer type) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            this.getGWClient().unbind(sensorieee, type);
        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }

        return result;
    }

    public ResultModel<Boolean> updateDeviceValue(String epieee, DeviceDataTypeKey attributeName, String value) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();

        AppClient client = null;
        try {
            client = this.getAppClient();
            HomeServer.getAppEngine().getEndPointByIeee(epieee).updateCurrvalue(attributeName, value);
            result.setData(true);

        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        } catch (JDSException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }

        return result;
    }

    public ResultModel<GatewayInfo> updateGateway(GatewayInfo gatewayInfo) {
        ResultModel<GatewayInfo> result = new ResultModel<GatewayInfo>();

        AppClient client = null;
        try {
            client = this.getAppClient();
            ZNode znode = client.getZNodeById(gatewayInfo.getId());
            GatewayInfo gateway = new GatewayInfo(znode);
            if (!gateway.getStatus().equals(gatewayInfo.getStatus())) {
                client.updateGatewayStatus(gatewayInfo.getId(), gatewayInfo.getStatus());
            } else {
                client.updateGateway(gatewayInfo.getSerialno(), gatewayInfo.getAlias(), gatewayInfo.getWbaccount(), gatewayInfo.getId(), gatewayInfo.getPlaceid());

            }
            client.getZNodeById(gatewayInfo.getId());
            result.setData(gateway);

        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }

        return result;
    }

    public ResultModel<SensorInfo> updateSensorValue(String sensorId, DeviceDataTypeKey attributeName, String value) {
        ResultModel<SensorInfo> result = new ResultModel<SensorInfo>();

        AppClient client = null;
        try {
            client = this.getAppClient();

            client.getZNodeById(sensorId).getEndPoint().updateCurrvalue(attributeName, value);
            ZNode znode = client.getZNodeById(sensorId);
            SensorInfo info = new SensorInfo(znode);
            result.setData(info);

        } catch (HomeException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrcode(e.getErrorCode());
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }

        return result;
    }

    public GWClient getGWClient() throws HomeException {
        if (this.gwClient == null) {

            try {
                JDSClientService client = (JDSClientService) EsbUtil.parExpression("$JDSGWC");
                gwClient = HomeServer.getInstance().getGWClient(client);
            } catch (Exception e) {
                throw new HomeException("not login!", 1005);
            }

            if (gwClient == null) {
                throw new HomeException("not login!", 1005);
            }
        }
        return gwClient;
    }

    public AppClient getAppClient() throws HomeException {
        JDSClientService client = (JDSClientService) EsbUtil.parExpression(JDSClientService.class);
        AppClient appClient = null;
        try {
            appClient = HomeServer.getInstance().getAppClient(client);
        } catch (JDSException e) {
            // appClient =CtAppClientImpl();
            //throw new HomeException("not login!", HomeException.NOTLOGIN);
        }
        if (appClient == null) {
            throw new HomeException("not login!", HomeException.NOTLOGIN);
        }
        return appClient;
    }

}
