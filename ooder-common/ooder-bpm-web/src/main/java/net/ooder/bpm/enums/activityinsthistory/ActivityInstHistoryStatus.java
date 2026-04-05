package net.ooder.bpm.enums.activityinsthistory;


import net.ooder.annotation.Enumstype;

public enum ActivityInstHistoryStatus implements Enumstype {

    NORMAL("NORMAL", "正常"),

    DELETE("DELETE", "已删除"),

    CLEAR("CLEAR", " 已清空"),

    STATUS("STATUS", "个人状态");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    ActivityInstHistoryStatus(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityInstHistoryStatus fromType(String typeName) {
	for (ActivityInstHistoryStatus type : ActivityInstHistoryStatus.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return NORMAL;
    }

    public static void main(String[] args) {

	switch (fromType("DELETE")) {

	case NORMAL:
	    System.out.println(NORMAL.getName());
	case DELETE:
	    System.out.println(DELETE.getName());
	}
	System.out.println(ActivityInstHistoryStatus.valueOf("NORMAL"));
    }

}
