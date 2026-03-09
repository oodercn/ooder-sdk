package net.ooder.scene.llm.context;

import net.ooder.scene.llm.command.ContextReference;
import net.ooder.scene.llm.command.ContextTransfer;
import net.ooder.scene.llm.command.ContextTransfer.TransferMode;
import net.ooder.scene.llm.command.ContextTransfer.ContextPart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 上下文传递处理器
 * 
 * <p>处理 LLM 场景上下文在 A2A 命令中的传递。</p>
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class ContextTransferHandler {

    private static final Logger log = LoggerFactory.getLogger(ContextTransferHandler.class);

    private final LlmContextRegistry contextRegistry;

    public ContextTransferHandler(LlmContextRegistry contextRegistry) {
        this.contextRegistry = contextRegistry;
    }

    public ContextTransfer prepareTransfer(
            LlmSceneContext sourceContext, 
            TransferMode mode,
            Set<ContextPart> includedParts) {
        
        if (sourceContext == null) {
            throw new IllegalArgumentException("Source context must not be null");
        }
        
        ContextTransfer.Builder builder = ContextTransfer.builder()
            .sourceContextId(sourceContext.getContextId())
            .transferMode(mode != null ? mode : TransferMode.REFERENCE);
        
        switch (mode != null ? mode : TransferMode.REFERENCE) {
            case FULL:
                builder.serializedContext(serializeContext(sourceContext, null));
                break;
                
            case REFERENCE:
                builder.contextReference(createReference(sourceContext));
                break;
                
            case DELTA:
                builder.contextDelta(extractDelta(sourceContext));
                break;
                
            case SELECTIVE:
                builder.serializedContext(serializeContext(sourceContext, includedParts))
                       .includedParts(includedParts);
                break;
        }
        
        log.debug("Prepared context transfer: contextId={}, mode={}", 
            sourceContext.getContextId(), mode);
        
        return builder.build();
    }

    public LlmSceneContext receiveTransfer(
            ContextTransfer transfer, 
            String targetSceneId) {
        
        if (transfer == null) {
            throw new IllegalArgumentException("Transfer must not be null");
        }
        
        TransferMode mode = transfer.getTransferMode();
        if (mode == null) {
            mode = TransferMode.REFERENCE;
        }
        
        LlmSceneContext context;
        
        switch (mode) {
            case FULL:
            case SELECTIVE:
                context = deserializeAndRegister(transfer.getSerializedContext(), targetSceneId);
                break;
                
            case REFERENCE:
                context = resolveReference(transfer.getContextReference());
                break;
                
            case DELTA:
                context = applyDelta(transfer, targetSceneId);
                break;
                
            default:
                throw new IllegalArgumentException("Unknown transfer mode: " + mode);
        }
        
        log.debug("Received context transfer: contextId={}, mode={}, targetSceneId={}", 
            context != null ? context.getContextId() : null, mode, targetSceneId);
        
        return context;
    }

    public void mergeContext(
            LlmSceneContext target, 
            LlmSceneContext source,
            MergeStrategy strategy) {
        
        if (target == null || source == null) {
            return;
        }
        
        switch (strategy) {
            case SOURCE_PRIORITY:
                mergeWithSourcePriority(target, source);
                break;
            case TARGET_PRIORITY:
                mergeWithTargetPriority(target, source);
                break;
            case DEEP_MERGE:
                deepMerge(target, source);
                break;
        }
        
        contextRegistry.update(target);
    }

    private String serializeContext(LlmSceneContext context, Set<ContextPart> includedParts) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"contextId\":\"").append(escapeJson(context.getContextId())).append("\",");
            sb.append("\"sceneId\":\"").append(escapeJson(context.getSceneId())).append("\",");
            sb.append("\"agentId\":\"").append(escapeJson(context.getAgentId())).append("\",");
            sb.append("\"sandboxId\":\"").append(escapeJson(context.getSandboxId())).append("\",");
            sb.append("\"createdAt\":").append(context.getCreatedAt()).append(",");
            sb.append("\"lastAccessedAt\":").append(context.getLastAccessedAt());
            
            if (context.getUserContext() != null) {
                sb.append(",\"userContext\":").append(serializeUserContext(context.getUserContext()));
            }
            
            if (context.getNlpContext() != null) {
                sb.append(",\"nlpContext\":").append(serializeNlpContext(context.getNlpContext()));
            }
            
            if (context.getKnowledgeContext() != null) {
                sb.append(",\"knowledgeContext\":").append(serializeKnowledgeContext(context.getKnowledgeContext()));
            }
            
            if (context.getSecurityContext() != null) {
                sb.append(",\"securityContext\":").append(serializeSecurityContext(context.getSecurityContext()));
            }
            
            sb.append("}");
            
            return sb.toString();
            
        } catch (Exception e) {
            log.error("Failed to serialize context: {}", e.getMessage());
            throw new ContextTransferException("Failed to serialize context", e);
        }
    }

    private ContextReference createReference(LlmSceneContext context) {
        return ContextReference.builder()
            .contextId(context.getContextId())
            .sceneId(context.getSceneId())
            .agentId(context.getAgentId())
            .createdAt(context.getCreatedAt())
            .checksum(computeChecksum(context))
            .build();
    }

    private LlmSceneContext resolveReference(ContextReference reference) {
        if (reference == null || reference.getContextId() == null) {
            throw new ContextTransferException("Invalid context reference");
        }
        
        LlmSceneContext context = contextRegistry.get(reference.getContextId());
        if (context == null) {
            throw new ContextTransferException("Context not found: " + reference.getContextId());
        }
        
        if (reference.getChecksum() != null) {
            String expectedChecksum = computeChecksum(context);
            if (!reference.getChecksum().equals(expectedChecksum)) {
                log.warn("Context checksum mismatch: expected={}, actual={}", 
                    expectedChecksum, reference.getChecksum());
            }
        }
        
        return context;
    }

    private Map<String, Object> extractDelta(LlmSceneContext context) {
        Map<String, Object> delta = new HashMap<>();
        if (context.getExtendedAttributes() != null) {
            delta.putAll(context.getExtendedAttributes());
        }
        return delta;
    }

    private LlmSceneContext deserializeAndRegister(String serialized, String targetSceneId) {
        if (serialized == null || serialized.isEmpty()) {
            throw new ContextTransferException("Serialized context is empty");
        }
        
        try {
            LlmSceneContext context = parseJsonContext(serialized);
            context.setSceneId(targetSceneId);
            context.setContextId(generateNewContextId());
            contextRegistry.register(context);
            return context;
        } catch (Exception e) {
            throw new ContextTransferException("Failed to deserialize context", e);
        }
    }

    private LlmSceneContext applyDelta(ContextTransfer transfer, String targetSceneId) {
        LlmSceneContext targetContext = contextRegistry.getBySceneId(targetSceneId);
        if (targetContext == null) {
            throw new ContextTransferException("Target context not found for scene: " + targetSceneId);
        }
        
        Map<String, Object> delta = transfer.getContextDelta();
        if (delta != null) {
            delta.forEach((key, value) -> targetContext.setExtendedAttribute(key, value));
        }
        
        contextRegistry.update(targetContext);
        return targetContext;
    }

    private void mergeWithSourcePriority(LlmSceneContext target, LlmSceneContext source) {
        if (source.getUserContext() != null) {
            target.setUserContext(source.getUserContext());
        }
        if (source.getNlpContext() != null) {
            target.setNlpContext(source.getNlpContext());
        }
        if (source.getKnowledgeContext() != null) {
            target.setKnowledgeContext(source.getKnowledgeContext());
        }
        if (source.getSecurityContext() != null) {
            target.setSecurityContext(source.getSecurityContext());
        }
        if (source.getExtendedAttributes() != null) {
            source.getExtendedAttributes().forEach(target::setExtendedAttribute);
        }
    }

    private void mergeWithTargetPriority(LlmSceneContext target, LlmSceneContext source) {
        if (target.getUserContext() == null && source.getUserContext() != null) {
            target.setUserContext(source.getUserContext());
        }
        if (target.getNlpContext() == null && source.getNlpContext() != null) {
            target.setNlpContext(source.getNlpContext());
        }
        if (target.getKnowledgeContext() == null && source.getKnowledgeContext() != null) {
            target.setKnowledgeContext(source.getKnowledgeContext());
        }
        if (target.getSecurityContext() == null && source.getSecurityContext() != null) {
            target.setSecurityContext(source.getSecurityContext());
        }
        if (source.getExtendedAttributes() != null) {
            source.getExtendedAttributes().forEach((key, value) -> {
                if (target.getExtendedAttribute(key) == null) {
                    target.setExtendedAttribute(key, value);
                }
            });
        }
    }

    private void deepMerge(LlmSceneContext target, LlmSceneContext source) {
        mergeWithTargetPriority(target, source);
    }

    private String computeChecksum(LlmSceneContext context) {
        try {
            String data = context.getContextId() + ":" + context.getCreatedAt();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String generateNewContextId() {
        return "ctx-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String serializeUserContext(UserContext ctx) {
        if (ctx == null) return "null";
        return "{\"userId\":\"" + escapeJson(ctx.getUserId()) + "\"" +
               ",\"userName\":\"" + escapeJson(ctx.getUserName()) + "\"" +
               ",\"isLlmUser\":" + ctx.isLlmUser() + "}";
    }

    private String serializeNlpContext(NlpContext ctx) {
        if (ctx == null) return "null";
        return "{\"nlpContextId\":\"" + escapeJson(ctx.getNlpContextId()) + "\"" +
               ",\"componentType\":\"" + escapeJson(ctx.getComponentType()) + "\"" +
               ",\"moduleViewType\":\"" + escapeJson(ctx.getModuleViewType()) + "\"}";
    }

    private String serializeKnowledgeContext(KnowledgeContext ctx) {
        if (ctx == null) return "null";
        return "{\"knowledgeBaseId\":\"" + escapeJson(ctx.getKnowledgeBaseId()) + "\"" +
               ",\"maxResults\":" + ctx.getMaxResults() + "}";
    }

    private String serializeSecurityContext(SecurityContext ctx) {
        if (ctx == null) return "null";
        return "{\"sessionId\":\"" + escapeJson(ctx.getSessionId()) + "\"" +
               ",\"securityLevel\":\"" + escapeJson(ctx.getSecurityLevel()) + "\"" +
               ",\"auditEnabled\":" + ctx.isAuditEnabled() + "}";
    }

    private LlmSceneContext parseJsonContext(String json) {
        LlmSceneContext context = new LlmSceneContext();
        
        String contextId = extractJsonValue(json, "contextId");
        if (contextId != null) {
            context.setContextId(contextId);
        }
        
        String sceneId = extractJsonValue(json, "sceneId");
        if (sceneId != null) {
            context.setSceneId(sceneId);
        }
        
        String agentId = extractJsonValue(json, "agentId");
        if (agentId != null) {
            context.setAgentId(agentId);
        }
        
        String sandboxId = extractJsonValue(json, "sandboxId");
        if (sandboxId != null) {
            context.setSandboxId(sandboxId);
        }
        
        return context;
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int start = json.indexOf(searchKey);
        if (start < 0) {
            searchKey = "\"" + key + "\":";
            start = json.indexOf(searchKey);
            if (start < 0) return null;
            start += searchKey.length();
            int end = json.indexOf(",", start);
            if (end < 0) end = json.indexOf("}", start);
            if (end < 0) return null;
            return json.substring(start, end).trim();
        }
        start += searchKey.length();
        int end = json.indexOf("\"", start);
        if (end < 0) return null;
        return json.substring(start, end);
    }

    public enum MergeStrategy {
        SOURCE_PRIORITY,
        TARGET_PRIORITY,
        DEEP_MERGE
    }
}
