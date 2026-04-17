package net.ooder.skill.capability.dto;

import java.util.List;

public class ScoreDistributionDTO {
    
    private double avgScore;
    private int highCount;
    private int mediumCount;
    private int lowCount;
    private List<Integer> distribution;

    public ScoreDistributionDTO() {}

    public double getAvgScore() { return avgScore; }
    public void setAvgScore(double avgScore) { this.avgScore = avgScore; }
    public int getHighCount() { return highCount; }
    public void setHighCount(int highCount) { this.highCount = highCount; }
    public int getMediumCount() { return mediumCount; }
    public void setMediumCount(int mediumCount) { this.mediumCount = mediumCount; }
    public int getLowCount() { return lowCount; }
    public void setLowCount(int lowCount) { this.lowCount = lowCount; }
    public List<Integer> getDistribution() { return distribution; }
    public void setDistribution(List<Integer> distribution) { this.distribution = distribution; }
}
