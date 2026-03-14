package net.ooder.agent.client.home.engine;

import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.home.client.CommandClient;
import net.ooder.agent.client.iot.*;
import net.ooder.common.ReturnType;
import  net.ooder.msg.Msg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


public interface 	IOTDataEngine {
	
	
	/**
	 * 
	 * @param ieee
	 * @return
	 * @throws HomeException
	 */
	public CommandClient getCommandClientByieee(String ieee) throws HomeException;


	/**
	 *
	 * @param znode
	 * @param wbaccount
	 * @return
	 * @throws HomeException
	 */
	public ReturnType bindingGateway(ZNode znode, String wbaccount)throws HomeException;
	
	/**
	 * 
	 * @param znode
	 * @param device
	 * @return
	 * @throws HomeException
	 */
	public ReturnType createZNode(ZNode znode, Device device)throws HomeException;

	/**
	 * 
	 * @param place
	 * @return
	 * @throws HomeException
	 */
	public ReturnType createPlace(Place place)throws HomeException;

	/**
	 * 
	 * @param znode
	 * @return
	 * @throws HomeException
	 */
	public ReturnType bindingSensor(ZNode znode)throws HomeException;

	/**
	 * 
	 * @param alarm
	 * @return
	 * @throws HomeException
	 */
	public ReturnType createAlarm(Alarm alarm)throws HomeException;

	/**
	 * 
	 * @param alarm
	 * @return
	 * @throws HomeException
	 */
	public ReturnType updateAlarm(Alarm alarm)throws HomeException;
	
	/**
	 * 
	 * @param device
	 * @return
	 * @throws HomeException
	 */
	public ReturnType updateDevice(Device device)throws HomeException;
	
	/**
	 * 
	 * @param endPoint
	 * @return
	 * @throws HomeException
	 */
	public ReturnType updateEndPoint(DeviceEndPoint endPoint)throws HomeException;
	
	/**
	 * 
	 * @param alarm
	 * @return
	 * @throws HomeException
	 */
	public ReturnType deleteAlarm(Alarm alarm)throws HomeException;

	/**
	 * 
	 * @param area
	 * @return
	 * @throws HomeException
	 */
	public ReturnType deleteArea(Area area)throws HomeException;

	/**
	 * 
	 * @param place
	 * @return
	 * @throws HomeException
	 */
	public ReturnType deletePlace(Place place)throws HomeException;

	/**
	 * 
	 * @param znode
	 * @return
	 * @throws HomeException
	 */
	public ReturnType deleteNode(ZNode znode)throws HomeException;

	/**
	 * 
	 * @param area
	 * @return
	 * @throws HomeException
	 */
	public ReturnType createArea(Area area)throws HomeException;

	/**
	 * 
	 * @param area
	 * @param name
	 * @return
	 * @throws HomeException
	 */
	public ReturnType updateAreaName(Area area, String name)throws HomeException;

	/**
	 * 
	 * @param place
	 * @return
	 * @throws HomeException
	 */
	public ReturnType updatePlace(Place place)throws HomeException;

	/**
	 * 
	 * @param device
	 * @param userId
	 * @return
	 * @throws HomeException
	 */
	public ReturnType checkGateway(Device device, String userId)throws HomeException;

	/**
	 * 
	 * @param device
	 * @return
	 * @throws HomeException
	 */
	public ReturnType canCreateGateway(Device device)throws HomeException;

	/**
	 * 
	 * @param gateway
	 * @param copyGateway
	 * @return
	 * @throws HomeException
	 */
	public ReturnType shareGateway(ZNode gateway, ZNode copyGateway)throws HomeException;

	/**
	 * 
	 * @param znode
	 * @param scene
	 * @return
	 * @throws HomeException
	 */
	public ReturnType deleteScene(ZNode znode, Scene scene)throws HomeException;

	/**
	 * 
	 * @param znode
	 * @param lightValue
	 * @return
	 * @throws HomeException
	 */
	public ReturnType setLightSensorInfo(ZNode znode, Integer lightValue)throws HomeException;
	
		
	
	
	/**
	 * 
	 * @param znode
	 * @param scene
	 * @return
	 * @throws HomeException
	 */
	public ReturnType updateScene(ZNode znode, Scene scene)throws HomeException;

	/**
	 * 
	 * @param znode
	 * @param scene
	 * @return
	 * @throws HomeException
	 */
	public ReturnType creatScene(ZNode znode, Scene scene)throws HomeException;

	/**
	 * 
	 * @param place
	 * @param sensorListByIds
	 * @return
	 * @throws HomeException
	 */
	public ReturnType addSensor2Start(Place place, List<ZNode> sensorListByIds)throws HomeException;

	/**
	 * 
	 * @param place
	 * @param sensorListByIds
	 * @return
	 * @throws HomeException
	 */
	public ReturnType addSensor2Index(Place place, List<ZNode> sensorListByIds)throws HomeException;

	/**
	 * 
	 * @param place
	 * @param sensorListByIds
	 * @return
	 * @throws HomeException
	 */
	public ReturnType removeSensor2Index(Place place, List<ZNode> sensorListByIds)throws HomeException;

	/**
	 * 
	 * @param place
	 * @param sensorListByIds
	 * @return
	 * @throws HomeException
	 */
	public ReturnType removeSensor2Start(Place place, List<ZNode> sensorListByIds)throws HomeException;

	/**
	 * 
	 * @param device
	 * @return
	 * @throws HomeException
	 */
	public ReturnType activateDevice(Device device) throws HomeException;

	/**
	 * 
	 * @param endPoint
	 * @param value
	 * @return
	 * @throws HomeException
	 */
	public ReturnType addAlarm(DeviceEndPoint endPoint, Map value) throws HomeException;

	/**
	 * 
	 * @param endPoint
	 * @param value
	 * @return
	 * @throws HomeException
	 */
	public ReturnType addData(DeviceEndPoint endPoint, Map value)throws HomeException;

	/**
	 * 
	 * @param device
	 * @return
	 * @throws HomeException
	 */
	public ReturnType registerSensor(Device device)throws HomeException;

	/**
	 * 
	 * @param device
	 * @return
	 * @throws HomeException
	 */
	public ReturnType deleteDevice(Device device)throws HomeException;

	/**
	 * 
	 * @param endPoint
	 * @return
	 * @throws HomeException
	 */
	public ReturnType sensorOffLine(Device endPoint)throws HomeException;

	/**
	 * 
	 * @param endPoint
	 * @return
	 * @throws HomeException
	 */
	public ReturnType sensorOnLine(Device endPoint)throws HomeException;
	
	/**
	 * 
	 * @param serialno
	 * @return
	 * @throws HomeException
	 */
	public ReturnType createGateway(String serialno)throws HomeException;
	
	/**
	 * 
	 * @param znode
	 * @param vlaue
	 * @throws HomeException
	 */
	public void setOutLetSensorInfo(ZNode znode, boolean vlaue)throws HomeException;
	
	/**
	 * 
	 * @param sensorId
	 * @param startTime
	 * @param endTime
	 * @param timeunit
	 * @return
	 * @throws HomeException
	 */
	public List<Msg> getSensorSetData(String sensorId, Long startTime, Long endTime, Integer timeunit) throws HomeException;

	/**
	 * 
	 * @param msg
	 * @return
	 * @throws HomeException
	 */
	public Msg updateCommandMsgStatus(Msg msg)throws HomeException;
	


	<T extends Command>Future<T> sendCommand(Command command, Integer delayTime);
}
