package net.ooder.scene.group.archive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SceneGroupManager;
import net.ooder.scene.group.persistence.SceneGroupPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SceneGroupArchiver {
    
    private static final Logger logger = LoggerFactory.getLogger(SceneGroupArchiver.class);
    
    private static final String ARCHIVES_DIR = "archives";
    private static final DateTimeFormatter ARCHIVE_ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    
    private final SceneGroupManager sceneGroupManager;
    private final SceneGroupPersistence persistence;
    private final ObjectMapper yamlMapper;
    
    public SceneGroupArchiver(SceneGroupManager sceneGroupManager, SceneGroupPersistence persistence) {
        this.sceneGroupManager = sceneGroupManager;
        this.persistence = persistence;
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }
    
    public ArchiveResult archive(String sceneGroupId, String description) {
        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (group == null) {
            return ArchiveResult.failure("SceneGroup not found: " + sceneGroupId);
        }
        
        if (group.getStatus() != SceneGroup.Status.ACTIVE && 
            group.getStatus() != SceneGroup.Status.SUSPENDED) {
            return ArchiveResult.failure("SceneGroup must be ACTIVE or SUSPENDED to archive, current: " + group.getStatus());
        }
        
        try {
            String archiveId = generateArchiveId(sceneGroupId);
            
            ArchiveMetadata metadata = new ArchiveMetadata();
            metadata.setArchiveId(archiveId);
            metadata.setSceneGroupId(sceneGroupId);
            metadata.setArchiveTime(LocalDateTime.now());
            metadata.setArchiveType(ArchiveMetadata.ArchiveType.USER_INITIATED);
            metadata.setDescription(description);
            
            Map<String, Object> runtimeData = extractRuntimeData(group);
            metadata.setRuntimeData(runtimeData);
            metadata.setDataSize(calculateDataSize(runtimeData));
            
            saveArchive(sceneGroupId, metadata);
            
            clearRuntimeData(group);
            
            group.archive();
            persistence.save(group);
            
            logger.info("Archived SceneGroup: {} -> {}", sceneGroupId, archiveId);
            
            return ArchiveResult.success(archiveId, metadata);
            
        } catch (Exception e) {
            logger.error("Failed to archive SceneGroup: " + sceneGroupId, e);
            return ArchiveResult.failure("Archive failed: " + e.getMessage());
        }
    }
    
    public ArchiveResult restore(String sceneGroupId, String archiveId) {
        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        if (group == null) {
            return ArchiveResult.failure("SceneGroup not found: " + sceneGroupId);
        }
        
        if (group.getStatus() != SceneGroup.Status.ARCHIVED) {
            return ArchiveResult.failure("SceneGroup must be ARCHIVED to restore, current: " + group.getStatus());
        }
        
        try {
            ArchiveMetadata metadata = loadArchive(sceneGroupId, archiveId);
            if (metadata == null) {
                return ArchiveResult.failure("Archive not found: " + archiveId);
            }
            
            restoreRuntimeData(group, metadata.getRuntimeData());
            
            group.activate();
            persistence.save(group);
            
            logger.info("Restored SceneGroup: {} from {}", sceneGroupId, archiveId);
            
            return ArchiveResult.success(archiveId, metadata);
            
        } catch (Exception e) {
            logger.error("Failed to restore SceneGroup: " + sceneGroupId, e);
            return ArchiveResult.failure("Restore failed: " + e.getMessage());
        }
    }
    
    public List<ArchiveMetadata> listArchives(String sceneGroupId) throws IOException {
        Path archivesDir = getArchivesDir(sceneGroupId);
        if (!Files.exists(archivesDir)) {
            return Collections.emptyList();
        }
        
        List<ArchiveMetadata> archives = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(archivesDir, "*.yaml")) {
            for (Path entry : stream) {
                try {
                    ArchiveMetadata metadata = yamlMapper.readValue(entry.toFile(), ArchiveMetadata.class);
                    archives.add(metadata);
                } catch (Exception e) {
                    logger.warn("Failed to load archive: {}", entry, e);
                }
            }
        }
        
        archives.sort((a, b) -> b.getArchiveTime().compareTo(a.getArchiveTime()));
        return archives;
    }
    
    public boolean deleteArchive(String sceneGroupId, String archiveId) throws IOException {
        Path archiveFile = getArchiveFile(sceneGroupId, archiveId);
        if (Files.exists(archiveFile)) {
            Files.delete(archiveFile);
            logger.info("Deleted archive: {} for SceneGroup: {}", archiveId, sceneGroupId);
            return true;
        }
        return false;
    }
    
    private String generateArchiveId(String sceneGroupId) {
        return "archive-" + LocalDateTime.now().format(ARCHIVE_ID_FORMATTER);
    }
    
    private Map<String, Object> extractRuntimeData(SceneGroup group) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("executionHistory", group.getConfig("executionHistory"));
        data.put("sessionContext", group.getConfig("sessionContext"));
        data.put("temporaryState", group.getConfig("temporaryState"));
        data.put("lastExecutionTime", group.getConfig("lastExecutionTime"));
        data.put("statistics", group.getConfig("statistics"));
        
        return data;
    }
    
    private void clearRuntimeData(SceneGroup group) {
        group.setConfig("executionHistory", null);
        group.setConfig("sessionContext", null);
        group.setConfig("temporaryState", null);
        group.setConfig("lastExecutionTime", null);
        group.setConfig("statistics", null);
    }
    
    private void restoreRuntimeData(SceneGroup group, Map<String, Object> runtimeData) {
        if (runtimeData == null) return;
        
        for (Map.Entry<String, Object> entry : runtimeData.entrySet()) {
            group.setConfig(entry.getKey(), entry.getValue());
        }
    }
    
    private long calculateDataSize(Map<String, Object> data) {
        if (data == null) return 0;
        return data.toString().length();
    }
    
    private void saveArchive(String sceneGroupId, ArchiveMetadata metadata) throws IOException {
        Path archivesDir = getArchivesDir(sceneGroupId);
        Files.createDirectories(archivesDir);
        
        Path archiveFile = archivesDir.resolve(metadata.getArchiveId() + ".yaml");
        yamlMapper.writeValue(archiveFile.toFile(), metadata);
    }
    
    private ArchiveMetadata loadArchive(String sceneGroupId, String archiveId) throws IOException {
        Path archiveFile = getArchiveFile(sceneGroupId, archiveId);
        if (!Files.exists(archiveFile)) {
            return null;
        }
        return yamlMapper.readValue(archiveFile.toFile(), ArchiveMetadata.class);
    }
    
    private Path getArchivesDir(String sceneGroupId) {
        return SceneGroupPersistence.BASE_DIR.resolve(sceneGroupId).resolve(ARCHIVES_DIR);
    }
    
    private Path getArchiveFile(String sceneGroupId, String archiveId) {
        return getArchivesDir(sceneGroupId).resolve(archiveId + ".yaml");
    }
    
    public static class ArchiveResult {
        private final boolean success;
        private final String archiveId;
        private final ArchiveMetadata metadata;
        private final String errorMessage;
        
        private ArchiveResult(boolean success, String archiveId, ArchiveMetadata metadata, String errorMessage) {
            this.success = success;
            this.archiveId = archiveId;
            this.metadata = metadata;
            this.errorMessage = errorMessage;
        }
        
        public static ArchiveResult success(String archiveId, ArchiveMetadata metadata) {
            return new ArchiveResult(true, archiveId, metadata, null);
        }
        
        public static ArchiveResult failure(String errorMessage) {
            return new ArchiveResult(false, null, null, errorMessage);
        }
        
        public boolean isSuccess() { return success; }
        public String getArchiveId() { return archiveId; }
        public ArchiveMetadata getMetadata() { return metadata; }
        public String getErrorMessage() { return errorMessage; }
    }
}
