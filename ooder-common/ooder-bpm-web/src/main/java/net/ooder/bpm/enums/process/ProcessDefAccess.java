package net.ooder.bpm.enums.process;


import net.ooder.annotation.Enumstype;

public enum ProcessDefAccess implements Enumstype {

    Public("Public", "独立启动"),

    Private("Private", "子流程 "),

    Block("Block", "流程块 ");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ProcessDefAccess(String type, String name) {
	this.type = type;
	this.name = name;
    }

    @Override
    public String toString() {
	return type;
    }

    public static ProcessDefAccess fromType(String typeName) {

	for (ProcessDefAccess type : ProcessDefAccess.values())
        if (type.getType().equals(typeName)) {
            return type;
        }
	return Public;
    }

}
