package net.ooder.annotation;



public enum ResponseType implements Enumstype {

    TEXT("TEXT", "TEXT"),

    JSON("JSON", "JSON"),

    XML("XML", "XML"),

    SOAP("SOAP", "SOAP");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    ResponseType(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ResponseType fromType(String typeName) {
	for (ResponseType type : ResponseType.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return JSON;
    }

}
