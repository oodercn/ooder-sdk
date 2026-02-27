package net.ooder.scene.provider;

import net.ooder.scene.core.HeartbeatResult;
import net.ooder.scene.core.HeartbeatStatus;

import java.util.concurrent.CompletableFuture;

/**
 * 心跳提供者接口
 * 管理场景组心跳
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface HeartbeatProvider extends BaseProvider {

    /**
     * 启动场景组心跳
     * @param userId 用户ID
     * @param groupId 场景组ID
     * @return 心跳结果
     */
    CompletableFuture<HeartbeatResult> startHeartbeat(String userId, String groupId);

    /**
     * 停止场景组心跳
     * @param userId 用户ID
     * @param groupId 场景组ID
     */
    void stopHeartbeat(String userId, String groupId);

    /**
     * 获取场景组心跳状态
     * @param userId 用户ID
     * @param groupId 场景组ID
     * @return 心跳状态
     */
    HeartbeatStatus getHeartbeatStatus(String userId, String groupId);
}
