package net.ooder.skill.common.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Service
public class SkillIndexLoader {

    private static final Logger log = LoggerFactory.getLogger(SkillIndexLoader.class);

    @Value("${ooder.skill.index-dir:skill-index}")
    private String skillIndexDir;
    
    @Value("${ooder.skill.index-search-paths:}")
    private String indexSearchPathsConfig;
    
    @Value("${ooder.skill.entry-search-paths:}")
    private String entrySearchPathsConfig;
    
    @Value("${ooder.mock.enabled:false}")
    private boolean mockEnabled;

    private List<Map<String, Object>> skills = new ArrayList<>();
    private List<Map<String, Object>> scenes = new ArrayList<>();
    private List<Map<String, Object>> categories = new ArrayList<>();
    private List<Map<String, Object>> sceneDrivers = new ArrayList<>();
    
    private Set<String> mockInstalledSkills = new HashSet<>();
    
    private Function<String, Boolean> installedChecker;

    @PostConstruct
    public void init() {
        loadSkillIndex();
    }
    
    public void setInstalledChecker(Function<String, Boolean> checker) {
        this.installedChecker = checker;
    }

    public void loadSkillIndex() {
        log.info("[loadSkillIndex] Loading skill index from directory: {}", skillIndexDir);
        
        Yaml yaml = new Yaml();
        
        try {
            File indexDir = findSkillIndexDir();
            if (indexDir != null) {
                loadFromDirectory(indexDir, yaml);
            } else {
                log.warn("[loadSkillIndex] Skill index directory not found");
            }

            log.info("[loadSkillIndex] Total: {} skills, {} scenes, {} categories",
                skills.size(), scenes.size(), categories.size());
        } catch (Exception e) {
            log.error("[loadSkillIndex] Failed to load skill index: {}", e.getMessage(), e);
        }
    }

    private File findSkillIndexDir() {
        List<String> searchPaths = getDefaultIndexSearchPaths();
        
        if (indexSearchPathsConfig != null && !indexSearchPathsConfig.isEmpty()) {
            String[] customPaths = indexSearchPathsConfig.split(",");
            for (String path : customPaths) {
                path = path.trim();
                if (!path.isEmpty()) {
                    searchPaths.add(0, path);
                }
            }
        }

        for (String path : searchPaths) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                log.info("[loadSkillIndex] Found index directory: {}", dir.getAbsolutePath());
                return dir;
            }
        }
        return null;
    }
    
    private List<String> getDefaultIndexSearchPaths() {
        List<String> paths = new ArrayList<>();
        paths.add(skillIndexDir);
        paths.add("../" + skillIndexDir);
        paths.add("../../" + skillIndexDir);
        paths.add("e:/github/ooder-skills/skill-index");
        return paths;
    }

    @SuppressWarnings("unchecked")
    private void loadFromDirectory(File indexDir, Yaml yaml) {
        log.info("[loadSkillIndex] Loading from directory: {}", indexDir.getAbsolutePath());
        
        File categoriesFile = new File(indexDir, "categories.yaml");
        if (categoriesFile.exists()) {
            try (Reader reader = new InputStreamReader(new FileInputStream(categoriesFile), StandardCharsets.UTF_8)) {
                Map<String, Object> data = yaml.load(reader);
                List<Map<String, Object>> cats = (List<Map<String, Object>>) data.get("categories");
                if (cats != null && !cats.isEmpty()) {
                    categories = cats;
                    log.info("[loadSkillIndex] Loaded {} categories from categories.yaml", cats.size());
                }
            } catch (Exception e) {
                log.warn("[loadSkillIndex] Failed to load categories.yaml: {}", e.getMessage());
            }
        }

        File sceneDriversFile = new File(indexDir, "scene-drivers.yaml");
        if (sceneDriversFile.exists()) {
            try (Reader reader = new InputStreamReader(new FileInputStream(sceneDriversFile), StandardCharsets.UTF_8)) {
                Map<String, Object> data = yaml.load(reader);
                List<Map<String, Object>> drivers = (List<Map<String, Object>>) data.get("sceneDrivers");
                if (drivers != null && !drivers.isEmpty()) {
                    sceneDrivers = drivers;
                    log.info("[loadSkillIndex] Loaded {} sceneDrivers from scene-drivers.yaml", drivers.size());
                }
            } catch (Exception e) {
                log.warn("[loadSkillIndex] Failed to load scene-drivers.yaml: {}", e.getMessage());
            }
        }

        File skillsDir = new File(indexDir, "skills");
        if (skillsDir.exists() && skillsDir.isDirectory()) {
            File[] skillFiles = skillsDir.listFiles((dir, name) -> name.endsWith(".yaml"));
            if (skillFiles != null) {
                for (File skillFile : skillFiles) {
                    try (Reader reader = new InputStreamReader(new FileInputStream(skillFile), StandardCharsets.UTF_8)) {
                        Map<String, Object> data = yaml.load(reader);
                        List<Map<String, Object>> fileSkills = (List<Map<String, Object>>) data.get("skills");
                        if (fileSkills != null) {
                            skills.addAll(fileSkills);
                            log.info("[loadSkillIndex] Loaded {} skills from {}", fileSkills.size(), skillFile.getName());
                        }
                    } catch (Exception e) {
                        log.warn("[loadSkillIndex] Failed to load {}: {}", skillFile.getName(), e.getMessage());
                    }
                }
            }
        }

        File scenesDir = new File(indexDir, "scenes");
        if (scenesDir.exists() && scenesDir.isDirectory()) {
            File[] sceneFiles = scenesDir.listFiles((dir, name) -> name.endsWith(".yaml"));
            if (sceneFiles != null) {
                for (File sceneFile : sceneFiles) {
                    try (Reader reader = new InputStreamReader(new FileInputStream(sceneFile), StandardCharsets.UTF_8)) {
                        Map<String, Object> data = yaml.load(reader);
                        List<Map<String, Object>> fileScenes = (List<Map<String, Object>>) data.get("scenes");
                        if (fileScenes != null) {
                            scenes.addAll(fileScenes);
                            log.info("[loadSkillIndex] Loaded {} scenes from {}", fileScenes.size(), sceneFile.getName());
                        }
                    } catch (Exception e) {
                        log.warn("[loadSkillIndex] Failed to load {}: {}", sceneFile.getName(), e.getMessage());
                    }
                }
            }
        }
    }

    public List<CapabilityDTO> getSkillsFromIndex(String source) {
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        Set<String> sceneIds = new HashSet<>();
        for (Map<String, Object> scene : scenes) {
            String sceneId = (String) scene.get("sceneId");
            if (sceneId != null) {
                sceneIds.add(sceneId);
            }
        }
        
        for (Map<String, Object> skill : skills) {
            CapabilityDTO cap = convertSkillToDTO(skill, source);
            capabilities.add(cap);
        }
        
        return capabilities;
    }
    
    @SuppressWarnings("unchecked")
    private CapabilityDTO convertSkillToDTO(Map<String, Object> skill, String source) {
        CapabilityDTO cap = new CapabilityDTO();
        String skillId = MetadataCompat.getSkillId(skill);
        cap.setId(skillId);
        cap.setName((String) skill.get("name"));
        cap.setDescription((String) skill.get("description"));
        cap.setVersion((String) skill.get("version"));
        cap.setSource(source);
        
        boolean isInstalled = checkIfInstalled(skillId);
        cap.setStatus(isInstalled ? "installed" : "available");
        
        Object caps = skill.get("capabilities");
        if (caps instanceof List) {
            cap.setCapabilities((List<String>) caps);
        }
        
        boolean isScene = MetadataCompat.isSceneSkill(skill);
        
        cap.setType(isScene ? "SCENE" : "SKILL");
        cap.setSceneCapability(isScene);
        
        Object skillFormObj = skill.get("skillForm");
        if (skillFormObj == null) {
            Map<String, Object> spec = (Map<String, Object>) skill.get("spec");
            if (spec != null) {
                skillFormObj = spec.get("skillForm");
            }
        }
        String skillFormCode = skillFormObj != null ? String.valueOf(skillFormObj) : null;
        if (skillFormCode == null) {
            skillFormCode = isScene ? "SCENE" : "PROVIDER";
        }
        cap.setSkillForm(skillFormCode);
        
        Object businessCategoryObj = skill.get("businessCategory");
        if (businessCategoryObj == null) {
            Map<String, Object> spec = (Map<String, Object>) skill.get("spec");
            if (spec != null) {
                businessCategoryObj = spec.get("businessCategory");
            }
        }
        if (businessCategoryObj != null) {
            cap.setBusinessCategory(String.valueOf(businessCategoryObj));
        }
        
        Object categoryObj = skill.get("category");
        if (categoryObj == null) {
            Map<String, Object> spec = (Map<String, Object>) skill.get("spec");
            if (spec != null) {
                categoryObj = spec.get("category");
            }
        }
        if (categoryObj != null) {
            cap.setCategory(String.valueOf(categoryObj));
        }
        
        Object capabilityCategoryObj = skill.get("capabilityCategory");
        if (capabilityCategoryObj != null) {
            cap.setCapabilityCategory(String.valueOf(capabilityCategoryObj));
        }
        
        cap.setVisibility(MetadataCompat.getVisibility(skill));
        
        if (isScene) {
            populateSceneFields(cap, skill);
        }
        
        return cap;
    }
    
    @SuppressWarnings("unchecked")
    private void populateSceneFields(CapabilityDTO cap, Map<String, Object> skill) {
        Object sceneTypeObj = skill.get("sceneType");
        if (sceneTypeObj == null) {
            Map<String, Object> spec = (Map<String, Object>) skill.get("spec");
            if (spec != null) {
                sceneTypeObj = spec.get("sceneType");
            }
        }
        String sceneTypeCode = sceneTypeObj != null ? String.valueOf(sceneTypeObj) : "MANUAL";
        
        boolean hasSelfDrive = "AUTO".equals(sceneTypeCode);
        
        cap.setSceneType(sceneTypeCode);
        cap.setMainFirst(hasSelfDrive);
        
        Object driverConditionsObj = skill.get("driverConditions");
        if (driverConditionsObj == null) {
            Map<String, Object> spec = (Map<String, Object>) skill.get("spec");
            if (spec != null) {
                driverConditionsObj = spec.get("driverConditions");
            }
        }
        if (driverConditionsObj instanceof List) {
            List<Map<String, Object>> driverConditions = (List<Map<String, Object>>) driverConditionsObj;
            cap.setDriverConditions(convertToMapList(driverConditions));
        }
        
        Object participantsObj = skill.get("participants");
        if (participantsObj == null) {
            Map<String, Object> spec = (Map<String, Object>) skill.get("spec");
            if (spec != null) {
                participantsObj = spec.get("participants");
            }
        }
        if (participantsObj instanceof List) {
            List<Map<String, Object>> participants = (List<Map<String, Object>>) participantsObj;
            cap.setParticipants(convertToMapList(participants));
        }
    }
    
    private List<Map<String, Object>> convertToMapList(List<Map<String, Object>> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> item : list) {
            result.add(new HashMap<>(item));
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public List<CapabilityDTO> getSkillsFromEntryFiles(String source) {
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        List<File> entryFiles = findSkillIndexEntryFiles();
        log.info("[getSkillsFromEntryFiles] Found {} skill-index-entry.yaml files", entryFiles.size());
        
        Yaml yaml = new Yaml();
        
        for (File entryFile : entryFiles) {
            try (Reader reader = new InputStreamReader(new FileInputStream(entryFile), StandardCharsets.UTF_8)) {
                Map<String, Object> entry = yaml.load(reader);
                if (entry == null) continue;
                
                Map<String, Object> metadata = (Map<String, Object>) entry.get("metadata");
                Map<String, Object> spec = (Map<String, Object>) entry.get("spec");
                
                if (metadata == null || spec == null) continue;
                
                CapabilityDTO cap = convertEntryToDTO(metadata, spec, source);
                capabilities.add(cap);
                
            } catch (Exception e) {
                log.warn("[getSkillsFromEntryFiles] Failed to load {}: {}", entryFile.getAbsolutePath(), e.getMessage());
            }
        }
        
        return capabilities;
    }
    
    @SuppressWarnings("unchecked")
    private CapabilityDTO convertEntryToDTO(Map<String, Object> metadata, Map<String, Object> spec, String source) {
        CapabilityDTO cap = new CapabilityDTO();
        String skillId = (String) metadata.get("id");
        cap.setId(skillId);
        cap.setName((String) metadata.get("name"));
        cap.setDescription((String) metadata.get("description"));
        cap.setVersion((String) metadata.get("version"));
        cap.setSource(source);
        
        boolean isInstalled = checkIfInstalled(skillId);
        cap.setStatus(isInstalled ? "installed" : "available");
        
        String skillForm = (String) spec.get("skillForm");
        cap.setSkillForm(skillForm != null ? skillForm : "PROVIDER");
        
        String sceneType = (String) spec.get("sceneType");
        cap.setSceneType(sceneType);
        
        String visibility = (String) spec.get("visibility");
        cap.setVisibility(visibility != null ? visibility : "public");
        
        String businessCategory = (String) spec.get("businessCategory");
        cap.setBusinessCategory(businessCategory);
        
        String capabilityCategory = (String) spec.get("capabilityCategory");
        cap.setCapabilityCategory(capabilityCategory);
        
        String category = (String) spec.get("category");
        cap.setCategory(category);
        
        String subCategory = (String) spec.get("subCategory");
        cap.setSubCategory(subCategory);
        
        Object tagsObj = spec.get("tags");
        if (tagsObj instanceof List) {
            cap.setTags((List<String>) tagsObj);
        }
        
        Object dependenciesObj = spec.get("dependencies");
        if (dependenciesObj instanceof List) {
            cap.setDependencies((List<String>) dependenciesObj);
        }
        
        boolean isScene = "SCENE".equals(skillForm);
        cap.setType(isScene ? "SCENE" : "SKILL");
        cap.setSceneCapability(isScene);
        
        return cap;
    }
    
    private List<File> findSkillIndexEntryFiles() {
        List<File> files = new ArrayList<>();
        
        List<String> searchPaths = getDefaultEntrySearchPaths();
        
        if (entrySearchPathsConfig != null && !entrySearchPathsConfig.isEmpty()) {
            String[] customPaths = entrySearchPathsConfig.split(",");
            for (String path : customPaths) {
                path = path.trim();
                if (!path.isEmpty()) {
                    searchPaths.add(0, path);
                }
            }
        }
        
        for (String searchPath : searchPaths) {
            File rootDir = new File(searchPath);
            if (rootDir.exists() && rootDir.isDirectory()) {
                findFilesRecursive(rootDir, "skill-index-entry.yaml", files);
                if (!files.isEmpty()) {
                    log.info("[findSkillIndexEntryFiles] Found {} files in {}", files.size(), searchPath);
                    break;
                }
            }
        }
        
        return files;
    }
    
    private List<String> getDefaultEntrySearchPaths() {
        List<String> paths = new ArrayList<>();
        paths.add("E:/github/ooder-skills/skills");
        paths.add("../skills");
        paths.add("../../skills");
        paths.add("../../../skills");
        return paths;
    }
    
    private void findFilesRecursive(File dir, String fileName, List<File> result) {
        File[] children = dir.listFiles();
        if (children == null) return;
        
        for (File child : children) {
            if (child.isDirectory()) {
                findFilesRecursive(child, fileName, result);
            } else if (fileName.equals(child.getName())) {
                result.add(child);
            }
        }
    }
    
    private boolean checkIfInstalled(String skillId) {
        if (skillId == null) {
            return false;
        }
        
        if (mockEnabled && mockInstalledSkills.contains(skillId)) {
            return true;
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
    
    public void markAsInstalled(String skillId) {
        if (skillId != null) {
            mockInstalledSkills.add(skillId);
            log.info("[markAsInstalled] Marked {} as installed (mock mode)", skillId);
        }
    }
    
    public void markAsUninstalled(String skillId) {
        if (skillId != null) {
            mockInstalledSkills.remove(skillId);
            log.info("[markAsUninstalled] Marked {} as uninstalled (mock mode)", skillId);
        }
    }

    public List<RepositoryDTO> getRepositories(String source) {
        List<RepositoryDTO> repos = new ArrayList<>();
        
        for (Map<String, Object> skill : skills) {
            RepositoryDTO repo = new RepositoryDTO();
            repo.setFullName(MetadataCompat.getSkillId(skill));
            repo.setName((String) skill.get("name"));
            repo.setDescription((String) skill.get("description"));
            repo.setHtmlUrl((String) skill.get("giteeDownloadUrl"));
            repo.setLatestVersion((String) skill.get("version"));
            repos.add(repo);
        }
        
        return repos;
    }

    public Map<String, Object> getSkillIndex() {
        Map<String, Object> result = new HashMap<>();
        result.put("skills", skills);
        result.put("scenes", scenes);
        result.put("categories", categories);
        result.put("sceneDrivers", sceneDrivers);
        return result;
    }

    public List<Map<String, Object>> getSkills() {
        return skills;
    }

    public List<Map<String, Object>> getScenes() {
        return scenes;
    }

    public List<Map<String, Object>> getCategories() {
        return categories;
    }

    public List<Map<String, Object>> getSceneDrivers() {
        return sceneDrivers;
    }

    public List<CapabilityDTO> getScenesFromIndex(String source) {
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        for (Map<String, Object> scene : scenes) {
            CapabilityDTO cap = convertSceneToDTO(scene, source);
            capabilities.add(cap);
        }
        
        return capabilities;
    }
    
    @SuppressWarnings("unchecked")
    private CapabilityDTO convertSceneToDTO(Map<String, Object> scene, String source) {
        CapabilityDTO cap = new CapabilityDTO();
        cap.setId((String) scene.get("sceneId"));
        cap.setName((String) scene.get("name"));
        cap.setDescription((String) scene.get("description"));
        cap.setVersion((String) scene.get("version"));
        cap.setSource(source);
        cap.setStatus("available");
        cap.setType("SCENE");
        cap.setSceneCapability(true);
        
        Object caps = scene.get("capabilities");
        if (caps instanceof List) {
            cap.setCapabilities((List<String>) caps);
        }
        
        Object sceneTypeObj = scene.get("sceneType");
        if (sceneTypeObj == null) {
            Map<String, Object> spec = (Map<String, Object>) scene.get("spec");
            if (spec != null) {
                sceneTypeObj = spec.get("sceneType");
            }
        }
        String sceneTypeCode = sceneTypeObj != null ? String.valueOf(sceneTypeObj) : "MANUAL";
        
        Object skillFormObj = scene.get("skillForm");
        if (skillFormObj == null) {
            Map<String, Object> spec = (Map<String, Object>) scene.get("spec");
            if (spec != null) {
                skillFormObj = spec.get("skillForm");
            }
        }
        String skillFormCode = skillFormObj != null ? String.valueOf(skillFormObj) : "STANDALONE";
        
        cap.setSceneType(sceneTypeCode);
        cap.setSkillForm(skillFormCode);
        cap.setMainFirst("AUTO".equals(sceneTypeCode));
        
        Object visibilityObj = scene.get("visibility");
        if (visibilityObj == null) {
            Map<String, Object> spec = (Map<String, Object>) scene.get("spec");
            if (spec != null) {
                visibilityObj = spec.get("visibility");
            }
        }
        cap.setVisibility(visibilityObj != null ? String.valueOf(visibilityObj) : "public");
        
        Object driverConditionsObj = scene.get("driverConditions");
        if (driverConditionsObj == null) {
            Map<String, Object> spec = (Map<String, Object>) scene.get("spec");
            if (spec != null) {
                driverConditionsObj = spec.get("driverConditions");
            }
        }
        if (driverConditionsObj instanceof List) {
            List<Map<String, Object>> driverConditions = (List<Map<String, Object>>) driverConditionsObj;
            cap.setDriverConditions(convertToMapList(driverConditions));
        }
        
        Object participantsObj = scene.get("participants");
        if (participantsObj == null) {
            Map<String, Object> spec = (Map<String, Object>) scene.get("spec");
            if (spec != null) {
                participantsObj = spec.get("participants");
            }
        }
        if (participantsObj instanceof List) {
            List<Map<String, Object>> participants = (List<Map<String, Object>>) participantsObj;
            cap.setParticipants(convertToMapList(participants));
        }
        
        return cap;
    }

    public List<CapabilityDTO> getAllCapabilities(String source) {
        List<CapabilityDTO> all = new ArrayList<>();
        all.addAll(getSkillsFromIndex(source));
        all.addAll(getScenesFromIndex(source));
        return all;
    }
    
    public Map<String, Object> getSkillInfo(String skillId) {
        if (skillId == null || skills == null) {
            return null;
        }
        
        for (Map<String, Object> skill : skills) {
            String id = MetadataCompat.getSkillId(skill);
            if (skillId.equals(id)) {
                return skill;
            }
        }
        
        return null;
    }
    
    public String getDownloadUrl(String skillId) {
        Map<String, Object> skill = getSkillInfo(skillId);
        if (skill != null) {
            String giteeUrl = (String) skill.get("giteeDownloadUrl");
            String githubUrl = (String) skill.get("downloadUrl");
            return giteeUrl != null ? giteeUrl : githubUrl;
        }
        return null;
    }
    
    public void reload() {
        skills.clear();
        scenes.clear();
        categories.clear();
        sceneDrivers.clear();
        loadSkillIndex();
    }
    
    public void loadFromDirectory(File indexDir) {
        if (indexDir != null && indexDir.exists() && indexDir.isDirectory()) {
            loadFromDirectory(indexDir, new Yaml());
        }
    }
}
