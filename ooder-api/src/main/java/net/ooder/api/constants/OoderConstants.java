package net.ooder.api.constants;

/**
 * Ooder 常量定义
 */
public final class OoderConstants {
    
    private OoderConstants() {
        // 禁止实例化
    }
    
    // 版本信息
    public static final String VERSION = "1.0.0";
    public static final String API_VERSION = "v1";
    
    // 分隔符
    public static final String SEPARATOR_DOT = ".";
    public static final String SEPARATOR_COMMA = ",";
    public static final String SEPARATOR_COLON = ":";
    public static final String SEPARATOR_SLASH = "/";
    public static final String SEPARATOR_BACKSLASH = "\\";
    
    // 编码
    public static final String ENCODING_UTF8 = "UTF-8";
    public static final String ENCODING_GBK = "GBK";
    
    // 时间单位（毫秒）
    public static final long SECOND = 1000L;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
}
