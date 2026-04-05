package net.ooder.bpm.enums.process;

import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Enumstype;
import net.ooder.bpm.enums.form.LockEnum;
import net.ooder.bpm.enums.form.MarkEnum;
import net.ooder.common.CommonYesNoEnum;

public enum FormRightEnums implements AttributeName {

    Mark("Mark", "数据留痕方式", MarkEnum.class),

    AutoSave("AutoSave", "数据自动保存", CommonYesNoEnum.class),

    NoSql("NoSql", "NoSql同步存储", CommonYesNoEnum.class),

    Lock("Lock", "数据锁定", LockEnum.class);


    private String name;
    private Class<? extends Enumstype> clazz;
    private String displayName;

    FormRightEnums(String name, String displayName, Class<? extends Enumstype> clazz) {
        this.name = name;
        this.displayName = displayName;
        this.clazz = clazz;

    }


    public static FormRightEnums fromType(String typeName) {
        for (FormRightEnums type : FormRightEnums.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends Enumstype> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends Enumstype> clazz) {
        this.clazz = clazz;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getType() {
        return name;
    }

}
