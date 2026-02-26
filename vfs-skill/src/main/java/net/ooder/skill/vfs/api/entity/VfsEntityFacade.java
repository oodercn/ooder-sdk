package net.ooder.skill.vfs.api.entity;

import net.ooder.skill.vfs.engine.VfsSkill;

import java.util.List;

/**
 * VFS 实体 Facade
 * 兼容层，保持原有 API 调用方式
 */
public class VfsEntityFacade {
    
    private static VfsEntityFacade instance;
    private VfsSkill vfsSkill;
    
    private VfsEntityFacade() {
    }
    
    public static VfsEntityFacade getInstance() {
        if (instance == null) {
            synchronized (VfsEntityFacade.class) {
                if (instance == null) {
                    instance = new VfsEntityFacade();
                }
            }
        }
        return instance;
    }
    
    /**
     * 设置 VfsSkill（由 SceneEngine 初始化时注入）
     * @param vfsSkill VfsSkill 实例
     */
    public void setVfsSkill(VfsSkill vfsSkill) {
        this.vfsSkill = vfsSkill;
    }
    
    /**
     * 获取文件版本列表
     * @param fileId 文件ID
     * @return 版本列表
     */
    public List<FileVersion> getFileVersionList(String fileId) {
        if (vfsSkill == null) {
            throw new IllegalStateException("VfsSkill not initialized");
        }
        return vfsSkill.getEngine().getFileVersions(fileId);
    }
}
