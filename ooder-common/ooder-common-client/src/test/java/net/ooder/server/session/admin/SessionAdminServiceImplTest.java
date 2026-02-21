package net.ooder.server.session.admin;

import net.ooder.common.JDSException;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.server.session.*;
import net.ooder.server.session.TestSessionCacheManager;
import net.ooder.server.session.TestSessionManager;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

public class SessionAdminServiceImplTest {
    
    private SessionAdminService adminService;
    private SessionManager sessionManager;
    private SessionCacheManager cacheManager;
    private static final long EXPIRE_TIME = 30 * 60 * 1000L;
    
    @Before
    public void setUp() throws JDSException {
        cacheManager = new TestSessionCacheManager(EXPIRE_TIME);
        sessionManager = new TestSessionManager(cacheManager);
        adminService = new TestSessionAdminService(cacheManager, sessionManager, EXPIRE_TIME);
    }
    
    private String createTestSession(String userId, String loginName) throws JDSException {
        ConnectInfo info = new ConnectInfo(userId, loginName, "password");
        JDSSessionHandle handle = sessionManager.createSession(info);
        return handle.getSessionID();
    }
    
    @Test
    public void testGetSessionInfo() throws JDSException {
        String sessionId = createTestSession("user001", "login001");
        
        SessionInfo info = adminService.getSessionInfo(sessionId);
        
        assertNotNull(info);
        assertEquals(sessionId, info.getSessionId());
        assertEquals("user001", info.getUserId());
        assertEquals("login001", info.getAccount());
        assertEquals("ACTIVE", info.getStatus());
    }
    
    @Test
    public void testGetSessionInfoNotFound() {
        SessionInfo info = adminService.getSessionInfo("non-existent-session");
        assertNull(info);
    }
    
    @Test
    public void testGetAllSessions() throws JDSException {
        createTestSession("user001", "login001");
        createTestSession("user002", "login002");
        createTestSession("user003", "login003");
        
        List<SessionInfo> sessions = adminService.getAllSessions();
        
        assertEquals(3, sessions.size());
    }
    
    @Test
    public void testGetSessionsByUserId() throws JDSException {
        createTestSession("user001", "login001");
        createTestSession("user001", "login002");
        createTestSession("user002", "login003");
        
        List<SessionInfo> user001Sessions = adminService.getSessionsByUserId("user001");
        
        assertEquals(2, user001Sessions.size());
    }
    
    @Test
    public void testGetSessionsByAccount() throws JDSException {
        createTestSession("user001", "login001");
        createTestSession("user002", "login001");
        createTestSession("user003", "login003");
        
        List<SessionInfo> sessions = adminService.getSessionsByAccount("login001");
        
        assertEquals(2, sessions.size());
    }
    
    @Test
    public void testGetSessionsPaged() throws JDSException {
        for (int i = 0; i < 25; i++) {
            createTestSession("user" + i, "login" + i);
        }
        
        List<SessionInfo> page0 = adminService.getSessions(0, 10);
        List<SessionInfo> page1 = adminService.getSessions(1, 10);
        List<SessionInfo> page2 = adminService.getSessions(2, 10);
        List<SessionInfo> page3 = adminService.getSessions(3, 10);
        
        assertEquals(10, page0.size());
        assertEquals(10, page1.size());
        assertEquals(5, page2.size());
        assertEquals(0, page3.size());
    }
    
    @Test
    public void testInvalidateSession() throws JDSException {
        String sessionId = createTestSession("user001", "login001");
        
        assertTrue(sessionManager.isSessionValid(sessionId));
        
        SessionOperationResult result = adminService.invalidateSession(sessionId);
        
        assertTrue(result.isSuccess());
        assertEquals("INVALIDATE", result.getOperation());
        assertEquals(sessionId, result.getSessionId());
        assertFalse(sessionManager.isSessionValid(sessionId));
    }
    
    @Test
    public void testInvalidateSessionNotFound() {
        SessionOperationResult result = adminService.invalidateSession("non-existent-session");
        
        assertFalse(result.isSuccess());
        assertEquals("NOT_FOUND", result.getErrorCode());
    }
    
    @Test
    public void testInvalidateSessionsByUserId() throws JDSException {
        createTestSession("user001", "login001");
        createTestSession("user001", "login002");
        createTestSession("user002", "login003");
        
        assertEquals(3, adminService.getActiveSessionCount());
        
        SessionOperationResult result = adminService.invalidateSessionsByUserId("user001");
        
        assertTrue(result.isSuccess());
        assertEquals(1, adminService.getActiveSessionCount());
    }
    
    @Test
    public void testInvalidateAllSessions() throws JDSException {
        createTestSession("user001", "login001");
        createTestSession("user002", "login002");
        createTestSession("user003", "login003");
        
        assertEquals(3, adminService.getActiveSessionCount());
        
        SessionOperationResult result = adminService.invalidateAllSessions();
        
        assertTrue(result.isSuccess());
        assertEquals(0, adminService.getActiveSessionCount());
    }
    
    @Test
    public void testKeepAlive() throws JDSException {
        String sessionId = createTestSession("user001", "login001");
        
        SessionOperationResult result = adminService.keepAlive(sessionId);
        
        assertTrue(result.isSuccess());
        assertEquals("KEEPALIVE", result.getOperation());
    }
    
    @Test
    public void testKeepAliveNotFound() {
        SessionOperationResult result = adminService.keepAlive("non-existent-session");
        
        assertFalse(result.isSuccess());
        assertEquals("NOT_FOUND", result.getErrorCode());
    }
    
    @Test
    public void testHealthCheck() throws JDSException {
        String sessionId = createTestSession("user001", "login001");
        cacheManager.putSession(cacheManager.getSessionHandle(sessionId), 
            cacheManager.getConnectInfo(sessionId), "system001");
        
        SessionHealthCheck health = adminService.healthCheck();
        
        assertEquals("UP", health.getStatus());
        assertEquals(1, health.getActiveSessionCount());
        assertTrue(health.isCacheHealthy());
        assertTrue(health.isSessionManagerHealthy());
        assertTrue(health.isHealthy());
    }
    
    @Test
    public void testDetailedHealthCheck() throws JDSException {
        String sessionId = createTestSession("user001", "login001");
        cacheManager.putSession(cacheManager.getSessionHandle(sessionId), 
            cacheManager.getConnectInfo(sessionId), "system001");
        
        SessionHealthCheck health = adminService.detailedHealthCheck();
        
        assertEquals("UP", health.getStatus());
        assertNotNull(health.getMemoryUsedFormatted());
        assertNotNull(health.getMemoryMaxFormatted());
        assertTrue(health.getMemoryUsagePercent() >= 0);
        assertTrue(health.getUptime() >= 0);
        assertNotNull(health.getUptimeFormatted());
    }
    
    @Test
    public void testGetStatistics() throws JDSException {
        createTestSession("user001", "login001");
        createTestSession("user002", "login002");
        
        Map<String, Object> stats = adminService.getStatistics();
        
        assertEquals(2, stats.get("activeSessions"));
        assertEquals(2, stats.get("totalLogins"));
        assertEquals(0, stats.get("totalLogouts"));
        assertNotNull(stats.get("uptime"));
        assertNotNull(stats.get("expireTime"));
    }
    
    @Test
    public void testGetCacheStatistics() throws JDSException {
        String sessionId = createTestSession("user001", "login001");
        cacheManager.putSession(cacheManager.getSessionHandle(sessionId), 
            cacheManager.getConnectInfo(sessionId), "system001");
        
        Map<String, Object> stats = adminService.getCacheStatistics();
        
        assertEquals(1, stats.get("sessionCount"));
        assertEquals(1, stats.get("connectInfoCount"));
        assertEquals(true, stats.get("consistent"));
    }
    
    @Test
    public void testSessionAttributes() throws JDSException {
        String sessionId = createTestSession("user001", "login001");
        
        SessionOperationResult result = adminService.setSessionAttribute(sessionId, "theme", "dark");
        assertTrue(result.isSuccess());
        
        Object theme = adminService.getSessionAttribute(sessionId, "theme");
        assertEquals("dark", theme);
        
        Map<String, Object> attrs = adminService.getSessionAttributes(sessionId);
        assertEquals(1, attrs.size());
        assertEquals("dark", attrs.get("theme"));
    }
    
    @Test
    public void testSessionAttributeNotFound() {
        Object attr = adminService.getSessionAttribute("non-existent-session", "key");
        assertNull(attr);
        
        Map<String, Object> attrs = adminService.getSessionAttributes("non-existent-session");
        assertTrue(attrs.isEmpty());
    }
    
    @Test
    public void testSetAttributeOnInvalidSession() {
        SessionOperationResult result = adminService.setSessionAttribute("non-existent-session", "key", "value");
        assertFalse(result.isSuccess());
        assertEquals("NOT_FOUND", result.getErrorCode());
    }
    
    @Test
    public void testForceLogout() throws JDSException {
        String sessionId = createTestSession("user001", "login001");
        
        assertTrue(sessionManager.isSessionValid(sessionId));
        
        SessionOperationResult result = adminService.forceLogout(sessionId, "管理员强制下线");
        
        assertTrue(result.isSuccess());
        assertEquals("FORCE_LOGOUT", result.getOperation());
        assertFalse(sessionManager.isSessionValid(sessionId));
    }
    
    @Test
    public void testGetActiveUserIds() throws JDSException {
        createTestSession("user001", "login001");
        createTestSession("user002", "login002");
        createTestSession("user001", "login003");
        
        List<String> userIds = adminService.getActiveUserIds();
        
        assertEquals(2, userIds.size());
        assertTrue(userIds.contains("user001"));
        assertTrue(userIds.contains("user002"));
    }
    
    @Test
    public void testGetSessionCountBySystemCode() throws JDSException {
        String sessionId1 = createTestSession("user001", "login001");
        String sessionId2 = createTestSession("user002", "login002");
        
        cacheManager.putSession(cacheManager.getSessionHandle(sessionId1), 
            cacheManager.getConnectInfo(sessionId1), "systemA");
        cacheManager.putSession(cacheManager.getSessionHandle(sessionId2), 
            cacheManager.getConnectInfo(sessionId2), "systemB");
        
        Map<String, Integer> counts = adminService.getSessionCountBySystemCode();
        
        assertTrue(counts.containsKey("systemA"));
        assertTrue(counts.containsKey("systemB"));
    }
    
    @Test
    public void testGetActiveSessionCount() throws JDSException {
        assertEquals(0, adminService.getActiveSessionCount());
        
        createTestSession("user001", "login001");
        assertEquals(1, adminService.getActiveSessionCount());
        
        createTestSession("user002", "login002");
        assertEquals(2, adminService.getActiveSessionCount());
    }
    
    @Test
    public void testGetTotalSessionCount() throws JDSException {
        createTestSession("user001", "login001");
        createTestSession("user002", "login002");
        
        assertEquals(2, adminService.getTotalSessionCount());
    }
    
    @Test
    public void testIsSessionValid() throws JDSException {
        String sessionId = createTestSession("user001", "login001");
        
        assertTrue(adminService.isSessionValid(sessionId));
        assertFalse(adminService.isSessionValid("non-existent-session"));
    }
    
    @Test
    public void testCleanupExpiredSessions() throws JDSException {
        createTestSession("user001", "login001");
        
        SessionOperationResult result = adminService.cleanupExpiredSessions();
        
        assertTrue(result.isSuccess());
        assertEquals("CLEANUP", result.getOperation());
    }
    
    @Test
    public void testBroadcastMessage() {
        SessionOperationResult result = adminService.broadcastMessage("test message");
        
        assertFalse(result.isSuccess());
        assertEquals("NOT_IMPLEMENTED", result.getErrorCode());
    }
    
    @Test
    public void testNullParameters() {
        assertNull(adminService.getSessionInfo(null));
        assertTrue(adminService.getSessionsByUserId(null).isEmpty());
        assertTrue(adminService.getSessionsByAccount(null).isEmpty());
        assertTrue(adminService.getSessionsBySystemCode(null).isEmpty());
        
        SessionOperationResult result = adminService.invalidateSession(null);
        assertFalse(result.isSuccess());
        assertEquals("INVALID_PARAM", result.getErrorCode());
    }
}
