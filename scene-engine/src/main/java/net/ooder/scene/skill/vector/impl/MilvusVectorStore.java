package net.ooder.scene.skill.vector.impl;

import com.alibaba.fastjson.JSONObject;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.HasCollectionReq;
import io.milvus.v2.service.collection.request.DropCollectionReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.DeleteReq;
import io.milvus.v2.service.vector.request.QueryReq;
import io.milvus.v2.service.vector.response.InsertResp;
import io.milvus.v2.service.vector.response.SearchResp;
import io.milvus.v2.service.vector.response.QueryResp;
import net.ooder.scene.skill.vector.AbstractVectorStore;
import net.ooder.scene.skill.vector.SearchResult;
import net.ooder.scene.skill.vector.VectorData;
import net.ooder.scene.skill.vector.VectorStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Milvus 向量存储实现
 *
 * <p>基于 Milvus 2.x 向量数据库的真实实现，使用 milvus-sdk-java v2.4.1。</p>
 *
 * <h3>功能特性：</h3>
 * <ul>
 *   <li>向量插入与批量插入</li>
 *   <li>向量相似度搜索（COSINE/L2/IP）</li>
 *   <li>元数据过滤搜索</li>
 *   <li>向量删除（按ID或元数据过滤）</li>
 *   <li>自动创建 Collection 和索引</li>
 * </ul>
 *
 * <h3>配置示例：</h3>
 * <pre>
 * scene.engine.knowledge.vector-store.type=milvus
 * scene.engine.knowledge.vector-store.milvus.host=localhost
 * scene.engine.knowledge.vector-store.milvus.port=19530
 * scene.engine.knowledge.vector-store.milvus.database=default
 * scene.engine.knowledge.vector-store.milvus.collection=knowledge_vectors
 * scene.engine.knowledge.vector-store.milvus.token=  # 可选，用于认证
 * </pre>
 *
 * <h3>Collection Schema：</h3>
 * <ul>
 *   <li>id (VARCHAR): 向量唯一标识</li>
 *   <li>vector (FLOAT_VECTOR): 嵌入向量</li>
 *   <li>content (VARCHAR): 原始文本内容</li>
 *   <li>kb_id (VARCHAR): 知识库ID</li>
 *   <li>scene_group_id (VARCHAR): 场景组ID</li>
 *   <li>layer (VARCHAR): 层级</li>
 *   <li>source (VARCHAR): 来源</li>
 *   <li>chunk_index (INT64): 分块索引</li>
 * </ul>
 *
 * @author ooder
 * @since 3.0.1
 */
public class MilvusVectorStore extends AbstractVectorStore {

    private static final Logger log = LoggerFactory.getLogger(MilvusVectorStore.class);

    private static final String FIELD_ID = "id";
    private static final String FIELD_VECTOR = "vector";
    private static final String FIELD_CONTENT = "content";
    private static final String FIELD_KB_ID = "kb_id";
    private static final String FIELD_SCENE_GROUP_ID = "scene_group_id";
    private static final String FIELD_LAYER = "layer";
    private static final String FIELD_SOURCE = "source";
    private static final String FIELD_CHUNK_INDEX = "chunk_index";

    private final String host;
    private final int port;
    private final String database;
    private final String collection;
    private final String token;

    private MilvusClientV2 client;
    private boolean initialized = false;

    public MilvusVectorStore(VectorStoreConfig config) {
        super(config);
        this.host = config.getProperty("host", "localhost");
        this.port = config.getProperty("port", 19530);
        this.database = config.getProperty("database", "default");
        this.collection = config.getProperty("collection", "knowledge_vectors");
        this.token = config.getProperty("token", "");
    }

    public MilvusVectorStore(String host, int port, String database, String collection, int dimension) {
        super(VectorStoreConfig.milvus(dimension, host, port, database, collection));
        this.host = host;
        this.port = port;
        this.database = database;
        this.collection = collection;
        this.token = "";
    }

    @Override
    public void initialize() {
        log.info("Initializing MilvusVectorStore: {}:{}", host, port);
        log.info("Collection: {}, Database: {}, Dimension: {}", collection, database, dimension);

        try {
            ConnectConfig.ConnectConfigBuilder builder = ConnectConfig.builder()
                    .uri("http://" + host + ":" + port)
                    .dbName(database);

            if (token != null && !token.isEmpty()) {
                builder.token(token);
            }

            client = new MilvusClientV2(builder.build());

            ensureCollectionExists();

            initialized = true;
            log.info("MilvusVectorStore initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize MilvusVectorStore: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize MilvusVectorStore", e);
        }
    }

    private void ensureCollectionExists() {
        try {
            HasCollectionReq hasCollectionReq = HasCollectionReq.builder()
                    .collectionName(collection)
                    .build();

            boolean hasCollection = client.hasCollection(hasCollectionReq);

            if (!hasCollection) {
                createCollection();
                log.info("Created new collection: {}", collection);
            } else {
                log.debug("Collection already exists: {}", collection);
            }
        } catch (Exception e) {
            log.error("Failed to check/create collection: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to ensure collection exists", e);
        }
    }

    private void createCollection() {
        CreateCollectionReq.CollectionSchema schema = client.createSchema();

        schema.addField(AddFieldReq.builder()
                .fieldName(FIELD_ID)
                .dataType(DataType.VarChar)
                .maxLength(256)
                .isPrimaryKey(true)
                .autoID(false)
                .build());

        schema.addField(AddFieldReq.builder()
                .fieldName(FIELD_VECTOR)
                .dataType(DataType.FloatVector)
                .dimension(dimension)
                .build());

        schema.addField(AddFieldReq.builder()
                .fieldName(FIELD_CONTENT)
                .dataType(DataType.VarChar)
                .maxLength(65535)
                .build());

        schema.addField(AddFieldReq.builder()
                .fieldName(FIELD_KB_ID)
                .dataType(DataType.VarChar)
                .maxLength(256)
                .build());

        schema.addField(AddFieldReq.builder()
                .fieldName(FIELD_SCENE_GROUP_ID)
                .dataType(DataType.VarChar)
                .maxLength(256)
                .build());

        schema.addField(AddFieldReq.builder()
                .fieldName(FIELD_LAYER)
                .dataType(DataType.VarChar)
                .maxLength(64)
                .build());

        schema.addField(AddFieldReq.builder()
                .fieldName(FIELD_SOURCE)
                .dataType(DataType.VarChar)
                .maxLength(1024)
                .build());

        schema.addField(AddFieldReq.builder()
                .fieldName(FIELD_CHUNK_INDEX)
                .dataType(DataType.Int64)
                .build());

        List<IndexParam> indexParams = new ArrayList<>();
        indexParams.add(IndexParam.builder()
                .fieldName(FIELD_VECTOR)
                .indexType(IndexParam.IndexType.AUTOINDEX)
                .metricType(mapMetricType(metricType))
                .build());

        CreateCollectionReq createCollectionReq = CreateCollectionReq.builder()
                .collectionName(collection)
                .collectionSchema(schema)
                .indexParams(indexParams)
                .build();

        client.createCollection(createCollectionReq);
        log.info("Collection created with AUTOINDEX, metric: {}", metricType);
    }

    private IndexParam.MetricType mapMetricType(String metricType) {
        switch (metricType.toUpperCase()) {
            case "L2":
            case "EUCLIDEAN":
                return IndexParam.MetricType.L2;
            case "IP":
            case "INNER_PRODUCT":
                return IndexParam.MetricType.IP;
            case "COSINE":
            default:
                return IndexParam.MetricType.COSINE;
        }
    }

    @Override
    public void shutdown() {
        if (client != null) {
            try {
                client.close(10L);
                log.info("MilvusVectorStore shutdown complete");
            } catch (Exception e) {
                log.error("Error shutting down MilvusVectorStore: {}", e.getMessage());
            }
        }
        initialized = false;
    }

    @Override
    public void insert(String id, float[] vector, Map<String, Object> metadata) {
        validateVector(vector);
        ensureInitialized();

        try {
            JSONObject data = new JSONObject();
            data.put(FIELD_ID, id);
            data.put(FIELD_VECTOR, toFloatList(vector));
            data.put(FIELD_CONTENT, getMetadataString(metadata, "content", ""));
            data.put(FIELD_KB_ID, getMetadataString(metadata, "kbId", ""));
            data.put(FIELD_SCENE_GROUP_ID, getMetadataString(metadata, "sceneGroupId", ""));
            data.put(FIELD_LAYER, getMetadataString(metadata, "layer", ""));
            data.put(FIELD_SOURCE, getMetadataString(metadata, "source", ""));
            data.put(FIELD_CHUNK_INDEX, getMetadataLong(metadata, "chunkIndex", 0L));

            InsertReq insertReq = InsertReq.builder()
                    .collectionName(collection)
                    .data(Collections.singletonList(data))
                    .build();

            client.insert(insertReq);
            log.debug("Inserted vector: {}", id);
        } catch (Exception e) {
            log.error("Failed to insert vector {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to insert vector", e);
        }
    }

    @Override
    public void batchInsert(List<VectorData> vectors) {
        if (vectors == null || vectors.isEmpty()) {
            return;
        }
        ensureInitialized();

        try {
            List<JSONObject> dataList = new ArrayList<>();
            for (VectorData vd : vectors) {
                validateVector(vd.getVector());

                JSONObject data = new JSONObject();
                data.put(FIELD_ID, vd.getId());
                data.put(FIELD_VECTOR, toFloatList(vd.getVector()));
                data.put(FIELD_CONTENT, getMetadataString(vd.getMetadata(), "content", ""));
                data.put(FIELD_KB_ID, getMetadataString(vd.getMetadata(), "kbId", ""));
                data.put(FIELD_SCENE_GROUP_ID, getMetadataString(vd.getMetadata(), "sceneGroupId", ""));
                data.put(FIELD_LAYER, getMetadataString(vd.getMetadata(), "layer", ""));
                data.put(FIELD_SOURCE, getMetadataString(vd.getMetadata(), "source", ""));
                data.put(FIELD_CHUNK_INDEX, getMetadataLong(vd.getMetadata(), "chunkIndex", 0L));

                dataList.add(data);
            }

            InsertReq insertReq = InsertReq.builder()
                    .collectionName(collection)
                    .data(dataList)
                    .build();

            InsertResp resp = client.insert(insertReq);
            log.debug("Batch inserted {} vectors, insert count: {}", vectors.size(), resp.getInsertCnt());
        } catch (Exception e) {
            log.error("Failed to batch insert vectors: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to batch insert vectors", e);
        }
    }

    @Override
    public List<SearchResult> search(float[] queryVector, int topK, Map<String, Object> filters) {
        validateVector(queryVector);
        ensureInitialized();

        try {
            String filterExpr = buildFilterExpression(filters);

            SearchReq.SearchReqBuilder searchReqBuilder = SearchReq.builder()
                    .collectionName(collection)
                    .data(Collections.singletonList(toFloatList(queryVector)))
                    .topK(topK)
                    .outputFields(Arrays.asList(FIELD_ID, FIELD_CONTENT, FIELD_KB_ID,
                            FIELD_SCENE_GROUP_ID, FIELD_LAYER, FIELD_SOURCE, FIELD_CHUNK_INDEX));

            if (filterExpr != null && !filterExpr.isEmpty()) {
                searchReqBuilder.filter(filterExpr);
            }

            SearchResp searchResp = client.search(searchReqBuilder.build());

            List<SearchResult> results = new ArrayList<>();
            List<List<SearchResp.SearchResult>> searchResults = searchResp.getSearchResults();

            if (searchResults != null && !searchResults.isEmpty()) {
                for (SearchResp.SearchResult sr : searchResults.get(0)) {
                    SearchResult result = new SearchResult();
                    result.setId(String.valueOf(sr.getId()));
                    result.setScore((float) sr.getDistance());

                    Map<String, Object> entity = sr.getEntity();
                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("content", entity.getOrDefault(FIELD_CONTENT, ""));
                    metadata.put("kbId", entity.getOrDefault(FIELD_KB_ID, ""));
                    metadata.put("sceneGroupId", entity.getOrDefault(FIELD_SCENE_GROUP_ID, ""));
                    metadata.put("layer", entity.getOrDefault(FIELD_LAYER, ""));
                    metadata.put("source", entity.getOrDefault(FIELD_SOURCE, ""));
                    metadata.put("chunkIndex", entity.getOrDefault(FIELD_CHUNK_INDEX, 0));
                    result.setMetadata(metadata);
                    result.setContent(String.valueOf(entity.getOrDefault(FIELD_CONTENT, "")));

                    results.add(result);
                }
            }

            log.debug("Search returned {} results for topK={}", results.size(), topK);
            return results;
        } catch (Exception e) {
            log.error("Failed to search vectors: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search vectors", e);
        }
    }

    @Override
    public void delete(String id) {
        ensureInitialized();

        try {
            String filterExpr = FIELD_ID + " == \"" + id + "\"";

            DeleteReq deleteReq = DeleteReq.builder()
                    .collectionName(collection)
                    .filter(filterExpr)
                    .build();

            client.delete(deleteReq);
            log.debug("Deleted vector: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete vector {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete vector", e);
        }
    }

    @Override
    public void deleteByMetadata(Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) {
            log.warn("deleteByMetadata called with empty filters, skipping");
            return;
        }
        ensureInitialized();

        try {
            String filterExpr = buildFilterExpression(filters);
            if (filterExpr == null || filterExpr.isEmpty()) {
                log.warn("Built empty filter expression, skipping delete");
                return;
            }

            DeleteReq deleteReq = DeleteReq.builder()
                    .collectionName(collection)
                    .filter(filterExpr)
                    .build();

            client.delete(deleteReq);
            log.debug("Deleted vectors by metadata filter: {}", filterExpr);
        } catch (Exception e) {
            log.error("Failed to delete vectors by metadata: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete vectors by metadata", e);
        }
    }

    @Override
    public long count() {
        ensureInitialized();

        try {
            QueryReq queryReq = QueryReq.builder()
                    .collectionName(collection)
                    .filter("")
                    .outputFields(Collections.singletonList("count(*)"))
                    .build();

            QueryResp queryResp = client.query(queryReq);
            List<QueryResp.QueryResult> results = queryResp.getQueryResults();

            if (results != null && !results.isEmpty()) {
                Object countObj = results.get(0).getEntity().get("count(*)");
                if (countObj instanceof Number) {
                    return ((Number) countObj).longValue();
                }
            }
            return 0;
        } catch (Exception e) {
            log.error("Failed to count vectors: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public void clear() {
        ensureInitialized();

        try {
            DropCollectionReq dropReq = DropCollectionReq.builder()
                    .collectionName(collection)
                    .build();

            client.dropCollection(dropReq);
            log.info("Dropped collection: {}", collection);

            createCollection();
            log.info("Recreated collection: {}", collection);
        } catch (Exception e) {
            log.error("Failed to clear collection: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to clear collection", e);
        }
    }

    private String buildFilterExpression(Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }

        List<String> conditions = new ArrayList<>();

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                continue;
            }

            String fieldName = mapFieldName(field);
            String condition;

            if (value instanceof String) {
                condition = fieldName + " == \"" + value + "\"";
            } else if (value instanceof Number) {
                condition = fieldName + " == " + value;
            } else if (value instanceof List) {
                List<?> values = (List<?>) value;
                if (!values.isEmpty()) {
                    StringBuilder sb = new StringBuilder(fieldName + " in [");
                    for (int i = 0; i < values.size(); i++) {
                        if (i > 0) sb.append(", ");
                        Object v = values.get(i);
                        if (v instanceof String) {
                            sb.append("\"").append(v).append("\"");
                        } else {
                            sb.append(v);
                        }
                    }
                    sb.append("]");
                    condition = sb.toString();
                } else {
                    continue;
                }
            } else {
                condition = fieldName + " == \"" + value + "\"";
            }

            conditions.add(condition);
        }

        if (conditions.isEmpty()) {
            return null;
        }

        return String.join(" and ", conditions);
    }

    private String mapFieldName(String field) {
        switch (field.toLowerCase()) {
            case "kbid":
            case "kb_id":
                return FIELD_KB_ID;
            case "scene_group_id":
            case "scene_groupid":
            case "sceneGroupId":
                return FIELD_SCENE_GROUP_ID;
            case "layer":
                return FIELD_LAYER;
            case "source":
                return FIELD_SOURCE;
            case "chunk_index":
            case "chunkIndex":
                return FIELD_CHUNK_INDEX;
            default:
                return field;
        }
    }

    private List<Float> toFloatList(float[] vector) {
        List<Float> list = new ArrayList<>(vector.length);
        for (float v : vector) {
            list.add(v);
        }
        return list;
    }

    private String getMetadataString(Map<String, Object> metadata, String key, String defaultValue) {
        if (metadata == null) {
            return defaultValue;
        }
        Object value = metadata.get(key);
        return value != null ? String.valueOf(value) : defaultValue;
    }

    private Long getMetadataLong(Map<String, Object> metadata, String key, Long defaultValue) {
        if (metadata == null) {
            return defaultValue;
        }
        Object value = metadata.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return defaultValue;
    }

    private void ensureInitialized() {
        if (!initialized || client == null) {
            throw new IllegalStateException("MilvusVectorStore not initialized. Call initialize() first.");
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getCollection() {
        return collection;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
