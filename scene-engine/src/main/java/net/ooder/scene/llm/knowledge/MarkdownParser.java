package net.ooder.scene.llm.knowledge;

import net.ooder.scene.llm.context.KnowledgeContext.KnowledgeChunk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown 解析器
 * 
 * <p>解析 Markdown 格式的知识文档，支持标题层级、代码块、列表等。</p>
 * <p>支持智能分块，根据标题层级和内容长度进行分块。</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class MarkdownParser {

    private static final Logger log = LoggerFactory.getLogger(MarkdownParser.class);

    // 最大块大小（字符数）
    private static final int MAX_CHUNK_SIZE = 2000;
    // 最小块大小
    private static final int MIN_CHUNK_SIZE = 200;

    // Markdown 标题正则
    private static final Pattern HEADER_PATTERN = Pattern.compile("^(#{1,6})\\s+(.+)$");
    // 代码块开始/结束
    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("^```(\\w*)$");

    /**
     * 解析 Markdown 内容
     *
     * @param content Markdown 内容
     * @param source  来源标识
     * @return 知识块列表
     */
    public List<KnowledgeChunk> parse(String content, String source) {
        List<KnowledgeChunk> chunks = new ArrayList<>();

        if (content == null || content.trim().isEmpty()) {
            return chunks;
        }

        // 1. 预处理：标准化换行符
        content = normalizeLineEndings(content);

        // 2. 按行解析
        String[] lines = content.split("\n");

        // 3. 提取元数据（YAML Front Matter）
        int contentStart = extractFrontMatter(lines, chunks, source);

        // 4. 按标题分块
        List<Section> sections = splitByHeaders(lines, contentStart);

        // 5. 将分块转换为 KnowledgeChunk
        for (Section section : sections) {
            KnowledgeChunk chunk = createChunkFromSection(section, source);
            if (chunk != null) {
                chunks.add(chunk);
            }
        }

        log.debug("Markdown parsed: source={}, lines={}, chunks={}",
                source, lines.length, chunks.size());

        return chunks;
    }

    /**
     * 标准化换行符
     */
    private String normalizeLineEndings(String content) {
        return content.replace("\r\n", "\n").replace("\r", "\n");
    }

    /**
     * 提取 YAML Front Matter 元数据
     */
    private int extractFrontMatter(String[] lines, List<KnowledgeChunk> chunks, String source) {
        if (lines.length < 2 || !lines[0].trim().equals("---")) {
            return 0;
        }

        StringBuilder frontMatter = new StringBuilder();
        int i = 1;
        while (i < lines.length && !lines[i].trim().equals("---")) {
            frontMatter.append(lines[i]).append("\n");
            i++;
        }

        if (i < lines.length && lines[i].trim().equals("---")) {
            // 创建元数据块
            KnowledgeChunk chunk = new KnowledgeChunk();
            chunk.setId(generateChunkId());
            chunk.setContent("## 文档元数据\n\n" + frontMatter.toString());
            chunk.setSource(source);
            chunk.setMetadata(parseYamlFrontMatter(frontMatter.toString()));
            chunks.add(chunk);
            return i + 1;
        }

        return 0;
    }

    /**
     * 解析 YAML Front Matter
     */
    private Map<String, Object> parseYamlFrontMatter(String yaml) {
        Map<String, Object> metadata = new HashMap<>();
        String[] lines = yaml.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                // 移除引号
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                metadata.put(key, value);
            }
        }

        return metadata;
    }

    /**
     * 按标题分块
     */
    private List<Section> splitByHeaders(String[] lines, int startIndex) {
        List<Section> sections = new ArrayList<>();
        Section currentSection = null;
        boolean inCodeBlock = false;
        StringBuilder codeBuffer = new StringBuilder();

        for (int i = startIndex; i < lines.length; i++) {
            String line = lines[i];

            // 检查代码块
            Matcher codeMatcher = CODE_BLOCK_PATTERN.matcher(line.trim());
            if (codeMatcher.matches()) {
                if (inCodeBlock) {
                    // 代码块结束
                    codeBuffer.append(line).append("\n");
                    if (currentSection != null) {
                        currentSection.appendContent(codeBuffer.toString());
                    }
                    codeBuffer.setLength(0);
                    inCodeBlock = false;
                } else {
                    // 代码块开始
                    inCodeBlock = true;
                    codeBuffer.append(line).append("\n");
                }
                continue;
            }

            if (inCodeBlock) {
                codeBuffer.append(line).append("\n");
                continue;
            }

            // 检查标题
            Matcher headerMatcher = HEADER_PATTERN.matcher(line);
            if (headerMatcher.matches()) {
                // 保存当前块
                if (currentSection != null && currentSection.getContentLength() > MIN_CHUNK_SIZE) {
                    sections.add(currentSection);
                }

                // 创建新块
                int level = headerMatcher.group(1).length();
                String title = headerMatcher.group(2);
                currentSection = new Section(level, title);
            }

            // 添加到当前块
            if (currentSection != null) {
                currentSection.appendContent(line + "\n");

                // 如果块太大，分割
                if (currentSection.getContentLength() > MAX_CHUNK_SIZE) {
                    sections.add(currentSection);
                    currentSection = new Section(currentSection.getLevel(), currentSection.getTitle() + " (续)");
                }
            } else {
                // 没有标题的内容，创建默认块
                currentSection = new Section(0, "概述");
                currentSection.appendContent(line + "\n");
            }
        }

        // 添加最后一个块
        if (currentSection != null && currentSection.getContentLength() > 0) {
            sections.add(currentSection);
        }

        return sections;
    }

    /**
     * 从分块创建 KnowledgeChunk
     */
    private KnowledgeChunk createChunkFromSection(Section section, String source) {
        String content = section.getContent().trim();
        if (content.isEmpty()) {
            return null;
        }

        KnowledgeChunk chunk = new KnowledgeChunk();
        chunk.setId(generateChunkId());
        chunk.setContent(content);
        chunk.setSource(source);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("headerLevel", section.getLevel());
        metadata.put("headerTitle", section.getTitle());
        chunk.setMetadata(metadata);

        return chunk;
    }

    /**
     * 生成块 ID
     */
    private String generateChunkId() {
        return "chunk-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 内部分块类
     */
    private static class Section {
        private final int level;
        private final String title;
        private final StringBuilder content = new StringBuilder();

        public Section(int level, String title) {
            this.level = level;
            this.title = title;
        }

        public void appendContent(String text) {
            content.append(text);
        }

        public int getLevel() {
            return level;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content.toString();
        }

        public int getContentLength() {
            return content.length();
        }
    }
}
