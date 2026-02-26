package net.ooder.sdk.core.capability.loader;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CapMarkdownLoader {
    
    private static final Logger log = LoggerFactory.getLogger(CapMarkdownLoader.class);
    
    private static final Pattern SECTION_PATTERN = Pattern.compile("^##\\s+(\\S+)");
    private static final Pattern TABLE_ROW_PATTERN = Pattern.compile("^\\|\\s*([^|]+)\\s*\\|\\s*([^|]+)\\s*\\|");
    private static final Pattern CODE_BLOCK_START = Pattern.compile("^```(\\w+)$");
    private static final Pattern CODE_BLOCK_END = Pattern.compile("^```$");
    
    public CapDocumentation load(Path mdPath) throws IOException {
        log.debug("Loading CAP documentation from file: {}", mdPath);
        try (InputStream is = Files.newInputStream(mdPath)) {
            return load(is);
        }
    }
    
    public CapDocumentation load(InputStream inputStream) throws IOException {
        log.debug("Loading CAP documentation from input stream");
        
        CapDocumentation doc = new CapDocumentation();
        StringBuilder currentSection = new StringBuilder();
        String currentSectionTitle = null;
        List<String> currentSectionLines = new ArrayList<>();
        boolean inCodeBlock = false;
        String codeLanguage = null;
        List<String> codeLines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher codeStartMatcher = CODE_BLOCK_START.matcher(line);
                Matcher codeEndMatcher = CODE_BLOCK_END.matcher(line);
                
                if (codeStartMatcher.matches()) {
                    inCodeBlock = true;
                    codeLanguage = codeStartMatcher.group(1);
                    codeLines.clear();
                    continue;
                }
                
                if (codeEndMatcher.matches() && inCodeBlock) {
                    inCodeBlock = false;
                    String codeContent = String.join("\n", codeLines);
                    doc.addCodeBlock(currentSectionTitle, codeLanguage, codeContent);
                    continue;
                }
                
                if (inCodeBlock) {
                    codeLines.add(line);
                    continue;
                }
                
                Matcher sectionMatcher = SECTION_PATTERN.matcher(line);
                if (sectionMatcher.matches()) {
                    if (currentSectionTitle != null) {
                        doc.addSection(currentSectionTitle, currentSection.toString().trim());
                        currentSection = new StringBuilder();
                    }
                    currentSectionTitle = sectionMatcher.group(1);
                    currentSectionLines.clear();
                } else {
                    if (currentSectionTitle != null) {
                        if (currentSectionLines.size() > 0 || !line.trim().isEmpty()) {
                            currentSection.append(line).append("\n");
                            currentSectionLines.add(line);
                        }
                    } else {
                        if (line.startsWith("# ")) {
                            doc.setTitle(line.substring(2).trim());
                        } else if (line.startsWith("| ")) {
                            parseTableRow(doc, line);
                        }
                    }
                }
            }
        }
        
        if (currentSectionTitle != null) {
            doc.addSection(currentSectionTitle, currentSection.toString().trim());
        }
        
        return doc;
    }
    
    private void parseTableRow(CapDocumentation doc, String line) {
        Matcher matcher = TABLE_ROW_PATTERN.matcher(line);
        if (matcher.matches()) {
            String key = matcher.group(1).trim();
            String value = matcher.group(2).trim();
            doc.addMetadata(key, value);
        }
    }
    
    public static class CapDocumentation {
        private String title;
        private Map<String, String> metadata = new HashMap<>();
        private Map<String, String> sections = new HashMap<>();
        private List<CodeBlock> codeBlocks = new ArrayList<>();
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public Map<String, String> getMetadata() { return metadata; }
        public void addMetadata(String key, String value) {
            metadata.put(key, value);
        }
        
        public Map<String, String> getSections() { return sections; }
        public void addSection(String title, String content) {
            sections.put(title, content);
        }
        
        public String getSection(String title) {
            return sections.get(title);
        }
        
        public List<CodeBlock> getCodeBlocks() { return codeBlocks; }
        public void addCodeBlock(String section, String language, String content) {
            codeBlocks.add(new CodeBlock(section, language, content));
        }
        
        public List<CodeBlock> getCodeBlocksBySection(String section) {
            List<CodeBlock> result = new ArrayList<>();
            for (CodeBlock block : codeBlocks) {
                if (section.equals(block.getSection())) {
                    result.add(block);
                }
            }
            return result;
        }
    }
    
    public static class CodeBlock {
        private final String section;
        private final String language;
        private final String content;
        
        public CodeBlock(String section, String language, String content) {
            this.section = section;
            this.language = language;
            this.content = content;
        }
        
        public String getSection() { return section; }
        public String getLanguage() { return language; }
        public String getContent() { return content; }
    }
}
