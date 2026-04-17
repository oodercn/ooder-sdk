package net.ooder.sdk.cli.command.skill;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.skills.api.InstalledSkill;
import net.ooder.skills.api.SkillDefinition;
import net.ooder.skills.api.SkillRegistry;
import net.ooder.skills.api.SkillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Skill列表命令
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SkillListCommand implements CliCommand {

    private static final Logger log = LoggerFactory.getLogger(SkillListCommand.class);

    private final SkillRegistry skillRegistry;

    public SkillListCommand(SkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
    }

    @Override
    public String getName() {
        return "skill:list";
    }

    @Override
    public String getDescription() {
        return "List all installed skills";
    }

    @Override
    public String getUsage() {
        return "ooder skill:list [--status <status>] [--type <type>]";
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String statusFilter = context.getAttribute("status");
        String typeFilter = context.getAttribute("type");

        try {
            log.debug("Listing skills with filters - status: {}, type: {}", statusFilter, typeFilter);

            if (skillRegistry == null) {
                log.error("SkillRegistry not injected");
                return CommandResult.error("SkillRegistry not available. Please check configuration.");
            }

            List<InstalledSkill> allSkills = skillRegistry.getInstalledSkills();

            if (allSkills == null) {
                allSkills = new ArrayList<>();
            }

            List<Map<String, Object>> skillList = new ArrayList<>();
            Map<String, Long> statusCounts = new HashMap<>();

            for (InstalledSkill installedSkill : allSkills) {
                String skillId = installedSkill.getSkillId();
                String status = getSkillStatus(skillId);

                if (statusFilter != null && !statusFilter.equalsIgnoreCase(status)) {
                    continue;
                }

                SkillDefinition definition = skillRegistry.getDefinition(skillId);
                String skillType = definition != null ? definition.getSkillType() : "UNKNOWN";
                
                if (typeFilter != null && !typeFilter.equalsIgnoreCase(skillType)) {
                    continue;
                }

                Map<String, Object> skillInfo = new HashMap<>();
                skillInfo.put("skillId", skillId);
                skillInfo.put("name", installedSkill.getName());
                skillInfo.put("version", installedSkill.getVersion());
                skillInfo.put("type", skillType);
                skillInfo.put("status", status);
                skillInfo.put("installTime", installedSkill.getInstallTime());

                skillList.add(skillInfo);

                statusCounts.merge(status, 1L, Long::sum);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", allSkills.size());
            result.put("filtered", skillList.size());
            result.put("skills", skillList);
            result.put("statusCounts", statusCounts);

            log.info("Listed {} skills ({} total)", skillList.size(), allSkills.size());

            return CommandResult.success(
                    String.format("Listed %d skills", skillList.size()),
                    result
            );

        } catch (Exception e) {
            log.error("Failed to list skills", e);
            return CommandResult.error("Failed to list skills: " + e.getMessage(), e);
        }
    }

    private String getSkillStatus(String skillId) {
        if (skillRegistry == null) {
            return "UNKNOWN";
        }

        SkillService service = skillRegistry.getService(skillId);
        if (service == null) {
            return "INSTALLED";
        }

        if (service.isRunning()) {
            return "RUNNING";
        } else {
            return "STOPPED";
        }
    }

    @Override
    public String getCategory() {
        return "skill";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"skills", "list-skills"};
    }
}
