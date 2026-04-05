package net.ooder.bpm.enums.safe;

import net.ooder.annotation.Enumstype;
import net.ooder.bpm.enums.process.FormRightEnums;

public enum SecretlevelEnums implements Enumstype {

    L1("1", "等保一级"),
    L2("2", "等保二级"),
    L3("3", "等保三级");

    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    SecretlevelEnums(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static SecretEnum fromType(String typeName) {
        for (SecretEnum type : SecretEnum.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

}
