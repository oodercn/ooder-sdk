package net.ooder.bpm.enums.form;


import net.ooder.annotation.Enumstype;

public enum EditorEnum implements Enumstype {

    CurrPerson("CurrPerson", "当前办理人可修改"),
    StartPerson("StartPerson", "流程发起人可以修改"),
    ProcessAdmin("ProcessAdmin", "流程管理员可修改"),
    Admin("Admin", "系統管理员可修改"),
    NO("NO", "全程不可修改");


    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    EditorEnum(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static EditorEnum fromType(String typeName) {
        for (EditorEnum type : EditorEnum.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

}
