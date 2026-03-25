package net.ooder.scene.fallback;

import net.ooder.scene.spi.VectorStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 空向量存储 - 降级实现
 *
 * <p>当没有其他 VectorStore 实现时自动启用</p>
 *
 * <p>不支持实际检索，返回空结果</p>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
@Component
@ConditionalOnMissingBean(VectorStore.class)
@ConditionalOnProperty(prefix = "scene.engine.fallback", name = "enabled", havingValue = "true", matchIfMissing = true)
public class NoOpVectorStore implements VectorStore {

    private static final Logger log = LoggerFactory.getLogger(NoOpVectorStore.class);

    public NoOpVectorStore() {
        log.warn("Using NoOpVectorStore - vector search is disabled!");
    }

    @Override
    public String getProviderType() {
        return "fallback";
    }

    @Override
    public void addVector(String id, float[] embedding, Map<String, Object> metadata) {
        log.debug("NoOp: Ignoring vector add for {}", id);
    }

    @Override
    public void addVectors(List<VectorData> vectors) {
        log.debug("NoOp: Ignoring batch vector add of {} items", vectors.size());
    }

    @Override
    public List<SearchResult> search(float[] embedding, int topK) {
        log.debug("NoOp: Returning empty search results");
        return Collections.emptyList();
    }

    @Override
    public List<SearchResult> search(float[] embedding, int topK, Map<String, Object> filter) {
        log.debug("NoOp: Returning empty search results with filter");
        return Collections.emptyList();
    }

    @Override
    public void deleteVector(String id) {
        log.debug("NoOp: Ignoring vector delete for {}", id);
    }

    @Override
    public void clear() {
        log.debug("NoOp: Ignoring clear");
    }

    @Override
    public int size() {
        return 0;
    }
}
