package net.ooder.bpm.enums.activityinst;


import net.ooder.annotation.Enumstype;

public enum ActivityInstReceiveMethod implements Enumstype {

    BACK("BACK", "退回"),

    SEND("SEND", "发送"),

    READ("READ", " 阅办"),

    SPECIAL("SPECIAL", "特送"),

    RESEND("RESEND", "补发");

    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    ActivityInstReceiveMethod(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static ActivityInstReceiveMethod fromType(String typeName) {
        for (ActivityInstReceiveMethod type : ActivityInstReceiveMethod.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return SEND;
    }

}
