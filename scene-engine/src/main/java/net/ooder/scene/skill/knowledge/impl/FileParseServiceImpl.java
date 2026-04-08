package net.ooder.scene.skill.knowledge.impl;

import net.ooder.scene.skill.knowledge.Document;
import net.ooder.scene.skill.knowledge.FileParseService;
import net.ooder.scene.skill.knowledge.FileParseException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 文件解析服务实现
 *
 * <p>支持多种文件格式的解析，包括 PDF、Word、Excel、Markdown、HTML 和纯文本。</p>
 *
 * <h3>支持的文件格式：</h3>
 * <ul>
 *   <li>PDF - application/pdf</li>
 *   <li>Word - application/msword, application/vnd.openxmlformats-officedocument.wordprocessingml.document</li>
 *   <li>Excel - application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet</li>
 *   <li>Markdown - com/markdown</li>
 *   <li>HTML - text/html</li>
 *   <li>纯文本 - text/plain</li>
 * </ul>
 *
 * @author ooder
 * @since 4.1.0
 */
@Service
public class FileParseServiceImpl implements FileParseService {

    private static final Logger log = LoggerFactory.getLogger(FileParseServiceImpl.class);

    private static final Map<String, String> EXTENSION_MIME_MAP = new HashMap<>();
    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>();
    private static final Set<String> SUPPORTED_MIME_TYPES = new HashSet<>();

    static {
        EXTENSION_MIME_MAP.put("pdf", "application/pdf");
        EXTENSION_MIME_MAP.put("doc", "application/msword");
        EXTENSION_MIME_MAP.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        EXTENSION_MIME_MAP.put("xls", "application/vnd.ms-excel");
        EXTENSION_MIME_MAP.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        EXTENSION_MIME_MAP.put("md", "text/markdown");
        EXTENSION_MIME_MAP.put("markdown", "text/markdown");
        EXTENSION_MIME_MAP.put("html", "text/html");
        EXTENSION_MIME_MAP.put("htm", "text/html");
        EXTENSION_MIME_MAP.put("txt", "text/plain");
        EXTENSION_MIME_MAP.put("csv", "text/csv");
        EXTENSION_MIME_MAP.put("json", "application/json");
        EXTENSION_MIME_MAP.put("xml", "application/xml");

        SUPPORTED_EXTENSIONS.addAll(EXTENSION_MIME_MAP.keySet());

        SUPPORTED_MIME_TYPES.add("application/pdf");
        SUPPORTED_MIME_TYPES.add("application/msword");
        SUPPORTED_MIME_TYPES.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        SUPPORTED_MIME_TYPES.add("application/vnd.ms-excel");
        SUPPORTED_MIME_TYPES.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        SUPPORTED_MIME_TYPES.add("text/markdown");
        SUPPORTED_MIME_TYPES.add("text/html");
        SUPPORTED_MIME_TYPES.add("text/plain");
        SUPPORTED_MIME_TYPES.add("text/csv");
        SUPPORTED_MIME_TYPES.add("application/json");
        SUPPORTED_MIME_TYPES.add("application/xml");
    }

    @Override
    public Document parse(InputStream input, String fileName) throws FileParseException {
        if (input == null) {
            throw new FileParseException(fileName, "Input stream cannot be null");
        }

        String extension = getExtension(fileName);
        String mimeType = getMimeType(fileName);

        log.info("Parsing file: {}, extension: {}, mimeType: {}", fileName, extension, mimeType);

        try {
            String content = parseContent(input, fileName, extension, mimeType);

            Document document = new Document();
            document.setDocId(generateDocId(fileName));
            document.setTitle(extractTitle(fileName, content));
            document.setContent(content);
            document.setType(extension != null ? extension.toLowerCase() : "unknown");
            document.setMimeType(mimeType);
            document.setSource(Document.SOURCE_UPLOAD);
            document.setFilePath(fileName);
            document.setCreatedAt(System.currentTimeMillis());
            document.setUpdatedAt(System.currentTimeMillis());
            document.setStatus(Document.STATUS_PROCESSING);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("originalFileName", fileName);
            metadata.put("parsedAt", System.currentTimeMillis());
            metadata.put("parserVersion", "4.1.0");
            document.setMetadata(metadata);

            log.info("File parsed successfully: {}, content length: {}", fileName, content.length());
            return document;

        } catch (FileParseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse file: {}", fileName, e);
            throw new FileParseException(fileName, mimeType, "Failed to parse file: " + e.getMessage(), e);
        }
    }

    @Override
    public Document parse(InputStream input, String fileName, String mimeType) throws FileParseException {
        if (input == null) {
            throw new FileParseException(fileName, "Input stream cannot be null");
        }

        log.info("Parsing file: {}, mimeType: {}", fileName, mimeType);

        try {
            String extension = getExtensionFromMimeType(mimeType);
            String content = parseContent(input, fileName, extension, mimeType);

            Document document = new Document();
            document.setDocId(generateDocId(fileName));
            document.setTitle(extractTitle(fileName, content));
            document.setContent(content);
            document.setType(extension != null ? extension.toLowerCase() : "unknown");
            document.setMimeType(mimeType);
            document.setSource(Document.SOURCE_UPLOAD);
            document.setFilePath(fileName);
            document.setCreatedAt(System.currentTimeMillis());
            document.setUpdatedAt(System.currentTimeMillis());
            document.setStatus(Document.STATUS_PROCESSING);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("originalFileName", fileName);
            metadata.put("parsedAt", System.currentTimeMillis());
            metadata.put("parserVersion", "4.1.0");
            document.setMetadata(metadata);

            return document;

        } catch (FileParseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse file: {}", fileName, e);
            throw new FileParseException(fileName, mimeType, "Failed to parse file: " + e.getMessage(), e);
        }
    }

    @Override
    public String parseToText(InputStream input, String fileName) throws FileParseException {
        Document doc = parse(input, fileName);
        return doc.getContent();
    }

    @Override
    public boolean supports(String fileName) {
        if (fileName == null) {
            return false;
        }
        String extension = getExtension(fileName);
        return extension != null && SUPPORTED_EXTENSIONS.contains(extension.toLowerCase());
    }

    @Override
    public boolean supportsMimeType(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        return SUPPORTED_MIME_TYPES.contains(mimeType.toLowerCase());
    }

    @Override
    public Set<String> getSupportedExtensions() {
        return Collections.unmodifiableSet(SUPPORTED_EXTENSIONS);
    }

    @Override
    public Set<String> getSupportedMimeTypes() {
        return Collections.unmodifiableSet(SUPPORTED_MIME_TYPES);
    }

    // ========== 私有方法 ==========

    private String parseContent(InputStream input, String fileName, String extension, String mimeType) 
            throws Exception {
        
        if (extension == null) {
            extension = getExtensionFromMimeType(mimeType);
        }

        if (extension == null) {
            return parseAsPlainText(input);
        }

        switch (extension.toLowerCase()) {
            case "pdf":
                return parsePdf(input);
            case "doc":
            case "docx":
                return parseWord(input);
            case "xls":
            case "xlsx":
                return parseExcel(input);
            case "md":
            case "markdown":
                return parseMarkdown(input);
            case "html":
            case "htm":
                return parseHtml(input);
            case "txt":
            case "csv":
            case "json":
            case "xml":
                return parseAsPlainText(input);
            default:
                log.warn("Unknown file extension: {}, trying plain text", extension);
                return parseAsPlainText(input);
        }
    }

    /**
     * 解析 PDF 文件
     */
    private String parsePdf(InputStream input) throws Exception {
        try (PDDocument document = Loader.loadPDF(input.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);
            return text.trim();
        }
    }

    /**
     * 解析 Word 文件
     */
    private String parseWord(InputStream input) throws Exception {
        StringBuilder content = new StringBuilder();
        
        try (XWPFDocument document = new XWPFDocument(input)) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text != null && !text.trim().isEmpty()) {
                    content.append(text.trim()).append("\n");
                }
            }
        }
        
        return content.toString().trim();
    }

    /**
     * 解析 Excel 文件
     */
    private String parseExcel(InputStream input) throws Exception {
        StringBuilder content = new StringBuilder();
        
        try (Workbook workbook = input.available() > 0 ? 
                (input.read() == 0x50 ? new XSSFWorkbook(input) : new HSSFWorkbook(input)) : 
                new XSSFWorkbook()) {
            
            DataFormatter formatter = new DataFormatter();
            
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                content.append("=== Sheet: ").append(sheet.getSheetName()).append(" ===\n");
                
                for (Row row : sheet) {
                    StringBuilder rowContent = new StringBuilder();
                    for (Cell cell : row) {
                        String cellValue = formatter.formatCellValue(cell);
                        if (cellValue != null && !cellValue.trim().isEmpty()) {
                            rowContent.append(cellValue.trim()).append("\t");
                        }
                    }
                    if (rowContent.length() > 0) {
                        content.append(rowContent.toString().trim()).append("\n");
                    }
                }
                content.append("\n");
            }
        }
        
        return content.toString().trim();
    }

    /**
     * 解析 Markdown 文件
     */
    private String parseMarkdown(InputStream input) throws Exception {
        String markdown = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        
        TextContentRenderer renderer = TextContentRenderer.builder().build();
        return renderer.render(document).trim();
    }

    /**
     * 解析 HTML 文件
     */
    private String parseHtml(InputStream input) throws Exception {
        String html = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        
        org.jsoup.nodes.Document doc = Jsoup.parse(html);
        
        doc.select("script").remove();
        doc.select("style").remove();
        doc.select("nav").remove();
        doc.select("footer").remove();
        
        String text = doc.text();
        return text.trim();
    }

    /**
     * 解析纯文本文件
     */
    private String parseAsPlainText(InputStream input) throws Exception {
        return new String(input.readAllBytes(), StandardCharsets.UTF_8).trim();
    }

    /**
     * 获取文件扩展名
     */
    private String getExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return null;
    }

    /**
     * 根据 MIME 类型获取扩展名
     */
    private String getExtensionFromMimeType(String mimeType) {
        if (mimeType == null) {
            return null;
        }
        
        switch (mimeType.toLowerCase()) {
            case "application/pdf":
                return "pdf";
            case "application/msword":
                return "doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return "docx";
            case "application/vnd.ms-excel":
                return "xls";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                return "xlsx";
            case "text/markdown":
                return "md";
            case "text/html":
                return "html";
            case "text/plain":
                return "txt";
            case "text/csv":
                return "csv";
            case "application/json":
                return "json";
            case "application/xml":
                return "xml";
            default:
                return null;
        }
    }

    /**
     * 获取 MIME 类型
     */
    private String getMimeType(String fileName) {
        String extension = getExtension(fileName);
        if (extension != null) {
            return EXTENSION_MIME_MAP.getOrDefault(extension.toLowerCase(), "application/octet-stream");
        }
        return "application/octet-stream";
    }

    /**
     * 生成文档 ID
     */
    private String generateDocId(String fileName) {
        return "doc-" + System.currentTimeMillis() + "-" + 
               Integer.toHexString(fileName != null ? fileName.hashCode() : new Random().nextInt());
    }

    /**
     * 提取文档标题
     */
    private String extractTitle(String fileName, String content) {
        if (fileName != null) {
            int lastSlash = fileName.lastIndexOf('/');
            int lastBackslash = fileName.lastIndexOf('\\');
            int lastIndex = Math.max(lastSlash, lastBackslash);
            
            String name = lastIndex >= 0 ? fileName.substring(lastIndex + 1) : fileName;
            
            int lastDot = name.lastIndexOf('.');
            if (lastDot > 0) {
                name = name.substring(0, lastDot);
            }
            
            return name;
        }
        
        if (content != null && content.length() > 0) {
            int firstNewline = content.indexOf('\n');
            if (firstNewline > 0 && firstNewline < 100) {
                return content.substring(0, firstNewline).trim();
            }
            if (content.length() > 50) {
                return content.substring(0, 50).trim() + "...";
            }
            return content.trim();
        }
        
        return "Untitled Document";
    }
}
