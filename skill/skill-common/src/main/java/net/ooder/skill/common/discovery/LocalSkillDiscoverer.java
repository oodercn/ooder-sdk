package net.ooder.skill.common.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.yaml.snakeyaml.Yaml;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class LocalSkillDiscoverer implements SkillDiscoverer {

    private static final Logger log = LoggerFactory.getLogger(LocalSkillDiscoverer.class);

    private static final String[] SKILL_FILE_NAMES = {"skill.yaml", "skill.yml", "skill.json"};

    @Value("${ooder.skills.path:./skills}")
    private String skillsPath;

    @Value("${ooder.skills.search-paths:}")
    private String searchPathsConfig;

    @Value("${ooder.mock.enabled:false}")
    private boolean mockEnabled;

    private final List<String> searchPaths = new ArrayList<>();
    private Function<String, Boolean> installedChecker;

    @PostConstruct
    public void init() {
        initSearchPaths();
        log.info("[LocalSkillDiscoverer] Initialized with {} search paths", searchPaths.size());
    }

    private void initSearchPaths() {
        if (searchPathsConfig != null && !searchPathsConfig.isEmpty()) {
            String[] customPaths = searchPathsConfig.split(",");
            for (String path : customPaths) {
                path = path.trim();
                if (!path.isEmpty()) {
                    searchPaths.add(path);
                }
            }
        }

        searchPaths.add(skillsPath);
        searchPaths.add("./skills");
        searchPaths.add("./skills-jars");
        searchPaths.add("./.ooder/downloads");
        searchPaths.add("./.ooder/installed");
        searchPaths.add("./.ooder/activated");
        searchPaths.add("../skills");
        searchPaths.add("../../skills");
    }

    public void setSkillsPath(String path) {
        this.skillsPath = path;
        initSearchPaths();
    }

    public void setSearchPaths(List<String> paths) {
        this.searchPaths.clear();
        this.searchPaths.addAll(paths);
    }

    public void setInstalledChecker(Function<String, Boolean> checker) {
        this.installedChecker = checker;
    }

    @Override
    public CompletableFuture<DiscoveryResult> discover(DiscoveryRequest request) {
        log.info("[discover] Starting local discovery with {} search paths", searchPaths.size());

        DiscoveryResult result = new DiscoveryResult();
        result.setMethod(DiscoveryMethod.LOCAL);
        result.setSource("LOCAL");
        result.setScanTime(System.currentTimeMillis());

        List<CapabilityDTO> capabilities = new ArrayList<>();
        Set<String> seenSkillIds = new HashSet<>();

        for (String searchPath : searchPaths) {
            Path path = Paths.get(searchPath);
            if (Files.exists(path) && Files.isDirectory(path)) {
                log.debug("[discover] Scanning directory: {}", searchPath);
                List<CapabilityDTO> found = scanDirectory(path, seenSkillIds);
                capabilities.addAll(found);
                log.info("[discover] Found {} skills in {}", found.size(), searchPath);
            }
        }

        result.setCapabilities(capabilities);
        result.setFromCache(false);

        log.info("[discover] Local discovery completed: {} skills found", capabilities.size());
        return CompletableFuture.completedFuture(result);
    }

    private List<CapabilityDTO> scanDirectory(Path directory, Set<String> seenSkillIds) {
        List<CapabilityDTO> capabilities = new ArrayList<>();

        try {
            Files.walk(directory, 3)
                .filter(Files::isDirectory)
                .forEach(dir -> {
                    CapabilityDTO cap = scanSkillDirectory(dir, seenSkillIds);
                    if (cap != null) {
                        capabilities.add(cap);
                    }
                });
        } catch (IOException e) {
            log.warn("[scanDirectory] Error scanning directory {}: {}", directory, e.getMessage());
        }

        return capabilities;
    }

    private CapabilityDTO scanSkillDirectory(Path skillDir, Set<String> seenSkillIds) {
        for (String fileName : SKILL_FILE_NAMES) {
            Path skillFile = skillDir.resolve(fileName);
            if (Files.exists(skillFile)) {
                try {
                    CapabilityDTO cap = parseSkillFile(skillFile, skillDir);
                    if (cap != null && cap.getId() != null) {
                        if (seenSkillIds.contains(cap.getId())) {
                            return null;
                        }
                        seenSkillIds.add(cap.getId());

                        boolean isInstalled = checkIfInstalled(cap.getId());
                        cap.setStatus(isInstalled ? "installed" : "available");
                        cap.setInstalled(isInstalled);

                        log.debug("[scanSkillDirectory] Found skill: {} from {}", cap.getId(), skillFile);
                        return cap;
                    }
                } catch (Exception e) {
                    log.warn("[scanSkillDirectory] Error parsing {}: {}", skillFile, e.getMessage());
                }
            }
        }

        Path srcResourcesPath = skillDir.resolve("src/main/resources");
        if (Files.exists(srcResourcesPath)) {
            for (String fileName : SKILL_FILE_NAMES) {
                Path skillFile = srcResourcesPath.resolve(fileName);
                if (Files.exists(skillFile)) {
                    try {
                        CapabilityDTO cap = parseSkillFile(skillFile, skillDir);
                        if (cap != null && cap.getId() != null) {
                            if (seenSkillIds.contains(cap.getId())) {
                                return null;
                            }
                            seenSkillIds.add(cap.getId());

                            boolean isInstalled = checkIfInstalled(cap.getId());
                            cap.setStatus(isInstalled ? "installed" : "available");
                            cap.setInstalled(isInstalled);

                            log.debug("[scanSkillDirectory] Found skill: {} from {}", cap.getId(), skillFile);
                            return cap;
                        }
                    } catch (Exception e) {
                        log.warn("[scanSkillDirectory] Error parsing {}: {}", skillFile, e.getMessage());
                    }
                }
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private CapabilityDTO parseSkillFile(Path skillFile, Path skillDir) {
        Yaml yaml = new Yaml();

        try (InputStream is = Files.newInputStream(skillFile)) {
            Map<String, Object> data = yaml.load(is);
            if (data == null) {
                return null;
            }

            CapabilityDTO cap = new CapabilityDTO();

            Map<String, Object> metadata = (Map<String, Object>) data.get("metadata");
            Map<String, Object> spec = (Map<String, Object>) data.get("spec");

            String skillId = null;
            String name = null;
            String version = null;
            String description = null;

            if (metadata != null) {
                skillId = (String) metadata.get("id");
                name = (String) metadata.get("name");
                version = (String) metadata.get("version");
                description = (String) metadata.get("description");
            }

            if (skillId == null) {
                skillId = (String) data.get("id");
            }
            if (skillId == null) {
                skillId = (String) data.get("skillId");
            }
            if (skillId == null) {
                skillId = skillDir.getFileName().toString();
            }

            if (name == null) {
                name = (String) data.get("name");
            }
            if (version == null) {
                version = (String) data.get("version");
            }
            if (description == null) {
                description = (String) data.get("description");
            }

            cap.setId(skillId);
            cap.setSkillId(skillId);
            cap.setName(name);
            cap.setVersion(version);
            cap.setDescription(description);
            cap.setSource("LOCAL");

            if (spec != null) {
                parseSpec(cap, spec);
            }

            String type = (String) data.get("type");
            if (type != null) {
                cap.setType(type.toUpperCase());
                if ("scene".equalsIgnoreCase(type) || "scene-skill".equalsIgnoreCase(type)) {
                    cap.setSceneCapability(true);
                    cap.setSkillForm("SCENE");
                }
            }

            Object caps = data.get("capabilities");
            if (caps instanceof List) {
                cap.setCapabilities((List<String>) caps);
            }

            return cap;

        } catch (IOException e) {
            log.warn("[parseSkillFile] Error reading {}: {}", skillFile, e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private void parseSpec(CapabilityDTO cap, Map<String, Object> spec) {
        String skillForm = (String) spec.get("skillForm");
        if (skillForm != null) {
            cap.setSkillForm(skillForm);
            if ("SCENE".equals(skillForm)) {
                cap.setSceneCapability(true);
                cap.setType("SCENE");
            } else {
                cap.setType("SKILL");
            }
        }

        String sceneType = (String) spec.get("sceneType");
        if (sceneType != null) {
            cap.setSceneType(sceneType);
        }

        String visibility = (String) spec.get("visibility");
        if (visibility != null) {
            cap.setVisibility(visibility);
        }

        String category = (String) spec.get("category");
        if (category == null) {
            Map<String, Object> capability = (Map<String, Object>) spec.get("capability");
            if (capability != null) {
                category = (String) capability.get("category");
            }
        }
        if (category != null) {
            cap.setCategory(category);
            cap.setCapabilityCategory(category);
        }

        String businessCategory = (String) spec.get("businessCategory");
        if (businessCategory != null) {
            cap.setBusinessCategory(businessCategory);
        }

        Object tagsObj = spec.get("tags");
        if (tagsObj instanceof List) {
            cap.setTags((List<String>) tagsObj);
        }

        Object depsObj = spec.get("dependencies");
        if (depsObj instanceof List) {
            cap.setDependencies((List<String>) depsObj);
        }

        Object capsObj = spec.get("capabilities");
        if (capsObj instanceof List) {
            cap.setCapabilities((List<String>) capsObj);
        }
    }

    private boolean checkIfInstalled(String skillId) {
        if (skillId == null) {
            return false;
        }

        if (installedChecker != null) {
            try {
                return Boolean.TRUE.equals(installedChecker.apply(skillId));
            } catch (Exception e) {
                log.debug("[checkIfInstalled] Failed to check installation status for {}: {}", skillId, e.getMessage());
                return false;
            }
        }

        return false;
    }

    @Override
    public CompletableFuture<CapabilityDTO> discoverOne(String skillId) {
        log.info("[discoverOne] Discovering single skill: {}", skillId);

        for (String searchPath : searchPaths) {
            Path path = Paths.get(searchPath);
            if (Files.exists(path) && Files.isDirectory(path)) {
                CapabilityDTO cap = findSkillById(path, skillId);
                if (cap != null) {
                    return CompletableFuture.completedFuture(cap);
                }
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    private CapabilityDTO findSkillById(Path directory, String skillId) {
        try {
            return Files.walk(directory, 3)
                .filter(Files::isDirectory)
                .map(dir -> {
                    for (String fileName : SKILL_FILE_NAMES) {
                        Path skillFile = dir.resolve(fileName);
                        if (Files.exists(skillFile)) {
                            try {
                                CapabilityDTO cap = parseSkillFile(skillFile, dir);
                                if (cap != null && skillId.equals(cap.getId())) {
                                    boolean isInstalled = checkIfInstalled(skillId);
                                    cap.setStatus(isInstalled ? "installed" : "available");
                                    cap.setInstalled(isInstalled);
                                    return cap;
                                }
                            } catch (Exception e) {
                                log.debug("[findSkillById] Error parsing {}: {}", skillFile, e.getMessage());
                            }
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        } catch (IOException e) {
            log.warn("[findSkillById] Error searching for skill {}: {}", skillId, e.getMessage());
            return null;
        }
    }

    @Override
    public DiscoveryMethod getMethod() {
        return DiscoveryMethod.LOCAL;
    }

    @Override
    public boolean isAvailable() {
        for (String searchPath : searchPaths) {
            Path path = Paths.get(searchPath);
            if (Files.exists(path) && Files.isDirectory(path)) {
                return true;
            }
        }
        return false;
    }
}
