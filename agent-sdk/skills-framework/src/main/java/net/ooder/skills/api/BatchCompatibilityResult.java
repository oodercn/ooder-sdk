package net.ooder.skills.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 批量兼容性检查结果
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class BatchCompatibilityResult {
    
    private boolean allCompatible;
    private int totalCount;
    private int compatibleCount;
    private int incompatibleCount;
    private int unknownCount;
    private List<CompatibilityResult> results;
    private List<String> conflicts;
    
    public BatchCompatibilityResult() {
        this.results = new ArrayList<>();
        this.conflicts = new ArrayList<>();
    }
    
    // Getters and Setters
    
    public boolean isAllCompatible() {
        return allCompatible;
    }
    
    public void setAllCompatible(boolean allCompatible) {
        this.allCompatible = allCompatible;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public int getCompatibleCount() {
        return compatibleCount;
    }
    
    public void setCompatibleCount(int compatibleCount) {
        this.compatibleCount = compatibleCount;
    }
    
    public int getIncompatibleCount() {
        return incompatibleCount;
    }
    
    public void setIncompatibleCount(int incompatibleCount) {
        this.incompatibleCount = incompatibleCount;
    }
    
    public int getUnknownCount() {
        return unknownCount;
    }
    
    public void setUnknownCount(int unknownCount) {
        this.unknownCount = unknownCount;
    }
    
    public List<CompatibilityResult> getResults() {
        return results;
    }
    
    public void setResults(List<CompatibilityResult> results) {
        this.results = results;
        recalculateCounts();
    }
    
    public void addResult(CompatibilityResult result) {
        if (this.results == null) {
            this.results = new ArrayList<>();
        }
        this.results.add(result);
        recalculateCounts();
    }
    
    public List<String> getConflicts() {
        return conflicts;
    }
    
    public void setConflicts(List<String> conflicts) {
        this.conflicts = conflicts;
    }
    
    public void addConflict(String conflict) {
        if (this.conflicts == null) {
            this.conflicts = new ArrayList<>();
        }
        this.conflicts.add(conflict);
    }
    
    /**
     * 获取不兼容的结果列表
     */
    public List<CompatibilityResult> getIncompatibleResults() {
        return results.stream()
            .filter(r -> !r.isCompatible())
            .collect(Collectors.toList());
    }
    
    /**
     * 获取未知的结果列表
     */
    public List<CompatibilityResult> getUnknownResults() {
        return results.stream()
            .filter(r -> r.getStatus() == CompatibilityResult.Status.UNKNOWN)
            .collect(Collectors.toList());
    }
    
    /**
     * 根据Skill ID获取结果
     */
    public CompatibilityResult getResultBySkillId(String skillId) {
        return results.stream()
            .filter(r -> r.getSkillId().equals(skillId))
            .findFirst()
            .orElse(null);
    }
    
    private void recalculateCounts() {
        if (results == null) {
            return;
        }
        this.totalCount = results.size();
        this.compatibleCount = (int) results.stream().filter(CompatibilityResult::isCompatible).count();
        this.incompatibleCount = (int) results.stream().filter(r -> !r.isCompatible() && r.getStatus() != CompatibilityResult.Status.UNKNOWN).count();
        this.unknownCount = (int) results.stream().filter(r -> r.getStatus() == CompatibilityResult.Status.UNKNOWN).count();
        this.allCompatible = incompatibleCount == 0 && unknownCount == 0;
    }
    
    @Override
    public String toString() {
        return "BatchCompatibilityResult{" +
            "allCompatible=" + allCompatible +
            ", totalCount=" + totalCount +
            ", compatibleCount=" + compatibleCount +
            ", incompatibleCount=" + incompatibleCount +
            ", unknownCount=" + unknownCount +
            '}';
    }
}
