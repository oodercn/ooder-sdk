package net.ooder.skill.capability.controller;

import net.ooder.skill.capability.dto.CapabilityBindingDTO;
import net.ooder.skill.capability.dto.CreateBindingRequest;
import net.ooder.skill.capability.dto.BindingTestResultDTO;
import net.ooder.skill.capability.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/capabilities/bindings")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class CapabilityBindingController {

    private static final Logger log = LoggerFactory.getLogger(CapabilityBindingController.class);

    private final Map<String, CapabilityBindingDTO> bindingStore = new HashMap<>();

    @GetMapping
    public ResultModel<List<CapabilityBindingDTO>> listBindings() {
        log.info("[CapabilityBindingController] List bindings");
        return ResultModel.success(new ArrayList<>(bindingStore.values()));
    }

    @GetMapping("/{id}")
    public ResultModel<CapabilityBindingDTO> getBinding(@PathVariable String id) {
        log.info("[CapabilityBindingController] Get binding: {}", id);
        CapabilityBindingDTO binding = bindingStore.get(id);
        if (binding == null) {
            return ResultModel.notFound("Binding not found: " + id);
        }
        return ResultModel.success(binding);
    }

    @PostMapping
    public ResultModel<CapabilityBindingDTO> createBinding(@RequestBody CreateBindingRequest request) {
        log.info("[CapabilityBindingController] Create binding");
        String id = UUID.randomUUID().toString();
        
        CapabilityBindingDTO binding = new CapabilityBindingDTO();
        binding.setId(id);
        binding.setCapabilityId(request.getCapabilityId());
        binding.setLinkId(request.getLinkId());
        binding.setLinkType(request.getLinkType());
        binding.setStatus(request.getStatus() != null ? request.getStatus() : "active");
        binding.setCreateTime(new Date().toString());
        
        bindingStore.put(id, binding);
        return ResultModel.success(binding);
    }

    @PutMapping("/{id}")
    public ResultModel<CapabilityBindingDTO> updateBinding(@PathVariable String id, @RequestBody CreateBindingRequest request) {
        log.info("[CapabilityBindingController] Update binding: {}", id);
        if (!bindingStore.containsKey(id)) {
            return ResultModel.notFound("Binding not found: " + id);
        }
        
        CapabilityBindingDTO binding = bindingStore.get(id);
        if (request.getCapabilityId() != null) binding.setCapabilityId(request.getCapabilityId());
        if (request.getLinkId() != null) binding.setLinkId(request.getLinkId());
        if (request.getLinkType() != null) binding.setLinkType(request.getLinkType());
        if (request.getStatus() != null) binding.setStatus(request.getStatus());
        binding.setUpdateTime(new Date().toString());
        
        return ResultModel.success(binding);
    }

    @DeleteMapping("/{id}")
    public ResultModel<Boolean> deleteBinding(@PathVariable String id) {
        log.info("[CapabilityBindingController] Delete binding: {}", id);
        if (!bindingStore.containsKey(id)) {
            return ResultModel.notFound("Binding not found: " + id);
        }
        bindingStore.remove(id);
        return ResultModel.success(true);
    }

    @PostMapping("/{id}/test")
    public ResultModel<BindingTestResultDTO> testBinding(@PathVariable String id) {
        log.info("[CapabilityBindingController] Test binding: {}", id);
        BindingTestResultDTO result = new BindingTestResultDTO();
        result.setBindingId(id);
        result.setStatus("success");
        result.setTestTime(new Date().toString());
        return ResultModel.success(result);
    }

    @GetMapping("/by-link/{linkId}")
    public ResultModel<List<CapabilityBindingDTO>> getBindingsByLink(@PathVariable String linkId) {
        log.info("[CapabilityBindingController] Get bindings by link: {}", linkId);
        List<CapabilityBindingDTO> result = new ArrayList<>();
        for (CapabilityBindingDTO binding : bindingStore.values()) {
            if (linkId.equals(binding.getLinkId())) {
                result.add(binding);
            }
        }
        return ResultModel.success(result);
    }
}
