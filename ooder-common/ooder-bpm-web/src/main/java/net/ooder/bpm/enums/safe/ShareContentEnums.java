package net.ooder.bpm.enums.safe;


import net.ooder.annotation.Enumstype;

public enum ShareContentEnums implements Enumstype {
    OnlyDocument("OnlyDocument", "仅正文"),
    Process("Process", "流转历程ALL"),
    ProcessNoPerson("ProcessNoPerson", "隐藏审批人"),
    All("All", "全部");

    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    ShareContentEnums(String type, String name) {
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
