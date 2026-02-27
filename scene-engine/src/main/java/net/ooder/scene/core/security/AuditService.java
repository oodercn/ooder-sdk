package net.ooder.scene.core.security;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 审计服务接口
 *
 * <p>所有操作必须经过审计服务记录</p>
 */
public interface AuditService {

    /**
     * 记录操作日志
     *
     * @param context 操作上下文
     * @param operation 操作类型
     * @param resource 资源类型
     * @param resourceId 资源ID
     * @param result 操作结果
     * @param details 详细信息
     */
    void logOperation(
        OperationContext context,
        String operation,
        String resource,
        String resourceId,
        OperationResult result,
        Map<String, Object> details
    );

    /**
     * 查询审计日志
     */
    CompletableFuture<List<AuditLog>> queryLogs(AuditLogQuery query);

    /**
     * 导出审计日志
     */
    CompletableFuture<AuditExportResult> exportLogs(AuditLogQuery query);

    /**
     * 获取用户操作统计
     */
    CompletableFuture<UserOperationStats> getUserStats(String userId, long startTime, long endTime);

    /**
     * 获取资源访问统计
     */
    CompletableFuture<ResourceAccessStats> getResourceStats(String resourceType, String resourceId);
}
