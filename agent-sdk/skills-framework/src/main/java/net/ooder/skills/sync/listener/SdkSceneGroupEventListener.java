package net.ooder.skills.sync.listener;

import net.ooder.skills.sync.event.SdkEvent;
import net.ooder.skills.sync.event.SdkMemberJoinedEvent;
import net.ooder.skills.sync.event.SdkMemberLeftEvent;
import net.ooder.skills.sync.event.SdkPrimaryChangedEvent;
import net.ooder.skills.sync.UserSceneGroup;
import net.ooder.skills.sync.UserSceneGroupManager;
import net.ooder.skills.sync.event.SyncEvent;
import net.ooder.skills.sync.event.SyncEventListener;
import net.ooder.skills.sync.event.SyncEventPublisher;
import net.ooder.skills.sync.impl.UserSceneGroupImpl;

public class SdkSceneGroupEventListener implements SyncEventListener {
    
    private final UserSceneGroupManager userSceneGroupManager;
    
    public SdkSceneGroupEventListener(UserSceneGroupManager userSceneGroupManager) {
        this.userSceneGroupManager = userSceneGroupManager;
    }
    
    @Override
    public void onEvent(SyncEvent event) {
        String sceneGroupId = event.getSceneGroupId();
        UserSceneGroupImpl userGroup = (UserSceneGroupImpl) userSceneGroupManager.getUserSceneGroup(sceneGroupId);
        
        if (userGroup == null) {
            return;
        }
        
        switch (event.getType()) {
            case MEMBER_STATUS_CHANGED:
                handleMemberStatusChanged(userGroup, event);
                break;
            case FAILOVER_COMPLETED:
                handleFailoverCompleted(userGroup, event);
                break;
            default:
                break;
        }
    }
    
    @Override
    public boolean supports(SyncEvent.Type type) {
        return type == SyncEvent.Type.MEMBER_STATUS_CHANGED 
            || type == SyncEvent.Type.FAILOVER_COMPLETED;
    }
    
    public void onSdkMemberStatusChanged(String sceneGroupId, String agentId, String newStatus) {
        UserSceneGroupImpl userGroup = (UserSceneGroupImpl) userSceneGroupManager.getUserSceneGroup(sceneGroupId);
        
        if (userGroup != null) {
            userGroup.updateParticipantStatus(agentId, newStatus);
        }
    }
    
    public void onSdkFailoverEvent(String sceneGroupId, String failedAgentId, String newPrimaryId) {
        UserSceneGroupImpl userGroup = (UserSceneGroupImpl) userSceneGroupManager.getUserSceneGroup(sceneGroupId);
        
        if (userGroup != null) {
            userGroup.handleFailoverEvent(failedAgentId, newPrimaryId);
        }
    }
    
    private void handleMemberStatusChanged(UserSceneGroupImpl userGroup, SyncEvent event) {
        SyncEventPublisher.MemberStatusChange change = 
            event.getDataAs(SyncEventPublisher.MemberStatusChange.class);
        
        if (change != null) {
            userGroup.updateParticipantStatus(change.getAgentId(), change.getNewStatus());
        }
    }
    
    private void handleFailoverCompleted(UserSceneGroupImpl userGroup, SyncEvent event) {
        SyncEventPublisher.FailoverData data = 
            event.getDataAs(SyncEventPublisher.FailoverData.class);
        
        if (data != null) {
            userGroup.handleFailoverEvent(data.getFailedAgentId(), data.getNewPrimaryId());
        }
    }
    
    public void onSdkEvent(SdkEvent sdkEvent) {
        if (sdkEvent instanceof SdkMemberJoinedEvent) {
            SdkMemberJoinedEvent joinedEvent = (SdkMemberJoinedEvent) sdkEvent;
            onSdkMemberStatusChanged(joinedEvent.getGroupId(), joinedEvent.getMemberId(), "online");
        } else if (sdkEvent instanceof SdkMemberLeftEvent) {
            SdkMemberLeftEvent leftEvent = (SdkMemberLeftEvent) sdkEvent;
            onSdkMemberStatusChanged(leftEvent.getGroupId(), leftEvent.getMemberId(), "offline");
        } else if (sdkEvent instanceof SdkPrimaryChangedEvent) {
            SdkPrimaryChangedEvent primaryEvent = (SdkPrimaryChangedEvent) sdkEvent;
            onSdkFailoverEvent(primaryEvent.getGroupId(), 
                              primaryEvent.getOldPrimaryId(), 
                              primaryEvent.getNewPrimaryId());
        }
    }
}
