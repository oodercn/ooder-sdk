package net.ooder.annotation;

/**
 * AI Agent领域分类枚举
 * 用于{@link Agent}注解指定Agent所属业务领域
 */
public enum AgentDomain {
    /** 自然语言处理领域 */
    NLP,
    /** 计算机视觉领域 */
    COMPUTER_VISION,
    /** 知识图谱领域 */
    KNOWLEDGE_GRAPH,
    /** 推荐系统领域 */
    RECOMMENDATION,
    /** 自动化流程领域 */
    AUTOMATION,
    /** 多模态智能领域 */
    MULTIMODAL,
    /** 通用AI能力领域 */
    GENERAL
}