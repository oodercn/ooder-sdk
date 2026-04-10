package net.ooder.annotation;

import net.ooder.common.CommonYesNoEnum;

public enum SimpleEnum implements Enumstype {

    DEFAULT("DEFAULT");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    SimpleEnum(String type) {
	this.type = type;
	this.name = type;

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
	return null;
    }

}
