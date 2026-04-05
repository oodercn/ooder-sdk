package net.ooder.bpm.enums.activitydef;


import net.ooder.annotation.Enumstype;

public enum ActivityDefDeadLineOperation implements Enumstype {

    DEFAULT("DEFAULT", "默认值"),
    
    DELAY("DELAY", "延期办理"),
    
    TAKEBACK("TAKEBACK", "自动收回"),
    
    SURROGATE("SURROGATE", "代办人自动接收");
    

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }



    ActivityDefDeadLineOperation(String type, String name) {
	this.type = type;
	this.name = name;

    }
    
    
    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefDeadLineOperation fromType(String typeName) {
	for (ActivityDefDeadLineOperation type : ActivityDefDeadLineOperation.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return DEFAULT;
    }

}
