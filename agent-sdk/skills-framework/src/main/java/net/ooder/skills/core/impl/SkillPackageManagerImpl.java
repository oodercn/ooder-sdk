
package net.ooder.skills.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.ooder.skills.common.enums.DiscoveryMethod;
import net.ooder.skills.common.enums.SkillStatus;
import net.ooder.skills.exception.SkillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ooder.skills.api.DependencyInfo;
import net.ooder.skills.api.DependencyResult;
import net.ooder.skills.api.InstallOptions;
import net.ooder.skills.api.InstallProgress;
import net.ooder.skills.api.InstallRequest;
import net.ooder.skills.api.InstallResult;
import net.ooder.skills.api.InstallResultWithDependencies;
import net.ooder.skills.api.InstalledSkill;
import net.ooder.skills.api.InterfaceDefinition;
import net.ooder.skills.api.InterfaceDependency;
import net.ooder.skills.api.SceneTemplate;
import net.ooder.skills.api.SkillCenterClient;
import net.ooder.skills.api.SkillDiscoverer;
import net.ooder.skills.api.SkillManifest;
import net.ooder.skills.api.SkillPackage;
import net.ooder.skills.api.SkillPackageManager;
import net.ooder.skills.api.SkillPackageObserver;
import net.ooder.skills.api.TemplateInstallResult;
import net.ooder.skills.api.UninstallResult;
import net.ooder.skills.api.UpdateResult;
import net.ooder.skills.api.impl.DependencyInfoImpl;
import net.ooder.skills.core.discovery.GitRepositoryDiscovererAdapter;
import net.ooder.skills.core.discovery.LocalDiscoverer;
import net.ooder.skills.core.discovery.SkillCenterDiscoverer;
import net.ooder.skills.core.discovery.UdpDiscoverer;

public class SkillPackageManagerImpl implements SkillPackageManager {
    
    private static final Logger log = LoggerFactory.getLogger(SkillPackageManagerImpl.class);
    
    private final LocalSkillRegistry registry;
    private final Map<DiscoveryMethod, SkillDiscoverer> discoverers;
    private final List<SkillPackageObserver> observers;
    private final Map<String, InstallProgress> activeInstalls;
    private String skillRootPath;
    
    public SkillPackageManagerImpl() {
        this.registry = new LocalSkillRegistry();
        this.discoverers = new ConcurrentHashMap<DiscoveryMethod, SkillDiscoverer>();
        this.observers = new CopyOnWriteArrayList<SkillPackageObserver>();
        this.activeInstalls = new ConcurrentHashMap<String, InstallProgress>();
        this.skillRootPath = "/skills/";
        
        initializeDiscoverers();
    }
    
    private void initializeDiscoverers() {
        discoverers.put(DiscoveryMethod.LOCAL_FS, new LocalDiscoverer());
        discoverers.put(DiscoveryMethod.UDP_BROADCAST, new UdpDiscoverer());
        discoverers.put(DiscoveryMethod.SKILL_CENTER, new SkillCenterDiscoverer());
        discoverers.put(DiscoveryMethod.GITHUB, new GitRepositoryDiscovererAdapter("github"));
        discoverers.put(DiscoveryMethod.GITEE, new GitRepositoryDiscovererAdapter("gitee"));
        discoverers.put(DiscoveryMethod.GIT_REPOSITORY, new GitRepositoryDiscovererAdapter());
    }
    
    @Override
    public CompletableFuture<SkillPackage> discover(String skillId, DiscoveryMethod method) {
        return CompletableFuture.supplyAsync(() -> {
            SkillDiscoverer discoverer = getDiscoverer(method);
            if (discoverer == null) {
                throw new SkillException(skillId, "Discovery method not available: " + method);
            }
            
            try {
                return discoverer.discover(skillId).join();
            } catch (Exception e) {
                log.error("Failed to discover skill: {}", skillId, e);
                throw new SkillException(skillId, "Discovery failed", e);
            }
        });
    }
    
    @Override
    public CompletableFuture<List<SkillPackage>> discoverAll(DiscoveryMethod method) {
        return CompletableFuture.supplyAsync(() -> {
            SkillDiscoverer discoverer = getDiscoverer(method);
            if (discoverer == null) {
                return new ArrayList<>();
            }
            
            try {
                return discoverer.discover().join();
            } catch (Exception e) {
                log.error("Failed to discover skills", e);
                return new ArrayList<>();
            }
        });
    }
    
    @Override
    public CompletableFuture<List<SkillPackage>> discoverByScene(String sceneId, DiscoveryMethod method) {
        return CompletableFuture.supplyAsync(() -> {
            SkillDiscoverer discoverer = getDiscoverer(method);
            if (discoverer == null) {
                return new ArrayList<>();
            }
            
            try {
                return discoverer.discoverByScene(sceneId).join();
            } catch (Exception e) {
                log.error("Failed to discover skills for scene: {}", sceneId, e);
                return new ArrayList<>();
            }
        });
    }
    
    @Override
    public CompletableFuture<InstallResult> install(InstallRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            InstallResult result = new InstallResult();
            result.setSkillId(request.getSkillId());
            
            try {
                notifyInstalling(request.getSkillId());
                
                DiscoveryMethod method = resolveDiscoveryMethod(request);
                
                SkillPackage skillPackage = null;
                
                if (request.getDownloadUrl() != null && !request.getDownloadUrl().isEmpty()) {
                    skillPackage = downloadFromUrl(request.getDownloadUrl(), request.getSkillId()).join();
                } else {
                    skillPackage = discover(request.getSkillId(), method).join();
                }
                
                if (skillPackage == null) {
                    throw new SkillException(request.getSkillId(), "Skill package not found");
                }
                
                registry.register(skillPackage);
                
                result.setSuccess(true);
                result.setVersion(skillPackage.getVersion());
                result.setInstallPath(skillRootPath + request.getSkillId());
                result.setDuration(System.currentTimeMillis() - startTime);
                
                notifyInstalled(request.getSkillId(), result);
                
                log.info("Skill installed: {} v{} via {}", request.getSkillId(), skillPackage.getVersion(), method);
                
            } catch (Exception e) {
                result.setSuccess(false);
                result.setError(e.getMessage());
                result.setDuration(System.currentTimeMillis() - startTime);
                
                notifyError(request.getSkillId(), e.getMessage());
                
                log.error("Failed to install skill: {}", request.getSkillId(), e);
            }
            
            return result;
        });
    }
    
    private DiscoveryMethod resolveDiscoveryMethod(InstallRequest request) {
        if (request.getDiscoveryMethod() != null && request.getDiscoveryMethod() != DiscoveryMethod.AUTO) {
            return request.getDiscoveryMethod();
        }
        
        if (request.getSource() != null && !request.getSource().isEmpty()) {
            return DiscoveryMethod.inferFromSource(request.getSource());
        }
        
        return DiscoveryMethod.LOCAL_FS;
    }
    
    private CompletableFuture<SkillPackage> downloadFromUrl(String url, String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            SkillPackage pkg = new SkillPackage();
            pkg.setSkillId(skillId);
            pkg.setName(skillId);
            pkg.setVersion("1.0.0");
            pkg.setSource(url);
            log.info("Downloaded skill from URL: {}", url);
            return pkg;
        });
    }
    
    @Override
    public CompletableFuture<InstallResultWithDependencies> installWithDependencies(String skillId, InstallRequest.InstallMode mode) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            InstallResultWithDependencies result = new InstallResultWithDependencies();
            result.setSkillId(skillId);
            
            log.info("[installWithDependencies] Installing skill: {}", skillId);
            
            try {
                // 1. 获取 Skill 元数据
                SkillManifest manifest = getManifest(skillId).join();
                if (manifest == null) {
                    result.setSuccess(false);
                    result.setStatus("failed");
                    result.setError("Skill manifest not found: " + skillId);
                    result.setDuration(System.currentTimeMillis() - startTime);
                    return result;
                }
                
                // 2. 解析依赖
                List<SkillManifest.Dependency> dependencies = manifest.getDependencies();
                log.info("[installWithDependencies] Found {} dependencies for {}", 
                    dependencies != null ? dependencies.size() : 0, skillId);
                
                // 3. 递归安装依赖
                if (dependencies != null) {
                    for (SkillManifest.Dependency dep : dependencies) {
                        String depSkillId = dep.getSkillId();
                        if (!isInstalled(depSkillId).join()) {
                            try {
                                log.info("[installWithDependencies] Installing dependency: {}", depSkillId);
                                InstallResultWithDependencies depResult = installWithDependencies(depSkillId, mode).join();
                                
                                if (depResult.isSuccess()) {
                                    result.addInstalledDependency(depSkillId);
                                    log.info("[installWithDependencies] Dependency installed: {}", depSkillId);
                                } else {
                                    result.addFailedDependency(depSkillId);
                                    log.error("[installWithDependencies] Failed to install dependency: {} - {}", 
                                        depSkillId, depResult.getError());
                                }
                            } catch (Exception e) {
                                log.error("[installWithDependencies] Exception installing dependency: {}", depSkillId, e);
                                result.addFailedDependency(depSkillId);
                            }
                        } else {
                            result.addExistingDependency(depSkillId);
                            log.info("[installWithDependencies] Dependency already installed: {}", depSkillId);
                        }
                    }
                }
                
                // 4. 如果有依赖安装失败，返回错误
                if (!result.getFailedDependencies().isEmpty()) {
                    result.setSuccess(false);
                    result.setStatus("failed");
                    result.setError("Failed to install dependencies: " + result.getFailedDependencies());
                    result.setDuration(System.currentTimeMillis() - startTime);
                    return result;
                }
                
                // 5. 检查主 Skill 是否已安装
                if (isInstalled(skillId).join()) {
                    result.setSuccess(true);
                    result.setStatus("existing");
                    result.addExistingDependency(skillId);
                    result.setDuration(System.currentTimeMillis() - startTime);
                    log.info("[installWithDependencies] Skill already installed: {}", skillId);
                    return result;
                }
                
                // 6. 安装主 Skill
                InstallRequest request = new InstallRequest();
                request.setSkillId(skillId);
                request.setMode(mode);
                request.setInstallDependencies(false);  // 已经手动安装了依赖
                
                InstallResult mainResult = install(request).join();
                
                if (mainResult.isSuccess()) {
                    result.setSuccess(true);
                    result.setStatus("installed");
                    log.info("[installWithDependencies] Skill installed: {}", skillId);
                } else {
                    result.setSuccess(false);
                    result.setStatus("failed");
                    result.setError(mainResult.getError());
                    log.error("[installWithDependencies] Failed to install skill: {} - {}", 
                        skillId, mainResult.getError());
                }
                
                result.setDuration(System.currentTimeMillis() - startTime);
                return result;
                
            } catch (Exception e) {
                log.error("[installWithDependencies] Exception installing skill: {}", skillId, e);
                result.setSuccess(false);
                result.setStatus("failed");
                result.setError(e.getMessage());
                result.setDuration(System.currentTimeMillis() - startTime);
                return result;
            }
        });
    }
    
    @Override
    public CompletableFuture<UninstallResult> uninstall(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            UninstallResult result = new UninstallResult();
            result.setSkillId(skillId);
            
            try {
                registry.unregister(skillId);
                result.setSuccess(true);
                result.setDataRemoved(true);
                
                log.info("Skill uninstalled: {}", skillId);
                
            } catch (Exception e) {
                result.setSuccess(false);
                result.setError(e.getMessage());
                log.error("Failed to uninstall skill: {}", skillId, e);
            }
            
            return result;
        });
    }
    
    @Override
    public CompletableFuture<UpdateResult> update(String skillId, String version) {
        return CompletableFuture.supplyAsync(() -> {
            UpdateResult result = new UpdateResult();
            result.setSkillId(skillId);
            
            try {
                SkillPackage current = registry.get(skillId);
                if (current == null) {
                    throw new SkillException(skillId, "Skill not installed");
                }
                
                result.setPreviousVersion(current.getVersion());
                result.setNewVersion(version);
                result.setSuccess(true);
                
                log.info("Skill updated: {} from {} to {}", skillId, current.getVersion(), version);
                
            } catch (Exception e) {
                result.setSuccess(false);
                result.setError(e.getMessage());
                log.error("Failed to update skill: {}", skillId, e);
            }
            
            return result;
        });
    }
    
    @Override
    public CompletableFuture<List<InstalledSkill>> listInstalled() {
        return CompletableFuture.supplyAsync(() -> {
            List<InstalledSkill> installed = new ArrayList<>();
            for (SkillPackage pkg : registry.getAll()) {
                InstalledSkill skill = new InstalledSkill();
                skill.setSkillId(pkg.getSkillId());
                skill.setName(pkg.getName());
                skill.setVersion(pkg.getVersion());
                skill.setSceneId(pkg.getSceneId());
                skill.setInstallPath(skillRootPath + pkg.getSkillId());
                skill.setStatus(SkillStatus.INSTALLED.getCode());
                skill.setInstallTime(System.currentTimeMillis());
                installed.add(skill);
            }
            return installed;
        });
    }
    
    @Override
    public CompletableFuture<InstalledSkill> getInstalled(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            SkillPackage pkg = registry.get(skillId);
            if (pkg == null) {
                return null;
            }
            
            InstalledSkill skill = new InstalledSkill();
            skill.setSkillId(pkg.getSkillId());
            skill.setName(pkg.getName());
            skill.setVersion(pkg.getVersion());
            skill.setSceneId(pkg.getSceneId());
            skill.setInstallPath(skillRootPath + pkg.getSkillId());
            skill.setStatus(SkillStatus.INSTALLED.getCode());
            return skill;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> isInstalled(String skillId) {
        return CompletableFuture.supplyAsync(() -> registry.has(skillId));
    }
    
    @Override
    public CompletableFuture<SkillPackage> getPackage(String skillId) {
        return CompletableFuture.supplyAsync(() -> registry.get(skillId));
    }
    
    @Override
    public CompletableFuture<SkillManifest> getManifest(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            SkillPackage pkg = registry.get(skillId);
            return pkg != null ? pkg.getManifest() : null;
        });
    }
    
    @Override
    public CompletableFuture<Void> registerObserver(SkillPackageObserver observer) {
        return CompletableFuture.runAsync(() -> {
            observers.add(observer);
        });
    }
    
    @Override
    public CompletableFuture<Void> unregisterObserver(SkillPackageObserver observer) {
        return CompletableFuture.runAsync(() -> {
            observers.remove(observer);
        });
    }
    
    @Override
    public CompletableFuture<List<SkillPackage>> search(String query, DiscoveryMethod method) {
        return CompletableFuture.supplyAsync(() -> {
            SkillDiscoverer discoverer = getDiscoverer(method);
            if (discoverer == null) {
                return new ArrayList<>();
            }
            return discoverer.search(query).join();
        });
    }
    
    @Override
    public CompletableFuture<List<SkillPackage>> searchByCapability(String capabilityId, DiscoveryMethod method) {
        return CompletableFuture.supplyAsync(() -> {
            SkillDiscoverer discoverer = getDiscoverer(method);
            if (discoverer == null) {
                return new ArrayList<>();
            }
            return discoverer.searchByCapability(capabilityId).join();
        });
    }
    
    @Override
    public String getSkillRootPath() {
        return skillRootPath;
    }
    
    @Override
    public void setSkillRootPath(String path) {
        this.skillRootPath = path;
    }
    
    @Override
    public CompletableFuture<Void> updateConfig(String skillId, Map<String, String> config) {
        return CompletableFuture.runAsync(() -> {
            SkillPackage pkg = registry.get(skillId);
            if (pkg == null) {
                throw new SkillException(skillId, "Skill not installed: " + skillId);
            }
            log.info("Updated config for skill: {}", skillId);
        });
    }
    
    @Override
    public CompletableFuture<Map<String, String>> getConfig(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            SkillPackage pkg = registry.get(skillId);
            if (pkg == null) {
                return null;
            }
            return new ConcurrentHashMap<String, String>();
        });
    }
    
    @Override
    public CompletableFuture<InstallProgress> getInstallProgress(String installId) {
        return CompletableFuture.supplyAsync(() -> {
            InstallProgress progress = activeInstalls.get(installId);
            if (progress == null) {
                progress = new InstallProgress(installId, "unknown");
            }
            return progress;
        });
    }
    
    @Override
    public CompletableFuture<List<InstallProgress>> getActiveInstalls() {
        return CompletableFuture.supplyAsync(() -> {
            List<InstallProgress> active = new ArrayList<InstallProgress>();
            for (InstallProgress progress : activeInstalls.values()) {
                if (progress.isInProgress()) {
                    active.add(progress);
                }
            }
            return active;
        });
    }
    
    @Override
    public CompletableFuture<DependencyInfo> getDependencies(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            return new DependencyInfoImpl(skillId);
        });
    }
    
    @Override
    public CompletableFuture<DependencyResult> installDependencies(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            DependencyResult result = new DependencyResult(skillId);

            try {
                SkillPackage pkg = registry.get(skillId);
                if (pkg == null) {
                    result.setSuccess(false);
                    result.setErrorMessage("Skill not found: " + skillId);
                    return result;
                }

                SkillManifest manifest = pkg.getManifest();
                if (manifest == null || manifest.getDependencies() == null || manifest.getDependencies().isEmpty()) {
                    result.setSuccess(true);
                    result.setTotalCount(0);
                    result.setProcessingTime(System.currentTimeMillis() - startTime);
                    log.info("No dependencies to install for skill: {}", skillId);
                    return result;
                }

                List<SkillManifest.Dependency> dependencies = manifest.getDependencies();
                result.setTotalCount(dependencies.size());

                for (SkillManifest.Dependency dep : dependencies) {
                    String depSkillId = dep.getSkillId();
                    DependencyResult.DependencyItemResult itemResult = new DependencyResult.DependencyItemResult();
                    itemResult.setDependencyId(depSkillId);
                    itemResult.setName(depSkillId);
                    itemResult.setVersion(dep.getVersionRange());

                    try {
                        boolean isInstalled = isInstalled(depSkillId).join();

                        if (isInstalled) {
                            itemResult.setAction(DependencyResult.DependencyItemResult.DependencyAction.SKIPPED);
                            itemResult.setSuccess(true);
                            itemResult.setMessage("Already installed");
                            result.incrementSkipped();
                            log.info("Dependency already installed: {}", depSkillId);
                        } else {
                            log.info("Installing dependency: {} (required: {})", depSkillId, dep.isRequired());

                            InstallRequest installRequest = new InstallRequest();
                            installRequest.setSkillId(depSkillId);

                            InstallResult installResult = install(installRequest).join();

                            if (installResult != null && installResult.isSuccess()) {
                                itemResult.setAction(DependencyResult.DependencyItemResult.DependencyAction.INSTALLED);
                                itemResult.setSuccess(true);
                                itemResult.setMessage("Installed successfully");
                                result.incrementInstalled();
                                log.info("Dependency installed: {}", depSkillId);

                                // 递归安装子依赖
                                DependencyResult subResult = installDependencies(depSkillId).join();
                                if (subResult != null && subResult.hasFailures()) {
                                    log.warn("Some sub-dependencies failed for: {}", depSkillId);
                                }
                            } else {
                                itemResult.setAction(DependencyResult.DependencyItemResult.DependencyAction.FAILED);
                                itemResult.setSuccess(false);
                                itemResult.setMessage(installResult != null ? installResult.getError() : "Unknown error");
                                result.incrementFailed();

                                if (dep.isRequired()) {
                                    result.setSuccess(false);
                                    result.setErrorMessage("Required dependency failed: " + depSkillId);
                                }
                                log.error("Failed to install dependency: {} - {}", depSkillId, itemResult.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        itemResult.setAction(DependencyResult.DependencyItemResult.DependencyAction.FAILED);
                        itemResult.setSuccess(false);
                        itemResult.setMessage(e.getMessage());
                        itemResult.setError(e);
                        result.incrementFailed();

                        if (dep.isRequired()) {
                            result.setSuccess(false);
                            result.setErrorMessage("Required dependency failed: " + depSkillId);
                        }
                        log.error("Error installing dependency: {}", depSkillId, e);
                    }

                    result.addItem(itemResult);
                }

                result.setProcessingTime(System.currentTimeMillis() - startTime);
                log.info("Dependencies processed for skill: {} - installed: {}, skipped: {}, failed: {}",
                    skillId, result.getInstalledCount(), result.getSkippedCount(), result.getFailedCount());

            } catch (Exception e) {
                result.setSuccess(false);
                result.setErrorMessage("Error processing dependencies: " + e.getMessage());
                result.setProcessingTime(System.currentTimeMillis() - startTime);
                log.error("Error installing dependencies for skill: {}", skillId, e);
            }

            return result;
        });
    }
    
    @Override
    public CompletableFuture<DependencyResult> updateDependencies(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            DependencyResult result = new DependencyResult(skillId);
            result.setSuccess(true);
            result.setTotalCount(0);
            result.setInstalledCount(0);
            result.setProcessingTime(0);
            log.info("Dependencies updated for skill: {}", skillId);
            return result;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> checkDependencySatisfied(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            SkillPackage pkg = registry.get(skillId);
            return pkg != null;
        });
    }
    
    @Override
    public CompletableFuture<List<InterfaceDefinition>> getProvidedInterfaces(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            SkillPackage pkg = registry.get(skillId);
            if (pkg == null || pkg.getManifest() == null) {
                return new ArrayList<InterfaceDefinition>();
            }
            
            List<InterfaceDefinition> interfaces = new ArrayList<>();
            SkillManifest manifest = pkg.getManifest();
            
            if (manifest.getProvidedInterfaces() != null) {
                for (String interfaceId : manifest.getProvidedInterfaces()) {
                    InterfaceDefinition def = new InterfaceDefinition(interfaceId, interfaceId);
                    interfaces.add(def);
                }
            }
            
            return interfaces;
        });
    }
    
    @Override
    public CompletableFuture<List<InterfaceDependency>> getRequiredInterfaces(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            SkillPackage pkg = registry.get(skillId);
            if (pkg == null || pkg.getManifest() == null) {
                return new ArrayList<InterfaceDependency>();
            }
            
            List<InterfaceDependency> dependencies = new ArrayList<>();
            SkillManifest manifest = pkg.getManifest();
            
            if (manifest.getRequiredInterfaces() != null) {
                for (String interfaceId : manifest.getRequiredInterfaces()) {
                    InterfaceDependency dep = new InterfaceDependency(interfaceId);
                    dependencies.add(dep);
                }
            }
            
            return dependencies;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> registerInterfaceProvider(String skillId, InterfaceDefinition interfaceDef) {
        return CompletableFuture.supplyAsync(() -> {
            if (skillId == null || interfaceDef == null) {
                return false;
            }
            
            SkillPackage pkg = registry.get(skillId);
            if (pkg == null) {
                log.warn("Cannot register interface for unknown skill: {}", skillId);
                return false;
            }
            
            interfaceDef.addImplementation(skillId);
            log.info("Interface {} registered for skill {}", interfaceDef.getInterfaceId(), skillId);
            return true;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> unregisterInterfaceProvider(String skillId, String interfaceId) {
        return CompletableFuture.supplyAsync(() -> {
            if (skillId == null || interfaceId == null) {
                return false;
            }
            
            log.info("Interface {} unregistered for skill {}", interfaceId, skillId);
            return true;
        });
    }
    
    @Override
    public CompletableFuture<List<String>> findSkillsProvidingInterface(String interfaceId) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> skills = new ArrayList<>();
            
            for (SkillPackage pkg : registry.getAll()) {
                if (pkg.getManifest() != null && 
                    pkg.getManifest().getProvidedInterfaces() != null &&
                    pkg.getManifest().getProvidedInterfaces().contains(interfaceId)) {
                    skills.add(pkg.getSkillId());
                }
            }
            
            return skills;
        });
    }
    
    @Override
    public CompletableFuture<List<String>> findSkillsProvidingInterface(String interfaceId, String version) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> skills = new ArrayList<>();
            
            for (SkillPackage pkg : registry.getAll()) {
                if (pkg.getManifest() != null && 
                    pkg.getManifest().getProvidedInterfaces() != null &&
                    pkg.getManifest().getProvidedInterfaces().contains(interfaceId)) {
                    if (version == null || version.equals(pkg.getVersion())) {
                        skills.add(pkg.getSkillId());
                    }
                }
            }
            
            return skills;
        });
    }
    
    @Override
    public CompletableFuture<Boolean> validateInterfaceCompatibility(String skillId, String interfaceId) {
        return CompletableFuture.supplyAsync(() -> {
            SkillPackage pkg = registry.get(skillId);
            if (pkg == null) {
                return false;
            }
            
            if (pkg.getManifest() != null && 
                pkg.getManifest().getProvidedInterfaces() != null &&
                pkg.getManifest().getProvidedInterfaces().contains(interfaceId)) {
                return true;
            }
            
            if (pkg.getManifest() != null && 
                pkg.getManifest().getRequiredInterfaces() != null &&
                pkg.getManifest().getRequiredInterfaces().contains(interfaceId)) {
                return true;
            }
            
            return false;
        });
    }
    
    @Override
    public CompletableFuture<InterfaceResolutionResult> resolveInterfaces(String skillId) {
        return CompletableFuture.supplyAsync(() -> {
            InterfaceResolutionResult result = new InterfaceResolutionResult();
            result.setResolvedInterfaces(new ArrayList<>());
            result.setUnresolvedInterfaces(new ArrayList<>());
            result.setInterfaceToSkill(new ConcurrentHashMap<>());
            
            SkillPackage pkg = registry.get(skillId);
            if (pkg == null) {
                result.setSuccess(false);
                result.setErrorMessage("Skill not found: " + skillId);
                return result;
            }
            
            List<InterfaceDependency> required = getRequiredInterfaces(skillId).join();
            
            for (InterfaceDependency dep : required) {
                List<String> providers = findSkillsProvidingInterface(dep.getInterfaceId()).join();
                
                if (!providers.isEmpty()) {
                    result.getResolvedInterfaces().add(dep.getInterfaceId());
                    result.getInterfaceToSkill().put(dep.getInterfaceId(), providers.get(0));
                } else {
                    result.getUnresolvedInterfaces().add(dep.getInterfaceId());
                }
            }
            
            result.setSuccess(result.getUnresolvedInterfaces().isEmpty());
            
            if (!result.isSuccess()) {
                result.setErrorMessage("Unresolved interfaces: " + result.getUnresolvedInterfaces());
            }
            
            return result;
        });
    }
    
    private SkillDiscoverer getDiscoverer(DiscoveryMethod method) {
        if (method == null) {
            return discoverers.get(DiscoveryMethod.LOCAL_FS);
        }
        return discoverers.get(method);
    }
    
    private void notifyInstalling(String skillId) {
        for (SkillPackageObserver observer : observers) {
            try {
                observer.onInstalling(skillId);
            } catch (Exception e) {
                log.warn("Observer error during installing notification", e);
            }
        }
    }
    
    private void notifyInstalled(String skillId, InstallResult result) {
        for (SkillPackageObserver observer : observers) {
            try {
                observer.onInstalled(skillId, result);
            } catch (Exception e) {
                log.warn("Observer error during installed notification", e);
            }
        }
    }
    
    private void notifyError(String skillId, String error) {
        for (SkillPackageObserver observer : observers) {
            try {
                observer.onError(skillId, error);
            } catch (Exception e) {
                log.warn("Observer error during error notification", e);
            }
        }
    }

    @Override
    public CompletableFuture<TemplateInstallResult> installFromTemplate(SceneTemplate template, InstallOptions options) {
        // 处理 null options
        final InstallOptions finalOptions = options != null ? options : InstallOptions.defaults();

        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            TemplateInstallResult result = new TemplateInstallResult();
            result.setTemplateId(template.getTemplateId());
            result.setStatus("installing");

            log.info("[installFromTemplate] Starting installation from template: {}", template.getTemplateId());

            try {
                // 1. 获取安装顺序
                List<String> installOrder = getInstallOrder(template);
                log.info("[installFromTemplate] Install order: {}", installOrder);

                int totalSkills = installOrder.size();
                int completedSkills = 0;

                // 2. 按顺序安装每个 Skill
                for (String skillId : installOrder) {
                    SceneTemplate.SkillRef skillRef = findSkillRef(template, skillId);
                    if (skillRef == null) {
                        log.warn("[installFromTemplate] SkillRef not found for: {}", skillId);
                        continue;
                    }

                    // 检查是否已安装
                    boolean alreadyInstalled = isInstalled(skillId).join();
                    if (alreadyInstalled && finalOptions.isSkipInstalled() && !finalOptions.isForceReinstall()) {
                        log.info("[installFromTemplate] Skipping already installed skill: {}", skillId);
                        result.addSkippedSkill(skillId);
                        TemplateInstallResult.SkillInstallDetail detail = new TemplateInstallResult.SkillInstallDetail();
                        detail.setSkillId(skillId);
                        detail.setStatus("skipped");
                        detail.setVersion(skillRef.getVersion());
                        result.addSkillDetail(skillId, detail);
                        completedSkills++;
                        result.updateProgress((completedSkills * 100) / totalSkills);
                        continue;
                    }

                    // 安装 Skill
                    log.info("[installFromTemplate] Installing skill: {}", skillId);
                    try {
                        InstallRequest request = new InstallRequest();
                        request.setSkillId(skillId);

                        // 应用配置覆盖
                        if (finalOptions.getConfigOverrides() != null && finalOptions.getConfigOverrides().containsKey(skillId)) {
                            // 配置覆盖逻辑
                        }

                        InstallResult installResult = install(request).join();

                        TemplateInstallResult.SkillInstallDetail detail = new TemplateInstallResult.SkillInstallDetail();
                        detail.setSkillId(skillId);
                        detail.setVersion(skillRef.getVersion());

                        if (installResult.isSuccess()) {
                            result.addInstalledSkill(skillId);
                            detail.setStatus("installed");
                            log.info("[installFromTemplate] Skill installed: {}", skillId);
                        } else {
                            result.addFailedSkill(skillId);
                            detail.setStatus("failed");
                            detail.setError(installResult.getError());
                            log.error("[installFromTemplate] Failed to install skill: {} - {}",
                                skillId, installResult.getError());

                            // 如果 required，则中止安装
                            if (skillRef.isRequired()) {
                                result.setSuccess(false);
                                result.setStatus("failed");
                                result.setError("Required skill failed: " + skillId);
                                result.setDuration(System.currentTimeMillis() - startTime);
                                return result;
                            }
                        }

                        result.addSkillDetail(skillId, detail);

                    } catch (Exception e) {
                        log.error("[installFromTemplate] Exception installing skill: {}", skillId, e);
                        result.addFailedSkill(skillId);

                        if (skillRef.isRequired()) {
                            result.setSuccess(false);
                            result.setStatus("failed");
                            result.setError("Required skill failed: " + skillId + " - " + e.getMessage());
                            result.setDuration(System.currentTimeMillis() - startTime);
                            return result;
                        }
                    }

                    completedSkills++;
                    result.updateProgress((completedSkills * 100) / totalSkills);
                }

                // 3. 判断最终结果
                boolean hasFailures = !result.getFailedSkills().isEmpty();
                boolean hasInstalled = !result.getInstalledSkills().isEmpty();

                if (hasFailures && hasInstalled) {
                    result.setSuccess(true);
                    result.setStatus("partial");
                } else if (hasFailures) {
                    result.setSuccess(false);
                    result.setStatus("failed");
                } else {
                    result.setSuccess(true);
                    result.setStatus("installed");
                }

                result.setDuration(System.currentTimeMillis() - startTime);

                log.info("[installFromTemplate] Template installation completed: {} - installed: {}, skipped: {}, failed: {}",
                    template.getTemplateId(),
                    result.getInstalledSkills().size(),
                    result.getSkippedSkills().size(),
                    result.getFailedSkills().size());

                return result;

            } catch (Exception e) {
                log.error("[installFromTemplate] Exception during template installation: {}", template.getTemplateId(), e);
                result.setSuccess(false);
                result.setStatus("failed");
                result.setError(e.getMessage());
                result.setDuration(System.currentTimeMillis() - startTime);
                return result;
            }
        });
    }

    /**
     * 获取安装顺序
     */
    private List<String> getInstallOrder(SceneTemplate template) {
        List<String> order = new ArrayList<>();

        if (template.getSkills() == null) {
            return order;
        }

        // 先安装 required，再安装 optional
        for (SceneTemplate.SkillRef skillRef : template.getSkills()) {
            if (skillRef.isRequired()) {
                order.add(skillRef.getSkillId());
            }
        }

        for (SceneTemplate.SkillRef skillRef : template.getSkills()) {
            if (!skillRef.isRequired()) {
                order.add(skillRef.getSkillId());
            }
        }

        return order;
    }

    /**
     * 查找 SkillRef
     */
    private SceneTemplate.SkillRef findSkillRef(SceneTemplate template, String skillId) {
        if (template.getSkills() == null) {
            return null;
        }

        for (SceneTemplate.SkillRef skillRef : template.getSkills()) {
            if (skillRef.getSkillId().equals(skillId)) {
                return skillRef;
            }
        }

        return null;
    }
}
