package net.ooder.scene.core.skill.security;

import net.ooder.scene.core.security.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 安全管理技能服务
 *
 * <p>提供用户、权限、审计等安全管理功能</p>
 */
public class SecuritySkillService extends SecureSkillService {

    @Override
    protected Object doExecute(SkillRequest request) {
        String operation = request.getOperation();
        switch (operation) {
            case "getUserPermissions":
                return getUserPermissions(request);
            case "grantPermission":
                return grantPermission(request);
            case "revokePermission":
                return revokePermission(request);
            case "queryAuditLogs":
                return queryAuditLogs(request);
            case "exportAuditLogs":
                return exportAuditLogs(request);
            case "getUserStats":
                return getUserStats(request);
            case "getResourceStats":
                return getResourceStats(request);
            default:
                throw new UnsupportedOperationException("Unsupported operation: " + operation);
        }
    }

    @Override
    protected String getResourceType() {
        return "security";
    }

    private Object getUserPermissions(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String userId = (String) params.get("userId");
        CompletableFuture<List<Permission>> future = permissionService.getUserPermissions(userId);
        return future.join();
    }

    private Object grantPermission(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String roleId = (String) params.get("roleId");
        Map<String, Object> permissionMap = (Map<String, Object>) params.get("permission");
        
        Permission permission = new Permission();
        permission.setPermissionId((String) permissionMap.get("permissionId"));
        permission.setResource((String) permissionMap.get("resource"));
        permission.setAction((String) permissionMap.get("action"));
        permission.setEffect((String) permissionMap.get("effect"));
        permission.setConditions((Map<String, Object>) permissionMap.get("conditions"));
        
        CompletableFuture<Void> future = permissionService.grantPermission(roleId, permission);
        future.join();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    private Object revokePermission(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String roleId = (String) params.get("roleId");
        String permissionId = (String) params.get("permissionId");
        
        CompletableFuture<Void> future = permissionService.revokePermission(roleId, permissionId);
        future.join();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    private Object queryAuditLogs(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        AuditLogQuery query = buildAuditLogQuery(params);
        
        CompletableFuture<List<AuditLog>> future = auditService.queryLogs(query);
        return future.join();
    }

    private Object exportAuditLogs(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        AuditLogQuery query = buildAuditLogQuery(params);
        
        CompletableFuture<AuditExportResult> future = auditService.exportLogs(query);
        return future.join();
    }

    private Object getUserStats(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String userId = (String) params.get("userId");
        long startTime = ((Number) params.get("startTime")).longValue();
        long endTime = ((Number) params.get("endTime")).longValue();
        
        CompletableFuture<UserOperationStats> future = auditService.getUserStats(userId, startTime, endTime);
        return future.join();
    }

    private Object getResourceStats(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String resourceType = (String) params.get("resourceType");
        String resourceId = (String) params.get("resourceId");
        
        CompletableFuture<ResourceAccessStats> future = auditService.getResourceStats(resourceType, resourceId);
        return future.join();
    }

    private AuditLogQuery buildAuditLogQuery(Map<String, Object> params) {
        AuditLogQuery query = new AuditLogQuery();
        if (params.containsKey("userId")) {
            query.setUserId((String) params.get("userId"));
        }
        if (params.containsKey("operation")) {
            query.setOperation((String) params.get("operation"));
        }
        if (params.containsKey("resource")) {
            query.setResource((String) params.get("resource"));
        }
        if (params.containsKey("result")) {
            query.setResult(OperationResult.valueOf((String) params.get("result")));
        }
        if (params.containsKey("startTime")) {
            query.setStartTime(((Number) params.get("startTime")).longValue());
        }
        if (params.containsKey("endTime")) {
            query.setEndTime(((Number) params.get("endTime")).longValue());
        }
        if (params.containsKey("page")) {
            query.setPageNum(((Number) params.get("page")).intValue());
        }
        if (params.containsKey("pageSize")) {
            query.setPageSize(((Number) params.get("pageSize")).intValue());
        }
        return query;
    }

    @Override
    protected String getSkillId() {
        return "skill-security";
    }
}
