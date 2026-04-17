package net.ooder.skill.capability.controller;

import net.ooder.skill.capability.dto.SceneCapabilityDTO;
import net.ooder.skill.capability.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/scene-capabilities")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class SceneCapabilityController {

    private static final Logger log = LoggerFactory.getLogger(SceneCapabilityController.class);

    private final Map<String, SceneCapabilityDTO> capabilityStore = new HashMap<>();

    @GetMapping("/{id}")
    public ResultModel<SceneCapabilityDTO> getCapability(@PathVariable String id) {
        log.info("[SceneCapabilityController] Get capability: {}", id);
        SceneCapabilityDTO capability = capabilityStore.get(id);
        if (capability == null) {
            return ResultModel.notFound("Capability not found: " + id);
        }
        return ResultModel.success(capability);
    }

    @PostMapping("/{id}/activate")
    public ResultModel<Map<String, Object>> activateCapability(@PathVariable String id) {
        log.info("[SceneCapabilityController] Activate capability: {}", id);
        
        SceneCapabilityDTO capability = capabilityStore.get(id);
        if (capability == null) {
            capability = new SceneCapabilityDTO();
            capability.setId(id);
            capabilityStore.put(id, capability);
        }
        capability.setActive(true);
        capability.setActivateTime(LocalDateTime.now());
        
        Map<String, Object> result = new HashMap<>();
        result.put("capabilityId", id);
        result.put("status", "ACTIVATED");
        result.put("message", "Capability activated successfully");
        result.put("activatedAt", System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    @PostMapping("/{id}/deactivate")
    public ResultModel<Map<String, Object>> deactivateCapability(@PathVariable String id) {
        log.info("[SceneCapabilityController] Deactivate capability: {}", id);
        
        SceneCapabilityDTO capability = capabilityStore.get(id);
        if (capability == null) {
            capability = new SceneCapabilityDTO();
            capability.setId(id);
            capabilityStore.put(id, capability);
        }
        capability.setActive(false);
        capability.setDeactivateTime(LocalDateTime.now());
        
        Map<String, Object> result = new HashMap<>();
        result.put("capabilityId", id);
        result.put("status", "DEACTIVATED");
        result.put("message", "Capability deactivated successfully");
        result.put("deactivatedAt", System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    @GetMapping("/{id}/state")
    public ResultModel<Map<String, Object>> getState(@PathVariable String id) {
        log.info("[SceneCapabilityController] Get state for capability: {}", id);
        
        SceneCapabilityDTO capability = capabilityStore.get(id);
        
        Map<String, Object> state = new HashMap<>();
        state.put("capabilityId", id);
        
        if (capability == null) {
            state.put("status", "UNKNOWN");
            state.put("running", false);
            state.put("lastActiveTime", null);
        } else {
            state.put("status", Boolean.TRUE.equals(capability.getActive()) ? "ACTIVATED" : "DEACTIVATED");
            state.put("running", Boolean.TRUE.equals(capability.getActive()));
            state.put("lastActiveTime", capability.getActivateTime() != null ? 
                capability.getActivateTime().toString() : null);
        }
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("invocations", 0);
        metrics.put("errors", 0);
        metrics.put("avgResponseTime", 0);
        state.put("metrics", metrics);
        
        return ResultModel.success(state);
    }

    @DeleteMapping("/{id}/uninstall")
    public ResultModel<Map<String, Object>> uninstall(@PathVariable String id) {
        log.info("[SceneCapabilityController] Uninstall capability: {}", id);
        
        SceneCapabilityDTO removed = capabilityStore.remove(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("capabilityId", id);
        result.put("status", "UNINSTALLED");
        result.put("message", removed != null ? "Capability uninstalled successfully" : "Capability not found, nothing to uninstall");
        
        return ResultModel.success(result);
    }

    @PostMapping("/{id}/pause")
    public ResultModel<Map<String, Object>> pause(@PathVariable String id) {
        log.info("[SceneCapabilityController] Pause capability: {}", id);
        
        SceneCapabilityDTO capability = capabilityStore.get(id);
        if (capability != null) {
            capability.setActive(false);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("capabilityId", id);
        result.put("status", "PAUSED");
        result.put("message", "Capability paused successfully");
        
        return ResultModel.success(result);
    }

    @PostMapping("/{id}/resume")
    public ResultModel<Map<String, Object>> resume(@PathVariable String id) {
        log.info("[SceneCapabilityController] Resume capability: {}", id);
        
        SceneCapabilityDTO capability = capabilityStore.get(id);
        if (capability != null) {
            capability.setActive(true);
            capability.setActivateTime(LocalDateTime.now());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("capabilityId", id);
        result.put("status", "RESUMED");
        result.put("message", "Capability resumed successfully");
        
        return ResultModel.success(result);
    }

    @PostMapping("/{id}/trigger")
    public ResultModel<Map<String, Object>> trigger(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, Object> request) {
        
        log.info("[SceneCapabilityController] Trigger capability: {}, request: {}", id, request);
        
        String action = request != null ? (String) request.get("action") : "default";
        
        Map<String, Object> result = new HashMap<>();
        result.put("capabilityId", id);
        result.put("action", action);
        result.put("status", "TRIGGERED");
        result.put("message", "Action '" + action + "' triggered successfully");
        result.put("timestamp", System.currentTimeMillis());
        
        return ResultModel.success(result);
    }

    @PostMapping("/{id}/archive")
    public ResultModel<Map<String, Object>> archive(@PathVariable String id) {
        log.info("[SceneCapabilityController] Archive capability: {}", id);
        
        SceneCapabilityDTO capability = capabilityStore.get(id);
        if (capability != null) {
            capability.setActive(false);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("capabilityId", id);
        result.put("status", "ARCHIVED");
        result.put("message", "Capability archived successfully");
        result.put("archivedAt", System.currentTimeMillis());
        
        return ResultModel.success(result);
    }
}
