package net.ooder.scene.skill.tool.builtin;

import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.knowledge.KnowledgeSearchRequest;
import net.ooder.scene.skill.knowledge.KnowledgeSearchResult;
import net.ooder.scene.skill.tool.*;

import java.util.*;

/**
 * 知识库检索工具
 *
 * <p>提供知识库向量检索能力，可被 LLM Function Calling 调用。</p>
 *
 * <p>架构层次：应用层 - 智能增强</p>
 *
 * @author ooder
 * @since 2.3
 */
public class SearchKnowledgeTool implements Tool {
    
    private static final String NAME = "search_knowledge";
    private static final String DESCRIPTION = "在知识库中检索相关信息。根据查询内容返回最相关的知识片段。";
    
    private final KnowledgeBaseService knowledgeBaseService;
    
    public SearchKnowledgeTool(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
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
    public Map<String, Object> getParametersSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        
        Map<String, Object> properties = new LinkedHashMap<>();
        
        Map<String, Object> kbIdProp = new LinkedHashMap<>();
        kbIdProp.put("type", "string");
        kbIdProp.put("description", "知识库ID");
        properties.put("kbId", kbIdProp);
        
        Map<String, Object> queryProp = new LinkedHashMap<>();
        queryProp.put("type", "string");
        queryProp.put("description", "检索查询内容");
        properties.put("query", queryProp);
        
        Map<String, Object> topKProp = new LinkedHashMap<>();
        topKProp.put("type", "integer");
        topKProp.put("description", "返回结果数量，默认5");
        topKProp.put("default", 5);
        properties.put("topK", topKProp);
        
        schema.put("properties", properties);
        schema.put("required", Arrays.asList("kbId", "query"));
        
        return schema;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> arguments, ToolExecutionContext context) {
        String kbId = (String) arguments.get("kbId");
        String query = (String) arguments.get("query");
        Integer topK = arguments.containsKey("topK") ? 
                ((Number) arguments.get("topK")).intValue() : 5;
        
        if (kbId == null || kbId.isEmpty()) {
            return ToolResult.failure("INVALID_ARGUMENT", "kbId is required");
        }
        
        if (query == null || query.isEmpty()) {
            return ToolResult.failure("INVALID_ARGUMENT", "query is required");
        }
        
        try {
            KnowledgeSearchRequest request = new KnowledgeSearchRequest();
            request.setQuery(query);
            request.setTopK(topK);
            
            List<KnowledgeSearchResult> results = knowledgeBaseService.search(kbId, request);
            
            List<Map<String, Object>> searchResults = new ArrayList<>();
            for (KnowledgeSearchResult result : results) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("docId", result.getDocId());
                item.put("chunkId", result.getChunkId());
                item.put("content", result.getContent());
                item.put("score", result.getScore());
                item.put("title", result.getTitle());
                searchResults.add(item);
            }
            
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("query", query);
            data.put("kbId", kbId);
            data.put("totalResults", searchResults.size());
            data.put("results", searchResults);
            
            return ToolResult.success(data);
            
        } catch (Exception e) {
            return ToolResult.failure("SEARCH_ERROR", "Search failed: " + e.getMessage());
        }
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
    }
    
    @Override
    public String getCategory() {
        return "knowledge";
    }
    
    @Override
    public List<String> getTags() {
        return Arrays.asList("search", "knowledge", "rag");
    }
}
