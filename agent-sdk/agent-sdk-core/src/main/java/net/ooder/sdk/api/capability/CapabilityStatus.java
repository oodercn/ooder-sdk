package net.ooder.sdk.api.capability;

/**
 * 能力状态枚举
 * 定义能力在生命周期中的各种状态
 *
 * @author ooder
 * @since 2.3
 */
public enum CapabilityStatus {
    /** 已禁用 */
    DISABLED,
    /** 已启用 */
    ENABLED,
    /** 健康状态 */
    HEALTHY,
    /** 不健康状态 */
    UNHEALTHY,
    /** 失败状态 */
    FAILED,
    /** 更新中 */
    UPDATING,
    /** 维护中 */
    MAINTENANCE,
    /** 已注册 */
    REGISTERED,
    /** 活跃状态 */
    ACTIVE,
    /** 非活跃状态 */
    INACTIVE,
    /** 错误状态 */
    ERROR
}
