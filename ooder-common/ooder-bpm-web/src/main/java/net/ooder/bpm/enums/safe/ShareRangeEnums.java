package net.ooder.bpm.enums.safe;


import net.ooder.annotation.Enumstype;

public enum ShareRangeEnums implements Enumstype {
    Lanopen("Lanopen", "仅内部开放"),
    Internet("Internet", "个人（微信、微博）"),
    Official("Internet", "公众号"),
    Lanperson("Lanperson", "内部指定人员可见");

    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    ShareRangeEnums(String type, String name) {
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
