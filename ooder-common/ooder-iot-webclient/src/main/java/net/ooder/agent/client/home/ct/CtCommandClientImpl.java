package net.ooder.agent.client.home.ct;

import com.alibaba.fastjson.JSONObject;
import net.ooder.agent.client.command.*;
import net.ooder.agent.client.iot.AppLockPassword;
import net.ooder.agent.client.iot.Device;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.ct.CtIotCacheManager;
import net.ooder.agent.client.iot.ct.CtIotFactory;
import net.ooder.agent.client.iot.json.NetworkInfo;
import  net.ooder.common.ConfigCode;
import  net.ooder.common.JDSConstants;
import  net.ooder.common.JDSException;
import  net.ooder.common.logging.Log;
import  net.ooder.common.logging.LogFactory;
import  net.ooder.context.JDSContext;
import  net.ooder.engine.ConnectInfo;
import  net.ooder.engine.ConnectionHandle;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.agent.client.enums.CommandEnums;
import  net.ooder.agent.client.home.client.AdminClient;
import  net.ooder.agent.client.home.client.CommandClient;
import  net.ooder.agent.client.home.client.GWClient;
import  net.ooder.agent.client.home.engine.HomeServer;
import  net.ooder.agent.client.home.engine.IOTDataEngine;
import  net.ooder.agent.client.home.event.GatewayEvent;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.JDSServer;
import  net.ooder.vfs.FileInfo;
import net.ooder.vfs.ct.CtVfsFactory;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class CtCommandClientImpl implements CommandClient {
    private ConfigCode configCode;

    private JDSClientService client;

    private IOTDataEngine commandEngine;

    private String gatewayieee;

    private ConnectInfo connectInfo;

    public static final String THREAD_LOCK = "Thread Lock";

    static Map<String, ScheduledExecutorService> commandServiceMap = new HashMap<String, ScheduledExecutorService>();


    CtCommandClientImpl() {
        try {
            commandEngine = HomeServer.getMsgEngine(configCode);
        } catch (HomeException e) {
            e.printStackTrace();
        }

        if (commandEngine==null){
            commandEngine = CtMsgDataEngine.getEngine(configCode);
        }
    }

    public CtCommandClientImpl(JDSClientService client, String gatewayieee) {
        this.client = client;
        this.connectInfo = client.getConnectInfo();
        this.gatewayieee = gatewayieee;
        this.configCode = client.getConfigCode();
        try {
            commandEngine = HomeServer.getMsgEngine(configCode);
        } catch (HomeException e) {
            e.printStackTrace();
        }

        if (commandEngine==null){
            commandEngine = CtMsgDataEngine.getEngine(configCode);
        }

    }

    public static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, CommandClient.class);

    public Future<AttributeWriteCommand> sendAttributeWriteCommand(String sensorieee, List<HAAttribute> attributes) throws HomeException {
        return sendAttributeWriteCommand(sensorieee, attributes);
    }



    public Future<AttributeWriteCommand> sendAttributeWriteCommand(String sensorieee,List<HAAttribute> attributes, Integer delayTime) throws HomeException {
        AttributeWriteCommand command = new AttributeWriteCommand();
        command.setAttributes(attributes);
        command.setSensorieee(sensorieee);
        return sendCommand(command, delayTime);
    }

    public Future<AttributeWriteCommand> sendAttributeWriteCommand(String sensorieee, String attriname, String value) throws HomeException {
        return sendAttributeWriteCommand(sensorieee, attriname, value, 0);
    }

    public Future<AttributeWriteCommand> sendAttributeWriteCommand(String sensorieee, String attriname, String value, Integer delayTime) throws HomeException {
        AttributeWriteCommand command = new AttributeWriteCommand();
        HAAttribute attribute = new HAAttribute();
        attribute.setAttributename(attriname);
        attribute.setValue(value);
        command.getAttributes().add(attribute);

        return sendCommand(command, 0);
    }

    public Future<ChannelNegotiateCommand> sendChannelNegotiateCommand(NetworkInfo networkInfo) throws HomeException {
        return sendChannelNegotiateCommand(networkInfo, 0);
    }

    public Future<ChannelNegotiateCommand> sendChannelNegotiateCommand(NetworkInfo networkInfo, Integer delayTime) throws HomeException {
        ChannelNegotiateCommand command = new ChannelNegotiateCommand();

        command.setGatewayieee(gatewayieee);
        Device device = null;
        try {
            device = CtIotCacheManager.getInstance().getDeviceByIeee(gatewayieee);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        command.setSystemCode(device.getSubsyscode());

        command.setWifi(networkInfo.getWifi());
        command.setWlan(networkInfo.getWlan());
        return sendCommand(command, delayTime);
    }

    public Future<ChannelNegotiateReportCommand> sendChannelNegotiateReportCommand(String sensorieee) throws HomeException {
        return sendChannelNegotiateReportCommand(sensorieee, 0);
    }

    public Future<ChannelNegotiateReportCommand> sendChannelNegotiateReportCommand(String sensorieee, Integer delayTime) throws HomeException {
        ChannelNegotiateReportCommand command = new ChannelNegotiateReportCommand();

        return sendCommand(command, delayTime);

    }

    public Future<ClearDataCommand> sendClearDataCommand(String sensorieee) throws HomeException {
        return sendClearDataCommand(sensorieee, 0);
    }

    public Future<ClearDataCommand> sendClearDataCommand(String sensorieee, Integer delayTime) throws HomeException {
        ClearDataCommand command = new ClearDataCommand();
        command.setSensorieee(sensorieee);
        return sendCommand(command, delayTime);
    }

    public Future<DebugCommand> sendDebugCommand(String commandUrl,String mainUrl) throws HomeException {
        return sendDebugCommand(commandUrl,mainUrl,0);
    }

    public Future<DebugCommand> sendDebugCommand(String commandUrl,String mainUrl,Integer delayTime) throws HomeException {
        DebugCommand command=new DebugCommand();
        command.setCommandServerUrl(commandUrl);
        command.setMainServerUrl(mainUrl);
        Device device= null;
        try {
            device = CtIotFactory.getCtIotService().getDeviceByIeee(this.gatewayieee);
        } catch (JDSException e) {
           throw  new HomeException(e);
        }
        command.setGatewayAccount(device.getBindingaccount());
        command.setKeyword(device.getDeviceid());
        return sendCommand(command, delayTime);
    }

    public Future<FirmwareDownloadCommand> sendFirmwareDownloadCommand(String vfsUrl, Integer delayTime) throws HomeException {

        FirmwareDownloadCommand command=new FirmwareDownloadCommand();
        try {
            FileInfo fileInfo= CtVfsFactory.getCtVfsService().getFileByPath(vfsUrl);

            command.setValue(vfsUrl);
            command.setMd5(fileInfo.getCurrentVersonFileHash());
            command.setSize(fileInfo.getCurrentVersion().getLength().toString());
        } catch (JDSException e) {
            throw new HomeException(e);
        }
        return this.sendCommand(command, delayTime);
    }

    public Future<FirmwareDownloadCommand> sendFirmwareDownloadCommand(String vfsUrl) throws HomeException {
       return sendFirmwareDownloadCommand(vfsUrl,0);
    }

    public Future<IRControlCommand> sendIRControlCommand(String sensorieee, String value) throws HomeException {
        return sendIRControlCommand(sensorieee, value, 0);
    }

    public Future<IRControlCommand> sendIRControlCommand(String sensorieee, String value, Integer delayTime) throws HomeException {
        IRControlCommand command = new IRControlCommand();

        command.setGatewayieee(gatewayieee);
        command.setSensorieee(sensorieee);
        command.setValue(value);
        return sendCommand(command, delayTime);
    }

    public boolean isOnLine() throws HomeException {

        if (client != null && client.getConnectInfo() != null) {
            Set<JDSSessionHandle> sessionHandleList = null;
            try {
                sessionHandleList = JDSServer.getInstance().getSessionHandleList(client.getConnectInfo());
            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if ((sessionHandleList == null) || (sessionHandleList.size() == 0)) {
                return true;
            }
        }
        return false;
    }

    public Future<IRLearnCommand> sendIRLearnCommand(String sensorieee) throws HomeException {

        return sendIRLearnCommand(sensorieee, 0);
    }

    public Future<IRLearnCommand> sendIRLearnCommand(String sensorieee, Integer delayTime) throws HomeException {
        IRLearnCommand command = new IRLearnCommand();
        command.setGatewayieee(gatewayieee);
        command.setSensorieee(sensorieee);
        return sendCommand(command, delayTime);
    }

    public Future<InitFactoryCommand> sendInitFactoryCommand(Integer delayTime) throws HomeException {
        InitFactoryCommand command = new InitFactoryCommand();

        command.setGatewayieee(gatewayieee);
        return sendCommand(command, delayTime);
    }

    public Future<InitFactoryCommand> sendInitFactoryCommand() throws HomeException {

        return sendInitFactoryCommand(0);
    }

    public Future<ReleaseAlarmCommand> sendReleaseAlarmCommand(String sensorieee) throws HomeException {
        return sendReleaseAlarmCommand(sensorieee, 0);
    }

    public Future<ReleaseAlarmCommand> sendReleaseAlarmCommand(String sensorieee, Integer delayTime) throws HomeException {
        ReleaseAlarmCommand command = new ReleaseAlarmCommand();

        command.setGatewayieee(gatewayieee);
        command.setSensorieee(sensorieee);
        return sendCommand(command, delayTime);
    }

    public Future<UnBindDeviceCommand> sendUNBindDeviceCommand(String sourcedev, String destdev, Integer delayTime) throws HomeException {
        UnBindDeviceCommand command = new UnBindDeviceCommand();

        command.setGatewayieee(gatewayieee);
        command.setSourcedev(sourcedev);
        command.setDestdev(destdev);
        command.setClusterid(BindClusterIdParamEnums.COMMAND_BINDDEVICE_CLUSTER_ONOFF.getType());
        command.setGatewayieee(gatewayieee);
        return sendCommand(command, delayTime);
    }

    public Future<UnBindDeviceCommand> sendUNBindDeviceCommand(String sourcedev, String destdev) throws HomeException {
        return sendUNBindDeviceCommand(sourcedev, destdev, Integer.valueOf(0));
    }

    public Future<MoveToLevelCommand> sendLightCommand(String sensorieee, Integer value, Integer delayTime) throws HomeException {
        MoveToLevelCommand command = new MoveToLevelCommand(OperationCommandTypeEnum.dimmableLightOperation);
        command.setGatewayieee(gatewayieee);
        command.setValue(Integer.toString(Math.round(value * 255 / 100)));
        command.setCommandType(OperationCommandTypeEnum.dimmableLightOperation);
        command.setSensorieee(sensorieee);
        return sendCommand(command, delayTime);
    }

    public Future<MoveToLevelCommand> sendLightCommand(String sensorieee, Integer value) throws HomeException {
        return sendLightCommand(sensorieee, value, Integer.valueOf(0));
    }

    public Future<OperatorCommand> sendOnOutLetCommand(String sensorieee, boolean operation, Integer delayTime) throws HomeException {
        OperatorCommand command = new OperatorCommand(OperationCommandTypeEnum.mainsOutLetOperation);
        command.setGatewayieee(gatewayieee);

        command.setSensorieee(sensorieee);
//	command.setCommandType(OperationCommandTypeEnum.COMMAND_MAINSOUTLETOPERATION);
        // command.setValue(operation ? OperationCommandParamEnums.COMMAND_OPERATION_ON :
        // OperationCommandParamEnums.COMMAND_OPERATION_OFF);
//	command.setOperation(operation ? OperationCommandParamEnums.COMMAND_OPERATION_ON : OperationCommandParamEnums.COMMAND_OPERATION_OFF);
        command.setCommand(CommandEnums.operation);
        command.setValue(operation ? "on" : "off");
        command.setGatewayieee(gatewayieee);
        return sendCommand(command, delayTime);
    }

    public Future<OperatorCommand> sendOnOutLetCommand(String sensorieee, boolean operation) throws HomeException {
        return sendOnOutLetCommand(sensorieee, operation, Integer.valueOf(0));
    }

    public Future<SensorReportCommand> sendSensorReportCommand(Integer delayTime) throws HomeException {
        SensorReportCommand command = new SensorReportCommand();

        command.setGatewayieee(gatewayieee);
        return sendCommand(command, delayTime);
    }

    public Future<SensorReportCommand> sendSensorReportCommand() throws HomeException {
        return sendSensorReportCommand(Integer.valueOf(0));
    }

    public Future<FindSensorCommand> sendFindSensorCommand(String sensorieee, Integer delayTime) throws HomeException {
        FindSensorCommand command = new FindSensorCommand();

        if (sensorieee.length() > 16) {
            sensorieee = sensorieee.substring(0, 16);
        }
        command.setSensorieee(sensorieee);
        command.setGatewayieee(gatewayieee);
        return sendCommand(command, delayTime);
    }

    public Future<FindSensorCommand> sendFindSensorCommand(String deviceId) throws HomeException {
        return sendFindSensorCommand(deviceId);
    }

    public Future<DataReportCommand> sendDataReportCommand(String sensorieee, Integer delayTime) throws HomeException {
        DataReportCommand command = new DataReportCommand();
        command.setSensorieee(sensorieee);
        command.setGatewayieee(gatewayieee);
        return sendCommand(command, delayTime);
    }

    public Future<DataReportCommand> sendDataReportCommand(String sensorieee) throws HomeException {
        return sendDataReportCommand(sensorieee, Integer.valueOf(0));
    }

    public Future<InitGatewayCommand> sendInitGatewayCommand(Integer delayTime) throws HomeException {
        InitGatewayCommand command = new InitGatewayCommand();
        command.setGatewayieee(gatewayieee);
        return sendCommand(command, delayTime);
    }

    public Future<InitGatewayCommand> sendInitGatewayCommand() throws HomeException {
        return sendInitGatewayCommand(Integer.valueOf(0));
    }

    public Future<RemoveSensorCommand> sendRemoveSensorCommand(String sensorieee, Integer sensorType, Integer delayTime) throws HomeException {
        RemoveSensorCommand command = new RemoveSensorCommand();
        command.setSensorType(sensorType);
        List sensoriees = new ArrayList();
        sensoriees.add(sensorieee);
        command.setSensorieee(sensorieee);
        command.setSensorieees(sensoriees);
        command.setGatewayieee(gatewayieee);
        return sendCommand(command, delayTime);
    }

    public Future<DeletePasswordCommand> sendDeleteLockPasswordCommand(String deviceId, String groupId, Integer passId) throws HomeException {
        return sendDeleteLockPasswordCommand(deviceId, groupId, passId, 0);

    }

    public Future<DeletePasswordCommand> sendDeleteLockPasswordCommand(String sensorieee, String groupId, Integer passId, Integer delayTime) throws HomeException {
        DeletePasswordCommand command = new DeletePasswordCommand();
        command.setSensorieee(sensorieee);
        command.setGatewayieee(gatewayieee);
        command.setPassId(passId);
        command.setModeId(groupId);
        return sendCommand(command, delayTime);

    }

    public Future<ClearPasswordCommand> sendClearPasswordCommand(String deviceId) throws HomeException {
        return sendClearPasswordCommand(deviceId, 0);

    }

    public Future<ClearPasswordCommand> sendClearPasswordCommand(String sensorieee, Integer delayTime) throws HomeException {
        ClearPasswordCommand command = new ClearPasswordCommand();

        command.setSensorieee(sensorieee);
        command.setGatewayieee(gatewayieee);
        return sendCommand(command, delayTime);

    }

    public Future<PasswordCommand> sendAddLockPasswordCommand(AppLockPassword lockPassword) throws HomeException {
        return sendAddLockPasswordCommand(lockPassword, 0);
    }

    public Future<PasswordCommand> sendAddLockPasswordCommand(AppLockPassword lockPassword, Integer delayTime) throws HomeException {
        PasswordCommand command = new AddPasswordCommand();
        Integer type = lockPassword.getPasswordType();
        if (type == null) {
            type = 0;
        }

        if (lockPassword.getSeed()!=null && lockPassword.getInterval()!=null){
            command = new AddPYPasswordCommand();
            command.setSeed(lockPassword.getSeed());
            command.setInterval(lockPassword.getInterval());
            type = 1;
        }else{
            if (lockPassword.getStartTime() > 0) {

                String startTimeStr = lockPassword.getStartTime().toString();
                if (startTimeStr.length() > 10) {
                    String shortStartTime = startTimeStr.substring(0, 10);
                    command.setStartTime(Long.valueOf(shortStartTime));
                }else{
                    command.setStartTime(lockPassword.getStartTime());
                }

            } else {
                command.setStartTime(new Long(0));
            }
            if (lockPassword.getEndTime() > 0) {
                String endTimeStr = lockPassword.getEndTime().toString();
                if (endTimeStr.length() > 10) {
                    String shortEndTime = endTimeStr.substring(0, 10);
                    command.setEndTime(Long.valueOf(shortEndTime));
                }else{
                    command.setEndTime(lockPassword.getEndTime());
                }

            } else {
                command.setEndTime(new Long(0));
            }
        }


        String sensorieee = lockPassword.getIeee();
        command.setSensorieee(sensorieee);
        command.setGatewayieee(gatewayieee);
        Integer passid = lockPassword.getPassId();



        if (lockPassword.getModeId()!=null){
            command.setModeId(lockPassword.getModeId());
        }else{
            command.setModeId(command.getCommandId());
        }
        if (lockPassword.getPhone()!=null &&   !lockPassword.getPhone().equals("")) {
            lockPassword.setPhone(lockPassword.getPhone());
        }


        command.setPassId(passid);
        command.setPassVal1(lockPassword.getPassword());
        command.setPassVal2(1);
        command.setPassType(type);

        return sendCommand(command, delayTime);

    }

    public Future<RemoveSensorCommand> sendRemoveSensorCommand(String sensorieee, Integer sensorType) throws HomeException {
        return sendRemoveSensorCommand(sensorieee, sensorType, Integer.valueOf(0));
    }

    public Future<AddSensorCommand> sendAddSensorCommand(String serialno, Integer sensorType, Integer delayTime) throws HomeException {
        AddSensorCommand command = new AddSensorCommand();
        command.setSensorType(sensorType);
        List sensoriees = new ArrayList();

        sensoriees.add(serialno);
        command.setSensorieee(serialno);

        command.setSensorieees(sensoriees);
        command.setGatewayieee(gatewayieee);

        return sendCommand(command, delayTime);
    }

    public Future<AddSensorCommand> sendAddSensorCommand(String serialno, Integer sensorType) throws HomeException {
        return sendAddSensorCommand(serialno, sensorType, Integer.valueOf(0));
    }

    public void sendFirmwareDownload() throws HomeException {
    }

    public Future<Command> sendCheckUpgradeCommand(String deviceId) throws HomeException {
        return sendCheckUpgradeCommand(deviceId, Integer.valueOf(0));
    }

    @Override
    public GWClient getGWClient() {
        return null;
    }

    public Future<Command> sendCheckUpgradeCommand(String deviceId, Integer delayTime) throws HomeException {
        return null;
    }


    public Future<IdentifyCommand> sendIdentifyDeviceCommand(String sensorieee, Integer times, Integer delayTime) throws HomeException {
        IdentifyCommand command = new IdentifyCommand();
        command.setCommandId(UUID.randomUUID().toString());

        command.setSensorieee(sensorieee);
        command.setValue(times == null ? "0" : times.toString());
        command.setGatewayieee(gatewayieee);
        return sendCommand(command);
    }

    public Future<IdentifyCommand> sendIdentifyDeviceCommand(String sensorieee, Integer times) throws HomeException {
        return sendIdentifyDeviceCommand(sensorieee, times, Integer.valueOf(0));
    }

    public <T extends Command>Future<T> sendCommand(T command,  Integer delayTime) throws HomeException {
        Map eventContext = new HashMap();
        eventContext = fillInUserID(eventContext);
        Device device = null;
        try {
            device = CtIotCacheManager.getInstance().getDeviceByIeee(gatewayieee);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        command.setSystemCode(device.getSubsyscode());
        command.setGatewayieee(gatewayieee);
        eventContext.put(GatewayEvent.CONTEXT_COMMAND, command);
       // fireCommandEvent(command, CommandEventEnums.COMMANDINIT, eventContext);
        logger.info("sendCommand:" + JSONObject.toJSONString(command));
        return commandEngine.sendCommand(command, delayTime);
    }

    @Override
    public <T extends Command> T getCommandById(String commandId) {
        try {
            return   CtIotFactory.getCtIotService().getCommandById(commandId);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T extends Command> Future<T> sendCommand(T command) throws HomeException {
        return sendCommand(command, Integer.valueOf(0));
    }

    public Future<BindDeviceCommand> sendBindDeviceCommand(String sourcedev, String destdev, Integer delayTime) throws HomeException {
        BindDeviceCommand command = new BindDeviceCommand();
        command.setSourcedev(sourcedev);
        command.setDestdev(destdev);
        command.setClusterid(BindClusterIdParamEnums.COMMAND_BINDDEVICE_CLUSTER_ONOFF.getType());
        command.setGatewayieee(this.gatewayieee);
        return sendCommand(command, delayTime);
    }

    public Future<BindDeviceCommand> sendBindDeviceCommand(String sourcedev, String destdev) throws HomeException {
        return sendBindDeviceCommand(sourcedev, destdev, Integer.valueOf(0));
    }

//    private void fireCommandEvent(Command command, CommandEventEnums eventID, Map eventContext) throws HomeException {
//        try {
//            eventContext = fillInUserID(eventContext);
//            CommandEvent event = new CommandEvent(command, this.client, eventID, null);
//            event.setContextMap(eventContext);
//            HomeEventControl.getInstance().dispatchEvent(event);
//        } catch (Exception e) {
//            // e.printStackTrace();
//        }
//    }


    public JDSSessionHandle getSessionHandle() {
        return this.client.getSessionHandle();
    }

    private Map fillInUserID(Map ctx) {
        Map result = ctx;
        if (result == null) {
            result = new HashMap();
        }

        result.put(AdminClient.CTX_USER_ID, getConnectInfo().getUserID());
        return result;
    }

    public void checkLogin() throws HomeException {
        if (getConnectInfo() == null)
            throw new HomeException("用户未登录", 1005);
    }

    public void setClientService(JDSClientService client) {
        this.client = client;
    }


    public ConnectionHandle getConnectionHandle() {
        return this.client.getConnectionHandle();
    }

    public JDSContext getContext() {
        return this.client.getContext();
    }

    public void setConnectionHandle(ConnectionHandle handle) {
        this.client.setConnectionHandle(handle);
    }

    public void setContext(JDSContext context) {
        this.client.setContext(context);
    }

    public ConnectInfo getConnectInfo() {
        return connectInfo;
    }

    public void setConnectInfo(ConnectInfo connectInfo) {
        this.connectInfo = connectInfo;
    }

    public Future<SyncTimeCommand> sendSyncTimeCommand(Integer delayTime) throws HomeException {
        SyncTimeCommand command = new SyncTimeCommand();
        String time = Long.valueOf(System.currentTimeMillis()).toString();
        command.setValue(time.substring(0, time.length() - 3));
        command.setGatewayieee(gatewayieee);

        return sendCommand(command, delayTime);
    }

    public Future<SyncTimeCommand> sendSyncTimeCommand() throws HomeException {
        return sendSyncTimeCommand(0);
    }

    /**
     * 创建指定数量的随机字符串
     *
     * @param numberFlag
     *            是否是数字
     * @param length
     * @return
     */
    public String createRandom(boolean numberFlag, int length) {
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);

        return retStr;
    }
}
