package net.ooder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public enum AggregationType implements IconEnumstype {
    API("通用API", "ri-plug-line"),
    MENU("菜单", "ri-menu-line"),
    VIEW("视图", "ri-eye-line"),
    NAVIGATION("导航", "ri-compass-line"),
    BAR("BAR组件", "ri-box-2-line"),
    MODULE("模块", "ri-box-2-line"),
    REPOSITORY("仓储", "ri-database-2-line"),
    DOMAIN("领域", "ri-stack-line"),
    ENTITY("实体", "ri-box-2-line");

    private final String name;
    private final String imageClass;


    AggregationType(String name, String imageClass) {
        this.name = name;
        this.imageClass = imageClass;
    }

    public static AggregationType fromType(String type) {
        AggregationType defaultViewType = API;
        if (type != null) {
            for (AggregationType viewType : AggregationType.values()) {
                if (viewType.getType().equals(type)) {
                    defaultViewType = viewType;
                }
            }
        }

        return defaultViewType;
    }

    public String getImageClass() {
        return imageClass;
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public String getType() {
        return name();
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * 标记AgentAction方法的参数元数据
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public static @interface AgentParam {
        /** 参数名称 */
        String name();

        /** 参数描述 */
        String description() default "";

        /** 是否必填 */
        boolean required() default true;

        /** 默认值 */
        String defaultValue() default "";

        /** 参数验证规则 */
        String validationRule() default "";
    }
}