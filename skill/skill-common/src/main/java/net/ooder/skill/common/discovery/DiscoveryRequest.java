package net.ooder.skill.common.discovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscoveryRequest {
    
    private DiscoveryMethod method;
    private String source;
    private String repoUrl;
    private String branch;
    private String category;
    private String sceneType;
    private List<String> tags = new ArrayList<>();
    private boolean useIndexFirst = true;
    private Map<String, Object> options = new HashMap<>();
    
    public DiscoveryRequest() {}
    
    public static DiscoveryRequest forLocal() {
        DiscoveryRequest request = new DiscoveryRequest();
        request.setMethod(DiscoveryMethod.LOCAL);
        request.setSource("LOCAL");
        return request;
    }
    
    public static DiscoveryRequest forGitHub(String owner, String repo) {
        DiscoveryRequest request = new DiscoveryRequest();
        request.setMethod(DiscoveryMethod.GITHUB);
        request.setSource("GITHUB");
        request.setRepoUrl("https://github.com/" + owner + "/" + repo);
        return request;
    }
    
    public static DiscoveryRequest forGitee(String owner, String repo) {
        DiscoveryRequest request = new DiscoveryRequest();
        request.setMethod(DiscoveryMethod.GITEE);
        request.setSource("GITEE");
        request.setRepoUrl("https://gitee.com/" + owner + "/" + repo);
        return request;
    }
    
    public static DiscoveryRequest forGit(String repoUrl, String branch) {
        DiscoveryRequest request = new DiscoveryRequest();
        request.setMethod(DiscoveryMethod.GIT);
        request.setSource("GIT");
        request.setRepoUrl(repoUrl);
        request.setBranch(branch);
        return request;
    }
    
    public DiscoveryMethod getMethod() { return method; }
    public void setMethod(DiscoveryMethod method) { this.method = method; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }
    
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getSceneType() { return sceneType; }
    public void setSceneType(String sceneType) { this.sceneType = sceneType; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public boolean isUseIndexFirst() { return useIndexFirst; }
    public void setUseIndexFirst(boolean useIndexFirst) { this.useIndexFirst = useIndexFirst; }
    
    public Map<String, Object> getOptions() { return options; }
    public void setOptions(Map<String, Object> options) { this.options = options; }
    
    public DiscoveryRequest addOption(String key, Object value) {
        this.options.put(key, value);
        return this;
    }
    
    public String[] parseRepoUrl() {
        if (repoUrl == null || repoUrl.isEmpty()) {
            return null;
        }
        try {
            String url = repoUrl.replaceAll("(https?://)?(github\\.com|gitee\\.com)/", "");
            String[] parts = url.split("/");
            if (parts.length >= 2) {
                return new String[]{parts[0], parts[1].replaceAll("\\.git$", "")};
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
