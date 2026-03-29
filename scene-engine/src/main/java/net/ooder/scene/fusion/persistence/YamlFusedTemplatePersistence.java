package net.ooder.scene.fusion.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.ooder.sdk.api.fusion.FusedWorkflowTemplate;
import net.ooder.sdk.api.fusion.TemplateVersion;
import net.ooder.scene.fusion.FusedWorkflowTemplateEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * YAML格式融合模板持久化实现
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class YamlFusedTemplatePersistence {

    private static final Logger log = LoggerFactory.getLogger(YamlFusedTemplatePersistence.class);

    private final ObjectMapper yamlMapper;
    private final Path baseDir;

    public YamlFusedTemplatePersistence() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        String userHome = System.getProperty("user.home");
        this.baseDir = Paths.get(userHome, ".ooder", "fused-templates");
        initDirectory();
    }

    public YamlFusedTemplatePersistence(String basePath) {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.baseDir = Paths.get(basePath, "fused-templates");
        initDirectory();
    }

    private void initDirectory() {
        try {
            Files.createDirectories(baseDir);
            log.debug("Initialized fused templates directory: {}", baseDir);
        } catch (IOException e) {
            log.warn("Failed to create fused templates directory: {}", e.getMessage());
        }
    }

    public void save(FusedWorkflowTemplate template) throws IOException {
        if (template == null || template.getTemplateId() == null) {
            throw new IllegalArgumentException("Template or templateId cannot be null");
        }

        Path templateDir = baseDir.resolve(template.getTemplateId());
        Files.createDirectories(templateDir);

        Path metadataFile = templateDir.resolve("metadata.yaml");
        yamlMapper.writeValue(metadataFile.toFile(), template);

        saveVersion(template);

        log.debug("Saved fused template: {}", template.getTemplateId());
    }

    public Optional<FusedWorkflowTemplate> load(String templateId) throws IOException {
        if (templateId == null) {
            return Optional.empty();
        }

        Path metadataFile = baseDir.resolve(templateId).resolve("metadata.yaml");
        if (!Files.exists(metadataFile)) {
            return Optional.empty();
        }

        FusedWorkflowTemplateEntity template = yamlMapper.readValue(
                metadataFile.toFile(),
                FusedWorkflowTemplateEntity.class
        );

        return Optional.of(template);
    }

    public void delete(String templateId) throws IOException {
        if (templateId == null) {
            return;
        }

        Path templateDir = baseDir.resolve(templateId);
        if (Files.exists(templateDir)) {
            deleteDirectory(templateDir.toFile());
            log.debug("Deleted fused template: {}", templateId);
        }
    }

    public List<FusedWorkflowTemplate> loadAll() throws IOException {
        List<FusedWorkflowTemplate> templates = new ArrayList<>();

        if (!Files.exists(baseDir)) {
            return templates;
        }

        Files.list(baseDir)
                .filter(Files::isDirectory)
                .forEach(dir -> {
                    try {
                        Optional<FusedWorkflowTemplate> template = load(dir.getFileName().toString());
                        template.ifPresent(templates::add);
                    } catch (IOException e) {
                        log.warn("Failed to load fused template from: {}", dir, e);
                    }
                });

        return templates;
    }

    public void saveVersion(FusedWorkflowTemplate template) throws IOException {
        if (template == null || template.getTemplateId() == null) {
            return;
        }

        Path versionsDir = baseDir.resolve(template.getTemplateId()).resolve("versions");
        Files.createDirectories(versionsDir);

        int version = getNextVersion(template.getTemplateId());
        Path versionFile = versionsDir.resolve("v" + version + ".yaml");
        yamlMapper.writeValue(versionFile.toFile(), template);

        log.debug("Saved version {} for template: {}", version, template.getTemplateId());
    }

    public Optional<FusedWorkflowTemplate> loadVersion(String templateId, int version) throws IOException {
        if (templateId == null) {
            return Optional.empty();
        }

        Path versionFile = baseDir.resolve(templateId).resolve("versions").resolve("v" + version + ".yaml");
        if (!Files.exists(versionFile)) {
            return Optional.empty();
        }

        FusedWorkflowTemplateEntity template = yamlMapper.readValue(
                versionFile.toFile(),
                FusedWorkflowTemplateEntity.class
        );

        return Optional.of(template);
    }

    public List<TemplateVersion> loadVersions(String templateId) throws IOException {
        List<TemplateVersion> versions = new ArrayList<>();

        Path versionsDir = baseDir.resolve(templateId).resolve("versions");
        if (!Files.exists(versionsDir)) {
            return versions;
        }

        Files.list(versionsDir)
                .filter(p -> p.getFileName().toString().endsWith(".yaml"))
                .sorted()
                .forEach(p -> {
                    try {
                        String fileName = p.getFileName().toString();
                        String versionStr = fileName.replace("v", "").replace(".yaml", "");
                        int versionNum = Integer.parseInt(versionStr);

                        TemplateVersionEntity version = new TemplateVersionEntity();
                        version.setVersion(versionNum);
                        version.setCreateTime(Files.getLastModifiedTime(p).toMillis());
                        versions.add(version);
                    } catch (Exception e) {
                        log.warn("Failed to load version file: {}", p, e);
                    }
                });

        return versions;
    }

    private int getNextVersion(String templateId) throws IOException {
        List<TemplateVersion> versions = loadVersions(templateId);
        return versions.isEmpty() ? 1 : versions.get(versions.size() - 1).getVersion() + 1;
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    private static class TemplateVersionEntity implements TemplateVersion {
        private int version;
        private String description;
        private Long createTime;
        private String createdBy;

        @Override
        public int getVersion() {
            return version;
        }

        @Override
        public void setVersion(int version) {
            this.version = version;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public Long getCreateTime() {
            return createTime;
        }

        @Override
        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }

        @Override
        public String getCreatedBy() {
            return createdBy;
        }

        @Override
        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }
    }
}
