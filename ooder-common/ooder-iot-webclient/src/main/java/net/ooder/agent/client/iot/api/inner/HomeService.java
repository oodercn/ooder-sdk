package net.ooder.agent.client.iot.api.inner;

import net.ooder.agent.client.iot.*;
import  net.ooder.config.ListResultModel;
import  net.ooder.config.ResultModel;
import java.util.List;
import java.util.Set;

public interface HomeService {
	

	
	/**
	 * 创建住所
	 * @return
	 */
	ResultModel<Place> createPlace(String name, String parentPlaceId, String personId);


	/**
	 * 修改住所信息
	 * @param place
	 * @return
	 */
	ResultModel<Boolean> savePlace(Place place);

	/**
	 * 删除住所信息
	 * @param placeId
	 * @return
	 */
	ResultModel<Boolean> deletePlace(String placeId) ;






	/**
	 * 获取住所信息
	 * @param placeIds
	 * @return
	 */
	ListResultModel<List<Place>> loadPlaceList(String[] placeIds);

	ListResultModel<Set<String>> getPlaceIds();


	/**
	 * 获取住所信息
	 * @return
	 */
	ResultModel<Place> getPlaceById(String placeId);





	/**
	 * 添加报警信息
	 * @param Alarm
	 * @return
	 */
	ResultModel<Boolean> addAlarm(Alarm Alarm);




	/**
	 * 删除报警规则
	 * @param alarmId
	 * @return
	 */
	ResultModel<Boolean> deleteAlarm(String alarmId);

	/**
	 * 传感器报警规则显示
	 * @param alarmId
	 * @return
	 */
	ResultModel<Alarm> getAlarm(String alarmId);

	/**
	 * 传感器报警规则显示
	 * @param sensorId
	 * @return
	 */
	ListResultModel<Set<String>> getAllAlarmsBySensorId(String sensorId);

	/**
	 * 创建房间
	 * @param name
	 * @return
	 */
	ResultModel<Area> createArea(String name, String placeId);

      /**
       * 修改房间信息
       * @param Area
       * @return
       */
	ResultModel<Boolean> updateArea(Area Area);

      /**
       * 删除房间信息
       * @param areaId
       * @return
       */
	ResultModel<Boolean> deleteArea(String areaId);


      /**
       * 查询房间列表
       * @param areaIdList
       * @return
       */
	  ListResultModel<List<Area>> getAreas(String[] areaIdList);

      /**
       *
       * @param placeId
       * @return
       */
	  ListResultModel<Set<String>> getAreaIdsbyPlaceId(String placeId);


	public ResultModel<Alarm> creatAlarm(String sensorId);

	public ResultModel<Boolean>  updateAlarm(Alarm alarm);

	public ListResultModel<Set<String>>  getAllUserByDeviceId(String deviceid);

	public ResultModel<Scene> getSceneById(String sensorSceneId);

	public ResultModel<Boolean> deleteScene(String sensorSceneId);

	public ResultModel<Boolean> updateScene(Scene scene);

	public ResultModel<String>  getMainUserByDeviceId(String serialno);

	public ListResultModel<Set<String>> getAllAreaByPlaceId(String placeId);

	public ListResultModel<List<Area>>  loadAreaList(String[] strings);

	public ListResultModel<List<Alarm>> loadAlarmList(String[] strings);

	public ListResultModel<Set<String>> getAllPlaceByUserId(String userID);

	public ListResultModel<Set<String>> getSceneBySensorId(String sensorId);

	public ListResultModel<List<Sensortype>> getSensorTypesByGatewayId(String gatewayId);


	public ResultModel<Boolean> deleteGateway(String deviceid);


	/**
	 *
	 * @param areaId
	 * @return
	 */
	public ResultModel<Area> getAreaById(String areaId);


	/**
	 * 根据门店ID获取所有房间
	 *
	 * @return
	 */
	public ListResultModel<Set<String>> getAreasByPlaceId(String placeId);


	public ListResultModel<Set<String>> getAllPlaceIds();

	public ResultModel<Boolean>  bindingArea(String sensorieee, String areaId);

	public ResultModel<Boolean> bindingPlace(String gwieee, String placeId);

}
