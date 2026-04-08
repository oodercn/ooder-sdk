package net.ooder.scene.skill.knowledge;

import java.io.InputStream;
import java.util.Set;

/**
 * 文件解析服务接口
 *
 * <p>负责将各种格式的文件解析为文本内容。</p>
 *
 * <h3>支持的文件格式：</h3>
 * <ul>
 *   <li>PDF - application/pdf</li>
 *   <li>Word - application/msword, application/vnd.openxmlformats-officedocument.wordprocessingml.document</li>
 *   <li>Excel - application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet</li>
 *   <li>Markdown - text/markdown</li>
 *   <li>HTML - text/html</li>
 *   <li>纯文本 - text/plain</li>
 * </ul>
 *
 * @author ooder
 * @since 3.1.0
 */
public interface FileParseService {

    /**
     * 解析文件内容
     *
     * @param input 文件输入流
     * @param fileName 文件名（用于判断文件类型）
     * @return 解析后的文档对象
     * @throws FileParseException 解析失败时抛出
     */
    Document parse(InputStream input, String fileName) throws FileParseException;

    /**
     * 解析文件内容并指定 MIME 类型
     *
     * @param input 文件输入流
     * @param fileName 文件名
     * @param mimeType MIME 类型
     * @return 解析后的文档对象
     * @throws FileParseException 解析失败时抛出
     */
    Document parse(InputStream input, String fileName, String mimeType) throws FileParseException;

    /**
     * 解析文件内容为纯文本
     *
     * @param input 文件输入流
     * @param fileName 文件名
     * @return 解析后的文本内容
     * @throws FileParseException 解析失败时抛出
     */
    String parseToText(InputStream input, String fileName) throws FileParseException;

    /**
     * 检查是否支持指定文件类型
     *
     * @param fileName 文件名
     * @return 是否支持
     */
    boolean supports(String fileName);

    /**
     * 检查是否支持指定 MIME 类型
     *
     * @param mimeType MIME 类型
     * @return 是否支持
     */
    boolean supportsMimeType(String mimeType);

    /**
     * 获取支持的文件扩展名
     *
     * @return 支持的扩展名集合
     */
    Set<String> getSupportedExtensions();

    /**
     * 获取支持的 MIME 类型
     *
     * @return 支持的 MIME 类型集合
     */
    Set<String> getSupportedMimeTypes();
}
