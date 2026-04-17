package net.ooder.skill.knowledge.dto;

public class UrlImportRequestDTO {
    
    private String url;
    
    private String title;
    
    private Boolean extractContent;

    public UrlImportRequestDTO() {}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getExtractContent() {
        return extractContent;
    }

    public void setExtractContent(Boolean extractContent) {
        this.extractContent = extractContent;
    }
}
