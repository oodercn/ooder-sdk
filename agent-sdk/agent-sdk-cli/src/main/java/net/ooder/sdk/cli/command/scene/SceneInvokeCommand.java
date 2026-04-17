package net.ooder.sdk.cli.command.scene;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.sdk.cli.adapter.SceneManagerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 场景能力调用命令
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SceneInvokeCommand implements CliCommand {

    private static final Logger log = LoggerFactory.getLogger(SceneInvokeCommand.class);

    private final SceneManagerAdapter sceneManagerAdapter;

    public SceneInvokeCommand(SceneManagerAdapter sceneManagerAdapter) {
        this.sceneManagerAdapter = sceneManagerAdapter;
    }

    @Override
    public String getName() {
        return "scene:invoke";
    }

    @Override
    public String getDescription() {
        return "Invoke a capability in a scene";
    }

    @Override
    public String getUsage() {
        return "ooder scene:invoke --group-id <id> --capability <capability-id> [--param key=value ...]";
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String groupId = context.getAttribute("group-id");
        String capabilityId = context.getAttribute("capability");

        if (groupId == null || groupId.isEmpty()) {
            return CommandResult.invalidArgs("Group ID is required (--group-id)");
        }

        if (capabilityId == null || capabilityId.isEmpty()) {
            return CommandResult.invalidArgs("Capability ID is required (--capability)");
        }

        try {
            log.info("Invoking capability: {} in scene group: {}", capabilityId, groupId);

            if (sceneManagerAdapter == null) {
                log.error("SceneManagerAdapter not injected");
                return CommandResult.error("SceneManagerAdapter not available. Please check configuration.");
            }

            // 提取参数
            Map<String, Object> params = extractParams(context);

            // 调用能力 - 使用真正的实现
            SceneManagerAdapter.InvokeResult invokeResult = 
                    sceneManagerAdapter.invokeCapability(groupId, capabilityId, params);

            if (!invokeResult.isSuccess()) {
                return CommandResult.error(invokeResult.getError());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("groupId", groupId);
            response.put("capabilityId", capabilityId);
            response.put("result", invokeResult.getResult());
            response.put("timestamp", System.currentTimeMillis());

            log.info("Capability invoked successfully: {} in scene: {}", capabilityId, groupId);

            return CommandResult.success(
                    String.format("Capability '%s' invoked successfully", capabilityId),
                    response
            );

        } catch (Exception e) {
            log.error("Failed to invoke capability: {} in scene: {}", capabilityId, groupId, e);
            return CommandResult.error("Capability invocation failed: " + e.getMessage(), e);
        }
    }

    /**
     * 提取参数
     */
    private Map<String, Object> extractParams(CommandContext context) {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> attributes = context.getAttributes();

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("param.")) {
                String paramName = key.substring(6);
                params.put(paramName, entry.getValue());
            }
        }

        return params;
    }

    @Override
    public String getCategory() {
        return "scene";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"invoke-capability", "scene-call"};
    }

    @Override
    public boolean validate(String[] args) {
        boolean hasGroupId = false;
        boolean hasCapability = false;

        for (int i = 0; i < args.length; i++) {
            if ("--group-id".equals(args[i]) && i + 1 < args.length) {
                hasGroupId = true;
            }
            if ("--capability".equals(args[i]) && i + 1 < args.length) {
                hasCapability = true;
            }
        }

        return hasGroupId && hasCapability;
    }
}
