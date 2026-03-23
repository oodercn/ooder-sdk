package net.ooder.skills.core.communication;

import net.ooder.skills.api.SceneCommunication;
import net.ooder.skills.api.CollaborativeSceneGroupManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 场景间通信实现
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SceneCommunicationImpl implements SceneCommunication {

    private final CollaborativeSceneGroupManager groupManager;
    private final Map<String, MessageHandler> handlers = new ConcurrentHashMap<>();
    private final Map<String, MessageHandler> groupHandlers = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> groupStates = new ConcurrentHashMap<>();
    private final List<CommunicationListener> listeners = new CopyOnWriteArrayList<>();

    public SceneCommunicationImpl(CollaborativeSceneGroupManager groupManager) {
        this.groupManager = groupManager;
    }

    @Override
    public CompletableFuture<MessageResult> sendMessage(String fromScene, String toScene, SceneMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            message.setFromScene(fromScene);
            message.setToScene(toScene);

            notifyMessageSent(fromScene, toScene, message);

            // 查找目标场景的消息处理器
            MessageHandler handler = handlers.get(toScene);
            if (handler == null) {
                return MessageResult.failure(message.getMessageId(), "目标场景没有注册消息处理器: " + toScene);
            }

            try {
                HandleResult result = handler.handle(message).get();
                notifyMessageReceived(toScene, message);

                if (result.isSuccess()) {
                    return MessageResult.success(message.getMessageId());
                } else {
                    return MessageResult.failure(message.getMessageId(), result.getMessage());
                }
            } catch (Exception e) {
                return MessageResult.failure(message.getMessageId(), "消息处理失败: " + e.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<BroadcastResult> broadcast(String groupId, SceneMessage message) {
        return broadcast(null, groupId, message);
    }

    @Override
    public CompletableFuture<BroadcastResult> broadcast(String fromScene, String groupId, SceneMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            message.setFromScene(fromScene);

            BroadcastResult result = new BroadcastResult();
            result.setMessageId(message.getMessageId());

            notifyBroadcastSent(groupId, message);

            try {
                // 获取场景组信息
                CollaborativeSceneGroupManager.SceneGroupInfo groupInfo = groupManager.getGroupInfo(groupId).get();
                if (groupInfo == null) {
                    result.setSuccess(false);
                    result.setMessage("场景组不存在: " + groupId);
                    return result;
                }

                List<CollaborativeSceneGroupManager.CollaborativeCapabilityInfo> collaborativeScenes = groupInfo.getCollaborativeCapabilities();
                if (collaborativeScenes == null || collaborativeScenes.isEmpty()) {
                    result.setSuccess(true);
                    result.setMessage("场景组中没有协作场景");
                    return result;
                }

                result.setTotalTargets(collaborativeScenes.size());

                for (CollaborativeSceneGroupManager.CollaborativeCapabilityInfo scene : collaborativeScenes) {
                    String sceneId = scene.getCapabilityId();

                    // 跳过发送方自己
                    if (sceneId.equals(fromScene)) {
                        continue;
                    }

                    MessageHandler handler = handlers.get(sceneId);
                    if (handler == null) {
                        result.addFailedTarget(sceneId);
                        continue;
                    }

                    try {
                        SceneMessage sceneMessage = new SceneMessage();
                        sceneMessage.setMessageType(message.getMessageType());
                        sceneMessage.setFromScene(fromScene);
                        sceneMessage.setToScene(sceneId);
                        sceneMessage.setPayload(message.getPayload());
                        sceneMessage.setMetadata(message.getMetadata());

                        HandleResult handleResult = handler.handle(sceneMessage).get();
                        if (handleResult.isSuccess()) {
                            result.setSuccessCount(result.getSuccessCount() + 1);
                            notifyMessageReceived(sceneId, sceneMessage);
                        } else {
                            result.addFailedTarget(sceneId);
                        }
                    } catch (Exception e) {
                        result.addFailedTarget(sceneId);
                    }
                }

                // 调用组消息处理器
                MessageHandler groupHandler = groupHandlers.get(groupId);
                if (groupHandler != null) {
                    try {
                        groupHandler.handle(message).get();
                    } catch (Exception e) {
                        // 组处理器失败不影响广播结果
                    }
                }

                result.setFailureCount(result.getFailedTargets().size());
                result.setSuccess(result.getFailureCount() == 0);
                result.setMessage(result.isSuccess() ? "广播成功" : "部分场景广播失败");

            } catch (Exception e) {
                result.setSuccess(false);
                result.setMessage("广播失败: " + e.getMessage());
            }

            return result;
        });
    }

    @Override
    public void registerHandler(String sceneId, MessageHandler handler) {
        handlers.put(sceneId, handler);
    }

    @Override
    public void unregisterHandler(String sceneId) {
        handlers.remove(sceneId);
    }

    @Override
    public void registerGroupHandler(String groupId, MessageHandler handler) {
        groupHandlers.put(groupId, handler);
    }

    @Override
    public void unregisterGroupHandler(String groupId) {
        groupHandlers.remove(groupId);
    }

    @Override
    public CompletableFuture<SyncResult> syncState(String groupId, Map<String, Object> state) {
        return CompletableFuture.supplyAsync(() -> {
            SyncResult result = new SyncResult();
            result.setGroupId(groupId);

            try {
                // 保存状态
                groupStates.put(groupId, new HashMap<>(state));

                // 同步到场景组
                groupManager.syncGroupState(groupId, state).get();

                // 获取场景组信息
                CollaborativeSceneGroupManager.SceneGroupInfo groupInfo = groupManager.getGroupInfo(groupId).get();
                if (groupInfo != null && groupInfo.getCollaborativeCapabilities() != null) {
                    result.setSyncedScenes(groupInfo.getCollaborativeCapabilities().size());
                }

                result.setSuccess(true);
                result.setMessage("状态同步成功");

                notifyStateSynced(groupId, state);

            } catch (Exception e) {
                result.setSuccess(false);
                result.setMessage("状态同步失败: " + e.getMessage());
            }

            return result;
        });
    }

    @Override
    public CompletableFuture<Map<String, Object>> getGroupState(String groupId) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> state = groupStates.get(groupId);
            if (state == null) {
                // 从groupManager获取
                try {
                    return groupManager.getGroupState(groupId).get();
                } catch (Exception e) {
                    return new HashMap<>();
                }
            }
            return new HashMap<>(state);
        });
    }

    @Override
    public void addCommunicationListener(CommunicationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeCommunicationListener(CommunicationListener listener) {
        listeners.remove(listener);
    }

    // ========== 通知方法 ==========

    private void notifyMessageSent(String fromScene, String toScene, SceneMessage message) {
        for (CommunicationListener listener : listeners) {
            try {
                listener.onMessageSent(fromScene, toScene, message);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }

    private void notifyMessageReceived(String sceneId, SceneMessage message) {
        for (CommunicationListener listener : listeners) {
            try {
                listener.onMessageReceived(sceneId, message);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }

    private void notifyBroadcastSent(String groupId, SceneMessage message) {
        for (CommunicationListener listener : listeners) {
            try {
                listener.onBroadcastSent(groupId, message);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }

    private void notifyStateSynced(String groupId, Map<String, Object> state) {
        for (CommunicationListener listener : listeners) {
            try {
                listener.onStateSynced(groupId, state);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }
}
