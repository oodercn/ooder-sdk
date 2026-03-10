package net.ooder.scene.skill.knowledge;

import java.util.List;
import java.util.Map;

/**
 * 文档创建请求
 *
 * @author ooder
 * @since 2.3
 */
public class DocumentCreateRequest {

    /** 文档标题 */
    private String title;

    /** 文档内容 */
    private String content;

    /** 文档类型 */
    private String type;

    /** 文档来源 */
    private String source;

    /** 来源URL */
    private String sourceUrl;

    /** 文件路径 */
    private String filePath;

    /** MIME类型 */
    private String mimeType;

    /** 文件大小 */
    private long fileSize;

    /** 标签 */
    private List<String> tags;

    /** 扩展属性 */
    private Map<String, Object> metadata;

    public DocumentCreateRequest() {}

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
