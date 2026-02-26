package net.ooder.scene.skills.org;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Org 能力工厂
 * 使用注册表模式管理 Org 能力配置
 */
public class OrgCapabilitiesFactory {

    private static final OrgCapabilitiesFactory INSTANCE = new OrgCapabilitiesFactory();
    private final Map<String, OrgCapabilitiesProvider> providers = new ConcurrentHashMap<>();

    private OrgCapabilitiesFactory() {
        registerDefaultProviders();
    }

    public static OrgCapabilitiesFactory getInstance() {
        return INSTANCE;
    }

    private void registerDefaultProviders() {
        // DingTalk provider
        providers.put("dingtalk", new OrgCapabilitiesProvider() {
            @Override
            public String getProviderType() {
                return "dingtalk";
            }

            @Override
            public String getProviderName() {
                return "DingTalk";
            }

            @Override
            public OrgCapabilities createCapabilities() {
                OrgCapabilities caps = new OrgCapabilities();
                caps.setProviderType("dingtalk");
                caps.setSkillId("skill-org-dingding");
                caps.setSupportOrgQuery(true);
                caps.setSupportPersonQuery(true);
                caps.setSupportPersonSync(true);
                caps.setSupportOrgSync(true);
                caps.setSupportOrgLevel(false);
                caps.setSupportOrgRole(false);
                caps.setSupportPersonDuty(false);
                caps.setSupportPersonGroup(false);
                caps.setSupportPersonLevel(false);
                caps.setSupportPersonPosition(false);
                caps.setSupportPersonRole(false);
                caps.setSupportUserAuth(false);
                return caps;
            }
        });

        // Feishu provider
        providers.put("feishu", new OrgCapabilitiesProvider() {
            @Override
            public String getProviderType() {
                return "feishu";
            }

            @Override
            public String getProviderName() {
                return "Feishu/Lark";
            }

            @Override
            public OrgCapabilities createCapabilities() {
                OrgCapabilities caps = new OrgCapabilities();
                caps.setProviderType("feishu");
                caps.setSkillId("skill-org-feishu");
                caps.setSupportOrgQuery(true);
                caps.setSupportPersonQuery(true);
                caps.setSupportPersonSync(true);
                caps.setSupportOrgSync(true);
                caps.setSupportOrgLevel(false);
                caps.setSupportOrgRole(false);
                caps.setSupportPersonDuty(false);
                caps.setSupportPersonGroup(false);
                caps.setSupportPersonLevel(false);
                caps.setSupportPersonPosition(false);
                caps.setSupportPersonRole(false);
                caps.setSupportUserAuth(false);
                return caps;
            }
        });

        // WeCom provider
        providers.put("wecom", new OrgCapabilitiesProvider() {
            @Override
            public String getProviderType() {
                return "wecom";
            }

            @Override
            public String getProviderName() {
                return "WeCom/WeChat Work";
            }

            @Override
            public OrgCapabilities createCapabilities() {
                OrgCapabilities caps = new OrgCapabilities();
                caps.setProviderType("wecom");
                caps.setSkillId("skill-org-wecom");
                caps.setSupportOrgQuery(true);
                caps.setSupportPersonQuery(true);
                caps.setSupportPersonSync(true);
                caps.setSupportOrgSync(true);
                caps.setSupportOrgLevel(false);
                caps.setSupportOrgRole(false);
                caps.setSupportPersonDuty(false);
                caps.setSupportPersonGroup(false);
                caps.setSupportPersonLevel(false);
                caps.setSupportPersonPosition(false);
                caps.setSupportPersonRole(false);
                caps.setSupportUserAuth(false);
                return caps;
            }
        });

        // JSON provider
        providers.put("json", new OrgCapabilitiesProvider() {
            @Override
            public String getProviderType() {
                return "json";
            }

            @Override
            public String getProviderName() {
                return "JSON File";
            }

            @Override
            public OrgCapabilities createCapabilities() {
                OrgCapabilities caps = new OrgCapabilities();
                caps.setProviderType("json");
                caps.setSupportOrgQuery(true);
                caps.setSupportPersonQuery(true);
                caps.setSupportPersonSync(true);
                caps.setSupportOrgSync(true);
                caps.setSupportOrgLevel(true);
                caps.setSupportOrgRole(true);
                caps.setSupportPersonDuty(true);
                caps.setSupportPersonGroup(true);
                caps.setSupportPersonLevel(true);
                caps.setSupportPersonPosition(true);
                caps.setSupportPersonRole(true);
                caps.setSupportUserAuth(true);
                caps.setSupportOrgAdmin(true);
                return caps;
            }
        });

        // Database provider
        providers.put("database", new OrgCapabilitiesProvider() {
            @Override
            public String getProviderType() {
                return "database";
            }

            @Override
            public String getProviderName() {
                return "Database";
            }

            @Override
            public OrgCapabilities createCapabilities() {
                OrgCapabilities caps = new OrgCapabilities();
                caps.setProviderType("database");
                caps.setSupportOrgQuery(true);
                caps.setSupportPersonQuery(true);
                caps.setSupportPersonSync(true);
                caps.setSupportOrgSync(true);
                caps.setSupportOrgLevel(true);
                caps.setSupportOrgRole(true);
                caps.setSupportPersonDuty(true);
                caps.setSupportPersonGroup(true);
                caps.setSupportPersonLevel(true);
                caps.setSupportPersonPosition(true);
                caps.setSupportPersonRole(true);
                caps.setSupportUserAuth(true);
                caps.setSupportOrgAdmin(true);
                return caps;
            }
        });
    }

    /**
     * 注册自定义能力提供者
     */
    public void registerProvider(OrgCapabilitiesProvider provider) {
        providers.put(provider.getProviderType(), provider);
    }

    /**
     * 创建能力配置
     */
    public OrgCapabilities createCapabilities(String providerType) {
        OrgCapabilitiesProvider provider = providers.get(providerType);
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
     * Org 能力提供者接口
     */
    public interface OrgCapabilitiesProvider {
        String getProviderType();
        String getProviderName();
        OrgCapabilities createCapabilities();
    }
}
