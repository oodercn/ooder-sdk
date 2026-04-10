package net.ooder.skill.common.api;

import net.ooder.skill.common.model.ResultModel;
import net.ooder.skill.common.model.SystemConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/system")
@CrossOrigin(origins = "*")
public class ConfigApi {

    @Value("${spring.application.name:skill-common}")
    private String applicationName;

    @Value("${ooder.jds.config.name:common}")
    private String configName;

    @Value("${ooder.sdk.enabled:true}")
    private boolean sdkEnabled;

    @Value("${ooder.mock.enabled:false}")
    private boolean mockEnabled;

    @GetMapping("/config")
    public ResultModel<Map<String, Object>> getSystemConfig() {
        Map<String, Object> config = new HashMap<>();
        
        config.put("syscode", applicationName);
        config.put("configName", configName);
        config.put("sdkEnabled", sdkEnabled);
        config.put("mockEnabled", mockEnabled);
        config.put("version", "1.0.0");
        config.put("environment", System.getProperty("spring.profiles.active", "default"));
        
        return ResultModel.success(config);
    }

    @GetMapping("/syscode")
    public ResultModel<String> getSyscode() {
        return ResultModel.success(applicationName);
    }

    @GetMapping("/check")
    public ResultModel<Map<String, Boolean>> checkSystem(@RequestParam String syscode) {
        Map<String, Boolean> result = new HashMap<>();
        result.put("isCurrentSystem", applicationName.equals(syscode));
        result.put("isCommon", applicationName.contains("common"));
        result.put("isScene", applicationName.contains("scene"));
        result.put("isCapability", applicationName.contains("capability"));
        
        return ResultModel.success(result);
    }

    @GetMapping("/health")
    public ResultModel<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", applicationName);
        health.put("timestamp", System.currentTimeMillis());
        return ResultModel.success(health);
    }
}
