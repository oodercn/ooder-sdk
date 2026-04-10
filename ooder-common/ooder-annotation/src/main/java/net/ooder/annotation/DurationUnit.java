package net.ooder.annotation;

public enum DurationUnit implements Enumstype {


    D("D", "日"),
    
    M("M", "月"),
    
    Y("Y", "年"),

    H("H", "时"),

    m("m", "分"),

    s("s", "秒"),

    w("w", "工作日");

    private String type;

    private String name;

    public String getType() {

	return type;
    }

    public String getName() {
	return name;
    }

    DurationUnit(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static DurationUnit fromType(String typeName) {

	for (DurationUnit type : DurationUnit.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
