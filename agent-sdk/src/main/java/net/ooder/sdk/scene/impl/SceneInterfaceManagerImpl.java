package net.ooder.sdk.scene.impl;

import net.ooder.sdk.api.skill.InterfaceDefinition;
import net.ooder.sdk.driver.DriverLoader;
import net.ooder.sdk.interfaceRegistry.InterfaceRegistry;
import net.ooder.sdk.resolver.InterfaceResolver;
import net.ooder.sdk.scene.SceneInterfaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class SceneInterfaceManagerImpl implements SceneInterfaceManager {
    
    private static final Logger log = LoggerFactory.getLogger(SceneInterfaceManagerImpl.class);
    
    private final InterfaceRegistry interfaceRegistry;
    private final DriverLoader driverLoader;
    private final InterfaceResolver interfaceResolver;
    
    private final Map<String, Map<String, InterfaceBinding>> sceneBindings = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> preferredImplementations = new ConcurrentHashMap<>();
    private final Map<String, InterfaceDefinition> interfaceDefinitions = new ConcurrentHashMap<>();
    
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    public SceneInterfaceManagerImpl(InterfaceRegistry interfaceRegistry, 
                                      DriverLoader driverLoader,
                                      InterfaceResolver interfaceResolver) {
        this.interfaceRegistry = interfaceRegistry;
        this.driverLoader = driverLoader;
        this.interfaceResolver = interfaceResolver;
    }
    
    @Override
    public void initialize(String sceneId) {
        sceneBindings.computeIfAbsent(sceneId, k -> new ConcurrentHashMap<>());
        preferredImplementations.computeIfAbsent(sceneId, k -> new ConcurrentHashMap<>());
        log.info("Scene interface manager initialized for scene: {}", sceneId);
    }
    
    @Override
    public void shutdown() {
        for (String sceneId : sceneBindings.keySet()) {
            clearScene(sceneId);
        }
        sceneBindings.clear();
        preferredImplementations.clear();
        executor.shutdown();
        log.info("Scene interface manager shutdown");
    }
    
    @Override
    public CompletableFuture<Void> bindInterface(String sceneId, String interfaceId, String skillId) {
        return CompletableFuture.runAsync(() -> {
            Map<String, InterfaceBinding> bindings = sceneBindings.computeIfAbsent(sceneId, k -> new ConcurrentHashMap<>());
            
            Optional<?> driverOpt = driverLoader.load(interfaceId, skillId, Object.class);
            if (!driverOpt.isPresent()) {
                throw new RuntimeException("Failed to load driver for interface: " + interfaceId + " -> " + skillId);
            }
            
            InterfaceBinding binding = new InterfaceBinding();
            binding.setSceneId(sceneId);
            binding.setInterfaceId(interfaceId);
            binding.setSkillId(skillId);
            binding.setDriver(driverOpt.get());
            binding.setBindTime(System.currentTimeMillis());
            binding.setLastAccessTime(System.currentTimeMillis());
            binding.setAccessCount(0);
            binding.setActive(true);
            
            bindings.put(interfaceId, binding);
            
            if (interfaceRegistry != null) {
                interfaceRegistry.registerImplementation(interfaceId, skillId);
            }
            
            log.info("Interface bound: {} -> {} for scene {}", interfaceId, skillId, sceneId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> unbindInterface(String sceneId, String interfaceId) {
        return CompletableFuture.runAsync(() -> {
            Map<String, InterfaceBinding> bindings = sceneBindings.get(sceneId);
            if (bindings != null) {
                InterfaceBinding removed = bindings.remove(interfaceId);
                if (removed != null) {
                    log.info("Interface unbound: {} from scene {}", interfaceId, sceneId);
                }
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> bindInterfaces(String sceneId, Map<String, String> interfaceBindings) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : interfaceBindings.entrySet()) {
            futures.add(bindInterface(sceneId, entry.getKey(), entry.getValue()));
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    @Override
    public Optional<InterfaceBinding> getBinding(String sceneId, String interfaceId) {
        Map<String, InterfaceBinding> bindings = sceneBindings.get(sceneId);
        if (bindings == null) {
            return Optional.empty();
        }
        
        InterfaceBinding binding = bindings.get(interfaceId);
        if (binding != null) {
            binding.recordAccess();
        }
        
        return Optional.ofNullable(binding);
    }
    
    @Override
    public List<InterfaceBinding> getBindings(String sceneId) {
        Map<String, InterfaceBinding> bindings = sceneBindings.get(sceneId);
        return bindings != null ? new ArrayList<>(bindings.values()) : Collections.emptyList();
    }
    
    @Override
    public <T> Optional<T> getInterface(String sceneId, String interfaceId, Class<T> type) {
        Optional<InterfaceBinding> bindingOpt = getBinding(sceneId, interfaceId);
        if (bindingOpt.isPresent()) {
            InterfaceBinding binding = bindingOpt.get();
            try {
                return Optional.of(type.cast(binding.getDriver()));
            } catch (ClassCastException e) {
                log.warn("Failed to cast interface {} to type {}", interfaceId, type.getName());
                return Optional.empty();
            }
        }
        
        String preferredSkillId = getPreferredImplementation(sceneId, interfaceId);
        if (preferredSkillId != null) {
            return getInterface(sceneId, interfaceId, preferredSkillId, type);
        }
        
        return driverLoader.load(interfaceId, type);
    }
    
    @Override
    public <T> Optional<T> getInterface(String sceneId, String interfaceId, String skillId, Class<T> type) {
        return driverLoader.load(interfaceId, skillId, type);
    }
    
    @Override
    public CompletableFuture<InterfaceResolver.ResolvedInterface> resolveInterface(String sceneId, String interfaceId) {
        return CompletableFuture.supplyAsync(() -> {
            String preferred = getPreferredImplementation(sceneId, interfaceId);
            if (preferred != null) {
                return interfaceResolver.resolve(interfaceId, preferred);
            }
            return interfaceResolver.resolve(interfaceId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<InterfaceResolver.ResolvedInterface>> resolveAllInterfaces(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            List<InterfaceResolver.ResolvedInterface> results = new ArrayList<>();
            Map<String, InterfaceBinding> bindings = sceneBindings.get(sceneId);
            
            if (bindings != null) {
                for (String interfaceId : bindings.keySet()) {
                    InterfaceResolver.ResolvedInterface resolved = resolveInterface(sceneId, interfaceId).join();
                    results.add(resolved);
                }
            }
            
            return results;
        }, executor);
    }
    
    @Override
    public boolean hasInterface(String sceneId, String interfaceId) {
        Map<String, InterfaceBinding> bindings = sceneBindings.get(sceneId);
        return bindings != null && bindings.containsKey(interfaceId);
    }
    
    @Override
    public List<String> getAvailableInterfaces(String sceneId) {
        Map<String, InterfaceBinding> bindings = sceneBindings.get(sceneId);
        return bindings != null ? new ArrayList<>(bindings.keySet()) : Collections.emptyList();
    }
    
    @Override
    public void setPreferredImplementation(String sceneId, String interfaceId, String skillId) {
        Map<String, String> prefs = preferredImplementations.computeIfAbsent(sceneId, k -> new ConcurrentHashMap<>());
        prefs.put(interfaceId, skillId);
        log.info("Preferred implementation set: {} -> {} for scene {}", interfaceId, skillId, sceneId);
    }
    
    @Override
    public String getPreferredImplementation(String sceneId, String interfaceId) {
        Map<String, String> prefs = preferredImplementations.get(sceneId);
        return prefs != null ? prefs.get(interfaceId) : null;
    }
    
    @Override
    public void registerInterfaceDefinition(InterfaceDefinition definition) {
        if (definition == null || definition.getInterfaceId() == null) {
            throw new IllegalArgumentException("Interface definition or ID cannot be null");
        }
        
        interfaceDefinitions.put(definition.getInterfaceId(), definition);
        
        if (interfaceRegistry != null) {
            interfaceRegistry.register(definition);
        }
        
        log.info("Interface definition registered: {}", definition.getInterfaceId());
    }
    
    @Override
    public void unregisterInterfaceDefinition(String interfaceId) {
        interfaceDefinitions.remove(interfaceId);
        
        if (interfaceRegistry != null) {
            interfaceRegistry.unregister(interfaceId);
        }
        
        log.info("Interface definition unregistered: {}", interfaceId);
    }
    
    @Override
    public Optional<InterfaceDefinition> getInterfaceDefinition(String interfaceId) {
        return Optional.ofNullable(interfaceDefinitions.get(interfaceId));
    }
    
    @Override
    public List<InterfaceDefinition> getAllInterfaceDefinitions() {
        return new ArrayList<>(interfaceDefinitions.values());
    }
    
    @Override
    public InterfacePoolStats getPoolStats(String sceneId) {
        InterfacePoolStats stats = new InterfacePoolStats();
        stats.setSceneId(sceneId);
        
        Map<String, InterfaceBinding> bindings = sceneBindings.get(sceneId);
        if (bindings == null) {
            stats.setTotalBindings(0);
            stats.setActiveBindings(0);
            stats.setInactiveBindings(0);
            stats.setTotalAccessCount(0);
            return stats;
        }
        
        int total = bindings.size();
        int active = 0;
        int inactive = 0;
        long totalAccess = 0;
        long lastAccess = 0;
        
        for (InterfaceBinding binding : bindings.values()) {
            if (binding.isActive()) {
                active++;
            } else {
                inactive++;
            }
            totalAccess += binding.getAccessCount();
            if (binding.getLastAccessTime() > lastAccess) {
                lastAccess = binding.getLastAccessTime();
            }
        }
        
        stats.setTotalBindings(total);
        stats.setActiveBindings(active);
        stats.setInactiveBindings(inactive);
        stats.setTotalAccessCount(totalAccess);
        stats.setLastAccessTime(lastAccess);
        
        return stats;
    }
    
    @Override
    public void clearScene(String sceneId) {
        Map<String, InterfaceBinding> bindings = sceneBindings.remove(sceneId);
        preferredImplementations.remove(sceneId);
        
        if (bindings != null) {
            log.info("Scene cleared: {} ({} bindings removed)", sceneId, bindings.size());
        }
    }
}
