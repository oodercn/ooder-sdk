package net.ooder.common;

import net.ooder.annotation.Enumstype;

public enum CommonYesNoEnum implements Enumstype {

    DEFAULT("DEFAULT", "默认值"),

    YES("YES", "可以"),

    NO("NO", "不可以");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    CommonYesNoEnum(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static CommonYesNoEnum fromType(String typeName) {
	for (CommonYesNoEnum type : CommonYesNoEnum.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return DEFAULT;
    }

}
