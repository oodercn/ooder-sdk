package net.ooder.sdk.cli.command.scene;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.sdk.cli.adapter.SceneManagerAdapter;
import net.ooder.skills.api.CollaborativeSceneGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scene列表命令
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SceneListCommand implements CliCommand {

    private static final Logger log = LoggerFactory.getLogger(SceneListCommand.class);

    private final SceneManagerAdapter sceneManagerAdapter;

    public SceneListCommand(SceneManagerAdapter sceneManagerAdapter) {
        this.sceneManagerAdapter = sceneManagerAdapter;
    }

    @Override
    public String getName() {
        return "scene:list";
    }

    @Override
    public String getDescription() {
        return "List all scenes";
    }

    @Override
    public String getUsage() {
        return "ooder scene:list [--status <status>]";
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String statusFilter = context.getAttribute("status");

        try {
            log.debug("Listing scenes with filter - status: {}", statusFilter);

            if (sceneManagerAdapter == null) {
                log.error("SceneManagerAdapter not injected");
                return CommandResult.error("SceneManagerAdapter not available. Please check configuration.");
            }

            SceneManagerAdapter.SceneGroupListResult listResult = sceneManagerAdapter.listSceneGroups();

            if (!listResult.isSuccess()) {
                return CommandResult.error(listResult.getMessage());
            }

            List<CollaborativeSceneGroupManager.SceneGroupInfo> groups = listResult.getGroups();

            if (groups == null) {
                groups = new ArrayList<>();
            }

            List<Map<String, Object>> sceneList = new ArrayList<>();
            Map<String, Long> statusCounts = new HashMap<>();

            for (CollaborativeSceneGroupManager.SceneGroupInfo group : groups) {
                CollaborativeSceneGroupManager.SceneGroupStatus status = group.getStatus();
                String statusStr = status != null ? status.name() : "UNKNOWN";

                if (statusFilter != null && !statusFilter.equalsIgnoreCase(statusStr)) {
                    continue;
                }

                Map<String, Object> sceneInfo = new HashMap<>();
                sceneInfo.put("groupId", group.getGroupId());
                sceneInfo.put("mainCapabilityId", group.getMainCapabilityId());
                
                // 获取协作能力列表
                List<CollaborativeSceneGroupManager.CollaborativeCapabilityInfo> capabilities = 
                    group.getCollaborativeCapabilities();
                List<String> capabilityIds = new ArrayList<>();
                if (capabilities != null) {
                    for (CollaborativeSceneGroupManager.CollaborativeCapabilityInfo cap : capabilities) {
                        if (cap.getCapabilityId() != null) {
                            capabilityIds.add(cap.getCapabilityId());
                        }
                    }
                }
                sceneInfo.put("capabilities", capabilityIds);
                sceneInfo.put("capabilityCount", capabilityIds.size());
                sceneInfo.put("status", statusStr);
                sceneInfo.put("createTime", group.getCreateTime());

                sceneList.add(sceneInfo);

                statusCounts.merge(statusStr, 1L, Long::sum);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", groups.size());
            result.put("filtered", sceneList.size());
            result.put("scenes", sceneList);
            result.put("statusCounts", statusCounts);

            log.info("Listed {} scenes ({} total)", sceneList.size(), groups.size());

            return CommandResult.success(
                    String.format("Listed %d scenes", sceneList.size()),
                    result
            );

        } catch (Exception e) {
            log.error("Failed to list scenes", e);
            return CommandResult.error("Failed to list scenes: " + e.getMessage(), e);
        }
    }

    @Override
    public String getCategory() {
        return "scene";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"scenes", "list-scenes"};
    }
}
