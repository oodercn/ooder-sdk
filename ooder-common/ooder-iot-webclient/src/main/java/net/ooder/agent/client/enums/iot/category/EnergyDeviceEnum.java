package net.ooder.enums.iot.category;


import net.ooder.annotation.IconEnumstype;

/**
 * 能源设备枚举类
 * 使用Remix Icon开源字体库
 */
public enum EnergyDeviceEnum implements IconEnumstype {
    // 智能电表与用电监控
    SMART_METER("smartMeter", "ri-counter-line", "智能电表"),
    ENERGY_MONITOR("energyMonitor", "ri-bolt-line", "能源监控器"),
    POWER_ANALYZER("powerAnalyzer", "ri-gauge-line", "电力分析仪"),
    
    // 智能插座与开关
    SMART_PLUG("smartPlug", "ri-plug-line", "智能插座"),
    SMART_SWITCH("smartSwitch", "ri-toggle-line", "智能开关"),
    
    // 温控设备
    SMART_THERMOSTAT("smartThermostat", "ri-thermometer-line", "智能恒温器"),
    HEATING_CONTROLLER("heatingController", "ri-fire-line", "暖气控制器"),
    COOLING_CONTROLLER("coolingController", "ri-snowflake-line", "制冷控制器"),
    
    // 可再生能源设备
    SOLAR_PANEL("solarPanel", "ri-sun-line", "太阳能板"),
    WIND_TURBINE("windTurbine", "ri-wind-line", "风力发电机"),
    BATTERY_STORAGE("batteryStorage", "ri-battery-line", "电池储能设备"),
    
    // 能源管理系统
    ENERGY_MANAGEMENT_SYSTEM("energyManagementSystem", "ri-bar-chart-line", "能源管理系统"),
    DEMAND_RESPONSE("demandResponse", "ri-exchange-line", "需求响应系统"),
    MICROGRID_CONTROLLER("microgridController", "ri-network-line", "微电网控制器");
    
    private final String type;
    private final String name;
    private final String imageClass;
    
    EnergyDeviceEnum(String type, String imageClass, String name) {
        this.type = type;
        this.name = name;
        this.imageClass = imageClass;
    }
    
    @Override
    public String getType() {
        return type;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getImageClass() {
        return imageClass;
    }
    
    /**
     * 获取设备所属子分类
     * @return 子分类名称
     */
    public String getSubCategory() {
        switch (this) {
            case SMART_METER:
            case ENERGY_MONITOR:
            case POWER_ANALYZER:
                return "智能电表与用电监控";
            case SMART_PLUG:
            case SMART_SWITCH:
                return "智能插座与开关";
            case SMART_THERMOSTAT:
            case HEATING_CONTROLLER:
            case COOLING_CONTROLLER:
                return "温控设备";
            case SOLAR_PANEL:
            case WIND_TURBINE:
            case BATTERY_STORAGE:
                return "可再生能源设备";
            case ENERGY_MANAGEMENT_SYSTEM:
            case DEMAND_RESPONSE:
            case MICROGRID_CONTROLLER:
                return "能源管理系统";
            default:
                return "未知子分类";
        }
    }
}