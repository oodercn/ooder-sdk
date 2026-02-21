package net.ooder.agent.client.command;

import  net.ooder.annotation.Enumstype;

public enum BindClusterIdParamEnums implements Enumstype {

    // COMMAND_BINDDEVICE_CLUSTER("clusterid", "clusterid"),

    COMMAND_BINDDEVICE_CLUSTER_ON("on", "开启"),

    COMMAND_BINDDEVICE_CLUSTER_OFF("off", "关闭"),

    COMMAND_BINDDEVICE_CLUSTER_ONOFF("on/off", "切换");

    private String type;

    private String name;


    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    BindClusterIdParamEnums(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static BindClusterIdParamEnums fromType(String typeName) {
	for (BindClusterIdParamEnums type : BindClusterIdParamEnums.values()) {
	    if (type.getType().toUpperCase().equals(typeName.toUpperCase())) {
		return type;
	    }
	}
	return null;
    }

}
