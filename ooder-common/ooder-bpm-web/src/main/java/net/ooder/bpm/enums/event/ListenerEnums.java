package net.ooder.bpm.enums.event;


import net.ooder.annotation.Enumstype;

public enum ListenerEnums implements Enumstype {

    PROCESS_LISTENER_EVENT("Process", "Process"),

    ACTIVITY_LISTENER_EVENT("Activity", "Activity"),

    RIGHT_LISTENER_EVENT("Right", "Right"),

    COMMAND_LISTENER_EVENT("Command", "Command"),

    EXPRESSIONLISENTERTYPE_EXPRESSION("Expression", "Expression");

    private String type;

    private String name;

    ListenerEnums(String type, String name) {
	this.type = type;
	this.name = name;

    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String toString() {
	return type;
    }

    public static ListenerEnums fromType(String typeName) {
	for (ListenerEnums type : ListenerEnums.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
