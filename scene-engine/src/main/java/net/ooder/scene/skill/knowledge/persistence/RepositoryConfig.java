package net.ooder.scene.skill.knowledge.persistence;

/**
 * 知识库仓库配置
 *
 * @author ooder
 * @since 2.3
 */
public class RepositoryConfig {

    public static final String TYPE_JSON = "json";
    public static final String TYPE_MEMORY = "memory";
    public static final String TYPE_SQL = "sql";

    private String type = TYPE_JSON;
    private String basePath;
    private boolean autoSave = true;
    private long saveIntervalMs = 5000;

    public RepositoryConfig() {
        this.basePath = System.getProperty("user.home") + "/.ooder/data/knowledge";
    }

    public RepositoryConfig(String type, String basePath) {
        this.type = type;
        this.basePath = basePath;
    }

    public static RepositoryConfig jsonFile(String basePath) {
        return new RepositoryConfig(TYPE_JSON, basePath);
    }

    public static RepositoryConfig inMemory() {
        RepositoryConfig config = new RepositoryConfig(TYPE_MEMORY, null);
        return config;
    }

    public static RepositoryConfig sql(String basePath) {
        return new RepositoryConfig(TYPE_SQL, basePath);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public boolean isAutoSave() {
        return autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public long getSaveIntervalMs() {
        return saveIntervalMs;
    }

    public void setSaveIntervalMs(long saveIntervalMs) {
        this.saveIntervalMs = saveIntervalMs;
    }
}
