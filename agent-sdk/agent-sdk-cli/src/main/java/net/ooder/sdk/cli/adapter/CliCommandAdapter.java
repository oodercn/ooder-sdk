package net.ooder.sdk.cli.adapter;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.skills.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * CLI命令适配器
 *
 * <p>将CLI命令转换为Skill调用，复用 SkillInvoker</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class CliCommandAdapter {

    private static final Logger log = LoggerFactory.getLogger(CliCommandAdapter.class);

    private final SkillInvoker skillInvoker;
    private final SkillRegistry skillRegistry;

    public CliCommandAdapter(SkillInvoker skillInvoker, SkillRegistry skillRegistry) {
        this.skillInvoker = skillInvoker;
        this.skillRegistry = skillRegistry;
    }

    /**
     * 调用Skill能力
     *
     * @param skillId Skill ID
     * @param capabilityId 能力ID
     * @param params 参数
     * @return 调用结果
     */
    public CommandResult invokeSkill(String skillId, String capabilityId, Map<String, Object> params) {
        try {
            log.debug("Invoking skill: {} capability: {}", skillId, capabilityId);

            // 检查Skill是否可用
            if (!skillInvoker.isSkillAvailable(skillId)) {
                return CommandResult.notFound("Skill not available: " + skillId);
            }

            // 调用Skill
            Object result = skillInvoker.invoke(skillId, capabilityId, params);

            return CommandResult.success("Skill invoked successfully", result);
        } catch (SkillInvoker.SkillInvocationException e) {
            log.error("Skill invocation failed: {}.{}", skillId, capabilityId, e);
            return CommandResult.error("Skill invocation failed: " + e.getMessage());
        }
    }

    /**
     * 异步调用Skill能力
     *
     * @param skillId Skill ID
     * @param operation 操作名
     * @param params 参数
     * @param callback 回调
     */
    public void invokeSkillAsync(String skillId, String operation, Map<String, Object> params,
                                  SkillCallback callback) {
        try {
            SkillService service = skillRegistry.getService(skillId);
            if (service == null) {
                callback.onError(SkillResponse.error("", "NOT_FOUND", "Skill not found: " + skillId));
                return;
            }

            SkillRequest request = SkillRequest.create();
            request.setSkillId(skillId);
            request.setOperation(operation);
            request.setParams(params);

            service.executeAsync(request, callback);
        } catch (Exception e) {
            log.error("Async skill invocation failed: {}.{}", skillId, operation, e);
            callback.onError(SkillResponse.error("", "ERROR", e.getMessage(), e));
        }
    }

    /**
     * 获取Skill信息
     *
     * @param skillId Skill ID
     * @return Skill信息
     */
    public SkillInvoker.SkillInfo getSkillInfo(String skillId) {
        return skillInvoker.getSkillInfo(skillId);
    }

    /**
     * 检查Skill是否可用
     *
     * @param skillId Skill ID
     * @return 是否可用
     */
    public boolean isSkillAvailable(String skillId) {
        return skillInvoker.isSkillAvailable(skillId);
    }
}
