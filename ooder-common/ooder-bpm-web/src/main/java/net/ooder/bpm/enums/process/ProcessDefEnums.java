package net.ooder.bpm.enums.process;

import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Enumstype;
import net.ooder.bpm.enums.right.CommissionEnums;

public enum ProcessDefEnums implements AttributeName {

    AccessLevel("AccessLevel", "流程类型", ProcessDefAccess.class),

    PublicationStatus("PublicationStatus", "版本状态", ProcessDefVersionStatus.class),

    CommissionEnums("CommissionEnums", "流程权限", CommissionEnums.class);

    private String name;
    private Class<? extends Enumstype> clazz;
    private String displayName;

    ProcessDefEnums(String name, String displayName, Class<? extends Enumstype> clazz) {

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
