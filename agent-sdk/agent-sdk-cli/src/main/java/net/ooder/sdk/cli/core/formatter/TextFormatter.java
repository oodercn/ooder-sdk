package net.ooder.sdk.cli.core.formatter;

import net.ooder.sdk.cli.api.CliFormatter;

import java.util.List;
import java.util.Map;

/**
 * 文本格式格式化器
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class TextFormatter implements CliFormatter {

    @Override
    public String format(Object data) {
        if (data == null) {
            return "";
        }
        return data.toString();
    }

    @Override
    public String formatList(List<?> list) {
        if (list == null || list.isEmpty()) {
            return "(empty list)";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(i + 1).append(". ").append(list.get(i)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String formatTable(String[] headers, List<String[]> rows) {
        if (headers == null || rows == null || rows.isEmpty()) {
            return "(empty table)";
        }

        StringBuilder sb = new StringBuilder();

        // 计算列宽
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }
        for (String[] row : rows) {
            for (int i = 0; i < row.length && i < widths.length; i++) {
                widths[i] = Math.max(widths[i], row[i] != null ? row[i].length() : 0);
            }
        }

        // 表头
        sb.append("| ");
        for (int i = 0; i < headers.length; i++) {
            sb.append(padRight(headers[i], widths[i])).append(" | ");
        }
        sb.append("\n");

        // 分隔线
        sb.append("|");
        for (int width : widths) {
            sb.append("-".repeat(width + 2)).append("|");
        }
        sb.append("\n");

        // 数据行
        for (String[] row : rows) {
            sb.append("| ");
            for (int i = 0; i < headers.length; i++) {
                String cell = i < row.length && row[i] != null ? row[i] : "";
                sb.append(padRight(cell, widths[i])).append(" | ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public String formatMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "(empty map)";
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String formatError(String error) {
        return "ERROR: " + error;
    }

    @Override
    public String formatSuccess(String message) {
        return "SUCCESS: " + message;
    }

    @Override
    public String getFormatType() {
        return "text";
    }

    private String padRight(String s, int n) {
        if (s == null) s = "";
        return String.format("%-" + n + "s", s);
    }
}
