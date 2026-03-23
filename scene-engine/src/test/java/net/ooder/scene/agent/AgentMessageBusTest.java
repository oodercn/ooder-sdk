package net.ooder.scene.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class AgentMessageBusTest {

    private AgentMessageBus messageBus;

    @BeforeEach
    void setUp() {
        messageBus = new AgentMessageBusImpl();
    }

    @Test
    void testSendMessage() {
        AgentMessage message = AgentMessage.builder()
                .from("agent-001")
                .to("agent-002")
                .type(MessageType.TASK_DELEGATE)
                .payloadItem("task", "analyze-data")
                .build();

        String messageId = messageBus.send(message);

        assertNotNull(messageId);
        assertEquals(1, messageBus.getPendingCount("agent-002"));
    }

    @Test
    void testReceiveMessages() {
        AgentMessage message1 = AgentMessage.builder()
                .from("agent-001")
                .to("agent-002")
                .type(MessageType.TASK_DELEGATE)
                .build();

        AgentMessage message2 = AgentMessage.builder()
                .from("agent-001")
                .to("agent-002")
                .type(MessageType.STATUS_UPDATE)
                .build();

        messageBus.send(message1);
        messageBus.send(message2);

        List<AgentMessage> messages = messageBus.receive("agent-002");

        assertEquals(2, messages.size());
    }

    @Test
    void testAcknowledgeMessage() {
        AgentMessage message = AgentMessage.builder()
                .from("agent-001")
                .to("agent-002")
                .type(MessageType.TASK_DELEGATE)
                .build();

        String messageId = messageBus.send(message);

        assertEquals(1, messageBus.getPendingCount("agent-002"));

        messageBus.acknowledge("agent-002", messageId);

        assertEquals(0, messageBus.getPendingCount("agent-002"));
    }

    @Test
    void testSubscribeHandler() {
        AtomicReference<AgentMessage> receivedMessage = new AtomicReference<>();

        messageBus.subscribe("agent-002", msg -> receivedMessage.set(msg));

        AgentMessage message = AgentMessage.builder()
                .from("agent-001")
                .to("agent-002")
                .type(MessageType.TASK_DELEGATE)
                .build();

        messageBus.send(message);

        assertNotNull(receivedMessage.get());
        assertEquals("agent-001", receivedMessage.get().getFromAgent());
    }

    @Test
    void testUnsubscribeHandler() {
        AtomicReference<AgentMessage> receivedMessage = new AtomicReference<>();

        messageBus.subscribe("agent-002", msg -> receivedMessage.set(msg));
        messageBus.unsubscribe("agent-002");

        AgentMessage message = AgentMessage.builder()
                .from("agent-001")
                .to("agent-002")
                .type(MessageType.TASK_DELEGATE)
                .build();

        messageBus.send(message);

        assertNull(receivedMessage.get());
    }

    @Test
    void testClearMessages() {
        AgentMessage message = AgentMessage.builder()
                .from("agent-001")
                .to("agent-002")
                .type(MessageType.TASK_DELEGATE)
                .build();

        messageBus.send(message);

        assertEquals(1, messageBus.getPendingCount("agent-002"));

        messageBus.clearMessages("agent-002");

        assertEquals(0, messageBus.getPendingCount("agent-002"));
    }

    @Test
    void testPriorityOrdering() {
        AgentMessage lowPriority = AgentMessage.builder()
                .from("agent-001")
                .to("agent-002")
                .type(MessageType.STATUS_UPDATE)
                .priority(1)
                .build();

        AgentMessage highPriority = AgentMessage.builder()
                .from("agent-001")
                .to("agent-002")
                .type(MessageType.TASK_DELEGATE)
                .priority(10)
                .build();

        messageBus.send(lowPriority);
        messageBus.send(highPriority);

        List<AgentMessage> messages = messageBus.receive("agent-002");

        assertEquals(MessageType.TASK_DELEGATE, messages.get(0).getType());
    }

    @Test
    void testExpiredMessage() {
        AgentMessage message = AgentMessage.builder()
                .from("agent-001")
                .to("agent-002")
                .type(MessageType.TASK_DELEGATE)
                .expireIn(-1000)
                .build();

        messageBus.send(message);

        List<AgentMessage> messages = messageBus.receive("agent-002");

        assertTrue(messages.isEmpty());
    }
}
