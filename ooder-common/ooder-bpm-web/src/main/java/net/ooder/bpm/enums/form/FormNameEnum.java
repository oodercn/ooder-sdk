package net.ooder.bpm.enums.form;


import net.ooder.annotation.Enumstype;

public enum FormNameEnum implements Enumstype {

    FORM("FORM", "自定义页面"),
    TABLE("TABLE", "数据库表");


    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    FormNameEnum(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static FormNameEnum fromType(String typeName) {
        for (FormNameEnum type : FormNameEnum.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

}
