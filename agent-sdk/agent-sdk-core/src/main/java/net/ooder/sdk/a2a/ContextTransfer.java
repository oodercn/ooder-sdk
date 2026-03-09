package net.ooder.sdk.a2a;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 上下文传递
 *
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
public class ContextTransfer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String transferId;
    private String sourceContextId;
    private String targetContextId;
    private String sourceSceneId;
    private String targetSceneId;

    private A2ACommand.TransferMode transferMode;
    private Set<ContextPart> includedParts;

    private String serializedContext;       // 序列化的上下文
    private ContextReference contextReference;
    private Map<String, Object> contextDelta;

    private long createdAt;
    private long expiresAt;

    /**
     * 上下文部分
     */
    public enum ContextPart {
        SCENE_CONTEXT,
        NLP_CONTEXT,
        KNOWLEDGE_CONTEXT,
        TOOL_CONTEXT,
        SECURITY_CONTEXT
    }

    /**
     * 上下文引用
     */
    @Data
    @Builder
    public static class ContextReference implements Serializable {
        private static final long serialVersionUID = 1L;
        private String contextId;
        private String registryEndpoint;
        private String accessToken;
    }
}
