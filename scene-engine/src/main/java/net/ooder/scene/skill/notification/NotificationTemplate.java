package net.ooder.scene.skill.notification;

import java.util.Map;

/**
 * 通知模板
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class NotificationTemplate {
    
    private String templateId;
    private String name;
    private NotificationType type;
    private String titleTemplate;
    private String contentTemplate;
    private NotificationService.PushChannel defaultChannel;
    private boolean enabled;
    private Long createTime;
    private Long updateTime;
    
    public NotificationTemplate() {}
    
    public String getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public String getTitleTemplate() {
        return titleTemplate;
    }
    
    public void setTitleTemplate(String titleTemplate) {
        this.titleTemplate = titleTemplate;
    }
    
    public String getContentTemplate() {
        return contentTemplate;
    }
    
    public void setContentTemplate(String contentTemplate) {
        this.contentTemplate = contentTemplate;
    }
    
    public NotificationService.PushChannel getDefaultChannel() {
        return defaultChannel;
    }
    
    public void setDefaultChannel(NotificationService.PushChannel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public Long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    
    public Long getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
    
    public String renderTitle(Map<String, Object> params) {
        return renderTemplate(titleTemplate, params);
    }
    
    public String renderContent(Map<String, Object> params) {
        return renderTemplate(contentTemplate, params);
    }
    
    private String renderTemplate(String template, Map<String, Object> params) {
        if (template == null) {
            return "";
        }
        
        String result = template;
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String placeholder = "${" + entry.getKey() + "}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                result = result.replace(placeholder, value);
            }
        }
        
        return result;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final NotificationTemplate template = new NotificationTemplate();
        
        public Builder templateId(String templateId) {
            template.setTemplateId(templateId);
            return this;
        }
        
        public Builder name(String name) {
            template.setName(name);
            return this;
        }
        
        public Builder type(NotificationType type) {
            template.setType(type);
            return this;
        }
        
        public Builder titleTemplate(String titleTemplate) {
            template.setTitleTemplate(titleTemplate);
            return this;
        }
        
        public Builder contentTemplate(String contentTemplate) {
            template.setContentTemplate(contentTemplate);
            return this;
        }
        
        public Builder defaultChannel(NotificationService.PushChannel defaultChannel) {
            template.setDefaultChannel(defaultChannel);
            return this;
        }
        
        public Builder enabled(boolean enabled) {
            template.setEnabled(enabled);
            return this;
        }
        
        public NotificationTemplate build() {
            if (template.getCreateTime() == null) {
                template.setCreateTime(System.currentTimeMillis());
            }
            template.setUpdateTime(System.currentTimeMillis());
            return template;
        }
    }
}
