package net.ooder.sdk.cli.command.scene;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.sdk.cli.adapter.SceneManagerAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * 场景创建命令
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SceneCreateCommand implements CliCommand {

    private final SceneManagerAdapter sceneManagerAdapter;

    public SceneCreateCommand(SceneManagerAdapter sceneManagerAdapter) {
        this.sceneManagerAdapter = sceneManagerAdapter;
    }

    @Override
    public String getName() {
        return "scene:create";
    }

    @Override
    public String getDescription() {
        return "Create a new scene group";
    }

    @Override
    public String getUsage() {
        return "ooder scene:create --group-id <id> --main <capability> [--collaborative <cap1,cap2,...>]";
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String groupId = context.getAttribute("group-id");
        String mainCapability = context.getAttribute("main");
        String collaborativeStr = context.getAttribute("collaborative");

        if (groupId == null || groupId.isEmpty()) {
            return CommandResult.invalidArgs("Group ID is required");
        }

        if (mainCapability == null || mainCapability.isEmpty()) {
            return CommandResult.invalidArgs("Main capability is required");
        }

        List<String> collaborativeCapabilities = collaborativeStr != null && !collaborativeStr.isEmpty()
                ? Arrays.asList(collaborativeStr.split(","))
                : java.util.Collections.emptyList();

        // 复用 SceneManagerAdapter 创建场景组
        SceneManagerAdapter.SceneGroupResult result = sceneManagerAdapter.createSceneGroup(
                groupId, mainCapability, collaborativeCapabilities);

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
        return new String[]{"create-scene"};
    }
}
