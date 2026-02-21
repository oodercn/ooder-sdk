package net.ooder.scene.skills.vfs;

import java.util.HashSet;
import java.util.Set;

/**
 * VfsCapabilities VFS能力配置
 * 
 * <p>定义VFS技能支持的能力集合，用于判断哪些功能由外部 skills 提供，
 * 哪些需要本地 JSON 降级实现。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public class VfsCapabilities {

    private boolean supportFileRead = true;
    private boolean supportFileWrite = true;
    private boolean supportFileDelete = true;
    private boolean supportFileCopy = true;
    private boolean supportFileMove = true;
    private boolean supportFileRename = true;
    
    private boolean supportFolderCreate = true;
    private boolean supportFolderDelete = true;
    private boolean supportFolderList = true;
    private boolean supportFolderCopy = true;
    private boolean supportFolderMove = true;
    
    private boolean supportFileVersion = false;
    private boolean supportFileShare = false;
    private boolean supportFilePreview = false;
    private boolean supportFileCompress = false;
    private boolean supportFileSearch = false;
    
    private boolean supportStreamUpload = false;
    private boolean supportStreamDownload = true;
    private boolean supportStreamAppend = false;
    
    private boolean supportAclRead = false;
    private boolean supportAclWrite = false;
    private boolean supportAclManage = false;
    
    private boolean supportMetadataRead = false;
    private boolean supportMetadataWrite = false;
    private boolean supportMetadata = false;

    private String providerType = "local";
    private String skillEndpoint;
    private String skillId;

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

    public boolean isSupportFileRename() {
        return supportFileRename;
    }

    public void setSupportFileRename(boolean supportFileRename) {
        this.supportFileRename = supportFileRename;
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

    public boolean isSupportFolderCopy() {
        return supportFolderCopy;
    }

    public void setSupportFolderCopy(boolean supportFolderCopy) {
        this.supportFolderCopy = supportFolderCopy;
    }

    public boolean isSupportFolderMove() {
        return supportFolderMove;
    }

    public void setSupportFolderMove(boolean supportFolderMove) {
        this.supportFolderMove = supportFolderMove;
    }

    public boolean isSupportFileVersion() {
        return supportFileVersion;
    }

    public void setSupportFileVersion(boolean supportFileVersion) {
        this.supportFileVersion = supportFileVersion;
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

    public boolean isSupportFileCompress() {
        return supportFileCompress;
    }

    public void setSupportFileCompress(boolean supportFileCompress) {
        this.supportFileCompress = supportFileCompress;
    }

    public boolean isSupportFileSearch() {
        return supportFileSearch;
    }

    public void setSupportFileSearch(boolean supportFileSearch) {
        this.supportFileSearch = supportFileSearch;
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

    public boolean isSupportStreamAppend() {
        return supportStreamAppend;
    }

    public void setSupportStreamAppend(boolean supportStreamAppend) {
        this.supportStreamAppend = supportStreamAppend;
    }

    public boolean isSupportAclRead() {
        return supportAclRead;
    }

    public void setSupportAclRead(boolean supportAclRead) {
        this.supportAclRead = supportAclRead;
    }

    public boolean isSupportAclWrite() {
        return supportAclWrite;
    }

    public void setSupportAclWrite(boolean supportAclWrite) {
        this.supportAclWrite = supportAclWrite;
    }

    public boolean isSupportAclManage() {
        return supportAclManage;
    }

    public void setSupportAclManage(boolean supportAclManage) {
        this.supportAclManage = supportAclManage;
    }

    public boolean isSupportMetadataRead() {
        return supportMetadataRead;
    }

    public void setSupportMetadataRead(boolean supportMetadataRead) {
        this.supportMetadataRead = supportMetadataRead;
    }

    public boolean isSupportMetadataWrite() {
        return supportMetadataWrite;
    }

    public void setSupportMetadataWrite(boolean supportMetadataWrite) {
        this.supportMetadataWrite = supportMetadataWrite;
    }

    public boolean isSupportMetadata() {
        return supportMetadata;
    }

    public void setSupportMetadata(boolean supportMetadata) {
        this.supportMetadata = supportMetadata;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getSkillEndpoint() {
        return skillEndpoint;
    }

    public void setSkillEndpoint(String skillEndpoint) {
        this.skillEndpoint = skillEndpoint;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public Set<String> getUnsupportedCapabilities() {
        Set<String> unsupported = new HashSet<String>();
        if (!supportFileVersion) unsupported.add("file-version");
        if (!supportFileShare) unsupported.add("file-share");
        if (!supportFilePreview) unsupported.add("file-preview");
        if (!supportFileCompress) unsupported.add("file-compress");
        if (!supportFileSearch) unsupported.add("file-search");
        if (!supportStreamUpload) unsupported.add("stream-upload");
        if (!supportAclManage) unsupported.add("acl-manage");
        if (!supportMetadata) unsupported.add("metadata");
        return unsupported;
    }

    public boolean requiresFallback() {
        return !supportFileVersion || !supportFileShare || !supportFilePreview 
            || !supportFileCompress || !supportFileSearch || !supportStreamUpload
            || !supportAclManage || !supportMetadata;
    }

    public static VfsCapabilities forLocal() {
        VfsCapabilities caps = new VfsCapabilities();
        caps.setProviderType("local");
        caps.setSkillId("skill-vfs-local");
        caps.setSupportFileRead(true);
        caps.setSupportFileWrite(true);
        caps.setSupportFileDelete(true);
        caps.setSupportFileCopy(true);
        caps.setSupportFileMove(true);
        caps.setSupportFolderCreate(true);
        caps.setSupportFolderDelete(true);
        caps.setSupportFolderList(true);
        caps.setSupportFileVersion(true);
        caps.setSupportFileCompress(true);
        caps.setSupportStreamDownload(true);
        caps.setSupportFileShare(false);
        caps.setSupportFilePreview(false);
        caps.setSupportStreamUpload(false);
        caps.setSupportAclManage(false);
        caps.setSupportMetadata(false);
        return caps;
    }

    public static VfsCapabilities forOSS() {
        VfsCapabilities caps = new VfsCapabilities();
        caps.setProviderType("oss");
        caps.setSkillId("skill-vfs-oss");
        caps.setSupportFileRead(true);
        caps.setSupportFileWrite(true);
        caps.setSupportFileDelete(true);
        caps.setSupportFileCopy(true);
        caps.setSupportFileMove(true);
        caps.setSupportFolderCreate(true);
        caps.setSupportFolderList(true);
        caps.setSupportFileShare(true);
        caps.setSupportFilePreview(true);
        caps.setSupportStreamUpload(true);
        caps.setSupportStreamDownload(true);
        caps.setSupportAclManage(true);
        caps.setSupportMetadata(true);
        caps.setSupportFileVersion(false);
        caps.setSupportFileCompress(false);
        return caps;
    }

    public static VfsCapabilities forMinIO() {
        VfsCapabilities caps = new VfsCapabilities();
        caps.setProviderType("minio");
        caps.setSkillId("skill-vfs-minio");
        caps.setSupportFileRead(true);
        caps.setSupportFileWrite(true);
        caps.setSupportFileDelete(true);
        caps.setSupportFileCopy(true);
        caps.setSupportFileMove(true);
        caps.setSupportFolderCreate(true);
        caps.setSupportFolderList(true);
        caps.setSupportFileShare(true);
        caps.setSupportStreamUpload(true);
        caps.setSupportStreamDownload(true);
        caps.setSupportAclManage(true);
        caps.setSupportMetadata(true);
        caps.setSupportFileVersion(false);
        caps.setSupportFilePreview(false);
        caps.setSupportFileCompress(false);
        return caps;
    }

    public static VfsCapabilities forS3() {
        VfsCapabilities caps = new VfsCapabilities();
        caps.setProviderType("s3");
        caps.setSkillId("skill-vfs-s3");
        caps.setSupportFileRead(true);
        caps.setSupportFileWrite(true);
        caps.setSupportFileDelete(true);
        caps.setSupportFileCopy(true);
        caps.setSupportFileMove(true);
        caps.setSupportFolderCreate(true);
        caps.setSupportFolderList(true);
        caps.setSupportFileVersion(true);
        caps.setSupportFileShare(true);
        caps.setSupportFilePreview(true);
        caps.setSupportStreamUpload(true);
        caps.setSupportStreamDownload(true);
        caps.setSupportAclManage(true);
        caps.setSupportMetadata(true);
        caps.setSupportFileCompress(false);
        return caps;
    }

    public static VfsCapabilities forDatabase() {
        VfsCapabilities caps = new VfsCapabilities();
        caps.setProviderType("database");
        caps.setSkillId("skill-vfs-database");
        caps.setSupportFileRead(true);
        caps.setSupportFileWrite(true);
        caps.setSupportFileDelete(true);
        caps.setSupportFolderCreate(true);
        caps.setSupportFolderDelete(true);
        caps.setSupportFolderList(true);
        caps.setSupportFileVersion(true);
        caps.setSupportAclManage(true);
        caps.setSupportMetadata(true);
        caps.setSupportFileCopy(false);
        caps.setSupportFileMove(false);
        caps.setSupportFileShare(false);
        caps.setSupportFilePreview(false);
        caps.setSupportStreamUpload(false);
        caps.setSupportStreamDownload(false);
        caps.setSupportFileCompress(false);
        return caps;
    }
}
