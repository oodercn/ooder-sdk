package net.ooder.scene.llm.knowledge;

import net.ooder.scene.discovery.UnifiedSkillRegistry;
import net.ooder.scene.llm.context.KnowledgeContext;
import net.ooder.scene.llm.context.KnowledgeContext.KnowledgeChunk;
import net.ooder.scene.llm.context.KnowledgeContext.KnowledgeLoadLevel;
import net.ooder.skills.api.SkillPackage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Skills.md 加载器
 * 
 * <p>负责从 Skill 包中加载 skills.md 文件及其相关知识文件。</p>
 * <p>支持多级加载策略：BASIC、ADVANCED、EXPERT、FULL</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SkillsMdLoader {

    private static final Logger log = LoggerFactory.getLogger(SkillsMdLoader.class);

    // 知识文件路径模板
    private static final String SKILLS_MD = "skills.md";
    private static final String BASIC_KNOWLEDGE = "knowledge/basic.md";
    private static final String ADVANCED_KNOWLEDGE = "knowledge/advanced.md";
    private static final String EXPERT_KNOWLEDGE = "knowledge/expert.md";
    private static final String FULL_KNOWLEDGE = "knowledge/full.md";

    private final UnifiedSkillRegistry skillRegistry;
    private final MarkdownParser markdownParser;

    public SkillsMdLoader(UnifiedSkillRegistry skillRegistry) {
        this.skillRegistry = skillRegistry;
        this.markdownParser = new MarkdownParser();
    }

    /**
     * 加载 Skill 知识库
     * 
     * 根据加载级别自动选择加载哪些知识文件
     *
     * @param skillId Skill ID
     * @param level 知识加载级别
     * @return 知识上下文
     */
    public CompletableFuture<KnowledgeContext> load(String skillId, KnowledgeLoadLevel level) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Loading knowledge for skill: skillId={}, level={}", skillId, level);

            KnowledgeContext context = new KnowledgeContext();
            context.setSkillId(skillId);
            context.setLoadLevel(level);

            try {
                // 获取 Skill 包
                SkillPackage skillPackage = skillRegistry.getSkill(skillId).get();
                if (skillPackage == null) {
                    log.warn("Skill not found: skillId={}", skillId);
                    return context;
                }

                // 1. 从 Skill 描述中提取知识
                extractKnowledgeFromSkill(skillPackage, context);

                // 2. 从元数据中读取 RAG 配置
                setupRagIndex(skillPackage, context);

                log.info("Knowledge loaded successfully: skillId={}, chunks={}",
                        skillId, context.getLoadedChunks().size());

                return context;

            } catch (Exception e) {
                log.error("Failed to load knowledge for skill: skillId={}", skillId, e);
                return context;
            }
        });
    }

    /**
     * 从 Skill 包中提取知识
     */
    private void extractKnowledgeFromSkill(SkillPackage skillPackage, KnowledgeContext context) {
        // 1. 使用 Skill 描述作为基础知识
        String description = skillPackage.getDescription();
        if (description != null && !description.isEmpty()) {
            KnowledgeChunk chunk = new KnowledgeChunk();
            chunk.setId(generateChunkId());
            chunk.setContent("## Skill Overview\n\n" + description);
            chunk.setSource("skill.description");
            context.addChunk(chunk);
        }

        // 2. 从元数据中读取知识文档
        Object knowledgeDocs = skillPackage.getMetadata().get("knowledgeDocuments");
        if (knowledgeDocs instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> docs = (List<String>) knowledgeDocs;
            for (String doc : docs) {
                KnowledgeChunk chunk = new KnowledgeChunk();
                chunk.setId(generateChunkId());
                chunk.setContent(doc);
                chunk.setSource("metadata.knowledgeDocuments");
                context.addChunk(chunk);
            }
        }

        // 3. 从元数据中读取详细知识
        Object detailedKnowledge = skillPackage.getMetadata().get("detailedKnowledge");
        if (detailedKnowledge instanceof String) {
            KnowledgeChunk chunk = new KnowledgeChunk();
            chunk.setId(generateChunkId());
            chunk.setContent((String) detailedKnowledge);
            chunk.setSource("metadata.detailedKnowledge");
            context.addChunk(chunk);
        }
    }

    /**
     * 设置 RAG 索引
     */
    private void setupRagIndex(SkillPackage skillPackage, KnowledgeContext context) {
        // 从 Skill 元数据中读取 RAG 索引 ID
        Object ragConfig = skillPackage.getMetadata().get("ragConfig");
        if (ragConfig instanceof java.util.Map) {
            Object indexId = ((java.util.Map<?, ?>) ragConfig).get("indexId");
            if (indexId != null) {
                context.setRagIndexId(indexId.toString());
                log.debug("RAG index configured: skillId={}, indexId={}",
                        skillPackage.getSkillId(), indexId);
            }
        }
    }

    /**
     * 生成块 ID
     */
    private String generateChunkId() {
        return "chunk-" + java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 异步加载（带超时）
     */
    public CompletableFuture<KnowledgeContext> loadAsync(String skillId, KnowledgeLoadLevel level, long timeoutMs) {
        return load(skillId, level)
                .exceptionally(ex -> {
                    log.error("Knowledge loading failed: skillId={}", skillId, ex);
                    KnowledgeContext context = new KnowledgeContext();
                    context.setSkillId(skillId);
                    context.setLoadLevel(level);
                    return context;
                });
    }

    /**
     * 批量加载多个 Skill
     */
    public CompletableFuture<List<KnowledgeContext>> loadMultiple(List<String> skillIds, KnowledgeLoadLevel level) {
        List<CompletableFuture<KnowledgeContext>> futures = new ArrayList<>();
        for (String skillId : skillIds) {
            futures.add(load(skillId, level));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<KnowledgeContext> contexts = new ArrayList<>();
                    for (CompletableFuture<KnowledgeContext> future : futures) {
                        contexts.add(future.join());
                    }
                    return contexts;
                });
    }
}
