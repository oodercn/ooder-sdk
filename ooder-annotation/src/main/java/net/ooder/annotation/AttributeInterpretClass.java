package net.ooder.annotation;

public enum AttributeInterpretClass{
   	
   
	NUMBER("NUMBER", "数字类型"),
	STRING("STRING", "字符串"), 
	MULTILIST("MULTILIST", "多选选项"),
	BOOLEAN("BOOLEAN", "BOOLEAN"),
	PERSON("PERSON", "参与者"),
	DEPARTMENT("DEPARTMENT", "部门属性");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    AttributeInterpretClass(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static AttributeInterpretClass fromType(String typeName) {
	for (AttributeInterpretClass type : AttributeInterpretClass.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

    
}
