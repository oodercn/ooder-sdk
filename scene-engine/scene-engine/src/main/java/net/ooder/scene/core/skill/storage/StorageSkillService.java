package net.ooder.scene.core.skill.storage;

import net.ooder.scene.core.security.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 存储服务技能
 *
 * <p>包装SDK存储能力，添加安全检查和审计日志</p>
 */
public class StorageSkillService extends SecureSkillService {

    private StorageSdkWrapper storageSdkWrapper;

    @Override
    protected Object doExecute(SkillRequest request) {
        String operation = request.getOperation();
        switch (operation) {
            case "read":
                return readFile(request);
            case "write":
                return writeFile(request);
            case "delete":
                return deleteFile(request);
            case "list":
                return listFiles(request);
            case "exists":
                return checkFileExists(request);
            default:
                throw new UnsupportedOperationException("Unsupported operation: " + operation);
        }
    }

    @Override
    protected String getResourceType() {
        return "storage";
    }

    private Object readFile(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String filePath = (String) params.get("filePath");
        
        byte[] content = storageSdkWrapper.readFile(filePath);
        Map<String, Object> result = new HashMap<>();
        result.put("filePath", filePath);
        result.put("content", content);
        result.put("size", content.length);
        return result;
    }

    private Object writeFile(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String filePath = (String) params.get("filePath");
        byte[] content = (byte[]) params.get("content");
        boolean overwrite = params.containsKey("overwrite") ? (Boolean) params.get("overwrite") : false;
        
        boolean success = storageSdkWrapper.writeFile(filePath, content, overwrite);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("filePath", filePath);
        result.put("size", content.length);
        return result;
    }

    private Object deleteFile(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String filePath = (String) params.get("filePath");
        boolean recursive = params.containsKey("recursive") ? (Boolean) params.get("recursive") : false;
        
        boolean success = storageSdkWrapper.deleteFile(filePath, recursive);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("filePath", filePath);
        return result;
    }

    private Object listFiles(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String directoryPath = (String) params.get("directoryPath");
        String pattern = params.containsKey("pattern") ? (String) params.get("pattern") : null;
        boolean recursive = params.containsKey("recursive") ? (Boolean) params.get("recursive") : false;
        
        String[] files = storageSdkWrapper.listFiles(directoryPath, pattern, recursive);
        Map<String, Object> result = new HashMap<>();
        result.put("directoryPath", directoryPath);
        result.put("files", files);
        result.put("count", files.length);
        return result;
    }

    private Object checkFileExists(SkillRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParameters();
        String filePath = (String) params.get("filePath");
        
        boolean exists = storageSdkWrapper.fileExists(filePath);
        Map<String, Object> result = new HashMap<>();
        result.put("filePath", filePath);
        result.put("exists", exists);
        return result;
    }

    @Override
    protected String getSkillId() {
        return "skill-storage";
    }
}

/**
 * 存储SDK包装器
 *
 * <p>包装底层SDK存储能力，提供统一接口</p>
 */
class StorageSdkWrapper {

    public byte[] readFile(String filePath) {
        return new byte[0];
    }

    public boolean writeFile(String filePath, byte[] content, boolean overwrite) {
        return true;
    }

    public boolean deleteFile(String filePath, boolean recursive) {
        return true;
    }

    public String[] listFiles(String directoryPath, String pattern, boolean recursive) {
        return new String[0];
    }

    public boolean fileExists(String filePath) {
        return false;
    }
}
