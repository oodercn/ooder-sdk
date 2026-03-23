package net.ooder.scene.skill.knowledge;

import java.util.Map;

public class KnowledgeBaseStats {

    private int totalKb;
    private int totalDocs;
    private int totalBindings;
    private String embeddingModel;
    private long totalSize;
    private int totalChunks;
    private Map<String, Integer> layerStats;
    private Map<String, Integer> visibilityStats;
    private Map<String, Integer> statusStats;

    public KnowledgeBaseStats() {}

    public int getTotalKb() {
        return totalKb;
    }

    public void setTotalKb(int totalKb) {
        this.totalKb = totalKb;
    }

    public int getTotalDocs() {
        return totalDocs;
    }

    public void setTotalDocs(int totalDocs) {
        this.totalDocs = totalDocs;
    }

    public int getTotalBindings() {
        return totalBindings;
    }

    public void setTotalBindings(int totalBindings) {
        this.totalBindings = totalBindings;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }

    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }

    public Map<String, Integer> getLayerStats() {
        return layerStats;
    }

    public void setLayerStats(Map<String, Integer> layerStats) {
        this.layerStats = layerStats;
    }

    public Map<String, Integer> getVisibilityStats() {
        return visibilityStats;
    }

    public void setVisibilityStats(Map<String, Integer> visibilityStats) {
        this.visibilityStats = visibilityStats;
    }

    public Map<String, Integer> getStatusStats() {
        return statusStats;
    }

    public void setStatusStats(Map<String, Integer> statusStats) {
        this.statusStats = statusStats;
    }

    public String getFormattedTotalSize() {
        if (totalSize < 1024) {
            return totalSize + " B";
        } else if (totalSize < 1024 * 1024) {
            return String.format("%.2f KB", totalSize / 1024.0);
        } else if (totalSize < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", totalSize / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", totalSize / (1024.0 * 1024 * 1024));
        }
    }
}
