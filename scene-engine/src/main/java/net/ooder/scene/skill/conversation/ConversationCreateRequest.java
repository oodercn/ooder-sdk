package net.ooder.scene.skill.conversation;

import java.util.List;
import java.util.Map;

/**
 * 对话创建请求
 *
 * @author ooder
 * @since 2.3
 */
public class ConversationCreateRequest {
    
    private String title;
    private String kbId;
    private List<String> enabledTools;
    private Map<String, Object> settings;
    private String systemPrompt;
    
    public ConversationCreateRequest() {
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getKbId() {
        return kbId;
    }
    
    public void setKbId(String kbId) {
        this.kbId = kbId;
    }
    
    public List<String> getEnabledTools() {
        return enabledTools;
    }
    
    public void setEnabledTools(List<String> enabledTools) {
        this.enabledTools = enabledTools;
    }
    
    public Map<String, Object> getSettings() {
        return settings;
    }
    
    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }
    
    public String getSystemPrompt() {
        return systemPrompt;
    }
    
    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
}
