
package net.ooder.skills.api;

import net.ooder.skills.common.enums.DiscoveryMethod;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public interface SkillPackageManager {
    
    CompletableFuture<SkillPackage> discover(String skillId, DiscoveryMethod method);
    
    CompletableFuture<List<SkillPackage>> discoverAll(DiscoveryMethod method);
    
    CompletableFuture<List<SkillPackage>> discoverByScene(String sceneId, DiscoveryMethod method);
    
    CompletableFuture<InstallResult> install(InstallRequest request);
    
    CompletableFuture<UninstallResult> uninstall(String skillId);
    
    CompletableFuture<UpdateResult> update(String skillId, String version);
    
    CompletableFuture<List<InstalledSkill>> listInstalled();
    
    CompletableFuture<InstalledSkill> getInstalled(String skillId);
    
    CompletableFuture<Boolean> isInstalled(String skillId);
    
    CompletableFuture<SkillPackage> getPackage(String skillId);
    
    CompletableFuture<SkillManifest> getManifest(String skillId);
    
    CompletableFuture<Void> registerObserver(SkillPackageObserver observer);
    
    CompletableFuture<Void> unregisterObserver(SkillPackageObserver observer);
    
    CompletableFuture<List<SkillPackage>> search(String query, DiscoveryMethod method);
    
    CompletableFuture<List<SkillPackage>> searchByCapability(String capabilityId, DiscoveryMethod method);
    
    String getSkillRootPath();
    
    void setSkillRootPath(String path);
    
    CompletableFuture<Void> updateConfig(String skillId, Map<String, String> config);
    
    CompletableFuture<Map<String, String>> getConfig(String skillId);
    
    CompletableFuture<InstallProgress> getInstallProgress(String installId);
    
    CompletableFuture<List<InstallProgress>> getActiveInstalls();
    
    CompletableFuture<DependencyInfo> getDependencies(String skillId);
    
    CompletableFuture<DependencyResult> installDependencies(String skillId);
    
    CompletableFuture<DependencyResult> updateDependencies(String skillId);
    
    CompletableFuture<Boolean> checkDependencySatisfied(String skillId);
    
    CompletableFuture<List<InterfaceDefinition>> getProvidedInterfaces(String skillId);
    
    CompletableFuture<List<InterfaceDependency>> getRequiredInterfaces(String skillId);
    
    CompletableFuture<Boolean> registerInterfaceProvider(String skillId, InterfaceDefinition interfaceDef);
    
    CompletableFuture<Boolean> unregisterInterfaceProvider(String skillId, String interfaceId);
    
    CompletableFuture<List<String>> findSkillsProvidingInterface(String interfaceId);
    
    CompletableFuture<List<String>> findSkillsProvidingInterface(String interfaceId, String version);
    
    CompletableFuture<Boolean> validateInterfaceCompatibility(String skillId, String interfaceId);
    
    CompletableFuture<InterfaceResolutionResult> resolveInterfaces(String skillId);
    
    class InterfaceResolutionResult {
        private boolean success;
        private List<String> resolvedInterfaces;
        private List<String> unresolvedInterfaces;
        private Map<String, String> interfaceToSkill;
        private String errorMessage;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public List<String> getResolvedInterfaces() { return resolvedInterfaces; }
        public void setResolvedInterfaces(List<String> resolvedInterfaces) { this.resolvedInterfaces = resolvedInterfaces; }
        
        public List<String> getUnresolvedInterfaces() { return unresolvedInterfaces; }
        public void setUnresolvedInterfaces(List<String> unresolvedInterfaces) { this.unresolvedInterfaces = unresolvedInterfaces; }
        
        public Map<String, String> getInterfaceToSkill() { return interfaceToSkill; }
        public void setInterfaceToSkill(Map<String, String> interfaceToSkill) { this.interfaceToSkill = interfaceToSkill; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}
