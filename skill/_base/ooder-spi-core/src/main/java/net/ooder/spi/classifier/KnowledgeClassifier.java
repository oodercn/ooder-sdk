package net.ooder.spi.classifier;

import java.util.List;

/**
 * 知识分类器 SPI
 * 提供文本分类、标签提取、实体识别等功能
 */
public interface KnowledgeClassifier {

    /**
     * 对文本进行分类
     *
     * @param text 待分类文本
     * @return 分类结果
     */
    String classify(String text);

    /**
     * 提取标签
     *
     * @param text 文本内容
     * @return 标签列表
     */
    List<String> extractTags(String text);

    /**
     * 提取实体
     *
     * @param text 文本内容
     * @return 实体列表
     */
    List<DictEntity> extractEntities(String text);

    /**
     * 判断是否可用
     *
     * @return 是否可用
     */
    boolean isAvailable();

    /**
     * 字典实体
     */
    class DictEntity {
        private String category;
        private String key;
        private String value;
        private String description;

        public DictEntity(String category, String key, String value, String description) {
            this.category = category;
            this.key = key;
            this.value = value;
            this.description = description;
        }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
