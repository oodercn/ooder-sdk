package net.ooder.scene.discovery.provider;

import net.ooder.scene.discovery.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SkillCenterDiscoveryProvider implements DiscoveryProvider {
    private static final String PROVIDER_NAME = "SKILL-CENTER";
    private static final int PRIORITY = 80;

    private String skillCenterUrl;
    private DiscoveryConfig config;
    private boolean running;

    public SkillCenterDiscoveryProvider() {
        this.running = false;
    }

    public void setSkillCenterUrl(String url) {
        this.skillCenterUrl = url;
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public void initialize(DiscoveryConfig config) {
        this.config = config;
        if (config.getProperty("skillCenterUrl") != null) {
            this.skillCenterUrl = (String) config.getProperty("skillCenterUrl");
        }
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running && skillCenterUrl != null;
    }

    @Override
    public CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            List<DiscoveredItem> results = new ArrayList<>();

            switch (query.getType()) {
                case SCENE:
                    results.addAll(discoverScenes(query));
                    break;
                case CAPABILITY:
                    results.addAll(discoverCapabilities(query));
                    break;
                case SKILL:
                    results.addAll(discoverSkills(query));
                    break;
                case AGENT:
                case PEER:
                    break;
            }

            return results;
        });
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public boolean isApplicable(DiscoveryScope scope) {
        switch (scope) {
            case PERSONAL:
                return false;
            case DEPARTMENT:
                return true;
            case COMPANY:
                return true;
            case PUBLIC:
                return true;
            default:
                return false;
        }
    }

    private List<DiscoveredItem> discoverScenes(DiscoveryQuery query) {
        List<DiscoveredItem> results = new ArrayList<>();
        // 这里可以添加实际的SkillCenter场景发现逻辑
        // 调用 SkillCenter API 获取场景列表
        return results;
    }

    private List<DiscoveredItem> discoverCapabilities(DiscoveryQuery query) {
        List<DiscoveredItem> results = new ArrayList<>();
        // 这里可以添加实际的SkillCenter能力发现逻辑
        // 调用 SkillCenter API 获取能力列表
        return results;
    }

    private List<DiscoveredItem> discoverSkills(DiscoveryQuery query) {
        List<DiscoveredItem> results = new ArrayList<>();
        // 这里可以添加实际的SkillCenter技能发现逻辑
        // 调用 SkillCenter API 获取技能列表
        return results;
    }
}
