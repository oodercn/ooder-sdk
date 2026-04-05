package net.ooder.bpm.enums.safe;


import net.ooder.annotation.Enumstype;

public enum SecretEnum implements Enumstype {
    no("no", "无"),
    secret("secret", "秘密"),
    topSecret("topSecret", "绝密");

    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    SecretEnum(String type, String name) {
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
