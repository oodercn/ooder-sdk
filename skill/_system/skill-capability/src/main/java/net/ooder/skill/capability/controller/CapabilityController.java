package net.ooder.skill.capability.controller;

import net.ooder.skill.capability.dto.BindingStatusDTO;
import net.ooder.skill.capability.dto.BindingStatusUpdateRequest;
import net.ooder.skill.capability.dto.CapabilityDTO;
import net.ooder.skill.capability.dto.CapabilityStatsDTO;
import net.ooder.skill.capability.dto.CapabilityRankDTO;
import net.ooder.skill.capability.dto.CapabilityTypeDTO;
import net.ooder.skill.capability.dto.LogEntryDTO;
import net.ooder.skill.capability.dto.ScoreDistributionDTO;
import net.ooder.skill.capability.dto.CategoryDistributionDTO;
import net.ooder.skill.capability.dto.PageResult;
import net.ooder.skill.capability.model.ResultModel;
import net.ooder.skill.capability.service.CapabilityStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/capabilities")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class CapabilityController {

    private static final Logger log = LoggerFactory.getLogger(CapabilityController.class);

    @Autowired(required = false)
    private CapabilityStatsService statsService;

    @GetMapping
    public ResultModel<PageResult<CapabilityDTO>> listCapabilities(
            @RequestParam(required = false) Boolean installed,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        log.info("[CapabilityController] List capabilities - installed: {}, type: {}, category: {}", installed, type, category);
        
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        PageResult<CapabilityDTO> result = new PageResult<>();
        result.setList(capabilities);
        result.setTotal(0);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        
        return ResultModel.success(result);
    }

    @GetMapping("/{id}")
    public ResultModel<CapabilityDTO> getCapability(@PathVariable String id) {
        log.info("[CapabilityController] Get capability: {}", id);
        
        CapabilityDTO capability = new CapabilityDTO();
        capability.setId(id);
        capability.setName("Capability " + id);
        capability.setDescription("Sample capability");
        capability.setType("skill");
        capability.setStatus("active");
        
        return ResultModel.success(capability);
    }

    @GetMapping("/discoverable")
    public ResultModel<List<CapabilityDTO>> getDiscoverableCapabilities() {
        log.info("[CapabilityController] Get discoverable capabilities");
        List<CapabilityDTO> capabilities = new ArrayList<>();
        return ResultModel.success(capabilities);
    }

    @GetMapping("/updates")
    public ResultModel<List<CapabilityDTO>> getCapabilityUpdates() {
        log.info("[CapabilityController] Get capability updates");
        List<CapabilityDTO> updates = new ArrayList<>();
        return ResultModel.success(updates);
    }

    @PostMapping("/{id}/deactivate")
    public ResultModel<CapabilityDTO> deactivateCapability(@PathVariable String id) {
        log.info("[CapabilityController] Deactivate capability: {}", id);
        CapabilityDTO capability = new CapabilityDTO();
        capability.setId(id);
        capability.setStatus("inactive");
        return ResultModel.success(capability);
    }

    @GetMapping("/scene/{id}")
    public ResultModel<CapabilityDTO> getSceneCapability(@PathVariable String id) {
        log.info("[CapabilityController] Get scene capability: {}", id);
        CapabilityDTO capability = new CapabilityDTO();
        capability.setId(id);
        capability.setName("Scene Capability " + id);
        return ResultModel.success(capability);
    }

    @PostMapping("/scene/{id}/activate")
    public ResultModel<CapabilityDTO> activateSceneCapability(@PathVariable String id) {
        log.info("[CapabilityController] Activate scene capability: {}", id);
        CapabilityDTO capability = new CapabilityDTO();
        capability.setId(id);
        capability.setStatus("active");
        return ResultModel.success(capability);
    }

    @PostMapping("/scene/{id}/deactivate")
    public ResultModel<CapabilityDTO> deactivateSceneCapability(@PathVariable String id) {
        log.info("[CapabilityController] Deactivate scene capability: {}", id);
        CapabilityDTO capability = new CapabilityDTO();
        capability.setId(id);
        capability.setStatus("inactive");
        return ResultModel.success(capability);
    }

    @GetMapping("/stats/overview")
    public ResultModel<CapabilityStatsDTO> getStatsOverview() {
        log.info("[CapabilityController] Get stats overview");
        
        if (statsService != null) {
            CapabilityStatsDTO stats = statsService.getOverviewStats();
            return ResultModel.success(stats);
        }
        
        CapabilityStatsDTO stats = new CapabilityStatsDTO();
        stats.setTotalCapabilities(0);
        stats.setActiveCapabilities(0);
        stats.setInstalledCapabilities(0);
        stats.setTotalInvocations(0);
        stats.setSuccessInvocations(0);
        stats.setFailedInvocations(0);
        stats.setAvgResponseTime(0);
        
        return ResultModel.success(stats);
    }

    @GetMapping("/stats/rank")
    public ResultModel<List<CapabilityRankDTO>> getCapabilityRank(
            @RequestParam(defaultValue = "invokeCount") String sortBy,
            @RequestParam(defaultValue = "5") int limit) {
        log.info("[CapabilityController] Get capability rank - sortBy: {}, limit: {}", sortBy, limit);
        
        if (statsService != null) {
            List<CapabilityRankDTO> rank = statsService.getCapabilityRank(sortBy, limit);
            return ResultModel.success(rank);
        }
        
        return ResultModel.success(new ArrayList<>());
    }

    @GetMapping("/stats/top")
    public ResultModel<List<CapabilityRankDTO>> getTopCapabilities(@RequestParam(defaultValue = "5") int limit) {
        log.info("[CapabilityController] Get top capabilities - limit: {}", limit);
        
        if (statsService != null) {
            List<CapabilityRankDTO> top = statsService.getTopCapabilities(limit);
            return ResultModel.success(top);
        }
        
        return ResultModel.success(new ArrayList<>());
    }

    @GetMapping("/stats/errors")
    public ResultModel<List<String>> getRecentErrors(@RequestParam(defaultValue = "5") int limit) {
        log.info("[CapabilityController] Get recent errors - limit: {}", limit);
        
        if (statsService != null) {
            List<String> errors = statsService.getRecentErrors(limit);
            return ResultModel.success(errors);
        }
        
        return ResultModel.success(Arrays.asList("暂无错误记录"));
    }

    @GetMapping("/stats/logs")
    public ResultModel<List<LogEntryDTO>> getRecentLogs(@RequestParam(defaultValue = "20") int limit) {
        log.info("[CapabilityController] Get recent logs - limit: {}", limit);
        
        if (statsService != null) {
            List<LogEntryDTO> logs = statsService.getRecentLogs(limit);
            return ResultModel.success(logs);
        }
        
        return ResultModel.success(new ArrayList<>());
    }

    @GetMapping("/stats/scores")
    public ResultModel<ScoreDistributionDTO> getScoreDistribution() {
        log.info("[CapabilityController] Get score distribution");
        
        if (statsService != null) {
            ScoreDistributionDTO distribution = statsService.getScoreDistribution();
            return ResultModel.success(distribution);
        }
        
        ScoreDistributionDTO dto = new ScoreDistributionDTO();
        dto.setAvgScore(0.0);
        dto.setHighCount(0);
        dto.setMediumCount(0);
        dto.setLowCount(0);
        dto.setDistribution(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        
        return ResultModel.success(dto);
    }

    @GetMapping("/stats/categories")
    public ResultModel<List<CategoryDistributionDTO>> getCategoryDistribution() {
        log.info("[CapabilityController] Get category distribution");
        
        if (statsService != null) {
            List<CategoryDistributionDTO> categories = statsService.getCategoryDistribution();
            return ResultModel.success(categories);
        }
        
        return ResultModel.success(new ArrayList<>());
    }

    @GetMapping("/scene/capabilities/search")
    public ResultModel<List<CapabilityDTO>> searchCapabilities(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        log.info("[CapabilityController] Search capabilities - keyword: {}, type: {}, category: {}", keyword, type, category);
        
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        return ResultModel.success(capabilities);
    }

    @GetMapping("/scene/capabilities/types")
    public ResultModel<List<CapabilityTypeDTO>> listCapabilityTypes() {
        log.info("[CapabilityController] List capability types");
        
        List<CapabilityTypeDTO> types = new ArrayList<>();
        
        CapabilityTypeDTO type1 = new CapabilityTypeDTO();
        type1.setCode("skill");
        type1.setName("技能");
        type1.setDescription("技能类型能力");
        types.add(type1);
        
        CapabilityTypeDTO type2 = new CapabilityTypeDTO();
        type2.setCode("agent");
        type2.setName("Agent");
        type2.setDescription("Agent类型能力");
        types.add(type2);
        
        CapabilityTypeDTO type3 = new CapabilityTypeDTO();
        type3.setCode("workflow");
        type3.setName("工作流");
        type3.setDescription("工作流类型能力");
        types.add(type3);
        
        CapabilityTypeDTO type4 = new CapabilityTypeDTO();
        type4.setCode("integration");
        type4.setName("集成");
        type4.setDescription("集成类型能力");
        types.add(type4);
        
        return ResultModel.success(types);
    }

    @PostMapping("/scene/capabilities")
    public ResultModel<CapabilityDTO> createCapability(@RequestBody CapabilityDTO capability) {
        log.info("[CapabilityController] Create capability: {}", capability != null ? capability.getName() : null);
        
        if (capability != null) {
            capability.setId(UUID.randomUUID().toString());
            capability.setCreateTime(java.time.LocalDateTime.now());
            capability.setStatus("active");
        }
        
        return ResultModel.success(capability);
    }

    @PostMapping("/scene/capabilities/bindings/{bindingId}/status")
    public ResultModel<BindingStatusDTO> updateBindingStatus(
            @PathVariable String bindingId,
            @RequestBody BindingStatusUpdateRequest request) {
        log.info("[CapabilityController] Update binding status: {}, request: {}", bindingId, request);
        
        BindingStatusDTO result = new BindingStatusDTO();
        result.setBindingId(bindingId);
        result.setStatus(request.getStatus() != null ? request.getStatus() : "active");
        result.setUpdateTime(java.time.LocalDateTime.now());
        
        return ResultModel.success(result);
    }

    @GetMapping("/scene/capabilities/logs")
    public ResultModel<PageResult<LogEntryDTO>> getSceneCapabilityLogs(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("[CapabilityController] Get scene capability logs - pageNum: {}, pageSize: {}", pageNum, pageSize);
        
        List<LogEntryDTO> logs = new ArrayList<>();
        
        if (statsService != null) {
            logs = statsService.getRecentLogs(pageSize);
        }
        
        PageResult<LogEntryDTO> result = new PageResult<>();
        result.setList(logs);
        result.setTotal(logs.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        
        return ResultModel.success(result);
    }
}
