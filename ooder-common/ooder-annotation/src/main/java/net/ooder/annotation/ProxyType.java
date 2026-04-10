package net.ooder.annotation;


public enum ProxyType implements Enumstype {

    auto("auto", "auto"),

    AJAX("AJAX", "AJAX"),

    JSONP("JSONP", "JSONP"),

    DELETE("XDMI", "XDMI"),;

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ProxyType(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ProxyType fromType(String typeName) {
	for (ProxyType type : ProxyType.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return auto;
    }

}
