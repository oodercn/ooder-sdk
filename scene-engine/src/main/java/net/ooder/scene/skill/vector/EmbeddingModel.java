package net.ooder.scene.skill.vector;

import java.util.List;

public interface EmbeddingModel {

    String getId();

    String getName();

    String getProvider();

    int getDimensions();

    int getMaxTokens();

    List<Float> embed(String text);

    List<List<Float>> embedBatch(List<String> texts);

    boolean isAvailable();

    EmbeddingModelInfo getInfo();

    class EmbeddingModelInfo {
        private String id;
        private String name;
        private String provider;
        private int dimensions;
        private int maxTokens;
        private boolean available;
        private String description;

        public EmbeddingModelInfo() {}

        public EmbeddingModelInfo(String id, String name, String provider, int dimensions, int maxTokens) {
            this.id = id;
            this.name = name;
            this.provider = provider;
            this.dimensions = dimensions;
            this.maxTokens = maxTokens;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public int getDimensions() {
            return dimensions;
        }

        public void setDimensions(int dimensions) {
            this.dimensions = dimensions;
        }

        public int getMaxTokens() {
            return maxTokens;
        }

        public void setMaxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
