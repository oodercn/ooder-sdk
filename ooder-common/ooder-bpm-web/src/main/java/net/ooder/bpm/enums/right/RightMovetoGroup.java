package net.ooder.bpm.enums.right;


import net.ooder.annotation.Enumstype;

public enum RightMovetoGroup implements Enumstype {

    MOVEPERFORMERTO("MOVEPERFORMERTO", "办理之后权限组"),

    MOVESPONSORTO("MOVESPONSORTO", "代办人"),

    MOVEREADERTO("MOVEREADERTO", "传阅之后权限组");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    RightMovetoGroup(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static RightMovetoGroup fromType(String typeName) {
	for (RightMovetoGroup type : RightMovetoGroup.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return MOVEPERFORMERTO;
    }

}
