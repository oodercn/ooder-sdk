package net.ooder.annotation;



public enum RequestType implements Enumstype {

    FORM("FORM", "FORM"),

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

    RequestType(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static RequestType fromType(String typeName) {
	for (RequestType type : RequestType.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return FORM;
    }

}
