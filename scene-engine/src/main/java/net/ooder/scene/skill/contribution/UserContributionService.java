package net.ooder.scene.skill.contribution;

import net.ooder.scene.skill.knowledge.Document;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 用户知识贡献服务接口
 *
 * <p>提供用户向知识库贡献知识的完整能力，包括：</p>
 * <ul>
 *   <li>文件上传</li>
 *   <li>文本输入</li>
 *   <li>URL 导入</li>
 *   <li>批量导入</li>
 * </ul>
 *
 * <p>架构层次：应用层 - 用户知识贡献</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface UserContributionService {
    
    /**
     * 上传文件到知识库
     *
     * @param userId 用户ID
     * @param kbId 知识库ID
     * @param request 上传请求
     * @return 创建的文档
     */
    Document uploadFile(String userId, String kbId, FileUploadRequest request);
    
    /**
     * 输入文本知识
     *
     * @param userId 用户ID
     * @param kbId 知识库ID
     * @param request 文本请求
     * @return 创建的文档
     */
    Document inputText(String userId, String kbId, TextKnowledgeRequest request);
    
    /**
     * 从URL导入知识
     *
     * @param userId 用户ID
     * @param kbId 知识库ID
     * @param request URL导入请求
     * @return 创建的文档
     */
    Document importFromUrl(String userId, String kbId, UrlImportRequest request);
    
    /**
     * 批量上传文件
     *
     * @param userId 用户ID
     * @param kbId 知识库ID
     * @param requests 上传请求列表
     * @return 批量导入结果
     */
    BatchImportResult batchUpload(String userId, String kbId, List<FileUploadRequest> requests);
    
    /**
     * 获取用户贡献统计
     *
     * @param userId 用户ID
     * @return 贡献统计
     */
    ContributionStats getStats(String userId);
}
