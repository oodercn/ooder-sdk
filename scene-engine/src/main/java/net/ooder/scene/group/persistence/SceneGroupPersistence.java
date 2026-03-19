package net.ooder.scene.group.persistence;

import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.participant.Participant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public interface SceneGroupPersistence {
    
    Path BASE_DIR = Paths.get(System.getProperty("user.home"), ".ooder", "scene-groups");
    
    void save(SceneGroup group) throws IOException;
    
    Optional<SceneGroup> load(String sceneGroupId) throws IOException;
    
    void delete(String sceneGroupId) throws IOException;
    
    List<String> listAllSceneGroupIds() throws IOException;
    
    void saveParticipants(String sceneGroupId, List<Participant> participants) throws IOException;
    
    List<Participant> loadParticipants(String sceneGroupId) throws IOException;
}
