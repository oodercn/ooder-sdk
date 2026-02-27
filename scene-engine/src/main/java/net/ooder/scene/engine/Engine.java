package net.ooder.scene.engine;

import java.util.List;
import java.util.Map;

public interface Engine {

    EngineType getType();

    String getName();

    EngineStatus getStatus();

    void initialize();

    void start();

    void stop();

    void destroy();

    Map<String, Object> getConfig();

    void updateConfig(Map<String, Object> config);

    EngineStats getStats();

    boolean healthCheck();

    List<String> getCapabilities();
}
