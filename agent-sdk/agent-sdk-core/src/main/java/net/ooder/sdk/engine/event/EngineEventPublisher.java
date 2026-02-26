package net.ooder.sdk.engine.event;

import net.ooder.sdk.engine.audit.AuditLogStorage;
import net.ooder.sdk.engine.audit.AuditLogStorage.AuditLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Engine 层事件发布器
 *
 * <p>职责：</p>
 * <ul>
 *   <li>将 Engine 事件发布到 Spring 上下文</li>
 *   <li>自动记录审计日志</li>
 *   <li>处理事件取消逻辑</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 */
@Component
public class EngineEventPublisher implements ApplicationEventPublisherAware {

    private static final Logger log = LoggerFactory.getLogger(EngineEventPublisher.class);

    private ApplicationEventPublisher springPublisher;

    @Autowired(required = false)
    private AuditLogStorage auditLogStorage;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.springPublisher = applicationEventPublisher;
    }

    /**
     * 发布 Engine 事件
     *
     * <p>流程：</p>
     * <ol>
     *   <li>记录审计日志</li>
     *   <li>发布到 Spring 上下文（支持 @EventListener）</li>
     *   <li>同步发布到 Core EventBean</li>
     *   <li>检查事件是否被取消</li>
     * </ol>
     *
     * @param event 要发布的事件
     * @return 如果事件被取消返回 false，否则返回 true
     */
    public boolean publish(EngineEvent event) {
        if (event == null) {
            log.warn("Cannot publish null engine event");
            return true;
        }

        // 1. 记录审计日志
        if (event.isAuditable()) {
            recordAuditLog(event);
        }

        // 2. 发布到 Spring 上下文
        if (springPublisher != null) {
            springPublisher.publishEvent(event);
        }

        // 3. 检查是否被取消
        if (event.isCancelled()) {
            log.warn("Engine event {} was cancelled by listener, reason: {}",
                event.getEventType(), event.getCancelReason());
            return false;
        }

        return true;
    }

    /**
     * 发布事件（带回调）
     *
     * <p>如果事件被取消，执行 onCancelled 回调</p>
     *
     * @param event        要发布的事件
     * @param onCancelled  取消回调
     * @return 如果事件被取消返回 false，否则返回 true
     */
    public boolean publish(EngineEvent event, Runnable onCancelled) {
        boolean result = publish(event);
        if (!result && onCancelled != null) {
            onCancelled.run();
        }
        return result;
    }

    /**
     * 记录审计日志
     */
    private void recordAuditLog(EngineEvent event) {
        // 1. 记录到日志
        String auditLog = String.format("%s|%s|%s|%s|%s|%s",
            event.getInstant(),
            event.getEventId(),
            event.getCallerInfo().getUserId(),
            event.getEventType(),
            event.getDescription(),
            event.getAuditLevel()
        );

        switch (event.getAuditLevel()) {
            case DEBUG:
                log.debug("[AUDIT] {}", auditLog);
                break;
            case INFO:
                log.info("[AUDIT] {}", auditLog);
                break;
            case WARNING:
                log.warn("[AUDIT] {}", auditLog);
                break;
            case ERROR:
            case CRITICAL:
                log.error("[AUDIT] {}", auditLog);
                break;
        }

        // 2. 存储到审计日志存储
        if (auditLogStorage != null) {
            try {
                AuditLogEntry entry = createAuditLogEntry(event);
                auditLogStorage.store(entry);
            } catch (Exception e) {
                log.error("Failed to store audit log for event: {}", event.getEventId(), e);
            }
        }
    }

    /**
     * 创建审计日志条目
     */
    private AuditLogEntry createAuditLogEntry(EngineEvent event) {
        AuditLogEntry entry = new AuditLogEntry();
        entry.setLogId(UUID.randomUUID().toString());
        entry.setEventId(event.getEventId());
        entry.setEventType(event.getEventType());
        entry.setCallerId(event.getCallerInfo().getUserId());
        entry.setCallerName(event.getCallerInfo().getUsername());
        entry.setCallerIp(event.getCallerInfo().getIpAddress());
        entry.setDescription(event.getDescription());
        entry.setLevel(event.getAuditLevel().name());
        entry.setTimestamp(event.getTimestamp());
        entry.setCancelled(event.isCancelled());
        entry.setCancelReason(event.getCancelReason());
        entry.setAttributes(event.getAttributes());

        // 如果是 SkillInvocationEvent，提取 Skill 和 Capability ID
        if (event instanceof net.ooder.sdk.engine.event.skill.SkillInvocationEvent) {
            net.ooder.sdk.engine.event.skill.SkillInvocationEvent skillEvent =
                (net.ooder.sdk.engine.event.skill.SkillInvocationEvent) event;
            entry.setSkillId(skillEvent.getSkillId());
            entry.setCapabilityId(skillEvent.getCapabilityId());
        }

        return entry;
    }
}
