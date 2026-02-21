package net.ooder.annotation;

public class EnumsAttribute {

    String code;
    String name;
    String displayName;
    Integer defaultIndex = 0;
    Object[] attributes;

    public EnumsAttribute() {

    }

    public String getDisplayName() {
	return displayName;
    }

    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }

    public void setAttributes(Object[] attributes) {
	this.attributes = attributes;
    }

    public Object[] getAttributes() {
	return attributes;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Integer getDefaultIndex() {
	return defaultIndex;
    }

    public void setDefaultIndex(Integer defaultIndex) {
	this.defaultIndex = defaultIndex;
    }

}
