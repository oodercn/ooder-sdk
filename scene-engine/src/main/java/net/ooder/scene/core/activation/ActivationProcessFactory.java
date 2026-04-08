package net.ooder.scene.core.activation;

import net.ooder.scene.core.activation.model.ActivationProcess;
import net.ooder.scene.core.activation.model.ActivationRequest;
import net.ooder.scene.core.activation.model.NetworkAction;
import net.ooder.scene.core.template.ActivationStepConfig;
import net.ooder.scene.core.template.SceneTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 激活流程工厂
 *
 * <p>用于创建激活流程实例，支持根据模板和角色创建不同的激活步骤</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class ActivationProcessFactory {
    
    public static final String STEP_CONFIRM_PARTICIPANTS = "confirm-participants";
    public static final String STEP_SELECT_PUSH_TARGETS = "select-push-targets";
    public static final String STEP_CONFIG_CONDITIONS = "config-conditions";
    public static final String STEP_GET_KEY = "get-key";
    public static final String STEP_CONFIG_PRIVATE_CAPABILITIES = "config-private-capabilities";
    public static final String STEP_CONFIRM_ACTIVATION = "confirm-activation";
    public static final String STEP_NETWORK_ACTIONS = "network-actions";
    public static final String STEP_CONFIRM_TASK = "confirm-task";
    
    public static final String ACTION_NOTIFY_OTHER_SCENES = "notify-other-scenes";
    public static final String ACTION_UPDATE_MY_CAPABILITIES = "update-my-capabilities";
    public static final String ACTION_UPDATE_MY_TODOS = "update-my-todos";
    public static final String ACTION_NOTIFY_COLLABORATORS = "notify-collaborators";
    
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_LEADER = "LEADER";
    public static final String ROLE_EMPLOYEE = "EMPLOYEE";
    
    /**
     * 根据请求创建激活流程
     */
    public static ActivationProcess create(ActivationRequest request) {
        ActivationProcess process = new ActivationProcess();
        process.setTemplateId(request.getTemplateId());
        process.setSceneGroupId(request.getSceneGroupId());
        process.setUserId(request.getActivator());
        process.setRoleName(request.getRoleName());
        process.setLeaderId(request.getLeaderId());
        process.setCollaboratorIds(request.getCollaboratorIds());
        process.setEnabledPrivateCapabilities(request.getEnabledPrivateCapabilities());
        
        List<ActivationProcess.StepExecution> steps = createStepsForRole(request.getRoleName());
        process.setSteps(steps);
        process.setTotalSteps(steps.size());
        
        List<NetworkAction> networkActions = createDefaultNetworkActions();
        process.setNetworkActions(networkActions);
        
        return process;
    }
    
    /**
     * 根据模板和角色创建激活流程
     */
    public static ActivationProcess create(SceneTemplate template, String roleName, String userId) {
        ActivationProcess process = new ActivationProcess();
        process.setTemplateId(template.getTemplateId());
        process.setUserId(userId);
        process.setRoleName(roleName);
        
        List<ActivationProcess.StepExecution> steps;
        List<ActivationStepConfig> stepConfigs = template.getActivationStepsForRole(roleName);
        
        if (stepConfigs != null && !stepConfigs.isEmpty()) {
            steps = convertStepConfigs(stepConfigs);
        } else {
            steps = createStepsForRole(roleName);
        }
        
        process.setSteps(steps);
        process.setTotalSteps(steps.size());
        
        List<NetworkAction> networkActions = createDefaultNetworkActions();
        process.setNetworkActions(networkActions);
        
        List<SceneTemplate.PrivateCapabilityConfig> privateCapabilities = convertPrivateCapabilities(
            template.getPrivateCapabilities());
        process.setPrivateCapabilities(privateCapabilities);
        
        return process;
    }
    
    /**
     * 创建默认的激活流程
     */
    public static ActivationProcess createDefault(String templateId, String userId) {
        ActivationRequest request = ActivationRequest.builder()
            .templateId(templateId)
            .activator(userId)
            .roleName(ROLE_MANAGER)
            .build();
        return create(request);
    }
    
    private static List<ActivationProcess.StepExecution> createStepsForRole(String roleName) {
        List<ActivationProcess.StepExecution> steps = new ArrayList<>();
        
        if (ROLE_MANAGER.equals(roleName) || ROLE_LEADER.equals(roleName)) {
            steps.add(createStep(STEP_CONFIRM_PARTICIPANTS, "确认参与者", true, false, false));
            steps.add(createStep(STEP_SELECT_PUSH_TARGETS, "选择推送目标", true, false, false));
            steps.add(createStep(STEP_CONFIG_CONDITIONS, "配置驱动条件", true, false, false));
            steps.add(createStep(STEP_GET_KEY, "获取密钥", true, false, true));
            steps.add(createStep(STEP_CONFIRM_ACTIVATION, "确认激活", true, false, false));
            steps.add(createStep(STEP_NETWORK_ACTIONS, "入网动作", true, false, true));
        } else if (ROLE_EMPLOYEE.equals(roleName)) {
            steps.add(createStep(STEP_CONFIRM_TASK, "确认任务", true, false, false));
            steps.add(createStep(STEP_CONFIG_PRIVATE_CAPABILITIES, "配置私有能力", false, true, false));
            steps.add(createStep(STEP_GET_KEY, "获取密钥", true, false, true));
            steps.add(createStep(STEP_CONFIRM_ACTIVATION, "确认激活", true, false, false));
        } else {
            steps.add(createStep(STEP_CONFIRM_PARTICIPANTS, "确认参与者", true, false, false));
            steps.add(createStep(STEP_GET_KEY, "获取密钥", true, false, true));
            steps.add(createStep(STEP_CONFIRM_ACTIVATION, "确认激活", true, false, false));
            steps.add(createStep(STEP_NETWORK_ACTIONS, "入网动作", true, false, true));
        }
        
        return steps;
    }
    
    private static ActivationProcess.StepExecution createStep(
            String stepId, String stepName, 
            boolean required, boolean skippable, boolean autoExecute) {
        ActivationProcess.StepExecution step = new ActivationProcess.StepExecution(stepId, stepName);
        return step;
    }
    
    private static List<NetworkAction> createDefaultNetworkActions() {
        List<NetworkAction> actions = new ArrayList<>();
        
        actions.add(new NetworkAction(ACTION_NOTIFY_OTHER_SCENES, "通知其他场景", NetworkAction.TYPE_NOTIFICATION));
        actions.add(new NetworkAction(ACTION_UPDATE_MY_CAPABILITIES, "更新我的能力", NetworkAction.TYPE_UPDATE));
        actions.add(new NetworkAction(ACTION_UPDATE_MY_TODOS, "更新我的待办", NetworkAction.TYPE_UPDATE));
        actions.add(new NetworkAction(ACTION_NOTIFY_COLLABORATORS, "通知协作者", NetworkAction.TYPE_NOTIFICATION));
        
        return actions;
    }
    
    private static List<ActivationProcess.StepExecution> convertStepConfigs(List<ActivationStepConfig> configs) {
        List<ActivationProcess.StepExecution> steps = new ArrayList<>();
        for (ActivationStepConfig config : configs) {
            ActivationProcess.StepExecution step = new ActivationProcess.StepExecution(
                config.getStepId(), 
                config.getName()
            );
            steps.add(step);
        }
        return steps;
    }
    
    private static List<SceneTemplate.PrivateCapabilityConfig> convertPrivateCapabilities(
            List<SceneTemplate.PrivateCapabilityConfig> templateCaps) {
        if (templateCaps == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(templateCaps);
    }
}
