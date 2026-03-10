package net.ooder.scene.skill.contribution;

/**
 * 贡献记录
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public class Contribution {

    private String id;
    private String type;
    private String description;
    private long points;
    private long timestamp;

    public Contribution() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
