package net.ooder.agent.client.iot.client;

import net.ooder.agent.client.iot.json.*;
import  net.ooder.config.ResultModel;
import java.util.List;

public interface AppWebService {
	/**
	 * 创建住所
	 * @param place
	 * @return
	 */
	ResultModel<PlaceInfo> createPlace(PlaceInfo place);
	/**
	 * 修改住所信息
	 * @param place
	 * @return
	 */
	ResultModel<Boolean> updatePlace(PlaceInfo place);
	
	/**
	 * 删除住所信息
	 * @param placeId
	 * @return
	 */
	ResultModel<Boolean> deletePlace(String placeId);
	
	/**
	 * 获取住所信息
	 * @param placeId
	 * @return
	 */
	ResultModel<PlaceInfo> getPlaceById(String placeId);



	/**
	 * 获取所有住所信息
	 * @return
	 */
	ResultModel<List<PlaceInfo>> getAllPlace();
	
	
	
	/**
	 * 更具组所ID获取组所对象

	 */
	ResultModel<List<PlaceInfo>> getPlaceByIds(List<String> placeIds);
			
	/**
	 * 保存住所首页信息
	 * @param sensorIds
	 * @param placeId
	 */
	ResultModel<Boolean> saveSensorShow(String sensorIds, String placeId);


	/**
	 * 创建房间
	 * @param areaInfo
	 * @return
	 */
	ResultModel<AreaInfo> createArea(AreaInfo areaInfo);

      /**
       * 修改房间信息
       * @param areaInfo
       * @return
       */
	ResultModel<AreaInfo> updateArea(AreaInfo areaInfo);

      /**
       * 删除房间信息
       * @param areaInfoId
       * @return
       */
	ResultModel<Boolean> deleteArea(String areaInfoId, String placeId);

      /**
       * 获取所有房间信息
       * @param areaInfoId
       * @return
       */
	ResultModel<List<AreaInfo>> getAllAreaByPlaceId(String placeId);

      /**
       * 根据传入ID获取传感器详细信息列表
       * @param ids
       * @return
       * @throws HomeException
       */
	ResultModel<List<AreaInfo>> getAreasByIds(List<String> ids);


      /**
       * 根据传入ID获取传感器详细信息列表
       * @param ids
       * @return
       * @throws HomeException
       */
	ResultModel<AreaInfo> getAreasById(String areaId);



	/**
	 * 首页显示的传感器Id列表
	 * @param placeId
	 * @param isShow
	 * @return
	 */
	ResultModel<List<String>> getIndexSensorIds(String placeId, int isShow);




	/**
	 * 获取版本信息
	 * @param osType
	 * @param currVersion
	 * @return
	 */
	ResultModel<String> getVersion(String osType, String currVersion);



	/**
	 * 修改报警规则
	 * @param alarmInfo
	 * @return
	 * @throws HomeException
	 */
	ResultModel<AlarmInfo> updateAlarm(AlarmInfo alarmInfo);

	/**
	 * 添加报警规则周期
	 * @param cycleInfo
	 * @return
	 * @throws HomeException
	 */
	ResultModel<AlarmInfo> createAlarm(AlarmInfo alarm);

	/**
	 * 启停报警规则周期
	 * @param alarmid
	 * @param status
	 * @return
	 * @throws HomeException
	 */
	ResultModel<AlarmInfo> changeStatusAlarm(String alarmid, Integer status);

	/**
	 * 删除报警规则周期
	 * @param alarmid
	 * @throws HomeException
	 */
	ResultModel<Boolean> deleteAlarm(String alarmid);

	/**
	 * 查询单个报警规则
	 * @param serialno
	 * @return
	 * @throws HomeException
	 */
	ResultModel<AlarmInfo> getAlarmById(String alarmId);



	/**
	 * 查询单个报警规则
	 * @param serialno
	 * @return
	 * @throws HomeException
	 */
	ResultModel<List<AlarmInfo>> getAlarmBySensorId(String sensorId);



	/**
	 * 2.6.5.4	插座传感器增加场景
	 * @param sensorId
	 * @param OutLetValue
	 * @param rgbValue
	 * @param name
	 * @param status
	 * @return
	 */
	ResultModel<Boolean> addOutLetSensorScene(SensorSceneInfo info);

	/**
	 * 2.6.5.5	插座传感器删除场景
	 * @param sensorSceneId
	 * @return
	 */
	ResultModel<Boolean> deleteOutLetSensorScene(String sensorSceneId);

	/**
	 * 2.6.5.6	插座传感器默认场景更改
	 * @param sensorSceneId
	 * @param status
	 * @return
	 */
	ResultModel<Boolean> updateOutLetSensorSceneStatus(String sensorSceneId, int status);
	

	/**
	 * 插座传感器默认场景更改
	 * @param SensorSceneInfo
	 * @throws HomeException
	 */
	ResultModel<Boolean> updateOutLetSensorScene(SensorSceneInfo info);
	
	
	/**
	 * 2.6.5.1	智能灯传感器场景列表
	 * @param sensorId
	 * @return
	 */
	ResultModel<List<SensorSceneInfo>> getLightSensorSceneInfo(String sensorId);
	
	

	/**
	 * 获取共享用户信息
	 * @param userId
	 * @return
	 */
	ResultModel<UserInfo> getMainUserInfo(String serialno) ;
	
	
	
	
	
}
