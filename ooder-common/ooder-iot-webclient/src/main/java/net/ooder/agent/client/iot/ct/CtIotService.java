package net.ooder.agent.client.iot.ct;

import net.ooder.agent.client.command.Command;
import net.ooder.agent.client.iot.*;
import net.ooder.common.JDSException;

import java.util.List;
import java.util.concurrent.Future;

public interface CtIotService {



    public void clearDeviceCache(String deviceId) throws JDSException;


    /**
     * @param deviceId
     * @return
     */
    public Device getDeviceById(String deviceId) throws JDSException;

    /**
     * 获取所有门店
     *
     * @return
     */
    public List<Place> getAllPlace() throws JDSException;


    /**
     * 根据门店ID获取门店
     *
     * @return
     */
    public Place getPlaceById(String placeId) throws JDSException;

    /**
     * @param areaId
     * @return
     */
    public Area getAreaById(String areaId) throws JDSException;

    /**
     * @param endPointieee
     * @return
     */
    public DeviceEndPoint getEndPointByIeee(String endPointieee) throws JDSException;

    /**
     * @param endPointId
     * @return
     */
    public DeviceEndPoint getEndPointById(String endPointId) throws JDSException;

    /**
     * @param znodeId
     * @return
     */
    public ZNode getZNodeById(String znodeId) throws JDSException;


    /**
     * @param commandId
     * @return
     */
    public <T extends Command>  T getCommandById(String commandId) throws JDSException;


    /**
     * 获取当前账户下所有设备
     *
     * @param account
     * @return
     */
    public List<DeviceEndPoint> getSensorByBindAccount(String account) throws JDSException;


    /**
     * 根据编码类型获取Sensortype信息
     * @param devicetype
     * @return
     * @throws JDSException
     */
    public Sensortype getSensorTypesByNo(String devicetype) throws JDSException;

    /**
     * 获取全部设备类型
     * @return
     * @throws JDSException
     */
    public List<Sensortype> getAllSensorTypes() throws JDSException;

    /**
     * 发送命令
     * @param command
     * @param delayTime
     * @return
     * @throws JDSException
     */
    public Future<Command> sendCommand(Command command, Integer delayTime) throws JDSException;

    /**
     *
     * @param gatewayieee
     * @return
     * @throws JDSException
     */
    public Device getDeviceByIeee(String gatewayieee) throws JDSException;


}
