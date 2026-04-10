package net.ooder.skill.common.spi.storage;

import java.util.List;
import java.util.Optional;

public interface SceneGroupStorage {
    
    SceneGroupData save(SceneGroupData data);
    
    Optional<SceneGroupData> findById(String id);
    
    PageResult<SceneGroupData> findByOwnerId(String ownerId, int pageNum, int pageSize);
    
    List<SceneGroupData> findByStatus(String status);
    
    List<SceneGroupData> findAll();
    
    void deleteById(String id);
    
    long count();
    
    boolean existsById(String id);
}
