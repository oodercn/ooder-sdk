package net.ooder.scene.llm.config.skillsmd;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * SKILLS.MD 文档模型
 *
 * @author ooder
 * @since 2.4
 */
public class SkillsMdDocument {

    private Path sourcePath;
    private String name;
    private String version;
    private String author;
    private String updateDate;
    private String overview;
    private List<CapabilityDefinition> capabilities = new ArrayList<>();
    private List<ScenarioDefinition> scenarios = new ArrayList<>();
    private List<String> knowledgePaths = new ArrayList<>();
    private String suggestedModel;
    private Double suggestedTemperature;
    private Integer suggestedMaxTokens;
    private String suggestedSystemPrompt;
    private String suggestedUserPrompt;

    public Path getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(Path sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<CapabilityDefinition> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<CapabilityDefinition> capabilities) {
        this.capabilities = capabilities;
    }

    public void addCapability(CapabilityDefinition capability) {
        this.capabilities.add(capability);
    }

    public List<ScenarioDefinition> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<ScenarioDefinition> scenarios) {
        this.scenarios = scenarios;
    }

    public void addScenario(ScenarioDefinition scenario) {
        this.scenarios.add(scenario);
    }

    public List<String> getKnowledgePaths() {
        return knowledgePaths;
    }

    public void setKnowledgePaths(List<String> knowledgePaths) {
        this.knowledgePaths = knowledgePaths;
    }

    public void addKnowledgePath(String path) {
        this.knowledgePaths.add(path);
    }

    public String getSuggestedModel() {
        return suggestedModel;
    }

    public void setSuggestedModel(String suggestedModel) {
        this.suggestedModel = suggestedModel;
    }

    public Double getSuggestedTemperature() {
        return suggestedTemperature;
    }

    public void setSuggestedTemperature(Double suggestedTemperature) {
        this.suggestedTemperature = suggestedTemperature;
    }

    public Integer getSuggestedMaxTokens() {
        return suggestedMaxTokens;
    }

    public void setSuggestedMaxTokens(Integer suggestedMaxTokens) {
        this.suggestedMaxTokens = suggestedMaxTokens;
    }

    public String getSuggestedSystemPrompt() {
        return suggestedSystemPrompt;
    }

    public void setSuggestedSystemPrompt(String suggestedSystemPrompt) {
        this.suggestedSystemPrompt = suggestedSystemPrompt;
    }

    public String getSuggestedUserPrompt() {
        return suggestedUserPrompt;
    }

    public void setSuggestedUserPrompt(String suggestedUserPrompt) {
        this.suggestedUserPrompt = suggestedUserPrompt;
    }

    public boolean hasCapabilities() {
        return capabilities != null && !capabilities.isEmpty();
    }

    public boolean hasKnowledgePaths() {
        return knowledgePaths != null && !knowledgePaths.isEmpty();
    }

    public boolean hasConfigSuggestions() {
        return suggestedModel != null || suggestedTemperature != null || suggestedMaxTokens != null;
    }
}
