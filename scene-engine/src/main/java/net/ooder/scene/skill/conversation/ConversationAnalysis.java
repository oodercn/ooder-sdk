package net.ooder.scene.skill.conversation;

import java.util.Map;

/**
 * 对话分析结果
 *
 * <p>提供对话的深入分析，包括：</p>
 * <ul>
 *   <li>用户意图识别</li>
 *   <li>情感倾向分析</li>
 *   <li>话题分类</li>
 *   <li>关键信息提取</li>
 * </ul>
 *
 * <p>架构层次：应用层 - 智能增强</p>
 *
 * @author ooder
 * @since 2.3
 */
public class ConversationAnalysis {

    private String conversationId;
    private String intent;
    private String sentiment;
    private String topic;
    private double confidence;
    private Map<String, Object> entities;
    private Map<String, Object> metadata;

    public ConversationAnalysis() {
    }

    public ConversationAnalysis(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * 获取用户意图
     */
    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    /**
     * 获取情感倾向 (positive, negative, neutral)
     */
    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    /**
     * 获取话题分类
     */
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * 获取置信度 (0-1)
     */
    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    /**
     * 获取提取的实体
     */
    public Map<String, Object> getEntities() {
        return entities;
    }

    public void setEntities(Map<String, Object> entities) {
        this.entities = entities;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "ConversationAnalysis{" +
                "conversationId='" + conversationId + '\'' +
                ", intent='" + intent + '\'' +
                ", sentiment='" + sentiment + '\'' +
                ", topic='" + topic + '\'' +
                ", confidence=" + confidence +
                '}';
    }
}
