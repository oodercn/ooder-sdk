package net.ooder.skills.sync.event;

public class SdkMemberJoinedEvent extends SdkEvent {
    
    private String groupId;
    private String memberId;
    
    public SdkMemberJoinedEvent() {
        super();
    }
    
    public SdkMemberJoinedEvent(String groupId, String memberId) {
        super("SceneGroupManager");
        this.groupId = groupId;
        this.memberId = memberId;
    }
    
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
}
