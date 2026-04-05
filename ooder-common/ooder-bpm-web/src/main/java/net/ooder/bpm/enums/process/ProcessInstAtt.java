package net.ooder.bpm.enums.process;


import net.ooder.annotation.Enumstype;

public enum ProcessInstAtt implements Enumstype {

    STARTER("STARTER", "STARTER"),

    ROOTFOLDERID("ROOTFOLDERID", "ROOTFOLDERID"),

    PROCESS_INSTANCE_STARTER("PROCESS_INSTANCE_STARTER", "启动者");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    
    public String getName() {
	return name;
    }

    ProcessInstAtt(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ProcessInstAtt fromType(String typeName) {
	for (ProcessInstAtt type : ProcessInstAtt.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
