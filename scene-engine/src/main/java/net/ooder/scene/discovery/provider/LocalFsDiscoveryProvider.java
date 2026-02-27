package net.ooder.scene.discovery.provider;

import net.ooder.scene.discovery.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LocalFsDiscoveryProvider implements DiscoveryProvider {
    private static final String PROVIDER_NAME = "LOCAL-FS";
    private static final int PRIORITY = 50;

    private String cachePath;
    private DiscoveryConfig config;
    private boolean running;

    public LocalFsDiscoveryProvider() {
        this.running = false;
    }

    public void setCachePath(String path) {
        this.cachePath = path;
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public void initialize(DiscoveryConfig config) {
        this.config = config;
        if (config.getProperty("cachePath") != null) {
            this.cachePath = (String) config.getProperty("cachePath");
        }
        if (this.cachePath == null) {
            this.cachePath = System.getProperty("user.home") + "/.ooder/cache";
        }
        ensureCacheDirectory();
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
        return running;
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
        return true;
    }

    private void ensureCacheDirectory() {
        File dir = new File(cachePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private List<DiscoveredItem> discoverScenes(DiscoveryQuery query) {
        List<DiscoveredItem> results = new ArrayList<>();
        File scenesDir = new File(cachePath, "scenes");
        if (scenesDir.exists() && scenesDir.isDirectory()) {
            File[] sceneFiles = scenesDir.listFiles((dir, name) -> name.endsWith(".yaml"));
            if (sceneFiles != null) {
                for (File sceneFile : sceneFiles) {
                    String sceneId = sceneFile.getName().replace(".yaml", "");
                    DiscoveredItem item = new DiscoveredItem(sceneId, sceneId, DiscoveryType.SCENE);
                    item.setProviderName(PROVIDER_NAME);
                    results.add(item);
                }
            }
        }
        return results;
    }

    private List<DiscoveredItem> discoverCapabilities(DiscoveryQuery query) {
        List<DiscoveredItem> results = new ArrayList<>();
        File capsDir = new File(cachePath, "capabilities");
        if (capsDir.exists() && capsDir.isDirectory()) {
            File[] capFiles = capsDir.listFiles((dir, name) -> name.endsWith(".yaml"));
            if (capFiles != null) {
                for (File capFile : capFiles) {
                    String capId = capFile.getName().replace(".yaml", "");
                    DiscoveredItem item = new DiscoveredItem(capId, capId, DiscoveryType.CAPABILITY);
                    item.setProviderName(PROVIDER_NAME);
                    results.add(item);
                }
            }
        }
        return results;
    }

    private List<DiscoveredItem> discoverSkills(DiscoveryQuery query) {
        List<DiscoveredItem> results = new ArrayList<>();
        File skillsDir = new File(cachePath, "skills");
        if (skillsDir.exists() && skillsDir.isDirectory()) {
            File[] skillFiles = skillsDir.listFiles((dir, name) -> name.endsWith(".yaml"));
            if (skillFiles != null) {
                for (File skillFile : skillFiles) {
                    String skillId = skillFile.getName().replace(".yaml", "");
                    DiscoveredItem item = new DiscoveredItem(skillId, skillId, DiscoveryType.SKILL);
                    item.setProviderName(PROVIDER_NAME);
                    results.add(item);
                }
            }
        }
        return results;
    }
}
