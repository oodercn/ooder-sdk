package net.ooder.bpm.enums.form;


import net.ooder.annotation.Enumstype;

public enum LockEnum implements Enumstype {

    Msg("Msg", "通知修改"),
    Lock("Lock", "锁定数据"),
    Person("Person", "人工合并"),
    Last("Last", "保留最后版本"),
    NO("NO", "禁止覆盖");


    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    LockEnum(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static LockEnum fromType(String typeName) {
        for (LockEnum type : LockEnum.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

}
