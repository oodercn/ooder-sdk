package net.ooder.agent.client.iot.client.api;

import net.ooder.agent.client.iot.client.DataWebService;
import net.ooder.agent.client.iot.json.AlarmMessageInfo;
import net.ooder.agent.client.iot.json.SensorHistoryDataInfo;
import net.ooder.agent.client.iot.json.device.SensorData;
import  net.ooder.annotation.MethodChinaName;
import  net.ooder.config.ResultModel;
import  net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/jds/iot/dataservice/")
@MethodChinaName(cname = "网关数据")
public class DataWebServiceAPI implements DataWebService {

    @MethodChinaName(cname = "添加告警", returnStr = "AddAlarm($SensorData)")

    @RequestMapping(method = {RequestMethod.POST}, value = {"addAlarm"})
    @ResponseBody
    public ResultModel<Boolean> addAlarm(@RequestBody SensorData sensor) {

        return getDataWebService().addAlarm(sensor);
    }

    @MethodChinaName(cname = "添加数据", returnStr = "AddData($SensorData)")

    @RequestMapping(method = {RequestMethod.POST}, value = {"ddData"})
    @ResponseBody
    public ResultModel<Boolean> addData(@RequestBody SensorData sensor) {
        return getDataWebService().addData(sensor);
    }

    @MethodChinaName(cname = "根据门店ID获取告警信息", returnStr = "GetAlarmMessageByPlaceId($R('placeId'))")

    @RequestMapping(method = {RequestMethod.POST}, value = {"getAlarmMessageByPlaceId"})
    @ResponseBody
    public ResultModel<List<AlarmMessageInfo>> getAlarmMessageByPlaceId(
            String placeId) {

        return getDataWebService().getAlarmMessageByPlaceId(placeId);
    }

    @MethodChinaName(cname = "获取传感器历史数据", returnStr = "GetHistorySennsorData($R('sensorId'),$R('starttime'),$R('endtime'),$R('currentIndex'),$R('pageSize'))")

    @RequestMapping(method = {RequestMethod.POST}, value = {"getHistorySennsorData"})
    @ResponseBody
    public ResultModel<List<SensorHistoryDataInfo>> getHistorySensorData(
            String sensorId, Date starttime, Date endtime,
            Integer currentIndex, Integer pageSize) {
        return getDataWebService().getHistorySensorData(sensorId, starttime, endtime, currentIndex, pageSize);
    }

    @MethodChinaName(cname = "获取传感器最近历史数据", returnStr = "GetLastSensorHistoryData($R('sensorId'),$R('currentIndex'),$R('pageSize'))")

    @RequestMapping(method = {RequestMethod.POST}, value = {"getLastSensorHistoryData"})
    @ResponseBody
    public ResultModel<List<SensorHistoryDataInfo>> getLastSensorHistoryData(
            String sensorId, Integer currentIndex, Integer pageSize) {
        return getDataWebService().getLastSensorHistoryData(sensorId, currentIndex, pageSize);


    }


    DataWebService getDataWebService() {
        return (DataWebService) EsbUtil.parExpression(DataWebService.class);
    }

}
