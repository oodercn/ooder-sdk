package net.ooder.annotation;

public enum HttpMethod implements Enumstype {

    auto("auto", "auto"),

    GET("GET", "GET"),

    POST("POST", "POST"),

    DELETE("DELETE", "DELETE"),

    OPTIONS("OPTIONS", "OPTIONS"),

    PATCH("PATCH", "PATCH"),

    HEAD("HEAD", "HEAD"),

    PUT("PUT", "PUT");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    HttpMethod(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static HttpMethod fromType(String typeName) {
	for (HttpMethod type : HttpMethod.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
