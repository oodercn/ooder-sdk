package net.ooder.enums.iot;


import net.ooder.annotation.IconEnumstype;
import net.ooder.enums.iot.category.SmartHomeAppCategory;
import net.ooder.enums.iot.category.SmartHomeAppFunction;

/**
 * 全屋智能APP模块主枚举类
 * 汇总所有全屋智能APP相关的功能和设备分类
 */
public enum SmartHomeAppModule implements IconEnumstype {
    // 功能模块
    FUNCTION_DASHBOARD(SmartHomeAppFunction.HOME_DASHBOARD),
    FUNCTION_DEVICE_CONTROL(SmartHomeAppFunction.DEVICE_CONTROL),
    FUNCTION_SCENE_CONTROL(SmartHomeAppFunction.SCENE_CONTROL),
    FUNCTION_ENERGY_MANAGEMENT(SmartHomeAppFunction.ENERGY_MANAGEMENT),
    FUNCTION_SECURITY_MONITORING(SmartHomeAppFunction.SECURITY_MONITORING),
    FUNCTION_ENVIRONMENT_MONITORING(SmartHomeAppFunction.ENVIRONMENT_MONITORING),
    FUNCTION_ROOM_MANAGEMENT(SmartHomeAppFunction.ROOM_MANAGEMENT),
    FUNCTION_USER_PROFILE(SmartHomeAppFunction.USER_PROFILE),
    FUNCTION_SMART_ASSISTANT(SmartHomeAppFunction.SMART_ASSISTANT),
    FUNCTION_AUTOMATION_RULES(SmartHomeAppFunction.AUTOMATION_RULES),
    FUNCTION_DEVICE_GROUPS(SmartHomeAppFunction.DEVICE_GROUPS),
    
    // 客厅设备
    DEVICE_LIVING_ROOM_SMART_LIGHT(SmartHomeAppCategory.LIVING_ROOM_SMART_LIGHT),
    DEVICE_LIVING_ROOM_SMART_TV(SmartHomeAppCategory.LIVING_ROOM_SMART_TV),
    DEVICE_LIVING_ROOM_SMART_SPEAKER(SmartHomeAppCategory.LIVING_ROOM_SMART_SPEAKER),
    DEVICE_LIVING_ROOM_SMART_CURTAIN(SmartHomeAppCategory.LIVING_ROOM_SMART_CURTAIN),
    
    // 卧室设备
    DEVICE_BEDROOM_SMART_LIGHT(SmartHomeAppCategory.BEDROOM_SMART_LIGHT),
    DEVICE_BEDROOM_SMART_CURTAIN(SmartHomeAppCategory.BEDROOM_SMART_CURTAIN),
    DEVICE_BEDROOM_SMART_AIR_CONDITIONER(SmartHomeAppCategory.BEDROOM_SMART_AIR_CONDITIONER),
    
    // 厨房设备
    DEVICE_KITCHEN_SMART_LIGHT(SmartHomeAppCategory.KITCHEN_SMART_LIGHT),
    DEVICE_KITCHEN_SMART_REFRIGERATOR(SmartHomeAppCategory.KITCHEN_SMART_REFRIGERATOR),
    DEVICE_KITCHEN_GAS_DETECTOR(SmartHomeAppCategory.KITCHEN_GAS_DETECTOR),
    
    // 浴室设备
    DEVICE_BATHROOM_SMART_LIGHT(SmartHomeAppCategory.BATHROOM_SMART_LIGHT),
    DEVICE_BATHROOM_SMART_WATER_HEATER(SmartHomeAppCategory.BATHROOM_SMART_WATER_HEATER),
    
    // 安全设备
    DEVICE_SECURITY_SMART_LOCK(SmartHomeAppCategory.SECURITY_SMART_LOCK),
    DEVICE_SECURITY_SECURITY_CAMERA(SmartHomeAppCategory.SECURITY_SECURITY_CAMERA),
    DEVICE_SECURITY_MOTION_SENSOR(SmartHomeAppCategory.SECURITY_MOTION_SENSOR),
    DEVICE_SECURITY_SMOKE_DETECTOR(SmartHomeAppCategory.SECURITY_SMOKE_DETECTOR),
    
    // 能源管理
    DEVICE_ENERGY_SMART_PLUG(SmartHomeAppCategory.ENERGY_SMART_PLUG),
    DEVICE_ENERGY_SMART_THERMOSTAT(SmartHomeAppCategory.ENERGY_SMART_THERMOSTAT),
    DEVICE_ENERGY_ENERGY_MONITOR(SmartHomeAppCategory.ENERGY_ENERGY_MONITOR),
    
    // 环境设备
    DEVICE_ENVIRONMENT_TEMPERATURE_SENSOR(SmartHomeAppCategory.ENVIRONMENT_TEMPERATURE_SENSOR),
    DEVICE_ENVIRONMENT_HUMIDITY_SENSOR(SmartHomeAppCategory.ENVIRONMENT_HUMIDITY_SENSOR),
    DEVICE_ENVIRONMENT_SMART_AIR_PURIFIER(SmartHomeAppCategory.ENVIRONMENT_SMART_AIR_PURIFIER);
    
    private final IconEnumstype iconEnum;
    
    SmartHomeAppModule(IconEnumstype iconEnum) {
        this.iconEnum = iconEnum;
    }
    
    @Override
    public String getType() {
        return iconEnum.getType();
    }
    
    @Override
    public String getName() {
        return iconEnum.getName();
    }
    
    @Override
    public String getImageClass() {
        return iconEnum.getImageClass();
    }
    
    /**
     * 获取模块类型
     * @return 模块类型名称
     */
    public String getModuleType() {
        if (name().startsWith("FUNCTION_")) {
            return "功能模块";
        } else if (name().startsWith("DEVICE_")) {
            return "设备";
        }
        return "未知类型";
    }
    
    /**
     * 获取设备所属区域（如果是设备）
     * @return 区域名称
     */
    public String getArea() {
        if (iconEnum instanceof SmartHomeAppCategory) {
            return ((SmartHomeAppCategory) iconEnum).getArea();
        }
        return "功能模块";
    }
    
    /**
     * 获取功能所属模块（如果是功能）
     * @return 功能模块名称
     */
    public String getFunctionModule() {
        if (iconEnum instanceof SmartHomeAppFunction) {
            return ((SmartHomeAppFunction) iconEnum).getModule();
        }
        return "设备";
    }
    
    /**
     * 判断是否为功能模块
     * @return 是否为功能模块
     */
    public boolean isFunctionModule() {
        return iconEnum instanceof SmartHomeAppFunction;
    }
    
    /**
     * 判断是否为设备
     * @return 是否为设备
     */
    public boolean isDevice() {
        return iconEnum instanceof SmartHomeAppCategory;
    }
}