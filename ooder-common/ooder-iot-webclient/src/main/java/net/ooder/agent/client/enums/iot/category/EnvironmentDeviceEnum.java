package net.ooder.enums.iot.category;


import net.ooder.annotation.IconEnumstype;

/**
 * 环境设备枚举类
 * 使用Remix Icon开源字体库
 */
public enum EnvironmentDeviceEnum implements IconEnumstype {
    // 环境监测传感器
    TEMPERATURE_SENSOR("temperatureSensor", "ri-temperature-line", "温度传感器"),
    HUMIDITY_SENSOR("humiditySensor", "ri-droplets-line", "湿度传感器"),
    AIR_QUALITY_SENSOR("airQualitySensor", "ri-wind-line", "空气质量传感器"),
    CO2_SENSOR("co2Sensor", "ri-molecule-line", "二氧化碳传感器"),
    PM25_SENSOR("pm25Sensor", "ri-cloud-line", "PM2.5传感器"),
    
    // 气象监测设备
    WEATHER_STATION("weatherStation", "ri-cloud-sun-line", "气象站"),
    RAIN_SENSOR("rainSensor", "ri-rainbow-line", "雨水传感器"),
    WIND_SENSOR("windSensor", "ri-wind-line", "风速传感器"),
    SUNLIGHT_SENSOR("sunlightSensor", "ri-sun-line", "日照传感器"),
    
    // 水质监测设备
    WATER_QUALITY_SENSOR("waterQualitySensor", "ri-ocean-line", "水质传感器"),
    WATER_LEVEL_SENSOR("waterLevelSensor", "ri-water-line", "水位传感器"),
    WATER_FLOW_SENSOR("waterFlowSensor", "ri-swim-line", "水流传感器"),
    
    // 土壤监测设备
    SOIL_MOISTURE_SENSOR("soilMoistureSensor", "ri-moisture-line", "土壤湿度传感器"),
    SOIL_PH_SENSOR("soilPhSensor", "ri-test-tube-line", "土壤PH传感器"),
    SOIL_TEMPERATURE_SENSOR("soilTemperatureSensor", "ri-temperature-line", "土壤温度传感器"),
    
    // 噪音监测设备
    NOISE_SENSOR("noiseSensor", "ri-volume-up-line", "噪音传感器"),
    
    // 环境控制设备
    AIR_PURIFIER("airPurifier", "ri-filter-line", "空气净化器"),
    HUMIDIFIER("humidifier", "ri-droplets-line", "加湿器"),
    DEHUMIDIFIER("dehumidifier", "ri-dehumidifier-line", "除湿机");
    
    private final String type;
    private final String name;
    private final String imageClass;
    
    EnvironmentDeviceEnum(String type, String imageClass, String name) {
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
            case TEMPERATURE_SENSOR:
            case HUMIDITY_SENSOR:
            case AIR_QUALITY_SENSOR:
            case CO2_SENSOR:
            case PM25_SENSOR:
                return "环境监测传感器";
            case WEATHER_STATION:
            case RAIN_SENSOR:
            case WIND_SENSOR:
            case SUNLIGHT_SENSOR:
                return "气象监测设备";
            case WATER_QUALITY_SENSOR:
            case WATER_LEVEL_SENSOR:
            case WATER_FLOW_SENSOR:
                return "水质监测设备";
            case SOIL_MOISTURE_SENSOR:
            case SOIL_PH_SENSOR:
            case SOIL_TEMPERATURE_SENSOR:
                return "土壤监测设备";
            case NOISE_SENSOR:
                return "噪音监测设备";
            case AIR_PURIFIER:
            case HUMIDIFIER:
            case DEHUMIDIFIER:
                return "环境控制设备";
            default:
                return "未知子分类";
        }
    }
}