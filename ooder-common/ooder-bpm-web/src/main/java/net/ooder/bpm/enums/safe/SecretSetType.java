package net.ooder.bpm.enums.safe;


import net.ooder.annotation.Enumstype;

public enum SecretSetType implements Enumstype {
    no("no", "无"),
    secret("secret", "秘密"),
    topSecret("topSecret", "绝密"),
    person("person", "办理人指定");

    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    SecretSetType(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static SecretSetType fromType(String typeName) {
        for (SecretSetType type : SecretSetType.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

}
