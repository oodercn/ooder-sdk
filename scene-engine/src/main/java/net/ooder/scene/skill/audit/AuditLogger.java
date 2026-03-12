package net.ooder.scene.skill.audit;

import net.ooder.scene.audit.AuditStats;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 审计日志接口
 * 提供操作审计日志记录能力
 *
 * <p>引擎层封装，不直接暴露 Web API</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface AuditLogger {
    
    /**
     * 记录操作日志
     * @param userId 用户ID
     * @param operation 操作类型
     * @param resourceId 资源ID
     * @param result 操作结果
     * @return 日志记录结果
     */
    CompletableFuture<Boolean> logOperation(String userId, String operation, 
                                            String resourceId, boolean result);
    
    /**
     * 记录详细操作日志
     * @param entry 审计日志条目
     * @return 日志记录结果
     */
    CompletableFuture<Boolean> log(AuditEntry entry);
    
    /**
     * 查询审计日志
     * @param userId 用户ID（可选）
     * @param operation 操作类型（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制条数
     * @return 日志列表
     */
    CompletableFuture<AuditLogQueryResult> queryLogs(String userId, String operation,
                                                      long startTime, long endTime, int limit);
    
    /**
     * 获取用户操作统计
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    CompletableFuture<AuditStats> getUserStats(String userId, long startTime, long endTime);
}
