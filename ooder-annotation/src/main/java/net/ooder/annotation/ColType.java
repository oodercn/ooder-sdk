package net.ooder.annotation;

public enum ColType implements Enumstype {
    VARCHAR("字符串"), DATETIME("日期"), TEXT("大文本"), INTEGER("整数"), FLOAT("数字"), BOOLEAN("BOOLEAN"), VARCHAR2("字符串"), BLOB("二进制"), CHAR("CHAR"), TINYINT("TINYINT"),
    SMALLINT("SMALLINT"), ENUM("枚举"), MEDIUMINT("MEDIUMINT"), BIT("BIT"), BIGINT("BIGINT"), DOUBLE("DOUBLE"), DECIMAL("可变长度"), SET("集合"), JSON("JSON"), Geometry("几何类型"),
    ID("ID"), INT("整形"), DATE("DATE"), TIME("TIME"), TIMESTAMP("TIMESTAMP"), YEAR("YEAR");

    private final String name;


    public static ColType fromType(String typeName) {
        for (ColType type : ColType.values()) {
            if (type.name().equals(typeName.toUpperCase())) {
                return type;
            }
        }
        return ColType.VARCHAR;
    }

    public static ColType fromJPAType(String typeName) {
        for (ColType type : ColType.values()) {
            if (type.name().startsWith(typeName.toUpperCase())) {
                return type;
            }
        }
        return ColType.VARCHAR;
    }


    ColType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public String getType() {
        return name();
    }

    @Override
    public String getName() {
        return name;
    }


}
