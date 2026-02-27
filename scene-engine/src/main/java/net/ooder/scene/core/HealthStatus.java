package net.ooder.scene.core;

/**
 * 健康状态枚举
 * 从 ooder-infra-driver 迁移过来，统一使用 scene-engine 的健康状态
 */
public enum HealthStatus {
    HEALTHY("健康"),
    UNHEALTHY("不健康"),
    DEGRADED("降级运行"),
    UNKNOWN("未知");

    private final String description;

    HealthStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isHealthy() {
        return this == HEALTHY;
    }
}
