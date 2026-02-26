package net.ooder.sdk.skill.impl;

import net.ooder.sdk.skill.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkillMdParserImpl implements SkillMdParser {

    private static final Pattern HEADER_PATTERN = Pattern.compile("^(#{1,6})\\s*(.+)$");
    private static final Pattern METADATA_PATTERN = Pattern.compile("^@([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*(.+)$");
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("^\\s*[-*]\\s*`?([^`:`]+)`?\\s*:\\s*(\\w+)\\s*-?\\s*(.*)$");
    private static final Pattern EXAMPLE_PATTERN = Pattern.compile("^\\s*###\\s*(.+)$");
    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("```(\\w*)\\n(.*?)```", Pattern.DOTALL);

    @Override
    public SkillMdDocument parse(String markdownContent) {
        if (markdownContent == null || markdownContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Markdown content cannot be null or empty");
        }

        SkillMdDocument document = new SkillMdDocument();
        String[] lines = markdownContent.split("\\r?\\n");

        parseMetadata(document, lines);
        parseSections(document, lines);
        parseInputs(document, lines);
        parseOutputs(document, lines);
        parseExamples(document, lines);
        parseExecutor(document, lines);

        if (document.getSkillId() == null) {
            document.setSkillId(generateSkillId(document.getName()));
        }

        return document;
    }

    @Override
    public SkillMdDocument parseFile(String filePath) {
        try {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            SkillMdDocument document = parse(content.toString());
            document.setMetadataItem("sourceFile", filePath);
            return document;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse SKILL.md file: " + filePath, e);
        }
    }

    @Override
    public boolean validate(String markdownContent) {
        try {
            SkillMdDocument document = parse(markdownContent);
            return document.getName() != null && !document.getName().isEmpty()
                && document.getDescription() != null && !document.getDescription().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<String> extractSections(String markdownContent) {
        List<String> sections = new ArrayList<>();
        String[] lines = markdownContent.split("\\r?\\n");

        for (String line : lines) {
            Matcher matcher = HEADER_PATTERN.matcher(line.trim());
            if (matcher.matches()) {
                sections.add(matcher.group(2).trim());
            }
        }

        return sections;
    }

    @Override
    public Map<String, String> extractMetadata(String markdownContent) {
        Map<String, String> metadata = new HashMap<>();
        String[] lines = markdownContent.split("\\r?\\n");

        for (String line : lines) {
            Matcher matcher = METADATA_PATTERN.matcher(line.trim());
            if (matcher.matches()) {
                metadata.put(matcher.group(1), matcher.group(2).trim());
            }
        }

        return metadata;
    }

    @Override
    public List<SkillMdSection> parseSections(String markdownContent) {
        List<SkillMdSection> sections = new ArrayList<>();
        String[] lines = markdownContent.split("\\r?\\n");

        SkillMdSection currentSection = null;
        StringBuilder contentBuilder = new StringBuilder();
        int startLine = 0;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            Matcher matcher = HEADER_PATTERN.matcher(line.trim());

            if (matcher.matches()) {
                if (currentSection != null) {
                    currentSection.setContent(contentBuilder.toString().trim());
                    currentSection.setEndLine(i - 1);
                    sections.add(currentSection);
                }

                currentSection = new SkillMdSection();
                currentSection.setTitle(matcher.group(2).trim());
                currentSection.setLevel(matcher.group(1).length());
                currentSection.setStartLine(i);
                contentBuilder = new StringBuilder();
                startLine = i;
            } else if (currentSection != null) {
                contentBuilder.append(line).append("\n");
            }
        }

        if (currentSection != null) {
            currentSection.setContent(contentBuilder.toString().trim());
            currentSection.setEndLine(lines.length - 1);
            sections.add(currentSection);
        }

        return sections;
    }

    private void parseMetadata(SkillMdDocument document, String[] lines) {
        for (String line : lines) {
            String trimmed = line.trim();

            if (trimmed.startsWith("# ")) {
                document.setName(trimmed.substring(2).trim());
            } else if (trimmed.startsWith("@id:")) {
                document.setSkillId(trimmed.substring(4).trim());
            } else if (trimmed.startsWith("@version:")) {
                document.setVersion(trimmed.substring(9).trim());
            } else if (trimmed.startsWith("@author:")) {
                document.setAuthor(trimmed.substring(8).trim());
            } else if (trimmed.startsWith("@domain:")) {
                document.setDomain(trimmed.substring(8).trim());
            } else if (trimmed.startsWith("@tags:")) {
                String tagsStr = trimmed.substring(6).trim();
                document.setTags(Arrays.asList(tagsStr.split("\\s*,\\s*")));
            } else {
                Matcher matcher = METADATA_PATTERN.matcher(trimmed);
                if (matcher.matches()) {
                    document.setMetadataItem(matcher.group(1), matcher.group(2).trim());
                }
            }
        }
    }

    private void parseSections(SkillMdDocument document, String[] lines) {
        document.setSections(parseSections(String.join("\n", lines)));

        for (SkillMdSection section : document.getSections()) {
            if (section.getTitle().equalsIgnoreCase("description") ||
                section.getTitle().equalsIgnoreCase("overview")) {
                document.setDescription(section.getContent());
            }
        }
    }

    private void parseInputs(SkillMdDocument document, String[] lines) {
        List<SkillMdParameter> inputs = new ArrayList<>();
        boolean inInputsSection = false;

        for (String line : lines) {
            String trimmed = line.trim();

            if (trimmed.matches("^##\\s*(?i)inputs?$")) {
                inInputsSection = true;
                continue;
            }

            if (trimmed.startsWith("## ") && inInputsSection) {
                break;
            }

            if (inInputsSection) {
                Matcher matcher = PARAMETER_PATTERN.matcher(trimmed);
                if (matcher.matches()) {
                    SkillMdParameter param = new SkillMdParameter();
                    param.setName(matcher.group(1).trim());
                    param.setType(matcher.group(2).trim());
                    param.setDescription(matcher.group(3).trim());
                    param.setRequired(!trimmed.toLowerCase().contains("optional"));
                    inputs.add(param);
                }
            }
        }

        document.setInputs(inputs);
    }

    private void parseOutputs(SkillMdDocument document, String[] lines) {
        List<SkillMdParameter> outputs = new ArrayList<>();
        boolean inOutputsSection = false;

        for (String line : lines) {
            String trimmed = line.trim();

            if (trimmed.matches("^##\\s*(?i)outputs?$")) {
                inOutputsSection = true;
                continue;
            }

            if (trimmed.startsWith("## ") && inOutputsSection) {
                break;
            }

            if (inOutputsSection) {
                Matcher matcher = PARAMETER_PATTERN.matcher(trimmed);
                if (matcher.matches()) {
                    SkillMdParameter param = new SkillMdParameter();
                    param.setName(matcher.group(1).trim());
                    param.setType(matcher.group(2).trim());
                    param.setDescription(matcher.group(3).trim());
                    outputs.add(param);
                }
            }
        }

        document.setOutputs(outputs);
    }

    private void parseExamples(SkillMdDocument document, String[] lines) {
        List<SkillMdExample> examples = new ArrayList<>();
        boolean inExamplesSection = false;
        SkillMdExample currentExample = null;

        for (int i = 0; i < lines.length; i++) {
            String trimmed = lines[i].trim();

            if (trimmed.matches("^##\\s*(?i)examples?$")) {
                inExamplesSection = true;
                continue;
            }

            if (trimmed.startsWith("## ") && inExamplesSection) {
                break;
            }

            if (inExamplesSection) {
                if (trimmed.startsWith("### ")) {
                    if (currentExample != null) {
                        examples.add(currentExample);
                    }
                    currentExample = new SkillMdExample();
                    currentExample.setName(trimmed.substring(4).trim());
                } else if (currentExample != null) {
                    if (trimmed.toLowerCase().startsWith("input:")) {
                        currentExample.setInput(parseExampleData(lines, i + 1));
                    } else if (trimmed.toLowerCase().startsWith("output:")) {
                        currentExample.setOutput(parseExampleData(lines, i + 1));
                    } else if (!trimmed.isEmpty() && currentExample.getDescription() == null) {
                        currentExample.setDescription(trimmed);
                    }
                }
            }
        }

        if (currentExample != null) {
            examples.add(currentExample);
        }

        document.setExamples(examples);
    }

    private void parseExecutor(SkillMdDocument document, String[] lines) {
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("@executor:")) {
                document.setExecutorClass(trimmed.substring(10).trim());
                break;
            }
        }
    }

    private Map<String, Object> parseExampleData(String[] lines, int startIndex) {
        Map<String, Object> data = new HashMap<>();
        StringBuilder jsonBuilder = new StringBuilder();
        boolean inCodeBlock = false;

        for (int i = startIndex; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = line.trim();

            if (trimmed.startsWith("```")) {
                if (inCodeBlock) {
                    break;
                } else {
                    inCodeBlock = true;
                    continue;
                }
            }

            if (inCodeBlock) {
                jsonBuilder.append(line).append("\n");
            } else if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.toLowerCase().startsWith("output:")) {
                break;
            }
        }

        String json = jsonBuilder.toString().trim();
        if (!json.isEmpty()) {
            try {
                data = parseSimpleJson(json);
            } catch (Exception e) {
                data.put("raw", json);
            }
        }

        return data;
    }

    private Map<String, Object> parseSimpleJson(String json) {
        Map<String, Object> result = new HashMap<>();

        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1).trim();
        }

        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                String key = kv[0].trim().replace("\"", "").replace("'", "");
                String value = kv[1].trim();

                if (value.startsWith("\"") && value.endsWith("\"")) {
                    result.put(key, value.substring(1, value.length() - 1));
                } else if (value.equalsIgnoreCase("true")) {
                    result.put(key, true);
                } else if (value.equalsIgnoreCase("false")) {
                    result.put(key, false);
                } else if (value.equalsIgnoreCase("null")) {
                    result.put(key, null);
                } else {
                    try {
                        if (value.contains(".")) {
                            result.put(key, Double.parseDouble(value));
                        } else {
                            result.put(key, Long.parseLong(value));
                        }
                    } catch (NumberFormatException e) {
                        result.put(key, value);
                    }
                }
            }
        }

        return result;
    }

    private String generateSkillId(String name) {
        if (name == null) {
            return "skill-" + System.currentTimeMillis();
        }
        return name.toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9-]", "");
    }
}
