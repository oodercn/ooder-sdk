package net.ooder.spi.knowledge;

import lombok.Data;
import java.util.Map;

/**
 * 知识文档
 */
@Data
public class KnowledgeDocument {

    /**
     * 文档ID
     */
    private String docId;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文档内容
     */
    private String content;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 上传用户ID
     */
    private String userId;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 更新时间
     */
    private long updateTime;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 文档状态
     */
    private DocumentStatus status;

    public enum DocumentStatus {
        ACTIVE,
        ARCHIVED,
        DELETED
    }
}
