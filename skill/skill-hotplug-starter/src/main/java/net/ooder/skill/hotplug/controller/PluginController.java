package net.ooder.skill.hotplug.controller;

import net.ooder.skill.hotplug.PluginManager;
import net.ooder.skill.hotplug.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件管理REST API
 */
@RestController
@RequestMapping("/api/plugins")
public class PluginController {

    private static final Logger logger = LoggerFactory.getLogger(PluginController.class);

    @Autowired
    private PluginManager pluginManager;

    /**
     * 获取所有已安装的插件
     */
    @GetMapping
    public ResponseEntity<List<PluginInfo>> listPlugins() {
        List<PluginInfo> plugins = pluginManager.getInstalledSkills();
        return ResponseEntity.ok(plugins);
    }

    /**
     * 获取指定插件信息
     */
    @GetMapping("/{skillId}")
    public ResponseEntity<PluginInfo> getPlugin(@PathVariable String skillId) {
        PluginInfo plugin = pluginManager.getSkillInfo(skillId);
        if (plugin == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(plugin);
    }

    /**
     * 上传并安装插件
     */
    @PostMapping("/install")
    public ResponseEntity<PluginInstallResult> installPlugin(@RequestParam("file") MultipartFile file) {
        try {
            // 保存上传的文件
            Path tempDir = Files.createTempDirectory("skill-upload");
            Path targetPath = tempDir.resolve(file.getOriginalFilename());
            file.transferTo(targetPath.toFile());

            // 加载并安装
            SkillPackage skillPackage = SkillPackage.fromFile(targetPath.toFile());
            PluginInstallResult result = pluginManager.installSkill(skillPackage);

            // 清理临时文件
            Files.deleteIfExists(targetPath);
            Files.deleteIfExists(tempDir);

            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }

        } catch (IOException e) {
            logger.error("Failed to install plugin", e);
            return ResponseEntity.badRequest()
                    .body(PluginInstallResult.failure("unknown", e.getMessage()));
        }
    }

    /**
     * 卸载插件
     */
    @PostMapping("/{skillId}/uninstall")
    public ResponseEntity<PluginUninstallResult> uninstallPlugin(@PathVariable String skillId) {
        PluginUninstallResult result = pluginManager.uninstallSkill(skillId);
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 更新插件
     */
    @PostMapping("/{skillId}/update")
    public ResponseEntity<PluginUpdateResult> updatePlugin(
            @PathVariable String skillId,
            @RequestParam("file") MultipartFile file) {
        try {
            // 保存上传的文件
            Path tempDir = Files.createTempDirectory("skill-update");
            Path targetPath = tempDir.resolve(file.getOriginalFilename());
            file.transferTo(targetPath.toFile());

            // 加载并更新
            SkillPackage skillPackage = SkillPackage.fromFile(targetPath.toFile());
            PluginUpdateResult result = pluginManager.updateSkill(skillId, skillPackage);

            // 清理临时文件
            Files.deleteIfExists(targetPath);
            Files.deleteIfExists(tempDir);

            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }

        } catch (IOException e) {
            logger.error("Failed to update plugin", e);
            return ResponseEntity.badRequest()
                    .body(PluginUpdateResult.failure(skillId, e.getMessage()));
        }
    }

    /**
     * 获取插件状态
     */
    @GetMapping("/{skillId}/status")
    public ResponseEntity<Map<String, Object>> getPluginStatus(@PathVariable String skillId) {
        PluginInfo plugin = pluginManager.getSkillInfo(skillId);
        if (plugin == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> status = new HashMap<>();
        status.put("skillId", skillId);
        status.put("state", plugin.getState());
        status.put("installed", true);
        status.put("installTime", plugin.getInstallTime());

        return ResponseEntity.ok(status);
    }

    /**
     * 检查插件是否已安装
     */
    @GetMapping("/{skillId}/check")
    public ResponseEntity<Map<String, Object>> checkPlugin(@PathVariable String skillId) {
        boolean installed = pluginManager.isInstalled(skillId);

        Map<String, Object> result = new HashMap<>();
        result.put("skillId", skillId);
        result.put("installed", installed);

        return ResponseEntity.ok(result);
    }
}
