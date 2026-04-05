package net.ooder.bpm.enums.safe;


import net.ooder.annotation.Enumstype;

public enum SecretTypeEnum implements Enumstype {
    Personkey("Personkey", "UKEY"),
    Publickey("Publickey", "公钥");
    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    SecretTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static SecretTypeEnum fromType(String typeName) {
        for (SecretTypeEnum type : SecretTypeEnum.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

}
