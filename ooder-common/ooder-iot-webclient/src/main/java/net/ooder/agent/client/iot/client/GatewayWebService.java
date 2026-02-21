package net.ooder.agent.client.iot.client;


import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;
import net.ooder.agent.client.iot.json.*;
import net.ooder.agent.client.iot.json.device.Gateway;
import net.ooder.agent.client.iot.json.device.Sensor;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;

import java.util.List;

public interface GatewayWebService {


    /**
     * 添加传感器
     *
     * @param sensors
     * @throws HomeException
     */
    ResultModel<List<Sensor>> addSensors(List<Sensor> sensors);

    /**
     * 同步传感器列表
     * （网关报送设备列表时调用）
     *
     * @param sensors
     * @param sensors
     * @throws HomeException
     */
    ResultModel<List<Sensor>> syncSensor(List<Sensor> sensors);


    /**
     * 删除传感器
     *
     * @param sensor
     * @throws HomeException
     */
    ResultModel<Boolean> removeSensor(Sensor sensor);

    /**
     * @param sensor
     * @throws HomeException
     */

    ResultModel<Boolean> sensorOnLine(Sensor sensor);

    /**
     * @param sensor
     * @throws HomeException
     */
    ResultModel<Boolean> sensorOffLine(Sensor sensor);


    /**
     * @param gateway
     * @throws HomeException
     */
    ResultModel<Boolean> gatewayOffLine(Gateway gateway);


    /**
     * @param gateway
     * @throws HomeException
     */
    ResultModel<Boolean> gatewayOnLine(Gateway gateway);


    /**
     * 添加指定类型传感器
     *
     * @param gatewayid
     * @param serialno
     * @return
     * @throws HomeException
     */

    ResultModel<SensorInfo> addSensorByType(String gatewayid, Integer type, String serialno);


    /**
     * 添加传感器
     *
     * @param gatewayid
     * @param serialno
     * @return
     * @throws HomeException
     */

    ResultModel<Boolean> addSensor(String gatewayid, String serialno);


    /**
     * 修改传感器信息
     *
     * @param sensorId
     * @param name
     * @return
     * @throws HomeException
     */
    ResultModel<SensorInfo> updateSensorName(String sensorId, String name);

    /**
     * 删除传感器
     *
     * @param sensorId
     * @throws HomeException
     */
    ResultModel<Boolean> deleteSensor(String sensorId);

    /**
     * 查询传感器
     *
     * @param sensorId
     * @return
     * @throws HomeException
     */
    ResultModel<SensorInfo> getSensorById(String sensorId);


    /**
     * 查询设备信息
     *
     * @param sensoriees
     * @return
     * @throws HomeException
     */
    ResultModel<List<SensorInfo>> getSensorByIeees(List<String> sensoriees);


    /**
     * 查询门锁信息
     *
     * @param search
     * @return
     */
    ListResultModel<List<PMSSensorInfo>> searchLock(PMSSensorSearch search);


    /**
     * 查询所有传感器
     * 返回网关下所有传感器
     *
     * @param gatewayId
     * @return
     * @throws HomeException
     */
    ResultModel<List<SensorInfo>> getAllSensorByGatewayId(String gatewayId);


    /**
     * 查询所有传感器
     * 返回该住所下所有传感器
     *
     * @param placeId
     * @return
     * @throws HomeException
     */
    ResultModel<List<SensorInfo>> getAllSensorByPlaceId(String placeId);


    /**
     * 获取所有传感器的类型信息
     *
     * @return
     * @throws HomeException
     */
    ResultModel<List<SensorTypeInfo>> getSensorTypesByGatewayId(String gatewayId);


    /**
     * 获取指定编码的传感器类型
     *
     * @return
     */
    ResultModel<SensorTypeInfo> getSensorTypesByNo(Integer typno);

    /**
     * @param znodeId
     * @param vlaue
     * @throws HomeException
     */
    ResultModel<Boolean> setOutLetSensorInfo(String znodeId, Boolean vlaue);


    /**
     * 获取共享用户信息
     *
     * @param gatewayId
     * @return
     */
    ResultModel<List<UserInfo>> getShareUserByGwId(String gatewayId);


    /**
     * 更新分享状态
     *
     * @param gatewayid
     * @return
     */
    ResultModel<Boolean> stopShareGateway(String gatewayid);

    /**
     * 更新分享状态
     *
     * @param gatewayid
     * @return
     */
    ResultModel<Boolean> openShareGateway(String gatewayid);

    /**
     * 是否允许创建网关
     *
     * @param serialno
     * @return
     * @throws HomeException
     */
    ResultModel<Boolean> canCreateGateway(String serialno, String placeId);

    /**
     * 共享网关
     *
     * @param serialno
     * @param mainaccount
     * @param mainpassword
     * @throws HomeException
     */
    ResultModel<PlaceInfo> shareGateway(String serialno, String mainaccount, String mainpassword, String placeId);


    /**
     * 传感器配对
     */
    ResultModel<Boolean> bindDevice(String sensorieee, Integer type);

    /**
     * 传感器解配
     */
    ResultModel<Boolean> unbindDevice(String sensorieee, Integer type);


    /**
     * 更新网关网络信息
     *
     * @param networkInfo
     * @return
     */
    ResultModel<NetworkInfo> changeNetworkResponse(NetworkInfo networkInfo);

    ResultModel<GatewayInfo> getGatewayById(String gatewayId);

    ResultModel<GatewayInfo> getGatewayInfoBySN(String gatewaySN);

    ResultModel<SensorInfo> updateSensorValue(String sensorId, DeviceDataTypeKey attributeName, String value);

    ResultModel<Boolean> updateDeviceValue(String deviceId, DeviceDataTypeKey attributeName, String value);

    ResultModel<GatewayInfo> createGateway(GatewayInfo gatewayInfo);

    ResultModel<GatewayInfo> bindingGateway(String wbaccount, String serialno);

    ResultModel<Boolean> deleteGateway(String gatewayId);

    ResultModel<List<GatewayInfo>> getAllGatewayInfos(String placeId);

    ResultModel<GatewayInfo> updateGateway(GatewayInfo gatewayInfo);

    ResultModel<String> getDeviceValue(String ieee, DeviceDataTypeKey attributeName);

    ResultModel<List<SensorInfo>> getSensorInfoByIds(List<String> sensorIds);

    ResultModel<GatewayInfo> addGateway(String serialno, String placeid);

    ResultModel<List<ShareUserInfo>> getShareUser();


}
