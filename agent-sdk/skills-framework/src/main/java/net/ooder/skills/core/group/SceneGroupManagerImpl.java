package net.ooder.skills.core.group;

import net.ooder.skills.api.CollaborativeSceneGroupManager;
import net.ooder.skills.api.SkillRegistry;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 场景组管理器实现
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SceneGroupManagerImpl implements CollaborativeSceneGroupManager {
    
    private final Map<String, SceneGroupInfo> groups = new ConcurrentHashMap<>();
    private final SkillRegistry skillRegistry;
    private final List<SceneGroupListener> listeners = new CopyOnWriteArrayList<>();
    
    public SceneGroupManagerImpl(SkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
    }
    
    @Override
    public CompletableFuture<SceneGroupInfo> createGroup(SceneGroupRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String groupId = request.getGroupId();
            if (groupId == null || groupId.isEmpty()) {
                groupId = generateGroupId();
            }
            
            // 检查是否已存在
            if (groups.containsKey(groupId)) {
                throw new IllegalStateException("Scene group already exists: " + groupId);
            }
            
            // 验证主场景能力
            String mainCapabilityId = request.getMainCapabilityId();
            if (mainCapabilityId == null || mainCapabilityId.isEmpty()) {
                throw new IllegalArgumentException("Main capability ID is required");
            }
            
            // 创建场景组信息
            SceneGroupInfo groupInfo = new SceneGroupInfo();
            groupInfo.setGroupId(groupId);
            groupInfo.setMainCapabilityId(mainCapabilityId);
            groupInfo.setStatus(SceneGroupStatus.CREATING);
            groupInfo.setSharedState(request.getSharedState() != null ? request.getSharedState() : new HashMap<>());
            groupInfo.setCreateTime(System.currentTimeMillis());
            groupInfo.setLastUpdateTime(System.currentTimeMillis());
            groupInfo.setCollaborativeCapabilities(new ArrayList<>());
            
            // 添加协作能力
            if (request.getCollaborativeCapabilityIds() != null) {
                for (String capabilityId : request.getCollaborativeCapabilityIds()) {
                    CollaborativeCapabilityInfo collabInfo = new CollaborativeCapabilityInfo();
                    collabInfo.setCapabilityId(capabilityId);
                    collabInfo.setRole("secondary");
                    collabInfo.setStatus(CollaborativeCapabilityInfo.CollaborativeStatus.PENDING);
                    collabInfo.setJoinTime(System.currentTimeMillis());
                    groupInfo.getCollaborativeCapabilities().add(collabInfo);
                }
            }
            
            // 保存场景组
            groups.put(groupId, groupInfo);
            
            // 更新状态为活跃
            groupInfo.setStatus(SceneGroupStatus.ACTIVE);
            
            // 通知监听器
            notifyGroupCreated(groupInfo);
            
            return groupInfo;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> disbandGroup(String groupId) {
        return CompletableFuture.supplyAsync(() -> {
            SceneGroupInfo group = groups.get(groupId);
            if (group == null) {
                return false;
            }
            
            // 更新状态
            group.setStatus(SceneGroupStatus.DISBANDING);
            
            // 通知所有协作能力离开
            if (group.getCollaborativeCapabilities() != null) {
                for (CollaborativeCapabilityInfo collab : group.getCollaborativeCapabilities()) {
                    collab.setStatus(CollaborativeCapabilityInfo.CollaborativeStatus.LEFT);
                }
            }
            
            // 移除场景组
            groups.remove(groupId);
            
            // 通知监听器
            notifyGroupDisbanded(groupId);
            
            return true;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> addCollaborativeCapability(String groupId, String capabilityId) {
        return addCollaborativeCapability(groupId, capabilityId, null);
    }
    
    @Override
    public CompletableFuture<Boolean> addCollaborativeCapability(String groupId, String capabilityId, CollaborativeConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            SceneGroupInfo group = groups.get(groupId);
            if (group == null) {
                return false;
            }
            
            // 检查是否已存在
            boolean exists = group.getCollaborativeCapabilities().stream()
                .anyMatch(c -> c.getCapabilityId().equals(capabilityId));
            if (exists) {
                return false;
            }
            
            // 创建协作能力信息
            CollaborativeCapabilityInfo collabInfo = new CollaborativeCapabilityInfo();
            collabInfo.setCapabilityId(capabilityId);
            collabInfo.setRole(config != null && config.getRole() != null ? config.getRole() : "secondary");
            collabInfo.setStatus(CollaborativeCapabilityInfo.CollaborativeStatus.JOINING);
            collabInfo.setConfig(config != null ? config.getInitParams() : null);
            collabInfo.setJoinTime(System.currentTimeMillis());
            
            group.getCollaborativeCapabilities().add(collabInfo);
            group.setLastUpdateTime(System.currentTimeMillis());
            
            // 模拟加入过程
            collabInfo.setStatus(CollaborativeCapabilityInfo.CollaborativeStatus.ACTIVE);
            
            // 通知监听器
            notifyCapabilityAdded(groupId, capabilityId);
            
            return true;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> removeCollaborativeCapability(String groupId, String capabilityId) {
        return CompletableFuture.supplyAsync(() -> {
            SceneGroupInfo group = groups.get(groupId);
            if (group == null) {
                return false;
            }
            
            // 查找并移除
            boolean removed = group.getCollaborativeCapabilities().removeIf(c -> {
                if (c.getCapabilityId().equals(capabilityId)) {
                    c.setStatus(CollaborativeCapabilityInfo.CollaborativeStatus.LEFT);
                    return true;
                }
                return false;
            });
            
            if (removed) {
                group.setLastUpdateTime(System.currentTimeMillis());
                notifyCapabilityRemoved(groupId, capabilityId);
            }
            
            return removed;
        });
    }
    
    @Override
    public CompletableFuture<SceneGroupInfo> getGroupInfo(String groupId) {
        return CompletableFuture.supplyAsync(() -> groups.get(groupId));
    }
    
    @Override
    public CompletableFuture<List<SceneGroupInfo>> listGroups() {
        return CompletableFuture.supplyAsync(() -> 
            new ArrayList<>(groups.values())
        );
    }
    
    @Override
    public CompletableFuture<List<SceneGroupInfo>> listGroupsByMainCapability(String mainCapabilityId) {
        return CompletableFuture.supplyAsync(() -> 
            groups.values().stream()
                .filter(g -> g.getMainCapabilityId().equals(mainCapabilityId))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public CompletableFuture<Boolean> updateGroupStatus(String groupId, SceneGroupStatus status) {
        return CompletableFuture.supplyAsync(() -> {
            SceneGroupInfo group = groups.get(groupId);
            if (group == null) {
                return false;
            }
            
            SceneGroupStatus oldStatus = group.getStatus();
            group.setStatus(status);
            group.setLastUpdateTime(System.currentTimeMillis());
            
            notifyGroupStatusChanged(groupId, oldStatus, status);
            
            return true;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> syncGroupState(String groupId, Map<String, Object> state) {
        return CompletableFuture.supplyAsync(() -> {
            SceneGroupInfo group = groups.get(groupId);
            if (group == null) {
                return false;
            }
            
            if (group.getSharedState() == null) {
                group.setSharedState(new HashMap<>());
            }
            group.getSharedState().putAll(state);
            group.setLastUpdateTime(System.currentTimeMillis());
            
            notifyGroupStateSynced(groupId, state);
            
            return true;
        });
    }
    
    @Override
    public CompletableFuture<Map<String, Object>> getGroupState(String groupId) {
        return CompletableFuture.supplyAsync(() -> {
            SceneGroupInfo group = groups.get(groupId);
            if (group == null) {
                return null;
            }
            return group.getSharedState();
        });
    }
    
    @Override
    public boolean exists(String groupId) {
        return groups.containsKey(groupId);
    }
    
    @Override
    public void addGroupListener(SceneGroupListener listener) {
        listeners.add(listener);
    }
    
    @Override
    public void removeGroupListener(SceneGroupListener listener) {
        listeners.remove(listener);
    }
    
    // ========== 私有方法 ==========
    
    private String generateGroupId() {
        return "group-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private void notifyGroupCreated(SceneGroupInfo group) {
        for (SceneGroupListener listener : listeners) {
            try {
                listener.onGroupCreated(group);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }
    
    private void notifyGroupDisbanded(String groupId) {
        for (SceneGroupListener listener : listeners) {
            try {
                listener.onGroupDisbanded(groupId);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }
    
    private void notifyCapabilityAdded(String groupId, String capabilityId) {
        for (SceneGroupListener listener : listeners) {
            try {
                listener.onCapabilityAdded(groupId, capabilityId);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }
    
    private void notifyCapabilityRemoved(String groupId, String capabilityId) {
        for (SceneGroupListener listener : listeners) {
            try {
                listener.onCapabilityRemoved(groupId, capabilityId);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }
    
    private void notifyGroupStatusChanged(String groupId, SceneGroupStatus oldStatus, SceneGroupStatus newStatus) {
        for (SceneGroupListener listener : listeners) {
            try {
                listener.onGroupStatusChanged(groupId, oldStatus, newStatus);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }
    
    private void notifyGroupStateSynced(String groupId, Map<String, Object> state) {
        for (SceneGroupListener listener : listeners) {
            try {
                listener.onGroupStateSynced(groupId, state);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }
}
