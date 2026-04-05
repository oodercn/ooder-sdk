package net.ooder.bpm.enums.form;


import net.ooder.annotation.Enumstype;

public enum HistoryRightEnum implements Enumstype {

    All("All", "所有人参与者可见"),
    StartPerson("StartPerson", "流程启动者可见"),
    PerforPerson("StartPerson", "曾经办理者可见"),
    ProcessAdmin("ProcessAdmin", "流程管理员可见"),
    Admin("Admin", "只有系統管理员可见"),
    NO("NO", "全程不可修改");


    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    HistoryRightEnum(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static HistoryRightEnum fromType(String typeName) {
        for (HistoryRightEnum type : HistoryRightEnum.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

}
