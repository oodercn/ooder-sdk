package net.ooder.scene.fusion.resolver;

import net.ooder.sdk.api.fusion.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 默认冲突解决器实现
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class DefaultConflictResolver {

    private static final Logger log = LoggerFactory.getLogger(DefaultConflictResolver.class);

    public void resolve(FusionConflict conflict, FusionStrategy strategy) {
        if (conflict == null) {
            return;
        }

        ConflictResolution resolution = determineResolution(conflict, strategy);
        conflict.setResolution(resolution);

        switch (resolution) {
            case ConflictResolution.USE_ENTERPRISE:
                conflict.setResolvedValue(conflict.getEnterpriseValue());
                break;
            case ConflictResolution.USE_SKILL:
                conflict.setResolvedValue(conflict.getSkillValue());
                break;
            case ConflictResolution.MERGE:
                conflict.setResolvedValue(mergeValues(conflict.getEnterpriseValue(), conflict.getSkillValue()));
                break;
            default:
                break;
        }

        log.debug("Resolved conflict {} with resolution {}", 
                conflict.getConflictId(), resolution);
    }

    private ConflictResolution determineResolution(FusionConflict conflict, FusionStrategy strategy) {
        FusionPriority priority = getPriorityForField(conflict.getField(), strategy);

        if (priority == null) {
            return ConflictResolution.USE_ENTERPRISE;
        }

        return switch (priority) {
            case ENTERPRISE_FIRST -> ConflictResolution.USE_ENTERPRISE;
            case SKILL_FIRST -> ConflictResolution.USE_SKILL;
            case MERGE -> ConflictResolution.MERGE;
            default -> ConflictResolution.USE_ENTERPRISE;
        };
    }

    private FusionPriority getPriorityForField(String field, FusionStrategy strategy) {
        if (strategy == null) {
            return null;
        }

        return switch (field) {
            case "roles", "role" -> strategy.getRolePriority();
            case "activationSteps", "steps" -> strategy.getActivationStepPriority();
            case "menus", "menu" -> strategy.getMenuPriority();
            case "capabilities", "capability" -> strategy.getCapabilityPriority();
            case "rules", "rule" -> strategy.getRulePriority();
            default -> null;
        };
    }

    private Object mergeValues(Object enterpriseValue, Object skillValue) {
        if (enterpriseValue == null) {
            return skillValue;
        }
        if (skillValue == null) {
            return enterpriseValue;
        }
        return enterpriseValue;
    }
}
