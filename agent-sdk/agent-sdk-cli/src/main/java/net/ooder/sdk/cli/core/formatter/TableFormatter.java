package net.ooder.sdk.cli.core.formatter;

import net.ooder.sdk.cli.api.CliFormatter;

import java.util.List;
import java.util.Map;

/**
 * 表格格式格式化器
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class TableFormatter implements CliFormatter {

    @Override
    public String format(Object data) {
        if (data == null) {
            return "";
        }

        if (data instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            return formatMapAsTable(map);
        }

        return data.toString();
    }

    @Override
    public String formatList(List<?> list) {
        if (list == null || list.isEmpty()) {
            return "(empty list)";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("┌").append("─".repeat(50)).append("┐\n");

        for (int i = 0; i < list.size(); i++) {
            String item = String.valueOf(list.get(i));
            sb.append("│ ").append(i + 1).append(". ")
              .append(truncate(item, 44)).append(" │\n");
        }

        sb.append("└").append("─".repeat(50)).append("┘");
        return sb.toString();
    }

    @Override
    public String formatTable(String[] headers, List<String[]> rows) {
        if (headers == null || rows == null) {
            return "";
        }

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

        // 确保最小宽度
        for (int i = 0; i < widths.length; i++) {
            widths[i] = Math.max(widths[i], 10);
        }

        StringBuilder sb = new StringBuilder();

        // 顶边框
        sb.append("┌");
        for (int i = 0; i < widths.length; i++) {
            sb.append("─".repeat(widths[i] + 2));
            if (i < widths.length - 1) sb.append("┬");
        }
        sb.append("┐\n");

        // 表头
        sb.append("│");
        for (int i = 0; i < headers.length; i++) {
            sb.append(" ").append(padRight(headers[i], widths[i])).append(" │");
        }
        sb.append("\n");

        // 分隔线
        sb.append("├");
        for (int i = 0; i < widths.length; i++) {
            sb.append("─".repeat(widths[i] + 2));
            if (i < widths.length - 1) sb.append("┼");
        }
        sb.append("┤\n");

        // 数据行
        for (String[] row : rows) {
            sb.append("│");
            for (int i = 0; i < headers.length; i++) {
                String cell = i < row.length && row[i] != null ? row[i] : "";
                sb.append(" ").append(padRight(cell, widths[i])).append(" │");
            }
            sb.append("\n");
        }

        // 底边框
        sb.append("└");
        for (int i = 0; i < widths.length; i++) {
            sb.append("─".repeat(widths[i] + 2));
            if (i < widths.length - 1) sb.append("┴");
        }
        sb.append("┘");

        return sb.toString();
    }

    @Override
    public String formatMap(Map<String, Object> map) {
        return formatMapAsTable(map);
    }

    @Override
    public String formatError(String error) {
        return "┌─────────────────────────────────────────────────┐\n"
             + "│ ERROR: " + padRight(error, 40) + "│\n"
             + "└─────────────────────────────────────────────────┘";
    }

    @Override
    public String formatSuccess(String message) {
        return "┌─────────────────────────────────────────────────┐\n"
             + "│ SUCCESS: " + padRight(message, 37) + "│\n"
             + "└─────────────────────────────────────────────────┘";
    }

    @Override
    public String getFormatType() {
        return "table";
    }

    private String formatMapAsTable(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "(empty)";
        }

        // 计算键的最大长度
        int maxKeyLength = map.keySet().stream()
                .mapToInt(String::length)
                .max()
                .orElse(10);
        maxKeyLength = Math.max(maxKeyLength, 10);

        StringBuilder sb = new StringBuilder();
        sb.append("┌").append("─".repeat(maxKeyLength + 2))
          .append("┬").append("─".repeat(40)).append("┐\n");

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());
            sb.append("│ ").append(padRight(key, maxKeyLength))
              .append(" │ ").append(truncate(value, 38)).append(" │\n");
        }

        sb.append("└").append("─".repeat(maxKeyLength + 2))
          .append("┴").append("─".repeat(40)).append("┘");

        return sb.toString();
    }

    private String padRight(String s, int n) {
        if (s == null) s = "";
        if (s.length() > n) {
            return s.substring(0, n - 3) + "...";
        }
        return String.format("%-" + n + "s", s);
    }

    private String truncate(String s, int n) {
        if (s == null) return "";
        if (s.length() > n) {
            return s.substring(0, n - 3) + "...";
        }
        return s;
    }
}
