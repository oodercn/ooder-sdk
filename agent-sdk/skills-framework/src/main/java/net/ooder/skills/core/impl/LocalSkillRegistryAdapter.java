package net.ooder.skills.core.impl;

import net.ooder.skills.api.InstalledSkill;
import net.ooder.skills.api.SkillDefinition;
import net.ooder.skills.api.SkillManifest;
import net.ooder.skills.api.SkillPackage;
import net.ooder.skills.api.SkillRegistry;
import net.ooder.skills.api.SkillService;
import net.ooder.skills.store.SkillRegistration;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LocalSkillRegistryAdapter implements SkillRegistry {
    
    private final LocalSkillRegistry localRegistry;
    
    public LocalSkillRegistryAdapter(LocalSkillRegistry localRegistry) {
        this.localRegistry = localRegistry;
    }
    
    @Override
    public SkillManifest getSkill(String skillId) {
        SkillPackage pkg = localRegistry.get(skillId);
        return pkg != null ? pkg.getManifest() : null;
    }
    
    @Override
    public List<String> getAvailableVersions(String skillId) {
        SkillPackage pkg = localRegistry.get(skillId);
        if (pkg != null && pkg.getVersion() != null) {
            return Collections.singletonList(pkg.getVersion());
        }
        return Collections.emptyList();
    }
    
    @Override
    public String getLatestVersion(String skillId) {
        SkillPackage pkg = localRegistry.get(skillId);
        return pkg != null ? pkg.getVersion() : null;
    }
    
    @Override
    public CompletableFuture<String> register(SkillDefinition definition, SkillService service) {
        throw new UnsupportedOperationException("Not implemented in LocalSkillRegistryAdapter");
    }
    
    @Override
    public CompletableFuture<Void> unregister(String skillId) {
        throw new UnsupportedOperationException("Not implemented in LocalSkillRegistryAdapter");
    }
    
    @Override
    public SkillService getService(String skillId) {
        throw new UnsupportedOperationException("Not implemented in LocalSkillRegistryAdapter");
    }
    
    @Override
    public SkillDefinition getDefinition(String skillId) {
        throw new UnsupportedOperationException("Not implemented in LocalSkillRegistryAdapter");
    }
    
    @Override
    public SkillRegistration getRegistration(String skillId) {
        throw new UnsupportedOperationException("Not implemented in LocalSkillRegistryAdapter");
    }
    
    @Override
    public List<SkillService> getServices(String sceneId, String groupId) {
        return Collections.emptyList();
    }
    
    @Override
    public List<SkillService> getServicesByType(String sceneId, String skillType) {
        return Collections.emptyList();
    }
    
    @Override
    public SkillService getServiceByType(String sceneId, String skillType) {
        return null;
    }
    
    @Override
    public List<SkillDefinition> listDefinitions(String sceneId) {
        return Collections.emptyList();
    }
    
    @Override
    public List<SkillRegistration> listRegistrations(String sceneId, String groupId) {
        return Collections.emptyList();
    }
    
    @Override
    public Map<String, Object> getEndpoints(String skillId) {
        return Collections.emptyMap();
    }
    
    @Override
    public void sendHeartbeat(String skillId) {
    }
    
    @Override
    public boolean isAlive(String skillId) {
        return localRegistry.has(skillId);
    }
    
    @Override
    public boolean isAlive(String skillId, long timeoutMs) {
        return localRegistry.has(skillId);
    }
    
    @Override
    public void start() {
    }
    
    @Override
    public void stop() {
    }
    
    @Override
    public List<InstalledSkill> getInstalledSkills() {
        return Collections.emptyList();
    }
    
    @Override
    public InstalledSkill getInstalledSkill(String skillId) {
        return null;
    }
}
