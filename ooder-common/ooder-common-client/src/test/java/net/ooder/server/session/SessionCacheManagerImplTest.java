package net.ooder.server.session;

import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SessionCacheManagerImplTest {
    
    private SessionCacheManager cacheManager;
    private static final long EXPIRE_TIME = 30 * 60 * 1000L;
    
    @Before
    public void setUp() {
        cacheManager = new TestSessionCacheManager(EXPIRE_TIME);
    }
    
    @Test
    public void testPutAndGetSession() {
        String sessionId = "test-session-001";
        JDSSessionHandle handle = new JDSSessionHandle(sessionId);
        ConnectInfo info = new ConnectInfo("user001", "login001", "password");
        
        cacheManager.putSession(handle, info, "system001");
        
        assertNotNull(cacheManager.getConnectInfo(sessionId));
        assertNotNull(cacheManager.getSessionHandle(sessionId));
        assertEquals("system001", cacheManager.getSystemCode(sessionId));
        assertNotNull(cacheManager.getConnectTime(sessionId));
    }
    
    @Test
    public void testContainsSession() {
        String sessionId = "test-session-002";
        JDSSessionHandle handle = new JDSSessionHandle(sessionId);
        ConnectInfo info = new ConnectInfo("user002", "login002", "password");
        
        assertFalse(cacheManager.containsSession(sessionId));
        
        cacheManager.putSession(handle, info, "system001");
        
        assertTrue(cacheManager.containsSession(sessionId));
    }
    
    @Test
    public void testInvalidateSession() {
        String sessionId = "test-session-003";
        JDSSessionHandle handle = new JDSSessionHandle(sessionId);
        ConnectInfo info = new ConnectInfo("user003", "login003", "password");
        
        cacheManager.putSession(handle, info, "system001");
        assertTrue(cacheManager.containsSession(sessionId));
        
        cacheManager.invalidateSession(sessionId);
        
        assertFalse(cacheManager.containsSession(sessionId));
        assertNull(cacheManager.getConnectInfo(sessionId));
        assertNull(cacheManager.getSessionHandle(sessionId));
    }
    
    @Test
    public void testGetSessionCount() {
        assertEquals(0, cacheManager.getSessionCount());
        
        JDSSessionHandle handle1 = new JDSSessionHandle("session-001");
        ConnectInfo info1 = new ConnectInfo("user001", "login001", "password");
        cacheManager.putSession(handle1, info1, "system001");
        
        assertEquals(1, cacheManager.getSessionCount());
        
        JDSSessionHandle handle2 = new JDSSessionHandle("session-002");
        ConnectInfo info2 = new ConnectInfo("user002", "login002", "password");
        cacheManager.putSession(handle2, info2, "system001");
        
        assertEquals(2, cacheManager.getSessionCount());
    }
    
    @Test
    public void testGetStats() {
        JDSSessionHandle handle = new JDSSessionHandle("session-001");
        ConnectInfo info = new ConnectInfo("user001", "login001", "password");
        cacheManager.putSession(handle, info, "system001");
        
        CacheStats stats = cacheManager.getStats();
        
        assertEquals(1, stats.getSessionCount());
        assertEquals(1, stats.getConnectInfoCount());
        assertEquals(1, stats.getSystemCodeCount());
        assertEquals(1, stats.getConnectTimeCount());
        assertTrue(stats.isConsistent());
    }
    
    @Test
    public void testUpdateConnectTime() throws InterruptedException {
        String sessionId = "test-session-004";
        JDSSessionHandle handle = new JDSSessionHandle(sessionId);
        ConnectInfo info = new ConnectInfo("user004", "login004", "password");
        
        cacheManager.putSession(handle, info, "system001");
        Long originalTime = cacheManager.getConnectTime(sessionId);
        
        Thread.sleep(100);
        
        cacheManager.updateConnectTime(sessionId);
        Long updatedTime = cacheManager.getConnectTime(sessionId);
        
        assertTrue(updatedTime > originalTime);
    }
    
    @Test
    public void testClear() {
        JDSSessionHandle handle1 = new JDSSessionHandle("session-001");
        ConnectInfo info1 = new ConnectInfo("user001", "login001", "password");
        cacheManager.putSession(handle1, info1, "system001");
        
        JDSSessionHandle handle2 = new JDSSessionHandle("session-002");
        ConnectInfo info2 = new ConnectInfo("user002", "login002", "password");
        cacheManager.putSession(handle2, info2, "system001");
        
        assertEquals(2, cacheManager.getSessionCount());
        
        cacheManager.clear();
        
        assertEquals(0, cacheManager.getSessionCount());
    }
    
    @Test
    public void testNullSessionId() {
        assertNull(cacheManager.getConnectInfo(null));
        assertNull(cacheManager.getSessionHandle(null));
        assertNull(cacheManager.getSystemCode(null));
        assertNull(cacheManager.getConnectTime(null));
        
        cacheManager.invalidateSession(null);
        cacheManager.updateConnectTime(null);
        
        assertFalse(cacheManager.containsSession(null));
    }
    
    @Test
    public void testNullHandle() {
        cacheManager.putSession(null, new ConnectInfo("user", "login", "pass"), "system");
        
        assertEquals(0, cacheManager.getSessionCount());
    }
    
    @Test
    public void testNullConnectInfo() {
        JDSSessionHandle handle = new JDSSessionHandle("session-001");
        cacheManager.putSession(handle, null, "system");
        
        assertEquals(0, cacheManager.getSessionCount());
    }
}
