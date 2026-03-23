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
import net.ooder.scene.event.scenegroup.SceneGroupAuditEvent;
import net.ooder.scene.event.org.OrganizationEvent;
import net.ooder.scene.event.workflow.WorkflowEvent;
import net.ooder.scene.event.asset.AssetAuditEvent;
import net.ooder.scene.event.permission.PermissionAuditEvent;
import net.ooder.scene.event.knowledge.KnowledgeBaseAuditEvent;
import net.ooder.scene.event.share.ShareAuditEvent;
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
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onSceneGroupAuditEvent(SceneGroupAuditEvent event) {
        log.info("[AUDIT] {} - groupId={}, groupName={}, operatorId={}, participantId={}, success={}",
                event.getEventType().getCode(),
                event.getGroupId(),
                event.getGroupName(),
                event.getOperatorId(),
                event.getParticipantId(),
                event.isSuccess());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onOrganizationEvent(OrganizationEvent event) {
        log.info("[AUDIT] {} - companyId={}, departmentId={}, userId={}, operatorId={}, success={}",
                event.getEventType().getCode(),
                event.getCompanyId(),
                event.getDepartmentId(),
                event.getUserId(),
                event.getOperatorId(),
                event.isSuccess());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onWorkflowEvent(WorkflowEvent event) {
        log.info("[AUDIT] {} - workflowId={}, workflowName={}, executionId={}, operatorId={}, status={}, success={}",
                event.getEventType().getCode(),
                event.getWorkflowId(),
                event.getWorkflowName(),
                event.getExecutionId(),
                event.getOperatorId(),
                event.getStatus(),
                event.isSuccess());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onAssetAuditEvent(AssetAuditEvent event) {
        log.info("[AUDIT] {} - assetId={}, assetName={}, assetType={}, ownerId={}, success={}",
                event.getEventType().getCode(),
                event.getAssetId(),
                event.getAssetName(),
                event.getAssetType(),
                event.getOwnerId(),
                event.isSuccess());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onPermissionAuditEvent(PermissionAuditEvent event) {
        log.info("[AUDIT] {} - kbId={}, userId={}, permission={}, grantedBy={}, success={}",
                event.getEventType().getCode(),
                event.getKbId(),
                event.getUserId(),
                event.getPermission(),
                event.getGrantedBy(),
                event.isSuccess());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onKnowledgeBaseAuditEvent(KnowledgeBaseAuditEvent event) {
        log.info("[AUDIT] {} - kbId={}, kbName={}, ownerId={}, docId={}, action={}, success={}",
                event.getEventType().getCode(),
                event.getKbId(),
                event.getKbName(),
                event.getOwnerId(),
                event.getDocId(),
                event.getAction(),
                event.isSuccess());
    }
    
    @Async("sceneEventExecutor")
    @EventListener
    public void onShareAuditEvent(ShareAuditEvent event) {
        log.info("[AUDIT] {} - shareId={}, shareCode={}, kbId={}, creatorId={}, action={}, success={}",
                event.getEventType().getCode(),
                event.getShareId(),
                event.getShareCode(),
                event.getKbId(),
                event.getCreatorId(),
                event.getAction(),
                event.isSuccess());
    }
}
