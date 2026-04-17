package net.ooder.sdk.cli.command.skill;

import net.ooder.sdk.cli.api.CliCommand;
import net.ooder.sdk.cli.api.CommandContext;
import net.ooder.sdk.cli.api.CommandResult;
import net.ooder.skills.api.InstallResult;
import net.ooder.skills.api.SkillInstaller;
import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Skill安装命令
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class SkillInstallCommand implements CliCommand {

    private static final Logger log = LoggerFactory.getLogger(SkillInstallCommand.class);

    private final SkillInstaller skillInstaller;

    public SkillInstallCommand(SkillInstaller skillInstaller) {
        this.skillInstaller = skillInstaller;
    }

    @Override
    public String getName() {
        return "skill:install";
    }

    @Override
    public String getDescription() {
        return "Install a skill from file or URL";
    }

    @Override
    public String getUsage() {
        return "ooder skill:install --source <path|url> [--force]";
    }

    @Override
    public CommandResult execute(CommandContext context) {
        String source = context.getAttribute("source");
        boolean force = Boolean.parseBoolean(context.getAttribute("force"));

        if (source == null || source.isEmpty()) {
            return CommandResult.invalidArgs("Source path or URL is required (--source)");
        }

        try {
            log.info("Installing skill from: {}", source);

            // 判断是本地文件还是URL
            if (source.startsWith("http://") || source.startsWith("https://")) {
                return installFromUrl(source, force, context);
            } else {
                return installFromFile(source, force, context);
            }

        } catch (Exception e) {
            log.error("Failed to install skill from: {}", source, e);
            return CommandResult.error("Installation failed: " + e.getMessage(), e);
        }
    }

    /**
     * 从本地文件安装
     */
    private CommandResult installFromFile(String filePath, boolean force, CommandContext context) {
        Path path = Paths.get(filePath);
        File file = path.toFile();

        if (!file.exists()) {
            return CommandResult.notFound("File not found: " + filePath);
        }

        try {
            // 创建 SkillPackage
            SkillPackage skillPackage = new SkillPackage();
            skillPackage.setSource(filePath);
            
            // 执行安装 - 使用异步 API
            CompletableFuture<InstallResult> future = skillInstaller.install(skillPackage, 
                force ? SkillInstaller.InstallMode.FORCE : SkillInstaller.InstallMode.NORMAL);
            
            InstallResult installResult = future.get();
            
            if (!installResult.isSuccess()) {
                return CommandResult.error("Installation failed: " + installResult.getError());
            }

            Map<String, Object> result = new HashMap<>();
            result.put("skillId", installResult.getSkillId());
            result.put("version", installResult.getVersion());
            result.put("status", "INSTALLED");
            result.put("location", filePath);

            log.info("Skill installed successfully: {} v{}", installResult.getSkillId(), installResult.getVersion());

            return CommandResult.success(
                    String.format("Skill installed: %s v%s", installResult.getSkillId(), installResult.getVersion()),
                    result
            );
        } catch (Exception e) {
            log.error("Failed to install skill from file: {}", filePath, e);
            return CommandResult.error("Installation failed: " + e.getMessage(), e);
        }
    }

    /**
     * 从URL安装
     */
    private CommandResult installFromUrl(String url, boolean force, CommandContext context) {
        log.info("Installing skill from URL: {}", url);

        try {
            // 创建 SkillPackage
            SkillPackage skillPackage = new SkillPackage();
            skillPackage.setDownloadUrl(url);
            
            // 执行安装 - 使用异步 API
            CompletableFuture<InstallResult> future = skillInstaller.install(skillPackage, 
                force ? SkillInstaller.InstallMode.FORCE : SkillInstaller.InstallMode.NORMAL);
            
            InstallResult installResult = future.get();
            
            if (!installResult.isSuccess()) {
                return CommandResult.error("Installation failed: " + installResult.getError());
            }

            Map<String, Object> result = new HashMap<>();
            result.put("skillId", installResult.getSkillId());
            result.put("version", installResult.getVersion());
            result.put("status", "INSTALLED");
            result.put("source", url);

            log.info("Skill installed successfully from URL: {} v{}", installResult.getSkillId(), installResult.getVersion());

            return CommandResult.success(
                    String.format("Skill installed from URL: %s v%s", installResult.getSkillId(), installResult.getVersion()),
                    result
            );
        } catch (Exception e) {
            log.error("Failed to install skill from URL: {}", url, e);
            return CommandResult.error("Installation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String getCategory() {
        return "skill";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"install-skill"};
    }

    @Override
    public boolean validate(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("--source".equals(args[i]) && i + 1 < args.length) {
                return true;
            }
        }
        return false;
    }
}
