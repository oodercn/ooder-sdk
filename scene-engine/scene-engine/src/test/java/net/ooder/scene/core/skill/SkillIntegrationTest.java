package net.ooder.scene.core.skill;

import net.ooder.scene.core.security.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Skills集成测试
 */
public class SkillIntegrationTest {

    @Test
    public void testSecureSkillServiceStructure() {
        assertNotNull(SecureSkillService.class);
    }

    @Test
    public void testSkillRequestStructure() {
        SkillRequest request = new SkillRequest();
        request.setRequestId("test-1");
        request.setOperation("read");
        request.setResourceId("resource-1");
        
        assertEquals("test-1", request.getRequestId());
        assertEquals("read", request.getOperation());
        assertEquals("resource-1", request.getResourceId());
    }

    @Test
    public void testSkillResponseSuccess() {
        SkillResponse response = SkillResponse.denied("Access denied");
        
        assertFalse(response.isSuccess());
        assertEquals("Access denied", response.getError());
    }

    @Test
    public void testSkillResponseError() {
        SkillResponse response = SkillResponse.error("System error");
        
        assertFalse(response.isSuccess());
        assertEquals("System error", response.getError());
    }

    @Test
    public void testOperationContext() {
        OperationContext context = new OperationContext();
        context.setUserId("user-1");
        context.setSessionId("session-1");
        context.setIpAddress("192.168.1.1");
        context.setSceneId("scene-1");
        context.setSkillId("skill-1");
        context.setTimestamp(System.currentTimeMillis());
        
        assertEquals("user-1", context.getUserId());
        assertEquals("session-1", context.getSessionId());
        assertEquals("192.168.1.1", context.getIpAddress());
        assertEquals("scene-1", context.getSceneId());
        assertEquals("skill-1", context.getSkillId());
    }

    @Test
    public void testOperationResultValues() {
        assertEquals("SUCCESS", OperationResult.SUCCESS.name());
        assertEquals("FAILURE", OperationResult.FAILURE.name());
        assertEquals("DENIED", OperationResult.DENIED.name());
        assertEquals("TIMEOUT", OperationResult.TIMEOUT.name());
    }

    @Test
    public void testAuditLogStructure() {
        AuditLog log = new AuditLog();
        log.setLogId("log-1");
        log.setUserId("user-1");
        log.setOperation("read");
        log.setResource("storage");
        log.setResult(OperationResult.SUCCESS);
        
        assertEquals("log-1", log.getLogId());
        assertEquals("user-1", log.getUserId());
        assertEquals("read", log.getOperation());
        assertEquals("storage", log.getResource());
        assertEquals(OperationResult.SUCCESS, log.getResult());
    }

    @Test
    public void testAuditLogQueryStructure() {
        AuditLogQuery query = new AuditLogQuery();
        query.setUserId("user-1");
        query.setOperation("read");
        query.setResource("storage");
        query.setResult(OperationResult.SUCCESS);
        query.setPageNum(1);
        query.setPageSize(20);
        
        assertEquals("user-1", query.getUserId());
        assertEquals("read", query.getOperation());
        assertEquals("storage", query.getResource());
        assertEquals(OperationResult.SUCCESS, query.getResult());
        assertEquals(1, query.getPageNum());
        assertEquals(20, query.getPageSize());
    }

    @Test
    public void testInterceptorResultStructure() {
        InterceptorResult result = new InterceptorResult();
        result.setAllowed(true);
        result.setDenyReason("Test reason");
        
        assertTrue(result.isAllowed());
        assertEquals("Test reason", result.getDenyReason());
    }

    @Test
    public void testPermissionStructure() {
        Permission permission = new Permission();
        permission.setPermissionId("perm-1");
        permission.setResource("storage");
        permission.setAction("read");
        permission.setEffect("ALLOW");
        
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("owner", "${userId}");
        permission.setConditions(conditions);
        
        assertEquals("perm-1", permission.getPermissionId());
        assertEquals("storage", permission.getResource());
        assertEquals("read", permission.getAction());
        assertEquals("ALLOW", permission.getEffect());
        assertNotNull(permission.getConditions());
    }

    @Test
    public void testNetworkSkillServiceExists() {
        assertNotNull(net.ooder.scene.core.skill.network.NetworkSkillService.class);
    }

    @Test
    public void testSecuritySkillServiceExists() {
        assertNotNull(net.ooder.scene.core.skill.security.SecuritySkillService.class);
    }

    @Test
    public void testStorageSkillServiceExists() {
        assertNotNull(net.ooder.scene.core.skill.storage.StorageSkillService.class);
    }

    @Test
    public void testLlmSkillServiceExists() {
        assertNotNull(net.ooder.scene.core.skill.llm.LlmSkillService.class);
    }

    @Test
    public void testSchedulerSkillServiceExists() {
        assertNotNull(net.ooder.scene.core.skill.scheduler.SchedulerSkillService.class);
    }

    @Test
    public void testNetworkSkillServiceInheritance() {
        assertTrue(SecureSkillService.class.isAssignableFrom(
            net.ooder.scene.core.skill.network.NetworkSkillService.class));
    }

    @Test
    public void testSecuritySkillServiceInheritance() {
        assertTrue(SecureSkillService.class.isAssignableFrom(
            net.ooder.scene.core.skill.security.SecuritySkillService.class));
    }

    @Test
    public void testStorageSkillServiceInheritance() {
        assertTrue(SecureSkillService.class.isAssignableFrom(
            net.ooder.scene.core.skill.storage.StorageSkillService.class));
    }

    @Test
    public void testLlmSkillServiceInheritance() {
        assertTrue(SecureSkillService.class.isAssignableFrom(
            net.ooder.scene.core.skill.llm.LlmSkillService.class));
    }

    @Test
    public void testSchedulerSkillServiceInheritance() {
        assertTrue(SecureSkillService.class.isAssignableFrom(
            net.ooder.scene.core.skill.scheduler.SchedulerSkillService.class));
    }
}
