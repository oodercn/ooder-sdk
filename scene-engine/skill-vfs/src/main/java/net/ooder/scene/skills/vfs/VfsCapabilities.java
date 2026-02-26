package net.ooder.scene.skills.vfs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * VFS能力配置
 * 定义VFS Skill支持的能力
 */
public class VfsCapabilities {
    
    private String skillId;
    private String providerType;
    
    // 文件操作
    private boolean supportFileRead;
    private boolean supportFileWrite;
    private boolean supportFileDelete;
    private boolean supportFileCopy;
    private boolean supportFileMove;
    private boolean supportFileShare;
    private boolean supportFilePreview;
    private boolean supportFileVersion;
    private boolean supportFileCompress;
    
    // 文件夹操作
    private boolean supportFolderCreate;
    private boolean supportFolderDelete;
    private boolean supportFolderList;
    
    // 流操作
    private boolean supportStreamUpload;
    private boolean supportStreamDownload;
    
    // 权限管理
    private boolean supportAclManage;
    private boolean supportMetadata;
    
    // 额外配置
    private Map<String, Object> extraConfig;
    
    public String getSkillId() {
        return skillId;
    }
    
    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }
    
    public String getProviderType() {
        return providerType;
    }
    
    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }
    
    public boolean isSupportFileRead() {
        return supportFileRead;
    }
    
    public void setSupportFileRead(boolean supportFileRead) {
        this.supportFileRead = supportFileRead;
    }
    
    public boolean isSupportFileWrite() {
        return supportFileWrite;
    }
    
    public void setSupportFileWrite(boolean supportFileWrite) {
        this.supportFileWrite = supportFileWrite;
    }
    
    public boolean isSupportFileDelete() {
        return supportFileDelete;
    }
    
    public void setSupportFileDelete(boolean supportFileDelete) {
        this.supportFileDelete = supportFileDelete;
    }
    
    public boolean isSupportFileCopy() {
        return supportFileCopy;
    }
    
    public void setSupportFileCopy(boolean supportFileCopy) {
        this.supportFileCopy = supportFileCopy;
    }
    
    public boolean isSupportFileMove() {
        return supportFileMove;
    }
    
    public void setSupportFileMove(boolean supportFileMove) {
        this.supportFileMove = supportFileMove;
    }
    
    public boolean isSupportFileShare() {
        return supportFileShare;
    }
    
    public void setSupportFileShare(boolean supportFileShare) {
        this.supportFileShare = supportFileShare;
    }
    
    public boolean isSupportFilePreview() {
        return supportFilePreview;
    }
    
    public void setSupportFilePreview(boolean supportFilePreview) {
        this.supportFilePreview = supportFilePreview;
    }
    
    public boolean isSupportFileVersion() {
        return supportFileVersion;
    }
    
    public void setSupportFileVersion(boolean supportFileVersion) {
        this.supportFileVersion = supportFileVersion;
    }
    
    public boolean isSupportFileCompress() {
        return supportFileCompress;
    }
    
    public void setSupportFileCompress(boolean supportFileCompress) {
        this.supportFileCompress = supportFileCompress;
    }
    
    public boolean isSupportFolderCreate() {
        return supportFolderCreate;
    }
    
    public void setSupportFolderCreate(boolean supportFolderCreate) {
        this.supportFolderCreate = supportFolderCreate;
    }
    
    public boolean isSupportFolderDelete() {
        return supportFolderDelete;
    }
    
    public void setSupportFolderDelete(boolean supportFolderDelete) {
        this.supportFolderDelete = supportFolderDelete;
    }
    
    public boolean isSupportFolderList() {
        return supportFolderList;
    }
    
    public void setSupportFolderList(boolean supportFolderList) {
        this.supportFolderList = supportFolderList;
    }
    
    public boolean isSupportStreamUpload() {
        return supportStreamUpload;
    }
    
    public void setSupportStreamUpload(boolean supportStreamUpload) {
        this.supportStreamUpload = supportStreamUpload;
    }
    
    public boolean isSupportStreamDownload() {
        return supportStreamDownload;
    }
    
    public void setSupportStreamDownload(boolean supportStreamDownload) {
        this.supportStreamDownload = supportStreamDownload;
    }
    
    public boolean isSupportAclManage() {
        return supportAclManage;
    }
    
    public void setSupportAclManage(boolean supportAclManage) {
        this.supportAclManage = supportAclManage;
    }
    
    public boolean isSupportMetadata() {
        return supportMetadata;
    }
    
    public void setSupportMetadata(boolean supportMetadata) {
        this.supportMetadata = supportMetadata;
    }
    
    public Map<String, Object> getExtraConfig() {
        return extraConfig;
    }

    public void setExtraConfig(Map<String, Object> extraConfig) {
        this.extraConfig = extraConfig;
    }

    // 额外方法，用于兼容 VfsSkillImpl
    public boolean requiresFallback() {
        return false;
    }

    public List<String> getUnsupportedCapabilities() {
        return new ArrayList<>();
    }

    public boolean isSupportFileRename() {
        return supportFileMove;
    }

    public boolean isSupportFileSearch() {
        return supportFileRead && supportFolderList;
    }

    /**
     * @deprecated 使用 VfsCapabilitiesFactory 和配置驱动的方式替代
     */
    @Deprecated
    public static VfsCapabilities forLocal() {
        return VfsCapabilitiesFactory.getInstance().createCapabilities("local");
    }

    /**
     * @deprecated 使用 VfsCapabilitiesFactory 和配置驱动的方式替代
     */
    @Deprecated
    public static VfsCapabilities forOSS() {
        return VfsCapabilitiesFactory.getInstance().createCapabilities("oss");
    }

    /**
     * @deprecated 使用 VfsCapabilitiesFactory 和配置驱动的方式替代
     */
    @Deprecated
    public static VfsCapabilities forMinIO() {
        return VfsCapabilitiesFactory.getInstance().createCapabilities("minio");
    }

    /**
     * @deprecated 使用 VfsCapabilitiesFactory 和配置驱动的方式替代
     */
    @Deprecated
    public static VfsCapabilities forS3() {
        return VfsCapabilitiesFactory.getInstance().createCapabilities("s3");
    }

    /**
     * @deprecated 使用 VfsCapabilitiesFactory 和配置驱动的方式替代
     */
    @Deprecated
    public static VfsCapabilities forDatabase() {
        return VfsCapabilitiesFactory.getInstance().createCapabilities("database");
    }
}
