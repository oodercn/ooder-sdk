package net.ooder.agent.client.home.client;

import net.ooder.agent.client.iot.*;
import net.ooder.agent.client.iot.enums.DeviceDataTypeKey;
import net.ooder.agent.client.iot.enums.DeviceStatus;
import net.ooder.agent.client.iot.json.SensorInfo;
import  net.ooder.annotation.MethodChinaName;
import  net.ooder.jds.core.User;
import  net.ooder.msg.Msg;
import  net.ooder.msg.SensorMsg;
import  net.ooder.server.JDSClientService;

import java.util.Date;
import java.util.List;

public interface AppClient extends JDSClientService {

    public static final String CTX_USER_ID = "AppEngine.USERID";

    public static final String CTX_USERS = "AppEngine.USERS";

    /**
     * 修改报警规则
     * 
     * @return
     * @throws HomeException
     */
    Alarm updateAlarm(Alarm alarm) throws HomeException;

    /**
     * 
     * @param sensorId
     * @param attributeName
     * @param value
     * @return
     * @throws HomeException
     */
    SensorInfo updateSensorValue(String sensorId, DeviceDataTypeKey attributeName, String value) throws HomeException;

    /**
     * 添加报警规则周期
     * 
     * @param sensorId
     * @return
     * @throws HomeException
     */
    Alarm createAlarm(String sensorId) throws HomeException;

    /**
     * 删除报警规则周期 String alarmid
     * 
     * @throws HomeException
     */
    void deleteAlarm(String alarmId) throws HomeException;

    /**
     * 修改房间信息
     * 
     * @param areaId
     * @return
     */
    void updateAreaName(String areaId, String name) throws HomeException;

    /**
     * 修改网关别名
     * 
     * @param ieee
     * @return
     */
    void updateGateway(String ieee, String name, String bindingaccount, String gatewayId, String placeId) throws HomeException;

    /**
     * 修改网关状态
     * 
     * @param gatewayId
     * @param status
     */
    void updateGatewayStatus(String gatewayId, DeviceStatus status) throws HomeException;

    /**
     * 修改传感器别名
     * 
     * @param sensorId
     * @return
     */
    ZNode updateSensorName(String sensorId, String name) throws HomeException;

    /**
     * 删除房间信息
     * 
     * @param areaId
     * @return
     */
    void deleteArea(String areaId) throws HomeException;

    /**
     * 获取所有房间信息
     * 
     * @param placeId
     * @return
     */
    List<Area> getAllAreaByPlaceId(String placeId) throws HomeException;

    /**
     * 获取网关所有者
     * 
     * @param ieee
     * @return
     * @throws HomeException
     */
    public User getMainUserInfo(String ieee) throws HomeException;

    /**
     * 是否允许创建网关
     * 
     * @param ieee
     * @return
     * @throws HomeException
     */
    public boolean canCreateGateway(String ieee, String placeId) throws HomeException;

    /**
     * 创建网关
     * 
     * @param ieee
     * @return
     */
    public ZNode createGateway(String ieee, String placeId) throws HomeException;

    /**
     * 绑定宽带账户
     * 
     * @param wbaccount
     * @param ieee
     * @return
     */
    ZNode bindingGateway(String wbaccount, String ieee) throws HomeException;

    /**
     * 根据设备号获取设备
     * 
     * @param ieee
     * @return
     * @throws HomeException
     */
    ZNode getZNodeByIeee(String ieee) throws HomeException;

    /**
     * 创建住所
     * 
     * @param name
     * @return
     */
    Place createPlace(String name) throws HomeException;

    /**
     * 修改住所信息
     * 
     * @param place
     * @return
     */
    void updatePlace(Place place) throws HomeException;

    /**
     * 删除住所信息
     * 
     * @param placeId
     * @return
     */
    void deletePlace(String placeId) throws HomeException;

    /**
     * 获取所有住所信息
     *
     * @return
     */
    List<Place> getAllPlace() throws HomeException;

    /**
     * 添加传感器
     * 
     * @param gatewayid
     * @param type
     * @return
     * @throws HomeException
     */
    public ZNode addDevice(String gatewayid, Integer type, String ieee) throws HomeException;

    /**
     * 添加传感器
     * 
     * @param gatewayid
     * @param ieee
     * @return
     * @throws HomeException
     */
    public void sendAddSensorCommand(String gatewayid, String ieee) throws HomeException;



    /**
     * 删除节点
     * 
     * @param sersorId
     * @throws HomeException
     */
    void deleteZNode(String sersorId) throws HomeException;

    /**
     * 获取所有的传感器类型
     * 
     * @return
     */
    List<Sensortype> getSensorTypesByGatewayId(String gatewayId) throws HomeException;

    /**
     * 获取指定编码的传感器类型
     * 
     * @return
     */
    Sensortype getSensorTypesByNo(Integer typno) throws HomeException;

    /**
     * 根据住所ID获取所有报警信息
     * 
     * @param placeId
     * @return
     */
    List<SensorMsg> getAlarmMessageByPlaceId(String placeId) throws HomeException;

    /**
     * 根据房间ID获取房间信息
     * 
     * @param ids
     * @return
     * @throws HomeException
     */
    List<Area> getAreasByIds(List<String> ids) throws HomeException;

    /**
     * 共享网关
     * 
     * @param ieee
     * @param mainaccount
     * @param mainpassword
     * @throws HomeException
     */
    public Place shareGateway(String ieee, String mainaccount, String mainpassword, String placeId) throws HomeException;

    /**
     * 获取指定传感器规则信息
     * 
     * @param sensorId
     * @return
     * @throws HomeException
     */
    public List<Alarm> getAlarmBySensorId(String sensorId) throws HomeException;

    /**
     * 添加灯场景
     * 
     * @param scene
     * @throws HomeException
     */
    void addLightSensorScene(Scene scene) throws HomeException;

    /**
     * 删除灯灯场景
     * 
     * @param sensorSceneId
     * @throws HomeException
     */
    void deleteScene(String sensorSceneId) throws HomeException;

    /**
     * 获取灯灯场景
     * 
     * @param sensorId
     * @return
     * @throws HomeException
     */
    List<Scene> getSceneBySensorId(String sensorId) throws HomeException;

    /**
     * 获取灯场景
     * 
     * @param scenId
     * @return
     * @throws HomeException
     */
    Scene geSceneById(String scenId) throws HomeException;

    /**
     * 设置灯亮度
     * 
     * @param sensorSceneId
     * @param lightValue
     * @throws HomeException
     */
    void setLightSensor(String sensorSceneId, Integer lightValue) throws HomeException;

    /**
     * 控制插座
     * 
     * @param sensorId
     * @param vlaue/boolean
     * @throws HomeException
     */
    void setOutLetSensor(String sensorId, boolean vlaue) throws HomeException;

    /**
     * 更新灯规则
     * 
     * @param info
     * @throws HomeException
     */
    void updateScene(Scene info) throws HomeException;

    /**
     * 获取共享网管用户
     * 
     * @param gatewayId
     * @return
     */
    List<User> getShareUser(String gatewayId) throws HomeException;

    /**
     * 分页获取传感器历史数据
     * 
     * @param sensorId
     * @param startTime
     * @param endTime
     * @param currentIndex
     * @param pageSize
     * @return
     */
    List<SensorMsg> getSensorHistoryData(String sensorId, Date startTime, Date endTime, Integer currentIndex, Integer pageSize) throws HomeException;

   
    /**
     * 分页获取传感器历史数据
     * 
     * @param sensorId
     * @param currentIndex
     *            当前页
     * @param pageSize
     *            每页数量
     * @return
     */
    List<SensorMsg> getLastSensorHistoryData(String sensorId, Integer currentIndex, Integer pageSize) throws HomeException;

    /**
     * 根据家ID获取所有节点
     * 
     * @param placeId
     * @return
     * @throws HomeException
     */
    public List<ZNode> getAllGatewayByPlaceId(String placeId) throws HomeException;

    /**
     * 根据家ID获取所有节点
     *
     * @return
     * @throws HomeException
     */
    public List<ZNode> getSensorByIeees(List<String> ieees) throws HomeException;

    /**
     * 启用传感器
     * 
     * @param placeId
     * @param sensorIds
     * @throws HomeException
     */
    public void addSensor2Start(String placeId, String... sensorIds) throws HomeException;

    /**
     * 添加传感器到首页
     * 
     * @param placeId
     * @param sensorIds
     * @throws HomeException
     */
    public void addSensor2Index(String placeId, String... sensorIds) throws HomeException;

    /**
     * 移除传感器从首页
     * 
     * @param placeId
     * @param sensorIds
     * @throws HomeException
     */
    public void removeSensor2Index(String placeId, String... sensorIds) throws HomeException;

    /**
     * 停用传感器
     * 
     * @param placeId
     * @param sensorIds
     * @throws HomeException
     */
    public void removeSensor2Start(String placeId, String... sensorIds) throws HomeException;

    /**
     * 向手机发送消息
     *
     * @param msg
     * @throws HomeException
     */
    public boolean sendAlarmMsg(SensorMsg msg) throws HomeException;

    /**
     * 向手机发送消息
     *
     * @param msg
     * @throws HomeException
     */
    public boolean sendDataMsg(SensorMsg msg) throws HomeException;

    /**
     * 向手机发送消息
     * 
     * @param msg
     * @throws HomeException
     */
    public boolean sendSystemMsg(Msg msg) throws HomeException;

    /**
     * 开始事务操作
     * 
     * @throws HomeException
     */
    @MethodChinaName(cname = "开始事务操作", returnStr = "beginTransaction()", display = false)
    public void beginTransaction() throws HomeException;

    /**
     * 提交事务操作
     * 
     * @throws HomeException
     */
    @MethodChinaName(cname = "提交事务操作", returnStr = "commitTransaction()", display = false)
    public void commitTransaction() throws HomeException;

    /**
     * 回滚事务操作
     * 
     * @throws HomeException
     */
    @MethodChinaName(cname = "回滚事务操作", returnStr = "rollbackTransaction()", display = false)
    public void rollbackTransaction() throws HomeException;

    /**
     * 
     * @param placeId
     * @return
     * @throws HomeException
     */
    public List<ZNode> getIndexSensorList(String placeId) throws HomeException;

    /**
     * 
     * @param sensorId
     * @return
     * @throws HomeException
     */
    public Scene creatScene(String sensorId) throws HomeException;

    /**
     * 
     * @param alarmid
     * @return
     * @throws HomeException
     */
    public Alarm getAlarmById(String alarmid) throws HomeException;

    /**
     * 
     * @param name
     * @param placeId
     * @return
     * @throws HomeException
     */
    public Area createArea(String name, String placeId) throws HomeException;

    /**
     * 
     * @param areaId
     * @return
     * @throws HomeException
     */
    public Area getAreaById(String areaId) throws HomeException;

    /**
     * 
     * @param placeId
     * @return
     * @throws HomeException
     */
    public Place getPlaceById(String placeId) throws HomeException;

    /**
     * 
     * @param id
     * @return
     * @throws HomeException
     */
    public Scene getSceneById(String id) throws HomeException;

    /**
     * 
     * @param gatewayId
     * @return
     * @throws HomeException
     */
    public List<ZNode> getAllChildNode(String gatewayId) throws HomeException;

    /**
     * 
     * @param placeId
     * @return
     * @throws HomeException
     */
    public List<ZNode> getAllZNodeByPlaceId(String placeId) throws HomeException;

    /**
     * 
     * @param znodeId
     * @return
     * @throws HomeException
     */
    public ZNode getZNodeById(String znodeId) throws HomeException;


    public void setClientService(JDSClientService client);


}
