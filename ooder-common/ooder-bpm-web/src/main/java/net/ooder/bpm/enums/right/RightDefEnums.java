package net.ooder.bpm.enums.right;

import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Enumstype;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.esd.annotation.RouteToType;

public enum RightDefEnums implements AttributeName {

    CANTAKEBACK("CANTAKEBACK", "是否允许强制收回", CommonYesNoEnum.class),

    CANRESEND("CANRESEND", "是否允许补发", CommonYesNoEnum.class),

    BUTTONS("BUTTONS", "按钮属性", RouteToType.class),

    CANSURROGATE("CANSURROGATE", "是否允许代办", CommonYesNoEnum.class),

    CANINSTEADSIGN("CANINSTEADSIGN", "是否允许代签", CommonYesNoEnum.class),

    PERFORMSEQUENCE("PERFORMSEQUENCE", "办理顺序", net.ooder.bpm.enums.activitydef.ActivityDefPerformSequence.class),

    PERFORMTYPE("PERFORMTYPE", "办理方式", net.ooder.bpm.enums.activitydef.ActivityDefPerformtype.class),

    RIGHTGROUP("RIGHTGROUP", "权限组", RightGroupEnums.class);

    private String name;
    private Class<? extends Enumstype> clazz;
    private String displayName;

    RightDefEnums(String name, String displayName, Class<? extends Enumstype> clazz) {

        this.name = name;
        this.displayName = displayName;
        this.clazz = clazz;

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
