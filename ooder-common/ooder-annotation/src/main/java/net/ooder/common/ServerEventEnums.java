package net.ooder.common;

import net.ooder.annotation.EventEnums;

public enum ServerEventEnums implements EventEnums {


    serverStarting("服务器正在启动", "serverStarting"),

    serverStarted("服务器已启动", "serverStarted"),

    serverStopping("服务器正在停止", "serverStopping"),

    serverStopped("服务器已停止", "serverStopped"),


    systemActivating("正在激活子系统", "systemActivating"),

    systemActivated("子系统激活完毕", "systemActivated"),

    systemFreezing("正在冻结子系统", "systemFreezing"),

    systemFreezed("冻结子系统完成", "systemFreezed"),


    systemSaving("正在保存子系统", "systemSaving"),

    systemSaved("保存子系统完毕", "systemSaved"),

    systemDeleting("正在删除子系统", "systemDeleting"),

    systemDeleted("子系统删除完毕", "systemDeleted");


    private String name;

    private Integer code;

    private String method;


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    ServerEventEnums(String name, String method) {

        this.name = name;
        this.method = method;
        this.code = code;

    }

    @Override
    public String toString() {
        return method.toString();
    }


    public static ServerEventEnums fromMethod(String method) {
        for (ServerEventEnums type : ServerEventEnums.values()) {
            if (type.getMethod().equals(method)) {
                return type;
            }
        }
        return null;
    }

    public static ServerEventEnums fromType(String typeName) {
        for (ServerEventEnums type : ServerEventEnums.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String getType() {
        return method.toString();
    }


}
