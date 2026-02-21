package net.ooder.annotation;

import java.lang.annotation.*;


/**
 * AIGC安全控制注解
 * 定义AI服务安全策略
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AIGCSecurity {
    /**
     * 敏感级别
     */
    SensitiveLevel sensitiveLevel() default SensitiveLevel.MEDIUM;

    /**
     * 隐私保护级别(0-5级)
     */
    int privacyLevel() default 2;

    /**
     * 是否启用数据脱敏
     */
    boolean dataMasking() default false;

    /**
     * 是否启用审计日志
     */
    boolean auditLog() default false;

    /**
     * 是否启用内容审核
     */
    boolean contentAudit() default true;

    /**
     * 审核策略
     */
    AuditPolicy auditPolicy() default AuditPolicy.STRICT;

    /**
     * 是否启用敏感信息过滤
     */
    boolean sensitiveFilter() default true;

    /**
     * 敏感信息过滤规则组
     */
    String[] filterGroups() default {"default"};

    /**
     * 是否启用访问控制
     */
    boolean accessControl() default true;

    /**
     * 允许访问的角色
     */
    String[] allowedRoles() default {};

    /**
     * 审核策略枚举
     */
    // 删除内部枚举定义

    /**
     * 数据保留期限(天)
     */
    int dataRetentionDays() default 30;

    /**
     * 合规认证要求
     */
    ComplianceLevel complianceLevel() default ComplianceLevel.BASIC;

    /**
     * 动态脱敏规则
     */
    String[] dynamicMaskingRules() default {};
}