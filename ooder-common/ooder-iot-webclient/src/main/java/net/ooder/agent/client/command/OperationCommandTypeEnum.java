package net.ooder.agent.client.command;

import  net.ooder.annotation.Enumstype;

public enum OperationCommandTypeEnum implements Enumstype {

    dimmableLightOperation("dimmableLightOperation", "调级"),

    scene("scene", "场景"),

    group("group", "组操作切换"),

    mainsOutLetOperation("mainsOutLetOperation", "电源设备");

    private String type;

    private String name;


    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    OperationCommandTypeEnum(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static OperationCommandTypeEnum fromType(String typeName) {
	for (OperationCommandTypeEnum type : OperationCommandTypeEnum.values()) {
	    if (type.getType().toUpperCase().equals(typeName.toUpperCase())) {
		return type;
	    }
	}
	return null;
    }

}
