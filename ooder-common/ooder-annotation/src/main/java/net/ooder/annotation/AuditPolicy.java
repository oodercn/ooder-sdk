package net.ooder.annotation;

/**
 * 审计策略枚举，定义不同级别的审计要求
 */
public enum AuditPolicy {
    /** 不审计 */
    NONE,
    /** 标准审计 - 记录关键操作 */
    NORMAL,
    /** 增强审计 - 记录详细交互内容 */
    ENHANCED,
    /** 严格审计 - 全量记录并启用合规检查 */
    STRICT
}