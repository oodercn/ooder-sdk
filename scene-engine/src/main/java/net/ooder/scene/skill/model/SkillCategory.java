package net.ooder.scene.skill.model;

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
 * @author Ooder Team
 * @version 3.0
 * @since 3.0
 */
public enum SkillCategory {
    
    /**
     * 知识类技能
     * 
     * <p>类比：.doc/.pdf（文档文件）</p>
     * 
     * <p>特点：</p>
     * <ul>
     *   <li>处理结构化/非结构化知识</li>
     *   <li>支持检索、问答、摘要</li>
     *   <li>通常与RAG技术结合</li>
     * </ul>
     * 
     * <p>示例：</p>
     * <ul>
     *   <li>智能文档助手</li>
     *   <li>知识库问答</li>
     *   <li>文档摘要生成</li>
     * </ul>
     */
    KNOWLEDGE("knowledge", "知识类", "文档", 
        new String[]{".doc", ".pdf", ".md"}, 
        "处理知识的技能"),
    
    /**
     * AI模型类技能
     * 
     * <p>类比：.ai/.model（AI模型文件）</p>
     * 
     * <p>特点：</p>
     * <ul>
     *   <li>基于大语言模型或其他AI模型</li>
     *   <li>支持自然语言理解和生成</li>
     *   <li>可调用Function Calling</li>
     * </ul>
     * 
     * <p>示例：</p>
     * <ul>
     *   <li>智能对话助手</li>
     *   <li>文本生成器</li>
     *   <li>代码助手</li>
     * </ul>
     */
    LLM("llm", "AI模型类", "AI模型", 
        new String[]{".ai", ".model", ".llm"}, 
        "基于AI模型的技能"),
    
    /**
     * 工具类技能
     * 
     * <p>类比：.exe/.sh（可执行文件）</p>
     * 
     * <p>特点：</p>
     * <ul>
     *   <li>执行特定功能或计算</li>
     *   <li>输入输出明确</li>
     *   <li>通常无状态</li>
     * </ul>
     * 
     * <p>示例：</p>
     * <ul>
     *   <li>计算器</li>
     *   <li>格式转换器</li>
     *   <li>数据处理器</li>
     * </ul>
     */
    TOOL("tool", "工具类", "可执行", 
        new String[]{".exe", ".sh", ".tool"}, 
        "执行特定功能的工具"),
    
    /**
     * 流程类技能
     * 
     * <p>类比：.flow/.pipeline（流程定义文件）</p>
     * 
     * <p>特点：</p>
     * <ul>
     *   <li>定义业务流程或工作流</li>
     *   <li>支持条件分支和状态流转</li>
     *   <li>可编排多个子步骤</li>
     * </ul>
     * 
     * <p>示例：</p>
     * <ul>
     *   <li>审批流程</li>
     *   <li>数据处理管道</li>
     *   <li>自动化工作流</li>
     * </ul>
     */
    WORKFLOW("workflow", "流程类", "流程", 
        new String[]{".flow", ".pipeline", ".bpmn"}, 
        "定义业务流程的技能"),
    
    /**
     * 数据类技能
     * 
     * <p>类比：.db/.json/.csv（数据文件）</p>
     * 
     * <p>特点：</p>
     * <ul>
     *   <li>处理数据存储和查询</li>
     *   <li>支持CRUD操作</li>
     *   <li>可与数据库或API交互</li>
     * </ul>
     * 
     * <p>示例：</p>
     * <ul>
     *   <li>数据库查询</li>
     *   <li>数据分析</li>
     *   <li>报表生成</li>
     * </ul>
     */
    DATA("data", "数据类", "数据", 
        new String[]{".db", ".json", ".csv"}, 
        "处理数据的技能"),
    
    /**
     * 服务类技能
     * 
     * <p>类比：.service/.api（服务定义文件）</p>
     * 
     * <p>特点：</p>
     * <ul>
     *   <li>封装外部服务或API</li>
     *   <li>支持服务发现和调用</li>
     *   <li>可与其他技能组合</li>
     * </ul>
     * 
     * <p>示例：</p>
     * <ul>
     *   <li>天气查询服务</li>
     *   <li>邮件发送服务</li>
     *   <li>第三方API封装</li>
     * </ul>
     */
    SERVICE("service", "服务类", "服务", 
        new String[]{".service", ".api", ".wsdl"}, 
        "封装外部服务的技能"),
    
    /**
     * 界面类技能
     * 
     * <p>类比：.ui/.html/.vue（界面文件）</p>
     * 
     * <p>特点：</p>
     * <ul>
     *   <li>提供用户界面交互</li>
     *   <li>支持表单、图表、仪表盘</li>
     *   <li>可嵌入其他场景</li>
     * </ul>
     * 
     * <p>示例：</p>
     * <ul>
     *   <li>数据可视化</li>
     *   <li>表单收集</li>
     *   <li>仪表盘</li>
     * </ul>
     */
    UI("ui", "界面类", "界面", 
        new String[]{".ui", ".html", ".vue"}, 
        "提供界面交互的技能"),
    
    /**
     * 其他/未知类型
     * 
     * <p>类比：.*（无扩展名文件）</p>
     */
    OTHER("other", "其他", "未知", 
        new String[]{".*"}, 
        "未分类的技能");
    
    private final String code;
    private final String name;
    private final String fileTypeAnalog;
    private final String[] fileExtensions;
    private final String description;
    
    SkillCategory(String code, String name, String fileTypeAnalog, 
            String[] fileExtensions, String description) {
        this.code = code;
        this.name = name;
        this.fileTypeAnalog = fileTypeAnalog;
        this.fileExtensions = fileExtensions;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public String getFileTypeAnalog() {
        return fileTypeAnalog;
    }
    
    public String[] getFileExtensions() {
        return fileExtensions;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 是否为知识类
     */
    public boolean isKnowledge() {
        return this == KNOWLEDGE;
    }
    
    /**
     * 是否为AI类
     */
    public boolean isLlm() {
        return this == LLM;
    }
    
    /**
     * 是否为工具类
     */
    public boolean isTool() {
        return this == TOOL;
    }
    
    /**
     * 是否需要模型支持（LLM或KNOWLEDGE）
     */
    public boolean requiresModel() {
        return this == LLM || this == KNOWLEDGE;
    }
    
    /**
     * 是否可执行（TOOL或WORKFLOW）
     */
    public boolean isExecutable() {
        return this == TOOL || this == WORKFLOW;
    }
    
    /**
     * 根据代码获取分类
     */
    public static SkillCategory fromCode(String code) {
        if (code == null) {
            return OTHER;
        }
        for (SkillCategory category : values()) {
            if (category.code.equalsIgnoreCase(code)) {
                return category;
            }
        }
        return OTHER;
    }
}
