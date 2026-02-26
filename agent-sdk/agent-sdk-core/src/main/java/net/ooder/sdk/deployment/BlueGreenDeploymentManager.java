package net.ooder.sdk.deployment;

import java.nio.file.Path;

/**
 * 蓝绿部署管理器接口
 */
public interface BlueGreenDeploymentManager {

    /**
     * 部署新版本
     */
    DeploymentResult deploy(String skillId, Path newVersionJar, DeploymentConfig config);

    /**
     * 回滚到上一个版本
     */
    DeploymentResult rollback(String skillId);

    /**
     * 切换流量
     */
    boolean switchTraffic(String skillId, Environment targetEnvironment);

    /**
     * 获取当前活跃环境
     */
    Environment getActiveEnvironment(String skillId);

    /**
     * 获取指定 Skill 的部署状态
     */
    DeploymentStatus getDeploymentStatus(String skillId);

    /**
     * 健康检查
     */
    boolean healthCheck(String skillId, Environment environment);

    /**
     * 清理旧版本
     */
    boolean cleanup(String skillId);

    /**
     * 部署状态
     */
    enum DeploymentStatus {
        IDLE,
        DEPLOYING,
        HEALTH_CHECKING,
        READY,
        ACTIVE,
        FAILED
    }
}
