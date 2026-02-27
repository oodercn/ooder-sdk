package net.ooder.scene.core;

import java.util.*;

/**
 * Skill分类定义
 *
 * <p>基于skills-category-proposal.md的分类方案</p>
 */
public class SkillCategory {

    public static final String SYSTEM = "system";
    public static final String PRODUCTIVITY = "productivity";
    public static final String DEVELOPMENT = "development";
    public static final String DATA = "data";
    public static final String INTEGRATION = "integration";
    public static final String COMMUNICATION = "communication";
    public static final String MEDIA = "media";
    public static final String SECURITY = "security";

    private static final Map<String, CategoryInfo> CATEGORIES = new LinkedHashMap<>();

    static {
        CATEGORIES.put(SYSTEM, new CategoryInfo(SYSTEM, "系统工具", "system", 1, Arrays.asList(
            new SubCategoryInfo("file", "文件管理", 1),
            new SubCategoryInfo("process", "进程管理", 2),
            new SubCategoryInfo("monitor", "系统监控", 3),
            new SubCategoryInfo("network", "网络工具", 4),
            new SubCategoryInfo("backup", "备份恢复", 5)
        )));
        
        CATEGORIES.put(PRODUCTIVITY, new CategoryInfo(PRODUCTIVITY, "生产力工具", "productivity", 2, Arrays.asList(
            new SubCategoryInfo("document", "文档处理", 1),
            new SubCategoryInfo("note", "笔记管理", 2),
            new SubCategoryInfo("task", "任务管理", 3),
            new SubCategoryInfo("calendar", "日历日程", 4),
            new SubCategoryInfo("automation", "自动化", 5)
        )));
        
        CATEGORIES.put(DEVELOPMENT, new CategoryInfo(DEVELOPMENT, "开发工具", "development", 3, Arrays.asList(
            new SubCategoryInfo("code", "代码工具", 1),
            new SubCategoryInfo("git", "版本控制", 2),
            new SubCategoryInfo("build", "构建部署", 3),
            new SubCategoryInfo("test", "测试工具", 4),
            new SubCategoryInfo("debug", "调试工具", 5)
        )));
        
        CATEGORIES.put(DATA, new CategoryInfo(DATA, "数据处理", "data", 4, Arrays.asList(
            new SubCategoryInfo("database", "数据库", 1),
            new SubCategoryInfo("etl", "数据转换", 2),
            new SubCategoryInfo("analysis", "数据分析", 3),
            new SubCategoryInfo("visualization", "数据可视化", 4),
            new SubCategoryInfo("ml", "机器学习", 5)
        )));
        
        CATEGORIES.put(INTEGRATION, new CategoryInfo(INTEGRATION, "集成服务", "integration", 5, Arrays.asList(
            new SubCategoryInfo("api", "API集成", 1),
            new SubCategoryInfo("webhook", "Webhook", 2),
            new SubCategoryInfo("mcp", "MCP服务", 3),
            new SubCategoryInfo("workflow", "工作流", 4),
            new SubCategoryInfo("connector", "连接器", 5)
        )));
        
        CATEGORIES.put(COMMUNICATION, new CategoryInfo(COMMUNICATION, "通讯协作", "communication", 6, Arrays.asList(
            new SubCategoryInfo("chat", "聊天工具", 1),
            new SubCategoryInfo("email", "邮件服务", 2),
            new SubCategoryInfo("meeting", "会议协作", 3),
            new SubCategoryInfo("notification", "通知服务", 4),
            new SubCategoryInfo("social", "社交集成", 5)
        )));
        
        CATEGORIES.put(MEDIA, new CategoryInfo(MEDIA, "媒体处理", "media", 7, Arrays.asList(
            new SubCategoryInfo("image", "图像处理", 1),
            new SubCategoryInfo("audio", "音频处理", 2),
            new SubCategoryInfo("video", "视频处理", 3),
            new SubCategoryInfo("ocr", "OCR识别", 4),
            new SubCategoryInfo("convert", "格式转换", 5)
        )));
        
        CATEGORIES.put(SECURITY, new CategoryInfo(SECURITY, "安全工具", "security", 8, Arrays.asList(
            new SubCategoryInfo("auth", "认证授权", 1),
            new SubCategoryInfo("encrypt", "加密解密", 2),
            new SubCategoryInfo("scan", "安全扫描", 3),
            new SubCategoryInfo("audit", "安全审计", 4),
            new SubCategoryInfo("firewall", "防火墙", 5)
        )));
    }

    public static Map<String, CategoryInfo> getAllCategories() {
        return Collections.unmodifiableMap(CATEGORIES);
    }

    public static CategoryInfo getCategory(String categoryId) {
        return CATEGORIES.get(categoryId);
    }

    public static List<CategoryInfo> getCategoryList() {
        return new ArrayList<>(CATEGORIES.values());
    }

    public static boolean isValidCategory(String categoryId) {
        return CATEGORIES.containsKey(categoryId);
    }

    public static boolean isValidSubCategory(String categoryId, String subCategoryId) {
        CategoryInfo category = CATEGORIES.get(categoryId);
        if (category == null) return false;
        return category.getSubCategories().stream()
            .anyMatch(sub -> sub.getSubCategoryId().equals(subCategoryId));
    }

    /**
     * 分类信息
     */
    public static class CategoryInfo {
        private String categoryId;
        private String categoryName;
        private String categoryIcon;
        private int sort;
        private List<SubCategoryInfo> subCategories;

        public CategoryInfo(String categoryId, String categoryName, String categoryIcon, int sort, List<SubCategoryInfo> subCategories) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.categoryIcon = categoryIcon;
            this.sort = sort;
            this.subCategories = subCategories;
        }

        public String getCategoryId() { return categoryId; }
        public String getCategoryName() { return categoryName; }
        public String getCategoryIcon() { return categoryIcon; }
        public int getSort() { return sort; }
        public List<SubCategoryInfo> getSubCategories() { return subCategories; }
    }

    /**
     * 子分类信息
     */
    public static class SubCategoryInfo {
        private String subCategoryId;
        private String subCategoryName;
        private int sort;

        public SubCategoryInfo(String subCategoryId, String subCategoryName, int sort) {
            this.subCategoryId = subCategoryId;
            this.subCategoryName = subCategoryName;
            this.sort = sort;
        }

        public String getSubCategoryId() { return subCategoryId; }
        public String getSubCategoryName() { return subCategoryName; }
        public int getSort() { return sort; }
    }
}
