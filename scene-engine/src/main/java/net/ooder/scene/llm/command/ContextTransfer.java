package net.ooder.scene.llm.command;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 上下文传递
 * 
 * <p>封装 A2A 命令中的上下文传递信息，支持多种传递模式。</p>
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class ContextTransfer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sourceContextId;
    private String targetContextId;
    private TransferMode transferMode = TransferMode.REFERENCE;
    private String serializedContext;
    private ContextReference contextReference;
    private Map<String, Object> contextDelta;
    private Set<ContextPart> includedParts;
    private Set<ContextPart> excludedParts;

    public enum TransferMode {
        FULL,
        REFERENCE,
        DELTA,
        SELECTIVE
    }
    
    public enum ContextPart {
        USER_CONTEXT,
        NLP_CONTEXT,
        KNOWLEDGE_CONTEXT,
        SECURITY_CONTEXT,
        EXTENDED_ATTRIBUTES,
        CONVERSATION_MEMORY
    }
    
    public ContextTransfer() {
        this.includedParts = new HashSet<>();
        this.excludedParts = new HashSet<>();
        this.contextDelta = new HashMap<>();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public void addIncludedPart(ContextPart part) {
        if (includedParts == null) {
            includedParts = new HashSet<>();
        }
        includedParts.add(part);
    }
    
    public void addExcludedPart(ContextPart part) {
        if (excludedParts == null) {
            excludedParts = new HashSet<>();
        }
        excludedParts.add(part);
    }
    
    public boolean shouldInclude(ContextPart part) {
        if (includedParts != null && !includedParts.isEmpty()) {
            return includedParts.contains(part);
        }
        if (excludedParts != null && !excludedParts.isEmpty()) {
            return !excludedParts.contains(part);
        }
        return true;
    }
    
    public String getSourceContextId() { return sourceContextId; }
    public void setSourceContextId(String sourceContextId) { this.sourceContextId = sourceContextId; }
    
    public String getTargetContextId() { return targetContextId; }
    public void setTargetContextId(String targetContextId) { this.targetContextId = targetContextId; }
    
    public TransferMode getTransferMode() { return transferMode; }
    public void setTransferMode(TransferMode transferMode) { this.transferMode = transferMode; }
    
    public String getSerializedContext() { return serializedContext; }
    public void setSerializedContext(String serializedContext) { this.serializedContext = serializedContext; }
    
    public ContextReference getContextReference() { return contextReference; }
    public void setContextReference(ContextReference contextReference) { this.contextReference = contextReference; }
    
    public Map<String, Object> getContextDelta() { return contextDelta; }
    public void setContextDelta(Map<String, Object> contextDelta) { this.contextDelta = contextDelta; }
    
    public Set<ContextPart> getIncludedParts() { return includedParts; }
    public void setIncludedParts(Set<ContextPart> includedParts) { this.includedParts = includedParts; }
    
    public Set<ContextPart> getExcludedParts() { return excludedParts; }
    public void setExcludedParts(Set<ContextPart> excludedParts) { this.excludedParts = excludedParts; }
    
    public static class Builder {
        private ContextTransfer transfer = new ContextTransfer();
        
        public Builder sourceContextId(String sourceContextId) {
            transfer.setSourceContextId(sourceContextId);
            return this;
        }
        
        public Builder targetContextId(String targetContextId) {
            transfer.setTargetContextId(targetContextId);
            return this;
        }
        
        public Builder transferMode(TransferMode mode) {
            transfer.setTransferMode(mode);
            return this;
        }
        
        public Builder serializedContext(String serializedContext) {
            transfer.setSerializedContext(serializedContext);
            return this;
        }
        
        public Builder contextReference(ContextReference reference) {
            transfer.setContextReference(reference);
            return this;
        }
        
        public Builder contextDelta(Map<String, Object> delta) {
            transfer.setContextDelta(delta);
            return this;
        }
        
        public Builder includePart(ContextPart part) {
            transfer.addIncludedPart(part);
            return this;
        }
        
        public Builder includedParts(Set<ContextPart> parts) {
            if (parts != null) {
                transfer.setIncludedParts(parts);
            }
            return this;
        }
        
        public Builder excludePart(ContextPart part) {
            transfer.addExcludedPart(part);
            return this;
        }
        
        public ContextTransfer build() {
            return transfer;
        }
    }
}
