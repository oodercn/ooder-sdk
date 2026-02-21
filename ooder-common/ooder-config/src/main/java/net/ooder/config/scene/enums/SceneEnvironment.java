package net.ooder.config.scene.enums;

import net.ooder.config.scene.docs.EnumDocument;

@EnumDocument(
    name = "场景环境",
    description = "定义系统支持的场景环境类型，用于区分不同的部署环境",
    installGuide = "无需安装，系统内置枚举。使用时通过 SceneEnvironment.DEV 等方式引用。",
    startupGuide = "1. 通过环境变量 SCENE_ENV 指定环境\n" +
                   "2. 或在代码中直接使用 SceneFactory.create(SceneEnvironment.DEV)\n" +
                   "3. 系统会自动加载对应的 scene-{env}.yaml 配置文件",
    configExample = "# 环境变量方式\n" +
                    "export SCENE_ENV=dev\n\n" +
                    "# 代码方式\n" +
                    "SceneFactory factory = SceneFactory.create(SceneEnvironment.DEV);"
)
public enum SceneEnvironment {
    
    DEV("dev", "开发环境", "用于本地开发和单元测试，使用内存数据库和本地服务"),
    
    TEST("test", "测试环境", "用于集成测试和系统测试，使用测试数据库"),
    
    STAGING("staging", "预发布环境", "用于生产前的最终验证，配置与生产环境一致"),
    
    PROD("prod", "生产环境", "正式生产环境，使用高可用配置");
    
    private final String code;
    private final String displayName;
    private final String description;
    
    SceneEnvironment(String code, String displayName, String description) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getConfigFileName() {
        return "scene-" + code + ".yaml";
    }
    
    public static SceneEnvironment fromCode(String code) {
        if (code == null) {
            return DEV;
        }
        for (SceneEnvironment env : values()) {
            if (env.code.equalsIgnoreCase(code)) {
                return env;
            }
        }
        return DEV;
    }
    
    public static SceneEnvironment fromSystemProperty() {
        String env = System.getProperty("scene.env");
        if (env == null) {
            env = System.getenv("SCENE_ENV");
        }
        return fromCode(env);
    }
}
