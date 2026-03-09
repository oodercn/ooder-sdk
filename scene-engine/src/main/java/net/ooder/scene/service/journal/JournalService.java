package net.ooder.scene.service.journal;

import java.util.Date;
import java.util.List;

/**
 * 日志撰写服务接口
 *
 * <p>提供日志撰写能力，支持草稿、提交、自动汇总等功能。</p>
 *
 * <h3>使用场景：</h3>
 * <ul>
 *   <li>用户手工撰写日志</li>
 *   <li>自动汇总生成日志</li>
 *   <li>日志提交和审核</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface JournalService {

    /**
     * 创建草稿
     *
     * @param sceneId 场景ID
     * @param userId 用户ID
     * @return 日志草稿
     */
    JournalDraft createDraft(String sceneId, String userId);

    /**
     * 从模板创建草稿
     *
     * @param sceneId 场景ID
     * @param userId 用户ID
     * @param templateId 模板ID
     * @return 日志草稿
     */
    JournalDraft createDraftFromTemplate(String sceneId, String userId, String templateId);

    /**
     * 保存草稿
     *
     * @param draftId 草稿ID
     * @param content 内容
     */
    void saveDraft(String draftId, String content);

    /**
     * 获取草稿
     *
     * @param draftId 草稿ID
     * @return 日志草稿
     */
    JournalDraft getDraft(String draftId);

    /**
     * 获取用户的草稿列表
     *
     * @param userId 用户ID
     * @return 草稿列表
     */
    List<JournalDraft> getUserDrafts(String userId);

    /**
     * 删除草稿
     *
     * @param draftId 草稿ID
     * @return 是否成功
     */
    boolean deleteDraft(String draftId);

    /**
     * 提交日志
     *
     * @param request 提交请求
     * @return 日志条目
     */
    JournalEntry submitJournal(JournalSubmitRequest request);

    /**
     * 获取用户日志列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 日志列表
     */
    List<JournalEntry> getUserJournals(String userId, int page, int size);

    /**
     * 获取场景日志列表
     *
     * @param sceneId 场景ID
     * @param page 页码
     * @param size 每页数量
     * @return 日志列表
     */
    List<JournalEntry> getSceneJournals(String sceneId, int page, int size);

    /**
     * 获取日志详情
     *
     * @param entryId 日志ID
     * @return 日志条目
     */
    JournalEntry getJournalEntry(String entryId);

    /**
     * 自动汇总生成日志
     *
     * @param sceneId 场景ID
     * @param userId 用户ID
     * @param options 生成选项
     * @return 日志草稿
     */
    JournalDraft autoGenerate(String sceneId, String userId, AutoGenerateOptions options);

    /**
     * 汇总邮件生成日志内容
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 汇总内容
     */
    String summarizeEmails(String userId, Date startTime, Date endTime);

    /**
     * 汇总代码提交生成日志内容
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 汇总内容
     */
    String summarizeGitCommits(String userId, Date startTime, Date endTime);

    /**
     * 获取日志模板列表
     *
     * @param sceneId 场景ID
     * @return 模板列表
     */
    List<JournalTemplate> getTemplates(String sceneId);
}
