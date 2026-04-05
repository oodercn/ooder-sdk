package net.ooder.bpm.enums.form;


import net.ooder.annotation.Enumstype;

public enum MarkEnum implements Enumstype {

    ProcessInst("ProcessInst", "全流程唯一"),
    ActivityInst("ActivityInst", "步骤唯一"),
    Person("Person", "办理人唯一"),
    ActivityInstPerson("ActivityInstPerson", "全过程记录");

    private String type;

    private String name;

    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }

    MarkEnum(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static MarkEnum fromType(String typeName) {
        for (MarkEnum type : MarkEnum.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

}
