package net.ooder.scene.skill.prompt;

import java.util.Arrays;
import java.util.List;

/**
 * 提示词加载选项
 *
 * <p>定义提示词加载的来源优先级和配置</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class PromptLoadOptions {

    private List<PromptSource> sources;
    private boolean enableCache;
    private boolean enableFallback;
    private int maxLength;

    public PromptLoadOptions() {
        this.sources = Arrays.asList(
            PromptSource.CONFIG,
            PromptSource.FILE_MD,
            PromptSource.FILE_YAML,
            PromptSource.RAG,
            PromptSource.DEFAULT
        );
        this.enableCache = true;
        this.enableFallback = true;
        this.maxLength = 8192;
    }

    public static PromptLoadOptions defaultOptions() {
        return new PromptLoadOptions();
    }

    public static PromptLoadOptions ragFirst() {
        PromptLoadOptions options = new PromptLoadOptions();
        options.sources = Arrays.asList(
            PromptSource.RAG,
            PromptSource.CONFIG,
            PromptSource.FILE_MD,
            PromptSource.DEFAULT
        );
        return options;
    }

    public static PromptLoadOptions configOnly() {
        PromptLoadOptions options = new PromptLoadOptions();
        options.sources = Arrays.asList(
            PromptSource.CONFIG,
            PromptSource.DEFAULT
        );
        return options;
    }

    public List<PromptSource> getSources() {
        return sources;
    }

    public void setSources(List<PromptSource> sources) {
        this.sources = sources;
    }

    public boolean isEnableCache() {
        return enableCache;
    }

    public void setEnableCache(boolean enableCache) {
        this.enableCache = enableCache;
    }

    public boolean isEnableFallback() {
        return enableFallback;
    }

    public void setEnableFallback(boolean enableFallback) {
        this.enableFallback = enableFallback;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * 提示词来源类型
     */
    public enum PromptSource {
        CONFIG("config", "skill.yaml 中的 systemPrompt 字段"),
        FILE_MD("file_md", "prompts/system.md 文件"),
        FILE_YAML("file_yaml", "skill.yaml 中的 systemPromptFile 字段"),
        RAG("rag", "从知识库 RAG 检索"),
        DEFAULT("default", "默认提示词");

        private final String code;
        private final String description;

        PromptSource(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}
