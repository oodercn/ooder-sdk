package net.ooder.scene.skill.security;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * 安全资源访问器
 * 提供带安全校验和审计日志的资源访问能力
 *
 * <p>引擎层封装，不直接暴露 Web API</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface SecureResourceAccessor {
    
    /**
     * 执行带安全校验的操作
     * @param context 安全上下文
     * @param operation 操作逻辑
     * @param <T> 返回类型
     * @return 操作结果
     */
    <T> CompletableFuture<T> execute(SecureOperationContext context, 
                                     Function<SecureOperationContext, T> operation);
    
    /**
     * 检查用户权限
     * @param userId 用户ID
     * @param resourceId 资源ID
     * @param action 操作类型
     * @return 检查结果
     */
    SecurityCheckResult checkPermission(String userId, String resourceId, String action);
    
    /**
     * 检查场景访问权限
     * @param userId 用户ID
     * @param sceneId 场景ID
     * @param action 操作类型
     * @return 检查结果
     */
    SecurityCheckResult checkSceneAccess(String userId, String sceneId, String action);
    
    /**
     * 检查能力访问权限
     * @param userId 用户ID
     * @param capabilityId 能力ID
     * @param action 操作类型
     * @return 检查结果
     */
    SecurityCheckResult checkCapabilityAccess(String userId, String capabilityId, String action);
}
