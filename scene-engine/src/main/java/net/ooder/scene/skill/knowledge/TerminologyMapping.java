package net.ooder.scene.skill.knowledge;

import java.util.*;

/**
 * 术语映射实体
 * <p>存储术语、别名、缩写、同义词等映射关系</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class TerminologyMapping {

    private String id;
    private String term;                          // 标准术语
    private String definition;                    // 定义
    private String description;                   // 详细描述
    private String category;                      // 分类
    private List<String> aliases;                 // 别名列表
    private List<String> abbreviations;           // 缩写列表
    private List<String> synonyms;                // 同义词列表
    private Map<String, Object> metadata;         // 扩展元数据
    private String source;                        // 来源（知识库ID等）
    private long createdAt;
    private long updatedAt;

    public TerminologyMapping() {
        this.aliases = new ArrayList<>();
        this.abbreviations = new ArrayList<>();
        this.synonyms = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public TerminologyMapping(String term, String definition) {
        this();
        this.term = term;
        this.definition = definition;
    }

    /**
     * 添加别名
     */
    public void addAlias(String alias) {
        if (alias != null && !alias.isEmpty() && !this.aliases.contains(alias)) {
            this.aliases.add(alias);
        }
    }

    /**
     * 添加缩写
     */
    public void addAbbreviation(String abbreviation) {
        if (abbreviation != null && !abbreviation.isEmpty() && !this.abbreviations.contains(abbreviation)) {
            this.abbreviations.add(abbreviation);
        }
    }

    /**
     * 添加同义词
     */
    public void addSynonym(String synonym) {
        if (synonym != null && !synonym.isEmpty() && !this.synonyms.contains(synonym)) {
            this.synonyms.add(synonym);
        }
    }

    /**
     * 获取所有匹配形式（术语 + 别名 + 缩写 + 同义词）
     */
    public List<String> getAllForms() {
        List<String> forms = new ArrayList<>();
        forms.add(term);
        forms.addAll(aliases);
        forms.addAll(abbreviations);
        forms.addAll(synonyms);
        return forms;
    }

    /**
     * 检查是否匹配给定的文本
     */
    public boolean matches(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        String lowerText = text.toLowerCase();
        return getAllForms().stream()
                .anyMatch(form -> form.toLowerCase().equals(lowerText));
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }

    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getAliases() { return aliases; }
    public void setAliases(List<String> aliases) { this.aliases = aliases; }

    public List<String> getAbbreviations() { return abbreviations; }
    public void setAbbreviations(List<String> abbreviations) { this.abbreviations = abbreviations; }

    public List<String> getSynonyms() { return synonyms; }
    public void setSynonyms(List<String> synonyms) { this.synonyms = synonyms; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "TerminologyMapping{" +
                "term='" + term + '\'' +
                ", definition='" + definition + '\'' +
                ", category='" + category + '\'' +
                ", aliases=" + aliases +
                ", abbreviations=" + abbreviations +
                '}';
    }
}
