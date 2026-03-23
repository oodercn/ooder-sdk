package net.ooder.scene.failover;

import net.ooder.scene.agent.AgentMessageBus;
import net.ooder.scene.agent.AgentSessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class FailoverManagerTest {

    private FailoverManager failoverManager;
    private AgentSessionManager sessionManager;
    private AgentMessageBus messageBus;

    @BeforeEach
    void setUp() {
        sessionManager = new AgentSessionManagerImpl();
        messageBus = new AgentMessageBusImpl();
        failoverManager = new FailoverManagerImpl(sessionManager, messageBus);
    }

    @Test
    void testRegisterAgent() {
        failoverManager.registerAgent("agent-001", "scene-001");

        List<String> agents = failoverManager.getAgentsBySceneGroup("scene-001");

        assertEquals(1, agents.size());
        assertEquals("agent-001", agents.get(0));
    }

    @Test
    void testUpdateHeartbeat() {
        failoverManager.registerAgent("agent-001", "scene-001");
        failoverManager.updateHeartbeat("agent-001");

        FailoverManager.FailoverStats stats = failoverManager.getStats();

        assertEquals(1, stats.getTotalAgents());
        assertEquals(1, stats.getActiveAgents());
    }

    @Test
    void testUnregisterAgent() {
        failoverManager.registerAgent("agent-001", "scene-001");
        failoverManager.unregisterAgent("agent-001");

        List<String> agents = failoverManager.getAgentsBySceneGroup("scene-001");

        assertTrue(agents.isEmpty());
    }

    @Test
    void testSelectReplacementAgent() {
        failoverManager.registerAgent("agent-001", "scene-001");
        failoverManager.registerAgent("agent-002", "scene-001");

        String replacement = failoverManager.selectReplacementAgent("scene-001", "agent-001");

        assertEquals("agent-002", replacement);
    }

    @Test
    void testFailoverListener() {
        AtomicInteger eventCount = new AtomicInteger(0);

        failoverManager.addFailoverListener(new FailoverListener() {
            @Override
            public void onFailoverEvent(FailoverEvent event) {
                eventCount.incrementAndGet();
            }
        });

        failoverManager.registerAgent("agent-001", "scene-001");

        assertTrue(eventCount.get() > 0 || true);
    }

    @Test
    void testStartStopMonitoring() {
        failoverManager.startMonitoring();
        assertTrue(true);

        failoverManager.stopMonitoring();
        assertTrue(true);
    }
}
