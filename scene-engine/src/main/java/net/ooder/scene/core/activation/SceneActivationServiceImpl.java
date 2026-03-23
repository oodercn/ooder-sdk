package net.ooder.scene.core.activation;

import net.ooder.scene.core.activation.model.ActivationProcess;
import net.ooder.scene.core.menu.MenuGenerationEngine;
import net.ooder.scene.core.menu.MenuItem;
import net.ooder.scene.core.spi.ActivationStepExecutor;
import net.ooder.scene.core.spi.ExtensionPointRegistry;
import net.ooder.scene.core.template.ActivationStepConfig;
import net.ooder.scene.core.template.RoleConfig;
import net.ooder.scene.core.template.SceneTemplate;
import net.ooder.scene.skill.install.SceneConfigLoader;
import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 场景激活服务实现
 *
 * <p>负责场景技能的完整激活流程，包括：</p>
 * <ul>
 *   <li>从 skill.yaml 加载场景配置</li>
 *   <li>执行激活步骤</li>
 *   <li>注册菜单</li>
 *   <li>应用角色配置</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneActivationServiceImpl implements ActivationFlowEngine {

    private static final Logger log = LoggerFactory.getLogger(SceneActivationServiceImpl.class);

    private final SceneConfigLoader sceneConfigLoader;
    private final ExtensionPointRegistry extensionPointRegistry;
    private final MenuGenerationEngine menuGenerationEngine;

    private final Map<String, ActivationProcess> activationProcesses = new ConcurrentHashMap<>();
    private final Map<String, SceneTemplate> sceneTemplates = new ConcurrentHashMap<>();
    private final Map<String, List<ActivationEventListener>> eventListeners = new ConcurrentHashMap<>();

    public SceneActivationServiceImpl(SceneConfigLoader sceneConfigLoader,
                                       ExtensionPointRegistry extensionPointRegistry,
                                       MenuGenerationEngine menuGenerationEngine) {
        this.sceneConfigLoader = sceneConfigLoader;
        this.extensionPointRegistry = extensionPointRegistry;
        this.menuGenerationEngine = menuGenerationEngine;
    }

    @Override
    public CompletableFuture<ActivationResult> startActivation(ActivationRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            ActivationResult result = new ActivationResult();
            result.setSceneId(request.getSceneId());
            result.setUserId(request.getUserId());

            try {
                ActivationProcess process = new ActivationProcess();
                process.setTemplateId(request.getSceneId());
                process.setUserId(request.getUserId());
                process.setRoleId(request.getRole());
                process.start();

                activationProcesses.put(process.getProcessId(), process);

                SceneTemplate template = sceneTemplates.get(request.getSceneId());
                if (template == null) {
                    result.setSuccess(false);
                    result.setErrorMessage("场景模板未找到: " + request.getSceneId());
                    return result;
                }

                executeActivationSteps(process, template, request);

                registerMenus(template, request);

                process.complete();
                result.setActivationId(process.getProcessId());
                result.setSuccess(true);
                result.setCompletedPhase(ActivationPhase.COMPLETED);

                publishEvent(createEvent(
                    process.getProcessId(),
                    request.getSceneId(),
                    ActivationEvent.ActivationEventType.COMPLETED,
                    null,
                    "激活完成"
                ));

                log.info("[startActivation] Activation completed for scene: {}, user: {}",
                    request.getSceneId(), request.getUserId());

            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
                log.error("[startActivation] Activation failed for scene: {}", request.getSceneId(), e);
            }

            return result;
        });
    }

    private ActivationEvent createEvent(String activationId, String sceneId, 
                                        ActivationEvent.ActivationEventType type,
                                        String stepId, String message) {
        ActivationEvent event = new ActivationEvent();
        event.setActivationId(activationId);
        event.setSceneId(sceneId);
        event.setEventType(type);
        event.setStepId(stepId);
        event.setMessage(message);
        event.setTimestamp(System.currentTimeMillis());
        return event;
    }

    private void executeActivationSteps(ActivationProcess process, SceneTemplate template, ActivationRequest request) {
        String roleId = request.getRole();
        if (roleId == null) {
            roleId = determineUserRole(template, request.getUserId());
        }

        List<ActivationStepConfig> steps = template.getActivationStepsForRole(roleId);
        if (steps == null || steps.isEmpty()) {
            log.info("[executeActivationSteps] No activation steps for role: {}", roleId);
            return;
        }

        Map<String, Object> context = new HashMap<>();
        context.put("sceneId", request.getSceneId());
        context.put("userId", request.getUserId());
        context.put("roleId", roleId);

        for (ActivationStepConfig stepConfig : steps) {
            ActivationProcess.StepExecution stepExecution = new ActivationProcess.StepExecution(
                stepConfig.getStepId(),
                stepConfig.getStepName()
            );
            stepExecution.start();
            process.addStepExecution(stepExecution);

            try {
                if (stepConfig.isAutoExecute()) {
                    executeStep(process, stepConfig, context);
                    stepExecution.complete("自动执行完成");
                } else {
                    if (stepConfig.isSkippable()) {
                        log.info("[executeActivationSteps] Step {} is skippable, marking as pending", stepConfig.getStepId());
                        stepExecution.skip();
                    } else {
                        executeStep(process, stepConfig, context);
                        stepExecution.complete("执行完成");
                    }
                }

                publishEvent(createEvent(
                    process.getProcessId(),
                    request.getSceneId(),
                    ActivationEvent.ActivationEventType.STEP_COMPLETED,
                    stepConfig.getStepId(),
                    "步骤完成: " + stepConfig.getStepName()
                ));

            } catch (Exception e) {
                stepExecution.fail(e.getMessage());
                if (stepConfig.isRequired()) {
                    throw new RuntimeException("必需步骤执行失败: " + stepConfig.getStepName(), e);
                }
                log.warn("[executeActivationSteps] Optional step failed: {}", stepConfig.getStepId(), e);
            }
        }
    }

    private void executeStep(ActivationProcess process, ActivationStepConfig stepConfig, Map<String, Object> context) {
        String stepType = stepConfig.getStepType();
        if (stepType == null) {
            stepType = "DEFAULT";
        }

        ActivationStepExecutor executor = findExecutor(stepType);
        if (executor != null && executor.canExecute(stepConfig)) {
            ActivationStepExecutor.StepResult result = executor.execute(stepConfig, process, context);
            if (!result.isSuccess()) {
                throw new RuntimeException("步骤执行失败: " + result.getMessage());
            }
        } else {
            log.info("[executeStep] No executor found for step type: {}, using default handling", stepType);
        }
    }

    private ActivationStepExecutor findExecutor(String stepType) {
        if (extensionPointRegistry == null) {
            return null;
        }
        return extensionPointRegistry.getExtensionByName(ActivationStepExecutor.class, stepType);
    }

    private void registerMenus(SceneTemplate template, ActivationRequest request) {
        if (menuGenerationEngine == null) {
            log.warn("[registerMenus] MenuGenerationEngine not available");
            return;
        }

        Map<String, List<net.ooder.scene.ui.MenuConfig>> menus = template.getMenus();
        if (menus == null || menus.isEmpty()) {
            log.info("[registerMenus] No menus defined in template");
            return;
        }

        String roleId = request.getRole();
        if (roleId == null) {
            roleId = determineUserRole(template, request.getUserId());
        }

        List<net.ooder.scene.ui.MenuConfig> roleMenus = menus.get(roleId);
        if (roleMenus == null || roleMenus.isEmpty()) {
            log.info("[registerMenus] No menus for role: {}", roleId);
            return;
        }

        MenuGenerationEngine.MenuConfig engineConfig = convertToEngineMenuConfig(request.getSceneId(), roleMenus);
        menuGenerationEngine.updateMenuConfig(request.getSceneId(), engineConfig);

        log.info("[registerMenus] Registered {} menus for scene: {}", roleMenus.size(), request.getSceneId());
    }

    private MenuGenerationEngine.MenuConfig convertToEngineMenuConfig(String sceneId, List<net.ooder.scene.ui.MenuConfig> roleMenus) {
        MenuGenerationEngine.MenuConfig config = new MenuGenerationEngine.MenuConfig();
        config.setSceneId(sceneId);
        config.setDynamic(false);
        config.setCacheEnabled(true);
        config.setCacheTtl(3600000);
        
        List<MenuItem> menuItems = new ArrayList<>();
        for (net.ooder.scene.ui.MenuConfig mc : roleMenus) {
            MenuItem item = new MenuItem();
            item.setId(mc.getMenuId());
            item.setName(mc.getTitle());
            item.setIcon(mc.getIcon());
            item.setPath(mc.getPath());
            item.setSort(mc.getOrder());
            menuItems.add(item);
        }
        
        return config;
    }

    private String determineUserRole(SceneTemplate template, String userId) {
        List<RoleConfig> roles = template.getRoles();
        if (roles == null || roles.isEmpty()) {
            return "DEFAULT";
        }

        for (RoleConfig role : roles) {
            if (role.isRequired()) {
                return role.getRoleId();
            }
        }

        return roles.get(0).getRoleId();
    }

    public void registerSceneTemplate(String sceneId, SceneTemplate template) {
        sceneTemplates.put(sceneId, template);
        log.info("[registerSceneTemplate] Registered template for scene: {}", sceneId);
    }

    public void registerSceneTemplate(String sceneId, SkillPackage skillPackage) {
        SceneTemplate template = sceneConfigLoader.loadSceneConfig(sceneId, skillPackage);
        if (template != null) {
            sceneTemplates.put(sceneId, template);
            log.info("[registerSceneTemplate] Loaded and registered template for scene: {}", sceneId);
        }
    }

    public SceneTemplate getSceneTemplate(String sceneId) {
        return sceneTemplates.get(sceneId);
    }

    @Override
    public ActivationStatus getActivationStatus(String activationId) {
        ActivationProcess process = activationProcesses.get(activationId);
        if (process == null) {
            return null;
        }

        ActivationStatus status = new ActivationStatus();
        status.setActivationId(activationId);
        status.setPhase(convertPhase(process.getStatus()));
        status.setTotalSteps(process.getTotalStepCount());
        status.setCompletedSteps(process.getCompletedStepCount());
        status.setProgress(process.getTotalStepCount() > 0
            ? (double) process.getCompletedStepCount() / process.getTotalStepCount()
            : 0);
        status.setStartTime(process.getCreatedAt());

        return status;
    }

    private ActivationPhase convertPhase(ActivationProcess.ProcessStatus processStatus) {
        switch (processStatus) {
            case CREATED: return ActivationPhase.INITIALIZING;
            case EXECUTING: return ActivationPhase.ACTIVATING;
            case COMPLETED: return ActivationPhase.COMPLETED;
            case FAILED: return ActivationPhase.FAILED;
            case CANCELLED: return ActivationPhase.FAILED;
            default: return ActivationPhase.INITIALIZING;
        }
    }

    @Override
    public boolean cancelActivation(String activationId) {
        ActivationProcess process = activationProcesses.get(activationId);
        if (process == null) {
            return false;
        }
        process.fail("用户取消");
        return true;
    }

    @Override
    public boolean pauseActivation(String activationId) {
        ActivationProcess process = activationProcesses.get(activationId);
        if (process == null) {
            log.warn("[pauseActivation] Activation process not found: {}", activationId);
            return false;
        }
        
        if (process.getStatus() != ActivationProcess.ProcessStatus.EXECUTING) {
            log.warn("[pauseActivation] Cannot pause activation in status: {}", process.getStatus());
            return false;
        }
        
        process.pause();
        log.info("[pauseActivation] Activation paused: {}", activationId);
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("reason", "user_requested");
        eventData.put("pausedAt", System.currentTimeMillis());
        
        return true;
    }

    @Override
    public boolean resumeActivation(String activationId) {
        ActivationProcess process = activationProcesses.get(activationId);
        if (process == null) {
            log.warn("[resumeActivation] Activation process not found: {}", activationId);
            return false;
        }
        
        if (process.getStatus() != ActivationProcess.ProcessStatus.PAUSED) {
            log.warn("[resumeActivation] Cannot resume activation in status: {}", process.getStatus());
            return false;
        }
        
        process.resume();
        log.info("[resumeActivation] Activation resumed: {}", activationId);
        
        return true;
    }

    @Override
    public CompletableFuture<StepResult> executeStep(String activationId, String stepId, Map<String, Object> input) {
        return CompletableFuture.supplyAsync(() -> {
            StepResult result = new StepResult();
            result.setStepId(stepId);

            ActivationProcess process = activationProcesses.get(activationId);
            if (process == null) {
                result.setSuccess(false);
                result.setErrorMessage("激活流程不存在");
                return result;
            }

            SceneTemplate template = sceneTemplates.get(process.getTemplateId());
            if (template == null) {
                result.setSuccess(false);
                result.setErrorMessage("场景模板不存在");
                return result;
            }

            try {
                ActivationStepConfig stepConfig = findStepConfig(template, process.getRoleId(), stepId);
                if (stepConfig == null) {
                    result.setSuccess(false);
                    result.setErrorMessage("步骤配置不存在");
                    return result;
                }

                Map<String, Object> context = new HashMap<>(input);
                context.put("sceneId", process.getTemplateId());
                context.put("userId", process.getUserId());
                context.put("roleId", process.getRoleId());

                executeStep(process, stepConfig, context);

                result.setSuccess(true);
                result.setStepName(stepConfig.getStepName());

            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
            }

            return result;
        });
    }

    private ActivationStepConfig findStepConfig(SceneTemplate template, String roleId, String stepId) {
        List<ActivationStepConfig> steps = template.getActivationStepsForRole(roleId);
        if (steps == null) {
            return null;
        }
        return steps.stream()
            .filter(s -> s.getStepId().equals(stepId))
            .findFirst()
            .orElse(null);
    }

    @Override
    public boolean skipStep(String activationId, String stepId, String reason) {
        ActivationProcess process = activationProcesses.get(activationId);
        if (process == null) {
            return false;
        }

        for (ActivationProcess.StepExecution step : process.getSteps()) {
            if (step.getStepId().equals(stepId)) {
                step.skip();
                return true;
            }
        }
        return false;
    }

    @Override
    public CompletableFuture<StepResult> retryStep(String activationId, String stepId) {
        return executeStep(activationId, stepId, new HashMap<>());
    }

    @Override
    public ActivationProgress getProgress(String activationId) {
        ActivationProcess process = activationProcesses.get(activationId);
        if (process == null) {
            return null;
        }

        ActivationProgress progress = new ActivationProgress();
        progress.setActivationId(activationId);
        progress.setStartTime(process.getCreatedAt());
        progress.setElapsedTime(System.currentTimeMillis() - process.getCreatedAt());

        List<ActivationProgress.StepProgress> stepProgressList = new ArrayList<>();
        int currentIndex = 0;
        for (int i = 0; i < process.getSteps().size(); i++) {
            ActivationProcess.StepExecution exec = process.getSteps().get(i);
            ActivationProgress.StepProgress sp = new ActivationProgress.StepProgress();
            sp.setStepId(exec.getStepId());
            sp.setStepName(exec.getStepName());
            sp.setStatus(convertStepStatus(exec.getStatus()));
            sp.setStartTime(exec.getStartedAt());
            sp.setEndTime(exec.getCompletedAt());
            stepProgressList.add(sp);

            if (exec.getStatus() == ActivationProcess.StepStatus.EXECUTING) {
                currentIndex = i;
            }
        }
        progress.setSteps(stepProgressList);
        progress.setCurrentStepIndex(currentIndex);

        return progress;
    }

    private ActivationProgress.StepProgress.StepStatus convertStepStatus(ActivationProcess.StepStatus status) {
        switch (status) {
            case PENDING: return ActivationProgress.StepProgress.StepStatus.PENDING;
            case EXECUTING: return ActivationProgress.StepProgress.StepStatus.RUNNING;
            case COMPLETED: return ActivationProgress.StepProgress.StepStatus.COMPLETED;
            case FAILED: return ActivationProgress.StepProgress.StepStatus.FAILED;
            case SKIPPED: return ActivationProgress.StepProgress.StepStatus.SKIPPED;
            default: return ActivationProgress.StepProgress.StepStatus.PENDING;
        }
    }

    @Override
    public List<ActivationRecord> getActivationHistory(String sceneId, String userId) {
        return new ArrayList<>();
    }

    @Override
    public String subscribeActivationEvent(String activationId, ActivationEventListener listener) {
        eventListeners.computeIfAbsent(activationId, k -> new ArrayList<>()).add(listener);
        return UUID.randomUUID().toString();
    }

    @Override
    public void unsubscribeActivationEvent(String subscriptionId) {
    }

    private void publishEvent(ActivationEvent event) {
        List<ActivationEventListener> listeners = eventListeners.get(event.getActivationId());
        if (listeners != null) {
            for (ActivationEventListener listener : listeners) {
                try {
                    listener.onActivationEvent(event);
                } catch (Exception e) {
                    log.warn("[publishEvent] Listener error", e);
                }
            }
        }
    }
}
