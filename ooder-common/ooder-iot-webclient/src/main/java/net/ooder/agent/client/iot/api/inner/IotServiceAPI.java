package net.ooder.agent.client.iot.api.inner;

import net.ooder.agent.client.iot.*;
import net.ooder.agent.client.iot.enums.DeviceStatus;
import net.ooder.agent.client.iot.enums.ZNodeZType;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.annotation.MethodChinaName;
import  net.ooder.common.ContextType;
import  net.ooder.common.TokenType;
import  net.ooder.config.ListResultModel;
import  net.ooder.config.ResultModel;
import  net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@MethodChinaName(cname = "IOT管理端接口")
@Controller
@RequestMapping("/jds/iot/iotservice/")
@EsbBeanAnnotation(dataType = ContextType.Server,tokenType = TokenType.admin)
public class IotServiceAPI implements IotService {

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getSensorsType")
    public @ResponseBody
    ListResultModel<List<Sensortype>> getSensorsType() {
        return getService().getSensorsType();
    }


    @RequestMapping(method = RequestMethod.POST, value = "getDeviceById")
    public @ResponseBody
    ResultModel<Device> getDeviceById(String deviceId) {
        return getService().getDeviceById(deviceId);
    }


    @RequestMapping(method = RequestMethod.POST, value = "getEndPointById")
    public @ResponseBody
    ResultModel<DeviceEndPoint> getEndPointById(String endpointId) {
        return getService().getEndPointById(endpointId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "getZNodeById")
    public @ResponseBody
    ResultModel<ZNode> getZNodeById(String znodeId) {
        return getService().getZNodeById(znodeId);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getDeviceByIeee")
    public @ResponseBody
    ResultModel<Device> getDeviceByIeee(String deviceieee) {
        return getService().getDeviceByIeee(deviceieee);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getEndPointByIeee")
    public @ResponseBody
    ResultModel<DeviceEndPoint> getEndPointByIeee(String epieee) {
        return getService().getEndPointByIeee(epieee);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "createRootZNode")
    public @ResponseBody
    ResultModel<ZNode> createRootZNode(String endPointId, String placeid, String personId, ZNodeZType type) {

        return this.getService().createRootZNode(endPointId, placeid, personId, type);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "createChildZNode")
    public @ResponseBody
    ResultModel<ZNode> createChildZNode(String parentNodeId, String endPointId) {
        return this.getService().createChildZNode(parentNodeId, endPointId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "createDevice")
    public @ResponseBody
    ResultModel<Device> createDevice(final String serialno, final String macaddress, Integer deviceType, String factoryName, String gwDeviceId) {
        return this.getService().createDevice(serialno, macaddress, deviceType, factoryName, gwDeviceId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "removeChildDevice")
    public @ResponseBody
    ResultModel<Boolean> removeChildDevice(String bindingAccount, String deviceid) {
        return this.getService().removeChildDevice(bindingAccount, deviceid);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "updateNodeStatus")
    public @ResponseBody
    ResultModel<Boolean> updateNodeStatus(String znodeid, DeviceStatus status) {
        return this.getService().updateNodeStatus(znodeid, status);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "deleteNode")
    public @ResponseBody
    ResultModel<Boolean> deleteNode(String nodeId) {
        return getService().deleteNode(nodeId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "updateZNode")
    public @ResponseBody
    ResultModel<Boolean> updateZNode(@RequestBody ZNode znode) {
        return getService().updateZNode(znode);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "updateDevice")
    public @ResponseBody
    ResultModel<Boolean> updateDevice(@RequestBody Device device) {
        return getService().updateDevice(device);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "updateEndPoint")
    public @ResponseBody
    ResultModel<Boolean> updateEndPoint(@RequestBody DeviceEndPoint endPoint) {
        return getService().updateEndPoint(endPoint);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "deleteDevice")
    public @ResponseBody
    ResultModel<Boolean> deleteDevice(String deviceid) {
        return getService().deleteDevice(deviceid);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "clearDevice")
    public @ResponseBody
    ResultModel<Boolean> clearDevices(@RequestBody String[] deviceids) {
        return getService().clearDevices(deviceids);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getAlarmById")
    public @ResponseBody
    ResultModel<Alarm> getAlarmById(String alarmid) {

        return getService().getAlarmById(alarmid);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "updateSensorType")
    public @ResponseBody
    ResultModel<Sensortype> updateSensorType(@RequestBody Sensortype sensortype) {
        return getService().updateSensorType(sensortype);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "deleteSensorType")
    public @ResponseBody
    ResultModel<Boolean> deleteSensorType(Integer typeno) {
        return getService().deleteSensorType(typeno);
    }


    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getSensorIdsByBindAccount")
    public @ResponseBody
    ListResultModel<List<String>> getSensorIdsByBindAccount(String account) {
        return getService().getSensorIdsByBindAccount(account);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "createRootDevice")
    public @ResponseBody
    ResultModel<Device> createRootDevice(String deviceId, String serialno, String macno, String systemCode, String currVersion) {
        return getService().createRootDevice(deviceId, serialno, macno, systemCode, currVersion);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "createEndPoint")
    public @ResponseBody
    ResultModel<DeviceEndPoint> createEndPoint(String sensorieee, String epieee, String ep, Integer sensorType, String name) {
        return getService().createEndPoint(sensorieee, epieee, ep, sensorType, name);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "updateCurrvalue")
    public @ResponseBody
    ResultModel<Boolean> updateCurrvalue(String endPointId, String type, String currvalue) {
        return getService().updateCurrvalue(endPointId, type, currvalue);
    }

    public IotService getService() {
        IotService service = (IotService) EsbUtil.parExpression(IotService.class);
        return service;
    }


}
