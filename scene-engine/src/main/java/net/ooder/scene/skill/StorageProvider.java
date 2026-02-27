package net.ooder.scene.skill;

import java.util.List;
import java.util.Map;

/**
 * Storage Provider 接口
 *
 * <p>定义存储能力接口，由 Skills Team 实现</p>
 * <p>实现类通过 ServiceLoader 注册</p>
 */
public interface StorageProvider {

    /**
     * 获取提供者类型
     * @return 如 "minio", "s3", "nas", "local", "mock"
     */
    String getProviderType();

    /**
     * 读取文件
     * @param filePath 文件路径
     * @return 文件内容
     */
    byte[] readFile(String filePath);

    /**
     * 写入文件
     * @param filePath 文件路径
     * @param content 文件内容
     * @param overwrite 是否覆盖
     * @return 是否成功
     */
    boolean writeFile(String filePath, byte[] content, boolean overwrite);

    /**
     * 删除文件
     * @param filePath 文件路径
     * @param recursive 是否递归删除
     * @return 是否成功
     */
    boolean deleteFile(String filePath, boolean recursive);

    /**
     * 列出文件
     * @param directoryPath 目录路径
     * @param pattern 文件模式（可选）
     * @param recursive 是否递归
     * @return 文件列表
     */
    List<FileInfo> listFiles(String directoryPath, String pattern, boolean recursive);

    /**
     * 检查文件是否存在
     * @param filePath 文件路径
     * @return 是否存在
     */
    boolean fileExists(String filePath);

    /**
     * 创建目录
     * @param directoryPath 目录路径
     * @return 是否成功
     */
    boolean createDirectory(String directoryPath);

    /**
     * 获取文件信息
     * @param filePath 文件路径
     * @return 文件信息
     */
    FileInfo getFileInfo(String filePath);

    /**
     * 复制文件
     * @param sourcePath 源路径
     * @param targetPath 目标路径
     * @return 是否成功
     */
    boolean copyFile(String sourcePath, String targetPath);

    /**
     * 移动文件
     * @param sourcePath 源路径
     * @param targetPath 目标路径
     * @return 是否成功
     */
    boolean moveFile(String sourcePath, String targetPath);

    /**
     * 获取存储配额
     * @return 配额信息
     */
    StorageQuota getQuota();

    /**
     * 文件信息
     */
    class FileInfo {
        private String path;
        private String name;
        private long size;
        private boolean directory;
        private long lastModified;
        private String contentType;
        private Map<String, String> metadata;

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        public boolean isDirectory() { return directory; }
        public void setDirectory(boolean directory) { this.directory = directory; }
        public long getLastModified() { return lastModified; }
        public void setLastModified(long lastModified) { this.lastModified = lastModified; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        public Map<String, String> getMetadata() { return metadata; }
        public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    }

    /**
     * 存储配额
     */
    class StorageQuota {
        private long totalSpace;
        private long usedSpace;
        private long availableSpace;
        private int fileCount;

        public long getTotalSpace() { return totalSpace; }
        public void setTotalSpace(long totalSpace) { this.totalSpace = totalSpace; }
        public long getUsedSpace() { return usedSpace; }
        public void setUsedSpace(long usedSpace) { this.usedSpace = usedSpace; }
        public long getAvailableSpace() { return availableSpace; }
        public void setAvailableSpace(long availableSpace) { this.availableSpace = availableSpace; }
        public int getFileCount() { return fileCount; }
        public void setFileCount(int fileCount) { this.fileCount = fileCount; }
    }
}
