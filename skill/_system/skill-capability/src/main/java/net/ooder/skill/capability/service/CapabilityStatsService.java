package net.ooder.skill.capability.service;

import java.util.List;
import net.ooder.skill.capability.dto.CapabilityStatsDTO;
import net.ooder.skill.capability.dto.CapabilityRankDTO;
import net.ooder.skill.capability.dto.LogEntryDTO;
import net.ooder.skill.capability.dto.ScoreDistributionDTO;
import net.ooder.skill.capability.dto.CategoryDistributionDTO;

public interface CapabilityStatsService {
    
    CapabilityStatsDTO getOverviewStats();
    
    List<CapabilityRankDTO> getTopCapabilities(int limit);
    
    List<CapabilityRankDTO> getCapabilityRank(String sortBy, int limit);
    
    List<String> getRecentErrors(int limit);
    
    List<LogEntryDTO> getRecentLogs(int limit);
    
    ScoreDistributionDTO getScoreDistribution();
    
    List<CategoryDistributionDTO> getCategoryDistribution();
    
    long getTotalInvokeCount(String capabilityId);
    
    double getAverageLatency(String capabilityId);
    
    double getSuccessRate(String capabilityId);
}
