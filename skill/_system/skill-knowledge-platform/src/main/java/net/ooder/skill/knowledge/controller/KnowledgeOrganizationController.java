package net.ooder.skill.knowledge.controller;

import net.ooder.scene.skill.knowledge.KnowledgeOrganization;
import net.ooder.scene.skill.knowledge.KnowledgeOrganizationService;
import net.ooder.skill.knowledge.dto.KnowledgeOrgDTO;
import net.ooder.skill.knowledge.dto.OrganizationKnowledgeBasesDTO;
import net.ooder.skill.knowledge.dto.OrganizationStatsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/knowledge-organizations")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class KnowledgeOrganizationController {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeOrganizationController.class);

    @Autowired(required = false)
    private KnowledgeOrganizationService knowledgeOrganizationService;

    @GetMapping
    public ResultModel<List<KnowledgeOrgDTO>> listOrganizations() {
        log.info("[listOrganizations] request start");
        if (knowledgeOrganizationService == null) {
            return ResultModel.error(503, "KnowledgeOrganizationService unavailable");
        }
        List<KnowledgeOrganization> list = knowledgeOrganizationService.listAll();
        List<KnowledgeOrgDTO> result = list.stream()
            .sorted(Comparator.comparingInt(KnowledgeOrganization::getSort))
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResultModel.success(result);
    }

    @GetMapping("/{orgId}")
    public ResultModel<KnowledgeOrgDTO> getOrganization(@PathVariable String orgId) {
        log.info("[getOrganization] orgId: {}", orgId);
        if (knowledgeOrganizationService == null) {
            return ResultModel.error(503, "KnowledgeOrganizationService unavailable");
        }
        KnowledgeOrganization org = knowledgeOrganizationService.get(orgId);
        if (org == null) {
            return ResultModel.error(404, "Organization not found: " + orgId);
        }
        return ResultModel.success(toDTO(org));
    }

    @PostMapping
    public ResultModel<KnowledgeOrgDTO> createOrganization(@RequestBody KnowledgeOrgDTO request) {
        log.info("[createOrganization] name: {}, type: {}", request.getName(), request.getType());
        if (knowledgeOrganizationService == null) {
            return ResultModel.error(503, "KnowledgeOrganizationService unavailable");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResultModel.error(400, "Name is required");
        }

        KnowledgeOrganization org = new KnowledgeOrganization();
        org.setOrgId(request.getOrgId() != null ? request.getOrgId() : "org-" + UUID.randomUUID().toString().substring(0, 8));
        org.setName(request.getName());
        org.setType(request.getType() != null ? request.getType() : "business");
        org.setParentId(request.getParentId());
        org.setSort(request.getSort() > 0 ? request.getSort() : 0);
        org.setDescription(request.getDescription());
        org.setKbIds(new ArrayList<>());

        KnowledgeOrganization created = knowledgeOrganizationService.create(org);
        return ResultModel.success(toDTO(created));
    }

    @PutMapping("/{orgId}")
    public ResultModel<KnowledgeOrgDTO> updateOrganization(
            @PathVariable String orgId,
            @RequestBody KnowledgeOrgDTO request) {
        log.info("[updateOrganization] orgId: {}", orgId);
        if (knowledgeOrganizationService == null) {
            return ResultModel.error(503, "KnowledgeOrganizationService unavailable");
        }

        KnowledgeOrganization existing = knowledgeOrganizationService.get(orgId);
        if (existing == null) {
            return ResultModel.error(404, "Organization not found: " + orgId);
        }

        if (request.getName() != null) existing.setName(request.getName());
        if (request.getDescription() != null) existing.setDescription(request.getDescription());
        if (request.getSort() > 0) existing.setSort(request.getSort());
        if (request.getParentId() != null) existing.setParentId(request.getParentId());

        KnowledgeOrganization updated = knowledgeOrganizationService.update(orgId, existing);
        return ResultModel.success(toDTO(updated));
    }

    @DeleteMapping("/{orgId}")
    public ResultModel<Boolean> deleteOrganization(@PathVariable String orgId) {
        log.info("[deleteOrganization] orgId: {}", orgId);
        if (knowledgeOrganizationService == null) {
            return ResultModel.error(503, "KnowledgeOrganizationService unavailable");
        }

        if ("org-company".equals(orgId) || "org-department".equals(orgId) || "org-special".equals(orgId)) {
            return ResultModel.error(400, "Cannot delete default organization");
        }

        knowledgeOrganizationService.delete(orgId);
        return ResultModel.success(true);
    }

    @GetMapping("/{orgId}/knowledge-bases")
    public ResultModel<OrganizationKnowledgeBasesDTO> getOrganizationKnowledgeBases(
            @PathVariable String orgId,
            @RequestParam(required = false, defaultValue = "false") boolean includeStats) {
        log.info("[getOrganizationKnowledgeBases] orgId: {}", orgId);
        if (knowledgeOrganizationService == null) {
            return ResultModel.error(503, "KnowledgeOrganizationService unavailable");
        }

        KnowledgeOrganization org = knowledgeOrganizationService.get(orgId);
        if (org == null) {
            return ResultModel.error(404, "Organization not found: " + orgId);
        }

        OrganizationKnowledgeBasesDTO result = new OrganizationKnowledgeBasesDTO();
        result.setOrganization(toDTO(org));
        List<String> kbIds = org.getKbIds() != null ? org.getKbIds() : new ArrayList<>();
        result.setKnowledgeBases(kbIds);

        if (includeStats) {
            OrganizationStatsDTO stats = new OrganizationStatsDTO();
            stats.setKbCount(kbIds.size());
            stats.setDocCount(knowledgeOrganizationService.getTotalDocs(orgId));
            result.setStats(stats);
        }

        return ResultModel.success(result);
    }

    @PostMapping("/{orgId}/knowledge-bases/{kbId}")
    public ResultModel<Boolean> bindKnowledgeBase(
            @PathVariable String orgId,
            @PathVariable String kbId) {
        log.info("[bindKnowledgeBase] orgId: {}, kbId: {}", orgId, kbId);
        if (knowledgeOrganizationService == null) {
            return ResultModel.error(503, "KnowledgeOrganizationService unavailable");
        }

        knowledgeOrganizationService.addKnowledgeBase(orgId, kbId);
        return ResultModel.success(true);
    }

    @DeleteMapping("/{orgId}/knowledge-bases/{kbId}")
    public ResultModel<Boolean> unbindKnowledgeBase(
            @PathVariable String orgId,
            @PathVariable String kbId) {
        log.info("[unbindKnowledgeBase] orgId: {}, kbId: {}", orgId, kbId);
        if (knowledgeOrganizationService == null) {
            return ResultModel.error(503, "KnowledgeOrganizationService unavailable");
        }

        KnowledgeOrganization org = knowledgeOrganizationService.get(orgId);
        if (org != null && org.getKbIds() != null) {
            org.removeKnowledgeBase(kbId);
            knowledgeOrganizationService.update(orgId, org);
        }
        return ResultModel.success(true);
    }

    private KnowledgeOrgDTO toDTO(KnowledgeOrganization org) {
        KnowledgeOrgDTO dto = new KnowledgeOrgDTO();
        dto.setOrgId(org.getOrgId());
        dto.setName(org.getName());
        dto.setType(org.getType());
        dto.setParentId(org.getParentId());
        dto.setSort(org.getSort());
        dto.setDescription(org.getDescription());
        dto.setKbIds(org.getKbIds() != null ? new ArrayList<>(org.getKbIds()) : new ArrayList<>());
        dto.setCreatedAt(new Date(org.getCreatedAt()));
        dto.setUpdatedAt(new Date(org.getUpdatedAt()));
        return dto;
    }

    public static class ResultModel<T> {
        private int code;
        private String status;
        private String message;
        private T data;
        private long timestamp;
        private String requestId;

        public ResultModel() {
            this.timestamp = System.currentTimeMillis();
            this.requestId = "REQ_" + timestamp + "_" + new Random().nextInt(1000);
        }

        public static <T> ResultModel<T> success(T data) {
            ResultModel<T> result = new ResultModel<>();
            result.setCode(200);
            result.setStatus("success");
            result.setMessage("操作成功");
            result.setData(data);
            return result;
        }

        public static <T> ResultModel<T> error(int code, String message) {
            ResultModel<T> result = new ResultModel<>();
            result.setCode(code);
            result.setStatus("error");
            result.setMessage(message);
            return result;
        }

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
    }
}
