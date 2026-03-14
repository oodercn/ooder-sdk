package net.ooder.enums.iot;

import net.ooder.annotation.IconEnumstype;
import net.ooder.enums.iot.category.*;

/**
 * IoT设备按功能分类主枚举类
 * 汇总所有按功能分类的IoT设备，便于统一管理和使用
 */
public enum IOTCategoryDeviceType implements IconEnumstype {
    // 安防设备
    SECURITY_SMART_LOCK(SecurityDeviceEnum.SMART_LOCK),
    SECURITY_SECURITY_CAMERA(SecurityDeviceEnum.SECURITY_CAMERA),
    SECURITY_DOOR_SENSOR(SecurityDeviceEnum.DOOR_SENSOR),
    SECURITY_WINDOW_SENSOR(SecurityDeviceEnum.WINDOW_SENSOR),
    SECURITY_MOTION_SENSOR(SecurityDeviceEnum.MOTION_SENSOR),
    SECURITY_GLASS_BREAK_SENSOR(SecurityDeviceEnum.GLASS_BREAK_SENSOR),
    SECURITY_ALARM_SYSTEM(SecurityDeviceEnum.ALARM_SYSTEM),
    SECURITY_SECURITY_KEYPAD(SecurityDeviceEnum.SECURITY_KEYPAD),
    SECURITY_EMERGENCY_BUTTON(SecurityDeviceEnum.EMERGENCY_BUTTON),
    SECURITY_SURVEILLANCE_CAMERA(SecurityDeviceEnum.SURVEILLANCE_CAMERA),
    SECURITY_ACCESS_CONTROL(SecurityDeviceEnum.ACCESS_CONTROL),
    SECURITY_FIRE_ALARM(SecurityDeviceEnum.FIRE_ALARM),
    SECURITY_SMOKE_DETECTOR(SecurityDeviceEnum.SMOKE_DETECTOR),
    SECURITY_GAS_DETECTOR(SecurityDeviceEnum.GAS_DETECTOR),
    SECURITY_BODY_TEMPERATURE_MONITOR(SecurityDeviceEnum.BODY_TEMPERATURE_MONITOR),
    
    // 能源设备
    ENERGY_SMART_METER(EnergyDeviceEnum.SMART_METER),
    ENERGY_ENERGY_MONITOR(EnergyDeviceEnum.ENERGY_MONITOR),
    ENERGY_POWER_ANALYZER(EnergyDeviceEnum.POWER_ANALYZER),
    ENERGY_SMART_PLUG(EnergyDeviceEnum.SMART_PLUG),
    ENERGY_SMART_SWITCH(EnergyDeviceEnum.SMART_SWITCH),
    ENERGY_SMART_THERMOSTAT(EnergyDeviceEnum.SMART_THERMOSTAT),
    ENERGY_HEATING_CONTROLLER(EnergyDeviceEnum.HEATING_CONTROLLER),
    ENERGY_COOLING_CONTROLLER(EnergyDeviceEnum.COOLING_CONTROLLER),
    ENERGY_SOLAR_PANEL(EnergyDeviceEnum.SOLAR_PANEL),
    ENERGY_WIND_TURBINE(EnergyDeviceEnum.WIND_TURBINE),
    ENERGY_BATTERY_STORAGE(EnergyDeviceEnum.BATTERY_STORAGE),
    ENERGY_ENERGY_MANAGEMENT_SYSTEM(EnergyDeviceEnum.ENERGY_MANAGEMENT_SYSTEM),
    ENERGY_DEMAND_RESPONSE(EnergyDeviceEnum.DEMAND_RESPONSE),
    ENERGY_MICROGRID_CONTROLLER(EnergyDeviceEnum.MICROGRID_CONTROLLER),
    
    // 照明设备
    LIGHTING_SMART_LIGHT(LightingDeviceEnum.SMART_LIGHT),
    LIGHTING_SMART_BULB(LightingDeviceEnum.SMART_BULB),
    LIGHTING_LED_STRIP(LightingDeviceEnum.LED_STRIP),
    LIGHTING_DOWNLIGHT(LightingDeviceEnum.DOWNLIGHT),
    LIGHTING_SPOTLIGHT(LightingDeviceEnum.SPOTLIGHT),
    LIGHTING_PANEL_LIGHT(LightingDeviceEnum.PANEL_LIGHT),
    LIGHTING_LIGHT_SWITCH(LightingDeviceEnum.LIGHT_SWITCH),
    LIGHTING_DIMMER_SWITCH(LightingDeviceEnum.DIMMER_SWITCH),
    LIGHTING_SCENE_CONTROLLER(LightingDeviceEnum.SCENE_CONTROLLER),
    LIGHTING_MOTION_SENSOR_LIGHT(LightingDeviceEnum.MOTION_SENSOR_LIGHT),
    LIGHTING_AMBIENT_LIGHT(LightingDeviceEnum.AMBIENT_LIGHT),
    LIGHTING_NIGHT_LIGHT(LightingDeviceEnum.NIGHT_LIGHT),
    LIGHTING_EMERGENCY_LIGHT(LightingDeviceEnum.EMERGENCY_LIGHT),
    LIGHTING_SMART_STREETLIGHT(LightingDeviceEnum.SMART_STREETLIGHT),
    LIGHTING_GARDEN_LIGHT(LightingDeviceEnum.GARDEN_LIGHT),
    LIGHTING_SECURITY_LIGHT(LightingDeviceEnum.SECURITY_LIGHT),
    LIGHTING_LIGHT_SENSOR(LightingDeviceEnum.LIGHT_SENSOR),
    LIGHTING_DAYLIGHT_SENSOR(LightingDeviceEnum.DAYLIGHT_SENSOR),
    
    // 家电设备
    APPLIANCE_SMART_REFRIGERATOR(ApplianceDeviceEnum.SMART_REFRIGERATOR),
    APPLIANCE_SMART_OVEN(ApplianceDeviceEnum.SMART_OVEN),
    APPLIANCE_SMART_MICROWAVE(ApplianceDeviceEnum.SMART_MICROWAVE),
    APPLIANCE_SMART_STOVE(ApplianceDeviceEnum.SMART_STOVE),
    APPLIANCE_SMART_DISHWASHER(ApplianceDeviceEnum.SMART_DISHWASHER),
    APPLIANCE_SMART_COFFEE_MAKER(ApplianceDeviceEnum.SMART_COFFEE_MAKER),
    APPLIANCE_SMART_VACUUM(ApplianceDeviceEnum.SMART_VACUUM),
    APPLIANCE_SMART_WASHER(ApplianceDeviceEnum.SMART_WASHER),
    APPLIANCE_SMART_DRYER(ApplianceDeviceEnum.SMART_DRYER),
    APPLIANCE_SMART_TV(ApplianceDeviceEnum.SMART_TV),
    APPLIANCE_SMART_SPEAKER(ApplianceDeviceEnum.SMART_SPEAKER),
    APPLIANCE_SMART_PROJECTOR(ApplianceDeviceEnum.SMART_PROJECTOR),
    APPLIANCE_GAME_CONSOLE(ApplianceDeviceEnum.GAME_CONSOLE),
    APPLIANCE_SMART_AIR_CONDITIONER(ApplianceDeviceEnum.SMART_AIR_CONDITIONER),
    APPLIANCE_SMART_AIR_PURIFIER(ApplianceDeviceEnum.SMART_AIR_PURIFIER),
    APPLIANCE_SMART_HUMIDIFIER(ApplianceDeviceEnum.SMART_HUMIDIFIER),
    APPLIANCE_SMART_DEHUMIDIFIER(ApplianceDeviceEnum.SMART_DEHUMIDIFIER),
    APPLIANCE_SMART_WATER_HEATER(ApplianceDeviceEnum.SMART_WATER_HEATER),
    APPLIANCE_SMART_IRON(ApplianceDeviceEnum.SMART_IRON),
    APPLIANCE_SMART_TOASTER(ApplianceDeviceEnum.SMART_TOASTER),
    APPLIANCE_SMART_BLENDER(ApplianceDeviceEnum.SMART_BLENDER),
    
    // 环境设备
    ENVIRONMENT_TEMPERATURE_SENSOR(EnvironmentDeviceEnum.TEMPERATURE_SENSOR),
    ENVIRONMENT_HUMIDITY_SENSOR(EnvironmentDeviceEnum.HUMIDITY_SENSOR),
    ENVIRONMENT_AIR_QUALITY_SENSOR(EnvironmentDeviceEnum.AIR_QUALITY_SENSOR),
    ENVIRONMENT_CO2_SENSOR(EnvironmentDeviceEnum.CO2_SENSOR),
    ENVIRONMENT_PM25_SENSOR(EnvironmentDeviceEnum.PM25_SENSOR),
    ENVIRONMENT_WEATHER_STATION(EnvironmentDeviceEnum.WEATHER_STATION),
    ENVIRONMENT_RAIN_SENSOR(EnvironmentDeviceEnum.RAIN_SENSOR),
    ENVIRONMENT_WIND_SENSOR(EnvironmentDeviceEnum.WIND_SENSOR),
    ENVIRONMENT_SUNLIGHT_SENSOR(EnvironmentDeviceEnum.SUNLIGHT_SENSOR),
    ENVIRONMENT_WATER_QUALITY_SENSOR(EnvironmentDeviceEnum.WATER_QUALITY_SENSOR),
    ENVIRONMENT_WATER_LEVEL_SENSOR(EnvironmentDeviceEnum.WATER_LEVEL_SENSOR),
    ENVIRONMENT_WATER_FLOW_SENSOR(EnvironmentDeviceEnum.WATER_FLOW_SENSOR),
    ENVIRONMENT_SOIL_MOISTURE_SENSOR(EnvironmentDeviceEnum.SOIL_MOISTURE_SENSOR),
    ENVIRONMENT_SOIL_PH_SENSOR(EnvironmentDeviceEnum.SOIL_PH_SENSOR),
    ENVIRONMENT_SOIL_TEMPERATURE_SENSOR(EnvironmentDeviceEnum.SOIL_TEMPERATURE_SENSOR),
    ENVIRONMENT_NOISE_SENSOR(EnvironmentDeviceEnum.NOISE_SENSOR),
    ENVIRONMENT_AIR_PURIFIER(EnvironmentDeviceEnum.AIR_PURIFIER),
    ENVIRONMENT_HUMIDIFIER(EnvironmentDeviceEnum.HUMIDIFIER),
    ENVIRONMENT_DEHUMIDIFIER(EnvironmentDeviceEnum.DEHUMIDIFIER),
    
    // 健康设备
    HEALTH_SMART_WATCH(HealthDeviceEnum.SMART_WATCH),
    HEALTH_FITNESS_TRACKER(HealthDeviceEnum.FITNESS_TRACKER),
    HEALTH_SMART_BAND(HealthDeviceEnum.SMART_BAND),
    HEALTH_SMART_GLASSES(HealthDeviceEnum.SMART_GLASSES),
    HEALTH_HEART_RATE_MONITOR(HealthDeviceEnum.HEART_RATE_MONITOR),
    HEALTH_BLOOD_PRESSURE_MONITOR(HealthDeviceEnum.BLOOD_PRESSURE_MONITOR),
    HEALTH_BLOOD_GLUCOSE_MONITOR(HealthDeviceEnum.BLOOD_GLUCOSE_MONITOR),
    HEALTH_BODY_TEMPERATURE_MONITOR(HealthDeviceEnum.BODY_TEMPERATURE_MONITOR),
    HEALTH_WEIGHT_SCALE(HealthDeviceEnum.WEIGHT_SCALE),
    HEALTH_SLEEP_TRACKER(HealthDeviceEnum.SLEEP_TRACKER),
    HEALTH_NEBULIZER(HealthDeviceEnum.NEBULIZER),
    HEALTH_OXYGEN_CONCENTRATOR(HealthDeviceEnum.OXYGEN_CONCENTRATOR),
    HEALTH_BLOOD_OXYGEN_MONITOR(HealthDeviceEnum.BLOOD_OXYGEN_MONITOR),
    HEALTH_ECG_MONITOR(HealthDeviceEnum.ECG_MONITOR),
    HEALTH_SMART_PILL_DISPENSER(HealthDeviceEnum.SMART_PILL_DISPENSER),
    HEALTH_UV_SANITIZER(HealthDeviceEnum.UV_SANITIZER),
    HEALTH_AIR_QUALITY_MONITOR(HealthDeviceEnum.AIR_QUALITY_MONITOR),
    
    // 智慧农业设备
    AGRICULTURE_SOIL_MOISTURE(AgricultureDeviceEnum.SENSOR_SOIL_MOISTURE),
    AGRICULTURE_SOIL_TEMPERATURE(AgricultureDeviceEnum.SENSOR_SOIL_TEMPERATURE),
    AGRICULTURE_SOIL_PH(AgricultureDeviceEnum.SENSOR_SOIL_PH),
    AGRICULTURE_SOIL_NUTRIENT(AgricultureDeviceEnum.SENSOR_SOIL_NUTRIENT),
    AGRICULTURE_AIR_TEMPERATURE(AgricultureDeviceEnum.SENSOR_AIR_TEMPERATURE),
    AGRICULTURE_AIR_HUMIDITY(AgricultureDeviceEnum.SENSOR_AIR_HUMIDITY),
    AGRICULTURE_LIGHT_INTENSITY(AgricultureDeviceEnum.SENSOR_LIGHT_INTENSITY),
    AGRICULTURE_CO2(AgricultureDeviceEnum.SENSOR_CO2),
    AGRICULTURE_WEATHER_STATION(AgricultureDeviceEnum.WEATHER_STATION),
    AGRICULTURE_IRRIGATION_SYSTEM(AgricultureDeviceEnum.IRRIGATION_SYSTEM),
    AGRICULTURE_FERTILIZER_SYSTEM(AgricultureDeviceEnum.FERTILIZER_SYSTEM),
    AGRICULTURE_DRIP_IRRIGATION(AgricultureDeviceEnum.DRIP_IRRIGATION),
    AGRICULTURE_SPRAY_IRRIGATION(AgricultureDeviceEnum.SPRAY_IRRIGATION),
    AGRICULTURE_GREENHOUSE_CONTROLLER(AgricultureDeviceEnum.GREENHOUSE_CONTROLLER),
    AGRICULTURE_GREENHOUSE_VENT(AgricultureDeviceEnum.GREENHOUSE_VENT),
    AGRICULTURE_GREENHOUSE_SHADING(AgricultureDeviceEnum.GREENHOUSE_SHADING),
    AGRICULTURE_CROP_MONITOR(AgricultureDeviceEnum.CROP_MONITOR),
    AGRICULTURE_PEST_CONTROL(AgricultureDeviceEnum.PEST_CONTROL),
    AGRICULTURE_GROWTH_TRACKING(AgricultureDeviceEnum.GROWTH_TRACKING),
    AGRICULTURE_YIELD_PREDICTION(AgricultureDeviceEnum.YIELD_PREDICTION),
    AGRICULTURE_SMART_TRACTOR(AgricultureDeviceEnum.SMART_TRACTOR),
    AGRICULTURE_HARVESTING_MACHINE(AgricultureDeviceEnum.HARVESTING_MACHINE),
    AGRICULTURE_PLANTING_MACHINE(AgricultureDeviceEnum.PLANTING_MACHINE),
    AGRICULTURE_FARM_MANAGEMENT(AgricultureDeviceEnum.FARM_MANAGEMENT),
    AGRICULTURE_EQUIPMENT_MAINTENANCE(AgricultureDeviceEnum.EQUIPMENT_MAINTENANCE),
    AGRICULTURE_SUPPLY_CHAIN_MANAGEMENT(AgricultureDeviceEnum.SUPPLY_CHAIN_MANAGEMENT),
    
    // 智慧城市设备
    CITY_TRAFFIC_LIGHT(CityDeviceEnum.TRAFFIC_LIGHT),
    CITY_SMART_PARKING(CityDeviceEnum.SMART_PARKING),
    CITY_TRAFFIC_SENSOR(CityDeviceEnum.TRAFFIC_SENSOR),
    CITY_TRAFFIC_CAMERA(CityDeviceEnum.TRAFFIC_CAMERA),
    CITY_VEHICLE_RECOGNITION(CityDeviceEnum.VEHICLE_RECOGNITION),
    CITY_TRAFFIC_MANAGEMENT(CityDeviceEnum.TRAFFIC_MANAGEMENT),
    CITY_SURVEILLANCE_CAMERA(CityDeviceEnum.SURVEILLANCE_CAMERA),
    CITY_PUBLIC_SECURITY(CityDeviceEnum.PUBLIC_SECURITY),
    CITY_EMERGENCY_BUTTON(CityDeviceEnum.EMERGENCY_BUTTON),
    CITY_FIRE_DETECTION(CityDeviceEnum.FIRE_DETECTION),
    CITY_ENERGY_MANAGEMENT(CityDeviceEnum.ENERGY_MANAGEMENT),
    CITY_SMART_METER(CityDeviceEnum.SMART_METER),
    CITY_SOLAR_PANEL(CityDeviceEnum.SOLAR_PANEL),
    CITY_WIND_TURBINE(CityDeviceEnum.WIND_TURBINE),
    CITY_SMART_STREETLIGHT(CityDeviceEnum.SMART_STREETLIGHT),
    CITY_AREA_LIGHTING(CityDeviceEnum.AREA_LIGHTING),
    CITY_PARK_LIGHTING(CityDeviceEnum.PARK_LIGHTING),
    CITY_ENVIRONMENTAL_SENSOR(CityDeviceEnum.ENVIRONMENTAL_SENSOR),
    CITY_AIR_QUALITY_SENSOR(CityDeviceEnum.AIR_QUALITY_SENSOR),
    CITY_NOISE_SENSOR(CityDeviceEnum.NOISE_SENSOR),
    CITY_WEATHER_STATION(CityDeviceEnum.WEATHER_STATION),
    CITY_WATER_MANAGEMENT(CityDeviceEnum.WATER_MANAGEMENT),
    CITY_WATER_METER(CityDeviceEnum.WATER_METER),
    CITY_WATER_QUALITY_SENSOR(CityDeviceEnum.WATER_QUALITY_SENSOR),
    CITY_LEAK_DETECTION(CityDeviceEnum.LEAK_DETECTION),
    CITY_SMART_BIN(CityDeviceEnum.SMART_BIN),
    CITY_WASTE_MANAGEMENT(CityDeviceEnum.WASTE_MANAGEMENT),
    CITY_CLEANING_ROBOT(CityDeviceEnum.CLEANING_ROBOT),
    CITY_CITY_INFRASTRUCTURE(CityDeviceEnum.CITY_INFRASTRUCTURE),
    CITY_BRIDGE_MONITOR(CityDeviceEnum.BRIDGE_MONITOR),
    CITY_ROAD_MONITOR(CityDeviceEnum.ROAD_MONITOR),
    CITY_TUNNEL_MONITOR(CityDeviceEnum.TUNNEL_MONITOR),
    CITY_EMERGENCY_RESPONSE(CityDeviceEnum.EMERGENCY_RESPONSE),
    CITY_DISASTER_PREVENTION(CityDeviceEnum.DISASTER_PREVENTION),
    CITY_SMART_CITY_HALL(CityDeviceEnum.SMART_CITY_HALL),
    CITY_PUBLIC_WIFI(CityDeviceEnum.PUBLIC_WIFI),
    CITY_SMART_BENCH(CityDeviceEnum.SMART_BENCH);
    
    private final IconEnumstype iconEnum;
    
    IOTCategoryDeviceType(IconEnumstype iconEnum) {
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
     * 获取设备所属主分类
     * @return 主分类名称
     */
    public String getCategory() {
        if (name().startsWith("SECURITY_")) {
            return "安防设备";
        } else if (name().startsWith("ENERGY_")) {
            return "能源设备";
        } else if (name().startsWith("LIGHTING_")) {
            return "照明设备";
        } else if (name().startsWith("APPLIANCE_")) {
            return "家电设备";
        } else if (name().startsWith("ENVIRONMENT_")) {
            return "环境设备";
        } else if (name().startsWith("HEALTH_")) {
            return "健康设备";
        } else if (name().startsWith("AGRICULTURE_")) {
            return "智慧农业设备";
        } else if (name().startsWith("CITY_")) {
            return "智慧城市设备";
        }
        return "未知分类";
    }
    
    /**
     * 获取设备所属子分类
     * @return 子分类名称
     */
    public String getSubCategory() {
        if (iconEnum instanceof SecurityDeviceEnum) {
            return ((SecurityDeviceEnum) iconEnum).getSubCategory();
        } else if (iconEnum instanceof EnergyDeviceEnum) {
            return ((EnergyDeviceEnum) iconEnum).getSubCategory();
        } else if (iconEnum instanceof LightingDeviceEnum) {
            return ((LightingDeviceEnum) iconEnum).getSubCategory();
        } else if (iconEnum instanceof ApplianceDeviceEnum) {
            return ((ApplianceDeviceEnum) iconEnum).getSubCategory();
        } else if (iconEnum instanceof EnvironmentDeviceEnum) {
            return ((EnvironmentDeviceEnum) iconEnum).getSubCategory();
        } else if (iconEnum instanceof HealthDeviceEnum) {
            return ((HealthDeviceEnum) iconEnum).getSubCategory();
        } else if (iconEnum instanceof AgricultureDeviceEnum) {
            return ((AgricultureDeviceEnum) iconEnum).getSubCategory();
        } else if (iconEnum instanceof CityDeviceEnum) {
            return ((CityDeviceEnum) iconEnum).getSubCategory();
        }
        return "未知子分类";
    }
}