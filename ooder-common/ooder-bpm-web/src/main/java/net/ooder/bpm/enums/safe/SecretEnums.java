package net.ooder.bpm.enums.safe;

import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Enumstype;
import net.ooder.bpm.enums.process.FormRightEnums;

import net.ooder.common.CommonYesNoEnum;

public enum SecretEnums implements AttributeName {

    CanSecret("CanSecret", "是否启用加密", CommonYesNoEnum.class),

    SecretType("SecretType", "加密方式", SecretSetType.class),

    //SecretLevelSet("Secret", "密級设定方式", Secre.class),

    CanSecretLevel("CanSecretLevel", "是否启动等保检查", CommonYesNoEnum.class),

    SecretLevel("SecretLevel", "启用等级", SecretlevelEnums.class);


    private String name;
    private Class<? extends Enumstype> clazz;
    private String displayName;

    SecretEnums(String name, String displayName, Class<? extends Enumstype> clazz) {
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
