package net.ooder.scene.core.security;

import net.ooder.scene.core.AuditLogFilter;
import net.ooder.scene.core.PageRequest;
import net.ooder.scene.core.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 审计服务适配器
 *
 * <p>将 net.ooder.scene.audit.AuditService 适配为 
 * net.ooder.scene.core.security.AuditService 接口</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
@Service
public class AuditServiceAdapter implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditServiceAdapter.class);

    private final net.ooder.scene.audit.AuditService delegate;

    public AuditServiceAdapter(net.ooder.scene.audit.AuditService auditService) {
        this.delegate = auditService;
        log.info("[AuditServiceAdapter] Initialized with delegate: {}", 
            auditService.getClass().getSimpleName());
    }

    @Override
    public void logOperation(
        OperationContext context,
        String operation,
        String resource,
        String resourceId,
        OperationResult result,
        Map<String, Object> details
    ) {
        net.ooder.scene.core.AuditLog auditLog = new net.ooder.scene.core.AuditLog();
        auditLog.setLogId(java.util.UUID.randomUUID().toString());
        auditLog.setUserId(context.getUserId());
        auditLog.setUserName(context.getUserName());
        auditLog.setEventType(resource);
        auditLog.setAction(operation);
        auditLog.setTarget(resourceId);
        auditLog.setResult(result.name());
        auditLog.setTimestamp(System.currentTimeMillis());
        auditLog.setSource(context.getSceneId());
        auditLog.setIpAddress(context.getIpAddress());
        
        if (details != null) {
            auditLog.setDetails(details.toString());
        }
        
        delegate.log(auditLog);
    }

    @Override
    public CompletableFuture<List<AuditLog>> queryLogs(AuditLogQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPageNum(query.getPageNum());
            pageRequest.setPageSize(query.getPageSize());
            
            AuditLogFilter filter = convertFilter(query);
            
            PageResult<net.ooder.scene.core.AuditLog> result = delegate.query(pageRequest, filter);
            
            return result.getItems().stream()
                .map(this::convertToSecurityAuditLog)
                .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<AuditExportResult> exportLogs(AuditLogQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            AuditLogFilter filter = convertFilter(query);
            byte[] data = delegate.export(filter, "json");
            
            AuditExportResult exportResult = new AuditExportResult();
            exportResult.setFormat("json");
            exportResult.setRecordCount(data.length);
            exportResult.setStatus("COMPLETED");
            exportResult.setTimestamp(System.currentTimeMillis());
            
            return exportResult;
        });
    }

    @Override
    public CompletableFuture<UserOperationStats> getUserStats(String userId, long startTime, long endTime) {
        return CompletableFuture.supplyAsync(() -> {
            net.ooder.scene.audit.AuditStats stats = delegate.getStats(startTime, endTime);
            
            UserOperationStats userStats = new UserOperationStats();
            userStats.setUserId(userId);
            userStats.setTotalOperations(stats.getTotalCount());
            userStats.setSuccessCount(stats.getSuccessCount());
            userStats.setFailureCount(stats.getFailureCount());
            
            return userStats;
        });
    }

    @Override
    public CompletableFuture<ResourceAccessStats> getResourceStats(String resourceType, String resourceId) {
        return CompletableFuture.supplyAsync(() -> {
            ResourceAccessStats stats = new ResourceAccessStats();
            stats.setResourceType(resourceType);
            stats.setResourceId(resourceId);
            return stats;
        });
    }

    private AuditLogFilter convertFilter(AuditLogQuery query) {
        AuditLogFilter filter = new AuditLogFilter();
        if (query.getUserId() != null) {
            filter.setUserId(query.getUserId());
        }
        if (query.getOperation() != null) {
            filter.setEventType(query.getOperation());
        }
        if (query.getStartTime() > 0) {
            filter.setStartTime(query.getStartTime());
        }
        if (query.getEndTime() > 0) {
            filter.setEndTime(query.getEndTime());
        }
        return filter;
    }

    private AuditLog convertToSecurityAuditLog(net.ooder.scene.core.AuditLog log) {
        AuditLog securityLog = new AuditLog();
        securityLog.setLogId(log.getLogId());
        securityLog.setUserId(log.getUserId());
        securityLog.setUserName(log.getUserName());
        securityLog.setOperation(log.getAction());
        securityLog.setResource(log.getEventType());
        securityLog.setResourceId(log.getTarget());
        securityLog.setTimestamp(log.getTimestamp());
        return securityLog;
    }
}
