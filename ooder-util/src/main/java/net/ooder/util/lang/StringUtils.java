package net.ooder.util.lang;

/**
 * 字符串工具类
 * 基于 Apache Commons Lang3 的 StringUtils 进行扩展
 */
public final class StringUtils {
    
    private StringUtils() {
        // 禁止实例化
    }
    
    /**
     * 判断字符串是否为空（null 或长度为 0）
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    
    /**
     * 判断字符串是否为空白（null、长度为 0 或仅包含空白字符）
     * @param str 字符串
     * @return 是否为空白
     */
    public static boolean isBlank(String str) {
        if (isEmpty(str)) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 判断字符串是否不为空
     * @param str 字符串
     * @return 是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 判断字符串是否不为空白
     * @param str 字符串
     * @return 是否不为空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /**
     * 如果字符串为空，返回默认值
     * @param str 字符串
     * @param defaultStr 默认值
     * @return 原字符串或默认值
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }
    
    /**
     * 截取字符串
     * @param str 字符串
     * @param maxLength 最大长度
     * @return 截取后的字符串
     */
    public static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength);
    }
}
