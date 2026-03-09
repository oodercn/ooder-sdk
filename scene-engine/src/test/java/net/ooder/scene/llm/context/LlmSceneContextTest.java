package net.ooder.scene.llm.context;

import net.ooder.scene.llm.command.*;
import net.ooder.scene.llm.command.ContextTransfer.TransferMode;
import net.ooder.scene.llm.command.ContextTransfer.ContextPart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LLM 场景上下文测试
 *
 * @author Ooder Team
 * @since 2.4.0
 */
class LlmSceneContextTest {

    private LlmContextRegistry contextRegistry;
    private ContextTransferHandler transferHandler;

    @BeforeEach
    void setUp() {
        contextRegistry = new LlmContextRegistry();
        transferHandler = new ContextTransferHandler(contextRegistry);
    }

    @Test
    void testCreateLlmSceneContext() {
        LlmSceneContext context = LlmSceneContext.builder()
            .sceneId("test-scene")
            .agentId("test-agent")
            .build();
        
        assertNotNull(context.getContextId());
        assertEquals("test-scene", context.getSceneId());
        assertEquals("test-agent", context.getAgentId());
        assertNotNull(context.getCreatedAt());
        assertNotNull(context.getLastAccessedAt());
    }

    @Test
    void testContextWithUserContext() {
        UserContext userContext = UserContext.builder()
            .userId("user-001")
            .userName("张三")
            .domainId("domain-001")
            .build();
        
        LlmSceneContext context = LlmSceneContext.builder()
            .sceneId("test-scene")
            .userContext(userContext)
            .build();
        
        assertNotNull(context.getUserContext());
        assertEquals("user-001", context.getUserContext().getUserId());
        assertEquals("张三", context.getUserContext().getUserName());
    }

    @Test
    void testContextWithLlmUser() {
        UserContext userContext = UserContext.ofLlmUser("llm-user-001", "user-001");
        
        assertTrue(userContext.isLlmUser());
        assertEquals("llm-user-001", userContext.getLlmUserId());
        assertEquals("user-001", userContext.getUserId());
    }

    @Test
    void testContextWithNlpContext() {
        NlpComponentContext componentContext = NlpComponentContext.builder()
            .componentId("form-001")
            .componentType("ClassForm")
            .property("dataUrl", "/api/candidates")
            .active(true)
            .build();
        
        NlpContext nlpContext = NlpContext.builder()
            .componentType("ClassForm")
            .moduleViewType("FORMCONFIG")
            .componentContext(componentContext)
            .expressionVariable("candidateId", "C001")
            .build();
        
        LlmSceneContext context = LlmSceneContext.builder()
            .sceneId("test-scene")
            .nlpContext(nlpContext)
            .build();
        
        assertNotNull(context.getNlpContext());
        assertEquals("ClassForm", context.getNlpContext().getComponentType());
        assertEquals("FORMCONFIG", context.getNlpContext().getModuleViewType());
        
        NlpComponentContext retrieved = context.getNlpContext().getComponentContext("form-001");
        assertNotNull(retrieved);
        assertEquals("ClassForm", retrieved.getComponentType());
        assertTrue(retrieved.isActive());
    }

    @Test
    void testContextWithKnowledgeContext() {
        KnowledgeContext knowledgeContext = KnowledgeContext.builder()
            .knowledgeBaseId("kb-recruitment")
            .knowledgeBaseType("PROFESSIONAL")
            .accessibleKnowledgeBase("kb-general")
            .maxResults(10)
            .similarityThreshold(0.8f)
            .build();
        
        LlmSceneContext context = LlmSceneContext.builder()
            .sceneId("test-scene")
            .knowledgeContext(knowledgeContext)
            .build();
        
        assertNotNull(context.getKnowledgeContext());
        assertEquals("kb-recruitment", context.getKnowledgeContext().getKnowledgeBaseId());
        assertTrue(context.getKnowledgeContext().hasAccessTo("kb-general"));
        assertFalse(context.getKnowledgeContext().hasAccessTo("kb-other"));
    }

    @Test
    void testContextWithSecurityContext() {
        SecurityContext securityContext = SecurityContext.builder()
            .sessionId("session-001")
            .traceId("trace-001")
            .securityLevel("HIGH")
            .auditEnabled(true)
            .allowedOperation("llm.execute")
            .allowedOperation("kb.search")
            .build();
        
        LlmSceneContext context = LlmSceneContext.builder()
            .sceneId("test-scene")
            .securityContext(securityContext)
            .build();
        
        assertNotNull(context.getSecurityContext());
        assertTrue(context.getSecurityContext().isOperationAllowed("llm.execute"));
        assertTrue(context.getSecurityContext().isOperationAllowed("kb.search"));
        assertFalse(context.getSecurityContext().isOperationAllowed("admin.delete"));
    }

    @Test
    void testContextRegistry() {
        LlmSceneContext context = LlmSceneContext.builder()
            .sceneId("test-scene")
            .agentId("test-agent")
            .build();
        
        contextRegistry.register(context);
        
        assertTrue(contextRegistry.contains(context.getContextId()));
        
        LlmSceneContext retrieved = contextRegistry.get(context.getContextId());
        assertNotNull(retrieved);
        assertEquals(context.getContextId(), retrieved.getContextId());
        
        LlmSceneContext byScene = contextRegistry.getBySceneId("test-scene");
        assertNotNull(byScene);
        
        LlmSceneContext byAgent = contextRegistry.getByAgentId("test-agent");
        assertNotNull(byAgent);
    }

    @Test
    void testContextRegistryRemove() {
        LlmSceneContext context = LlmSceneContext.builder()
            .sceneId("test-scene")
            .agentId("test-agent")
            .build();
        
        contextRegistry.register(context);
        assertTrue(contextRegistry.contains(context.getContextId()));
        
        contextRegistry.remove(context.getContextId());
        assertFalse(contextRegistry.contains(context.getContextId()));
        assertNull(contextRegistry.getBySceneId("test-scene"));
        assertNull(contextRegistry.getByAgentId("test-agent"));
    }

    @Test
    void testContextTransferReference() {
        LlmSceneContext sourceContext = LlmSceneContext.builder()
            .sceneId("source-scene")
            .agentId("source-agent")
            .userContext(UserContext.of("user-001", "张三"))
            .build();
        
        contextRegistry.register(sourceContext);
        
        ContextTransfer transfer = transferHandler.prepareTransfer(
            sourceContext, 
            TransferMode.REFERENCE, 
            null
        );
        
        assertNotNull(transfer);
        assertEquals(TransferMode.REFERENCE, transfer.getTransferMode());
        assertEquals(sourceContext.getContextId(), transfer.getSourceContextId());
        assertNotNull(transfer.getContextReference());
        
        LlmSceneContext receivedContext = transferHandler.receiveTransfer(transfer, "target-scene");
        assertNotNull(receivedContext);
        assertEquals(sourceContext.getContextId(), receivedContext.getContextId());
    }

    @Test
    void testContextTransferFull() {
        LlmSceneContext sourceContext = LlmSceneContext.builder()
            .sceneId("source-scene")
            .agentId("source-agent")
            .userContext(UserContext.of("user-001", "张三"))
            .knowledgeContext(KnowledgeContext.builder().knowledgeBaseId("kb-001").build())
            .build();
        
        contextRegistry.register(sourceContext);
        
        ContextTransfer transfer = transferHandler.prepareTransfer(
            sourceContext, 
            TransferMode.FULL, 
            null
        );
        
        assertNotNull(transfer);
        assertEquals(TransferMode.FULL, transfer.getTransferMode());
        assertNotNull(transfer.getSerializedContext());
        
        LlmSceneContext receivedContext = transferHandler.receiveTransfer(transfer, "target-scene");
        assertNotNull(receivedContext);
        assertEquals("target-scene", receivedContext.getSceneId());
        assertNotEquals(sourceContext.getContextId(), receivedContext.getContextId());
    }

    @Test
    void testContextTransferSelective() {
        LlmSceneContext sourceContext = LlmSceneContext.builder()
            .sceneId("source-scene")
            .agentId("source-agent")
            .userContext(UserContext.of("user-001", "张三"))
            .nlpContext(NlpContext.builder().componentType("Form").build())
            .knowledgeContext(KnowledgeContext.builder().knowledgeBaseId("kb-001").build())
            .build();
        
        contextRegistry.register(sourceContext);
        
        Set<ContextPart> includedParts = new HashSet<>(Arrays.asList(
            ContextPart.USER_CONTEXT,
            ContextPart.KNOWLEDGE_CONTEXT
        ));
        
        ContextTransfer transfer = transferHandler.prepareTransfer(
            sourceContext, 
            TransferMode.SELECTIVE, 
            includedParts
        );
        
        assertNotNull(transfer);
        assertEquals(TransferMode.SELECTIVE, transfer.getTransferMode());
        assertTrue(transfer.shouldInclude(ContextPart.USER_CONTEXT));
        assertTrue(transfer.shouldInclude(ContextPart.KNOWLEDGE_CONTEXT));
        assertFalse(transfer.shouldInclude(ContextPart.NLP_CONTEXT));
    }

    @Test
    void testContextMerge() {
        LlmSceneContext targetContext = LlmSceneContext.builder()
            .sceneId("target-scene")
            .userContext(UserContext.of("user-001", "张三"))
            .build();
        
        LlmSceneContext sourceContext = LlmSceneContext.builder()
            .sceneId("source-scene")
            .knowledgeContext(KnowledgeContext.builder().knowledgeBaseId("kb-001").build())
            .build();
        
        contextRegistry.register(targetContext);
        
        transferHandler.mergeContext(targetContext, sourceContext, 
            ContextTransferHandler.MergeStrategy.SOURCE_PRIORITY);
        
        assertNotNull(targetContext.getKnowledgeContext());
        assertEquals("kb-001", targetContext.getKnowledgeContext().getKnowledgeBaseId());
    }

    @Test
    void testA2ACommand() {
        LlmSceneContext context = LlmSceneContext.builder()
            .sceneId("test-scene")
            .agentId("test-agent")
            .build();
        
        contextRegistry.register(context);
        
        ContextTransfer contextTransfer = transferHandler.prepareTransfer(
            context, TransferMode.REFERENCE, null
        );
        
        A2ACommand command = A2ACommand.builder()
            .header(CommandHeader.builder()
                .commandType(A2ACommandType.LLM_CHAT)
                .commandId("cmd-001")
                .traceId("trace-001")
                .build())
            .body(CommandBody.builder()
                .source(AgentInfo.builder()
                    .agentId("agent-source")
                    .sceneId("scene-source")
                    .build())
                .target(AgentInfo.builder()
                    .agentId("agent-target")
                    .sceneId("scene-target")
                    .build())
                .param("message", "Hello")
                .build())
            .metadata(CommandMetadata.builder()
                .priority(CommandMetadata.Priority.HIGH)
                .timeoutMs(60000)
                .build())
            .security(SecurityInfo.builder()
                .userId("user-001")
                .sessionId("session-001")
                .build())
            .contextTransfer(contextTransfer)
            .build();
        
        assertNotNull(command);
        assertEquals(A2ACommandType.LLM_CHAT, command.getCommandType());
        assertEquals("cmd-001", command.getCommandId());
        assertNotNull(command.getContextTransfer());
        assertEquals(TransferMode.REFERENCE, command.getContextTransfer().getTransferMode());
    }

    @Test
    void testA2ACommandResponse() {
        A2ACommandResponse success = A2ACommandResponse.success("cmd-001", "Result data");
        assertTrue(success.isSuccess());
        assertEquals("cmd-001", success.getHeader().getCommandId());
        
        A2ACommandResponse failure = A2ACommandResponse.failure("cmd-002", "Error message");
        assertFalse(failure.isSuccess());
        assertEquals("Error message", failure.getHeader().getErrorMessage());
        
        A2ACommandResponse timeout = A2ACommandResponse.timeout("cmd-003");
        assertFalse(timeout.isSuccess());
        assertEquals(A2ACommandResponse.ResponseStatus.TIMEOUT, timeout.getHeader().getStatus());
    }

    @Test
    void testExtendedAttributes() {
        LlmSceneContext context = new LlmSceneContext();
        
        context.setExtendedAttribute("key1", "value1");
        context.setExtendedAttribute("key2", 123);
        
        assertEquals("value1", context.getExtendedAttribute("key1"));
        assertEquals(123, context.getExtendedAttribute("key2"));
        assertNull(context.getExtendedAttribute("key3"));
        
        context.removeExtendedAttribute("key1");
        assertNull(context.getExtendedAttribute("key1"));
    }

    @Test
    void testContextTouch() throws InterruptedException {
        LlmSceneContext context = new LlmSceneContext();
        long firstAccess = context.getLastAccessedAt();
        
        Thread.sleep(10);
        context.touch();
        
        assertTrue(context.getLastAccessedAt() > firstAccess);
    }

    @Test
    void testContextStats() {
        for (int i = 0; i < 5; i++) {
            LlmSceneContext context = LlmSceneContext.builder()
                .sceneId("scene-" + i)
                .agentId("agent-" + i)
                .build();
            contextRegistry.register(context);
        }
        
        LlmContextRegistry.ContextStats stats = contextRegistry.getStats();
        assertEquals(5, stats.getTotalContexts());
        assertEquals(5, stats.getTotalScenes());
        assertEquals(5, stats.getTotalAgents());
    }
}
