package net.ooder.agent.client.iot.client;


import net.ooder.agent.client.iot.json.AlarmMessageInfo;
import net.ooder.agent.client.iot.json.SensorHistoryDataInfo;
import net.ooder.agent.client.iot.json.device.SensorData;
import net.ooder.config.ResultModel;

import java.util.Date;
import java.util.List;

public interface DataWebService {



	/**
	 * 添加报警
	 * @param alarm
	 */
	ResultModel<Boolean> addAlarm(SensorData alarm);
	
	/**
	 * 上报数据
	 * @param sensor
	 */
	ResultModel<Boolean> addData(SensorData sensor);
	
	/**
	 * 	历史数据
	 * @param sensorId 编号
	 * @param starttime
	 * @param endtime
     * @param currentIndex
	 * @param pageSize
	 * @return
	 */
	ResultModel<List<SensorHistoryDataInfo>> getHistorySensorData(String sensorId, Date starttime, Date endtime, Integer currentIndex, Integer pageSize);



	/**
	 * 传感器历史数据查询
	 * @param sensorId
	 * @param currentIndex
	 * @param pageSize
	 * @return
	 */
	ResultModel<List<SensorHistoryDataInfo>>  getLastSensorHistoryData(String sensorId, Integer currentIndex, Integer pageSize);

	

	/**
	 * 获取指定住所报警信息
	 * @param placeId
	 * @return
	 */
	ResultModel<List<AlarmMessageInfo>>  getAlarmMessageByPlaceId(String placeId);



}
