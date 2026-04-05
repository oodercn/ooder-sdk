package net.ooder.bpm.enums.activitydef.service;


import net.ooder.annotation.Enumstype;

public enum ActivityDefServiceAtt implements Enumstype {

    HTTP_URL("Url", "地址"),

    HTTP_REQUESTTYPE("RequestType", " 请求类型"),

    HTTP_METHOD("Method", "请求类型"),

    HTTP_RESPONSETYPE("ResponseType", " 响应类型"),

    HTTP_SERVICEPARAMS("ServiceParams", " 响应类型"),

    SERVICESELECTEDID("ServiceSelectedID", "回调");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    ActivityDefServiceAtt(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefServiceAtt fromType(String typeName) {
	for (ActivityDefServiceAtt type : ActivityDefServiceAtt.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
