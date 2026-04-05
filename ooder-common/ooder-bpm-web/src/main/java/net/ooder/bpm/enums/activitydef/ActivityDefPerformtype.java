package net.ooder.bpm.enums.activitydef;


import net.ooder.annotation.Enumstype;

public enum ActivityDefPerformtype implements Enumstype {

    SINGLE("SINGLE", "单人办理"),

    MULTIPLE("MULTIPLE", "多人办理"),

    JOINTSIGN("JOINTSIGN", "合会签办理"),

    NEEDNOTSELECT("NEEDNOTSELECT", "无需选择，直接送达"),

    NOSELECT("NOSELECT", "不需要选择，所有候选人成为办理人"),

    DEFAULT("DEFAULT", "默认值");
    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    ActivityDefPerformtype(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static ActivityDefPerformtype fromType(String typeName) {
        for (ActivityDefPerformtype type : ActivityDefPerformtype.values()) {
            if (type.getType().toUpperCase().equals(typeName.toUpperCase())) {
                return type;
            }
        }
        return DEFAULT;
    }

}
