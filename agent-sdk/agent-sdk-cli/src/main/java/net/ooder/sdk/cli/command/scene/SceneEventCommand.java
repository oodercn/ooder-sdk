package net.ooder.sdk.cli.command.scene;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.sdk.cli.adapter.SceneManagerAdapter;
import net.ooder.skills.api.CollaborativeSceneGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 场景事件发布命令
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SceneEventCommand implements CliCommand {

    private static final Logger log = LoggerFactory.getLogger(SceneEventCommand.class);

    private final SceneManagerAdapter sceneManagerAdapter;

    public SceneEventCommand(SceneManagerAdapter sceneManagerAdapter) {
        this.sceneManagerAdapter = sceneManagerAdapter;
    }

    @Override
    public String getName() {
        return "scene:event";
    }

    @Override
    public String getDescription() {
        return "Publish an event to a scene";
    }

    @Override
    public String getUsage() {
        return "ooder scene:event --group-id <id> --type <event-type> [--data key=value ...]";
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String groupId = context.getAttribute("group-id");
        String eventType = context.getAttribute("type");

        if (groupId == null || groupId.isEmpty()) {
            return CommandResult.invalidArgs("Group ID is required (--group-id)");
        }

        if (eventType == null || eventType.isEmpty()) {
            return CommandResult.invalidArgs("Event type is required (--type)");
        }

        try {
            log.info("Publishing event: {} to scene group: {}", eventType, groupId);

            // 获取场景组信息
            SceneManagerAdapter.SceneGroupResult groupResult = sceneManagerAdapter.getSceneGroup(groupId);
            if (!groupResult.isSuccess()) {
                return CommandResult.notFound("Scene group not found: " + groupId);
            }

            CollaborativeSceneGroupManager.SceneGroupInfo groupInfo = groupResult.getGroupInfo();

            // 检查场景状态
            if (!"ACTIVE".equals(groupInfo.getStatus())) {
                return CommandResult.error("Scene is not active: " + groupInfo.getStatus());
            }

            // 提取事件数据
            Map<String, Object> eventData = extractEventData(context);
            eventData.put("eventType", eventType);
            eventData.put("timestamp", System.currentTimeMillis());
            eventData.put("source", "cli");

            // 同步场景状态（实际应该通过事件总线发布）
            SceneManagerAdapter.SceneGroupResult syncResult =
                    sceneManagerAdapter.syncGroupState(groupId, eventData);

            if (!syncResult.isSuccess()) {
                return CommandResult.error("Failed to publish event: " + syncResult.getMessage());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("groupId", groupId);
            response.put("eventType", eventType);
            response.put("data", eventData);
            response.put("status", "PUBLISHED");

            log.info("Event published successfully: {} to scene: {}", eventType, groupId);

            return CommandResult.success(
                    String.format("Event '%s' published successfully", eventType),
                    response
            );

        } catch (Exception e) {
            log.error("Failed to publish event: {} to scene: {}", eventType, groupId, e);
            return CommandResult.error("Event publication failed: " + e.getMessage(), e);
        }
    }

    /**
     * 提取事件数据
     */
    private Map<String, Object> extractEventData(CommandContext context) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> attributes = context.getAttributes();

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("data.")) {
                String dataKey = key.substring(5);
                data.put(dataKey, entry.getValue());
            }
        }

        return data;
    }

    @Override
    public String getCategory() {
        return "scene";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"publish-event", "scene-publish"};
    }

    @Override
    public boolean validate(String[] args) {
        boolean hasGroupId = false;
        boolean hasType = false;

        for (int i = 0; i < args.length; i++) {
            if ("--group-id".equals(args[i]) && i + 1 < args.length) {
                hasGroupId = true;
            }
            if ("--type".equals(args[i]) && i + 1 < args.length) {
                hasType = true;
            }
        }

        return hasGroupId && hasType;
    }
}
