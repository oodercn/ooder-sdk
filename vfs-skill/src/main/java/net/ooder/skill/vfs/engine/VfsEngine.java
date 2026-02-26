package net.ooder.skill.vfs.engine;

import net.ooder.skill.vfs.api.entity.FileVersion;
import net.ooder.skill.vfs.driver.VfsDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VFS Engine 层
 * 业务逻辑实现
 */
public class VfsEngine {
    
    private final VfsDriver driver;
    
    public VfsEngine(VfsDriver driver) {
        this.driver = driver;
    }
    
    /**
     * 获取文件版本列表
     * @param fileId 文件ID
     * @return 版本列表
     */
    public List<FileVersion> getFileVersions(String fileId) {
        // 调用 Driver 层获取版本列表
        Map<String, Object> params = new HashMap<>();
        params.put("fileId", fileId);
        Object result = driver.invoke("vfs.getFileVersions", params);
        if (result instanceof List) {
            return (List<FileVersion>) result;
        }
        return new ArrayList<>();
    }
    
    /**
     * 执行能力
     * @param capabilityId 能力ID
     * @param params 参数
     * @return 结果
     */
    public Object execute(String capabilityId, Map<String, Object> params) {
        switch (capabilityId) {
            case "vfs.getFileVersions":
                return getFileVersions((String) params.get("fileId"));
            default:
                throw new UnsupportedOperationException("Unknown capability: " + capabilityId);
        }
    }
}
