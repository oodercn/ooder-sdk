package net.ooder.scene.protocol;

import net.ooder.scene.protocol.impl.*;
import net.ooder.scene.session.impl.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Protocol Adapter 测试
 */
public class ProtocolAdapterTest {

    private LoginProtocolAdapter loginAdapter;
    private DiscoveryProtocolAdapter discoveryAdapter;
    private SessionManagerImpl sessionManager;
    private TokenManagerImpl tokenManager;

    @BeforeEach
    public void setup() {
        sessionManager = new SessionManagerImpl();
        tokenManager = new TokenManagerImpl();
        
        loginAdapter = new LoginProtocolAdapterImpl(sessionManager, tokenManager);
        discoveryAdapter = new DiscoveryProtocolAdapterImpl();
    }

    @Test
    public void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");
        request.setClientIp("192.168.1.1");
        request.setUserAgent("TestClient/1.0");
        
        CompletableFuture<LoginResult> future = loginAdapter.login(request);
        LoginResult result = future.get();
        
        assertTrue(result.isSuccess());
        assertEquals("Login successful", result.getMessage());
        assertNotNull(result.getSession());
        assertNotNull(result.getSession().getSessionId());
        assertEquals("user-admin", result.getSession().getUserId());
        assertEquals("admin", result.getSession().getUsername());
    }

    @Test
    public void testLoginFailed() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrongpassword");
        
        CompletableFuture<LoginResult> future = loginAdapter.login(request);
        LoginResult result = future.get();
        
        assertFalse(result.isSuccess());
        assertEquals("AUTH_FAILED", result.getErrorCode());
    }

    @Test
    public void testLoginEmptyUsername() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("password");
        
        CompletableFuture<LoginResult> future = loginAdapter.login(request);
        LoginResult result = future.get();
        
        assertFalse(result.isSuccess());
        assertEquals("USERNAME_REQUIRED", result.getErrorCode());
    }

    @Test
    public void testValidateSession() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");
        
        LoginResult loginResult = loginAdapter.login(request).get();
        String sessionId = loginResult.getSession().getSessionId();
        
        CompletableFuture<Boolean> validateFuture = loginAdapter.validateSession(sessionId);
        Boolean isValid = validateFuture.get();
        
        assertTrue(isValid);
    }

    @Test
    public void testLogout() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");
        
        LoginResult loginResult = loginAdapter.login(request).get();
        String sessionId = loginResult.getSession().getSessionId();
        
        loginAdapter.logout(sessionId).get();
        
        CompletableFuture<Boolean> validateFuture = loginAdapter.validateSession(sessionId);
        Boolean isValid = validateFuture.get();
        
        assertFalse(isValid);
    }

    @Test
    public void testGetCurrentUserId() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");
        
        LoginResult loginResult = loginAdapter.login(request).get();
        String sessionId = loginResult.getSession().getSessionId();
        
        CompletableFuture<String> userIdFuture = loginAdapter.getCurrentUserId(sessionId);
        String userId = userIdFuture.get();
        
        assertEquals("user-admin", userId);
    }

    @Test
    public void testDiscoverPeers() throws Exception {
        DiscoveryRequest request = new DiscoveryRequest();
        request.setDiscoveryType("BROADCAST");
        request.setMaxPeers(10);
        
        CompletableFuture<DiscoveryResult> future = discoveryAdapter.discoverPeers(request);
        DiscoveryResult result = future.get();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getPeers());
    }

    @Test
    public void testDiscoverMcp() throws Exception {
        CompletableFuture<Peer> future = discoveryAdapter.discoverMcp();
        Peer mcpPeer = future.get();
        
        assertNotNull(mcpPeer);
        assertEquals("MCP", mcpPeer.getPeerType());
        assertNotNull(mcpPeer.getPeerId());
    }

    @Test
    public void testListDiscoveredPeers() throws Exception {
        discoveryAdapter.discoverMcp().get();
        
        CompletableFuture<java.util.List<Peer>> future = discoveryAdapter.listDiscoveredPeers();
        java.util.List<Peer> peers = future.get();
        
        assertNotNull(peers);
        assertTrue(peers.size() > 0);
    }

    @Test
    public void testIsPeerOnline() throws Exception {
        Peer mcpPeer = discoveryAdapter.discoverMcp().get();
        
        CompletableFuture<Boolean> future = discoveryAdapter.isPeerOnline(mcpPeer.getPeerId());
        Boolean isOnline = future.get();
        
        assertTrue(isOnline);
    }

    @Test
    public void testDiscoveryListener() throws Exception {
        final boolean[] listenerCalled = {false};
        
        discoveryAdapter.addDiscoveryListener(new DiscoveryEventListener() {
            @Override
            public void onPeerDiscovered(Peer peer) {
                listenerCalled[0] = true;
            }
            
            @Override
            public void onPeerOffline(String peerId) {
            }
            
            @Override
            public void onPeerStatusChanged(String peerId, String oldStatus, String newStatus) {
            }
        });
        
        DiscoveryProtocolAdapterImpl impl = (DiscoveryProtocolAdapterImpl) discoveryAdapter;
        Peer testPeer = new Peer();
        testPeer.setPeerId("test-peer-1");
        testPeer.setPeerName("Test Peer");
        testPeer.setPeerType("TEST");
        testPeer.setStatus("ONLINE");
        
        impl.registerPeer(testPeer);
        
        assertTrue(listenerCalled[0]);
    }
}
