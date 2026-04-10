package net.ooder.skill.common.sdk.migration;

/**
 * 杩佺Щ鏉′欢鏋氫妇
 *
 * @author SDK Team
 * @version 2.3.0
 * @since 2026-02-24
 */
public enum MigrationCondition {

    /**
     * 鏁版嵁澶у皬瓒呰繃闃堝€?     */
    DATA_SIZE_THRESHOLD,

    /**
     * 鏈嶅姟涓嶅彲鐢?     */
    SERVICE_UNAVAILABLE,

    /**
     * 鎬ц兘涓嬮檷
     */
    PERFORMANCE_DEGRADATION,

    /**
     * 鎵嬪姩瑙﹀彂
     */
    MANUAL_TRIGGER,

    /**
     * 鐗堟湰杩囨湡
     */
    VERSION_DEPRECATED,

    /**
     * 瀹夊叏婕忔礊
     */
    SECURITY_VULNERABILITY
}
