package net.ooder.bpm.enums.safe;


import net.ooder.annotation.Enumstype;

public enum SaftContentEnums implements Enumstype {
    OnlyDocument("OnlyDocument", "仅正文"),
    AttachMent("AttachMent", "附件与正文"),
    All("All", "全表单");

    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    SaftContentEnums(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static SecretEnum fromType(String typeName) {
        for (SecretEnum type : SecretEnum.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

}
