package net.ooder.skill.capability.service.impl;

import net.ooder.skill.capability.dto.CapabilityStatsDTO;
import net.ooder.skill.capability.dto.CapabilityRankDTO;
import net.ooder.skill.capability.dto.LogEntryDTO;
import net.ooder.skill.capability.dto.ScoreDistributionDTO;
import net.ooder.skill.capability.dto.CategoryDistributionDTO;
import net.ooder.skill.capability.model.Capability;
import net.ooder.skill.capability.model.CapabilityCategory;
import net.ooder.skill.capability.model.CapabilityStatus;
import net.ooder.skill.capability.service.CapabilityService;
import net.ooder.skill.capability.service.CapabilityStatsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CapabilityStatsServiceImpl implements CapabilityStatsService {

    private static final Logger log = LoggerFactory.getLogger(CapabilityStatsServiceImpl.class);

    @Autowired(required = false)
    private CapabilityService capabilityService;

    @Override
    public CapabilityStatsDTO getOverviewStats() {
        log.info("[getOverviewStats] Getting capability overview stats from real data");
        
        CapabilityStatsDTO stats = new CapabilityStatsDTO();
        
        if (capabilityService == null) {
            log.warn("[getOverviewStats] CapabilityService not available, returning empty stats");
            stats.setTotalCapabilities(0);
            stats.setActiveCapabilities(0);
            stats.setTotalInvocations(0);
            stats.setSuccessInvocations(0);
            stats.setFailedInvocations(0);
            stats.setAvgResponseTime(0);
            return stats;
        }
        
        List<Capability> capabilities = capabilityService.findAll();
        
        int totalCapabilities = capabilities.size();
        int activeCapabilities = 0;
        int installedCapabilities = 0;
        
        for (Capability cap : capabilities) {
            if (cap.isInstalled()) {
                installedCapabilities++;
            }
            if (cap.getStatus() != null && 
                (cap.getStatus() == CapabilityStatus.ENABLED || cap.getStatus() == CapabilityStatus.REGISTERED)) {
                activeCapabilities++;
            }
        }
        
        stats.setTotalCapabilities(totalCapabilities);
        stats.setActiveCapabilities(activeCapabilities);
        stats.setInstalledCapabilities(installedCapabilities);
        stats.setTotalInvocations(0);
        stats.setSuccessInvocations(0);
        stats.setFailedInvocations(0);
        stats.setAvgResponseTime(0.0);
        
        log.info("[getOverviewStats] Stats: total={}, active={}, installed={}", 
            totalCapabilities, activeCapabilities, installedCapabilities);
        
        return stats;
    }

    @Override
    public List<CapabilityRankDTO> getTopCapabilities(int limit) {
        log.info("[getTopCapabilities] Getting top {} capabilities from real data", limit);
        
        List<CapabilityRankDTO> result = new ArrayList<>();
        
        if (capabilityService == null) {
            log.warn("[getTopCapabilities] CapabilityService not available");
            return result;
        }
        
        List<Capability> capabilities = capabilityService.findAll();
        
        int rank = 0;
        for (Capability cap : capabilities) {
            if (rank >= limit) break;
            
            CapabilityRankDTO dto = new CapabilityRankDTO();
            dto.setCapabilityId(cap.getCapabilityId());
            dto.setName(cap.getName());
            dto.setType(cap.getCapabilityType() != null ? cap.getCapabilityType().name() : "SERVICE");
            dto.setInvokeCount(0);
            dto.setSuccessRate(0.0);
            dto.setAvgResponseTime(0.0);
            dto.setCategory(cap.getCapabilityCategory() != null ? cap.getCapabilityCategory().getCode() : "sys");
            dto.setStatus(cap.getStatus() != null ? cap.getStatus().name() : "REGISTERED");
            dto.setScore(cap.getBusinessSemanticsScore() != null ? cap.getBusinessSemanticsScore() : 0);
            
            result.add(dto);
            rank++;
        }
        
        return result;
    }

    @Override
    public List<CapabilityRankDTO> getCapabilityRank(String sortBy, int limit) {
        log.info("[getCapabilityRank] Getting capability rank by {} limit {} from real data", sortBy, limit);
        return getTopCapabilities(limit);
    }

    @Override
    public List<String> getRecentErrors(int limit) {
        log.info("[getRecentErrors] Getting recent {} errors", limit);
        
        List<String> errors = new ArrayList<>();
        
        if (capabilityService != null) {
            List<Capability> capabilities = capabilityService.findAll();
            
            for (Capability cap : capabilities) {
                if (errors.size() >= limit) break;
                
                if (cap.getStatus() != null && 
                    (cap.getStatus().name().contains("ERROR") || cap.getStatus().name().contains("FAILED"))) {
                    errors.add(String.format("[%s] %s: 状态异常 - %s", 
                        new Date(), cap.getName(), cap.getStatus()));
                }
            }
        }
        
        if (errors.isEmpty()) {
            errors.add("暂无错误记录");
        }
        
        return errors;
    }

    @Override
    public List<LogEntryDTO> getRecentLogs(int limit) {
        log.info("[getRecentLogs] Getting recent {} logs from real data", limit);
        
        List<LogEntryDTO> logs = new ArrayList<>();
        
        if (capabilityService != null) {
            List<Capability> capabilities = capabilityService.findAll();
            
            int count = 0;
            for (Capability cap : capabilities) {
                if (count >= limit) break;
                
                LogEntryDTO logEntry = new LogEntryDTO();
                logEntry.setTimestamp(cap.getUpdateTime() > 0 ? cap.getUpdateTime() : System.currentTimeMillis());
                logEntry.setCapabilityId(cap.getCapabilityId());
                logEntry.setCapabilityName(cap.getName());
                logEntry.setLevel("INFO");
                logEntry.setMessage(String.format("能力 %s 已注册", cap.getName()));
                logEntry.setDuration(0L);
                
                logs.add(logEntry);
                count++;
            }
        }
        
        return logs;
    }

    @Override
    public ScoreDistributionDTO getScoreDistribution() {
        log.info("[getScoreDistribution] Getting score distribution");
        
        ScoreDistributionDTO dto = new ScoreDistributionDTO();
        
        if (capabilityService == null) {
            log.warn("[getScoreDistribution] CapabilityService not available");
            dto.setAvgScore(0.0);
            dto.setHighCount(0);
            dto.setMediumCount(0);
            dto.setLowCount(0);
            dto.setDistribution(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
            return dto;
        }
        
        List<Capability> capabilities = capabilityService.findAll();
        
        int highCount = 0;
        int mediumCount = 0;
        int lowCount = 0;
        double totalScore = 0.0;
        int[] distribution = new int[11];
        
        for (Capability cap : capabilities) {
            Integer scoreValue = cap.getBusinessSemanticsScore();
            double score = (scoreValue != null && scoreValue > 0) ? scoreValue : 5.0;
            totalScore += score;
            
            int scoreInt = (int) Math.min(10, Math.max(0, score));
            distribution[scoreInt]++;
            
            if (score >= 8) {
                highCount++;
            } else if (score >= 5) {
                mediumCount++;
            } else {
                lowCount++;
            }
        }
        
        int total = capabilities.size();
        dto.setAvgScore(total > 0 ? totalScore / total : 0.0);
        dto.setHighCount(highCount);
        dto.setMediumCount(mediumCount);
        dto.setLowCount(lowCount);
        dto.setDistribution(Arrays.stream(distribution).boxed().collect(Collectors.toList()));
        
        return dto;
    }

    @Override
    public List<CategoryDistributionDTO> getCategoryDistribution() {
        log.info("[getCategoryDistribution] Getting category distribution");
        
        List<CategoryDistributionDTO> result = new ArrayList<>();
        
        String[] colors = {"#9334ff", "#10b981", "#f97316", "#4f46e5", "#6b7280"};
        
        if (capabilityService == null) {
            log.warn("[getCategoryDistribution] CapabilityService not available");
            return result;
        }
        
        List<Capability> capabilities = capabilityService.findAll();
        Map<String, Integer> categoryCount = new HashMap<>();
        
        for (Capability cap : capabilities) {
            String category = cap.getCapabilityCategory() != null ? 
                cap.getCapabilityCategory().getName() : "其他";
            categoryCount.merge(category, 1, Integer::sum);
        }
        
        int colorIndex = 0;
        for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            result.add(new CategoryDistributionDTO(
                entry.getKey(), 
                entry.getValue(), 
                colors[colorIndex % colors.length]
            ));
            colorIndex++;
        }
        
        result.sort((a, b) -> Integer.compare(b.getCount(), a.getCount()));
        
        return result;
    }

    @Override
    public long getTotalInvokeCount(String capabilityId) {
        return 0;
    }

    @Override
    public double getAverageLatency(String capabilityId) {
        return 0.0;
    }

    @Override
    public double getSuccessRate(String capabilityId) {
        return 0.0;
    }
}
