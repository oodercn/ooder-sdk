package net.ooder.sdk.api.capability;

/**
 * 能力类型枚举
 * 定义系统中支持的各种能力类型
 *
 * @author ooder
 * @since 2.3
 */
public enum CapabilityType {
    /** 驱动类型 - 提供驱动源头的特殊能力 */
    DRIVER,
    /** 服务类型 */
    SERVICE,
    /** 管理类型 */
    MANAGEMENT,
    /** AI类型 */
    AI,
    /** 存储类型 */
    STORAGE,
    /** 通信类型 */
    COMMUNICATION,
    /** 安全类型 */
    SECURITY,
    /** 监控类型 */
    MONITORING,
    /** 技能类型 */
    SKILL,
    /** 场景类型 - 自驱型SuperAgent能力 */
    SCENE,
    /** 场景组类型 */
    SCENE_GROUP,
    /** 能力链类型 */
    CAPABILITY_CHAIN,
    /** 原子能力 - 单一功能，不可分解 */
    ATOMIC,
    /** 组合能力 - 组合多个原子能力 */
    COMPOSITE,
    /** 协作能力 - 跨场景协作能力 */
    COLLABORATIVE,
    /** 自定义类型 */
    CUSTOM
}
