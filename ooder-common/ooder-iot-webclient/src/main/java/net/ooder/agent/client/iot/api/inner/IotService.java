package net.ooder.agent.client.iot.api.inner;

import net.ooder.agent.client.iot.*;
import net.ooder.agent.client.iot.enums.DeviceStatus;
import net.ooder.agent.client.iot.enums.ZNodeZType;
import  net.ooder.config.ListResultModel;
import  net.ooder.config.ResultModel;
import java.util.List;

public interface IotService {


    /**
     * @param deviceId
     * @return
     */
    public ResultModel<Device> getDeviceById(String deviceId);

    /**
     * @param deviceieee
     * @deviceieee
     */
    public ResultModel<Device> getDeviceByIeee(String deviceieee);


    /**
     * @param endPointId
     * @return
     */
    public ResultModel<DeviceEndPoint> getEndPointById(String endPointId);

    /**
     * @param epieee
     * @return
     */
    public ResultModel<DeviceEndPoint> getEndPointByIeee(String epieee);

    /**
     * @param znodeId
     * @return
     */
    public ResultModel<ZNode> getZNodeById(String znodeId);


    /**
     * @return
     */
    public ListResultModel<List<Sensortype>> getSensorsType();


    public ResultModel<ZNode> createRootZNode(String endPointId, String placeid, String personId, ZNodeZType type);

    public ResultModel<ZNode> createChildZNode(String parenetNodeId, String endPointId);

    public ResultModel<Device> createDevice(String serialno, String serialno1, Integer deviceType, String factoryName, String gwDeviceId);

    public ResultModel<Boolean> removeChildDevice(String bindingAccount, String deviceid);

    public ResultModel<Boolean> updateNodeStatus(String znodeid, DeviceStatus status);

    public ResultModel<Boolean> deleteNode(String nodeId);

    public ResultModel<Boolean> updateZNode(ZNode znode);

    public ResultModel<Boolean> updateDevice(Device device);

    public ResultModel<Boolean> updateEndPoint(DeviceEndPoint endPoint);

    public ResultModel<Boolean> deleteDevice(String deviceid);


    public ResultModel<Boolean> clearDevices(String[] deviceids);

    public ResultModel<Alarm> getAlarmById(String alarmid);

    public ResultModel<Sensortype> updateSensorType(Sensortype sensortype);

    public ResultModel<Boolean> deleteSensorType(Integer typeno);

    public ListResultModel<List<String>> getSensorIdsByBindAccount(String account);


    ResultModel<Device> createRootDevice(String deviceId, String serialno, String macno, String systemCode, String currVersion);

    ResultModel<DeviceEndPoint> createEndPoint(String sensorieee, String epieee, String ep, Integer sensorType, String name);

    ResultModel<Boolean> updateCurrvalue(String endPointId, String type, String currvalue);
}
