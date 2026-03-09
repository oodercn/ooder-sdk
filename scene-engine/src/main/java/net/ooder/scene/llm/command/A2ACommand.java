package net.ooder.scene.llm.command;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * A2A 命令消息
 * 
 * <p>Agent-to-Agent 通信的核心消息结构，支持上下文传递。</p>
 * 
 * <p>设计原则：</p>
 * <ul>
 *   <li>统一协议：所有 Agent 间通信使用统一消息格式</li>
 *   <li>安全传输：包含安全信息用于验证</li>
 *   <li>上下文传递：支持 LLM 场景上下文的传递</li>
 * </ul>
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class A2ACommand implements Serializable {

    private static final long serialVersionUID = 2L;

    private CommandHeader header;
    private CommandBody body;
    private CommandMetadata metadata;
    private SecurityInfo security;
    private ContextTransfer contextTransfer;

    public A2ACommand() {
        this.metadata = new CommandMetadata();
        this.header = new CommandHeader();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getCommandId() {
        return header != null ? header.getCommandId() : null;
    }
    
    public A2ACommandType getCommandType() {
        return header != null ? header.getCommandType() : null;
    }
    
    public CommandHeader getHeader() { return header; }
    public void setHeader(CommandHeader header) { this.header = header; }
    
    public CommandBody getBody() { return body; }
    public void setBody(CommandBody body) { this.body = body; }
    
    public CommandMetadata getMetadata() { return metadata; }
    public void setMetadata(CommandMetadata metadata) { this.metadata = metadata; }
    
    public SecurityInfo getSecurity() { return security; }
    public void setSecurity(SecurityInfo security) { this.security = security; }
    
    public ContextTransfer getContextTransfer() { return contextTransfer; }
    public void setContextTransfer(ContextTransfer contextTransfer) { this.contextTransfer = contextTransfer; }
    
    public static class Builder {
        private A2ACommand command = new A2ACommand();
        
        public Builder header(CommandHeader header) {
            command.setHeader(header);
            return this;
        }
        
        public Builder body(CommandBody body) {
            command.setBody(body);
            return this;
        }
        
        public Builder metadata(CommandMetadata metadata) {
            command.setMetadata(metadata);
            return this;
        }
        
        public Builder security(SecurityInfo security) {
            command.setSecurity(security);
            return this;
        }
        
        public Builder contextTransfer(ContextTransfer contextTransfer) {
            command.setContextTransfer(contextTransfer);
            return this;
        }
        
        public A2ACommand build() {
            return command;
        }
    }
}
