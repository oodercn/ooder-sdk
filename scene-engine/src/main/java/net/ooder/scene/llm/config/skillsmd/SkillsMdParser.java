package net.ooder.scene.llm.config.skillsmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SKILLS.MD 解析器
 *
 * <p>解析 SKILLS.MD 文件，提取：</p>
 * <ul>
 *   <li>技能元信息（名称、版本、描述）</li>
 *   <li>Capability 定义</li>
 *   <li>参数类型信息</li>
 *   <li>配置建议</li>
 *   <li>知识库路径</li>
 * </ul>
 *
 * @author ooder
 * @since 2.4
 */
public class SkillsMdParser {

    private static final Logger log = LoggerFactory.getLogger(SkillsMdParser.class);

    private static final Pattern VERSION_PATTERN = Pattern.compile("版本:\\s*([\\d.]+)");
    private static final Pattern AUTHOR_PATTERN = Pattern.compile("作者:\\s*([^|]+)");
    private static final Pattern DATE_PATTERN = Pattern.compile("更新日期:\\s*(\\d{4}-\\d{2}-\\d{2})");
    private static final Pattern CAPABILITY_PATTERN = Pattern.compile("###\\s*(\\S+):\\s*(.+)");
    private static final Pattern PARAM_PATTERN = Pattern.compile("`(\\w+)`\\s*\\(([^)]+)\\)(?:,\\s*(\\w+))?:\\s*(.+)");

    /**
     * 解析 SKILLS.MD 文件
     */
    public SkillsMdDocument parse(Path skillsMdPath) throws IOException {
        String content = readFile(skillsMdPath);
        SkillsMdDocument doc = parse(content);
        doc.setSourcePath(skillsMdPath);
        return doc;
    }

    /**
     * 解析 SKILLS.MD 内容
     */
    public SkillsMdDocument parse(String content) {
        SkillsMdDocument doc = new SkillsMdDocument();

        parseMetadata(content, doc);
        parseOverview(content, doc);
        parseCapabilities(content, doc);
        parseScenarios(content, doc);
        parseKnowledgePaths(content, doc);
        parseConfigSuggestions(content, doc);

        return doc;
    }

    private String readFile(Path path) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } finally {
            reader.close();
        }
        return content.toString();
    }

    private void parseMetadata(String content, SkillsMdDocument doc) {
        Matcher versionMatcher = VERSION_PATTERN.matcher(content);
        if (versionMatcher.find()) {
            doc.setVersion(versionMatcher.group(1));
        }

        Matcher authorMatcher = AUTHOR_PATTERN.matcher(content);
        if (authorMatcher.find()) {
            doc.setAuthor(authorMatcher.group(1).trim());
        }

        Matcher dateMatcher = DATE_PATTERN.matcher(content);
        if (dateMatcher.find()) {
            doc.setUpdateDate(dateMatcher.group(1));
        }

        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.startsWith("# ") && !line.startsWith("## ")) {
                doc.setName(line.substring(2).trim());
                break;
            }
        }
    }

    private void parseOverview(String content, SkillsMdDocument doc) {
        int start = content.indexOf("## 概述");
        if (start == -1) {
            start = content.indexOf("## 简介");
        }
        if (start == -1) return;

        int end = content.indexOf("## ", start + 4);
        if (end == -1) end = content.length();

        String overview = content.substring(start + 4, end).trim();
        doc.setOverview(overview);
    }

    private void parseCapabilities(String content, SkillsMdDocument doc) {
        int start = content.indexOf("## 能力列表");
        if (start == -1) {
            start = content.indexOf("## Capabilities");
        }
        if (start == -1) return;

        int end = content.indexOf("## ", start + 6);
        if (end == -1) end = content.length();

        String section = content.substring(start, end);
        String[] blocks = section.split("### ");

        for (String block : blocks) {
            if (block.trim().isEmpty()) continue;

            CapabilityDefinition cap = parseCapabilityBlock(block);
            if (cap != null) {
                doc.addCapability(cap);
            }
        }
    }

    private CapabilityDefinition parseCapabilityBlock(String block) {
        String[] lines = block.split("\n");
        if (lines.length < 2) return null;

        CapabilityDefinition cap = new CapabilityDefinition();

        String[] header = lines[0].split(":", 2);
        cap.setId(header[0].trim());
        if (header.length > 1) {
            cap.setName(header[1].trim());
        }

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.startsWith("- **名称**:")) {
                cap.setName(line.substring(7).trim());
            } else if (line.startsWith("- **描述**:")) {
                cap.setDescription(line.substring(7).trim());
            } else if (line.startsWith("- **输入参数**:")) {
                for (int j = i + 1; j < lines.length; j++) {
                    String paramLine = lines[j].trim();
                    if (!paramLine.startsWith("- `")) break;

                    ParameterDefinition param = parseParameter(paramLine);
                    if (param != null) {
                        cap.addParameter(param);
                    }
                }
            } else if (line.startsWith("- **输出**:")) {
                cap.setOutput(line.substring(7).trim());
            }
        }

        return cap;
    }

    private ParameterDefinition parseParameter(String line) {
        Matcher matcher = PARAM_PATTERN.matcher(line);
        if (!matcher.find()) return null;

        ParameterDefinition param = new ParameterDefinition();
        param.setName(matcher.group(1));
        param.setType(matcher.group(2));
        param.setRequired("required".equals(matcher.group(3)));
        param.setDescription(matcher.group(4));

        if (param.getType().startsWith("enum[")) {
            String enumStr = param.getType().substring(5, param.getType().length() - 1);
            param.setEnumValues(Arrays.asList(enumStr.split(",\\s*")));
            param.setType("string");
        }

        return param;
    }

    private void parseScenarios(String content, SkillsMdDocument doc) {
        int start = content.indexOf("## 使用场景");
        if (start == -1) {
            start = content.indexOf("## 场景");
        }
        if (start == -1) return;

        int end = content.indexOf("## ", start + 6);
        if (end == -1) end = content.length();

        String section = content.substring(start, end);
        String[] blocks = section.split("### ");

        for (String block : blocks) {
            if (block.trim().isEmpty()) continue;

            String[] lines = block.split("\n", 2);
            if (lines.length >= 2) {
                ScenarioDefinition scenario = new ScenarioDefinition();
                scenario.setName(lines[0].trim());
                scenario.setDescription(lines[1].trim());
                doc.addScenario(scenario);
            }
        }
    }

    private void parseKnowledgePaths(String content, SkillsMdDocument doc) {
        int start = content.indexOf("## 知识库");
        if (start == -1) {
            start = content.indexOf("## Knowledge");
        }
        if (start == -1) return;

        int end = content.indexOf("## ", start + 5);
        if (end == -1) end = content.length();

        String section = content.substring(start, end);

        Pattern pathPattern = Pattern.compile("\\(([^)]+\\.md)\\)");
        Matcher matcher = pathPattern.matcher(section);

        while (matcher.find()) {
            doc.addKnowledgePath(matcher.group(1));
        }
    }

    private void parseConfigSuggestions(String content, SkillsMdDocument doc) {
        int start = content.indexOf("## 配置建议");
        if (start == -1) {
            start = content.indexOf("## Config");
        }
        if (start == -1) return;

        int end = content.indexOf("## ", start + 6);
        if (end == -1) end = content.length();

        String section = content.substring(start, end);

        Pattern modelPattern = Pattern.compile("推荐模型:\\s*(\\S+)");
        Matcher modelMatcher = modelPattern.matcher(section);
        if (modelMatcher.find()) {
            doc.setSuggestedModel(modelMatcher.group(1));
        }

        Pattern tempPattern = Pattern.compile("Temperature:\\s*([\\d.]+)");
        Matcher tempMatcher = tempPattern.matcher(section);
        if (tempMatcher.find()) {
            doc.setSuggestedTemperature(Double.parseDouble(tempMatcher.group(1)));
        }

        Pattern tokensPattern = Pattern.compile("MaxTokens:\\s*(\\d+)");
        Matcher tokensMatcher = tokensPattern.matcher(section);
        if (tokensMatcher.find()) {
            doc.setSuggestedMaxTokens(Integer.parseInt(tokensMatcher.group(1)));
        }
    }
}
