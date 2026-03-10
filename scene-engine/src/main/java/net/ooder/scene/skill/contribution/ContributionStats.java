package net.ooder.scene.skill.contribution;

import java.util.Map;

/**
 * 贡献统计
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public class ContributionStats {

    private String userId;
    private int totalContributions;
    private long totalPoints;
    private int level;
    private Map<String, Long> typeCounts;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalContributions() {
        return totalContributions;
    }

    public void setTotalContributions(int totalContributions) {
        this.totalContributions = totalContributions;
    }

    public long getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Map<String, Long> getTypeCounts() {
        return typeCounts;
    }

    public void setTypeCounts(Map<String, Long> typeCounts) {
        this.typeCounts = typeCounts;
    }
}
