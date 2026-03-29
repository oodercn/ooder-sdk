package net.ooder.scene.log.cascade;

import net.ooder.scene.log.*;

import java.util.function.Predicate;

public enum LogCascadeRule {

    LLM_TO_AUDIT(
        LogCategory.LLM_CALL,
        LogCategory.AUDIT,
        trigger -> trigger.getStatus() == LogStatus.SUCCESS
                || trigger.getStatus() == LogStatus.FAILURE,
        "LLM调用审计",
        LogPriority.NORMAL
    ),

    CAPABILITY_TO_EXECUTION(
        LogCategory.CAPABILITY,
        LogCategory.EXECUTION,
        trigger -> true,
        "能力执行记录",
        LogPriority.NORMAL
    ),

    EXECUTION_ERROR_TO_AUDIT(
        LogCategory.EXECUTION,
        LogCategory.AUDIT,
        trigger -> trigger.getLevel() == LogLevel.ERROR,
        "执行异常审计",
        LogPriority.HIGH
    ),

    INSTALL_TO_AUDIT(
        LogCategory.INSTALL,
        LogCategory.AUDIT,
        trigger -> true,
        "安装操作审计",
        LogPriority.NORMAL
    ),

    SECURITY_TO_AUDIT(
        LogCategory.SECURITY,
        LogCategory.AUDIT,
        trigger -> true,
        "安全事件审计",
        LogPriority.CRITICAL
    ),

    LOGIN_FAILURE_TO_SECURITY(
        LogCategory.LOGIN,
        LogCategory.SECURITY,
        trigger -> trigger.getStatus() == LogStatus.FAILURE,
        "登录失败安全告警",
        LogPriority.HIGH
    );

    private final LogCategory sourceCategory;
    private final LogCategory targetCategory;
    private final Predicate<LogEntry> triggerCondition;
    private final String description;
    private final LogPriority priority;

    LogCascadeRule(LogCategory sourceCategory,
                   LogCategory targetCategory,
                   Predicate<LogEntry> triggerCondition,
                   String description,
                   LogPriority priority) {
        this.sourceCategory = sourceCategory;
        this.targetCategory = targetCategory;
        this.triggerCondition = triggerCondition;
        this.description = description;
        this.priority = priority;
    }

    public LogCategory getSourceCategory() { return sourceCategory; }
    public LogCategory getTargetCategory() { return targetCategory; }
    public Predicate<LogEntry> getTriggerCondition() { return triggerCondition; }
    public String getDescription() { return description; }
    public LogPriority getPriority() { return priority; }

    public boolean shouldTrigger(LogEntry entry) {
        return entry.getCategory() == sourceCategory
            && triggerCondition.test(entry);
    }
}
