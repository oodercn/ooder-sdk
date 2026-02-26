package net.ooder.scene.core.security;

import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.security.OperationDeniedEvent;
import net.ooder.scene.event.skill.SkillEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全技能服务基类
 *
 * <p>所有 Skill 应继承此基类，自动获得安全能力</p>
 */
public abstract class SecureSkillService implements SkillService {

    protected AuditService auditService;
    protected PermissionService permissionService;
    protected List<SecurityInterceptor> interceptors;
    protected SceneEventPublisher eventPublisher;

    @Override
    public final Object execute(SkillRequest request) {
        OperationContext context = buildContext(request);

        for (SecurityInterceptor interceptor : interceptors) {
            InterceptorResult result = interceptor.beforeExecute(context, request);
            if (!result.isAllowed()) {
                Map<String, Object> details = new HashMap<>();
                details.put("reason", result.getDenyReason());
                auditService.logOperation(context, request.getOperation(),
                    getResourceType(), request.getResourceId(),
                    OperationResult.DENIED, details);
                publishOperationDenied(context.getUserId(), request.getOperation(), 
                    request.getResourceId(), result.getDenyReason());
                return SkillResponse.denied(result.getDenyReason());
            }
        }

        if (!permissionService.checkPermission(context.getUserId(),
                getResourceType(), request.getOperation())) {
            Map<String, Object> details = new HashMap<>();
            details.put("reason", "Permission denied");
            auditService.logOperation(context, request.getOperation(),
                getResourceType(), request.getResourceId(),
                OperationResult.DENIED, details);
            publishOperationDenied(context.getUserId(), request.getOperation(),
                request.getResourceId(), "Permission denied");
            return SkillResponse.denied("Permission denied");
        }

        long startTime = System.currentTimeMillis();
        try {
            Object result = doExecute(request);

            Map<String, Object> details = new HashMap<>();
            details.put("duration", System.currentTimeMillis() - startTime);
            auditService.logOperation(context, request.getOperation(),
                getResourceType(), request.getResourceId(),
                OperationResult.SUCCESS, details);

            for (SecurityInterceptor interceptor : interceptors) {
                interceptor.afterExecute(context, request, (SkillResponse) result);
            }

            return result;
        } catch (Exception e) {
            Map<String, Object> details = new HashMap<>();
            details.put("error", e.getMessage());
            details.put("duration", System.currentTimeMillis() - startTime);
            auditService.logOperation(context, request.getOperation(),
                getResourceType(), request.getResourceId(),
                OperationResult.FAILURE, details);

            for (SecurityInterceptor interceptor : interceptors) {
                interceptor.onError(context, request, e);
            }
            
            publishSkillExecutionError(getSkillId(), getSkillId(), e.getMessage());

            return SkillResponse.error(e.getMessage());
        }
    }

    protected abstract Object doExecute(SkillRequest request);

    protected abstract String getResourceType();

    protected OperationContext buildContext(SkillRequest request) {
        OperationContext context = new OperationContext();
        context.setUserId(getCurrentUserId());
        context.setSessionId(getCurrentSessionId());
        context.setIpAddress(getClientIpAddress());
        context.setSceneId(getSceneId());
        context.setGroupId(getGroupId());
        context.setSkillId(getSkillId());
        context.setTimestamp(System.currentTimeMillis());
        context.setRequestId(request.getRequestId());
        return context;
    }
    
    protected String getCurrentUserId() {
        return "system";
    }
    
    protected String getCurrentSessionId() {
        return "session-123";
    }
    
    protected String getClientIpAddress() {
        return "127.0.0.1";
    }
    
    protected String getSceneId() {
        return "default";
    }
    
    protected String getGroupId() {
        return "default";
    }
    
    protected String getSkillId() {
        return "unknown";
    }
    
    public void setEventPublisher(SceneEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    private void publishOperationDenied(String userId, String operation, String resource, String reason) {
        if (eventPublisher != null) {
            eventPublisher.publish(new OperationDeniedEvent(this, userId, operation, resource, reason));
        }
    }
    
    private void publishSkillExecutionError(String skillId, String skillName, String error) {
        if (eventPublisher != null) {
            eventPublisher.publish(SkillEvent.executionError(this, skillId, skillName, error));
        }
    }
}

interface SkillService {
    Object execute(SkillRequest request);
}
