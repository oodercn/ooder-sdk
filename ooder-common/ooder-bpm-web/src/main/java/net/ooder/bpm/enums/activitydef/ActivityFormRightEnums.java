package net.ooder.bpm.enums.activitydef;


import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Enumstype;
import net.ooder.bpm.enums.form.LockEnum;

import net.ooder.bpm.enums.safe.SecretEnum;
import net.ooder.common.CommonYesNoEnum;

public enum ActivityFormRightEnums implements AttributeName {

    Secret("Secret", "密级", SecretEnum.class),

    AutoSave("AutoSave", "数据自动保存", CommonYesNoEnum.class),

    NoSql("NoSql", "NoSql同步存储", CommonYesNoEnum.class),

    Lock("Lock", "数据锁定", LockEnum.class);


    private String name;
    private Class<? extends Enumstype> clazz;
    private String displayName;

    ActivityFormRightEnums(String name, String displayName, Class<? extends Enumstype> clazz) {
        this.name = name;
        this.displayName = displayName;
        this.clazz = clazz;

    }


    public static ActivityFormRightEnums fromType(String typeName) {
        for (ActivityFormRightEnums type : ActivityFormRightEnums.values()) {
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
