package net.ooder.scene.core.activation.executor;

import net.ooder.scene.core.spi.org.OrganizationService;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.StepResult;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 选择推送目标执行器
 *
 * <p>支持USER和DEPARTMENT两种推送类型，DEPARTMENT类型会自动展开获取所有成员。</p>
 * <p>需要通过ServiceLocator注入OrganizationService以支持部门成员展开。</p>
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class SelectPushTargetsExecutor implements EnhancedActivationStepExecutor {

    private static final Logger log = LoggerFactory.getLogger(SelectPushTargetsExecutor.class);

    public static final String STEP_TYPE = "select-push-targets";
    public static final String TARGET_TYPE_USER = "USER";
    public static final String TARGET_TYPE_DEPARTMENT = "DEPARTMENT";

    private OrganizationService organizationService;

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public OrganizationService getOrganizationService() {
        return organizationService;
    }

    @Override
    public String getStepType() {
        return STEP_TYPE;
    }

    @Override
    public boolean canExecute(Map<String, Object> stepConfig) {
        return stepConfig != null && stepConfig.containsKey("targetType");
    }

    @Override
    public StepResult execute(Map<String, Object> stepConfig, 
                               Map<String, Object> process, 
                               Map<String, Object> context) {
        try {
            String targetType = getConfigString(stepConfig, "targetType");
            List<String> targetIds = getTargetIds(stepConfig);
            String sceneGroupId = (String) process.get("sceneGroupId");

            log.info("Selecting push targets for scene: {} with type: {}", sceneGroupId, targetType);

            if (targetIds.isEmpty()) {
                return StepResult.failure(
                    ExecutorErrorCodes.TARGETS_REQUIRED + ": 未选择推送目标"
                );
            }

            Map<String, Object> data = new HashMap<>();
            data.put("sceneGroupId", sceneGroupId);
            data.put("targetType", targetType);
            data.put("targetIds", targetIds);
            data.put("selectedAt", System.currentTimeMillis());

            if (TARGET_TYPE_DEPARTMENT.equals(targetType) && organizationService != null) {
                List<String> allMemberIds = new ArrayList<>();
                List<String> invalidDepartments = new ArrayList<>();
                
                for (String deptId : targetIds) {
                    try {
                        List<String> members = organizationService.getDepartmentMembers(deptId);
                        if (members != null && !members.isEmpty()) {
                            allMemberIds.addAll(members);
                        } else {
                            var dept = organizationService.getDepartment(deptId);
                            if (dept == null) {
                                invalidDepartments.add(deptId);
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Failed to get members for department: {}", deptId, e);
                        invalidDepartments.add(deptId);
                    }
                }

                if (!invalidDepartments.isEmpty()) {
                    StepResult result = StepResult.failure(
                        ExecutorErrorCodes.DEPARTMENT_NOT_FOUND + ": 以下部门不存在: " + String.join(", ", invalidDepartments)
                    );
                    result.setData(Map.of("invalidDepartments", invalidDepartments));
                    return result;
                }

                data.put("allMemberIds", allMemberIds);
                data.put("totalMemberCount", allMemberIds.size());
                log.info("Expanded {} departments to {} members", targetIds.size(), allMemberIds.size());
            }

            StepResult result = StepResult.success("Push targets selected: " + targetIds.size() + " targets");
            result.setData(data);
            return result;
        } catch (Exception e) {
            log.error("Failed to select push targets", e);
            return StepResult.failure(
                ExecutorErrorCodes.EXECUTION_ERROR + ": Failed to select push targets: " + e.getMessage()
            );
        }
    }

    @Override
    public ValidationResult validateInput(Map<String, Object> stepConfig,
                                           Map<String, Object> input) {
        List<String> errors = new ArrayList<>();

        if (!stepConfig.containsKey("targetType")) {
            errors.add("Missing required field: targetType");
        }

        String targetType = getConfigString(stepConfig, "targetType");
        if (targetType != null && !TARGET_TYPE_USER.equals(targetType) && !TARGET_TYPE_DEPARTMENT.equals(targetType)) {
            errors.add("Invalid targetType: " + targetType + ", must be USER or DEPARTMENT");
        }

        if (!errors.isEmpty()) {
            return ValidationResult.failure(errors);
        }

        return ValidationResult.success();
    }

    @SuppressWarnings("unchecked")
    private List<String> getTargetIds(Map<String, Object> stepConfig) {
        Object targetIds = stepConfig.get("targetIds");
        if (targetIds instanceof List) {
            return (List<String>) targetIds;
        }
        return Collections.emptyList();
    }

    protected String getConfigString(Map<String, Object> stepConfig, String key) {
        Object value = stepConfig != null ? stepConfig.get(key) : null;
        return value != null ? value.toString() : null;
    }
}
