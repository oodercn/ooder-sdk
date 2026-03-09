package net.ooder.scene.skill.knowledge;

import java.util.List;

/**
 * 文档分块服务接口
 *
 * <p>负责将文档内容分割成适合向量化的片段。</p>
 *
 * <p>分块策略：</p>
 * <ul>
 *   <li>固定大小分块 - 按字符数分割</li>
 *   <li>句子分块 - 按句子边界分割</li>
 *   <li>段落分块 - 按段落分割</li>
 *   <li>语义分块 - 按语义边界分割</li>
 * </ul>
 *
 * @author ooder
 * @since 2.3
 */
public interface DocumentChunker {
    
    /**
     * 分块文档
     *
     * @param document 文档
     * @param chunkSize 分块大小
     * @param overlap 重叠大小
     * @return 分块列表
     */
    List<DocumentChunk> chunk(Document document, int chunkSize, int overlap);
    
    /**
     * 分块文本
     *
     * @param text 文本内容
     * @param chunkSize 分块大小
     * @param overlap 重叠大小
     * @return 分块列表
     */
    List<String> chunkText(String text, int chunkSize, int overlap);
    
    /**
     * 分块文档（使用知识库配置）
     *
     * @param document 文档
     * @param kb 知识库配置
     * @return 分块列表
     */
    List<DocumentChunk> chunk(Document document, KnowledgeBase kb);
    
    /**
     * 获取分块策略名称
     */
    String getStrategyName();
}
