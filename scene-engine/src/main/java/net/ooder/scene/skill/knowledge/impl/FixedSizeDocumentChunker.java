package net.ooder.scene.skill.knowledge.impl;

import net.ooder.scene.skill.knowledge.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 固定大小文档分块器
 *
 * <p>按固定字符数分割文档，支持重叠。</p>
 *
 * @author ooder
 * @since 2.3
 */
public class FixedSizeDocumentChunker implements DocumentChunker {
    
    private static final Logger log = LoggerFactory.getLogger(FixedSizeDocumentChunker.class);
    
    public static final String STRATEGY_NAME = "fixed-size";
    
    private static final int DEFAULT_CHUNK_SIZE = 500;
    private static final int DEFAULT_OVERLAP = 50;
    
    @Override
    public List<DocumentChunk> chunk(Document document, int chunkSize, int overlap) {
        log.debug("Chunking document: {}, chunkSize: {}, overlap: {}", 
            document.getDocId(), chunkSize, overlap);
        
        List<DocumentChunk> chunks = new ArrayList<>();
        String content = document.getContent();
        
        if (content == null || content.isEmpty()) {
            return chunks;
        }
        
        int effectiveChunkSize = chunkSize > 0 ? chunkSize : DEFAULT_CHUNK_SIZE;
        int effectiveOverlap = overlap >= 0 ? overlap : DEFAULT_OVERLAP;
        
        if (effectiveOverlap >= effectiveChunkSize) {
            effectiveOverlap = effectiveChunkSize / 4;
        }
        
        int contentLength = content.length();
        int startIndex = 0;
        int chunkIndex = 0;
        
        while (startIndex < contentLength) {
            int endIndex = Math.min(startIndex + effectiveChunkSize, contentLength);
            
            // 尝试在句子边界分割
            int actualEndIndex = findSentenceBoundary(content, startIndex, endIndex);
            if (actualEndIndex <= startIndex) {
                actualEndIndex = endIndex;
            }
            
            String chunkContent = content.substring(startIndex, actualEndIndex).trim();
            
            if (!chunkContent.isEmpty()) {
                String chunkId = generateChunkId(document.getDocId(), chunkIndex);
                DocumentChunk chunk = new DocumentChunk(
                    chunkId,
                    document.getDocId(),
                    document.getKbId(),
                    chunkIndex,
                    chunkContent
                );
                chunks.add(chunk);
                chunkIndex++;
            }
            
            // 移动到下一个分块起点（考虑重叠）
            startIndex = actualEndIndex - effectiveOverlap;
            if (startIndex <= (chunks.size() > 1 ? chunks.get(chunks.size() - 2).getContent().length() : 0)) {
                startIndex = actualEndIndex;
            }
        }
        
        log.debug("Document {} chunked into {} chunks", document.getDocId(), chunks.size());
        return chunks;
    }
    
    @Override
    public List<String> chunkText(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        
        if (text == null || text.isEmpty()) {
            return chunks;
        }
        
        int effectiveChunkSize = chunkSize > 0 ? chunkSize : DEFAULT_CHUNK_SIZE;
        int effectiveOverlap = overlap >= 0 ? overlap : DEFAULT_OVERLAP;
        
        int textLength = text.length();
        int startIndex = 0;
        
        while (startIndex < textLength) {
            int endIndex = Math.min(startIndex + effectiveChunkSize, textLength);
            
            int actualEndIndex = findSentenceBoundary(text, startIndex, endIndex);
            if (actualEndIndex <= startIndex) {
                actualEndIndex = endIndex;
            }
            
            String chunk = text.substring(startIndex, actualEndIndex).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }
            
            startIndex = actualEndIndex - effectiveOverlap;
            if (startIndex < 0) {
                startIndex = actualEndIndex;
            }
        }
        
        return chunks;
    }
    
    @Override
    public List<DocumentChunk> chunk(Document document, KnowledgeBase kb) {
        int chunkSize = kb.getChunkSize() > 0 ? kb.getChunkSize() : DEFAULT_CHUNK_SIZE;
        int overlap = kb.getChunkOverlap() >= 0 ? kb.getChunkOverlap() : DEFAULT_OVERLAP;
        return chunk(document, chunkSize, overlap);
    }
    
    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }
    
    /**
     * 查找句子边界
     */
    private int findSentenceBoundary(String content, int startIndex, int endIndex) {
        if (endIndex >= content.length()) {
            return endIndex;
        }
        
        // 在 endIndex 附近查找句子结束符
        int searchStart = Math.max(startIndex, endIndex - 100);
        int searchEnd = Math.min(content.length(), endIndex + 100);
        
        String searchText = content.substring(searchStart, searchEnd);
        
        // 句子结束符优先级
        char[] delimiters = {'。', '！', '？', '.', '!', '?', '\n', '；', ';'};
        
        int bestPosition = -1;
        int bestOffset = Integer.MAX_VALUE;
        
        for (char delimiter : delimiters) {
            int pos = searchText.indexOf(delimiter);
            if (pos != -1) {
                int absolutePos = searchStart + pos + 1;
                int offset = Math.abs(absolutePos - endIndex);
                if (offset < bestOffset) {
                    bestOffset = offset;
                    bestPosition = absolutePos;
                }
            }
        }
        
        if (bestPosition > startIndex && bestPosition <= content.length()) {
            return bestPosition;
        }
        
        return endIndex;
    }
    
    /**
     * 生成分块ID
     */
    private String generateChunkId(String docId, int chunkIndex) {
        return docId + "_chunk_" + chunkIndex + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
