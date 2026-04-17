package net.ooder.skill.capability.model;

public enum AccessLevel {
    PUBLIC("PUBLIC", "公开"),
    SCENE("SCENE", "场景级"),
    PRIVATE("PRIVATE", "私有");

    private final String code;
    private final String name;

    AccessLevel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    public static AccessLevel fromCode(String code) {
        if (code == null) return SCENE;
        for (AccessLevel level : values()) {
            if (level.code.equals(code) || level.name().equals(code)) {
                return level;
            }
        }
        return SCENE;
    }
}
