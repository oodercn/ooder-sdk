package net.ooder.skill.capability.model;

public enum CapabilityOwnership {
    PLATFORM("PLATFORM", "平台能力", 1),
    INDEPENDENT("INDEPENDENT", "独立能力", 2),
    SCENE_INTERNAL("SCENE_INTERNAL", "场景内部能力", 3);

    private final String code;
    private final String name;
    private final int sort;

    CapabilityOwnership(String code, String name, int sort) {
        this.code = code;
        this.name = name;
        this.sort = sort;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return name; }
    public int getSort() { return sort; }

    public static CapabilityOwnership fromCode(String code) {
        if (code == null) return null;
        for (CapabilityOwnership ownership : values()) {
            if (ownership.code.equals(code) || ownership.name().equals(code)) {
                return ownership;
            }
        }
        return PLATFORM;
    }
}
