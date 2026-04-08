package net.ooder.skill.hotplug;

/**
 * Skill 形态枚举
 * 定义 Skill 的三种基本形态
 */
public enum SkillForm {
    /**
     * 场景应用：面向业务场景的完整应用
     * 例如：招聘场景、审批流程、房产管理、会议系统等
     */
    SCENE("场景应用", "面向业务场景的完整应用"),

    /**
     * 驱动适配：连接外部系统的适配器
     * 例如：数据库驱动、API 适配器、消息队列连接器等
     */
    DRIVER("驱动适配", "连接外部系统的适配器"),

    /**
     * 能力提供：提供基础能力的组件
     * 例如：字典服务、用户服务、通知服务、文件存储等
     */
    PROVIDER("能力提供", "提供基础能力的组件");

    private final String displayName;
    private final String description;

    SkillForm(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
