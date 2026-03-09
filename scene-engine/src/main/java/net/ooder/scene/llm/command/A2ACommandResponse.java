package net.ooder.scene.llm.command;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A2A 命令响应
 *
 * @author Ooder Team
 * @since 2.4.0
 */
public class A2ACommandResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private ResponseHeader header;
    private ResponseBody body;

    public A2ACommandResponse() {
        this.header = new ResponseHeader();
        this.body = new ResponseBody();
    }
    
    public static A2ACommandResponse success(String commandId, Object result) {
        A2ACommandResponse response = new A2ACommandResponse();
        response.getHeader().setCommandId(commandId);
        response.getHeader().setStatus(ResponseStatus.SUCCESS);
        response.getBody().setResult(result);
        return response;
    }
    
    public static A2ACommandResponse failure(String commandId, String errorMessage) {
        A2ACommandResponse response = new A2ACommandResponse();
        response.getHeader().setCommandId(commandId);
        response.getHeader().setStatus(ResponseStatus.FAILED);
        response.getHeader().setErrorMessage(errorMessage);
        return response;
    }
    
    public static A2ACommandResponse timeout(String commandId) {
        A2ACommandResponse response = new A2ACommandResponse();
        response.getHeader().setCommandId(commandId);
        response.getHeader().setStatus(ResponseStatus.TIMEOUT);
        response.getHeader().setErrorMessage("Command execution timeout");
        return response;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public boolean isSuccess() {
        return header != null && header.getStatus() == ResponseStatus.SUCCESS;
    }
    
    public ResponseHeader getHeader() { return header; }
    public void setHeader(ResponseHeader header) { this.header = header; }
    
    public ResponseBody getBody() { return body; }
    public void setBody(ResponseBody body) { this.body = body; }
    
    public static class Builder {
        private A2ACommandResponse response = new A2ACommandResponse();
        
        public Builder header(ResponseHeader header) {
            response.setHeader(header);
            return this;
        }
        
        public Builder body(ResponseBody body) {
            response.setBody(body);
            return this;
        }
        
        public Builder status(ResponseStatus status) {
            response.getHeader().setStatus(status);
            return this;
        }
        
        public Builder result(Object result) {
            response.getBody().setResult(result);
            return this;
        }
        
        public Builder errorMessage(String errorMessage) {
            response.getHeader().setErrorMessage(errorMessage);
            return this;
        }
        
        public A2ACommandResponse build() {
            return response;
        }
    }
    
    public enum ResponseStatus {
        SUCCESS,
        FAILED,
        TIMEOUT,
        REJECTED,
        PENDING
    }
    
    public static class ResponseHeader implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private String commandId;
        private String responseId;
        private long timestamp;
        private ResponseStatus status;
        private String errorMessage;
        private String errorCode;
        
        public ResponseHeader() {
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getCommandId() { return commandId; }
        public void setCommandId(String commandId) { this.commandId = commandId; }
        
        public String getResponseId() { return responseId; }
        public void setResponseId(String responseId) { this.responseId = responseId; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        
        public ResponseStatus getStatus() { return status; }
        public void setStatus(ResponseStatus status) { this.status = status; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    }
    
    public static class ResponseBody implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private Object result;
        private Map<String, Object> metadata;
        private TokenUsage tokenUsage;
        
        public ResponseBody() {
            this.metadata = new HashMap<>();
        }
        
        public Object getResult() { return result; }
        public void setResult(Object result) { this.result = result; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        
        public TokenUsage getTokenUsage() { return tokenUsage; }
        public void setTokenUsage(TokenUsage tokenUsage) { this.tokenUsage = tokenUsage; }
    }
    
    public static class TokenUsage implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private long promptTokens;
        private long completionTokens;
        private long totalTokens;
        
        public long getPromptTokens() { return promptTokens; }
        public void setPromptTokens(long promptTokens) { this.promptTokens = promptTokens; }
        
        public long getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(long completionTokens) { this.completionTokens = completionTokens; }
        
        public long getTotalTokens() { return totalTokens; }
        public void setTotalTokens(long totalTokens) { this.totalTokens = totalTokens; }
    }
}
