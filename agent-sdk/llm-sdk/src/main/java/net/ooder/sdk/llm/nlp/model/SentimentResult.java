package net.ooder.sdk.llm.nlp.model;

public class SentimentResult {
    private String sentiment;
    private Double positiveScore;
    private Double negativeScore;
    private Double neutralScore;
    private String emotion;
    private Double confidence;

    public SentimentResult() {
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public Double getPositiveScore() {
        return positiveScore;
    }

    public void setPositiveScore(Double positiveScore) {
        this.positiveScore = positiveScore;
    }

    public Double getNegativeScore() {
        return negativeScore;
    }

    public void setNegativeScore(Double negativeScore) {
        this.negativeScore = negativeScore;
    }

    public Double getNeutralScore() {
        return neutralScore;
    }

    public void setNeutralScore(Double neutralScore) {
        this.neutralScore = neutralScore;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
