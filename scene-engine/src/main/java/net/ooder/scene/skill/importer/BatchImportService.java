package net.ooder.scene.skill.importer;

import net.ooder.scene.skill.knowledge.Document;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 批量导入服务接口
 *
 * <p>提供知识库批量导入的完整能力，包括：</p>
 * <ul>
 *   <li>压缩包导入</li>
 *   <li>目录导入</li>
 *   <li>批量URL导入</li>
 *   <li>导入任务管理</li>
 * </ul>
 *
 * <p>架构层次：应用层 - 批量导入</p>
 *
 * @author ooder
 * @since 2.3
 */
public interface BatchImportService {
    
    /**
     * 从压缩包导入
     *
     * @param userId 用户ID
     * @param kbId 知识库ID
     * @param request 压缩包导入请求
     * @return 导入任务
     */
    ImportTask importFromArchive(String userId, String kbId, ArchiveImportRequest request);
    
    /**
     * 批量导入URL
     *
     * @param userId 用户ID
     * @param kbId 知识库ID
     * @param urls URL列表
     * @return 导入任务
     */
    ImportTask importFromUrls(String userId, String kbId, List<String> urls);
    
    /**
     * 获取导入任务状态
     *
     * @param taskId 任务ID
     * @return 任务状态
     */
    ImportTask getTask(String taskId);
    
    /**
     * 取消导入任务
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     */
    void cancelTask(String taskId, String userId);
    
    /**
     * 列出用户的导入任务
     *
     * @param userId 用户ID
     * @return 任务列表
     */
    List<ImportTask> listUserTasks(String userId);
    
    /**
     * 获取导入任务结果
     *
     * @param taskId 任务ID
     * @return 导入结果
     */
    ImportResult getResult(String taskId);
}
