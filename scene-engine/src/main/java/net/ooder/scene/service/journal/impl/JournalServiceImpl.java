package net.ooder.scene.service.journal.impl;

import net.ooder.scene.core.SceneConfig;
import net.ooder.scene.service.journal.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 日志撰写服务实现
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class JournalServiceImpl implements JournalService {

    private final Map<String, JournalDraft> draftStore = new ConcurrentHashMap<>();
    private final Map<String, JournalEntry> entryStore = new ConcurrentHashMap<>();
    private final Map<String, List<JournalEntry>> userEntries = new ConcurrentHashMap<>();
    private final Map<String, List<JournalEntry>> sceneEntries = new ConcurrentHashMap<>();
    private final Map<String, JournalTemplate> templates = new ConcurrentHashMap<>();

    public JournalServiceImpl() {
        initDefaultTemplates();
    }

    private void initDefaultTemplates() {
        JournalTemplate weekly = JournalTemplate.weeklyReport();
        weekly.setTemplateId("tpl-weekly");
        weekly.setSceneId("*");
        templates.put(weekly.getTemplateId(), weekly);

        JournalTemplate daily = JournalTemplate.dailyReport();
        daily.setTemplateId("tpl-daily");
        daily.setSceneId("*");
        templates.put(daily.getTemplateId(), daily);
    }

    @Override
    public JournalDraft createDraft(String sceneId, String userId) {
        String draftId = "draft-" + UUID.randomUUID().toString().substring(0, 8);
        
        JournalDraft draft = new JournalDraft(sceneId, userId);
        draft.setDraftId(draftId);
        draft.setTitle("新日志");
        draft.setContent("");
        
        draftStore.put(draftId, draft);
        return draft;
    }

    @Override
    public JournalDraft createDraftFromTemplate(String sceneId, String userId, String templateId) {
        JournalDraft draft = createDraft(sceneId, userId);
        
        JournalTemplate template = templates.get(templateId);
        if (template != null) {
            draft.setTemplateId(templateId);
            draft.setContent(template.getContentTemplate());
        }
        
        return draft;
    }

    @Override
    public void saveDraft(String draftId, String content) {
        JournalDraft draft = draftStore.get(draftId);
        if (draft != null) {
            draft.updateContent(content);
        }
    }

    @Override
    public JournalDraft getDraft(String draftId) {
        return draftStore.get(draftId);
    }

    @Override
    public List<JournalDraft> getUserDrafts(String userId) {
        return draftStore.values().stream()
            .filter(d -> d.getUserId().equals(userId))
            .filter(d -> d.getStatus() == JournalDraft.DraftStatus.DRAFT)
            .sorted(Comparator.comparing(JournalDraft::getUpdateTime).reversed())
            .collect(Collectors.toList());
    }

    @Override
    public boolean deleteDraft(String draftId) {
        return draftStore.remove(draftId) != null;
    }

    @Override
    public JournalEntry submitJournal(JournalSubmitRequest request) {
        String entryId = "entry-" + UUID.randomUUID().toString().substring(0, 8);
        
        JournalEntry entry = new JournalEntry();
        entry.setEntryId(entryId);
        entry.setSceneId(request.getSceneId());
        entry.setUserId(request.getUserId());
        entry.setTitle(request.getTitle());
        entry.setContent(request.getContent());
        entry.setSubmitTime(new Date());
        
        if (request.isRequireReview()) {
            entry.setStatus(JournalEntry.JournalStatus.UNDER_REVIEW);
        } else {
            entry.setStatus(JournalEntry.JournalStatus.APPROVED);
        }
        
        entryStore.put(entryId, entry);
        
        userEntries.computeIfAbsent(request.getUserId(), k -> new ArrayList<>()).add(entry);
        if (request.getSceneId() != null) {
            sceneEntries.computeIfAbsent(request.getSceneId(), k -> new ArrayList<>()).add(entry);
        }
        
        if (request.getDraftId() != null) {
            JournalDraft draft = draftStore.get(request.getDraftId());
            if (draft != null) {
                draft.setStatus(JournalDraft.DraftStatus.SUBMITTED);
                draft.setSubmitTime(new Date());
            }
        }
        
        return entry;
    }

    @Override
    public List<JournalEntry> getUserJournals(String userId, int page, int size) {
        List<JournalEntry> entries = userEntries.getOrDefault(userId, new ArrayList<>());
        
        int start = page * size;
        int end = Math.min(start + size, entries.size());
        
        if (start >= entries.size()) {
            return new ArrayList<>();
        }
        
        return entries.subList(start, end);
    }

    @Override
    public List<JournalEntry> getSceneJournals(String sceneId, int page, int size) {
        List<JournalEntry> entries = sceneEntries.getOrDefault(sceneId, new ArrayList<>());
        
        int start = page * size;
        int end = Math.min(start + size, entries.size());
        
        if (start >= entries.size()) {
            return new ArrayList<>();
        }
        
        return entries.subList(start, end);
    }

    @Override
    public JournalEntry getJournalEntry(String entryId) {
        return entryStore.get(entryId);
    }

    @Override
    public JournalDraft autoGenerate(String sceneId, String userId, AutoGenerateOptions options) {
        JournalDraft draft = createDraft(sceneId, userId);
        draft.setAutoGenerateSource(determineSource(options));
        
        StringBuilder content = new StringBuilder();
        
        if (options.isIncludeEmail()) {
            String emailSummary = summarizeEmails(userId, options.getStartTime(), options.getEndTime());
            content.append("## 邮件汇总\n\n").append(emailSummary).append("\n\n");
        }
        
        if (options.isIncludeGitCommit()) {
            String gitSummary = summarizeGitCommits(userId, options.getStartTime(), options.getEndTime());
            content.append("## 代码提交汇总\n\n").append(gitSummary).append("\n\n");
        }
        
        draft.setContent(content.toString());
        return draft;
    }

    private JournalDraft.AutoGenerateSource determineSource(AutoGenerateOptions options) {
        if (options.isIncludeEmail() && options.isIncludeGitCommit()) {
            return JournalDraft.AutoGenerateSource.AI_GENERATED;
        } else if (options.isIncludeEmail()) {
            return JournalDraft.AutoGenerateSource.EMAIL_SUMMARY;
        } else if (options.isIncludeGitCommit()) {
            return JournalDraft.AutoGenerateSource.GIT_SUMMARY;
        }
        return JournalDraft.AutoGenerateSource.MANUAL;
    }

    @Override
    public String summarizeEmails(String userId, Date startTime, Date endTime) {
        return "本周共收到 15 封邮件，其中重要邮件 3 封。\n- 项目进度更新\n- 会议邀请\n- 任务分配";
    }

    @Override
    public String summarizeGitCommits(String userId, Date startTime, Date endTime) {
        return "本周共提交 12 次代码，涉及 5 个项目。\n- feat: 新增用户管理功能\n- fix: 修复登录问题\n- refactor: 重构代码结构";
    }

    @Override
    public List<JournalTemplate> getTemplates(String sceneId) {
        return templates.values().stream()
            .filter(t -> t.getSceneId().equals("*") || t.getSceneId().equals(sceneId))
            .filter(JournalTemplate::isEnabled)
            .collect(Collectors.toList());
    }
}
