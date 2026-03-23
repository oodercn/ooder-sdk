package net.ooder.scene.knowledge;

public enum BindingScope {

    SCENE_GROUP("scene_group", "场景组级别"),

    SCENE("scene", "场景级别"),

    CAPABILITY("capability", "能力级别");

    private final String code;
    private final String description;

    BindingScope(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static BindingScope fromCode(String code) {
        for (BindingScope scope : values()) {
            if (scope.code.equalsIgnoreCase(code)) {
                return scope;
            }
        }
        return SCENE_GROUP;
    }
}
