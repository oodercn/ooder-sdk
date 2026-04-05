package net.ooder.bpm.enums.command;


import net.ooder.annotation.AttributeName;
import net.ooder.annotation.DurationUnit;
import net.ooder.annotation.Enumstype;

public enum CommonEnums implements AttributeName {
    
    AlertTime("AlertTime", "预警时间",null),
    
    COMMANDRETRY("reTryType", "重试方式",CommandRetry.class),

    DurationUnit("DurationUnit", "时间单位", net.ooder.annotation.DurationUnit.class),

    COMMANDPERFORMTYPE("COMMANDPERFORMTYPE", "执行方式", CommandExecType.class);

    private String name;
    private Class<? extends Enumstype>  clazz;
    private String displayName;

    CommonEnums(String name, String displayName, Class<? extends Enumstype> clazz ) {

	this.name = name;
	this.displayName = displayName;
	this.clazz = clazz;

    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Class<? extends Enumstype>  getClazz() {
	return clazz;
    }

    public void setClazz(Class<? extends Enumstype>  clazz) {
	this.clazz = clazz;
    }

    public String getDisplayName() {
	return displayName;
    }

    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }

    @Override
    public String getType() {
	return name;
    }

}
