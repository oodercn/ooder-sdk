package net.ooder.skill.capability.model;

public enum Visibility {
    PUBLIC("PUBLIC", "公开"),
    INTERNAL("INTERNAL", "内部"),
    DEVELOPER("DEVELOPER", "开发者"),
    PRIVATE("PRIVATE", "私有");

    private final String code;
    private final String name;

    Visibility(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    public static Visibility fromCode(String code) {
        if (code == null) return PUBLIC;
        for (Visibility v : values()) {
            if (v.code.equals(code) || v.name().equals(code)) {
                return v;
            }
        }
        return PUBLIC;
    }
}
