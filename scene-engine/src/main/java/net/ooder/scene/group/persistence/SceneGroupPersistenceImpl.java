package net.ooder.scene.group.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.participant.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SceneGroupPersistenceImpl implements SceneGroupPersistence {
    
    private static final Logger logger = LoggerFactory.getLogger(SceneGroupPersistenceImpl.class);
    
    private static final String METADATA_FILE = "metadata.yaml";
    private static final String PARTICIPANTS_FILE = "participants.yaml";
    private static final String CONFIG_FILE = "config.yaml";
    private static final String CAPABILITIES_DIR = "capabilities";
    private static final String KNOWLEDGE_DIR = "knowledge";
    private static final String ARCHIVES_DIR = "archives";
    
    private final ObjectMapper yamlMapper;
    
    public SceneGroupPersistenceImpl() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }
    
    @Override
    public void save(SceneGroup group) throws IOException {
        Path groupDir = BASE_DIR.resolve(group.getSceneGroupId());
        Files.createDirectories(groupDir);
        
        saveMetadata(groupDir, group);
        saveParticipantsInternal(groupDir, group.getAllParticipants());
        saveConfig(groupDir, group);
        
        logger.info("Saved SceneGroup: {}", group.getSceneGroupId());
    }
    
    @Override
    public Optional<SceneGroup> load(String sceneGroupId) throws IOException {
        Path groupDir = BASE_DIR.resolve(sceneGroupId);
        
        if (!Files.exists(groupDir)) {
            return Optional.empty();
        }
        
        return loadMetadata(groupDir, sceneGroupId);
    }
    
    @Override
    public void delete(String sceneGroupId) throws IOException {
        Path groupDir = BASE_DIR.resolve(sceneGroupId);
        
        if (Files.exists(groupDir)) {
            deleteDirectory(groupDir);
            logger.info("Deleted SceneGroup: {}", sceneGroupId);
        }
    }
    
    @Override
    public List<String> listAllSceneGroupIds() throws IOException {
        if (!Files.exists(BASE_DIR)) {
            return Collections.emptyList();
        }
        
        List<String> ids = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(BASE_DIR)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    ids.add(entry.getFileName().toString());
                }
            }
        }
        return ids;
    }
    
    @Override
    public void saveParticipants(String sceneGroupId, List<Participant> participants) throws IOException {
        Path groupDir = BASE_DIR.resolve(sceneGroupId);
        Files.createDirectories(groupDir);
        
        Path participantsFile = groupDir.resolve(PARTICIPANTS_FILE);
        yamlMapper.writeValue(participantsFile.toFile(), participants);
    }
    
    private void saveParticipantsInternal(Path groupDir, List<Participant> participants) throws IOException {
        Path participantsFile = groupDir.resolve(PARTICIPANTS_FILE);
        yamlMapper.writeValue(participantsFile.toFile(), participants);
    }
    
    @Override
    public List<Participant> loadParticipants(String sceneGroupId) throws IOException {
        Path groupDir = BASE_DIR.resolve(sceneGroupId);
        Path participantsFile = groupDir.resolve(PARTICIPANTS_FILE);
        
        if (!Files.exists(participantsFile)) {
            return Collections.emptyList();
        }
        
        return yamlMapper.readValue(participantsFile.toFile(), 
            yamlMapper.getTypeFactory().constructCollectionType(List.class, Participant.class));
    }
    
    private void saveMetadata(Path groupDir, SceneGroup group) throws IOException {
        SceneGroupMetadata metadata = new SceneGroupMetadata();
        metadata.setSceneGroupId(group.getSceneGroupId());
        metadata.setTemplateId(group.getTemplateId());
        metadata.setName(group.getName());
        metadata.setDescription(group.getDescription());
        metadata.setStatus(group.getStatus().name());
        metadata.setCreatorId(group.getCreatorId());
        metadata.setCreatorType(group.getCreatorType().name());
        metadata.setCreateTime(group.getCreateTime().toString());
        metadata.setLastUpdateTime(group.getLastUpdateTime().toString());
        
        Path metadataFile = groupDir.resolve(METADATA_FILE);
        yamlMapper.writeValue(metadataFile.toFile(), metadata);
    }
    
    private Optional<SceneGroup> loadMetadata(Path groupDir, String sceneGroupId) throws IOException {
        Path metadataFile = groupDir.resolve(METADATA_FILE);
        
        if (!Files.exists(metadataFile)) {
            return Optional.empty();
        }
        
        SceneGroupMetadata metadata = yamlMapper.readValue(metadataFile.toFile(), SceneGroupMetadata.class);
        
        SceneGroup group = new SceneGroup(
            metadata.getSceneGroupId(),
            metadata.getTemplateId(),
            metadata.getCreatorId(),
            SceneGroup.CreatorType.valueOf(metadata.getCreatorType())
        );
        
        group.setName(metadata.getName());
        group.setDescription(metadata.getDescription());
        
        return Optional.of(group);
    }
    
    private void saveConfig(Path groupDir, SceneGroup group) throws IOException {
        SceneGroupConfigData config = new SceneGroupConfigData();
        config.setConfig(group.getAllConfig());
        config.setLlmConfig(group.getAllLlmConfig());
        
        Path configFile = groupDir.resolve(CONFIG_FILE);
        yamlMapper.writeValue(configFile.toFile(), config);
    }
    
    private void deleteDirectory(Path dir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    deleteDirectory(entry);
                } else {
                    Files.delete(entry);
                }
            }
        }
        Files.delete(dir);
    }
}
