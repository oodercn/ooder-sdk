package net.ooder.skill.capability.model;

public enum SkillForm {
    PROVIDER("PROVIDER", "提供者"),
    SCENE("SCENE", "场景"),
    DRIVER("DRIVER", "驱动"),
    INTEGRATION("INTEGRATION", "集成");

    private final String code;
    private final String name;

    SkillForm(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    public static SkillForm fromCode(String code) {
        if (code == null) return PROVIDER;
        for (SkillForm form : values()) {
            if (form.code.equals(code) || form.name().equals(code)) {
                return form;
            }
        }
        return PROVIDER;
    }
}
