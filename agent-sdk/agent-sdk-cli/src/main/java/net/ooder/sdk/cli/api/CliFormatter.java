package net.ooder.sdk.cli.api;

import java.util.List;
import java.util.Map;

/**
 * CLI格式化器接口
 *
 * <p>负责格式化输出结果</p>
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public interface CliFormatter {

    /**
     * 格式化对象
     *
     * @param data 数据对象
     * @return 格式化字符串
     */
    String format(Object data);

    /**
     * 格式化列表
     *
     * @param list 列表
     * @return 格式化字符串
     */
    String formatList(List<?> list);

    /**
     * 格式化表格
     *
     * @param headers 表头
     * @param rows 行数据
     * @return 格式化字符串
     */
    String formatTable(String[] headers, List<String[]> rows);

    /**
     * 格式化映射
     *
     * @param map 映射
     * @return 格式化字符串
     */
    String formatMap(Map<String, Object> map);

    /**
     * 格式化错误
     *
     * @param error 错误信息
     * @return 格式化字符串
     */
    String formatError(String error);

    /**
     * 格式化成功消息
     *
     * @param message 消息
     * @return 格式化字符串
     */
    String formatSuccess(String message);

    /**
     * 获取格式类型
     *
     * @return 格式类型
     */
    String getFormatType();
}
