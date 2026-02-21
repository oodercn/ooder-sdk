package net.ooder.agent.client.iot.enums;

import  net.ooder.annotation.Enumstype;

public enum DeviceDataTypeKey implements Enumstype {

    NetworkInfo("NetworkInfo", "网络配置", ParamType.App),

    imgUrl("imgUrl", "图片", ParamType.App),

    Time("Time", "时间", ParamType.System),


    PYPassword("PYPassword", "动态密码", ParamType.App),

    Password("Password", "密码", ParamType.App),

    Emergency("Emergency", "紧急报警", ParamType.HA),

    Fire("Fire", "火防", ParamType.System),

    Voltate("Voltate", "电压", ParamType.HA),

    Current("Current", "电流", ParamType.HA),

    Energy("Energy", "功率", ParamType.HA),

    Power("Power", "电能", ParamType.HA),

    Temperature("Temperature", "温度", ParamType.HA),

    Humidity("Humidity", "湿度", ParamType.HA),


    battery("battery", "设备电量", ParamType.HA),

    Status("Status", "在线状态", ParamType.System),

    passId("passId", "密码ID", ParamType.App),

    modeid("modeid", "密码modeID", ParamType.Factory),

    lqi("lqi", "信号强度", ParamType.System),

    rssi("rssi", "接收信号强度", ParamType.System),

    Other_Lock("Other_Lock", "反锁", ParamType.App),

    LockTime("LockTime", "门锁本地时间", ParamType.App),

    Zone_Status("Zone_Status", "报警状态", ParamType.HA),

    LastUpdateTime("LastUpdateTime", "最后更新时间", ParamType.System),

    //AddAlarm
    Burglar("Burglar", "防盗报警", ParamType.System),

    lowbattery("lowbattery", "低电压报警", ParamType.System),

    Trouble("Trouble", "故障", ParamType.System),

    pm25("pm25", "PM2.5", ParamType.App),

    Lux("Lux", "照度", ParamType.HA),


    SetTemperature("SetTemperature", "空调模式", ParamType.HA),
    FanMode("FanMode", "空调风扇模式", ParamType.HA),


    Level("Level", "调光", ParamType.App),

    StateOnOff("StateOnOff", "开关状态", ParamType.HA),

    LockSeed("LockSeed", "种子", ParamType.System),

    UNKNOW("UNKNOW", "未知变量", ParamType.Other);


    private String type;

    private String name;

    private ParamType paramType;

    public String getType() {
        return type;
    }

    public ParamType getParamType() {
        return paramType;
    }

    public String getName() {
        return name;
    }

    DeviceDataTypeKey(String type, String name, ParamType paramType) {
        this.type = type;
        this.name = name;
        this.paramType = paramType;

    }

    @Override
    public String toString() {
        return type;
    }

    public static DeviceDataTypeKey fromType(String typeName) {
        for (DeviceDataTypeKey type : DeviceDataTypeKey.values()) {
            if (type.getType().toUpperCase().equals(typeName.toUpperCase())) {
                return type;
            }
        }
        return UNKNOW;
    }

}
