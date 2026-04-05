package net.ooder.bpm.enums.right;


import net.ooder.annotation.Enumstype;

public enum PerformGroupEnums implements Enumstype {

    performs("performs", "办理人", "bpmfont bpmgongzuoliu", "Performs"),

    readers("readers", "阅办人", "bpmfont bpmyuedu", "Readers"),

    insteadSigns("insteadSigns", "代签人", "bpmfont bpmdingzhigongzuoliu", "InsteadSigns");

    public String getImageClass() {
        return imageClass;
    }

    private String type;

    private String name;

    private String imageClass;


    private String className;


    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }

    PerformGroupEnums(String type, String name, String imageClass, String className) {
        this.type = type;
        this.name = name;
        this.imageClass = imageClass;
        this.className = className;

    }

    @Override
    public String toString() {
        return type;
    }

    public static PerformGroupEnums fromType(String typeName) {
        for (PerformGroupEnums type : PerformGroupEnums.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

    public static PerformGroupEnums fromName(String name) {
        for (PerformGroupEnums type : PerformGroupEnums.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
