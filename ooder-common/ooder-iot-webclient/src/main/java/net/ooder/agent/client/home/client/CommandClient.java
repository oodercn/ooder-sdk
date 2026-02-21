package net.ooder.agent.client.home.client;

import net.ooder.agent.client.command.*;
import net.ooder.agent.client.iot.AppLockPassword;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.json.NetworkInfo;
import  net.ooder.server.JDSClientService;

import java.util.List;
import java.util.concurrent.Future;

public interface CommandClient {

    public static final String CTX_USER_ID = "GwEngine.USERID";

    public boolean isOnLine() throws HomeException;

    /**
     * 开启红外学习
     *
     * @param sensorieee
     * @throws HomeException
     */
    public Future<IRLearnCommand> sendIRLearnCommand(String sensorieee) throws HomeException;

    public Future<IRLearnCommand> sendIRLearnCommand(String sensorieee, Integer delayTime) throws HomeException;

    /**
     * 关闭报警
     *
     * @param sensorieee
     * @throws HomeException
     */
    public Future<ReleaseAlarmCommand> sendReleaseAlarmCommand(String sensorieee) throws HomeException;

    public Future<ReleaseAlarmCommand> sendReleaseAlarmCommand(String sensorieee, Integer delayTime) throws HomeException;

    /**
     * 清除数据
     *
     * @param sensorieee
     * @throws HomeException
     */
    public Future<ClearDataCommand> sendClearDataCommand(String sensorieee) throws HomeException;

    public Future<ClearDataCommand> sendClearDataCommand(String sensorieee, Integer delayTime) throws HomeException;

    /**
     * 修改属性
     *
     * @throws HomeException
     */
    public Future<AttributeWriteCommand> sendAttributeWriteCommand(String sensorieee, List<HAAttribute> attributes) throws HomeException;

    public Future<AttributeWriteCommand> sendAttributeWriteCommand(String sensorieee,List<HAAttribute> attributes, Integer delayTime) throws HomeException;

    /**
     * 修改属性
     *
     * @param sensorieee
     * @throws HomeException
     */
    public Future<AttributeWriteCommand> sendAttributeWriteCommand(String sensorieee, String attriname, String value) throws HomeException;

    public Future<AttributeWriteCommand> sendAttributeWriteCommand(String sensorieee, String attriname, String value, Integer delayTime) throws HomeException;

    /**
     * 发送上报网络信息命令
     *
     * @param sensorieee
     * @throws HomeException
     */
    public Future<ChannelNegotiateReportCommand> sendChannelNegotiateReportCommand(String sensorieee) throws HomeException;

    public Future<ChannelNegotiateReportCommand> sendChannelNegotiateReportCommand(String sensorieee, Integer delayTime) throws HomeException;

    /**
     * 发送修改网络信息命令
     *
     * @param networkInfo
     * @throws HomeException
     */
    public Future<ChannelNegotiateCommand> sendChannelNegotiateCommand(NetworkInfo networkInfo) throws HomeException;

    public Future<ChannelNegotiateCommand> sendChannelNegotiateCommand(NetworkInfo networkInfo, Integer delayTime) throws HomeException;

    /**
     * 发送红外命令
     *
     * @param sensorieee
     * @param value
     * @throws HomeException
     */
    public Future<IRControlCommand> sendIRControlCommand(String sensorieee, String value) throws HomeException;

    public Future<IRControlCommand> sendIRControlCommand(String sensorieee, String value, Integer delayTime) throws HomeException;

    /**
     * 设备绑定
     *
     * @param sourcedev
     * @param destdev   on/off
     * @return
     * @throws HomeException
     */

    public Future<BindDeviceCommand> sendBindDeviceCommand(String sourcedev, String destdev) throws HomeException;

    public Future<BindDeviceCommand> sendBindDeviceCommand(String sourcedev, String destdev, Integer delayTime) throws HomeException;

    /**
     * 设备解除
     *
     * @param sourcedev
     * @param sourcedev
     * @param destdev
     * @param delayTime on/off
     * @return
     * @throws HomeException
     */

    public Future<UnBindDeviceCommand> sendUNBindDeviceCommand(String sourcedev, String destdev, Integer delayTime) throws HomeException;

    public Future<UnBindDeviceCommand> sendUNBindDeviceCommand(String sourcedev, String destdev) throws HomeException;

    /**
     * 控制灯亮度
     *
     * @param sessionieee
     * @param value
     * @param value
     * @return
     * @throws HomeException
     */
    public Future<MoveToLevelCommand> sendLightCommand(String sessionieee, Integer value, Integer delayTime) throws HomeException;

    public Future<MoveToLevelCommand> sendLightCommand(String sessionieee, Integer value) throws HomeException;

    /**
     * @param sessionieee
     * @param operation
     * @param delayTime
     * @return
     * @throws HomeException
     */
    public Future<OperatorCommand> sendOnOutLetCommand(String sessionieee, boolean operation, Integer delayTime) throws HomeException;

    public Future<OperatorCommand> sendOnOutLetCommand(String sessionieee, boolean operation) throws HomeException;

    /**
     * @throws HomeException
     */
    public Future<PasswordCommand> sendAddLockPasswordCommand(AppLockPassword lockPassword) throws HomeException;

    public Future<PasswordCommand> sendAddLockPasswordCommand(AppLockPassword lockPassword, Integer delayTime) throws HomeException;

    /**
     * @param sessionieee
     * @param groupId
     * @param passId
     * @throws HomeException
     */
    public Future<DeletePasswordCommand> sendDeleteLockPasswordCommand(String sessionieee, String groupId, Integer passId) throws HomeException;

    public Future<DeletePasswordCommand> sendDeleteLockPasswordCommand(String sessionieee, String groupId, Integer passId, Integer delayTime) throws HomeException;

    /**
     * @param sessionieee
     * @throws HomeException
     */
    public Future<ClearPasswordCommand> sendClearPasswordCommand(String sessionieee) throws HomeException;

    public Future<ClearPasswordCommand> sendClearPasswordCommand(String sessionieee, Integer delayTime) throws HomeException;

    /**
     * @param delayTime
     * @return
     * @throws HomeException
     */
    public Future<SensorReportCommand> sendSensorReportCommand(Integer delayTime) throws HomeException;

    public Future<SensorReportCommand> sendSensorReportCommand() throws HomeException;

    /**
     * @param delayTime
     * @return
     * @throws HomeException
     */
    public Future<SyncTimeCommand> sendSyncTimeCommand(Integer delayTime) throws HomeException;

    public Future<SyncTimeCommand> sendSyncTimeCommand() throws HomeException;

    /**
     * @param sessionieee
     * @param delayTime
     * @return
     * @throws HomeException
     */
    public Future<FindSensorCommand> sendFindSensorCommand(String sessionieee, Integer delayTime) throws HomeException;

    public Future<FindSensorCommand> sendFindSensorCommand(String sessionieee) throws HomeException;

    /**
     * 发送闪灯命令
     *
     * @param sessionieee
     * @param times
     * @param delayTime
     * @return
     * @throws HomeException
     */
    public Future<IdentifyCommand> sendIdentifyDeviceCommand(String sessionieee, Integer times, Integer delayTime) throws HomeException;

    public Future<IdentifyCommand> sendIdentifyDeviceCommand(String sessionieee, Integer times) throws HomeException;

    /**
     * @param sessionieee
     * @param delayTime
     * @return
     * @throws HomeException
     */
    public Future<DataReportCommand> sendDataReportCommand(String sessionieee, Integer delayTime) throws HomeException;

    public Future<DataReportCommand> sendDataReportCommand(String sessionieee) throws HomeException;

    /**
     * @return
     * @throws HomeException
     */
    public Future<InitGatewayCommand> sendInitGatewayCommand(Integer delayTime) throws HomeException;

    public Future<InitGatewayCommand> sendInitGatewayCommand() throws HomeException;

    /**
     * @return
     * @throws HomeException
     */
    public Future<InitFactoryCommand> sendInitFactoryCommand(Integer delayTime) throws HomeException;

    public Future<InitFactoryCommand> sendInitFactoryCommand() throws HomeException;

    /**
     * @return
     * @throws HomeException
     */
    public Future<DebugCommand> sendDebugCommand(String commandUrl,String mainUrl) throws HomeException;

    public Future<DebugCommand> sendDebugCommand(String commandUrl,String mainUrl, Integer delayTime) throws HomeException;

    /**
     * @param sensorieee
     * @param sensorType
     * @param delayTime
     * @return
     * @throws HomeException
     */
    public Future<RemoveSensorCommand> sendRemoveSensorCommand(String sensorieee, Integer sensorType, Integer delayTime) throws HomeException;

    public Future<RemoveSensorCommand> sendRemoveSensorCommand(String sensorieee, Integer sensorType) throws HomeException;

    /**
     * @param serialno
     * @param sensorType
     * @return
     * @throws HomeException
     */
    public Future<AddSensorCommand> sendAddSensorCommand(String serialno, Integer sensorType, Integer delayTime) throws HomeException;

    public Future<AddSensorCommand> sendAddSensorCommand(String serialno, Integer sensorType) throws HomeException;

    /**
     * 固件升级命令
     *
     * @throws HomeException
     */
    public Future<FirmwareDownloadCommand> sendFirmwareDownloadCommand(String vfsUrl, Integer delayTime) throws HomeException;

    public Future<FirmwareDownloadCommand> sendFirmwareDownloadCommand(String vfsUrl) throws HomeException;

    /**
     * 检查更新
     *
     * @throws HomeException
     */
    public Future<Command> sendCheckUpgradeCommand(String sensorieee, Integer delayTime) throws HomeException;

    public Future<Command> sendCheckUpgradeCommand(String sensorieee) throws HomeException;

    public <T extends Command>Future<T > sendCommand(T command, Integer delayTime) throws HomeException;

    public  <T extends Command>  T getCommandById(String commandId);

    public GWClient getGWClient()throws HomeException;

    public void setClientService(JDSClientService client);

}
