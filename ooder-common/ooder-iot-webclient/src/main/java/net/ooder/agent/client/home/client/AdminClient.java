package net.ooder.agent.client.home.client;

import net.ooder.agent.client.command.AddSensorCommand;
import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.iot.*;
import  net.ooder.annotation.JLuceneIndex;
import  net.ooder.common.Condition;
import  net.ooder.common.JDSException;
import  net.ooder.config.ListResultModel;
import  net.ooder.config.ResultModel;
import  net.ooder.agent.client.home.query.IOTConditionKey;
import  net.ooder.server.JDSClientService;

import java.util.List;


public interface AdminClient extends JDSClientService {




	public static final String CTX_USER_ID = "AdminClient.USERID";

	public static final String CTX_USERS = "AdminClient.USERS";

	

	
	/**
	 * 
	 * @param ieee
	 * @param attributeName
	 * @param value
	 * @return
	 * @throws HomeException
	 */
	public void updateDeviceValue(String ieee, String attributeName, String value) throws HomeException;




	/**
	 *
	 * @param znodeId
	 * @return
	 * @throws HomeException
	 */
	public void deleteZNode(String znodeId) throws HomeException;



	/**
	 *
	 * @param znodeId
	 * @return
	 * @throws HomeException
	 */
	public void login(String deviceId) throws HomeException;

	/**
	 *
	 * @param ieee
	 * @param name
	 * @return
	 * @throws HomeException
	 */
	public void updateEndPointName(String ieee, String name) throws HomeException;




	/**
	 * 修改房间信息
	 * 
	 * @param area
	 * @return
	 */
	public void updateArea(Area area) throws HomeException;
	


	/**
	 * 修改网关状态
	 * @param ieee
	 * @param status
	 */
	public void updateGatewayStatus(String ieee, int status) throws HomeException;


	/**
	 *
	 * @return
	 */
	public ResultModel<List<Device>> getAllOnLineGateway() ;



	/**
	 * 删除房间信息
	 * 
	 * @param areaId
	 * @return
	 */
	public void deleteArea(String areaId) throws HomeException;


	/**
	 * 删除设备信息(相当于恢复出厂)
	 *
	 * @param deviceId
	 * @return
	 */
	public void deleteDevice(String deviceId) throws HomeException;


	/**
	 * 物联删除
	 *
	 * @param deviceIds
	 * @return
	 */
	public void clearDevices(List<String> deviceIds) throws HomeException;




	/**
	 * 获取所有房间信息
	 * 
	 * @param placeId
	 * @return
	 */
	public List<Area> getAllAreaByPlaceId(String placeId) throws HomeException;



	/**
	 * 创建住所
	 * 
	 * @param name
	 * @return
	 */
	public Place createPlace(String name, String parentId) throws HomeException;

	/**
	 * 修改住所信息
	 * 
	 * @param place
	 * @return
	 */
	public void updatePlace(Place place) throws HomeException;





	/**
	 * 删除住所信息
	 * 
	 * @param placeId
	 * @return
	 */
	public void deletePlace(String placeId) throws HomeException;

	
	/**
	 * 获取所有住所信息
	 *
	 * @return
	 */
	public List<Place> getAllPlace() throws HomeException;

	/**
	 * 添加传感器
	 * 
	 * @param gwieee
	 * @return
	 * @throws HomeException
	 */
	public AddSensorCommand addDevice(String gwieee, Integer type, String serialno)
			throws HomeException;


	/**
	 * 绑定传感器
	 *
	 * @return
	 * @throws HomeException
	 */
	public void bindingSensor(String ieee, String areId)
			throws HomeException;

//	/**
//	 * 绑定房间
//	 *
//	 * @return
//	 * @throws HomeException
//	 */
//	public void bindPlace(String ieee, String placeId)
//			throws HomeException;
//
//

	void deleteSensrotype(Integer sensorno) throws HomeException;

	void updateSensrotype(Sensortype sensortype) throws HomeException;

	public ZNode createGateway(String ieee, String placeId, String gatewayName, String personId) throws HomeException;
	
	/**
	 * 获取指定编码的传感器类型
	 * 
	 * @return
	 */
	public Sensortype getSensorTypesByNo(Integer typno) throws HomeException;


	/**
	 * 根据房间ID获取房间信息
	 * 
	 * @param ids
	 * @return
	 * @throws HomeException
	 */
	public List<Area> getAreasByIds(List<String> ids) throws HomeException;
	

	
	/**
	 * 
	 * @param name
	 * @param placeId
	 * @return
	 * @throws HomeException
	 */
	public Area createArea(String name, String placeId)throws HomeException;
	
	/**
	 * 
	 * @param areaId
	 * @return
	 * @throws HomeException
	 */
	public Area getAreaById(String areaId)throws HomeException;

	/**
	 *
	 * @param placeId
	 * @return
	 * @throws HomeException
	 */
	public Place getPlaceById(String placeId)throws HomeException;


	/**
	 *
	 * @param condition
	 * @return
	 */
	ListResultModel<List<DeviceEndPoint>> findEndPoint(Condition<IOTConditionKey,JLuceneIndex> condition) throws JDSException;
	/**
	 *
	 * @param condition
	 * @return
	 */
	ListResultModel<List<ZNode>> findZnode(Condition<IOTConditionKey,JLuceneIndex> condition)throws JDSException ;

	/**
	 *
	 * @param condition
	 * @return
	 */
	ListResultModel<List<Device>> findDevice(Condition<IOTConditionKey,JLuceneIndex> condition)throws JDSException ;

	/**
	 *
	 * @param factoryName
	 * @return
	 */
	ListResultModel<List<Device>> getAllGatewayByFactory(String factoryName) throws JDSException;

	/**
	 * 命令测试
	 *
	 * @param commandStr
	 * @param gatewayieees
	 * @param num
	 * @param times
	 * @return
	 */
	public List<Command> sendCommands(String commandStr, String gatewayieees, Integer num, Integer times);



	}
