package net.ooder.agent.client.iot;

import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.command.CommandReportStatus;
import net.ooder.agent.client.iot.json.NetworkInfo;
import net.ooder.agent.client.iot.json.device.*;
import net.ooder.config.ResultModel;

import java.util.List;
import java.util.Map;




public interface GWClientService {
	

	/**
	 * 网关注册
	 * @param gateway
	 * @return
	 */
	public ResultModel<GatewayRegister> register(Gateway gateway);

	/**
	 * 获取绑定列表
	 * 
	 * @param bindInfo
	 * @return
	 */

	public ResultModel<Boolean> bindListReport(BindInfo bindInfo);


	/**
	 * 模式列表
	 * 
	 * @param mode
	 * @return
	 */
	
	public ResultModel<Boolean> modeListReport(SenceMode mode);

	/**
	 * 网关激活
	 * @param gateway
	 * @return
	 */
	public ResultModel<GatewayActivate> activate(Gateway gateway);

	/**
	 * 登录
	 * @param userInfo
	 * @return
	 */
	public ResultModel<GWUser> login(GWUser userInfo);

	/**
	 * 退出
	 * @return
	 */
	public ResultModel<Boolean> logout();

	/**
	 * 传感器列表
	 * @param sensorList
	 * @return
	 */
	public ResultModel<Boolean> findSensor(SensorListInfo sensorList);

	/**
	 * 获取网关配置信息
	 * @param networkInfo
	 * @return
	 */
	public ResultModel<NetworkInfo> changeNetworkResponse(NetworkInfo networkInfo);

	

	/**
	 * 传感器上报
	 * @param sensorList
	 * @return
	 */
	public ResultModel<Boolean> sensorReport(SensorListInfo sensorList);

	/**
	 * 绑定执行报告
	 * @param report
	 * @return
	 */
	public ResultModel<Boolean> bindingStatusReport(CommandReportStatus report);


	/**
	 * 命令报告
	 * @param commandReportStatus
	 * @return
	 */
	public ResultModel<Boolean> commandReport(CommandReportStatus commandReportStatus);


	/**
	 * 传感器上线
	 * @param sensor
	 * @return
	 */
	public ResultModel<Boolean> sensorOnLine(Sensor sensor);


	/**
	 * 传感器离线事件
	 * @param sensor
	 * @return
	 */
	public ResultModel<Boolean> sensorOffLine(Sensor sensor);


	/**
	 * 网关离线
	 * @param gateway
	 * @return
	 */
	public ResultModel<Boolean> gatewayOffLine(Gateway gateway);
	
	/**
	 * 网关上线
	 * @param gateway
	 * @return
	 */
	public ResultModel<Boolean> gatewayOnLine(Gateway gateway);

	/**
	 * 报警信息上报
	 * @param sensordata
	 * @return
	 */
	public ResultModel<Boolean> alarmReport(SensorData sensordata);


	/**
	 * 网关错误报告上报
	 * @param errorPort
	 * @return
	 */
	public ResultModel<Map<String,List<Command>>> gatewayErrorReport(GatewayErrorReport errorPort);


	/**
	 * 数据上报
	 * @param sensordata
	 * @return
	 */
	public ResultModel<Boolean> dataReport(SensorData sensordata);


}