/**
 * $RCSfile: ServiceEventEnums.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.cluster.event;


import net.ooder.annotation.EventEnums;

public enum ServiceEventEnums implements EventEnums {



    addService("添加服務", "addService"),

    delService("删除服务", "delService"),

    updateService("修改服务信息", "updateService"),

    addJar("添加服务", "addJar");

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

    ServiceEventEnums(String name, String method) {

        this.name = name;
        this.method = method;
        this.code = code;

    }

    @Override
    public String toString() {
        return method.toString();
    }


    public static ServiceEventEnums fromMethod(String method) {
        for (ServiceEventEnums type : ServiceEventEnums.values()) {
            if (type.getMethod().equals(method)) {
                return type;
            }
        }
        return null;
    }

    public static ServiceEventEnums fromType(String method) {
        for (ServiceEventEnums type : ServiceEventEnums.values()) {
            if (type.getMethod().equals(method)) {
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
