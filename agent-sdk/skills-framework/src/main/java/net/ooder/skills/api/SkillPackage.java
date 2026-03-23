
package net.ooder.skills.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkillPackage {
    
    private String skillId;
    private String name;
    private String description;
    private String version;
    private String sceneId;
    private String source;
    private String downloadUrl;
    private String checksum;
    private long size;
    private List<Capability> capabilities;
    private List<String> dependencies;
    private Map<String, Object> metadata;
    private SkillManifest manifest;
    private String category;
    private String subCategory;
    private List<String> tags;
    private String resourcePath;
    
    public String getSkillId() {
        return skillId;
    }
    
    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    
    public String getChecksum() {
        return checksum;
    }
    
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
    
    public long getSize() {
        return size;
    }
    
    public void setSize(long size) {
        this.size = size;
    }
    
    public List<Capability> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(List<Capability> capabilities) {
        this.capabilities = capabilities;
    }
    
    public List<String> getDependencies() {
        return dependencies;
    }
    
    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public SkillManifest getManifest() {
        return manifest;
    }
    
    public void setManifest(SkillManifest manifest) {
        this.manifest = manifest;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getSubCategory() {
        return subCategory;
    }
    
    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public String getResourcePath() {
        return resourcePath;
    }
    
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
    
    public InputStream getResource(String relativePath) {
        if (resourcePath == null || relativePath == null) {
            return null;
        }
        
        try {
            Path fullPath = Paths.get(resourcePath, relativePath);
            if (Files.exists(fullPath)) {
                return new FileInputStream(fullPath.toFile());
            }
            
            String classpathResource = "/" + skillId + "/" + relativePath;
            InputStream is = getClass().getResourceAsStream(classpathResource);
            if (is != null) {
                return is;
            }
            
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            return loader.getResourceAsStream(skillId + "/" + relativePath);
            
        } catch (IOException e) {
            return null;
        }
    }
    
    public boolean hasResource(String relativePath) {
        if (resourcePath == null || relativePath == null) {
            return false;
        }
        
        Path fullPath = Paths.get(resourcePath, relativePath);
        if (Files.exists(fullPath)) {
            return true;
        }
        
        try (InputStream is = getResource(relativePath)) {
            return is != null;
        } catch (IOException e) {
            return false;
        }
    }
    
    public List<String> listResources(String directory) {
        if (resourcePath == null || directory == null) {
            return Collections.emptyList();
        }
        
        try {
            Path dirPath = Paths.get(resourcePath, directory);
            if (Files.isDirectory(dirPath)) {
                return Files.list(dirPath)
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());
            }
        } catch (IOException e) {
            // ignore
        }
        
        return Collections.emptyList();
    }
}
