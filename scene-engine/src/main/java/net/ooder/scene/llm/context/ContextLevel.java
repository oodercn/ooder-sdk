package net.ooder.scene.llm.context;

/**
 * 上下文层级枚举
 *
 * @author ooder Team
 * @since 2.3.1
 */
public enum ContextLevel {

    GLOBAL(0, "全局上下文", "系统基础信息、菜单功能映射、全局工具定义"),
    SKILL(1, "技能上下文", "技能 SystemPrompt、工具定义、知识库绑定"),
    PAGE(2, "页面上下文", "当前页面信息、可用 API、状态数据"),
    SESSION(3, "会话上下文", "多轮对话历史、用户意图追踪");

    private final int level;
    private final String name;
    private final String description;

    ContextLevel(int level, String name, String description) {
        this.level = level;
        this.name = name;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getCode() {
        return this.name().toLowerCase();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isHigherThan(ContextLevel other) {
        return this.level > other.level;
    }

    public boolean isLowerThan(ContextLevel other) {
        return this.level < other.level;
    }
}
