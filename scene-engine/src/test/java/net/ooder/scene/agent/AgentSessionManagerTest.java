package net.ooder.scene.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AgentSessionManagerTest {

    private AgentSessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = new AgentSessionManagerImpl();
    }

    @Test
    void testRegister() {
        AgentRegistration registration = new AgentRegistration();
        registration.setAgentId("agent-001");
        registration.setAgentName("Test Agent");
        registration.setAgentType("assistant");
        registration.setCredentials("secret-key");
        registration.setCapabilities(Arrays.asList("chat", "search"));

        AgentSession session = sessionManager.register(registration);

        assertNotNull(session);
        assertEquals("agent-001", session.getAgentId());
        assertNotNull(session.getSessionToken());
        assertEquals(AgentStatus.ONLINE, session.getStatus());
        assertTrue(session.isValid());
    }

    @Test
    void testAuthenticate() {
        AgentRegistration registration = new AgentRegistration();
        registration.setAgentId("agent-002");
        registration.setCredentials("correct-password");

        sessionManager.register(registration);

        AgentSession session = sessionManager.authenticate("agent-002", "correct-password");
        assertNotNull(session);

        AgentSession failedSession = sessionManager.authenticate("agent-002", "wrong-password");
        assertNull(failedSession);
    }

    @Test
    void testInvalidate() {
        AgentRegistration registration = new AgentRegistration();
        registration.setAgentId("agent-003");
        registration.setCredentials("key");

        AgentSession session = sessionManager.register(registration);
        String token = session.getSessionToken();

        sessionManager.invalidate("agent-003");

        assertNull(sessionManager.getSession("agent-003"));
        assertFalse(sessionManager.isValid(token));
    }

    @Test
    void testHeartbeat() {
        AgentRegistration registration = new AgentRegistration();
        registration.setAgentId("agent-004");

        AgentSession session = sessionManager.register(registration);
        long initialHeartbeat = session.getLastHeartbeat();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        sessionManager.heartbeat("agent-004");

        AgentSession updatedSession = sessionManager.getSession("agent-004");
        assertNotNull(updatedSession);
        assertTrue(updatedSession.getLastHeartbeat() > initialHeartbeat);
    }

    @Test
    void testUpdateStatus() {
        AgentRegistration registration = new AgentRegistration();
        registration.setAgentId("agent-005");

        sessionManager.register(registration);

        sessionManager.updateStatus("agent-005", AgentStatus.BUSY);

        AgentSession session = sessionManager.getSession("agent-005");
        assertNotNull(session);
        assertEquals(AgentStatus.BUSY, session.getStatus());
    }

    @Test
    void testIsValidToken() {
        AgentRegistration registration = new AgentRegistration();
        registration.setAgentId("agent-006");

        AgentSession session = sessionManager.register(registration);
        String token = session.getSessionToken();

        assertTrue(sessionManager.isValid(token));
        assertFalse(sessionManager.isValid("invalid-token"));
    }

    @Test
    void testDuplicateRegistration() {
        AgentRegistration registration1 = new AgentRegistration();
        registration1.setAgentId("agent-007");
        registration1.setCredentials("key1");

        AgentRegistration registration2 = new AgentRegistration();
        registration2.setAgentId("agent-007");
        registration2.setCredentials("key2");

        AgentSession session1 = sessionManager.register(registration1);
        AgentSession session2 = sessionManager.register(registration2);

        assertEquals(1, ((AgentSessionManagerImpl) sessionManager).getSessionCount());
        assertNotEquals(session1.getSessionToken(), session2.getSessionToken());
    }
}
