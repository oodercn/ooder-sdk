package net.ooder.skill.capability.model;

public enum SceneType {
    CHAT("CHAT", "对话场景"),
    WORKFLOW("WORKFLOW", "工作流场景"),
    AGENT("AGENT", "代理场景"),
    RAG("RAG", "检索增强场景"),
    ANALYSIS("ANALYSIS", "分析场景"),
    AUTOMATION("AUTOMATION", "自动化场景");

    private final String code;
    private final String name;

    SceneType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    public static SceneType fromCode(String code) {
        if (code == null) return null;
        for (SceneType type : values()) {
            if (type.code.equals(code) || type.name().equals(code)) {
                return type;
            }
        }
        return CHAT;
    }
}
