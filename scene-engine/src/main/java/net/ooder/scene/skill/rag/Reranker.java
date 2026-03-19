package net.ooder.scene.skill.rag;

import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.vector.SceneEmbeddingService;
import net.ooder.scene.skill.vector.VectorStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * RAG 重排序器
 * 
 * <p>对检索结果进行二次排序，提升检索质量</p>
 * 
 * <h3>重排序策略：</h3>
 * <ul>
 *   <li>MMR（最大边际相关性）- 平衡相关性和多样性</li>
 *   <li>Cross-Encoder - 使用交叉编码器精确打分</li>
 *   <li>LLM Rerank - 使用 LLM 进行相关性判断</li>
 *   <li>Hybrid - 混合多种策略</li>
 * </ul>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class Reranker {
    
    private static final Logger log = LoggerFactory.getLogger(Reranker.class);
    
    private final SceneEmbeddingService embeddingService;
    private final LlmGenerator llmGenerator;
    
    private RerankStrategy strategy = RerankStrategy.MMR;
    private double diversityWeight = 0.5;
    private int mmrTopK = 20;
    
    public Reranker(SceneEmbeddingService embeddingService, LlmGenerator llmGenerator) {
        this.embeddingService = embeddingService;
        this.llmGenerator = llmGenerator;
    }
    
    /**
     * 重排序检索结果
     *
     * @param query 原始查询
     * @param chunks 检索到的片段
     * @param topK 返回数量
     * @return 重排序后的结果
     */
    public List<RagResult.RetrievedChunk> rerank(String query, List<RagResult.RetrievedChunk> chunks, int topK) {
        if (chunks == null || chunks.isEmpty()) {
            return chunks;
        }
        
        log.info("Reranking {} chunks using strategy: {}", chunks.size(), strategy);
        
        switch (strategy) {
            case MMR:
                return rerankWithMMR(query, chunks, topK);
            case CROSS_ENCODER:
                return rerankWithCrossEncoder(query, chunks, topK);
            case LLM:
                return rerankWithLLM(query, chunks, topK);
            case HYBRID:
                return rerankHybrid(query, chunks, topK);
            default:
                return chunks.stream()
                    .sorted((a, b) -> Float.compare(b.getScore(), a.getScore()))
                    .limit(topK)
                    .collect(Collectors.toList());
        }
    }
    
    /**
     * MMR（最大边际相关性）重排序
     * 
     * <p>平衡相关性和多样性，避免结果过于单一</p>
     */
    private List<RagResult.RetrievedChunk> rerankWithMMR(String query, List<RagResult.RetrievedChunk> chunks, int topK) {
        if (chunks.size() <= topK) {
            return chunks;
        }
        
        float[] queryVector = embeddingService.embed(query);
        
        List<RagResult.RetrievedChunk> selected = new ArrayList<>();
        List<RagResult.RetrievedChunk> remaining = new ArrayList<>(chunks);
        
        Map<RagResult.RetrievedChunk, float[]> chunkVectors = new HashMap<>();
        for (RagResult.RetrievedChunk chunk : chunks) {
            chunkVectors.put(chunk, embeddingService.embed(chunk.getContent()));
        }
        
        RagResult.RetrievedChunk first = remaining.stream()
            .max(Comparator.comparingDouble(c -> cosineSimilarity(queryVector, chunkVectors.get(c))))
            .orElse(null);
        
        if (first != null) {
            selected.add(first);
            remaining.remove(first);
        }
        
        while (selected.size() < topK && !remaining.isEmpty()) {
            RagResult.RetrievedChunk best = null;
            double bestScore = Double.NEGATIVE_INFINITY;
            
            for (RagResult.RetrievedChunk candidate : remaining) {
                float[] candidateVector = chunkVectors.get(candidate);
                
                double relevance = cosineSimilarity(queryVector, candidateVector);
                
                double maxSimilarity = selected.stream()
                    .mapToDouble(s -> cosineSimilarity(chunkVectors.get(s), candidateVector))
                    .max()
                    .orElse(0);
                
                double mmrScore = diversityWeight * relevance - (1 - diversityWeight) * maxSimilarity;
                
                if (mmrScore > bestScore) {
                    bestScore = mmrScore;
                    best = candidate;
                }
            }
            
            if (best != null) {
                selected.add(best);
                remaining.remove(best);
            } else {
                break;
            }
        }
        
        log.info("MMR rerank completed: selected {} chunks", selected.size());
        return selected;
    }
    
    /**
     * Cross-Encoder 重排序
     * 
     * <p>使用交叉编码器对查询和文档进行精确打分</p>
     */
    private List<RagResult.RetrievedChunk> rerankWithCrossEncoder(String query, List<RagResult.RetrievedChunk> chunks, int topK) {
        List<ScoredChunk> scoredChunks = new ArrayList<>();
        
        for (RagResult.RetrievedChunk chunk : chunks) {
            double crossScore = computeCrossEncoderScore(query, chunk.getContent());
            scoredChunks.add(new ScoredChunk(chunk, crossScore));
        }
        
        scoredChunks.sort((a, b) -> Double.compare(b.score, a.score));
        
        List<RagResult.RetrievedChunk> result = scoredChunks.stream()
            .limit(topK)
            .map(sc -> {
                sc.chunk.setScore((float) sc.score);
                return sc.chunk;
            })
            .collect(Collectors.toList());
        
        log.info("Cross-Encoder rerank completed: selected {} chunks", result.size());
        return result;
    }
    
    /**
     * LLM 重排序
     * 
     * <p>使用 LLM 判断文档与查询的相关性</p>
     */
    private List<RagResult.RetrievedChunk> rerankWithLLM(String query, List<RagResult.RetrievedChunk> chunks, int topK) {
        if (llmGenerator == null) {
            log.warn("LLM Generator not available, falling back to score-based rerank");
            return chunks.stream()
                .sorted((a, b) -> Float.compare(b.getScore(), a.getScore()))
                .limit(topK)
                .collect(Collectors.toList());
        }
        
        List<ScoredChunk> scoredChunks = new ArrayList<>();
        
        for (RagResult.RetrievedChunk chunk : chunks) {
            double llmScore = computeLLMScore(query, chunk);
            scoredChunks.add(new ScoredChunk(chunk, llmScore));
        }
        
        scoredChunks.sort((a, b) -> Double.compare(b.score, a.score));
        
        List<RagResult.RetrievedChunk> result = scoredChunks.stream()
            .limit(topK)
            .map(sc -> {
                sc.chunk.setScore((float) sc.score);
                return sc.chunk;
            })
            .collect(Collectors.toList());
        
        log.info("LLM rerank completed: selected {} chunks", result.size());
        return result;
    }
    
    /**
     * 混合重排序
     * 
     * <p>结合多种策略进行重排序</p>
     */
    private List<RagResult.RetrievedChunk> rerankHybrid(String query, List<RagResult.RetrievedChunk> chunks, int topK) {
        int mmrK = Math.min(mmrTopK, chunks.size());
        List<RagResult.RetrievedChunk> mmrResults = rerankWithMMR(query, chunks, mmrK);
        
        List<RagResult.RetrievedChunk> crossResults = rerankWithCrossEncoder(query, mmrResults, topK);
        
        log.info("Hybrid rerank completed: selected {} chunks", crossResults.size());
        return crossResults;
    }
    
    private double cosineSimilarity(float[] a, float[] b) {
        if (a == null || b == null || a.length != b.length) {
            return 0;
        }
        
        double dotProduct = 0;
        double normA = 0;
        double normB = 0;
        
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        if (normA == 0 || normB == 0) {
            return 0;
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    private double computeCrossEncoderScore(String query, String document) {
        float[] queryVec = embeddingService.embed(query);
        float[] docVec = embeddingService.embed(document);
        
        double similarity = cosineSimilarity(queryVec, docVec);
        
        double lengthPenalty = 1.0 - Math.min(1.0, document.length() / 1000.0 * 0.1);
        
        return similarity * lengthPenalty;
    }
    
    private double computeLLMScore(String query, RagResult.RetrievedChunk chunk) {
        if (llmGenerator == null) {
            return chunk.getScore();
        }
        
        String prompt = String.format(
            "请判断以下文档与查询的相关性，返回0-1之间的分数。\n" +
            "查询：%s\n" +
            "文档：%s\n" +
            "相关性分数：",
            query,
            chunk.getContent().length() > 500 ? chunk.getContent().substring(0, 500) + "..." : chunk.getContent()
        );
        
        try {
            String response = llmGenerator.generate(prompt);
            String scoreStr = response.trim().replaceAll("[^0-9.]", "");
            if (!scoreStr.isEmpty()) {
                double score = Double.parseDouble(scoreStr);
                return Math.max(0, Math.min(1, score));
            }
        } catch (Exception e) {
            log.warn("Failed to compute LLM score", e);
        }
        
        return chunk.getScore();
    }
    
    public void setStrategy(RerankStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void setDiversityWeight(double diversityWeight) {
        this.diversityWeight = diversityWeight;
    }
    
    public void setMmrTopK(int mmrTopK) {
        this.mmrTopK = mmrTopK;
    }
    
    /**
     * 重排序策略
     */
    public enum RerankStrategy {
        MMR,            // 最大边际相关性
        CROSS_ENCODER,  // 交叉编码器
        LLM,            // LLM 重排序
        HYBRID,         // 混合策略
        NONE            // 不重排序
    }
    
    private static class ScoredChunk {
        final RagResult.RetrievedChunk chunk;
        final double score;
        
        ScoredChunk(RagResult.RetrievedChunk chunk, double score) {
            this.chunk = chunk;
            this.score = score;
        }
    }
}
