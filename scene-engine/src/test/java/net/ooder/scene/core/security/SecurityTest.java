package net.ooder.scene.core.security;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 安全能力测试
 */
public class SecurityTest {

    private SecureSkillService mockSkillService;
    private AuditService mockAuditService;
    private PermissionService mockPermissionService;
    private SecurityInterceptor mockInterceptor;

    @Before
    public void setup() {
        mockAuditService = new MockAuditService();
        mockPermissionService = new MockPermissionService();
        mockInterceptor = new MockSecurityInterceptor();
        
        mockSkillService = new MockSecureSkillService();
        mockSkillService.auditService = mockAuditService;
        mockSkillService.permissionService = mockPermissionService;
        List<SecurityInterceptor> interceptorList = new ArrayList<>();
        interceptorList.add(mockInterceptor);
        mockSkillService.interceptors = interceptorList;
    }

    @Test
    public void testAuditLogRecording() {
        SkillRequest request = new SkillRequest();
        request.setRequestId("test-request-1");
        request.setOperation("read");
        request.setResourceId("test-resource-1");
        
        Object result = mockSkillService.execute(request);
        
        MockAuditService auditService = (MockAuditService) mockAuditService;
        assertTrue("审计日志应被记录", auditService.isLogRecorded());
    }

    @Test
    public void testPermissionCheckPass() {
        SkillRequest request = new SkillRequest();
        request.setRequestId("test-request-2");
        request.setOperation("read");
        request.setResourceId("test-resource-2");
        
        MockPermissionService permissionService = (MockPermissionService) mockPermissionService;
        permissionService.setAllowAccess(true);
        
        Object result = mockSkillService.execute(request);
        
        assertNotNull("操作应成功执行", result);
    }

    @Test
    public void testPermissionCheckFail() {
        SkillRequest request = new SkillRequest();
        request.setRequestId("test-request-3");
        request.setOperation("write");
        request.setResourceId("test-resource-3");
        
        MockPermissionService permissionService = (MockPermissionService) mockPermissionService;
        permissionService.setAllowAccess(false);
        
        Object result = mockSkillService.execute(request);
        
        assertTrue("应返回SkillResponse", result instanceof SkillResponse);
        SkillResponse response = (SkillResponse) result;
        assertFalse("操作应被拒绝", response.isSuccess());
        assertEquals("Permission denied", response.getError());
    }

    @Test
    public void testSecurityInterceptorBeforeExecute() {
        SkillRequest request = new SkillRequest();
        request.setRequestId("test-request-4");
        request.setOperation("read");
        request.setResourceId("test-resource-4");
        
        MockSecurityInterceptor interceptor = (MockSecurityInterceptor) mockInterceptor;
        interceptor.setAllowAccess(true);
        
        Object result = mockSkillService.execute(request);
        
        assertTrue("拦截器前置检查应被调用", interceptor.isBeforeExecuteCalled());
    }

    @Test
    public void testSecurityInterceptorDenyAccess() {
        SkillRequest request = new SkillRequest();
        request.setRequestId("test-request-5");
        request.setOperation("write");
        request.setResourceId("test-resource-5");
        
        MockSecurityInterceptor interceptor = (MockSecurityInterceptor) mockInterceptor;
        interceptor.setAllowAccess(false);
        
        Object result = mockSkillService.execute(request);
        
        assertTrue("应返回SkillResponse", result instanceof SkillResponse);
        SkillResponse response = (SkillResponse) result;
        assertFalse("操作应被拦截器拒绝", response.isSuccess());
    }

    @Test
    public void testExceptionHandling() {
        SkillRequest request = new SkillRequest();
        request.setRequestId("test-request-6");
        request.setOperation("read");
        request.setResourceId("test-resource-6");
        
        MockSecureSkillService skillService = (MockSecureSkillService) mockSkillService;
        skillService.setThrowException(true);
        
        Object result = mockSkillService.execute(request);
        
        assertTrue("应返回SkillResponse", result instanceof SkillResponse);
        SkillResponse response = (SkillResponse) result;
        assertFalse("操作应失败", response.isSuccess());
        assertNotNull("应包含错误信息", response.getError());
    }

    private static class MockAuditService implements AuditService {
        private boolean logRecorded = false;

        @Override
        public void logOperation(OperationContext context, String operation, String resource, 
                                String resourceId, OperationResult result, Map<String, Object> details) {
            logRecorded = true;
        }

        @Override
        public CompletableFuture<List<AuditLog>> queryLogs(AuditLogQuery query) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        @Override
        public CompletableFuture<AuditExportResult> exportLogs(AuditLogQuery query) {
            return CompletableFuture.completedFuture(new AuditExportResult());
        }

        @Override
        public CompletableFuture<UserOperationStats> getUserStats(String userId, long startTime, long endTime) {
            return CompletableFuture.completedFuture(new UserOperationStats());
        }

        @Override
        public CompletableFuture<ResourceAccessStats> getResourceStats(String resourceType, String resourceId) {
            return CompletableFuture.completedFuture(new ResourceAccessStats());
        }

        public boolean isLogRecorded() {
            return logRecorded;
        }
    }

    private static class MockPermissionService implements PermissionService {
        private boolean allowAccess = true;

        @Override
        public boolean checkPermission(String userId, String resource, String action) {
            return allowAccess;
        }

        @Override
        public PermissionCheckResult checkPermissionWithContext(OperationContext context, String resource, String action) {
            PermissionCheckResult result = new PermissionCheckResult();
            result.setAllowed(allowAccess);
            return result;
        }

        @Override
        public CompletableFuture<List<Permission>> getUserPermissions(String userId) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        @Override
        public CompletableFuture<List<Permission>> getRolePermissions(String roleId) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        @Override
        public CompletableFuture<Void> grantPermission(String roleId, Permission permission) {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletableFuture<Void> revokePermission(String roleId, String permissionId) {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public void addPermissionChangeListener(PermissionChangeListener listener) {
        }

        public void setAllowAccess(boolean allowAccess) {
            this.allowAccess = allowAccess;
        }
    }

    private static class MockSecurityInterceptor implements SecurityInterceptor {
        private boolean allowAccess = true;
        private boolean beforeExecuteCalled = false;

        @Override
        public InterceptorResult beforeExecute(OperationContext context, SkillRequest request) {
            beforeExecuteCalled = true;
            InterceptorResult result = new InterceptorResult();
            result.setAllowed(allowAccess);
            if (!allowAccess) {
                result.setDenyReason("Interceptor denied access");
            }
            return result;
        }

        @Override
        public void afterExecute(OperationContext context, SkillRequest request, SkillResponse response) {
        }

        @Override
        public void onError(OperationContext context, SkillRequest request, Throwable error) {
        }

        @Override
        public int getOrder() {
            return 100;
        }

        public void setAllowAccess(boolean allowAccess) {
            this.allowAccess = allowAccess;
        }

        public boolean isBeforeExecuteCalled() {
            return beforeExecuteCalled;
        }
    }

    private static class MockSecureSkillService extends SecureSkillService {
        private boolean throwException = false;

        @Override
        protected Object doExecute(SkillRequest request) {
            if (throwException) {
                throw new RuntimeException("Test exception");
            }
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            return result;
        }

        @Override
        protected String getResourceType() {
            return "test";
        }

        public void setThrowException(boolean throwException) {
            this.throwException = throwException;
        }
    }
}
