package net.ooder.scene.snapshot;

/**
 * 参与者快照
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class ParticipantSnapshot {

    private String participantId;
    private String userId;
    private String role;
    private String status;
    private long joinTime;

    public ParticipantSnapshot() {}

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }
}
