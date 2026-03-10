package net.ooder.scene.skill.tool.builtin;

import net.ooder.scene.skill.knowledge.Document;
import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.tool.*;

import java.util.*;

/**
 * 列出知识库文档工具
 *
 * @author ooder
 * @since 2.3
 */
public class ListDocumentsTool implements Tool {
    
    private static final String ID = "list_documents";
    private static final String NAME = "list_documents";
    private static final String DESCRIPTION = "列出知识库中的文档列表。";

    private final KnowledgeBaseService knowledgeBaseService;

    public ListDocumentsTool(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
    }
    
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
    
    @Override
    public Map<String, Object> getParameters() {
        return getParametersSchema();
    }

    @Override
    public Map<String, Object> getParametersSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new LinkedHashMap<>();

        Map<String, Object> kbIdProp = new LinkedHashMap<>();
        kbIdProp.put("type", "string");
        kbIdProp.put("description", "知识库ID");
        properties.put("kbId", kbIdProp);

        Map<String, Object> limitProp = new LinkedHashMap<>();
        limitProp.put("type", "integer");
        limitProp.put("description", "返回数量限制，默认10");
        limitProp.put("default", 10);
        properties.put("limit", limitProp);

        schema.put("properties", properties);
        schema.put("required", Collections.singletonList("kbId"));

        return schema;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> arguments, ToolContext context) {
        String kbId = (String) arguments.get("kbId");
        Integer limit = arguments.containsKey("limit") ? 
                ((Number) arguments.get("limit")).intValue() : 10;
        
        if (kbId == null || kbId.isEmpty()) {
            return ToolResult.failure("INVALID_ARGUMENT", "kbId is required");
        }
        
        try {
            List<Document> documents = knowledgeBaseService.listDocuments(kbId);
            
            List<Map<String, Object>> docList = new ArrayList<>();
            int count = 0;
            for (Document doc : documents) {
                if (count >= limit) break;
                
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("docId", doc.getDocId());
                item.put("title", doc.getTitle());
                item.put("source", doc.getSource());
                item.put("createdAt", doc.getCreatedAt());
                docList.add(item);
                count++;
            }
            
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("kbId", kbId);
            data.put("totalDocuments", documents.size());
            data.put("returnedCount", docList.size());
            data.put("documents", docList);
            
            return ToolResult.success(data);
            
        } catch (Exception e) {
            return ToolResult.failure("LIST_ERROR", "Failed to list documents: " + e.getMessage());
        }
    }
    
    @Override
    public String getCategory() {
        return "knowledge";
    }

    @Override
    public List<String> getTags() {
        return Arrays.asList("knowledge", "list");
    }
}
