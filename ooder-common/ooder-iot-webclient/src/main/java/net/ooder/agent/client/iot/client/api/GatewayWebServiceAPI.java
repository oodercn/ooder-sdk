package net.ooder.agent.client.iot.client.api;

import net.ooder.agent.client.iot.client.GatewayWebService;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;
import net.ooder.agent.client.iot.json.*;
import net.ooder.agent.client.iot.json.device.Gateway;
import net.ooder.agent.client.iot.json.device.Sensor;
import  net.ooder.annotation.MethodChinaName;
import  net.ooder.config.ListResultModel;
import  net.ooder.config.ResultModel;
import  net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/jds/iot/gatewayservice/")
@MethodChinaName(cname = "网关服务接口")
public class GatewayWebServiceAPI implements GatewayWebService {

    @MethodChinaName(cname = "批量添加传感器", returnStr = "addSensors($Sensor)")
    @RequestMapping(method = {RequestMethod.POST}, value = {"addSensors"})
    @ResponseBody
    @Override
    public ResultModel<List<Sensor>> addSensors(@RequestBody
                                                        List<Sensor> sensors) {
        return getGatewayWebService().addSensors(sensors);
    }


    @MethodChinaName(cname = "移除传感器", returnStr = "removeSensor($Sensor)")
    @RequestMapping(method = {RequestMethod.POST}, value = {"removeSensor"})
    @ResponseBody
    @Override
    public ResultModel<Boolean> removeSensor(@RequestBody
                                                     Sensor sensor) {
        return getGatewayWebService().removeSensor(sensor);
    }

    @MethodChinaName(cname = "传感器下线", returnStr = "sensorOffLine($Sensor)")
    @RequestMapping(method = {RequestMethod.POST}, value = {"sensorOffLine"})
    @ResponseBody
    @Override
    public ResultModel<Boolean> sensorOffLine(@RequestBody
                                                      Sensor sensor) {
        return getGatewayWebService().sensorOffLine(sensor);
    }

    @MethodChinaName(cname = "网关下线", returnStr = "gatewayOffLine($Gateway)")
    @RequestMapping(method = RequestMethod.POST, value = "gatewayOffLine")
    public @ResponseBody
    @Override
    ResultModel<Boolean> gatewayOffLine(@RequestBody Gateway gateway) {
        return getGatewayWebService().gatewayOffLine(gateway);
    }

    @MethodChinaName(cname = "网关在线", returnStr = "gatewayOnLine($Gateway)")
    @RequestMapping(method = RequestMethod.POST, value = "gatewayOnLine")
    @Override
    public @ResponseBody
    ResultModel<Boolean> gatewayOnLine(@RequestBody Gateway gateway) {

        return getGatewayWebService().gatewayOnLine(gateway);
    }


    @MethodChinaName(cname = "添加传感器", returnStr = "addSensorByType($R('gatewayid'),$R('type'),$R('serialno'))")
    @RequestMapping(method = {RequestMethod.POST}, value = {"addSensorByType"})
    @ResponseBody
    @Override
    public ResultModel<SensorInfo> addSensorByType(String gatewayid, Integer type, String serialno) {
        return null;
    }

    @MethodChinaName(cname = "添加传感器", returnStr = "AddSensor($R('gatewayid'),$R('serialno'))")
    @RequestMapping(method = {RequestMethod.POST}, value = {"addSensor"})
    @ResponseBody
    @Override
    public ResultModel<Boolean> addSensor(String gatewayid, String serialno) {
        return getGatewayWebService().addSensor(gatewayid, serialno);
    }


    @MethodChinaName(cname = "传感器在线", returnStr = "SensorOnLine($Sensor)")
    @RequestMapping(method = {RequestMethod.POST}, value = {"sensorOnLine"})
    @ResponseBody
    @Override
    public ResultModel<Boolean> sensorOnLine(@RequestBody
                                                     Sensor sensor) {
        return getGatewayWebService().sensorOnLine(sensor);
    }

    @MethodChinaName(cname = "同步传感器", returnStr = "SyncSensor($Sensor)")
    @RequestMapping(method = {RequestMethod.POST}, value = {"syncSensor"})
    @ResponseBody
    @Override
    public ResultModel<List<Sensor>> syncSensor(@RequestBody
                                                        List<Sensor> sensors) {
        return getGatewayWebService().syncSensor(sensors);

    }

    @MethodChinaName(cname = "切换传感器信息", returnStr = "SetOutLetSensorInfo($R('znodeId'),$R('vlaue'))")
    @RequestMapping(method = {RequestMethod.POST}, value = {"setOutLetSensorInfo"})
    @ResponseBody
    @Override
    public ResultModel<Boolean> setOutLetSensorInfo(String znodeId, Boolean vlaue) {
        return getGatewayWebService().setOutLetSensorInfo(znodeId, vlaue);

    }

    @MethodChinaName(cname = "共享网关", returnStr = "ShareGateway($R('serialno'),$R('mainaccount'),$R('mainpassword'),$R('placeId'))")
    @RequestMapping(method = {RequestMethod.POST}, value = {"shareGateway"})
    @ResponseBody
    @Override
    public ResultModel<PlaceInfo> shareGateway(String serialno, String mainaccount, String mainpassword, String placeId) {
        return getGatewayWebService().shareGateway(serialno, mainaccount, mainpassword, placeId);
    }

    @MethodChinaName(cname = "停止共享网关", returnStr = "StopShareGateway($R('gatewayid'))")
    @RequestMapping(method = {RequestMethod.POST}, value = {"stopShareGateway"})
    @ResponseBody
    @Override
    public ResultModel<Boolean> stopShareGateway(String gatewayid) {
        return getGatewayWebService().stopShareGateway(gatewayid);
    }

    @MethodChinaName(cname = "修改传感器名称", returnStr = "UpdateSensorName($R('sensorId'),$R('name'))")
    @RequestMapping(method = {RequestMethod.POST}, value = {"updateSensorName"})
    @ResponseBody
    public ResultModel<SensorInfo> updateSensorName(String sensorId, String name) {
        return getGatewayWebService().updateSensorName(sensorId, name);
    }

    @MethodChinaName(cname = "是否可以创建网关", returnStr = "CanCreateGateway($R('serialno'),$R('placeId'))")
    @RequestMapping(method = {RequestMethod.POST}, value = {"canCreateGateway"})
    @ResponseBody
    @Override
    public ResultModel<Boolean> canCreateGateway(String serialno, String placeId) {
        return getGatewayWebService().canCreateGateway(serialno, placeId);
    }

    @MethodChinaName(cname = "删除传感器", returnStr = "DeleteSensor($R('sensorId'))")
    @RequestMapping(method = {RequestMethod.POST}, value = {"deleteSensor"})
    @ResponseBody
    @Override
    public ResultModel<Boolean> deleteSensor(String sensorId) {
        return getGatewayWebService().deleteSensor(sensorId);
    }

    @MethodChinaName(cname = "根据网关ID获取传感器列表", returnStr = "GetAllSensorByGatewayId($R('gatewayId'))")
    @RequestMapping(method = {RequestMethod.POST}, value = {"getAllSensorByGatewayId"})
    @ResponseBody
    public ResultModel<List<SensorInfo>> getAllSensorByGatewayId(String gatewayId) {
        return getGatewayWebService().getAllSensorByGatewayId(gatewayId);
    }

    @MethodChinaName(cname = "根据ID获取传感器列表", returnStr = "GetAllSensorByPlaceId($R('placeId'))")
    @RequestMapping(method = {RequestMethod.POST}, value = {"getAllSensorByPlaceId"})
    @ResponseBody
    @Override
    public ResultModel<List<SensorInfo>> getAllSensorByPlaceId(String placeId) {
        return getGatewayWebService().getAllSensorByPlaceId(placeId);
    }

    @MethodChinaName(cname = "根据传感器编号获取传感器", returnStr = "GetSensorById($R('sensorId'))")
    @RequestMapping(method = {RequestMethod.POST}, value = {"getSensorById"})
    @ResponseBody
    public ResultModel<SensorInfo> getSensorById(String sensorId) {
        return getGatewayWebService().getSensorById(sensorId);
    }

    @MethodChinaName(cname = "根据网关ID获取传感器类型数据", returnStr = "GetSensorTypesByGatewayId($R('gatewayId'))")
    @RequestMapping(method = {RequestMethod.POST}, value = {"getSensorTypesByGatewayId"})
    @ResponseBody
    @Override
    public ResultModel<List<SensorTypeInfo>> getSensorTypesByGatewayId(String gatewayId) {
        return getGatewayWebService().getSensorTypesByGatewayId(gatewayId);
    }

    @MethodChinaName(cname = "根据编号获取传感器类型", returnStr = "GetSensorTypesByNo($R('typno'))")
    @RequestMapping(method = {RequestMethod.POST}, value = {"getSensorTypesByNo"})
    @ResponseBody
    public ResultModel<SensorTypeInfo> getSensorTypesByNo(Integer typno) {
        return getGatewayWebService().getSensorTypesByNo(typno);
    }

    @MethodChinaName(cname = "获取共享用户", returnStr = "getShareUserByGwId($R('gatewayId'))")
    @RequestMapping(method = {RequestMethod.POST}, value = {"getShareUserByGwId"})
    @ResponseBody
    @Override
    public ResultModel<List<UserInfo>> getShareUserByGwId(String gatewayId) {
        return getGatewayWebService().getShareUserByGwId(gatewayId);
    }

    @MethodChinaName(cname = "打开共享网关", returnStr = "OpenShareGateway($R('gatewayid'))")
    @RequestMapping(method = {RequestMethod.POST}, value = {"openShareGateway"})
    @ResponseBody
    public ResultModel<Boolean> openShareGateway(String gatewayid) {
        return getGatewayWebService().openShareGateway(gatewayid);
    }

    @MethodChinaName(cname = "更改WIFI网络配置", returnStr = "ChangeNetworkResponse($NetworkInfo)")
    @RequestMapping(method = {RequestMethod.POST}, value = {"changeNetworkResponse"})
    @ResponseBody
    @Override
    public ResultModel<NetworkInfo> changeNetworkResponse(@RequestBody
                                                                  NetworkInfo networkInfo) {
        return getGatewayWebService().changeNetworkResponse(networkInfo);
    }

    @MethodChinaName(cname = "根据IEEE获取传感器", returnStr = "GetSensorByIeees($R('sensoriees'))")

    @RequestMapping(method = {RequestMethod.POST}, value = {"getSensorByIeees"})
    @ResponseBody
    public ResultModel<List<SensorInfo>> getSensorByIeees(@RequestBody List<String> sensoriees) {
        return getGatewayWebService().getSensorByIeees(sensoriees);
    }

    @MethodChinaName(cname = "查询锁", returnStr = "SearchLock($PMSSensorSearch)")
    @RequestMapping(method = {RequestMethod.POST}, value = {"searchLock"})
    @ResponseBody
    @Override
    public ListResultModel<List<PMSSensorInfo>> searchLock(@RequestBody PMSSensorSearch search) {
        return getGatewayWebService().searchLock(search);
    }


    @MethodChinaName(cname = "绑定设备")
    @RequestMapping(method = {RequestMethod.POST}, value = {"bindDevice"})
    @ResponseBody
    @Override
    public ResultModel<Boolean> bindDevice(String sensorieee, Integer type) {
        return getGatewayWebService().bindDevice(sensorieee, type);
    }

    @MethodChinaName(cname = "解绑设备")
    @RequestMapping(method = {RequestMethod.POST}, value = {"unbindDevice"})
    @ResponseBody
    @Override
    public ResultModel<Boolean> unbindDevice(String sensorieee, Integer type) {
        return getGatewayWebService().unbindDevice(sensorieee, type);
    }

    @MethodChinaName(cname = "添加网关")
    @RequestMapping(method = {RequestMethod.POST}, value = {"addGateway"})
    @ResponseBody
    @Override
    public ResultModel<GatewayInfo> addGateway(String serialno, String placeid) {
        return getGatewayWebService().addGateway(serialno, placeid);
    }

    @MethodChinaName(cname = "绑定网关到房间")
    @RequestMapping(method = {RequestMethod.POST}, value = {"bindingGateway"})
    @ResponseBody
    @Override
    public ResultModel<GatewayInfo> bindingGateway(String wbaccount, String serialno) {
        return getGatewayWebService().bindingGateway(wbaccount, serialno);
    }

    @MethodChinaName(cname = "绑定网关到房间")
    @RequestMapping(method = {RequestMethod.POST}, value = {"createGateway"})
    @ResponseBody
    @Override
    public ResultModel<GatewayInfo> createGateway(GatewayInfo gatewayInfo) {
        return getGatewayWebService().createGateway(gatewayInfo);
    }

    @MethodChinaName(cname = "删除网关")
    @RequestMapping(method = {RequestMethod.POST}, value = {"deleteGateway"})
    @ResponseBody
    @Override
    public ResultModel<Boolean> deleteGateway(String gatewayId) {
        return getGatewayWebService().deleteGateway(gatewayId);
    }

    public ResultModel<List<GatewayInfo>> getAllGatewayInfos(String placeId) {
        return getGatewayWebService().getAllGatewayInfos(placeId);
    }

    @MethodChinaName(cname = "获取设备属性")
    @RequestMapping(method = {RequestMethod.POST}, value = {"=getDeviceValue"})
    @ResponseBody
    @Override
    public ResultModel<String> getDeviceValue(String ieee, DeviceDataTypeKey attributeName) {
        return getGatewayWebService().getDeviceValue(ieee, attributeName);
    }

    @MethodChinaName(cname = "获取网关信息")
    @RequestMapping(method = {RequestMethod.POST}, value = {"getGatewayById"})
    @ResponseBody
    @Override
    public ResultModel<GatewayInfo> getGatewayById(String gatewayId) {
        return getGatewayWebService().getGatewayById(gatewayId);
    }

    @MethodChinaName(cname = "获取网关信息")
    @RequestMapping(method = {RequestMethod.POST}, value = {"getGatewayInfoBySN"})
    @ResponseBody
    @Override
    public ResultModel<GatewayInfo> getGatewayInfoBySN(String gatewaySN) {
        return getGatewayWebService().getGatewayInfoBySN(gatewaySN);
    }

    public ResultModel<List<SensorInfo>> getSensorInfoByIds(List<String> sensorIds) {
        return getGatewayWebService().getSensorInfoByIds(sensorIds);
    }

    @MethodChinaName(cname = "获取网关共享用户")
    @RequestMapping(method = {RequestMethod.POST}, value = {"getShareUser"})
    @ResponseBody
    @Override
    public ResultModel<List<ShareUserInfo>> getShareUser() {
        return getGatewayWebService().getShareUser();
    }


    public ResultModel<Boolean> updateDeviceValue(String deviceId, DeviceDataTypeKey attributeName, String value) {
        return getGatewayWebService().updateDeviceValue(deviceId, attributeName, value);
    }

    @MethodChinaName(cname = "更新网关信息")
    @RequestMapping(method = {RequestMethod.POST}, value = {"updateGateway"})
    @ResponseBody
    @Override
    public ResultModel<GatewayInfo> updateGateway(GatewayInfo gatewayInfo) {
        return getGatewayWebService().updateGateway(gatewayInfo);
    }

    @MethodChinaName(cname = "更新传感器信息")
    @RequestMapping(method = {RequestMethod.POST}, value = {"updateSensorValue"})
    @ResponseBody
    @Override
    public ResultModel<SensorInfo> updateSensorValue(String sensorId, DeviceDataTypeKey attributeName, String value) {
        return getGatewayWebService().updateSensorValue(sensorId, attributeName, value);
    }

    GatewayWebService getGatewayWebService() {
        return (GatewayWebService) EsbUtil.parExpression(GatewayWebService.class);
    }


}
