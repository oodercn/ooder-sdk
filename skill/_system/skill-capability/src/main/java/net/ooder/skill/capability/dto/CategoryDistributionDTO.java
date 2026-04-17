package net.ooder.skill.capability.dto;

public class CategoryDistributionDTO {
    
    private String name;
    private int count;
    private String color;

    public CategoryDistributionDTO() {}

    public CategoryDistributionDTO(String name, int count, String color) {
        this.name = name;
        this.count = count;
        this.color = color;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
