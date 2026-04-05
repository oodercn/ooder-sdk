package net.ooder.bpm.enums.process;


import net.ooder.annotation.Enumstype;

public enum ProcessInstStatus implements Enumstype {

    running("running", "流程运行中状态"),

    open("open", "打开"),

    notRunning("notRunning", "草稿状态"),

    notStarted("notStarted", "未启动"),

    suspended("suspended", "挂起状态"),

    closed("closed", "closed"),

    aborted("aborted", "流程中止"),

    terminated("terminated", "恢复"),

    completed("completed", "流程结束");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ProcessInstStatus(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ProcessInstStatus fromType(String typeName) {
	for (ProcessInstStatus type : ProcessInstStatus.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return running;
    }

}
