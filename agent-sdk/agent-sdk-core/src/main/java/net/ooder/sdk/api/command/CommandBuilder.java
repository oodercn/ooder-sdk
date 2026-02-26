package net.ooder.sdk.api.command;

import java.util.Map;

public class CommandBuilder {
    private CommandPacket packet;

    private CommandBuilder() {
        this.packet = new CommandPacket();
    }

    public static CommandBuilder create() {
        return new CommandBuilder();
    }

    public CommandBuilder packetId(String packetId) {
        packet.setPacketId(packetId);
        return this;
    }

    public CommandBuilder protocolType(String protocolType) {
        packet.getHeader().setProtocolType(protocolType);
        return this;
    }

    public CommandBuilder commandType(String commandType) {
        packet.getHeader().setCommandType(commandType);
        return this;
    }

    public CommandBuilder payload(Map<String, Object> payload) {
        packet.setPayload(payload);
        return this;
    }

    public CommandBuilder source(String source) {
        packet.setSource(source);
        return this;
    }

    public CommandBuilder target(String target) {
        packet.setTarget(target);
        return this;
    }

    public CommandBuilder direction(CommandDirection direction) {
        packet.setDirection(direction);
        return this;
    }

    public CommandBuilder northbound() {
        packet.setDirection(CommandDirection.NORTHBOUND);
        return this;
    }

    public CommandBuilder southbound() {
        packet.setDirection(CommandDirection.SOUTHBOUND);
        return this;
    }

    public CommandBuilder priority(int priority) {
        packet.getHeader().setPriority(priority);
        return this;
    }

    public CommandBuilder timeout(int timeout) {
        packet.getHeader().setTimeout(timeout);
        return this;
    }

    public CommandBuilder requiresAck(boolean requiresAck) {
        packet.getHeader().setRequiresAck(requiresAck);
        return this;
    }

    public CommandBuilder sceneId(String sceneId) {
        packet.setSceneId(sceneId);
        return this;
    }

    public CommandBuilder domainId(String domainId) {
        packet.setDomainId(domainId);
        return this;
    }

    public CommandBuilder parentCommandId(String parentCommandId) {
        packet.setParentCommandId(parentCommandId);
        return this;
    }

    public CommandBuilder rollbackable(boolean rollbackable) {
        packet.setRollbackable(rollbackable);
        return this;
    }

    public CommandBuilder llmIntent(String llmIntent) {
        packet.setLlmIntent(llmIntent);
        return this;
    }

    public CommandBuilder reasoningChain(String reasoningChain) {
        packet.setReasoningChain(reasoningChain);
        return this;
    }

    public CommandBuilder a2aContext(String a2aContext) {
        packet.setA2aContext(a2aContext);
        return this;
    }

    public CommandBuilder contextLevel(CommandPacket.ContextLevel contextLevel) {
        packet.setContextLevel(contextLevel);
        return this;
    }

    public CommandBuilder originalUserInput(String originalUserInput) {
        packet.setOriginalUserInput(originalUserInput);
        return this;
    }

    public CommandBuilder expectedResult(String expectedResult) {
        packet.setExpectedResult(expectedResult);
        return this;
    }

    public CommandBuilder tokenUsage(CommandPacket.TokenUsage tokenUsage) {
        packet.setTokenUsage(tokenUsage);
        return this;
    }

    public CommandPacket build() {
        return packet;
    }
}
