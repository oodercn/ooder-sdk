package net.ooder.scene.service.push;

import net.ooder.sdk.common.enums.MemberRole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 推送请求
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class PushRequest {

    private String sceneId;
    private String sceneName;
    private String leaderId;
    private String leaderName;
    private List<String> subordinateIds = new ArrayList<>();
    private Map<String, MemberRole> roleAssignments = new HashMap<>();
    private String message;
    private long expireTime;
    private int expireHours = 72;
    private boolean requireConfirmation = true;
    private Map<String, Object> extraData = new HashMap<>();

    public PushRequest() {}

    public PushRequest(String sceneId, String leaderId) {
        this.sceneId = sceneId;
        this.leaderId = leaderId;
    }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getSceneName() { return sceneName; }
    public void setSceneName(String sceneName) { this.sceneName = sceneName; }

    public String getLeaderId() { return leaderId; }
    public void setLeaderId(String leaderId) { this.leaderId = leaderId; }

    public String getLeaderName() { return leaderName; }
    public void setLeaderName(String leaderName) { this.leaderName = leaderName; }

    public List<String> getSubordinateIds() { return subordinateIds; }
    public void setSubordinateIds(List<String> subordinateIds) { this.subordinateIds = subordinateIds; }

    public Map<String, MemberRole> getRoleAssignments() { return roleAssignments; }
    public void setRoleAssignments(Map<String, MemberRole> roleAssignments) { this.roleAssignments = roleAssignments; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getExpireTime() { return expireTime; }
    public void setExpireTime(long expireTime) { this.expireTime = expireTime; }

    public int getExpireHours() { return expireHours; }
    public void setExpireHours(int expireHours) { this.expireHours = expireHours; }

    public boolean isRequireConfirmation() { return requireConfirmation; }
    public void setRequireConfirmation(boolean requireConfirmation) { this.requireConfirmation = requireConfirmation; }

    public Map<String, Object> getExtraData() { return extraData; }
    public void setExtraData(Map<String, Object> extraData) { this.extraData = extraData; }

    public void addSubordinate(String userId, MemberRole role) {
        subordinateIds.add(userId);
        roleAssignments.put(userId, role);
    }

    public long calculateExpireTime() {
        if (expireTime > 0) {
            return expireTime;
        }
        return System.currentTimeMillis() + (expireHours * 60 * 60 * 1000L);
    }
}
