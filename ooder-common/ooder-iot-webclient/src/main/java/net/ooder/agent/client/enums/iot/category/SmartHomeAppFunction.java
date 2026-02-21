package net.ooder.enums.iot.category;


import net.ooder.annotation.IconEnumstype;

/**
 * 全屋智能APP功能分类枚举类
 * 按照全屋智能APP的设计需求，定义主要功能模块分类
 */
public enum SmartHomeAppFunction implements IconEnumstype {
    // 首页模块
    HOME_DASHBOARD("dashboard", "ri-home-line", "首页"),
    
    // 设备控制
    DEVICE_CONTROL("deviceControl", "ri-controller-line", "设备控制"),
    DEVICE_LIGHTING("lighting", "ri-lightbulb-line", "照明控制"),
    DEVICE_CLIMATE("climate", "ri-thermometer-line", "温控系统"),
    DEVICE_SECURITY("security", "ri-shield-check-line", "安防设备"),
    DEVICE_APPLIANCES("appliances", "ri-television-line", "家用电器"),
    DEVICE_SENSORS("sensors", "ri-radar-line", "传感器"),
    
    // 场景模式
    SCENE_CONTROL("sceneControl", "ri-magic-line", "场景控制"),
    SCENE_MORNING("morning", "ri-sun-line", "早晨模式"),
    SCENE_NIGHT("night", "ri-moon-line", "夜间模式"),
    SCENE_AWAY("away", "ri-plane-line", "离家模式"),
    SCENE_HOME("home", "ri-door-open-line", "回家模式"),
    SCENE_MOVIE("movie", "ri-film-line", "影院模式"),
    SCENE_PARTY("party", "ri-music-line", "派对模式"),
    
    // 能源管理
    ENERGY_MANAGEMENT("energy", "ri-bolt-line", "能源管理"),
    ENERGY_CONSUMPTION("consumption", "ri-line-chart-line", "能耗统计"),
    ENERGY_SAVING("saving", "ri-leaf-line", "节能模式"),
    
    // 安全监控
    SECURITY_MONITORING("monitoring", "ri-video-line", "安全监控"),
    SECURITY_RECORDS("records", "ri-history-line", "监控记录"),
    SECURITY_ALERTS("alerts", "ri-alarm-line", "报警中心"),
    
    // 环境监测
    ENVIRONMENT_MONITORING("environment", "ri-leaf-line", "环境监测"),
    ENVIRONMENT_AIR_QUALITY("airQuality", "ri-wind-line", "空气质量"),
    ENVIRONMENT_TEMPERATURE_HUMIDITY("tempHum", "ri-temperature-line", "温湿度"),
    
    // 房间管理
    ROOM_MANAGEMENT("roomManagement", "ri-building-line", "房间管理"),
    ROOM_LIVING_ROOM("livingRoom", "ri-couch-line", "客厅"),
    ROOM_BEDROOM("bedroom", "ri-bed-line", "卧室"),
    ROOM_KITCHEN("kitchen", "ri-cooking-pot-line", "厨房"),
    ROOM_BATHROOM("bathroom", "ri-shower-line", "浴室"),
    ROOM_OFFICE("office", "ri-briefcase-line", "书房"),
    ROOM_OUTDOOR("outdoor", "ri-tree-pine-line", "室外"),
    
    // 个人中心
    USER_PROFILE("profile", "ri-user-line", "个人中心"),
    USER_SETTINGS("settings", "ri-settings-line", "设置"),
    USER_HELP("help", "ri-question-line", "帮助中心"),
    
    // 智能助手
    SMART_ASSISTANT("assistant", "ri-robot-line", "智能助手"),
    
    // 自动化规则
    AUTOMATION_RULES("automation", "ri-loop-right-line", "自动化规则"),
    
    // 设备分组
    DEVICE_GROUPS("groups", "ri-group-line", "设备分组");
    
    private final String type;
    private final String name;
    private final String imageClass;
    
    SmartHomeAppFunction(String type, String imageClass, String name) {
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
     * 获取功能所属模块
     * @return 模块名称
     */
    public String getModule() {
        if (this == HOME_DASHBOARD) {
            return "首页";
        } else if (this == DEVICE_CONTROL || this == DEVICE_LIGHTING || 
                   this == DEVICE_CLIMATE || this == DEVICE_SECURITY || 
                   this == DEVICE_APPLIANCES || this == DEVICE_SENSORS) {
            return "设备控制";
        } else if (this == SCENE_CONTROL || this == SCENE_MORNING || 
                   this == SCENE_NIGHT || this == SCENE_AWAY || 
                   this == SCENE_HOME || this == SCENE_MOVIE || 
                   this == SCENE_PARTY) {
            return "场景模式";
        } else if (this == ENERGY_MANAGEMENT || this == ENERGY_CONSUMPTION || 
                   this == ENERGY_SAVING) {
            return "能源管理";
        } else if (this == SECURITY_MONITORING || this == SECURITY_RECORDS || 
                   this == SECURITY_ALERTS) {
            return "安全监控";
        } else if (this == ENVIRONMENT_MONITORING || this == ENVIRONMENT_AIR_QUALITY || 
                   this == ENVIRONMENT_TEMPERATURE_HUMIDITY) {
            return "环境监测";
        } else if (this == ROOM_MANAGEMENT || this == ROOM_LIVING_ROOM || 
                   this == ROOM_BEDROOM || this == ROOM_KITCHEN || 
                   this == ROOM_BATHROOM || this == ROOM_OFFICE || 
                   this == ROOM_OUTDOOR) {
            return "房间管理";
        } else if (this == USER_PROFILE || this == USER_SETTINGS || 
                   this == USER_HELP) {
            return "个人中心";
        } else if (this == SMART_ASSISTANT) {
            return "智能助手";
        } else if (this == AUTOMATION_RULES) {
            return "自动化规则";
        } else if (this == DEVICE_GROUPS) {
            return "设备分组";
        }
        return "未知模块";
    }
    
    /**
     * 判断是否为父功能
     * @return 是否为父功能
     */
    public boolean isParentFunction() {
        return this == HOME_DASHBOARD || 
               this == DEVICE_CONTROL || 
               this == SCENE_CONTROL || 
               this == ENERGY_MANAGEMENT || 
               this == SECURITY_MONITORING || 
               this == ENVIRONMENT_MONITORING || 
               this == ROOM_MANAGEMENT || 
               this == USER_PROFILE || 
               this == SMART_ASSISTANT || 
               this == AUTOMATION_RULES || 
               this == DEVICE_GROUPS;
    }
    
    /**
     * 获取父功能
     * @return 父功能
     */
    public SmartHomeAppFunction getParentFunction() {
        if (this == HOME_DASHBOARD) {
            return null;
        } else if (this == DEVICE_LIGHTING || this == DEVICE_CLIMATE || 
                   this == DEVICE_SECURITY || this == DEVICE_APPLIANCES || 
                   this == DEVICE_SENSORS) {
            return DEVICE_CONTROL;
        } else if (this == SCENE_MORNING || this == SCENE_NIGHT || 
                   this == SCENE_AWAY || this == SCENE_HOME || 
                   this == SCENE_MOVIE || this == SCENE_PARTY) {
            return SCENE_CONTROL;
        } else if (this == ENERGY_CONSUMPTION || this == ENERGY_SAVING) {
            return ENERGY_MANAGEMENT;
        } else if (this == SECURITY_RECORDS || this == SECURITY_ALERTS) {
            return SECURITY_MONITORING;
        } else if (this == ENVIRONMENT_AIR_QUALITY || 
                   this == ENVIRONMENT_TEMPERATURE_HUMIDITY) {
            return ENVIRONMENT_MONITORING;
        } else if (this == ROOM_LIVING_ROOM || this == ROOM_BEDROOM || 
                   this == ROOM_KITCHEN || this == ROOM_BATHROOM || 
                   this == ROOM_OFFICE || this == ROOM_OUTDOOR) {
            return ROOM_MANAGEMENT;
        } else if (this == USER_SETTINGS || this == USER_HELP) {
            return USER_PROFILE;
        }
        return null;
    }
}