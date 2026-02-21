package net.ooder.agent.client.home.client;

import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.DeviceEndPoint;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;
import net.ooder.agent.client.iot.json.NetworkInfo;
import net.ooder.agent.client.iot.json.device.EndPoint;
import  net.ooder.common.JDSException;
import  net.ooder.engine.ConnectInfo;
import  net.ooder.common.CommandEventEnums;
import  net.ooder.annotation.MethodChinaName;
import  net.ooder.server.JDSClientService;

import java.util.List;

public interface GWClient extends JDSClientService {
	
	
	/**
	 * 网关下线
	 * @param deviceId
	 * @throws HomeException
	 */
	public void gatewayOffLine(String deviceId) throws HomeException;
	
	
	
	/**
	 * 网关上线
	 * @param deviceId
	 * @throws HomeException
	 */
	public void
	gatewayOnLine(String deviceId) throws HomeException;
	
	/**
	 * 网关激活
	 * @param deviceId
	 * @return
	 * @throws HomeException
	 */
	public Device activateDevice(String deviceId)throws HomeException;
	

	/**
	 * 根据设备ID获取设备属性
	 * @param deviceId
	 * @return
	 * @throws HomeException
	 */
	public Device getDeviceById(String deviceId) throws HomeException;
	
	
	/**
	 * 根据设备IEEE获取设备属性
	 * @param ieee
	 * @return
	 * @throws HomeException
	 */
	public Device getDeviceByIeee(String ieee) throws HomeException;
	
	

	/**
	 * 根据设备IEEE获取应用EP属性
	 * @param ieee
	 * @return
	 * @throws HomeException
	 */
	public DeviceEndPoint getEndPointByIeee(String ieee) throws HomeException;

	

	/**
	 * 根据设备IEEE获取应用EP属性
	 * @param epId
	 * @return
	 * @throws HomeException
	 */
	public DeviceEndPoint getEndPointById(String epId) throws HomeException;


	
	
		/**
	 * 同步传感器列表
	 * （网关报送设备列表时调用）
	 * @param sensors
	 * @throws HomeException
	 */
	void syncSensor(List<Device> sensors)throws HomeException;
	
	
	/**
	 * 注册应用
	 * @param ep
	 * @return
	 * @throws HomeException
	 */
	public DeviceEndPoint registerEndPonit(String sensorieee, EndPoint ep) throws JDSException;

	/**
	 * 注册设备
	 * @param ieee
	 * @param type
	 * @param factory
	 * @return
	 * @throws HomeException
	 */
	public Device registerSensor(String ieee, Integer type, String factory) throws JDSException;
	/**
	 * 删除传感器
	 * @param ieee
	 * @throws HomeException
	 */
	void removeSensor(String ieee)throws HomeException;



	/**
	 * 检查是否在线
	 *
	 * @throws JDSException
	 */
	@MethodChinaName(cname="检查是否在线",returnStr="isOnLine()")
	public Boolean isOnLine() throws JDSException;


	/**
	 * 添加报警
	 */
	void addAlarm(String deviceId, DeviceDataTypeKey datatype, String value, String time)throws HomeException;

	/**
	 * 上报数据
	 * @param deviceId
	 */
	void addData(String deviceId, DeviceDataTypeKey datatype, String value, String time)throws HomeException;

	/**
	 * 设备下线通知
	 * @param deviceId
	 * @throws HomeException
	 */
	void sensorOffLine(String deviceId)throws HomeException;

	/**
	 * 设备上线通知
	 * @param deviceId
	 * @throws HomeException
	 */
	void sensorOnLine(String deviceId)throws HomeException;

	/**
	 * 更新设备信息
	 * @param device
	 * @throws HomeException
	 */
	public void updateDevice(Device device) throws HomeException;




	/**
	 * 更新应用节点信息
	 * @param endPoint
	 * @throws HomeException
	 */
	public void updateEndPoint(DeviceEndPoint endPoint) throws HomeException;



	/**
	 * 绑定
	 * @param sensorieee
	 * @throws HomeException
	 */
	public void bind(String sensorieee, Integer type) throws HomeException;


	/**
	 * 解绑
	 * @param sensorieee
	 * @throws HomeException
	 */
	public void unbind(String sensorieee, Integer type) throws HomeException;


	/**
	 * 绑定成功
	 * @param sensorieee
	 * @throws HomeException
	 */
	public void bindSuccess(String sensorieee) throws HomeException;

	/**
	 * 解绑成功
	 * @param sensorieee
	 * @throws HomeException
	 */
	public void unbindSuccess(String sensorieee) throws HomeException;

	/**
	 * 修改网关联网信息
	 * @throws HomeException
	 */
	public void changeGatewayNetwork(NetworkInfo network) throws HomeException;

	/**
	 * 获取网关联网信息
	 * @throws HomeException
	 */
	public NetworkInfo getGatewayNetwork() throws HomeException;




	/**
	 * 更新命令报告状态信息
	 * @param commandId
	 * @throws HomeException
	 */
	public void commandReport(String commandId, Integer status, String modeId, CommandEventEnums code) throws HomeException;
	
	

	

	/**
	 * 开始事务操作
	 * 
	 * @throws HomeException
	 */
	@MethodChinaName(cname="开始事务操作",returnStr="beginTransaction()",display=false)
	public void beginTransaction() throws HomeException;

	/**
	 * 提交事务操作
	 * 
	 * @throws HomeException
	 */
	@MethodChinaName(cname="提交事务操作",returnStr="commitTransaction()",display=false)
	public void commitTransaction() throws HomeException;

	/**
	 * 回滚事务操作
	 * 
	 * @throws HomeException
	 */
	@MethodChinaName(cname="回滚事务操作",returnStr="rollbackTransaction()",display=false)
	public void rollbackTransaction() throws HomeException;
	

	public JDSClientService getJDSClient();


	public void setClientService(JDSClientService client);

	

	public void setConnInfo(ConnectInfo connInfo) ;



	

}
