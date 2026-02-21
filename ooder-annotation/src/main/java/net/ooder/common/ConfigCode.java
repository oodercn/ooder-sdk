package net.ooder.common;

import net.ooder.annotation.Enumstype;

public enum ConfigCode implements Enumstype {

    client("client", "客户端"),

    app("app", "业务服务"),

    cluster("cluster", "系统服务"),

    userdef("userdef", "用户服务"),

    service("service", "API应用"),

    org("org", "组织机构"),

    data("data", "数据处理"),

    device("device", "设备连接"),

    vfs("vfs", "文件存储"),

    gw("gw", "网关长连接"),

    vfsstore("vfsstore", "文件存储"),

    gwdata("gwdata", "网关数据上报"),

    udp("udp", "消息调度"),

    mqtt("mqtt", "长连接"),

    scene("scene", "流程服务");

    private String type;

    private String name;

    private String desc;


    ConfigCode(String type, String desc) {
        this.type = type;
        this.name = name();
        this.desc = desc;
    }


    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }


    @Override
    public String toString() {
        return name;
    }

    public static ConfigCode fromType(String typeName) {
        if (typeName != null) {
            for (ConfigCode type : ConfigCode.values()) {
                if (type.getType().toUpperCase().equals(typeName.toUpperCase())) {
                    return type;
                }
            }
        }
        return app;
    }


}
