package net.ooder.common;

import net.ooder.annotation.Enumstype;

public enum MsgStatus implements Enumstype {
    
  
    DELETE("DELETE", "DELETE"),

    UPDATE("UPDATE", "UPDATE"),

    READED("READED", "READED"),

    ERROR("ERROR", "ERROR"),

    TIMEOUT("TIMEOUT", "TIMEOUT"),

    NORMAL("NORMAL", "NORMAL");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    MsgStatus(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static MsgStatus fromType(String typeName) {
	for (MsgStatus type : MsgStatus.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
