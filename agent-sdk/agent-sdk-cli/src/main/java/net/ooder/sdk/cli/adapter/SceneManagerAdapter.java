package net.ooder.sdk.cli.adapter;

import net.ooder.skills.api.CollaborativeSceneGroupManager;
import net.ooder.skills.api.SkillInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 场景管理适配器
 *
 * <p>复用 CollaborativeSceneGroupManager 实现场景管理</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SceneManagerAdapter {

    private static final Logger log = LoggerFactory.getLogger(SceneManagerAdapter.class);

    private final CollaborativeSceneGroupManager sceneGroupManager;
    private final SkillInvoker skillInvoker;

    public SceneManagerAdapter(CollaborativeSceneGroupManager sceneGroupManager, SkillInvoker skillInvoker) {
        this.sceneGroupManager = sceneGroupManager;
        this.skillInvoker = skillInvoker;
    }

    public SceneManagerAdapter(CollaborativeSceneGroupManager sceneGroupManager) {
        this(sceneGroupManager, null);
    }

    /**
     * 创建场景组
     */
    public SceneGroupResult createSceneGroup(String groupId, String mainCapabilityId,
                                              List<String> collaborativeCapabilityIds) {
        try {
            CollaborativeSceneGroupManager.SceneGroupRequest request =
                    new CollaborativeSceneGroupManager.SceneGroupRequest();
            request.setGroupId(groupId);
            request.setMainCapabilityId(mainCapabilityId);
            request.setCollaborativeCapabilityIds(collaborativeCapabilityIds);

            CompletableFuture<CollaborativeSceneGroupManager.SceneGroupInfo> future =
                    sceneGroupManager.createGroup(request);

            CollaborativeSceneGroupManager.SceneGroupInfo info = future.get();

            return SceneGroupResult.success("Scene group created", info);
        } catch (Exception e) {
            log.error("Failed to create scene group: {}", groupId, e);
            return SceneGroupResult.error("Failed to create scene group: " + e.getMessage());
        }
    }

    /**
     * 获取场景组信息
     */
    public SceneGroupResult getSceneGroup(String groupId) {
        try {
            CompletableFuture<CollaborativeSceneGroupManager.SceneGroupInfo> future =
                    sceneGroupManager.getGroupInfo(groupId);

            CollaborativeSceneGroupManager.SceneGroupInfo info = future.get();

            if (info == null) {
                return SceneGroupResult.notFound("Scene group not found: " + groupId);
            }

            return SceneGroupResult.success("Scene group found", info);
        } catch (Exception e) {
            log.error("Failed to get scene group: {}", groupId, e);
            return SceneGroupResult.error("Failed to get scene group: " + e.getMessage());
        }
    }

    /**
     * 列出所有场景组
     */
    public SceneGroupListResult listSceneGroups() {
        try {
            CompletableFuture<List<CollaborativeSceneGroupManager.SceneGroupInfo>> future =
                    sceneGroupManager.listGroups();

            List<CollaborativeSceneGroupManager.SceneGroupInfo> groups = future.get();

            return new SceneGroupListResult(true, "Listed " + groups.size() + " scene groups", groups);
        } catch (Exception e) {
            log.error("Failed to list scene groups", e);
            return new SceneGroupListResult(false, "Failed to list scene groups: " + e.getMessage(), null);
        }
    }

    /**
     * 解散场景组
     */
    public SceneGroupResult disbandSceneGroup(String groupId) {
        try {
            CompletableFuture<Boolean> future = sceneGroupManager.disbandGroup(groupId);
            Boolean success = future.get();

            if (success) {
                return SceneGroupResult.success("Scene group disbanded: " + groupId, null);
            } else {
                return SceneGroupResult.error("Failed to disband scene group: " + groupId);
            }
        } catch (Exception e) {
            log.error("Failed to disband scene group: {}", groupId, e);
            return SceneGroupResult.error("Failed to disband scene group: " + e.getMessage());
        }
    }

    /**
     * 添加协作能力
     */
    public SceneGroupResult addCollaborativeCapability(String groupId, String capabilityId) {
        try {
            CompletableFuture<Boolean> future =
                    sceneGroupManager.addCollaborativeCapability(groupId, capabilityId);

            Boolean success = future.get();

            if (success) {
                return SceneGroupResult.success("Capability added to scene group", null);
            } else {
                return SceneGroupResult.error("Failed to add capability");
            }
        } catch (Exception e) {
            log.error("Failed to add capability: {} to group: {}", capabilityId, groupId, e);
            return SceneGroupResult.error("Failed to add capability: " + e.getMessage());
        }
    }

    /**
     * 同步场景组状态
     */
    public SceneGroupResult syncGroupState(String groupId, Map<String, Object> state) {
        try {
            CompletableFuture<Boolean> future = sceneGroupManager.syncGroupState(groupId, state);
            Boolean success = future.get();

            if (success) {
                return SceneGroupResult.success("Scene group state synced", null);
            } else {
                return SceneGroupResult.error("Failed to sync scene group state");
            }
        } catch (Exception e) {
            log.error("Failed to sync scene group state: {}", groupId, e);
            return SceneGroupResult.error("Failed to sync state: " + e.getMessage());
        }
    }

    /**
     * 调用场景中的能力
     *
     * @param groupId 场景组ID
     * @param capabilityId 能力ID
     * @param params 参数
     * @return 调用结果
     */
    public InvokeResult invokeCapability(String groupId, String capabilityId, Map<String, Object> params) {
        try {
            log.info("Invoking capability: {} in scene group: {}", capabilityId, groupId);

            // 1. 获取场景组信息验证能力存在
            SceneGroupResult groupResult = getSceneGroup(groupId);
            if (!groupResult.isSuccess()) {
                return InvokeResult.error("Scene group not found: " + groupId);
            }

            CollaborativeSceneGroupManager.SceneGroupInfo groupInfo = groupResult.getGroupInfo();
            if (groupInfo == null) {
                return InvokeResult.error("Scene group info not available");
            }

            // 2. 检查能力是否在场景中
            List<CollaborativeSceneGroupManager.CollaborativeCapabilityInfo> capabilities = 
                groupInfo.getCollaborativeCapabilities();
            if (capabilities == null) {
                return InvokeResult.error("No capabilities available in scene: " + groupId);
            }
            
            boolean capabilityFound = capabilities.stream()
                .anyMatch(cap -> capabilityId.equals(cap.getCapabilityId()));
            
            if (!capabilityFound) {
                return InvokeResult.error("Capability not available in scene: " + capabilityId);
            }

            // 3. 通过SkillInvoker调用能力
            if (skillInvoker == null) {
                return InvokeResult.error("SkillInvoker not available");
            }

            // 解析skillId和operation
            String[] parts = capabilityId.split(":");
            String skillId = parts.length > 0 ? parts[0] : capabilityId;
            String operation = parts.length > 1 ? parts[1] : "execute";

            Object result = skillInvoker.invoke(skillId, operation, params);

            log.info("Capability invoked successfully: {} in scene: {}", capabilityId, groupId);

            return InvokeResult.success(result);

        } catch (SkillInvoker.SkillInvocationException e) {
            log.error("Skill invocation failed: {} in scene: {}", capabilityId, groupId, e);
            return InvokeResult.error("Skill invocation failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to invoke capability: {} in scene: {}", capabilityId, groupId, e);
            return InvokeResult.error("Capability invocation failed: " + e.getMessage());
        }
    }

    /**
     * 场景组结果
     */
    public static class SceneGroupResult {
        private final boolean success;
        private final String message;
        private final CollaborativeSceneGroupManager.SceneGroupInfo groupInfo;

        public SceneGroupResult(boolean success, String message,
                                CollaborativeSceneGroupManager.SceneGroupInfo groupInfo) {
            this.success = success;
            this.message = message;
            this.groupInfo = groupInfo;
        }

        public static SceneGroupResult success(String message,
                                               CollaborativeSceneGroupManager.SceneGroupInfo groupInfo) {
            return new SceneGroupResult(true, message, groupInfo);
        }

        public static SceneGroupResult error(String message) {
            return new SceneGroupResult(false, message, null);
        }

        public static SceneGroupResult notFound(String message) {
            return new SceneGroupResult(false, message, null);
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public CollaborativeSceneGroupManager.SceneGroupInfo getGroupInfo() { return groupInfo; }
    }

    /**
     * 场景组列表结果
     */
    public static class SceneGroupListResult {
        private final boolean success;
        private final String message;
        private final List<CollaborativeSceneGroupManager.SceneGroupInfo> groups;

        public SceneGroupListResult(boolean success, String message,
                                    List<CollaborativeSceneGroupManager.SceneGroupInfo> groups) {
            this.success = success;
            this.message = message;
            this.groups = groups;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<CollaborativeSceneGroupManager.SceneGroupInfo> getGroups() { return groups; }
    }

    /**
     * 能力调用结果
     */
    public static class InvokeResult {
        private final boolean success;
        private final Object result;
        private final String error;

        private InvokeResult(boolean success, Object result, String error) {
            this.success = success;
            this.result = result;
            this.error = error;
        }

        public static InvokeResult success(Object result) {
            return new InvokeResult(true, result, null);
        }

        public static InvokeResult error(String error) {
            return new InvokeResult(false, null, error);
        }

        public boolean isSuccess() { return success; }
        public Object getResult() { return result; }
        public String getError() { return error; }
    }
}
