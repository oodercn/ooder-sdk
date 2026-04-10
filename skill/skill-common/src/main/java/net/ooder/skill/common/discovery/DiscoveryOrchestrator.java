package net.ooder.skill.common.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Service
public class DiscoveryOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryOrchestrator.class);

    @Value("${ooder.discovery.use-index-first:true}")
    private boolean useIndexFirst;
    
    @Value("${ooder.mock.enabled:false}")
    private boolean mockEnabled;

    @Autowired
    private SkillIndexLoader skillIndexLoader;
    
    @Autowired(required = false)
    private LocalSkillDiscoverer localSkillDiscoverer;
    
    private final Map<DiscoveryMethod, SkillDiscoverer> discoverers = new HashMap<>();
    
    private Function<String, Boolean> installedChecker;
    
    private List<DiscoveryListener> listeners = new ArrayList<>();

    @PostConstruct
    public void init() {
        log.info("[DiscoveryOrchestrator] Initialized with useIndexFirst: {}", useIndexFirst);
        
        if (localSkillDiscoverer != null && localSkillDiscoverer.isAvailable()) {
            registerDiscoverer(DiscoveryMethod.LOCAL, localSkillDiscoverer);
            log.info("[init] LocalSkillDiscoverer registered successfully");
        }
    }
    
    public void registerDiscoverer(DiscoveryMethod method, SkillDiscoverer discoverer) {
        if (discoverer != null && discoverer.isAvailable()) {
            discoverers.put(method, discoverer);
            log.info("[registerDiscoverer] Registered discoverer for method: {}", method);
        }
    }
    
    public void setInstalledChecker(Function<String, Boolean> checker) {
        this.installedChecker = checker;
        skillIndexLoader.setInstalledChecker(checker);
    }
    
    public void addListener(DiscoveryListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(DiscoveryListener listener) {
        listeners.remove(listener);
    }

    public CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request) {
        log.info("[discover] Starting discovery with method: {}, useIndexFirst: {}", 
            request.getMethod(), useIndexFirst);
        
        fireBeforeDiscovery(request);
        
        return doDiscover(request)
            .thenApply(result -> {
                fireAfterDiscovery(result);
                return result;
            });
    }
    
    private CompletableFuture<DiscoveryResult> doDiscover(DiscoveryRequest request) {
        if (useIndexFirst && request.isUseIndexFirst()) {
            DiscoveryResult indexResult = discoverFromIndex(request);
            if (indexResult.getTotalCount() > 0) {
                log.info("[discover] Found {} capabilities from index", indexResult.getTotalCount());
                return CompletableFuture.completedFuture(indexResult);
            }
        }
        
        SkillDiscoverer discoverer = discoverers.get(request.getMethod());
        if (discoverer != null && discoverer.isAvailable()) {
            return discoverer.discover(request);
        }
        
        if (mockEnabled) {
            log.info("[discover] Using mock data for method: {}", request.getMethod());
            return CompletableFuture.completedFuture(getMockResult(request));
        }
        
        return CompletableFuture.completedFuture(
            DiscoveryResult.error("No discoverer available for method: " + request.getMethod(), request.getMethod())
        );
    }
    
    private DiscoveryResult discoverFromIndex(DiscoveryRequest request) {
        String source = request.getSource() != null ? request.getSource() : "INDEX";
        
        List<CapabilityDTO> capabilities = skillIndexLoader.getSkillsFromIndex(source);
        List<CapabilityDTO> scenes = skillIndexLoader.getScenesFromIndex(source);
        
        capabilities = filterCapabilities(capabilities, request);
        scenes = filterCapabilities(scenes, request);
        
        DiscoveryResult result = new DiscoveryResult();
        result.setCapabilities(capabilities);
        result.setScenes(scenes);
        result.setMethod(DiscoveryMethod.INDEX);
        result.setSource(source);
        result.setFromCache(true);
        result.setScanTime(System.currentTimeMillis());
        
        return result;
    }
    
    private List<CapabilityDTO> filterCapabilities(List<CapabilityDTO> capabilities, DiscoveryRequest request) {
        if (capabilities == null || capabilities.isEmpty()) {
            return capabilities;
        }
        
        List<CapabilityDTO> filtered = new ArrayList<>(capabilities);
        
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            filtered.removeIf(cap -> !request.getCategory().equals(cap.getCategory()));
        }
        
        if (request.getSceneType() != null && !request.getSceneType().isEmpty()) {
            filtered.removeIf(cap -> !request.getSceneType().equals(cap.getSceneType()));
        }
        
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            filtered.removeIf(cap -> {
                if (cap.getTags() == null) return true;
                return !cap.getTags().containsAll(request.getTags());
            });
        }
        
        return filtered;
    }
    
    public CompletableFuture<CapabilityDTO> discoverOne(String skillId) {
        log.info("[discoverOne] Discovering single skill: {}", skillId);
        
        CapabilityDTO fromIndex = findFromIndex(skillId);
        if (fromIndex != null) {
            return CompletableFuture.completedFuture(fromIndex);
        }
        
        for (Map.Entry<DiscoveryMethod, SkillDiscoverer> entry : discoverers.entrySet()) {
            if (entry.getValue().isAvailable()) {
                try {
                    CapabilityDTO result = entry.getValue().discoverOne(skillId).get();
                    if (result != null) {
                        return CompletableFuture.completedFuture(result);
                    }
                } catch (Exception e) {
                    log.debug("[discoverOne] Discoverer {} failed: {}", entry.getKey(), e.getMessage());
                }
            }
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    private CapabilityDTO findFromIndex(String skillId) {
        for (CapabilityDTO cap : skillIndexLoader.getSkillsFromIndex("INDEX")) {
            if (skillId.equals(cap.getId())) {
                return cap;
            }
        }
        for (CapabilityDTO cap : skillIndexLoader.getScenesFromIndex("INDEX")) {
            if (skillId.equals(cap.getId())) {
                return cap;
            }
        }
        return null;
    }
    
    public DiscoveryResult discoverLocal() {
        return discover(DiscoveryRequest.forLocal()).join();
    }
    
    public DiscoveryResult discoverFromGitHub(String owner, String repo) {
        return discover(DiscoveryRequest.forGitHub(owner, repo)).join();
    }
    
    public DiscoveryResult discoverFromGitee(String owner, String repo) {
        return discover(DiscoveryRequest.forGitee(owner, repo)).join();
    }
    
    public DiscoveryResult discoverAll() {
        log.info("[discoverAll] Discovering from all sources");
        
        DiscoveryResult result = new DiscoveryResult();
        result.setMethod(DiscoveryMethod.HYBRID);
        result.setSource("ALL");
        result.setScanTime(System.currentTimeMillis());
        
        List<CapabilityDTO> allCapabilities = new ArrayList<>();
        allCapabilities.addAll(skillIndexLoader.getAllCapabilities("ALL"));
        
        Set<String> seenIds = new HashSet<>();
        List<CapabilityDTO> deduped = new ArrayList<>();
        for (CapabilityDTO cap : allCapabilities) {
            if (!seenIds.contains(cap.getId())) {
                seenIds.add(cap.getId());
                deduped.add(cap);
            }
        }
        
        result.setCapabilities(deduped);
        result.setFromCache(true);
        
        return result;
    }
    
    private DiscoveryResult getMockResult(DiscoveryRequest request) {
        DiscoveryResult result = new DiscoveryResult();
        result.setMethod(request.getMethod());
        result.setSource(request.getSource());
        result.setScanTime(System.currentTimeMillis());
        
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        capabilities.add(createMockCapability("daily-log-scene", "日志汇报场景", "SCENE", 
            "完整的日志汇报场景能力", "2.3", request.getSource()));
        capabilities.add(createMockCapability("report-remind", "日志提醒", "COMMUNICATION", 
            "定时提醒员工提交工作日志", "2.3", request.getSource()));
        capabilities.add(createMockCapability("report-submit", "日志提交", "SERVICE", 
            "员工提交工作日志的表单能力", "2.3", request.getSource()));
        
        result.setCapabilities(capabilities);
        return result;
    }
    
    private CapabilityDTO createMockCapability(String id, String name, String type, 
            String description, String version, String source) {
        CapabilityDTO cap = new CapabilityDTO();
        cap.setId(id);
        cap.setName(name);
        cap.setType(type);
        cap.setDescription(description);
        cap.setVersion(version);
        cap.setSource(source);
        cap.setStatus("available");
        cap.setSceneCapability("SCENE".equals(type));
        return cap;
    }
    
    private void fireBeforeDiscovery(DiscoveryRequest request) {
        for (DiscoveryListener listener : listeners) {
            try {
                listener.onBeforeDiscovery(request);
            } catch (Exception e) {
                log.warn("[fireBeforeDiscovery] Listener error: {}", e.getMessage());
            }
        }
    }
    
    private void fireAfterDiscovery(DiscoveryResult result) {
        for (DiscoveryListener listener : listeners) {
            try {
                listener.onAfterDiscovery(result);
            } catch (Exception e) {
                log.warn("[fireAfterDiscovery] Listener error: {}", e.getMessage());
            }
        }
    }
    
    public SkillIndexLoader getSkillIndexLoader() {
        return skillIndexLoader;
    }
    
    public void reloadIndex() {
        skillIndexLoader.reload();
    }
}
