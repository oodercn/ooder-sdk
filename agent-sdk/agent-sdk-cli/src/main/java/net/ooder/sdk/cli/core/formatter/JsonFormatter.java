package net.ooder.sdk.cli.core.formatter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import net.ooder.sdk.cli.api.CliFormatter;

import java.util.List;
import java.util.Map;

/**
 * JSON格式格式化器
 *
 * @author Agent-SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class JsonFormatter implements CliFormatter {

    @Override
    public String format(Object data) {
        if (data == null) {
            return "{}";
        }
        return JSON.toJSONString(data, JSONWriter.Feature.PrettyFormat);
    }

    @Override
    public String formatList(List<?> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        return JSON.toJSONString(list, JSONWriter.Feature.PrettyFormat);
    }

    @Override
    public String formatTable(String[] headers, List<String[]> rows) {
        // JSON格式下表格转为对象数组
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            sb.append("  {");

            for (int j = 0; j < headers.length && j < row.length; j++) {
                if (j > 0) sb.append(", ");
                sb.append("\"").append(headers[j]).append("\": ");
                sb.append("\"").append(escapeJson(row[j])).append("\"");
            }

            sb.append("}");
            if (i < rows.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("]");
        return sb.toString();
    }

    @Override
    public String formatMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        return JSON.toJSONString(map, JSONWriter.Feature.PrettyFormat);
    }

    @Override
    public String formatError(String error) {
        return JSON.toJSONString(Map.of("error", error), JSONWriter.Feature.PrettyFormat);
    }

    @Override
    public String formatSuccess(String message) {
        return JSON.toJSONString(Map.of("success", true, "message", message), JSONWriter.Feature.PrettyFormat);
    }

    @Override
    public String getFormatType() {
        return "json";
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
