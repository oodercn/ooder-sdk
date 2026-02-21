package net.ooder.agent.client.iot.client.impl;

import net.ooder.agent.client.iot.*;
import net.ooder.agent.client.iot.client.AppWebService;
import net.ooder.agent.client.iot.json.*;
import  net.ooder.common.JDSException;
import  net.ooder.config.ErrorResultModel;
import  net.ooder.config.ResultModel;
import  net.ooder.context.JDSActionContext;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.agent.client.home.client.AppClient;
import  net.ooder.agent.client.home.engine.HomeServer;
import  net.ooder.server.JDSClientService;

import java.util.ArrayList;
import java.util.List;

@EsbBeanAnnotation(id = "AppWebService", name = "APP手机服务", expressionArr = "AppWebServiceImpl()", desc = "APP手机服务")
public class AppWebServiceImpl implements AppWebService {
  

	public ResultModel<Boolean> addOutLetSensorScene(SensorSceneInfo info) {
		return updateOutLetSensorScene(info);
	}

	public ResultModel<Boolean> deleteOutLetSensorScene(String sensorSceneId) {
		ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}		
			appClient.beginTransaction();
			appClient.deleteScene(sensorSceneId);
			appClient.commitTransaction();
		} catch (HomeException e) {
			try {
				getJDSClientService().rollbackTransaction();
			} catch (HomeException robacke) {
			
			}
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;

	}

	public ResultModel<Boolean> updateOutLetSensorSceneStatus(String sensorSceneId,
                                                              int status) {
		ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			appClient.beginTransaction();
			
			Scene info = appClient.geSceneById(sensorSceneId);
			appClient.updateScene(info);
			appClient.commitTransaction();
		} catch (HomeException e) {
			try {
				getJDSClientService().rollbackTransaction();
			} catch (HomeException robacke) {
			
			}
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;
	}

	public ResultModel<Boolean> updateOutLetSensorScene(SensorSceneInfo info) {

		ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			appClient.beginTransaction();
			
			if (appClient != null) {
				Scene scene = null;
				if (info.getId() == null) {
					scene = getJDSClientService().creatScene(info.getSensorId());
				} else {
					scene = appClient.getSceneById(info.getId());
					if (scene == null) {
						scene = appClient.creatScene(info.getSensorId());
					} else {
						scene.setSceneid(info.getId());
					}
				}
				scene.setIntvalue(info.getLightValue());
				scene.setName(info.getName());
				scene.setObjvalue(info.getRgbValue());
				scene.setSensorid(info.getSensorId());
				scene.setStatus(info.getStatus());
				appClient.updateScene(scene);
			}

			appClient.commitTransaction();
		} catch (HomeException e) {
			try {
				getJDSClientService().rollbackTransaction();
			} catch (HomeException robacke) {
			
			}
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;

	}

	public ResultModel<AlarmInfo> changeStatusAlarm(String alarmid,
													Integer status) {

		ResultModel<AlarmInfo> userStatusInfo = new ResultModel<AlarmInfo>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			appClient.beginTransaction();
			
			Alarm alarm = appClient.getAlarmById(alarmid);
			alarm.setIstart(status);
			alarm = appClient.updateAlarm(alarm);
			userStatusInfo.setData(new AlarmInfo(alarm));

			appClient.commitTransaction();
		} catch (HomeException e) {
			try {
				getJDSClientService().rollbackTransaction();
			} catch (HomeException robacke) {
			
			}
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;
	}

	public ResultModel<AlarmInfo> createAlarm(AlarmInfo alarm) {
		return this.updateAlarm(alarm);
	}

	public ResultModel<Boolean> deleteAlarm(String alarmid) {

		ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
		try {
			
			AppClient appClient = getJDSClientService();
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			appClient.beginTransaction();
			
			appClient.deleteAlarm(alarmid);

			appClient.commitTransaction();
		} catch (HomeException e) {
			try {
				getJDSClientService().rollbackTransaction();
			} catch (HomeException robacke) {
			
			}
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;

	}

	public ResultModel<AlarmInfo> getAlarmById(String alarmId) {
		ResultModel<AlarmInfo> userStatusInfo = new ResultModel<AlarmInfo>();
		try {
			AppClient appClient = getJDSClientService();

			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			Alarm alarm = appClient.getAlarmById(alarmId);
			userStatusInfo.setData(new AlarmInfo(alarm));

		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;

	}

	public ResultModel<List<AlarmInfo>> getAlarmBySensorId(String sensorId) {

		ResultModel<List<AlarmInfo>> userStatusInfo = new ResultModel<List<AlarmInfo>>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			List<AlarmInfo> alarmInfos = new ArrayList<AlarmInfo>();
			List<Alarm> alarms = appClient.getAlarmBySensorId(sensorId);
			for (Alarm alarm : alarms) {
				alarmInfos.add(new AlarmInfo(alarm));
			}
			userStatusInfo.setData(alarmInfos);

		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;

	}

	public ResultModel<AlarmInfo> updateAlarm(AlarmInfo alarmInfo) {

		ResultModel<AlarmInfo> userStatusInfo = new ResultModel<AlarmInfo>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			appClient.beginTransaction();
			
			Alarm alarm = null;
			String sensorid = alarmInfo.getSensorId();
			if (sensorid == null) {
				throw new HomeException("sensorid is null!",
						HomeException.NOTIMPLEMENTED);
			}

			List<AlarmInfo> alarmList = this.getAlarmBySensorId(
					alarmInfo.getSensorId()).get();
			for (AlarmInfo ialarmInfo : alarmList) {
				if (ialarmInfo.getName().equals(alarmInfo.getName())) {
					throw new HomeException("场景名称存在",
							HomeException.NOTIMPLEMENTED);
				}
			}

			if (alarmInfo.getId() == null) {
				alarm = appClient.createAlarm(alarmInfo.getSensorId());
			} else {
				alarm = appClient.getAlarmById(alarmInfo.getId());
			}

			alarm.setStarttime(alarmInfo.getStarttime());
			alarm.setName(alarmInfo.getName());
			alarm.setEndtime(alarmInfo.getEndtime());
			alarm.setCycle(alarmInfo.getCycle());
			alarm.setSensorid(alarmInfo.getSensorId());
			alarm.setAlertcontent(alarmInfo.getAlertcontent());
			alarm.setComfort(alarmInfo.getComfort());
			alarm.setIstart(alarmInfo.getIsStart());
			alarm.setDelaytime(alarmInfo.getDelayTime());
			;
			alarm.setSceneid(alarmInfo.getSceneid());
			alarm.setOperatestatus(alarmInfo.getOperateStatus());
			alarm.setDevicestatus(alarmInfo.getDeviceStatus());
			alarm = appClient.updateAlarm(alarm);
			userStatusInfo.setData(new AlarmInfo(alarm));

			appClient.commitTransaction();
		} catch (JDSException e) {
			try {
				getJDSClientService().rollbackTransaction();
			} catch (HomeException robacke) {
			
			}
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());

		}
		return userStatusInfo;

	}

	public ResultModel<AreaInfo> createArea(AreaInfo areaInfo) {

		ResultModel<AreaInfo> userStatusInfo = new ResultModel<AreaInfo>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			appClient.beginTransaction();
			
			Area area = appClient.createArea(areaInfo.getName(), areaInfo.getPlaceId());
			userStatusInfo.setData(new AreaInfo(area));

			appClient.commitTransaction();
		} catch (HomeException e) {
			try {
				getJDSClientService().rollbackTransaction();
			} catch (HomeException robacke) {
			
			}
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;
	}

	public ResultModel<AreaInfo> updateArea(AreaInfo areaInfo) {

		ResultModel<AreaInfo> userStatusInfo = new ResultModel<AreaInfo>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			appClient.beginTransaction();
			
			appClient.updateAreaName(areaInfo.getId(), areaInfo.getName());
			
			userStatusInfo.setData(areaInfo);
			appClient.commitTransaction();
		} catch (HomeException e) {
			try {
				getJDSClientService().rollbackTransaction();
			} catch (HomeException robacke) {
			
			}
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;
	}

	public ResultModel<Boolean> deleteArea(String areaInfoId, String placeId) {
		ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
		try {
			AppClient appClient = getJDSClientService();

			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			appClient.deleteArea(areaInfoId);

			appClient.commitTransaction();
		} catch (HomeException e) {
			try {
				getJDSClientService().rollbackTransaction();
			} catch (HomeException robacke) {
			
			}
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;

	}

	public ResultModel<AreaInfo> getAreasById(String areaId) {

		ResultModel<AreaInfo> userStatusInfo = new ResultModel<AreaInfo>();
		try {
			AppClient appClient = getJDSClientService();

			userStatusInfo.setData(new AreaInfo(appClient.getAreaById(areaId)));

		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;
	}

	public ResultModel<List<AreaInfo>> getAreasByIds(List<String> ids) {
		ResultModel<List<AreaInfo>> userStatusInfo = new ResultModel<List<AreaInfo>>();
		try {
			AppClient appClient = getJDSClientService();

			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			List<Area> areas = appClient.getAreasByIds(ids);
			List<AreaInfo> areaInfos = new ArrayList<AreaInfo>();
			for (Area area : areas) {

				areaInfos.add(new AreaInfo(area));
			}

			userStatusInfo.setData(areaInfos);

		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;

	}

	public ResultModel<PlaceInfo> createPlace(PlaceInfo placeInfo) {
		ResultModel<PlaceInfo> userStatusInfo = new ResultModel<PlaceInfo>();

		try {
			AppClient appClient = getJDSClientService();
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			appClient.beginTransaction();
			
			List<Place> places = appClient.getAllPlace();

			for (Place place : places) {
				if (place.getName().equals(placeInfo.getName())) {
					throw new HomeException("住所名称已存在，请重新命名",
							HomeException.PLACEEXITS);
				}
			}

			Place place = appClient.createPlace(placeInfo.getName());
			place.setName(placeInfo.getName());
			place.setMemo(placeInfo.getSensorIds());
			place.setStart(placeInfo.getIsstarts());
			appClient.updatePlace(place);
			userStatusInfo.setData(new PlaceInfo(place));

			appClient.commitTransaction();
		} catch (HomeException e) {
			try {
				getJDSClientService().rollbackTransaction();
			} catch (HomeException robacke) {
			
			}
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;
	}

	public ResultModel<Boolean> deletePlace(String placeId) {

		ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			appClient.beginTransaction();
			
			appClient.deletePlace(placeId);
			appClient.commitTransaction();
		} catch (HomeException e) {
			try {
				getJDSClientService().rollbackTransaction();
			} catch (HomeException robacke) {
			
			}
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;

	}

	public ResultModel<List<AreaInfo>> getAllAreaByPlaceId(String placeId) {

		ResultModel<List<AreaInfo>> userStatusInfo = new ResultModel<List<AreaInfo>>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			List<Area> areas = appClient.getAllAreaByPlaceId(placeId);
			List<AreaInfo> areaInfos = new ArrayList<AreaInfo>();
			for (Area area : areas) {

				areaInfos.add(new AreaInfo(area));
			}
			userStatusInfo.setData(areaInfos);

		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}

		return userStatusInfo;
	}

	public ResultModel<List<PlaceInfo>> getAllPlace() {
		ResultModel<List<PlaceInfo>> userStatusInfo = new ResultModel<List<PlaceInfo>>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			List<Place> places = appClient.getAllPlace();
			List<PlaceInfo> placeInfos = new ArrayList<PlaceInfo>();
			for (Place place : places) {
				PlaceInfo info = this.getPlaceById(place.getPlaceid())
						.get();
				placeInfos.add(info);
			}
			userStatusInfo.setData(placeInfos);

		} catch (JDSException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}

		return userStatusInfo;
	}

	public ResultModel<PlaceInfo> getPlaceById(String placeId) {
		ResultModel<PlaceInfo> userStatusInfo = new ResultModel<PlaceInfo>();
		try {
			AppClient appClient = getJDSClientService();

			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			Place place = appClient.getPlaceById(placeId);
			userStatusInfo.setData(new PlaceInfo(place));

		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}

		return userStatusInfo;

	}

	public ResultModel<List<PlaceInfo>> getPlaceByIds(List<String> placeIds) {

		ResultModel<List<PlaceInfo>> userStatusInfo = new ResultModel<List<PlaceInfo>>();
		try {
			AppClient appClient = getJDSClientService();

			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}

			List<PlaceInfo> places = new ArrayList<PlaceInfo>();
			for (String placeInfoId : placeIds) {
				places.add(this.getPlaceById(placeInfoId).get());
			}

			userStatusInfo.setData(places);

		} catch (JDSException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}

		return userStatusInfo;

	}

	public ResultModel<Boolean> updatePlace(PlaceInfo placeInfo) {
		ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			appClient.beginTransaction();
			
			Place place = appClient.getPlaceById(placeInfo.getId());
			place.setName(placeInfo.getName());
			place.setUserid(placeInfo.getAccount());
			place.setMemo(placeInfo.getSensorIds());
			place.setStart(placeInfo.getIsstarts());
			appClient.updatePlace(place);

			appClient.commitTransaction();
		} catch (HomeException e) {
			try {
				getJDSClientService().rollbackTransaction();
			} catch (HomeException robacke) {
			
			}
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;
	}

	public ResultModel<List<String>> getIndexSensorIds(String placeId,
                                                       int isShow) {
		ResultModel<List<String>> userStatusInfo = new ResultModel<List<String>>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient == null) {
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			List<ZNode> indexSensorList  = appClient.getIndexSensorList(placeId);
			List<String>  indexIds=new ArrayList<String> ();
			for(ZNode znode:indexSensorList){
				indexIds.add(znode.getZnodeid());
			}
			userStatusInfo.setData(indexIds);
		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;
	}
	

	public ResultModel<List<SensorSceneInfo>> getLightSensorSceneInfo(
			String sensorId) {
		ResultModel<List<SensorSceneInfo>> userStatusInfo = new ResultModel<List<SensorSceneInfo>>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient==null){
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			List<SensorSceneInfo> sceneinfos=new ArrayList<SensorSceneInfo>();
			List<Scene> scenes=appClient.getSceneBySensorId(sensorId);
			for(Scene scene:scenes){
				sceneinfos.add(new SensorSceneInfo(scene));
			}
			
			userStatusInfo.setData(sceneinfos);
		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;		
	}

	public ResultModel<UserInfo> getMainUserInfo(String serialno) {
		ResultModel<UserInfo> userStatusInfo = new ResultModel<UserInfo>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient==null){
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
			UserInfo info=new UserInfo(appClient.getMainUserInfo(serialno));
			userStatusInfo.setData(info);
		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;		
	}

	public ResultModel<String> getVersion(String osType, String currVersion) {
		
		
		ResultModel<String> userStatusInfo = new ResultModel<String>();
		try {
			AppClient appClient = getJDSClientService();
			if (appClient==null){
				throw new HomeException("not login!", HomeException.NOTLOGIN);
			}
		
			userStatusInfo.setData("1.0.0");
		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;		
	}

	public ResultModel<Boolean> saveSensorShow(String sensorIds, String placeId) {
		
		ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
		
		try {
			
			PlaceInfo place = this.getPlaceById(placeId).get();
			
			place.setSensorIds(sensorIds);
			this.updatePlace(place);
			
		} catch (JDSException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		
		
		
		
		return userStatusInfo;		
			
	}


	private AppClient getJDSClientService()throws HomeException {
		AppClient appClient=null;
		
		JDSClientService client=(JDSClientService) JDSActionContext.getActionContext().Par("$JDSC");
		
		if (client == null) {
			throw new HomeException("not login!", 1005);
		}
		try {
			appClient=HomeServer.getInstance().getAppClient(client);
		} catch (JDSException e) {
			throw new HomeException("not login!", 1005);
		}
		if (appClient == null) {
			throw new HomeException("not login!", 1005);
		}
		
		return appClient;
	}
}
