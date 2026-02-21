/**
 * $RCSfile: ActivityDefImpl.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.config;


import net.ooder.annotation.IconEnumstype;

public enum ActivityDefImpl implements IconEnumstype {

    No( "人工活动","fa-solid fa-user"),

    Tool("自动任务","fa-solid fa-cog"),

    Device( "设备节点","fa-solid fa-mobile"),

    Service("服务节点","fa-solid fa-server"),

    Block( "场景服务","fa-solid fa-film"),

    Event("设备事件","fa-solid fa-bell"),

    SubFlow( "子流程","fa-solid fa-sitemap"),

    OutFlow("外部流程","fa-solid fa-external-link"),

    Process("默认流程","fa-solid fa-tasks");


    private final String name;
    private String type;
    private String imageClass;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    ActivityDefImpl(String name, String imageClass) {
        this.type = name();
        this.name = name;
        this.imageClass=imageClass;


    }

    @Override
    public String toString() {
        return name;
    }

    public static ActivityDefImpl fromType(String typeName) {
        for (ActivityDefImpl type : ActivityDefImpl.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return Process;
    }

    @Override
    public String getImageClass() {
        return imageClass;
    }



    @Override
    public String getName() {
        return name;
    }

}
