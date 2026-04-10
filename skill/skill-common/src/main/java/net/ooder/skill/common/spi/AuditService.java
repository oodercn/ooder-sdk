package net.ooder.skill.common.spi;

import net.ooder.skill.common.spi.audit.AuditEvent;

public interface AuditService {
    
    void log(AuditEvent event);
    
    void log(String eventType, String userId, String action, String message);
    
    void logSuccess(String eventType, String userId, String action, String message);
    
    void logFailure(String eventType, String userId, String action, String message, String error);
}
