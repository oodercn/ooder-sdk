package net.ooder.scene.a2a.mcp;

/**
 * MCP 错误
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class MCPError {
    
    private int code;
    private String message;
    private Object data;
    
    public MCPError() {
    }
    
    public MCPError(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return "MCPError{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
