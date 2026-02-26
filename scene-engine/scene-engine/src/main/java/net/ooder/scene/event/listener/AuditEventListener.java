package net.ooder.scene.event.listener;

import net.ooder.scene.event.SceneEvent;
import net.ooder.scene.event.security.LoginEvent;
import net.ooder.scene.event.security.LogoutEvent;
import net.ooder.scene.event.security.OperationDeniedEvent;
import net.ooder.scene.event.security.TokenEvent;
import net.ooder.scene.event.session.SessionEvent;
import net.ooder.scene.event.skill.SkillEvent;
import net.ooder.scene.event.capability.CapabilityEvent;
import net.ooder.scene.event.config.ConfigEvent;
import net.ooder.scene.event.engine.EngineEvent;
import net.ooder.scene.event.scene.SceneAgentEvent;
import net.ooder.scene.event.user.UserEvent;
import net.ooder.scene.event.peer.PeerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AuditEventListener {
    
    private static final Logger log = LoggerFactory.getLogger(AuditEventListener.class);
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onLoginEvent(LoginEvent event) {
        log.info("[AUDIT] {} - user={}, userId={}, ip={}, success={}, reason={}",
                event.getEventType().getCode(),
                event.getUsername(),
                event.getUserId(),
                event.getIpAddress(),
                event.isSuccess(),
                event.getFailureReason());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onLogoutEvent(LogoutEvent event) {
        log.info("[AUDIT] {} - userId={}, username={}, sessionId={}",
                event.getEventType().getCode(),
                event.getUserId(),
                event.getUsername(),
                event.getSessionId());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onTokenEvent(TokenEvent event) {
        log.info("[AUDIT] {} - tokenId={}, userId={}, success={}",
                event.getEventType().getCode(),
                event.getTokenId(),
                event.getUserId(),
                event.isSuccess());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onOperationDeniedEvent(OperationDeniedEvent event) {
        log.warn("[AUDIT] {} - userId={}, operation={}, resource={}, reason={}",
                event.getEventType().getCode(),
                event.getUserId(),
                event.getOperation(),
                event.getResource(),
                event.getReason());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onSessionEvent(SessionEvent event) {
        log.info("[AUDIT] {} - sessionId={}, userId={}, reason={}",
                event.getEventType().getCode(),
                event.getSessionId(),
                event.getUserId(),
                event.getReason());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onSkillEvent(SkillEvent event) {
        log.info("[AUDIT] {} - skillId={}, skillName={}, version={}, success={}, error={}",
                event.getEventType().getCode(),
                event.getSkillId(),
                event.getSkillName(),
                event.getVersion(),
                event.isSuccess(),
                event.getErrorMessage());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onCapabilityEvent(CapabilityEvent event) {
        log.info("[AUDIT] {} - capId={}, capName={}, providerId={}, success={}",
                event.getEventType().getCode(),
                event.getCapId(),
                event.getCapName(),
                event.getProviderId(),
                event.isSuccess());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onConfigEvent(ConfigEvent event) {
        log.info("[AUDIT] {} - key={}, group={}, userId={}",
                event.getEventType().getCode(),
                event.getConfigKey(),
                event.getConfigGroup(),
                event.getUserId());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onEngineEvent(EngineEvent event) {
        log.info("[AUDIT] {} - engineId={}, engineName={}, healthy={}",
                event.getEventType().getCode(),
                event.getEngineId(),
                event.getEngineName(),
                event.isHealthy());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onSceneAgentEvent(SceneAgentEvent event) {
        log.info("[AUDIT] {} - sceneId={}, sceneName={}, agentId={}, userId={}",
                event.getEventType().getCode(),
                event.getSceneId(),
                event.getSceneName(),
                event.getAgentId(),
                event.getUserId());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onUserEvent(UserEvent event) {
        log.info("[AUDIT] {} - userId={}, username={}, operatorId={}, enabled={}",
                event.getEventType().getCode(),
                event.getUserId(),
                event.getUsername(),
                event.getOperatorId(),
                event.isEnabled());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onPeerEvent(PeerEvent event) {
        log.info("[AUDIT] {} - peerId={}, oldStatus={}, newStatus={}",
                event.getEventType().getCode(),
                event.getPeerId(),
                event.getOldStatus(),
                event.getNewStatus());
    }
}
