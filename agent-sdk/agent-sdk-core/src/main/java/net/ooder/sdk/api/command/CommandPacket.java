package net.ooder.sdk.api.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandPacket {

    private String packetId;
    private CommandHeader header;
    private Map<String, Object> payload;
    private long timestamp;
    private String source;
    private String target;
    private CommandDirection direction;
    private String parentCommandId;
    private List<String> childCommandIds = new ArrayList<String>();
    private boolean rollbackable;
    private String sceneId;
    private String domainId;
    private int retryCount;
    private int priority;
    private int timeout;
    private long createdTime;
    private long executedTime;

    private String llmIntent;
    private String reasoningChain;
    private String a2aContext;
    private ContextLevel contextLevel;
    private TokenUsage tokenUsage;
    private String originalUserInput;
    private String expectedResult;

    public CommandPacket() {
        this.packetId = "pkt_" + System.currentTimeMillis();
        this.timestamp = System.currentTimeMillis();
        this.createdTime = System.currentTimeMillis();
        this.header = new CommandHeader();
        this.rollbackable = false;
        this.retryCount = 0;
        this.contextLevel = ContextLevel.EXECUTION;
    }

    public static CommandPacket of(String protocolType, String commandType) {
        CommandPacket packet = new CommandPacket();
        packet.getHeader().setProtocolType(protocolType);
        packet.getHeader().setCommandType(commandType);
        return packet;
    }

    public static CommandPacket of(String protocolType, String commandType, Map<String, Object> payload) {
        CommandPacket packet = of(protocolType, commandType);
        packet.setPayload(payload);
        return packet;
    }

    public static CommandPacket llmCommand(String llmIntent, String reasoningChain) {
        CommandPacket packet = new CommandPacket();
        packet.setLlmIntent(llmIntent);
        packet.setReasoningChain(reasoningChain);
        packet.setDirection(CommandDirection.NORTHBOUND);
        packet.setContextLevel(ContextLevel.SESSION);
        return packet;
    }

    public static CommandBuilder builder() {
        return CommandBuilder.create();
    }

    public String getPacketId() { return packetId; }
    public void setPacketId(String packetId) { this.packetId = packetId; }

    public CommandHeader getHeader() { return header; }
    public void setHeader(CommandHeader header) { this.header = header; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public CommandDirection getDirection() { return direction; }
    public void setDirection(CommandDirection direction) { this.direction = direction; }

    public String getParentCommandId() { return parentCommandId; }
    public void setParentCommandId(String parentCommandId) { this.parentCommandId = parentCommandId; }

    public List<String> getChildCommandIds() { return childCommandIds; }
    public void setChildCommandIds(List<String> childCommandIds) { this.childCommandIds = childCommandIds; }

    public void addChildCommandId(String commandId) { this.childCommandIds.add(commandId); }

    public boolean isRollbackable() { return rollbackable; }
    public void setRollbackable(boolean rollbackable) { this.rollbackable = rollbackable; }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getDomainId() { return domainId; }
    public void setDomainId(String domainId) { this.domainId = domainId; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }

    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }

    public long getExecutedTime() { return executedTime; }
    public void setExecutedTime(long executedTime) { this.executedTime = executedTime; }

    public String getLlmIntent() { return llmIntent; }
    public void setLlmIntent(String llmIntent) { this.llmIntent = llmIntent; }

    public String getReasoningChain() { return reasoningChain; }
    public void setReasoningChain(String reasoningChain) { this.reasoningChain = reasoningChain; }

    public String getA2aContext() { return a2aContext; }
    public void setA2aContext(String a2aContext) { this.a2aContext = a2aContext; }

    public ContextLevel getContextLevel() { return contextLevel; }
    public void setContextLevel(ContextLevel contextLevel) { this.contextLevel = contextLevel; }

    public TokenUsage getTokenUsage() { return tokenUsage; }
    public void setTokenUsage(TokenUsage tokenUsage) { this.tokenUsage = tokenUsage; }

    public String getOriginalUserInput() { return originalUserInput; }
    public void setOriginalUserInput(String originalUserInput) { this.originalUserInput = originalUserInput; }

    public String getExpectedResult() { return expectedResult; }
    public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }

    public boolean isNorthbound() { return direction == CommandDirection.NORTHBOUND; }

    public boolean isSouthbound() { return direction == CommandDirection.SOUTHBOUND; }

    public boolean hasParent() { return parentCommandId != null && !parentCommandId.isEmpty(); }

    public boolean hasChildren() { return !childCommandIds.isEmpty(); }

    public boolean isLlmGenerated() {
        return llmIntent != null && !llmIntent.isEmpty();
    }

    public int getReasoningStepCount() {
        if (reasoningChain == null || reasoningChain.isEmpty()) {
            return 0;
        }
        return reasoningChain.split("step").length - 1;
    }

    public void recordTokenUsage(int promptTokens, int completionTokens, String model) {
        this.tokenUsage = new TokenUsage(promptTokens, completionTokens, model);
    }

    public static class CommandHeader {
        private String protocolType;
        private String commandType;
        private String version;
        private int priority;
        private boolean requiresAck;
        private long timeout;

        public CommandHeader() {
            this.version = "1.0";
            this.priority = 5;
            this.requiresAck = true;
            this.timeout = 30000;
        }

        public String getProtocolType() { return protocolType; }
        public void setProtocolType(String protocolType) { this.protocolType = protocolType; }

        public String getCommandType() { return commandType; }
        public void setCommandType(String commandType) { this.commandType = commandType; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }

        public boolean isRequiresAck() { return requiresAck; }
        public void setRequiresAck(boolean requiresAck) { this.requiresAck = requiresAck; }

        public long getTimeout() { return timeout; }
        public void setTimeout(long timeout) { this.timeout = timeout; }
    }

    public enum ContextLevel {
        GLOBAL,
        DOMAIN,
        SCENE,
        SESSION,
        EXECUTION
    }

    public static class TokenUsage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
        private String model;
        private long timestamp;

        public TokenUsage() {
            this.timestamp = System.currentTimeMillis();
        }

        public TokenUsage(int promptTokens, int completionTokens, String model) {
            this();
            this.promptTokens = promptTokens;
            this.completionTokens = completionTokens;
            this.totalTokens = promptTokens + completionTokens;
            this.model = model;
        }

        public int getPromptTokens() { return promptTokens; }
        public void setPromptTokens(int promptTokens) { this.promptTokens = promptTokens; }

        public int getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(int completionTokens) { this.completionTokens = completionTokens; }

        public int getTotalTokens() { return totalTokens; }
        public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

        @Override
        public String toString() {
            return "TokenUsage{" +
                    "promptTokens=" + promptTokens +
                    ", completionTokens=" + completionTokens +
                    ", totalTokens=" + totalTokens +
                    ", model='" + model + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CommandPacket{" +
                "packetId='" + packetId + '\'' +
                ", protocolType='" + (header != null ? header.getProtocolType() : null) + '\'' +
                ", commandType='" + (header != null ? header.getCommandType() : null) + '\'' +
                ", direction=" + direction +
                ", llmIntent='" + llmIntent + '\'' +
                ", contextLevel=" + contextLevel +
                ", timestamp=" + timestamp +
                '}';
    }
}
