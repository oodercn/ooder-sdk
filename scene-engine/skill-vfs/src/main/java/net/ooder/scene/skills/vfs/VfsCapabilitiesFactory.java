package net.ooder.scene.skills.vfs;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * VFS 能力工厂
 * 使用注册表模式管理 VFS 能力配置
 */
public class VfsCapabilitiesFactory {

    private static final VfsCapabilitiesFactory INSTANCE = new VfsCapabilitiesFactory();
    private final Map<String, VfsCapabilitiesProvider> providers = new ConcurrentHashMap<>();

    private VfsCapabilitiesFactory() {
        registerDefaultProviders();
    }

    public static VfsCapabilitiesFactory getInstance() {
        return INSTANCE;
    }

    private void registerDefaultProviders() {
        // Local provider
        providers.put("local", new VfsCapabilitiesProvider() {
            @Override
            public String getProviderType() {
                return "local";
            }

            @Override
            public String getProviderName() {
                return "Local File System";
            }

            @Override
            public VfsCapabilities createCapabilities() {
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
        });

        // OSS provider
        providers.put("oss", new VfsCapabilitiesProvider() {
            @Override
            public String getProviderType() {
                return "oss";
            }

            @Override
            public String getProviderName() {
                return "Aliyun OSS";
            }

            @Override
            public VfsCapabilities createCapabilities() {
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
        });

        // MinIO provider
        providers.put("minio", new VfsCapabilitiesProvider() {
            @Override
            public String getProviderType() {
                return "minio";
            }

            @Override
            public String getProviderName() {
                return "MinIO";
            }

            @Override
            public VfsCapabilities createCapabilities() {
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
        });

        // S3 provider
        providers.put("s3", new VfsCapabilitiesProvider() {
            @Override
            public String getProviderType() {
                return "s3";
            }

            @Override
            public String getProviderName() {
                return "AWS S3";
            }

            @Override
            public VfsCapabilities createCapabilities() {
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
        });

        // Database provider
        providers.put("database", new VfsCapabilitiesProvider() {
            @Override
            public String getProviderType() {
                return "database";
            }

            @Override
            public String getProviderName() {
                return "Database Storage";
            }

            @Override
            public VfsCapabilities createCapabilities() {
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
        });
    }

    /**
     * 注册自定义能力提供者
     */
    public void registerProvider(VfsCapabilitiesProvider provider) {
        providers.put(provider.getProviderType(), provider);
    }

    /**
     * 创建能力配置
     */
    public VfsCapabilities createCapabilities(String providerType) {
        VfsCapabilitiesProvider provider = providers.get(providerType);
        if (provider != null) {
            return provider.createCapabilities();
        }
        return null;
    }

    /**
     * 检查是否支持该类型
     */
    public boolean supports(String providerType) {
        return providers.containsKey(providerType);
    }

    /**
     * 获取所有支持的类型
     */
    public Set<String> getSupportedTypes() {
        return providers.keySet();
    }

    /**
     * VFS 能力提供者接口
     */
    public interface VfsCapabilitiesProvider {
        String getProviderType();
        String getProviderName();
        VfsCapabilities createCapabilities();
    }
}
