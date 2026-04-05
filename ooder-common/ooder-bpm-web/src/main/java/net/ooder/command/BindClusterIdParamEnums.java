package net.ooder.command;

import net.ooder.annotation.Enums;

public enum BindClusterIdParamEnums implements Enums {
    CLUSTER_ID("clusterId"),
    GATEWAY_ID("gatewayId"),
    DEVICE_ID("deviceId");

    private final String paramName;

    BindClusterIdParamEnums(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }

    public String getType() {
        return name();
    }

    public String getName() {
        return paramName;
    }
}
