package net.ooder.enums.iot;


import net.ooder.annotation.IconEnumstype;

/**
 * 智慧农业设备枚举类
 * 使用Remix Icon开源字体库
 */
public enum SmartAgricultureEnum implements IconEnumstype {
    // 常用设备
    SENSOR_SOIL_MOISTURE("soilMoisture", "ri-moisture-line", "土壤湿度传感器"),
    SENSOR_TEMPERATURE("temperature", "ri-temperature-line", "温度传感器"),
    SENSOR_HUMIDITY("humidity", "ri-droplets-line", "湿度传感器"),
    SENSOR_LIGHT("light", "ri-sun-line", "光照传感器"),
    SENSOR_PH("ph", "ri-test-tube-line", "PH值传感器"),
    IRRIGATION_SYSTEM("irrigation", "ri-showers-line", "灌溉系统"),
    GREENHOUSE_CONTROLLER("greenhouse", "ri-home-smile-line", "温室控制器"),
    WEATHER_STATION("weather", "ri-cloud-sun-line", "气象站"),
    
    // 日常管理
    CROP_MONITOR("cropMonitor", "ri-eye-line", "作物监控"),
    FERTILIZER_SYSTEM("fertilizer", "ri-flask-line", "施肥系统"),
    PEST_CONTROL("pestControl", "ri-bug-line", "病虫害防治"),
    GROWTH_TRACKING("growth", "ri-line-chart-line", "生长跟踪"),
    YIELD_PREDICTION("prediction", "ri-bar-chart-line", "产量预测"),
    EQUIPMENT_MAINTENANCE("maintenance", "ri-tools-line", "设备维护"),
    FARM_MANAGEMENT("farmManage", "ri-plant-line", "农场管理");
    
    private final String type;
    private final String name;
    private final String imageClass;
    
    SmartAgricultureEnum(String type, String imageClass, String name) {
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
}