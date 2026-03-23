package net.ooder.scene.skill.prompt.rag.impl;

import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.knowledge.KnowledgeBaseCreateRequest;
import net.ooder.scene.skill.knowledge.KnowledgeSearchRequest;
import net.ooder.scene.skill.knowledge.KnowledgeSearchResult;
import net.ooder.scene.skill.prompt.model.PromptDocument;
import net.ooder.scene.skill.prompt.model.PromptFragment;
import net.ooder.scene.skill.prompt.rag.SkillPromptRagProvider;
import net.ooder.scene.skill.vector.SceneEmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 技能提示词 RAG 提供者实现
 *
 * <p>基于知识库的提示词检索和存储</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class SkillPromptRagProviderImpl implements SkillPromptRagProvider {

    private static final Logger log = LoggerFactory.getLogger(SkillPromptRagProviderImpl.class);

    private static final String DEFAULT_PROMPT_KB_ID = "kb-skill-prompts";

    private final KnowledgeBaseService knowledgeBaseService;
    private final SceneEmbeddingService embeddingService;
    private final String promptKbId;
    private float threshold = 0.6f;
    private int topK = 5;

    private final Map<String, Boolean> indexedSkills = new ConcurrentHashMap<>();
    private final Map<String, Map<String, PromptDocument>> promptCache = new ConcurrentHashMap<>();

    public SkillPromptRagProviderImpl(KnowledgeBaseService knowledgeBaseService,
                                       SceneEmbeddingService embeddingService) {
        this(knowledgeBaseService, embeddingService, DEFAULT_PROMPT_KB_ID);
    }

    public SkillPromptRagProviderImpl(KnowledgeBaseService knowledgeBaseService,
                                       SceneEmbeddingService embeddingService,
                                       String promptKbId) {
        this.knowledgeBaseService = knowledgeBaseService;
        this.embeddingService = embeddingService;
        this.promptKbId = promptKbId != null ? promptKbId : DEFAULT_PROMPT_KB_ID;
    }

    @Override
    public int indexPromptDocuments(String skillId, List<PromptDocument> documents) {
        if (skillId == null || documents == null || documents.isEmpty()) {
            return 0;
        }

        log.info("Indexing {} prompt documents for skill: {}", documents.size(), skillId);

        ensureKnowledgeBaseExists();

        int indexedCount = 0;
        for (PromptDocument doc : documents) {
            if (doc.getContent() == null || doc.getContent().isEmpty()) {
                continue;
            }

            try {
                cachePromptDocument(skillId, doc);
                indexedCount++;
            } catch (Exception e) {
                log.warn("Failed to index prompt document for skill: {}", skillId, e);
            }
        }

        if (indexedCount > 0) {
            indexedSkills.put(skillId, true);
            log.info("Indexed {} prompt documents for skill: {}", indexedCount, skillId);
        }

        return indexedCount;
    }

    @Override
    public String retrieveSystemPrompt(String skillId, String context) {
        List<PromptFragment> fragments = searchPrompts(skillId, 
            buildSearchQuery("system", context), topK);

        if (fragments.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (PromptFragment fragment : fragments) {
            if (fragment.getScore() >= threshold) {
                if (sb.length() > 0) {
                    sb.append("\n\n");
                }
                sb.append(fragment.getContent());
            }
        }

        return sb.length() > 0 ? sb.toString() : null;
    }

    @Override
    public String retrieveRolePrompt(String skillId, String roleId, String context) {
        List<PromptFragment> fragments = searchPrompts(skillId, 
            buildSearchQuery("role:" + roleId, context), topK);

        if (fragments.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (PromptFragment fragment : fragments) {
            if (fragment.getScore() >= threshold && 
                roleId != null && roleId.equals(fragment.getRoleId())) {
                if (sb.length() > 0) {
                    sb.append("\n\n");
                }
                sb.append(fragment.getContent());
            }
        }

        return sb.length() > 0 ? sb.toString() : null;
    }

    @Override
    public List<PromptFragment> searchPrompts(String skillId, String query, int maxResults) {
        List<PromptFragment> results = new ArrayList<>();

        if (!isPromptIndexed(skillId)) {
            Map<String, PromptDocument> cached = promptCache.get(skillId);
            if (cached != null) {
                for (PromptDocument doc : cached.values()) {
                    PromptFragment fragment = PromptFragment.builder()
                        .fragmentId(doc.getDocId())
                        .content(doc.getContent())
                        .score(1.0f)
                        .type(doc.getType())
                        .skillId(skillId)
                        .roleId(doc.getRoleId())
                        .build();
                    results.add(fragment);
                }
            }
            return results;
        }

        try {
            KnowledgeSearchRequest request = new KnowledgeSearchRequest();
            request.setQuery(query);
            request.setTopK(maxResults);

            List<KnowledgeSearchResult> searchResults = knowledgeBaseService.search(promptKbId, request);

            for (KnowledgeSearchResult result : searchResults) {
                PromptFragment fragment = convertToFragment(result);
                if (fragment != null) {
                    results.add(fragment);
                }
            }

            results.sort(Comparator.comparing(PromptFragment::getScore).reversed());
            if (results.size() > maxResults) {
                results = new ArrayList<>(results.subList(0, maxResults));
            }

        } catch (Exception e) {
            log.error("Failed to search prompts for skill: {}", skillId, e);
        }

        return results;
    }

    @Override
    public String hybridRetrieve(String skillId, String staticPrompt, String context, Map<String, Object> options) {
        String ragPrompt = retrieveSystemPrompt(skillId, context);
        
        if (ragPrompt != null && !ragPrompt.isEmpty()) {
            if (staticPrompt != null && !staticPrompt.isEmpty()) {
                return staticPrompt + "\n\n" + ragPrompt;
            }
            return ragPrompt;
        }
        
        return staticPrompt;
    }

    @Override
    public int deletePromptIndex(String skillId) {
        if (skillId == null) {
            return 0;
        }

        try {
            indexedSkills.remove(skillId);
            Map<String, PromptDocument> removed = promptCache.remove(skillId);
            log.info("Deleted prompt index for skill: {}", skillId);
            return removed != null ? removed.size() : 0;
        } catch (Exception e) {
            log.error("Failed to delete prompt index for skill: {}", skillId, e);
            return 0;
        }
    }

    @Override
    public boolean isPromptIndexed(String skillId) {
        return indexedSkills.getOrDefault(skillId, false);
    }

    @Override
    public Map<String, Object> getPromptIndexStats(String skillId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("skillId", skillId);
        stats.put("indexed", isPromptIndexed(skillId));
        
        Map<String, PromptDocument> cached = promptCache.get(skillId);
        if (cached != null) {
            stats.put("cachedDocuments", cached.size());
        }
        
        return stats;
    }

    private void ensureKnowledgeBaseExists() {
        if (knowledgeBaseService != null && !knowledgeBaseService.exists(promptKbId)) {
            try {
                KnowledgeBaseCreateRequest request = new KnowledgeBaseCreateRequest();
                request.setName(promptKbId);
                request.setDescription("技能提示语知识库");
                knowledgeBaseService.create(request);
                log.info("Created prompt knowledge base: {}", promptKbId);
            } catch (Exception e) {
                log.warn("Knowledge base may already exist: {}", promptKbId);
            }
        }
    }

    private String buildSearchQuery(String type, String context) {
        if (context == null || context.isEmpty()) {
            return type;
        }
        return type + " " + context;
    }

    private void cachePromptDocument(String skillId, PromptDocument doc) {
        Map<String, PromptDocument> cache = promptCache.computeIfAbsent(
            skillId, k -> new ConcurrentHashMap<>()
        );
        String key = doc.getType().getCode() + ":" + (doc.getRoleId() != null ? doc.getRoleId() : "default");
        cache.put(key, doc);
    }

    private PromptFragment convertToFragment(KnowledgeSearchResult result) {
        if (result == null) {
            return null;
        }

        Map<String, Object> metadata = result.getMetadata();
        String promptTypeStr = metadata != null ? (String) metadata.get("promptType") : null;
        PromptDocument.PromptType type = null;
        if (promptTypeStr != null) {
            for (PromptDocument.PromptType t : PromptDocument.PromptType.values()) {
                if (t.getCode().equals(promptTypeStr)) {
                    type = t;
                    break;
                }
            }
        }

        return PromptFragment.builder()
            .fragmentId(result.getDocId())
            .content(result.getContent())
            .score(result.getScore())
            .type(type)
            .skillId(metadata != null ? (String) metadata.get("skillId") : null)
            .roleId(metadata != null ? (String) metadata.get("roleId") : null)
            .metadata(metadata)
            .build();
    }

    public void setThreshold(float threshold) {
        this.threshold = Math.max(0.0f, Math.min(1.0f, threshold));
    }

    public void setTopK(int topK) {
        this.topK = Math.max(1, topK);
    }
}
