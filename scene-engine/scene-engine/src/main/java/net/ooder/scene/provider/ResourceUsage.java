package net.ooder.scene.provider;

/**
 * 资源使用
 */
public class ResourceUsage {
    private String name;
    private String type;
    private String unit;
    private double used;
    private double total;
    private double percentage;
    private long timestamp;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public double getUsed() { return used; }
    public void setUsed(double used) { this.used = used; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
