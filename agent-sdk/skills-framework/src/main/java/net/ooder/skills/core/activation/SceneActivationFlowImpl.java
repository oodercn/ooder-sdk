package net.ooder.skills.core.activation;

import net.ooder.skills.api.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 场景激活流程实现
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SceneActivationFlowImpl implements SceneActivationFlow {

    private final SkillRegistry skillRegistry;
    private final CapabilityBindingService bindingService;
    private final CollaborativeSceneGroupManager groupManager;
    private final MainFirstService mainFirstService;
    private final Map<String, SceneState> sceneStates = new HashMap<>();
    private final List<ActivationListener> listeners = new CopyOnWriteArrayList<>();

    public SceneActivationFlowImpl(
            SkillRegistry skillRegistry,
            CapabilityBindingService bindingService,
            CollaborativeSceneGroupManager groupManager,
            MainFirstService mainFirstService) {
        this.skillRegistry = skillRegistry;
        this.bindingService = bindingService;
        this.groupManager = groupManager;
        this.mainFirstService = mainFirstService;
    }

    @Override
    public CompletableFuture<ActivationResult> activate(String sceneId) {
        return activate(sceneId, new ActivationConfig());
    }

    @Override
    public CompletableFuture<ActivationResult> activate(String sceneId, ActivationConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            ActivationResult result = new ActivationResult();
            result.setSceneId(sceneId);
            result.setStartTime(System.currentTimeMillis());

            notifyActivationStarted(sceneId);

            try {
                // Step 1: 检查场景状态
                ActivationStep checkStep = new ActivationStep();
                checkStep.setStepName("CHECK_SCENE_STATUS");
                checkStep.setStatus(ActivationStep.StepStatus.RUNNING);
                checkStep.setStartTime(System.currentTimeMillis());
                notifyActivationStep(sceneId, "CHECK_SCENE_STATUS", ActivationStep.StepStatus.RUNNING);

                SceneState currentState = sceneStates.getOrDefault(sceneId, SceneState.INACTIVE);
                if (currentState == SceneState.ACTIVE) {
                    checkStep.setStatus(ActivationStep.StepStatus.COMPLETED);
                    checkStep.setMessage("场景已经是激活状态");
                    checkStep.setEndTime(System.currentTimeMillis());
                    result.addStep(checkStep);
                    notifyActivationStep(sceneId, "CHECK_SCENE_STATUS", ActivationStep.StepStatus.COMPLETED);

                    result.setSuccess(true);
                    result.setStatus(ActivationStatus.ACTIVE);
                    result.setMessage("场景已经是激活状态");
                    result.setEndTime(System.currentTimeMillis());
                    notifyActivationCompleted(sceneId, result);
                    return result;
                }

                sceneStates.put(sceneId, SceneState.ACTIVATING);
                checkStep.setStatus(ActivationStep.StepStatus.COMPLETED);
                checkStep.setEndTime(System.currentTimeMillis());
                result.addStep(checkStep);
                notifyActivationStep(sceneId, "CHECK_SCENE_STATUS", ActivationStep.StepStatus.COMPLETED);

                // Step 2: 启动场景服务
                ActivationStep startStep = new ActivationStep();
                startStep.setStepName("START_SCENE_SERVICE");
                startStep.setStatus(ActivationStep.StepStatus.RUNNING);
                startStep.setStartTime(System.currentTimeMillis());
                notifyActivationStep(sceneId, "START_SCENE_SERVICE", ActivationStep.StepStatus.RUNNING);

                // 获取场景信息
                SkillManifest manifest = skillRegistry.getSkill(sceneId);
                if (manifest == null) {
                    throw new IllegalStateException("场景不存在: " + sceneId);
                }

                startStep.setStatus(ActivationStep.StepStatus.COMPLETED);
                startStep.setEndTime(System.currentTimeMillis());
                result.addStep(startStep);
                notifyActivationStep(sceneId, "START_SCENE_SERVICE", ActivationStep.StepStatus.COMPLETED);

                // Step 3: 绑定能力（如果启用）
                if (config.isAutoBindCapabilities() && manifest.getCapabilities() != null) {
                    ActivationStep bindStep = new ActivationStep();
                    bindStep.setStepName("BIND_CAPABILITIES");
                    bindStep.setStatus(ActivationStep.StepStatus.RUNNING);
                    bindStep.setStartTime(System.currentTimeMillis());
                    notifyActivationStep(sceneId, "BIND_CAPABILITIES", ActivationStep.StepStatus.RUNNING);

                    List<CapabilityBindingService.CapabilityBinding> bindings = new ArrayList<>();
                    for (Capability capability : manifest.getCapabilities()) {
                        CapabilityBindingService.CapabilityBinding binding = new CapabilityBindingService.CapabilityBinding();
                        binding.setCapabilityId(capability.getCapId());
                        binding.setSkillId(manifest.getSkillId());
                        binding.setStatus(CapabilityBindingService.CapabilityBinding.BindingStatus.PENDING);
                        bindings.add(binding);
                    }

                    CapabilityBindingService.BatchBindingResult bindResult = bindingService.bindCapabilities(
                        sceneId, bindings, config.getInitParams()
                    ).get();

                    if (bindResult.isAllSuccess()) {
                        for (CapabilityBindingService.BindingResult br : bindResult.getResults()) {
                            result.getBoundCapabilities().add(br.getCapabilityId());
                        }
                        bindStep.setStatus(ActivationStep.StepStatus.COMPLETED);
                    } else {
                        bindStep.setStatus(ActivationStep.StepStatus.FAILED);
                        bindStep.setMessage("部分能力绑定失败");
                    }
                    bindStep.setEndTime(System.currentTimeMillis());
                    result.addStep(bindStep);
                    notifyActivationStep(sceneId, "BIND_CAPABILITIES", bindStep.getStatus());
                }

                // Step 4: 检查是否有协作场景
                List<String> collaborativeCapabilities = manifest.getCollaborativeCapabilities();
                boolean hasCollaborative = collaborativeCapabilities != null && !collaborativeCapabilities.isEmpty();

                // Step 5: 创建场景组（如果有协作场景且启用）
                if (hasCollaborative && config.isAutoCreateGroup()) {
                    ActivationStep groupStep = new ActivationStep();
                    groupStep.setStepName("CREATE_SCENE_GROUP");
                    groupStep.setStatus(ActivationStep.StepStatus.RUNNING);
                    groupStep.setStartTime(System.currentTimeMillis());
                    notifyActivationStep(sceneId, "CREATE_SCENE_GROUP", ActivationStep.StepStatus.RUNNING);

                    CollaborativeSceneGroupManager.SceneGroupRequest groupRequest = new CollaborativeSceneGroupManager.SceneGroupRequest();
                    groupRequest.setMainCapabilityId(sceneId);
                    groupRequest.setCollaborativeCapabilityIds(collaborativeCapabilities);
                    groupRequest.setSharedState(config.getInitParams());

                    CollaborativeSceneGroupManager.SceneGroupInfo groupInfo = groupManager.createGroup(groupRequest).get();
                    result.setGroupId(groupInfo.getGroupId());

                    groupStep.setStatus(ActivationStep.StepStatus.COMPLETED);
                    groupStep.setEndTime(System.currentTimeMillis());
                    result.addStep(groupStep);
                    notifyActivationStep(sceneId, "CREATE_SCENE_GROUP", ActivationStep.StepStatus.COMPLETED);
                }

                // Step 6: 建立场景间通信（如果启用）
                if (hasCollaborative && config.isAutoStartCollaboration()) {
                    ActivationStep collabStep = new ActivationStep();
                    collabStep.setStepName("ESTABLISH_COLLABORATION");
                    collabStep.setStatus(ActivationStep.StepStatus.RUNNING);
                    collabStep.setStartTime(System.currentTimeMillis());
                    notifyActivationStep(sceneId, "ESTABLISH_COLLABORATION", ActivationStep.StepStatus.RUNNING);

                    // 启动MainFirst协作
                    if (manifest.getMainFirstScene() != null && manifest.getMainFirstScene().isMainFirst()) {
                        net.ooder.skills.config.CollaborativeConfiguration collabConfig = new net.ooder.skills.config.CollaborativeConfiguration();
                        collabConfig.setCollaborativeCapabilityIds(collaborativeCapabilities);
                        collabConfig.setInitParams(config.getInitParams());
                        collabConfig.setAutoSyncState(true);

                        mainFirstService.startCollaboration(sceneId, collabConfig).get();
                    }

                    result.getCollaborativeScenes().addAll(collaborativeCapabilities);

                    collabStep.setStatus(ActivationStep.StepStatus.COMPLETED);
                    collabStep.setEndTime(System.currentTimeMillis());
                    result.addStep(collabStep);
                    notifyActivationStep(sceneId, "ESTABLISH_COLLABORATION", ActivationStep.StepStatus.COMPLETED);
                }

                // Step 7: 同步初始状态
                ActivationStep syncStep = new ActivationStep();
                syncStep.setStepName("SYNC_INITIAL_STATE");
                syncStep.setStatus(ActivationStep.StepStatus.RUNNING);
                syncStep.setStartTime(System.currentTimeMillis());
                notifyActivationStep(sceneId, "SYNC_INITIAL_STATE", ActivationStep.StepStatus.RUNNING);

                if (result.getGroupId() != null && config.getInitParams() != null) {
                    groupManager.syncGroupState(result.getGroupId(), config.getInitParams()).get();
                }

                syncStep.setStatus(ActivationStep.StepStatus.COMPLETED);
                syncStep.setEndTime(System.currentTimeMillis());
                result.addStep(syncStep);
                notifyActivationStep(sceneId, "SYNC_INITIAL_STATE", ActivationStep.StepStatus.COMPLETED);

                // 更新场景状态
                sceneStates.put(sceneId, SceneState.ACTIVE);

                result.setSuccess(true);
                result.setStatus(ActivationStatus.ACTIVE);
                result.setMessage("场景激活成功");
                result.setEndTime(System.currentTimeMillis());

                notifyActivationCompleted(sceneId, result);

            } catch (Exception e) {
                sceneStates.put(sceneId, SceneState.ERROR);
                result.setSuccess(false);
                result.setStatus(ActivationStatus.FAILED);
                result.setMessage("场景激活失败: " + e.getMessage());
                result.setEndTime(System.currentTimeMillis());
                notifyActivationFailed(sceneId, e.getMessage());
            }

            return result;
        });
    }

    @Override
    public CompletableFuture<DeactivationResult> deactivate(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            DeactivationResult result = new DeactivationResult();
            result.setSceneId(sceneId);
            result.setTimestamp(System.currentTimeMillis());

            try {
                SceneState currentState = sceneStates.getOrDefault(sceneId, SceneState.INACTIVE);
                if (currentState == SceneState.INACTIVE) {
                    result.setSuccess(true);
                    result.setMessage("场景已经是停用状态");
                    return result;
                }

                sceneStates.put(sceneId, SceneState.DEACTIVATING);

                // 1. 停止协作
                mainFirstService.gracefulShutdown(sceneId).get();

                // 2. 解绑能力
                List<CapabilityBindingService.CapabilityBinding> bindings = bindingService.getBindings(sceneId).get();
                if (bindings != null) {
                    for (CapabilityBindingService.CapabilityBinding binding : bindings) {
                        bindingService.unbind(sceneId, binding.getCapabilityId()).get();
                    }
                }

                // 3. 解散场景组
                List<CollaborativeSceneGroupManager.SceneGroupInfo> groups = groupManager.listGroupsByMainCapability(sceneId).get();
                for (CollaborativeSceneGroupManager.SceneGroupInfo group : groups) {
                    groupManager.disbandGroup(group.getGroupId()).get();
                }

                sceneStates.put(sceneId, SceneState.INACTIVE);

                result.setSuccess(true);
                result.setMessage("场景停用成功");

                notifyDeactivationCompleted(sceneId, result);

            } catch (Exception e) {
                sceneStates.put(sceneId, SceneState.ERROR);
                result.setSuccess(false);
                result.setMessage("场景停用失败: " + e.getMessage());
            }

            return result;
        });
    }

    @Override
    public CompletableFuture<SceneState> getSceneState(String sceneId) {
        return CompletableFuture.supplyAsync(() ->
            sceneStates.getOrDefault(sceneId, SceneState.INACTIVE)
        );
    }

    @Override
    public CompletableFuture<Boolean> isActive(String sceneId) {
        return CompletableFuture.supplyAsync(() ->
            sceneStates.getOrDefault(sceneId, SceneState.INACTIVE) == SceneState.ACTIVE
        );
    }

    @Override
    public void addActivationListener(ActivationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeActivationListener(ActivationListener listener) {
        listeners.remove(listener);
    }

    // ========== 通知方法 ==========

    private void notifyActivationStarted(String sceneId) {
        for (ActivationListener listener : listeners) {
            try {
                listener.onActivationStarted(sceneId);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }

    private void notifyActivationStep(String sceneId, String stepName, ActivationStep.StepStatus status) {
        for (ActivationListener listener : listeners) {
            try {
                listener.onActivationStep(sceneId, stepName, status);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }

    private void notifyActivationCompleted(String sceneId, ActivationResult result) {
        for (ActivationListener listener : listeners) {
            try {
                listener.onActivationCompleted(sceneId, result);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }

    private void notifyActivationFailed(String sceneId, String error) {
        for (ActivationListener listener : listeners) {
            try {
                listener.onActivationFailed(sceneId, error);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }

    private void notifyDeactivationCompleted(String sceneId, DeactivationResult result) {
        for (ActivationListener listener : listeners) {
            try {
                listener.onDeactivationCompleted(sceneId, result);
            } catch (Exception e) {
                // 忽略监听器异常
            }
        }
    }
}
