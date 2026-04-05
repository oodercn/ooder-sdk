package net.ooder.bpm.enums.safe;

import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Enumstype;
import net.ooder.bpm.enums.process.FormRightEnums;
import net.ooder.common.CommonYesNoEnum;

public enum ShareEnums implements AttributeName {

    CanPhone("CanPhone", "是否允许移动端访问", CommonYesNoEnum.class),

    CanShare("anShare", "是否允许分享", CommonYesNoEnum.class),

    ShareRange("ShareRange", "分享范围", ShareRangeEnums.class),

    CanMark("CanMark", "是否启用安全水印", CommonYesNoEnum.class),

    ShareContent("ShareContent", "可分享内容", ShareContentEnums.class);


    private String name;
    private Class<? extends Enumstype> clazz;
    private String displayName;

    ShareEnums(String name, String displayName, Class<? extends Enumstype> clazz) {
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
