package net.ooder.agent.client.enums;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.agent.client.command.*;
import  net.ooder.annotation.Enumstype;

public enum CommandEnums implements Enumstype {

    InitGateway("初始化网关", "InitGateway", InitGatewayCommand.class),

    SensorReport("传感器上报", "SensorReport", SensorReportCommand.class),

    SyncTime("时间同步", "SyncTime", SyncTimeCommand.class),

    Start("开启", "Start", StartCommand.class),

    Stop("结束", "Stop", StopCommand.class),

    ZigbeeScan("开启加网", "ZigbeeScan", ZigbeeScanCommand.class),


    InitFactory("出厂设置", "InitFactory", InitFactoryCommand.class),

    FirmwareDownload("网关固件升级", "FirmwareDownload", FirmwareDownloadCommand.class),

    GoRun("重新上线", "GoRun", GoRunCommand.class),

    Debug("切换到调试环境", "Debug", DebugCommand.class),

    ChannelNegotiateReport("网络信息上报", "ChannelNegotiateReport", ChannelNegotiateReportCommand.class),

    ChannelNegotiate("修改网络信息", "ChannelNegotiate", ChannelNegotiateCommand.class),

    ReleaseAlarm("关闭报警", "ReleaseAlarm", ReleaseAlarmCommand.class),

    OpenAlarm("开启报警", "OpenAlarm", OpenAlarmCommand.class),


    AddSensor("添加白名单", "AddSensor", AddSensorCommand.class),

    RemoveSensor("移除传感器", "RemoveSensor", RemoveSensorCommand.class),

    FindSensor("查找传感器", "FindSensor", FindSensorCommand.class),

    DataReport("数据上报", "DataReport", DataReportCommand.class),


    bindDevice("设备绑定", "bindDevice", BindDeviceCommand.class),

    unbindDevice("设备解绑", "unbindDevice", UnBindDeviceCommand.class),

    IRLearn("红外指令学习", "IRLearn", IRLearnCommand.class),

    IRControl("红外命令", "IRControl", IRControlCommand.class),


    DelMode("删除模式", "DelMode", DeleteModeCommand.class),

    OtaUpgrade("设备OTA升级", "OtaUpgrade", OTAFirmwareCommand.class),

    operation("开关切换", "operation", OperatorCommand.class),

    movetolevel("调级（调光）", "movetolevel", MoveToLevelCommand.class),

    AddNode2Route("将设备添加到指定节点", "AddNode2Route", AddNode2RouteCommand.class),

    IdentifyDevice("识别指示（闪灯）", "IdentifyDevice", IdentifyCommand.class),


    LevelNetWork("强制离开网络", "LevelNetWork", LevelNetWorkCommand.class),

    GetBindList("上报绑定列表", "GetBindList", GetBindListCommand.class),

    ClearData("清空数据", "ClearData", ClearDataCommand.class),


    //ADCommand
    AttributeWrite("HA属性修改", "AttributeWrite", AttributeWriteCommand.class),

    CreateGroup("创建分组", "CreateGroup", CreateGroupCommand.class),

    RemoveGroup("移除分组", "RemoveGroup", RemoveGroupCommand.class),

    CreateScene("创建场景", "CreateScene", CreateSceneCommand.class),


    ClusterCMD("指令透传", "ClusterCMD", ClusterCMDCommand.class),
    GroupClusterCMD("指令Group透传", "GroupClusterCMD", GroupClusterCMDCommand.class),

    LinkReport("链路上报", "LinkReport", LinkReportCommand.class),

    LinkSet("链路上报", "LinkSet", LinkSetCommand.class),


    ClearScene("清空场景", "ClearScene", ClearSceneCommand.class),

    ReplaceSensor("替换传感器", "ReplaceSensor", ReplaceSensorCommand.class),


    AddPassword("添加密码", "AddPassword", AddPasswordCommand.class),

    AddPYPassword("添加PY密码", "AddPYPassword", AddPYPasswordCommand.class),

    DelPassword("删除密码", "DelPassword", DeletePasswordCommand.class),



    SetTemperature("设置温度", "SetTemperature", SetTemperatureCommand.class),
    SetSystemMode("设置模式", "SetSystemMode", SetSystemModeCommand.class),

    SetFanMode("设置风速", "SetFanMode", SetFanModeCommand.class),



    ClearPassword("清空密码", "ClearPassword", ClearPasswordCommand.class);


    private String name;
    private Class<? extends Command> command;
    private String type;

    CommandEnums(String name, String type, Class<? extends Command> command) {

        this.name = name;
        this.type = type;
        this.command = command;
    }

    public static CommandEnums fromByName(String typename) {
        for (CommandEnums type : CommandEnums.values()) {
            if (type.getType().toUpperCase().equals(typename.toUpperCase())) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String getType() {
        return type;
    }

    @JSONField
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return type;
    }

    public Class<? extends Command> getCommand() {
        return command;
    }

    public void setCommand(Class<? extends Command> command) {
        this.command = command;
    }

    public void setName(String name) {
        this.name = name;
    }

}
