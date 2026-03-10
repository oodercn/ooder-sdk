package net.ooder.skills.api;

/**
 * 技能分类
 * 
 * <p>定义技能的能力类型，类比文件系统中的文件扩展名/类型</p>
 * 
 * <h3>设计原则：</h3>
 * <ul>
 *   <li>分类描述技能的"能力类型"，而非"场景类型"</li>
 *   <li>分类与形态（SCENE/STANDALONE）是正交维度</li>
 *   <li>分类决定技能的实现技术和使用方式</li>
 * </ul>
 * 
 * <h3>与旧模型对比：</h3>
 * <ul>
 *   <li>旧：ABS/ASS/TBS/NOT_SCENE_SKILL（运行时计算的场景分类）</li>
 *   <li>新：knowledge/llm/tool/...（开发时声明的能力分类）</li>
 * </ul>
 *
 * @author Agent-SDK Team
 * @version 3.0
 * @since 3.0
 */
public enum SkillCategory {
    
    KNOWLEDGE("knowledge", "知识类", "文档", new String[]{".doc", ".pdf", ".md"}, "处理知识的技能"),
    LLM("llm", "AI模型类", "AI模型", new String[]{".ai", ".model", ".llm"}, "基于AI模型的技能"),
    TOOL("tool", "工具类", "可执行", new String[]{".exe", ".sh", ".tool"}, "执行特定功能的工具"),
    WORKFLOW("workflow", "流程类", "流程", new String[]{".flow", ".pipeline", ".bpmn"}, "定义业务流程的技能"),
    DATA("data", "数据类", "数据", new String[]{".db", ".json", ".csv"}, "处理数据的技能"),
    SERVICE("service", "服务类", "服务", new String[]{".service", ".api", ".wsdl"}, "封装外部服务的技能"),
    UI("ui", "界面类", "界面", new String[]{".ui", ".html", ".vue"}, "提供界面交互的技能"),
    OTHER("other", "其他", "未知", new String[]{".*"}, "未分类的技能");
    
    private final String code;
    private final String name;
    private final String fileTypeAnalog;
    private final String[] fileExtensions;
    private final String description;
    
    SkillCategory(String code, String name, String fileTypeAnalog, String[] fileExtensions, String description) {
        this.code = code;
        this.name = name;
        this.fileTypeAnalog = fileTypeAnalog;
        this.fileExtensions = fileExtensions;
        this.description = description;
    }
    
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getFileTypeAnalog() { return fileTypeAnalog; }
    public String[] getFileExtensions() { return fileExtensions; }
    public String getDescription() { return description; }
    
    public boolean isKnowledge() { return this == KNOWLEDGE; }
    public boolean isLlm() { return this == LLM; }
    public boolean isTool() { return this == TOOL; }
    public boolean requiresModel() { return this == LLM || this == KNOWLEDGE; }
    public boolean isExecutable() { return this == TOOL || this == WORKFLOW; }
    
    public static SkillCategory fromCode(String code) {
        if (code == null) return OTHER;
        for (SkillCategory category : values()) {
            if (category.code.equalsIgnoreCase(code)) return category;
        }
        return OTHER;
    }
}
