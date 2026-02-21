package net.ooder.enums.iot.category;


import net.ooder.annotation.IconEnumstype;

/**
 * 智慧农业设备枚举类
 * 使用Remix Icon开源字体库
 */
public enum AgricultureDeviceEnum implements IconEnumstype {
    // 土壤监测设备
    SENSOR_SOIL_MOISTURE("soilMoisture", "ri-moisture-line", "土壤湿度传感器"),
    SENSOR_SOIL_TEMPERATURE("soilTemperature", "ri-temperature-line", "土壤温度传感器"),
    SENSOR_SOIL_PH("soilPh", "ri-test-tube-line", "土壤PH值传感器"),
    SENSOR_SOIL_NUTRIENT("soilNutrient", "ri-molecule-line", "土壤养分传感器"),
    
    // 环境监测设备
    SENSOR_AIR_TEMPERATURE("airTemperature", "ri-temperature-line", "空气温度传感器"),
    SENSOR_AIR_HUMIDITY("airHumidity", "ri-droplets-line", "空气湿度传感器"),
    SENSOR_LIGHT_INTENSITY("light", "ri-sun-line", "光照强度传感器"),
    SENSOR_CO2("co2", "ri-cloud-line", "二氧化碳传感器"),
    WEATHER_STATION("weather", "ri-cloud-sun-line", "农业气象站"),
    
    // 灌溉与施肥设备
    IRRIGATION_SYSTEM("irrigation", "ri-showers-line", "灌溉系统"),
    FERTILIZER_SYSTEM("fertilizer", "ri-flask-line", "施肥系统"),
    DRIP_IRRIGATION("dripIrrigation", "ri-water-line", "滴灌系统"),
    SPRAY_IRRIGATION("sprayIrrigation", "ri-showers-line", "喷灌系统"),
    
    // 温室控制设备
    GREENHOUSE_CONTROLLER("greenhouse", "ri-home-smile-line", "温室控制器"),
    GREENHOUSE_VENT("greenhouseVent", "ri-window-line", "温室通风口控制器"),
    GREENHOUSE_SHADING("greenhouseShading", "ri-shield-line", "温室遮阳控制器"),
    
    // 作物管理设备
    CROP_MONITOR("cropMonitor", "ri-eye-line", "作物监控摄像头"),
    PEST_CONTROL("pestControl", "ri-bug-line", "病虫害防治系统"),
    GROWTH_TRACKING("growth", "ri-line-chart-line", "作物生长跟踪系统"),
    YIELD_PREDICTION("prediction", "ri-bar-chart-line", "产量预测系统"),
    
    // 农业机械与设备
    SMART_TRACTOR("smartTractor", "ri-tractor-line", "智能拖拉机"),
    HARVESTING_MACHINE("harvestingMachine", "ri-scissors-line", "收获机械"),
    PLANTING_MACHINE("plantingMachine", "ri-seed-line", "播种机械"),
    
    // 农场管理系统
    FARM_MANAGEMENT("farmManage", "ri-plant-line", "农场管理系统"),
    EQUIPMENT_MAINTENANCE("maintenance", "ri-tools-line", "设备维护系统"),
    SUPPLY_CHAIN_MANAGEMENT("supplyChain", "ri-exchange-line", "供应链管理系统");
    
    private final String type;
    private final String name;
    private final String imageClass;
    
    AgricultureDeviceEnum(String type, String imageClass, String name) {
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
            case SENSOR_SOIL_MOISTURE:
            case SENSOR_SOIL_TEMPERATURE:
            case SENSOR_SOIL_PH:
            case SENSOR_SOIL_NUTRIENT:
                return "土壤监测设备";
            case SENSOR_AIR_TEMPERATURE:
            case SENSOR_AIR_HUMIDITY:
            case SENSOR_LIGHT_INTENSITY:
            case SENSOR_CO2:
            case WEATHER_STATION:
                return "环境监测设备";
            case IRRIGATION_SYSTEM:
            case FERTILIZER_SYSTEM:
            case DRIP_IRRIGATION:
            case SPRAY_IRRIGATION:
                return "灌溉与施肥设备";
            case GREENHOUSE_CONTROLLER:
            case GREENHOUSE_VENT:
            case GREENHOUSE_SHADING:
                return "温室控制设备";
            case CROP_MONITOR:
            case PEST_CONTROL:
            case GROWTH_TRACKING:
            case YIELD_PREDICTION:
                return "作物管理设备";
            case SMART_TRACTOR:
            case HARVESTING_MACHINE:
            case PLANTING_MACHINE:
                return "农业机械与设备";
            case FARM_MANAGEMENT:
            case EQUIPMENT_MAINTENANCE:
            case SUPPLY_CHAIN_MANAGEMENT:
                return "农场管理系统";
            default:
                return "未知子分类";
        }
    }
}