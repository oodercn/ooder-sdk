package net.ooder.scene.log;

public enum LogPriority {

    LOW(1, "低"),

    NORMAL(2, "普通"),

    HIGH(3, "高"),

    CRITICAL(4, "关键");

    private final int value;
    private final String name;

    LogPriority(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() { return value; }
    public String getName() { return name; }
}
