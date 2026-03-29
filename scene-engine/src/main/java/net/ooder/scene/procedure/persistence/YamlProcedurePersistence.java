package net.ooder.scene.procedure.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.ooder.sdk.api.procedure.EnterpriseProcedure;
import net.ooder.scene.procedure.EnterpriseProcedureEntity;
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
 * YAML格式企业规范流程持久化实现
 *
 * @author SE SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class YamlProcedurePersistence {

    private static final Logger log = LoggerFactory.getLogger(YamlProcedurePersistence.class);

    private final ObjectMapper yamlMapper;
    private final Path baseDir;

    public YamlProcedurePersistence() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        String userHome = System.getProperty("user.home");
        this.baseDir = Paths.get(userHome, ".ooder", "procedures");
        initDirectory();
    }

    public YamlProcedurePersistence(String basePath) {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.baseDir = Paths.get(basePath, "procedures");
        initDirectory();
    }

    private void initDirectory() {
        try {
            Files.createDirectories(baseDir);
            log.debug("Initialized procedure directory: {}", baseDir);
        } catch (IOException e) {
            log.warn("Failed to create procedure directory: {}", e.getMessage());
        }
    }

    public void save(EnterpriseProcedure procedure) throws IOException {
        if (procedure == null || procedure.getProcedureId() == null) {
            throw new IllegalArgumentException("Procedure or procedureId cannot be null");
        }

        Path procedureDir = baseDir.resolve(procedure.getProcedureId());
        Files.createDirectories(procedureDir);

        Path metadataFile = procedureDir.resolve("metadata.yaml");
        yamlMapper.writeValue(metadataFile.toFile(), procedure);

        log.debug("Saved procedure: {}", procedure.getProcedureId());
    }

    public Optional<EnterpriseProcedure> load(String procedureId) throws IOException {
        if (procedureId == null) {
            return Optional.empty();
        }

        Path metadataFile = baseDir.resolve(procedureId).resolve("metadata.yaml");
        if (!Files.exists(metadataFile)) {
            return Optional.empty();
        }

        EnterpriseProcedureEntity procedure = yamlMapper.readValue(
                metadataFile.toFile(),
                EnterpriseProcedureEntity.class
        );

        return Optional.of(procedure);
    }

    public void delete(String procedureId) throws IOException {
        if (procedureId == null) {
            return;
        }

        Path procedureDir = baseDir.resolve(procedureId);
        if (Files.exists(procedureDir)) {
            deleteDirectory(procedureDir.toFile());
            log.debug("Deleted procedure: {}", procedureId);
        }
    }

    public List<EnterpriseProcedure> loadAll() throws IOException {
        List<EnterpriseProcedure> procedures = new ArrayList<>();

        if (!Files.exists(baseDir)) {
            return procedures;
        }

        Files.list(baseDir)
                .filter(Files::isDirectory)
                .forEach(dir -> {
                    try {
                        Optional<EnterpriseProcedure> procedure = load(dir.getFileName().toString());
                        procedure.ifPresent(procedures::add);
                    } catch (IOException e) {
                        log.warn("Failed to load procedure from: {}", dir, e);
                    }
                });

        return procedures;
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
}
