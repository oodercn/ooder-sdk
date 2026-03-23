package net.ooder.scene.core.instance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 内存场景实例存储库实现
 * 
 * <p><strong>⚠️ 警告：此实现仅用于开发和测试环境！</strong></p>
 * 
 * <p><strong>生产环境请使用 {@link SqlSceneInstanceRepository}</strong></p>
 * 
 * <p>限制：</p>
 * <ul>
 *   <li>数据仅存储在内存中，应用重启后丢失</li>
 *   <li>不支持分布式部署</li>
 *   <li>无事务支持</li>
 *   <li>无数据备份能力</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 * @deprecated 生产环境请使用 {@link SqlSceneInstanceRepository}
 * @see SqlSceneInstanceRepository
 */
@Deprecated
public class InMemorySceneInstanceRepository implements SceneInstanceRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemorySceneInstanceRepository.class);

    private final Map<String, SceneInstance> instances = new ConcurrentHashMap<>();

    @Override
    public SceneInstance save(SceneInstance instance) {
        if (instance == null || instance.getInstanceId() == null) {
            throw new IllegalArgumentException("Instance and instanceId must not be null");
        }
        instances.put(instance.getInstanceId(), instance);
        log.debug("[save] Saved instance: {}", instance.getInstanceId());
        return instance;
    }

    @Override
    public Optional<SceneInstance> findById(String instanceId) {
        if (instanceId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(instances.get(instanceId));
    }

    @Override
    public List<SceneInstance> findBySceneId(String sceneId) {
        if (sceneId == null) {
            return Collections.emptyList();
        }
        return instances.values().stream()
                .filter(i -> sceneId.equals(i.getSceneId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SceneInstance> findByTemplateId(String templateId) {
        if (templateId == null) {
            return Collections.emptyList();
        }
        return instances.values().stream()
                .filter(i -> templateId.equals(i.getTemplateId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SceneInstance> findByUserId(String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return instances.values().stream()
                .filter(i -> i.getParticipants().containsKey(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<SceneInstance> findByUserIdAndRoleId(String userId, String roleId) {
        if (userId == null || roleId == null) {
            return Collections.emptyList();
        }
        return instances.values().stream()
                .filter(i -> {
                    SceneInstance.ParticipantInfo p = i.getParticipants().get(userId);
                    return p != null && roleId.equals(p.getRoleId());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SceneInstance> findByState(String state) {
        if (state == null) {
            return Collections.emptyList();
        }
        return instances.values().stream()
                .filter(i -> i.getState() != null && state.equals(i.getState().name()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SceneInstance> findAll() {
        return new ArrayList<>(instances.values());
    }

    @Override
    public boolean deleteById(String instanceId) {
        if (instanceId == null) {
            return false;
        }
        SceneInstance removed = instances.remove(instanceId);
        if (removed != null) {
            log.debug("[deleteById] Deleted instance: {}", instanceId);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateState(String instanceId, String state) {
        SceneInstance instance = instances.get(instanceId);
        if (instance == null || state == null) {
            return false;
        }
        try {
            instance.setState(net.ooder.scene.core.lifecycle.SceneSkillLifecycle.SkillLifecycleState.valueOf(state));
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("[updateState] Invalid state: {}", state);
            return false;
        }
    }

    @Override
    public boolean addParticipant(String instanceId, SceneInstance.ParticipantInfo participant) {
        SceneInstance instance = instances.get(instanceId);
        if (instance == null || participant == null) {
            return false;
        }
        instance.addParticipant(participant);
        return true;
    }

    @Override
    public boolean removeParticipant(String instanceId, String userId) {
        SceneInstance instance = instances.get(instanceId);
        if (instance == null || userId == null) {
            return false;
        }
        instance.removeParticipant(userId);
        return true;
    }

    @Override
    public boolean updateConfig(String instanceId, Map<String, Object> config) {
        SceneInstance instance = instances.get(instanceId);
        if (instance == null) {
            return false;
        }
        if (config != null) {
            instance.getConfig().putAll(config);
        }
        return true;
    }

    @Override
    public boolean addActivationRecord(String instanceId, SceneInstance.ActivationRecord record) {
        SceneInstance instance = instances.get(instanceId);
        if (instance == null || record == null) {
            return false;
        }
        instance.addActivationRecord(record);
        return true;
    }

    @Override
    public long count() {
        return instances.size();
    }

    @Override
    public long countBySceneId(String sceneId) {
        if (sceneId == null) {
            return 0;
        }
        return instances.values().stream()
                .filter(i -> sceneId.equals(i.getSceneId()))
                .count();
    }

    @Override
    public boolean existsById(String instanceId) {
        return instanceId != null && instances.containsKey(instanceId);
    }

    public void clear() {
        instances.clear();
        log.debug("[clear] Cleared all instances");
    }
}
