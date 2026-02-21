package net.ooder.server.session;

import net.ooder.common.JDSException;
import net.ooder.engine.ConnectInfo;
import net.ooder.engine.JDSSessionHandle;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SessionManagerImplTest {
    
    private SessionManager sessionManager;
    private SessionCacheManager cacheManager;
    private static final long EXPIRE_TIME = 30 * 60 * 1000L;
    
    @Before
    public void setUp() {
        cacheManager = new TestSessionCacheManager(EXPIRE_TIME);
        sessionManager = new TestSessionManager(cacheManager);
    }
    
    @Test
    public void testCreateSession() throws JDSException {
        ConnectInfo info = new ConnectInfo("user001", "login001", "password");
        
        JDSSessionHandle handle = sessionManager.createSession(info);
        
        assertNotNull(handle);
        assertNotNull(handle.getSessionID());
        assertTrue(sessionManager.isSessionValid(handle.getSessionID()));
    }
    
    @Test
    public void testGetSession() throws JDSException {
        ConnectInfo info = new ConnectInfo("user001", "login001", "password");
        JDSSessionHandle createdHandle = sessionManager.createSession(info);
        
        JDSSessionHandle retrievedHandle = sessionManager.getSession(createdHandle.getSessionID());
        
        assertNotNull(retrievedHandle);
        assertEquals(createdHandle.getSessionID(), retrievedHandle.getSessionID());
    }
    
    @Test
    public void testInvalidateSession() throws JDSException {
        ConnectInfo info = new ConnectInfo("user001", "login001", "password");
        JDSSessionHandle handle = sessionManager.createSession(info);
        String sessionId = handle.getSessionID();
        
        assertTrue(sessionManager.isSessionValid(sessionId));
        
        sessionManager.invalidateSession(sessionId);
        
        assertFalse(sessionManager.isSessionValid(sessionId));
        assertNull(sessionManager.getSession(sessionId));
    }
    
    @Test
    public void testKeepAlive() throws JDSException {
        ConnectInfo info = new ConnectInfo("user001", "login001", "password");
        JDSSessionHandle handle = sessionManager.createSession(info);
        String sessionId = handle.getSessionID();
        
        Long originalTime = cacheManager.getConnectTime(sessionId);
        
        sessionManager.keepAlive(sessionId);
        
        Long updatedTime = cacheManager.getConnectTime(sessionId);
        assertTrue(updatedTime >= originalTime);
    }
    
    @Test
    public void testIsSessionValid() throws JDSException {
        ConnectInfo info = new ConnectInfo("user001", "login001", "password");
        JDSSessionHandle handle = sessionManager.createSession(info);
        String sessionId = handle.getSessionID();
        
        assertTrue(sessionManager.isSessionValid(sessionId));
        assertFalse(sessionManager.isSessionValid("non-existent-session"));
        assertFalse(sessionManager.isSessionValid(null));
    }
    
    @Test
    public void testGetStats() throws JDSException {
        ConnectInfo info1 = new ConnectInfo("user001", "login001", "password");
        sessionManager.createSession(info1);
        
        ConnectInfo info2 = new ConnectInfo("user002", "login002", "password");
        sessionManager.createSession(info2);
        
        SessionStats stats = sessionManager.getStats();
        
        assertEquals(2, stats.getActiveSessions());
        assertEquals(2, stats.getTotalLogins());
        assertEquals(0, stats.getTotalLogouts());
        assertEquals(0, stats.getExpiredSessions());
    }
    
    @Test
    public void testStatsAfterInvalidate() throws JDSException {
        ConnectInfo info = new ConnectInfo("user001", "login001", "password");
        JDSSessionHandle handle = sessionManager.createSession(info);
        
        SessionStats statsBefore = sessionManager.getStats();
        assertEquals(1, statsBefore.getActiveSessions());
        assertEquals(1, statsBefore.getTotalLogins());
        
        sessionManager.invalidateSession(handle.getSessionID());
        
        SessionStats statsAfter = sessionManager.getStats();
        assertEquals(0, statsAfter.getActiveSessions());
        assertEquals(1, statsAfter.getTotalLogouts());
    }
    
    @Test(expected = JDSException.class)
    public void testCreateSessionWithNullInfo() throws JDSException {
        sessionManager.createSession(null);
    }
    
    @Test
    public void testLifecycleListener() throws JDSException {
        final boolean[] createdCalled = {false};
        final boolean[] destroyedCalled = {false};
        
        SessionLifecycle listener = new SessionLifecycle() {
            @Override
            public void onCreated(JDSSessionHandle handle, ConnectInfo info) {
                createdCalled[0] = true;
            }
            
            @Override
            public void onActivated(JDSSessionHandle handle) {
            }
            
            @Override
            public void onExpired(JDSSessionHandle handle) {
            }
            
            @Override
            public void onDestroyed(JDSSessionHandle handle) {
                destroyedCalled[0] = true;
            }
        };
        
        sessionManager.registerLifecycleListener(listener);
        
        ConnectInfo info = new ConnectInfo("user001", "login001", "password");
        JDSSessionHandle handle = sessionManager.createSession(info);
        
        assertTrue(createdCalled[0]);
        
        sessionManager.invalidateSession(handle.getSessionID());
        
        assertTrue(destroyedCalled[0]);
    }
    
    @Test
    public void testUnregisterLifecycleListener() throws JDSException {
        final int[] callCount = {0};
        
        SessionLifecycle listener = new SessionLifecycle() {
            @Override
            public void onCreated(JDSSessionHandle handle, ConnectInfo info) {
                callCount[0]++;
            }
            
            @Override
            public void onActivated(JDSSessionHandle handle) {
            }
            
            @Override
            public void onExpired(JDSSessionHandle handle) {
            }
            
            @Override
            public void onDestroyed(JDSSessionHandle handle) {
            }
        };
        
        sessionManager.registerLifecycleListener(listener);
        
        ConnectInfo info1 = new ConnectInfo("user001", "login001", "password");
        sessionManager.createSession(info1);
        assertEquals(1, callCount[0]);
        
        sessionManager.unregisterLifecycleListener(listener);
        
        ConnectInfo info2 = new ConnectInfo("user002", "login002", "password");
        sessionManager.createSession(info2);
        assertEquals(1, callCount[0]);
    }
    
    @Test
    public void testGetNullSession() {
        assertNull(sessionManager.getSession(null));
        assertNull(sessionManager.getSession("non-existent-session"));
    }
    
    @Test
    public void testInvalidateNullSession() {
        sessionManager.invalidateSession(null);
        sessionManager.invalidateSession("non-existent-session");
    }
    
    @Test
    public void testKeepAliveNullSession() {
        sessionManager.keepAlive(null);
        sessionManager.keepAlive("non-existent-session");
    }
}
