package net.ooder.agent.client.iot.api.inner;

import net.ooder.agent.client.iot.*;
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
import java.util.Set;

@MethodChinaName(cname = "房源管理接口")
@Controller
@RequestMapping("/jds/iot/home/")
public class HomeServiceAPI implements HomeService {

    @MethodChinaName(cname = "创建房间", returnStr = "createArea($Area)")

    @RequestMapping(method = RequestMethod.POST, value = "createArea")
    @Override
    public @ResponseBody
    ResultModel<Area> createArea(String name, String placeid) {
        return getHomeService().createArea(name, placeid);
    }

    @MethodChinaName(cname = "删除房间", returnStr = "deleteArea($Area)")
    @Override
    @RequestMapping(method = RequestMethod.POST, value = "deleteArea")
    public @ResponseBody
    ResultModel<Boolean> deleteArea(String areaId) {
        return getHomeService().deleteArea(areaId);
    }

    @MethodChinaName(cname = "根据房源ID获取房间号", returnStr = "getAreaIdsbyPlaceId($R('placeId'))")
    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getAreaIdsbyPlaceId")
    public @ResponseBody
    ListResultModel<Set<String>> getAreaIdsbyPlaceId(String placeId) {
        return getHomeService().getAreaIdsbyPlaceId(placeId);
    }

    @MethodChinaName(cname = "获取房间信息", returnStr = "getAreas($R('areaIdList'))")
    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getAreas")
    public @ResponseBody
    ListResultModel<List<Area>> getAreas(@RequestBody String[] areaIds) {
        return getHomeService().getAreas(areaIds);
    }


    @MethodChinaName(cname = "修改房间", returnStr = "updateArea($Area)")
    @Override
    @RequestMapping(method = RequestMethod.POST, value = "updateArea")
    public @ResponseBody
    ResultModel<Boolean> updateArea(@RequestBody Area area) {
        return getHomeService().updateArea(area);
    }


    @MethodChinaName(cname = "删除房源", returnStr = "deletePlace($placeId)")

    @RequestMapping(method = RequestMethod.POST, value = "deletePlace")
    public @ResponseBody
    ResultModel<Boolean> deletePlace(String placeId) {

        return getHomeService().deletePlace(placeId);

    }


    @MethodChinaName(cname = "获取房源编号", returnStr = "getPlaceIds()")
    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPlaceIds")
    public @ResponseBody
    ListResultModel<Set<String>> getPlaceIds() {
        return getHomeService().getPlaceIds();
    }

    @MethodChinaName(cname = "获取房源信息", returnStr = "GetPlace($R('placeId'))")
    @Override
    @RequestMapping(method = RequestMethod.POST, value = "getPlaceById")
    public @ResponseBody
    ResultModel<Place> getPlaceById(String placeId) {
        return getHomeService().getPlaceById(placeId);
    }


    @MethodChinaName(cname = "获取全部房源信息", returnStr = "loadPlaceList($R('placeIdList'))")
    @Override
    @RequestMapping(method = RequestMethod.POST, value = "loadPlaceList")
    public @ResponseBody
    ListResultModel<List<Place>> loadPlaceList(@RequestBody String[] placeIds) {
        return getHomeService().loadPlaceList(placeIds);

    }


    @MethodChinaName(cname = "获取房源传感器编号", returnStr = "createPlace($R('placeId'))")
    @Override
    @RequestMapping(method = RequestMethod.POST, value = "createPlace")
    public @ResponseBody
    ResultModel<Place> createPlace(String name, String placeId, String personId) {
        return getHomeService().createPlace(name, placeId, personId);
    }

    @MethodChinaName(cname = "保存房源", returnStr = "savePlace($Place)")
    @Override
    @RequestMapping(method = RequestMethod.POST, value = "savePlace")
    public @ResponseBody
    ResultModel<Boolean> savePlace(@RequestBody Place Place) {
        return getHomeService().savePlace(Place);
    }


    @Override
    @MethodChinaName(cname = "创建报警规则", returnStr = "creatAlarm($Place)")
    @RequestMapping(method = RequestMethod.POST, value = "creatAlarm")
    public @ResponseBody
    ResultModel<Alarm> creatAlarm(String sensorId) {
        return getHomeService().creatAlarm(sensorId);
    }

    @Override
    @MethodChinaName(cname = "更新报警规则", returnStr = "creatAlarm($Place)")
    @RequestMapping(method = RequestMethod.POST, value = "updateAlarm")
    public @ResponseBody
    ResultModel<Boolean> updateAlarm(Alarm alarm) {
        return getHomeService().updateAlarm(alarm);
    }

    @Override
    @MethodChinaName(cname = "获取所有用户设备")
    @RequestMapping(method = RequestMethod.POST, value = "getAllUserByDeviceId")
    public @ResponseBody
    ListResultModel<Set<String>> getAllUserByDeviceId(String deviceid) {
        return getHomeService().getAllUserByDeviceId(deviceid);
    }

    @Override
    @MethodChinaName(cname = "获取场景")
    @RequestMapping(method = RequestMethod.POST, value = "getSceneById")
    public @ResponseBody
    ResultModel<Scene> getSceneById(String sensorSceneId) {
        return getHomeService().getSceneById(sensorSceneId);
    }

    @Override
    @MethodChinaName(cname = "删除场景")
    @RequestMapping(method = RequestMethod.POST, value = "/deleteScene")
    public @ResponseBody
    ResultModel<Boolean> deleteScene(String sensorSceneId) {
        return getHomeService().deleteScene(sensorSceneId);
    }

    @Override
    @MethodChinaName(cname = "更新场景")
    @RequestMapping(method = RequestMethod.POST, value = "updateScene")
    public @ResponseBody
    ResultModel<Boolean> updateScene(@RequestBody Scene scene) {
        return getHomeService().updateScene(scene);
    }

    @Override
    @MethodChinaName(cname = "获取主账号设备")
    @RequestMapping(method = RequestMethod.POST, value = "getMainUserByDeviceId")
    public @ResponseBody
    ResultModel<String> getMainUserByDeviceId(String serialno) {
        return getHomeService().getMainUserByDeviceId(serialno);
    }

    @Override
    @MethodChinaName(cname = "获取所有房间")
    @RequestMapping(method = RequestMethod.POST, value = "getAllAreaByPlaceId")
    public @ResponseBody
    ListResultModel<Set<String>> getAllAreaByPlaceId(String placeId) {
        return getHomeService().getAllAreaByPlaceId(placeId);
    }

    @Override
    @MethodChinaName(cname = "装载房间数据")
    @RequestMapping(method = RequestMethod.POST, value = "loadAreaList")
    public @ResponseBody
    ListResultModel<List<Area>> loadAreaList(@RequestBody String[] areasIds) {
        return getHomeService().loadAreaList(areasIds);
    }

    @Override
    @MethodChinaName(cname = "装载报警规则数据")
    @RequestMapping(method = RequestMethod.POST, value = "loadAlarmList")
    public @ResponseBody
    ListResultModel<List<Alarm>> loadAlarmList(@RequestBody String[] alarmIds) {
        return getHomeService().loadAlarmList(alarmIds);
    }

    @Override
    @MethodChinaName(cname = "获取所有房源")
    @RequestMapping(method = RequestMethod.POST, value = "getAllPlaceByUserId")
    public @ResponseBody
    ListResultModel<Set<String>> getAllPlaceByUserId(String userID) {
        return getHomeService().getAllPlaceByUserId(userID);
    }

    @Override
    @MethodChinaName(cname = "获取指定设备场景")
    @RequestMapping(method = RequestMethod.POST, value = "getSceneBySensorId")
    public @ResponseBody
    ListResultModel<Set<String>> getSceneBySensorId(String sensorId) {
        return getHomeService().getSceneBySensorId(sensorId);
    }

    @Override
    @MethodChinaName(cname = "获取指定网关设备场景")
    @RequestMapping(method = RequestMethod.POST, value = "getSensorTypesByGatewayId")
    public @ResponseBody
    ListResultModel<List<Sensortype>> getSensorTypesByGatewayId(String gatewayId) {
        return getHomeService().getSensorTypesByGatewayId(gatewayId);
    }

    @Override
    @MethodChinaName(cname = "删除网关")
    @RequestMapping(method = RequestMethod.POST, value = "deleteGateway")
    public @ResponseBody
    ResultModel<Boolean> deleteGateway(String deviceid) {
        return getHomeService().deleteGateway(deviceid);
    }

    @Override
    @MethodChinaName(cname = "获取房间")
    @RequestMapping(method = RequestMethod.POST, value = "getAreaById")
    public @ResponseBody
    ResultModel<Area> getAreaById(String areaId) {
        return getHomeService().getAreaById(areaId);
    }

    @Override
    @MethodChinaName(cname = "获取该房源下所有房间")
    @RequestMapping(method = RequestMethod.POST, value = "getAreasByPlaceId")
    public @ResponseBody
    ListResultModel<Set<String>> getAreasByPlaceId(String placeId) {
        return getHomeService().getAreasByPlaceId(placeId);
    }

    @Override
    @MethodChinaName(cname = "获取所有房源")
    @RequestMapping(method = RequestMethod.POST, value = "getAllPlaceIds")
    public @ResponseBody
    ListResultModel<Set<String>> getAllPlaceIds() {
        return getHomeService().getAllPlaceIds();
    }

    @Override
    @MethodChinaName(cname = "绑定房间")
    @RequestMapping(method = RequestMethod.POST, value = "bindingArea")
    public @ResponseBody
    ResultModel<Boolean> bindingArea(String sensorieee, String areaId) {
        return getHomeService().bindingArea(sensorieee, areaId);
    }

    @Override
    @MethodChinaName(cname = "绑定房源")
    @RequestMapping(method = RequestMethod.POST, value = "bindingPlace")
    public @ResponseBody
    ResultModel<Boolean> bindingPlace(String gwieee, String placeId) {
        return getHomeService().bindingArea(gwieee, placeId);
    }


    @MethodChinaName(cname = "添加告警", returnStr = "addAlarm($Alarm)")
    @RequestMapping(method = RequestMethod.POST, value = "addAlarm")
    public @ResponseBody
    ResultModel<Boolean> addAlarm(@RequestBody Alarm Alarm) {
        return getHomeService().addAlarm(Alarm);
    }

    @MethodChinaName(cname = "删除告警", returnStr = "deleteAlarm($R('alarmId'))")

    @RequestMapping(method = RequestMethod.POST, value = "deleteAlarm")
    public @ResponseBody
    ResultModel<Boolean> deleteAlarm(String alarmId) {
        return getHomeService().deleteAlarm(alarmId);
    }

    @MethodChinaName(cname = "根据传感器ID获取全部告警", returnStr = "getAllAlarmsBySensorId($R('sensorId'))")

    @RequestMapping(method = RequestMethod.POST, value = "getAllAlarmsBySensorId")
    public @ResponseBody
    ListResultModel<Set<String>> getAllAlarmsBySensorId(String sensorId) {
        return getHomeService().getAllAlarmsBySensorId(sensorId);
    }


    @MethodChinaName(cname = "获取告警", returnStr = "getAlarm($R('alarmId'))")

    @RequestMapping(method = RequestMethod.POST, value = "getAlarm")
    public @ResponseBody
    ResultModel<Alarm> getAlarm(String alarmId) {
        return getHomeService().getAlarm(alarmId);
    }

    HomeService getHomeService() {
        return (HomeService) EsbUtil.parExpression(HomeService.class);
    }

}
