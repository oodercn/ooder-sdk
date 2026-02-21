package net.ooder.scene.core;

import java.util.Map;

/**
 * 用户设置
 */
public class UserSettings {
    private String userId;
    private String theme;
    private String language;
    private Map<String, Object> preferences;
    private Map<String, Boolean> notifications;

    public UserSettings() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public Map<String, Object> getPreferences() { return preferences; }
    public void setPreferences(Map<String, Object> preferences) { this.preferences = preferences; }
    public Map<String, Boolean> getNotifications() { return notifications; }
    public void setNotifications(Map<String, Boolean> notifications) { this.notifications = notifications; }
}
