package net.ooder.scene.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mock Storage Provider
 *
 * <p>默认的 Mock 实现，用于测试和开发</p>
 */
public class MockStorageProvider implements StorageProvider {

    @Override
    public String getProviderType() {
        return "mock";
    }

    @Override
    public byte[] readFile(String filePath) {
        return ("Mock content for: " + filePath).getBytes();
    }

    @Override
    public boolean writeFile(String filePath, byte[] content, boolean overwrite) {
        return true;
    }

    @Override
    public boolean deleteFile(String filePath, boolean recursive) {
        return true;
    }

    @Override
    public List<FileInfo> listFiles(String directoryPath, String pattern, boolean recursive) {
        List<FileInfo> result = new ArrayList<FileInfo>();
        FileInfo info = new FileInfo();
        info.setPath(directoryPath + "/mock-file.txt");
        info.setName("mock-file.txt");
        info.setSize(1024);
        info.setDirectory(false);
        result.add(info);
        return result;
    }

    @Override
    public boolean fileExists(String filePath) {
        return true;
    }

    @Override
    public boolean createDirectory(String directoryPath) {
        return true;
    }

    @Override
    public FileInfo getFileInfo(String filePath) {
        FileInfo info = new FileInfo();
        info.setPath(filePath);
        info.setName(filePath.substring(filePath.lastIndexOf('/') + 1));
        info.setSize(1024);
        info.setDirectory(false);
        return info;
    }

    @Override
    public boolean copyFile(String sourcePath, String targetPath) {
        return true;
    }

    @Override
    public boolean moveFile(String sourcePath, String targetPath) {
        return true;
    }

    @Override
    public StorageQuota getQuota() {
        StorageQuota quota = new StorageQuota();
        quota.setTotalSpace(1024L * 1024 * 1024 * 100);
        quota.setUsedSpace(1024L * 1024 * 1024 * 10);
        quota.setAvailableSpace(1024L * 1024 * 1024 * 90);
        quota.setFileCount(100);
        return quota;
    }
}
