package net.ooder.sdk.generator;

public class GeneratorOptions {
    private String category;
    private String version;
    private String packageName;
    private String language;
    private boolean generateDriver;
    private boolean generateSkill;
    private boolean generateCapabilities;
    private boolean generateManager;
    private boolean generateFallback;
    private boolean generateModels;
    private boolean generateTests;
    private boolean generateDocs;
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public boolean isGenerateDriver() { return generateDriver; }
    public void setGenerateDriver(boolean generateDriver) { this.generateDriver = generateDriver; }
    
    public boolean isGenerateSkill() { return generateSkill; }
    public void setGenerateSkill(boolean generateSkill) { this.generateSkill = generateSkill; }
    
    public boolean isGenerateCapabilities() { return generateCapabilities; }
    public void setGenerateCapabilities(boolean generateCapabilities) { this.generateCapabilities = generateCapabilities; }
    
    public boolean isGenerateManager() { return generateManager; }
    public void setGenerateManager(boolean generateManager) { this.generateManager = generateManager; }
    
    public boolean isGenerateFallback() { return generateFallback; }
    public void setGenerateFallback(boolean generateFallback) { this.generateFallback = generateFallback; }
    
    public boolean isGenerateModels() { return generateModels; }
    public void setGenerateModels(boolean generateModels) { this.generateModels = generateModels; }
    
    public boolean isGenerateTests() { return generateTests; }
    public void setGenerateTests(boolean generateTests) { this.generateTests = generateTests; }
    
    public boolean isGenerateDocs() { return generateDocs; }
    public void setGenerateDocs(boolean generateDocs) { this.generateDocs = generateDocs; }
}