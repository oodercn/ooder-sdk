package net.ooder.scene.core.driver;

/**
 * 健康状态
 * 继承自 agent-sdk 的核心枚举，保持兼容性
 * @deprecated 请使用 net.ooder.sdk.core.driver.HealthStatus
 */
@Deprecated
public enum HealthStatus {
    UP,
    DOWN,
    DEGRADED,
    UNKNOWN;
    
    /**
     * 转换为 agent-sdk 的 HealthStatus
     * @return agent-sdk 的 HealthStatus
     */
    public net.ooder.sdk.core.driver.HealthStatus toSdkHealthStatus() {
        switch (this) {
            case UP:
                return net.ooder.sdk.core.driver.HealthStatus.UP;
            case DOWN:
                return net.ooder.sdk.core.driver.HealthStatus.DOWN;
            case DEGRADED:
                return net.ooder.sdk.core.driver.HealthStatus.DEGRADED;
            case UNKNOWN:
            default:
                return net.ooder.sdk.core.driver.HealthStatus.UNKNOWN;
        }
    }
    
    /**
     * 从 agent-sdk 的 HealthStatus 转换
     * @param status agent-sdk 的 HealthStatus
     * @return scene-engine 的 HealthStatus
     */
    public static HealthStatus fromSdkHealthStatus(net.ooder.sdk.core.driver.HealthStatus status) {
        switch (status) {
            case UP:
                return UP;
            case DOWN:
                return DOWN;
            case DEGRADED:
                return DEGRADED;
            case UNKNOWN:
            default:
                return UNKNOWN;
        }
    }
}
