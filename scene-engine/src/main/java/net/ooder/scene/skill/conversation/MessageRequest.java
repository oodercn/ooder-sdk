package net.ooder.scene.skill.conversation;

import java.util.List;
import java.util.Map;

/**
 * 消息请求
 *
 * @author ooder
 * @since 2.3
 */
public class MessageRequest {
    
    private String content;
    private List<String> kbIds;
    private boolean enableRag;
    private boolean enableTools;
    private List<String> specificTools;
    private Map<String, Object> options;
    
    public MessageRequest() {
    }
    
    public MessageRequest(String content) {
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public List<String> getKbIds() {
        return kbIds;
    }
    
    public void setKbIds(List<String> kbIds) {
        this.kbIds = kbIds;
    }
    
    public boolean isEnableRag() {
        return enableRag;
    }
    
    public void setEnableRag(boolean enableRag) {
        this.enableRag = enableRag;
    }
    
    public boolean isEnableTools() {
        return enableTools;
    }
    
    public void setEnableTools(boolean enableTools) {
        this.enableTools = enableTools;
    }
    
    public List<String> getSpecificTools() {
        return specificTools;
    }
    
    public void setSpecificTools(List<String> specificTools) {
        this.specificTools = specificTools;
    }
    
    public Map<String, Object> getOptions() {
        return options;
    }
    
    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }
}
