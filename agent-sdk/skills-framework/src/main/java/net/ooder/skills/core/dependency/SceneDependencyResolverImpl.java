package net.ooder.skills.core.dependency;

import net.ooder.skills.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 场景依赖解析器实现
 * 支持场景模板依赖解析和拓扑排序
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SceneDependencyResolverImpl implements SceneDependencyResolver {

    private static final Logger log = LoggerFactory.getLogger(SceneDependencyResolverImpl.class);

    private final LocalCapabilityRepository capabilityRepository;
    private final Map<String, SceneDependency> dependencies = new ConcurrentHashMap<>();
    private final List<DependencyListener> listeners = new CopyOnWriteArrayList<>();

    public SceneDependencyResolverImpl() {
        this.capabilityRepository = null;
    }

    public SceneDependencyResolverImpl(LocalCapabilityRepository capabilityRepository) {
        this.capabilityRepository = capabilityRepository;
    }

    @Override
    public List<SceneDependency> resolve(SkillManifest manifest) {
        List<SceneDependency> result = new ArrayList<>();

        if (manifest == null) {
            return result;
        }

        List<String> collaborativeScenes = manifest.getCollaborativeScenes();
        if (collaborativeScenes != null) {
            for (String sceneName : collaborativeScenes) {
                net.ooder.skills.core.dependency.SceneDependencyImpl dep = new net.ooder.skills.core.dependency.SceneDependencyImpl(sceneName);
                dep.setRequired(true);
                dep.setStatus(SceneDependency.DependencyStatus.PENDING);
                dependencies.put(sceneName, dep);
                result.add(dep);
            }
        }

        List<SkillManifest.Dependency> deps = manifest.getDependencies();
        if (deps != null) {
            for (SkillManifest.Dependency d : deps) {
                net.ooder.skills.core.dependency.SceneDependencyImpl dep = new net.ooder.skills.core.dependency.SceneDependencyImpl(d.getSkillId());
                dep.setRequired(d.isRequired());
                dep.setStatus(SceneDependency.DependencyStatus.PENDING);
                dependencies.put(d.getSkillId(), dep);
                result.add(dep);
            }
        }

        log.info("Resolved {} dependencies for skill: {}", result.size(), manifest.getSkillId());
        return result;
    }

    @Override
    public List<SceneDependency> resolveFromTemplate(SceneTemplate template) {
        List<SceneDependency> result = new ArrayList<>();

        if (template == null || template.getSkills() == null) {
            return result;
        }

        // 解析模板中的 Skills
        for (SceneTemplate.SkillRef skillRef : template.getSkills()) {
            String skillId = skillRef.getSkillId();
            net.ooder.skills.core.dependency.SceneDependencyImpl dep = new net.ooder.skills.core.dependency.SceneDependencyImpl(skillId);
            dep.setRequired(skillRef.isRequired());
            dep.setStatus(SceneDependency.DependencyStatus.PENDING);

            // 添加版本信息到配置
            Map<String, Object> config = new HashMap<>();
            if (skillRef.getVersion() != null) {
                config.put("version", skillRef.getVersion());
            }
            if (skillRef.getConfig() != null) {
                config.putAll(skillRef.getConfig());
            }
            dep.setConfig(config);

            dependencies.put(skillId, dep);
            result.add(dep);
        }

        // 解析协作场景
        if (template.getCollaborativeScenes() != null) {
            for (SceneTemplate.CollaborativeSceneRef collabRef : template.getCollaborativeScenes()) {
                String sceneId = collabRef.getSceneId();
                net.ooder.skills.core.dependency.SceneDependencyImpl dep = new net.ooder.skills.core.dependency.SceneDependencyImpl(sceneId);
                dep.setRequired(true);
                dep.setStatus(SceneDependency.DependencyStatus.PENDING);

                Map<String, Object> config = new HashMap<>();
                config.put("relation", collabRef.getRelation());
                config.put("bidirectional", collabRef.isBidirectional());
                dep.setConfig(config);

                dependencies.put(sceneId, dep);
                result.add(dep);
            }
        }

        log.info("Resolved {} dependencies from template: {}", result.size(), template.getTemplateId());
        return result;
    }

    @Override
    public List<String> getInstallOrder(SceneTemplate template) {
        if (template == null || template.getSkills() == null) {
            return new ArrayList<>();
        }

        // 构建依赖图
        Map<String, Set<String>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();

        // 初始化
        for (SceneTemplate.SkillRef skillRef : template.getSkills()) {
            String skillId = skillRef.getSkillId();
            graph.put(skillId, new HashSet<>());
            inDegree.put(skillId, 0);
        }

        // 构建依赖关系（简化版本：按 required 字段排序）
        // 实际应该解析依赖关系图
        List<String> requiredSkills = new ArrayList<>();
        List<String> optionalSkills = new ArrayList<>();

        for (SceneTemplate.SkillRef skillRef : template.getSkills()) {
            if (skillRef.isRequired()) {
                requiredSkills.add(skillRef.getSkillId());
            } else {
                optionalSkills.add(skillRef.getSkillId());
            }
        }

        // 先安装 required，再安装 optional
        List<String> result = new ArrayList<>();
        result.addAll(requiredSkills);
        result.addAll(optionalSkills);

        log.info("Calculated install order for template {}: {}", template.getTemplateId(), result);
        return result;
    }

    @Override
    public Map<String, DependencyStatus> checkAllDependencies(SceneTemplate template) {
        Map<String, DependencyStatus> result = new HashMap<>();

        if (template == null || template.getSkills() == null) {
            return result;
        }

        for (SceneTemplate.SkillRef skillRef : template.getSkills()) {
            String skillId = skillRef.getSkillId();
            DependencyStatus status = checkDependencyStatus(skillId, skillRef.getVersion());
            result.put(skillId, status);
        }

        return result;
    }

    /**
     * 检查单个依赖状态
     *
     * @param skillId Skill ID
     * @param requiredVersion 需要的版本
     * @return 依赖状态
     */
    private DependencyStatus checkDependencyStatus(String skillId, String requiredVersion) {
        // 检查本地是否已安装
        if (capabilityRepository != null && capabilityRepository.hasCapability(skillId)) {
            // 版本检查（简化版本）
            if (requiredVersion != null) {
                String installedVersion = capabilityRepository.getCapabilityVersion(skillId);
                if (installedVersion != null && !isVersionCompatible(installedVersion, requiredVersion)) {
                    return DependencyStatus.VERSION_MISMATCH;
                }
            }
            return DependencyStatus.INSTALLED;
        }

        // 检查是否有正在安装的
        SceneDependency dep = dependencies.get(skillId);
        if (dep != null) {
            SceneDependency.DependencyStatus depStatus = dep.getStatus();
            if (depStatus == SceneDependency.DependencyStatus.RESOLVING) {
                return DependencyStatus.INSTALLING;
            } else if (depStatus == SceneDependency.DependencyStatus.FAILED) {
                return DependencyStatus.INSTALL_FAILED;
            }
        }

        return DependencyStatus.NOT_INSTALLED;
    }

    /**
     * 检查版本兼容性
     *
     * @param installedVersion 已安装版本
     * @param requiredVersion 需要的版本
     * @return 是否兼容
     */
    private boolean isVersionCompatible(String installedVersion, String requiredVersion) {
        // 简化版本：直接比较
        // 实际应该使用语义化版本比较
        return installedVersion.equals(requiredVersion) ||
               installedVersion.startsWith(requiredVersion + ".") ||
               requiredVersion.startsWith(installedVersion + ".");
    }

    @Override
    public boolean checkDependencySatisfied(String sceneName) {
        SceneDependency dep = dependencies.get(sceneName);
        if (dep == null) {
            return false;
        }

        if (capabilityRepository != null) {
            for (String capId : dep.getRequiredCapabilities()) {
                if (!capabilityRepository.hasCapability(capId)) {
                    return false;
                }
            }
        }

        dep.setStatus(SceneDependency.DependencyStatus.RESOLVED);
        notifyResolved(sceneName);
        return true;
    }

    @Override
    public List<SceneDependency> getUnsatisfiedDependencies() {
        List<SceneDependency> result = new ArrayList<>();
        for (SceneDependency dep : dependencies.values()) {
            if (dep.getStatus() != SceneDependency.DependencyStatus.RESOLVED) {
                result.add(dep);
            }
        }
        return result;
    }

    @Override
    public List<String> getDependencyOrder() {
        List<String> order = new ArrayList<>();

        List<SceneDependency> pending = new ArrayList<>();
        List<SceneDependency> resolved = new ArrayList<>();

        for (SceneDependency dep : dependencies.values()) {
            if (dep.getStatus() == SceneDependency.DependencyStatus.RESOLVED) {
                resolved.add(dep);
            } else {
                pending.add(dep);
            }
        }

        for (SceneDependency dep : resolved) {
            order.add(dep.getSceneName());
        }
        for (SceneDependency dep : pending) {
            order.add(dep.getSceneName());
        }

        return order;
    }

    @Override
    public void addDependencyListener(DependencyListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeDependencyListener(DependencyListener listener) {
        listeners.remove(listener);
    }

    private void notifyResolved(String sceneName) {
        for (DependencyListener listener : listeners) {
            try {
                listener.onDependencyResolved(sceneName);
            } catch (Exception e) {
                log.warn("DependencyListener error", e);
            }
        }
    }

    private void notifyFailed(String sceneName, String reason) {
        for (DependencyListener listener : listeners) {
            try {
                listener.onDependencyFailed(sceneName, reason);
            } catch (Exception e) {
                log.warn("DependencyListener error", e);
            }
        }
    }
}
