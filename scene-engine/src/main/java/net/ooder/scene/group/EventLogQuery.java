package net.ooder.scene.group;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 事件日志查询条件
 *
 * <p>用于高级查询场景组事件日志，支持按类型、时间范围、参与者等条件过滤。</p>
 *
 * <h3>使用示例：</h3>
 * <pre>
 * EventLogQuery query = EventLogQuery.builder()
 *     .eventTypes(List.of("PARTICIPANT_JOIN", "KNOWLEDGE_BIND"))
 *     .startTime(Instant.now().minus(Duration.ofHours(24)))
 *     .endTime(Instant.now())
 *     .participantId("user-123")
 *     .limit(100)
 *     .build();
 *
 * List&lt;SceneGroupEvent&gt; events = sceneGroup.getEventLog(query);
 * </pre>
 *
 * @author SE Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class EventLogQuery {

    /**
     * 事件类型列表，null 表示不过滤
     */
    private List<String> eventTypes;

    /**
     * 开始时间，null 表示不限制
     */
    private Instant startTime;

    /**
     * 结束时间，null 表示不限制
     */
    private Instant endTime;

    /**
     * 参与者ID，null 表示不过滤
     */
    private String participantId;

    /**
     * 相关对象ID，null 表示不过滤
     */
    private String relatedId;

    /**
     * 搜索关键词，null 表示不过滤
     */
    private String keyword;

    /**
     * 返回数量限制，默认 100，-1 表示无限制
     */
    private int limit = 100;

    /**
     * 偏移量，用于分页，默认 0
     */
    private int offset = 0;

    /**
     * 是否按时间倒序，默认 true
     */
    private boolean descending = true;

    public EventLogQuery() {}

    // ===== Builder 模式 =====

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final EventLogQuery query = new EventLogQuery();

        public Builder eventTypes(List<String> eventTypes) {
            query.setEventTypes(eventTypes);
            return this;
        }

        public Builder eventType(String eventType) {
            if (query.eventTypes == null) {
                query.eventTypes = new ArrayList<>();
            }
            query.eventTypes.add(eventType);
            return this;
        }

        public Builder startTime(Instant startTime) {
            query.setStartTime(startTime);
            return this;
        }

        public Builder endTime(Instant endTime) {
            query.setEndTime(endTime);
            return this;
        }

        public Builder participantId(String participantId) {
            query.setParticipantId(participantId);
            return this;
        }

        public Builder relatedId(String relatedId) {
            query.setRelatedId(relatedId);
            return this;
        }

        public Builder keyword(String keyword) {
            query.setKeyword(keyword);
            return this;
        }

        public Builder limit(int limit) {
            query.setLimit(limit);
            return this;
        }

        public Builder offset(int offset) {
            query.setOffset(offset);
            return this;
        }

        public Builder descending(boolean descending) {
            query.setDescending(descending);
            return this;
        }

        public EventLogQuery build() {
            return query;
        }
    }

    // ===== Getter/Setter =====

    public List<String> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<String> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    // ===== 便捷方法 =====

    /**
     * 检查是否有过滤条件
     *
     * @return true 如果有任何过滤条件
     */
    public boolean hasFilters() {
        return eventTypes != null && !eventTypes.isEmpty()
            || startTime != null
            || endTime != null
            || participantId != null
            || relatedId != null
            || keyword != null;
    }

    /**
     * 检查是否包含指定事件类型
     *
     * @param eventType 事件类型
     * @return true 如果包含
     */
    public boolean includesEventType(String eventType) {
        if (eventTypes == null || eventTypes.isEmpty()) {
            return true;
        }
        return eventTypes.contains(eventType);
    }

    /**
     * 检查时间是否在范围内
     *
     * @param timestamp 时间戳
     * @return true 如果在范围内
     */
    public boolean isInTimeRange(long timestamp) {
        if (startTime != null && timestamp < startTime.toEpochMilli()) {
            return false;
        }
        if (endTime != null && timestamp > endTime.toEpochMilli()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EventLogQuery{" +
            "eventTypes=" + eventTypes +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", participantId='" + participantId + '\'' +
            ", relatedId='" + relatedId + '\'' +
            ", keyword='" + keyword + '\'' +
            ", limit=" + limit +
            ", offset=" + offset +
            ", descending=" + descending +
            '}';
    }
}
