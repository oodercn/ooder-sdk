package net.ooder.agent.client.iot.client.api;

import net.ooder.agent.client.iot.client.AppWebService;
import net.ooder.agent.client.iot.json.*;
import  net.ooder.annotation.MethodChinaName;
import  net.ooder.config.ResultModel;
import  net.ooder.jds.core.esb.EsbUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/jds/iot/appwebservice/")
@MethodChinaName(cname = "IOT 手机接口", returnStr = "AddOutLetSensorScene($SensorSceneInfo)")
public class AppWebServiceAPI implements AppWebService {

    @MethodChinaName(cname = "添加场景", returnStr = "addOutLetSensorScene($SensorSceneInfo)")

    @RequestMapping(method = RequestMethod.POST, value = "addOutLetSensorScene")
    public @ResponseBody
    ResultModel<Boolean> addOutLetSensorScene(@RequestBody SensorSceneInfo info) {
        return getAppWebService().addOutLetSensorScene(info);
    }

    @MethodChinaName(cname = "改变告警状态", returnStr = "changeStatusAlarm($R('userName'),$R('status'))")

    @RequestMapping(method = RequestMethod.POST, value = "changeStatusAlarm")
    public @ResponseBody
    ResultModel<AlarmInfo> changeStatusAlarm(String alarmid, Integer status) {
        return getAppWebService().changeStatusAlarm(alarmid, status);
    }

    @MethodChinaName(cname = "创建告警", returnStr = "createAlarm($AlarmInfo)")

    @RequestMapping(method = RequestMethod.POST, value = "createAlarm")
    public @ResponseBody
    ResultModel<AlarmInfo> createAlarm(@RequestBody AlarmInfo alarm) {
        return getAppWebService().createAlarm(alarm);
    }

    @MethodChinaName(cname = "创建门店", returnStr = "createPlace($PlaceInfo)")

    @RequestMapping(method = RequestMethod.POST, value = "createPlace")
    public @ResponseBody
    ResultModel<PlaceInfo> createPlace(@RequestBody PlaceInfo place) {
        return getAppWebService().createPlace(place);
    }

    @MethodChinaName(cname = "删除告警", returnStr = "deleteAlarm($R('alarmid'))")

    @RequestMapping(method = RequestMethod.POST, value = "deleteAlarm")
    public @ResponseBody
    ResultModel<Boolean> deleteAlarm(String alarmid) {
        return getAppWebService().deleteAlarm(alarmid);
    }

    @MethodChinaName(cname = "删除房间", returnStr = "deleteArea($R('areaInfoId'),$R('placeId'))")

    @RequestMapping(method = RequestMethod.POST, value = "deleteArea")
    public @ResponseBody
    ResultModel<Boolean> deleteArea(String areaInfoId, String placeId) {
        return getAppWebService().deleteArea(areaInfoId, placeId);
    }

    @MethodChinaName(cname = "删除切换场景", returnStr = "deleteOutLetSensorScene($R('sensorSceneId'))")

    @RequestMapping(method = RequestMethod.POST, value = "deleteOutLetSensorScene")
    public @ResponseBody
    ResultModel<Boolean> deleteOutLetSensorScene(String sensorSceneId) {
        return getAppWebService().deleteOutLetSensorScene(sensorSceneId);
    }

    @MethodChinaName(cname = "删除门店", returnStr = "deletePlace($R('placeId'))")

    @RequestMapping(method = RequestMethod.POST, value = "deletePlace")
    public @ResponseBody
    ResultModel<Boolean> deletePlace(String placeId) {
        return getAppWebService().deletePlace(placeId);
    }

    @MethodChinaName(cname = "根据ID获取告警信息", returnStr = "getAlarmById($R('alarmId'))")

    @RequestMapping(method = RequestMethod.POST, value = "getAlarmById")
    public @ResponseBody
    ResultModel<AlarmInfo> getAlarmById(String alarmId) {
        return getAppWebService().getAlarmById(alarmId);
    }

    @MethodChinaName(cname = "根据传感器编号获取告警信息", returnStr = "getAlarmBySensorId($R('sensorId'))")

    @RequestMapping(method = RequestMethod.POST, value = "getAlarmBySensorId")
    public @ResponseBody
    ResultModel<List<AlarmInfo>> getAlarmBySensorId(String sensorId) {
        return getAppWebService().getAlarmBySensorId(sensorId);
    }

    @MethodChinaName(cname = "根据门店ID获取房间数据", returnStr = "getAllAreaByPlaceId($R('placeId'))")

    @RequestMapping(method = RequestMethod.POST, value = "getAllAreaByPlaceId")
    public @ResponseBody
    ResultModel<List<AreaInfo>> getAllAreaByPlaceId(String placeId) {
        return getAppWebService().getAllAreaByPlaceId(placeId);
    }

    @MethodChinaName(cname = "获取全部门店", returnStr = "getAllPlace()")

    @RequestMapping(method = RequestMethod.POST, value = "getAllPlace")
    public @ResponseBody
    ResultModel<List<PlaceInfo>> getAllPlace() {
        return getAppWebService().getAllPlace();
    }

    @MethodChinaName(cname = "根据ID获取房间", returnStr = "getAreasById($R('areaId'))")

    @RequestMapping(method = RequestMethod.POST, value = "getAreasById")
    public @ResponseBody
    ResultModel<AreaInfo> getAreasById(String areaId) {
        return getAppWebService().getAreasById(areaId);
    }

    @MethodChinaName(cname = "根据ID获取房间数据", returnStr = "getAreasByIds($R('ids'))")

    @RequestMapping(method = RequestMethod.POST, value = "getAreasByIds")
    public @ResponseBody
    ResultModel<List<AreaInfo>> getAreasByIds(@RequestBody List<String> ids) {
        return getAppWebService().getAreasByIds(ids);
    }

    @MethodChinaName(cname = "获取传感器ID", returnStr = "getIndexSensorIds($R('placeId'),$R('isShow'))")

    @RequestMapping(method = RequestMethod.POST, value = "getIndexSensorIds")
    public @ResponseBody
    ResultModel<List<String>> getIndexSensorIds(String placeId, int isShow) {
        return getAppWebService().getIndexSensorIds(placeId, isShow);
    }

    @MethodChinaName(cname = "获取灯光场景信息", returnStr = "GetLightSensorSceneInfo($R('sensorId'))")

    @RequestMapping(method = RequestMethod.POST, value = "getLightSensorSceneInfo")
    public @ResponseBody
    ResultModel<List<SensorSceneInfo>> getLightSensorSceneInfo(String sensorId) {
        return getAppWebService().getLightSensorSceneInfo(sensorId);
    }

    @MethodChinaName(cname = "获取主要用户信息", returnStr = "getMainUserInfo($R('serialno'))")

    @RequestMapping(method = RequestMethod.POST, value = "getMainUserInfo")
    public @ResponseBody
    ResultModel<UserInfo> getMainUserInfo(String serialno) {
        return getAppWebService().getMainUserInfo(serialno);
    }

    @MethodChinaName(cname = "根据ID获取门店", returnStr = "getPlaceById($R('placeId'))")

    @RequestMapping(method = RequestMethod.POST, value = "getPlaceById")
    public @ResponseBody
    ResultModel<PlaceInfo> getPlaceById(String placeId) {
        return getAppWebService().getPlaceById(placeId);
    }

    @MethodChinaName(cname = "根据ID获取门店数据", returnStr = "getPlaceByIds($R('placeId'))")

    @RequestMapping(method = RequestMethod.POST, value = "getPlaceByIds")
    public @ResponseBody
    ResultModel<List<PlaceInfo>> getPlaceByIds(@RequestBody List<String> placeIds) {
        return getAppWebService().getPlaceByIds(placeIds);
    }

    @MethodChinaName(cname = "获取版本号", returnStr = "getVersion($R('osType'),$R('currVersion'))")

    @RequestMapping(method = RequestMethod.POST, value = "getVersion")
    public @ResponseBody
    ResultModel<String> getVersion(String osType, String currVersion) {
        return getAppWebService().getVersion(osType, currVersion);
    }

    @MethodChinaName(cname = "保存场景", returnStr = "saveSensorShow($R('sensorIds'),$R('placeId'))")

    @RequestMapping(method = RequestMethod.POST, value = "saveSensorShow")
    public @ResponseBody
    ResultModel<Boolean> saveSensorShow(String sensorIds, String placeId) {
        return getAppWebService().saveSensorShow(sensorIds, placeId);
    }

    @MethodChinaName(cname = "创建房间", returnStr = "createArea($AreaInfo)")

    @RequestMapping(method = RequestMethod.POST, value = "createArea")
    public @ResponseBody
    ResultModel<AreaInfo> createArea(@RequestBody AreaInfo areaInfo) {
        return getAppWebService().createArea(areaInfo);
    }

    @MethodChinaName(cname = "更新告警", returnStr = "updateAlarm($AlarmInfo)")

    @RequestMapping(method = RequestMethod.POST, value = "updateAlarm")
    public @ResponseBody
    ResultModel<AlarmInfo> updateAlarm(@RequestBody AlarmInfo alarmInfo) {
        return getAppWebService().updateAlarm(alarmInfo);
    }

    @MethodChinaName(cname = "更新房间", returnStr = "updateArea($AreaInfo)")

    @RequestMapping(method = RequestMethod.POST, value = "updateArea")
    public @ResponseBody
    ResultModel<AreaInfo> updateArea(@RequestBody AreaInfo areaInfo) {
        return getAppWebService().updateArea(areaInfo);
    }

    @MethodChinaName(cname = "切换传感器场景状态", returnStr = "updateOutLetSensorSceneStatus($R('sensorSceneId'),$R('status'))")

    @RequestMapping(method = RequestMethod.POST, value = "updateOutLetSensorSceneStatus")
    public @ResponseBody
    ResultModel<Boolean> updateOutLetSensorSceneStatus(String sensorSceneId, int status) {
        return getAppWebService().updateOutLetSensorSceneStatus(sensorSceneId, status);
    }

    @MethodChinaName(cname = "切换传感器场景", returnStr = "updateOutLetSensorScene($SensorSceneInfo)")

    @RequestMapping(method = RequestMethod.POST, value = "updateOutLetSensorScene")
    public @ResponseBody
    ResultModel<Boolean> updateOutLetSensorScene(@RequestBody SensorSceneInfo info) {
        return getAppWebService().updateOutLetSensorScene(info);
    }

    @MethodChinaName(cname = "更新门店", returnStr = "updatePlace($PlaceInfo)")

    @RequestMapping(method = RequestMethod.POST, value = "updatePlace")
    public @ResponseBody
    ResultModel<Boolean> updatePlace(@RequestBody PlaceInfo place) {
        return getAppWebService().updatePlace(place);
    }

    AppWebService getAppWebService() {
        return (AppWebService) EsbUtil.parExpression(AppWebService.class);
    }


}
