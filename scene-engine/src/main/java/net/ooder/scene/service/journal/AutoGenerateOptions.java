package net.ooder.scene.service.journal;

import java.util.Date;

/**
 * 自动生成选项
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class AutoGenerateOptions {

    private boolean includeEmail = false;
    private boolean includeGitCommit = false;
    private boolean includeCalendar = false;
    private boolean includeChatHistory = false;
    private Date startTime;
    private Date endTime;
    private String template;
    private String style = "professional";
    private int maxLength = 2000;
    private String language = "zh-CN";

    public AutoGenerateOptions() {}

    public static AutoGenerateOptions emailSummary() {
        AutoGenerateOptions options = new AutoGenerateOptions();
        options.setIncludeEmail(true);
        return options;
    }

    public static AutoGenerateOptions gitSummary() {
        AutoGenerateOptions options = new AutoGenerateOptions();
        options.setIncludeGitCommit(true);
        return options;
    }

    public static AutoGenerateOptions fullSummary() {
        AutoGenerateOptions options = new AutoGenerateOptions();
        options.setIncludeEmail(true);
        options.setIncludeGitCommit(true);
        options.setIncludeCalendar(true);
        return options;
    }

    public boolean isIncludeEmail() { return includeEmail; }
    public void setIncludeEmail(boolean includeEmail) { this.includeEmail = includeEmail; }

    public boolean isIncludeGitCommit() { return includeGitCommit; }
    public void setIncludeGitCommit(boolean includeGitCommit) { this.includeGitCommit = includeGitCommit; }

    public boolean isIncludeCalendar() { return includeCalendar; }
    public void setIncludeCalendar(boolean includeCalendar) { this.includeCalendar = includeCalendar; }

    public boolean isIncludeChatHistory() { return includeChatHistory; }
    public void setIncludeChatHistory(boolean includeChatHistory) { this.includeChatHistory = includeChatHistory; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }

    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }

    public int getMaxLength() { return maxLength; }
    public void setMaxLength(int maxLength) { this.maxLength = maxLength; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public boolean hasAnySource() {
        return includeEmail || includeGitCommit || includeCalendar || includeChatHistory;
    }
}
