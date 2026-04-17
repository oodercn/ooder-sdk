package net.ooder.spi.messaging.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class Content {

    private ContentType type;

    private String text;

    private String markdown;

    private String html;

    private List<Attachment> attachments;

    private Map<String, Object> metadata;

    public enum ContentType {
        TEXT, MARKDOWN, HTML, IMAGE, FILE, CARD, TEMPLATE
    }

    @Data
    public static class Attachment {
        private String id;
        private String type;
        private String url;
        private String name;
        private Long size;
        private Map<String, Object> metadata;
    }
}
