package net.ooder.sdk.cli.command.scene;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.sdk.cli.adapter.SceneManagerAdapter;

/**
 * 场景信息命令
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SceneInfoCommand implements CliCommand {

    private final SceneManagerAdapter sceneManagerAdapter;

    public SceneInfoCommand(SceneManagerAdapter sceneManagerAdapter) {
        this.sceneManagerAdapter = sceneManagerAdapter;
    }

    @Override
    public String getName() {
        return "scene:info";
    }

    @Override
    public String getDescription() {
        return "Show scene group information";
    }

    @Override
    public String getUsage() {
        return "ooder scene:info --group-id <id>";
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String groupId = context.getAttribute("group-id");

        if (groupId == null || groupId.isEmpty()) {
            return CommandResult.invalidArgs("Group ID is required");
        }

        // 复用 SceneManagerAdapter 获取场景组信息
        SceneManagerAdapter.SceneGroupResult result = sceneManagerAdapter.getSceneGroup(groupId);

        if (result.isSuccess()) {
            return CommandResult.success(result.getMessage(), result.getGroupInfo());
        } else {
            return CommandResult.error(result.getMessage());
        }
    }

    @Override
    public String getCategory() {
        return "scene";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"scene-info", "show-scene"};
    }
}
