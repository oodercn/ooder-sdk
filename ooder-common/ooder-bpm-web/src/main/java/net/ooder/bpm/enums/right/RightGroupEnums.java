package net.ooder.bpm.enums.right;


import net.ooder.annotation.Enumstype;

public enum RightGroupEnums implements Enumstype {

    PERFORMER("PERFORMER", "当前办理人"),

    SPONSOR("SPONSOR", "发起人"),

    READER("READER", "读者组"),

    HISTORYPERFORMER("HISTORYPERFORMER", "曾经办理人"),

    HISSPONSOR("HISSPONSOR", "发送人"),

    HISTORYREADER("HISTORYREADER", "历史读者"),

    NORIGHT("NORIGHT", "无权限组"),

    NULL("", "访客组");

    private String type;

    private String name;

    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }

    RightGroupEnums(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static RightGroupEnums fromType(String typeName) {
        for (RightGroupEnums type : RightGroupEnums.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

}
