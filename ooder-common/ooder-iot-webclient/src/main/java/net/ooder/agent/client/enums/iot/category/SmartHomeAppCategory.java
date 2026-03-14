package net.ooder.enums.iot.category;


import net.ooder.annotation.IconEnumstype;
import net.ooder.enums.iot.SmartHomeEnum;

/**
 * 全屋智能APP设备功能聚合分类枚举类
 * 按照全屋智能APP的设计需求，将智能家居设备按功能区域和使用场景进行聚合分类
 */
public enum SmartHomeAppCategory implements IconEnumstype {
    // 客厅设备
    LIVING_ROOM_SMART_LIGHT(LightingDeviceEnum.SMART_LIGHT, "客厅智能灯"),
    LIVING_ROOM_SMART_TV(ApplianceDeviceEnum.SMART_TV, "客厅智能电视"),
    LIVING_ROOM_SMART_SPEAKER(ApplianceDeviceEnum.SMART_SPEAKER, "客厅智能音箱"),
    LIVING_ROOM_SMART_CURTAIN(SmartHomeEnum.SMART_CURTAIN, "客厅智能窗帘"),
    
    // 卧室设备
    BEDROOM_SMART_LIGHT(LightingDeviceEnum.SMART_LIGHT, "卧室智能灯"),
    BEDROOM_NIGHT_LIGHT(LightingDeviceEnum.NIGHT_LIGHT, "卧室夜灯"),
    BEDROOM_SMART_CURTAIN(SmartHomeEnum.SMART_CURTAIN, "卧室智能窗帘"),
    BEDROOM_SMART_AIR_CONDITIONER(ApplianceDeviceEnum.SMART_AIR_CONDITIONER, "卧室智能空调"),
    
    // 厨房设备
    KITCHEN_SMART_LIGHT(LightingDeviceEnum.SMART_LIGHT, "厨房智能灯"),
    KITCHEN_SMART_REFRIGERATOR(ApplianceDeviceEnum.SMART_REFRIGERATOR, "智能冰箱"),
    KITCHEN_SMART_OVEN(ApplianceDeviceEnum.SMART_OVEN, "智能烤箱"),
    KITCHEN_SMART_MICROWAVE(ApplianceDeviceEnum.SMART_MICROWAVE, "智能微波炉"),
    KITCHEN_SMART_STOVE(ApplianceDeviceEnum.SMART_STOVE, "智能炉灶"),
    KITCHEN_SMART_DISHWASHER(ApplianceDeviceEnum.SMART_DISHWASHER, "智能洗碗机"),
    KITCHEN_SMART_COFFEE_MAKER(ApplianceDeviceEnum.SMART_COFFEE_MAKER, "智能咖啡机"),
    KITCHEN_GAS_DETECTOR(SecurityDeviceEnum.GAS_DETECTOR, "燃气探测器"),
    
    // 浴室设备
    BATHROOM_SMART_LIGHT(LightingDeviceEnum.SMART_LIGHT, "浴室智能灯"),
    BATHROOM_SMART_WATER_HEATER(ApplianceDeviceEnum.SMART_WATER_HEATER, "智能热水器"),
    
    // 安全设备
    SECURITY_SMART_LOCK(SecurityDeviceEnum.SMART_LOCK, "智能门锁"),
    SECURITY_DOOR_SENSOR(SecurityDeviceEnum.DOOR_SENSOR, "门磁传感器"),
    SECURITY_WINDOW_SENSOR(SecurityDeviceEnum.WINDOW_SENSOR, "窗磁传感器"),
    SECURITY_MOTION_SENSOR(SecurityDeviceEnum.MOTION_SENSOR, "人体感应传感器"),
    SECURITY_SECURITY_CAMERA(SecurityDeviceEnum.SECURITY_CAMERA, "安防摄像头"),
    SECURITY_ALARM_SYSTEM(SecurityDeviceEnum.ALARM_SYSTEM, "报警系统"),
    SECURITY_SMOKE_DETECTOR(SecurityDeviceEnum.SMOKE_DETECTOR, "烟雾探测器"),
    SECURITY_GLASS_BREAK_SENSOR(SecurityDeviceEnum.GLASS_BREAK_SENSOR, "玻璃破碎传感器"),
    
    // 能源管理
    ENERGY_SMART_METER(EnergyDeviceEnum.SMART_METER, "智能电表"),
    ENERGY_ENERGY_MONITOR(EnergyDeviceEnum.ENERGY_MONITOR, "能源监控器"),
    ENERGY_SMART_PLUG(EnergyDeviceEnum.SMART_PLUG, "智能插座"),
    ENERGY_SMART_SWITCH(EnergyDeviceEnum.SMART_SWITCH, "智能开关"),
    ENERGY_SMART_THERMOSTAT(EnergyDeviceEnum.SMART_THERMOSTAT, "智能恒温器"),
    ENERGY_ENERGY_MANAGEMENT_SYSTEM(EnergyDeviceEnum.ENERGY_MANAGEMENT_SYSTEM, "能源管理系统"),
    
    // 娱乐设备
    ENTERTAINMENT_SMART_TV(ApplianceDeviceEnum.SMART_TV, "智能电视"),
    ENTERTAINMENT_SMART_SPEAKER(ApplianceDeviceEnum.SMART_SPEAKER, "智能音箱"),
    ENTERTAINMENT_SMART_PROJECTOR(ApplianceDeviceEnum.SMART_PROJECTOR, "智能投影仪"),
    ENTERTAINMENT_GAME_CONSOLE(ApplianceDeviceEnum.GAME_CONSOLE, "游戏主机"),
    
    // 环境设备
    ENVIRONMENT_TEMPERATURE_SENSOR(EnvironmentDeviceEnum.TEMPERATURE_SENSOR, "温度传感器"),
    ENVIRONMENT_HUMIDITY_SENSOR(EnvironmentDeviceEnum.HUMIDITY_SENSOR, "湿度传感器"),
    ENVIRONMENT_AIR_QUALITY_SENSOR(EnvironmentDeviceEnum.AIR_QUALITY_SENSOR, "空气质量传感器"),
    ENVIRONMENT_CO2_SENSOR(EnvironmentDeviceEnum.CO2_SENSOR, "二氧化碳传感器"),
    ENVIRONMENT_PM25_SENSOR(EnvironmentDeviceEnum.PM25_SENSOR, "PM2.5传感器"),
    ENVIRONMENT_SMART_AIR_PURIFIER(ApplianceDeviceEnum.SMART_AIR_PURIFIER, "智能空气净化器"),
    ENVIRONMENT_SMART_HUMIDIFIER(ApplianceDeviceEnum.SMART_HUMIDIFIER, "智能加湿器"),
    ENVIRONMENT_SMART_DEHUMIDIFIER(ApplianceDeviceEnum.SMART_DEHUMIDIFIER, "智能除湿器"),
    
    // 清洁设备
    CLEANING_SMART_VACUUM(ApplianceDeviceEnum.SMART_VACUUM, "智能扫地机器人"),
    CLEANING_SMART_WASHER(ApplianceDeviceEnum.SMART_WASHER, "智能洗衣机"),
    CLEANING_SMART_DRYER(ApplianceDeviceEnum.SMART_DRYER, "智能烘干机"),
    
    // 全屋控制
    WHOLE_HOME_SCENE_CONTROLLER(LightingDeviceEnum.SCENE_CONTROLLER, "场景控制器"),
    WHOLE_HOME_HOME_ASSISTANT(SmartHomeEnum.HOME_ASSISTANT, "家庭助手"),
    WHOLE_HOME_HOME_AUTOMATION(SmartHomeEnum.HOME_AUTOMATION, "家庭自动化系统");
    
    private final IconEnumstype iconEnum;
    private final String name;
    
    SmartHomeAppCategory(IconEnumstype iconEnum, String name) {
        this.iconEnum = iconEnum;
        this.name = name;
    }
    
    @Override
    public String getType() {
        return iconEnum.getType();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getImageClass() {
        return iconEnum.getImageClass();
    }
    
    /**
     * 获取设备所属区域
     * @return 区域名称
     */
    public String getArea() {
        if (name().startsWith("LIVING_ROOM_")) {
            return "客厅";
        } else if (name().startsWith("BEDROOM_")) {
            return "卧室";
        } else if (name().startsWith("KITCHEN_")) {
            return "厨房";
        } else if (name().startsWith("BATHROOM_")) {
            return "浴室";
        } else if (name().startsWith("SECURITY_")) {
            return "安全";
        } else if (name().startsWith("ENERGY_")) {
            return "能源";
        } else if (name().startsWith("ENTERTAINMENT_")) {
            return "娱乐";
        } else if (name().startsWith("ENVIRONMENT_")) {
            return "环境";
        } else if (name().startsWith("CLEANING_")) {
            return "清洁";
        } else if (name().startsWith("WHOLE_HOME_")) {
            return "全屋控制";
        }
        return "未知区域";
    }
    
    /**
     * 获取设备所属功能分类
     * @return 功能分类名称
     */
    public String getFunctionCategory() {
        if (this == LIVING_ROOM_SMART_LIGHT || this == BEDROOM_SMART_LIGHT || 
            this == KITCHEN_SMART_LIGHT || this == BATHROOM_SMART_LIGHT || 
            this == BEDROOM_NIGHT_LIGHT) {
            return "照明";
        } else if (this == LIVING_ROOM_SMART_TV || this == ENTERTAINMENT_SMART_TV || 
                   this == LIVING_ROOM_SMART_SPEAKER || this == ENTERTAINMENT_SMART_SPEAKER || 
                   this == ENTERTAINMENT_SMART_PROJECTOR || this == ENTERTAINMENT_GAME_CONSOLE) {
            return "娱乐";
        } else if (this == SECURITY_SMART_LOCK || this == SECURITY_DOOR_SENSOR || 
                   this == SECURITY_WINDOW_SENSOR || this == SECURITY_MOTION_SENSOR || 
                   this == SECURITY_SECURITY_CAMERA || this == SECURITY_ALARM_SYSTEM || 
                   this == SECURITY_SMOKE_DETECTOR || this == SECURITY_GLASS_BREAK_SENSOR || 
                   this == KITCHEN_GAS_DETECTOR) {
            return "安防";
        } else if (this == ENERGY_SMART_METER || this == ENERGY_ENERGY_MONITOR || 
                   this == ENERGY_SMART_PLUG || this == ENERGY_SMART_SWITCH || 
                   this == ENERGY_SMART_THERMOSTAT || this == ENERGY_ENERGY_MANAGEMENT_SYSTEM) {
            return "能源管理";
        } else if (this == ENVIRONMENT_TEMPERATURE_SENSOR || this == ENVIRONMENT_HUMIDITY_SENSOR || 
                   this == ENVIRONMENT_AIR_QUALITY_SENSOR || this == ENVIRONMENT_CO2_SENSOR || 
                   this == ENVIRONMENT_PM25_SENSOR || this == ENVIRONMENT_SMART_AIR_PURIFIER || 
                   this == ENVIRONMENT_SMART_HUMIDIFIER || this == ENVIRONMENT_SMART_DEHUMIDIFIER) {
            return "环境监测与控制";
        } else if (this == CLEANING_SMART_VACUUM || 
                   this == CLEANING_SMART_WASHER || this == CLEANING_SMART_DRYER) {
            return "清洁";
        } else if (this == WHOLE_HOME_SCENE_CONTROLLER || this == WHOLE_HOME_HOME_ASSISTANT || 
                   this == WHOLE_HOME_HOME_AUTOMATION) {
            return "全屋控制";
        }
        return "家电";
    }
    
    /**
     * 获取设备所属房间类型
     * @return 房间类型
     */
    public String getRoomType() {
        if (name().startsWith("LIVING_ROOM_")) {
            return "客厅";
        } else if (name().startsWith("BEDROOM_")) {
            return "卧室";
        } else if (name().startsWith("KITCHEN_")) {
            return "厨房";
        } else if (name().startsWith("BATHROOM_")) {
            return "浴室";
        } else if (name().startsWith("SECURITY_")) {
            return "全屋";
        } else if (name().startsWith("ENERGY_")) {
            return "全屋";
        } else if (name().startsWith("ENTERTAINMENT_")) {
            return "客厅/娱乐室";
        } else if (name().startsWith("ENVIRONMENT_")) {
            return "全屋";
        } else if (name().startsWith("CLEANING_")) {
            return "全屋";
        } else if (name().startsWith("WHOLE_HOME_")) {
            return "全屋";
        }
        return "未知房间";
    }
}