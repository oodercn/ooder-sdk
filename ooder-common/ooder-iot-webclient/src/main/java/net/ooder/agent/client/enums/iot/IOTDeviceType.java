package net.ooder.enums.iot;


import net.ooder.annotation.IconEnumstype;

/**
 * IOT设备类型主枚举类
 * 汇总所有IOT设备类型，便于统一管理和使用
 */
public enum IOTDeviceType implements IconEnumstype {
    // 智慧农业分类
    AGRICULTURE_SOIL_MOISTURE(SmartAgricultureEnum.SENSOR_SOIL_MOISTURE),
    AGRICULTURE_TEMPERATURE(SmartAgricultureEnum.SENSOR_TEMPERATURE),
    AGRICULTURE_HUMIDITY(SmartAgricultureEnum.SENSOR_HUMIDITY),
    AGRICULTURE_LIGHT(SmartAgricultureEnum.SENSOR_LIGHT),
    AGRICULTURE_PH(SmartAgricultureEnum.SENSOR_PH),
    AGRICULTURE_IRRIGATION(SmartAgricultureEnum.IRRIGATION_SYSTEM),
    AGRICULTURE_GREENHOUSE(SmartAgricultureEnum.GREENHOUSE_CONTROLLER),
    AGRICULTURE_WEATHER(SmartAgricultureEnum.WEATHER_STATION),
    AGRICULTURE_CROP_MONITOR(SmartAgricultureEnum.CROP_MONITOR),
    AGRICULTURE_FERTILIZER(SmartAgricultureEnum.FERTILIZER_SYSTEM),
    AGRICULTURE_PEST_CONTROL(SmartAgricultureEnum.PEST_CONTROL),
    AGRICULTURE_GROWTH_TRACKING(SmartAgricultureEnum.GROWTH_TRACKING),
    AGRICULTURE_YIELD_PREDICTION(SmartAgricultureEnum.YIELD_PREDICTION),
    AGRICULTURE_EQUIPMENT_MAINTENANCE(SmartAgricultureEnum.EQUIPMENT_MAINTENANCE),
    AGRICULTURE_FARM_MANAGEMENT(SmartAgricultureEnum.FARM_MANAGEMENT),
    
    // 智慧城市分类
    CITY_TRAFFIC_LIGHT(SmartCityEnum.TRAFFIC_LIGHT),
    CITY_SURVEILLANCE_CAMERA(SmartCityEnum.SURVEILLANCE_CAMERA),
    CITY_SMART_PARKING(SmartCityEnum.SMART_PARKING),
    CITY_ENVIRONMENTAL_SENSOR(SmartCityEnum.ENVIRONMENTAL_SENSOR),
    CITY_SMART_BIN(SmartCityEnum.SMART_BIN),
    CITY_SMART_STREETLIGHT(SmartCityEnum.SMART_STREETLIGHT),
    CITY_WEATHER_STATION(SmartCityEnum.WEATHER_STATION),
    CITY_TRAFFIC_MANAGEMENT(SmartCityEnum.TRAFFIC_MANAGEMENT),
    CITY_PUBLIC_SECURITY(SmartCityEnum.PUBLIC_SECURITY),
    CITY_ENERGY_MANAGEMENT(SmartCityEnum.ENERGY_MANAGEMENT),
    CITY_WATER_MANAGEMENT(SmartCityEnum.WATER_MANAGEMENT),
    CITY_WASTE_MANAGEMENT(SmartCityEnum.WASTE_MANAGEMENT),
    CITY_EMERGENCY_RESPONSE(SmartCityEnum.EMERGENCY_RESPONSE),
    CITY_CITY_INFRASTRUCTURE(SmartCityEnum.CITY_INFRASTRUCTURE),
    
    // 智能家居分类
    HOME_SMART_LIGHT(SmartHomeEnum.SMART_LIGHT),
    HOME_SMART_THERMOSTAT(SmartHomeEnum.SMART_THERMOSTAT),
    HOME_SMART_LOCK(SmartHomeEnum.SMART_LOCK),
    HOME_SMART_CURTAIN(SmartHomeEnum.SMART_CURTAIN),
    HOME_SMART_PLUG(SmartHomeEnum.SMART_PLUG),
    HOME_SMART_CAMERA(SmartHomeEnum.SMART_CAMERA),
    HOME_SMART_SPEAKER(SmartHomeEnum.SMART_SPEAKER),
    HOME_HOME_SECURITY(SmartHomeEnum.HOME_SECURITY),
    HOME_ENERGY_MONITORING(SmartHomeEnum.ENERGY_MONITORING),
    HOME_HOME_AUTOMATION(SmartHomeEnum.HOME_AUTOMATION),
    HOME_AIR_QUALITY(SmartHomeEnum.AIR_QUALITY),
    HOME_WATER_CONTROL(SmartHomeEnum.WATER_CONTROL),
    HOME_APPLIANCE_CONTROL(SmartHomeEnum.APPLIANCE_CONTROL),
    HOME_HOME_ASSISTANT(SmartHomeEnum.HOME_ASSISTANT);
    
    private final IconEnumstype iconEnum;
    
    IOTDeviceType(IconEnumstype iconEnum) {
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
     * 获取设备所属分类
     * @return 分类名称
     */
    public String getCategory() {
        if (name().startsWith("AGRICULTURE_")) {
            return "智慧农业";
        } else if (name().startsWith("CITY_")) {
            return "智慧城市";
        } else if (name().startsWith("HOME_")) {
            return "智能家居";
        }
        return "未知分类";
    }
}