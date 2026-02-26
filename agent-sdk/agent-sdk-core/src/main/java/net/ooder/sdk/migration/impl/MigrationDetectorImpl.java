package net.ooder.sdk.migration.impl;

import net.ooder.sdk.api.skill.SkillRegistry;
import net.ooder.sdk.api.skill.InstalledSkill;
import net.ooder.sdk.migration.*;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 迁移检测器实现
 */
public class MigrationDetectorImpl implements MigrationDetector {

    private final SkillRegistry skillRegistry;
    private final DataMigrationEngine migrationEngine;

    private final List<MigrationRule> rules = new CopyOnWriteArrayList<>();
    private final List<MigrationListener> listeners = new CopyOnWriteArrayList<>();

    public MigrationDetectorImpl(SkillRegistry skillRegistry, DataMigrationEngine migrationEngine) {
        this.skillRegistry = skillRegistry;
        this.migrationEngine = migrationEngine;
        initDefaultRules();
    }

    @Override
    public List<MigrationOpportunity> detectAll() {
        List<MigrationOpportunity> opportunities = new ArrayList<>();
        List<InstalledSkill> installedSkills = skillRegistry.getInstalledSkills();

        for (InstalledSkill skill : installedSkills) {
            MigrationOpportunity opportunity = detectForSkill(skill.getSkillId());
            if (opportunity != null) {
                opportunities.add(opportunity);
            }
        }

        return opportunities;
    }

    @Override
    public MigrationOpportunity detectForSkill(String skillId) {
        InstalledSkill skill = skillRegistry.getInstalledSkill(skillId);
        if (skill == null) {
            return null;
        }

        String currentVersion = skill.getVersion();
        String latestVersion = skillRegistry.getLatestVersion(skillId);

        if (latestVersion == null || currentVersion.equals(latestVersion)) {
            return null;
        }

        MigrationOpportunity opportunity = new MigrationOpportunity();
        opportunity.setSkillId(skillId);
        opportunity.setCurrentVersion(currentVersion);
        opportunity.setTargetVersion(latestVersion);
        opportunity.setType(MigrationOpportunity.OpportunityType.UPGRADE);
        opportunity.setDetectedAt(LocalDateTime.now());
        opportunity.setAssessment(assessMigration(skillId, currentVersion, latestVersion));

        if (compareVersions(latestVersion, currentVersion) > 0) {
            opportunity.setReason("New version available: " + latestVersion);
        } else {
            opportunity.setReason("Rollback opportunity detected");
            opportunity.setType(MigrationOpportunity.OpportunityType.ROLLBACK);
        }

        notifyOpportunityDetected(opportunity);
        checkRules(opportunity);

        return opportunity;
    }

    @Override
    public void addMigrationRule(MigrationRule rule) {
        rules.add(rule);
        rules.sort(Comparator.comparingInt(MigrationRule::getPriority).reversed());
    }

    @Override
    public void removeMigrationRule(String ruleId) {
        rules.removeIf(r -> r.getId().equals(ruleId));
    }

    @Override
    public void setRuleEnabled(String ruleId, boolean enabled) {
        rules.stream()
            .filter(r -> r.getId().equals(ruleId))
            .findFirst()
            .ifPresent(r -> r.setEnabled(enabled));
    }

    @Override
    public MigrationAssessment assessMigration(String skillId, String fromVersion, String toVersion) {
        MigrationAssessment assessment = new MigrationAssessment();

        boolean needsMigration = migrationEngine.needsMigration(skillId, fromVersion, toVersion);
        List<MigrationScript> scripts = migrationEngine.findMigrationScripts(skillId, fromVersion, toVersion);

        if (!needsMigration || scripts.isEmpty()) {
            assessment.setRiskLevel(MigrationAssessment.RiskLevel.LOW);
            assessment.setEstimatedDowntime(0);
            assessment.setServiceDisruptionRequired(false);
            assessment.setSuccessProbability(1.0);
            return assessment;
        }

        long dataSize = estimateDataSize(skillId);
        int scriptCount = scripts.size();
        boolean hasBackup = scripts.stream().anyMatch(MigrationScript::isBackupRequired);

        assessment.setEstimatedDataSize(dataSize);
        assessment.setServiceDisruptionRequired(hasBackup);

        if (scriptCount == 1 && !hasBackup) {
            assessment.setRiskLevel(MigrationAssessment.RiskLevel.LOW);
            assessment.setEstimatedDowntime(1000);
            assessment.setSuccessProbability(0.95);
        } else if (scriptCount <= 3) {
            assessment.setRiskLevel(MigrationAssessment.RiskLevel.MEDIUM);
            assessment.setEstimatedDowntime(5000);
            assessment.setSuccessProbability(0.85);
        } else {
            assessment.setRiskLevel(MigrationAssessment.RiskLevel.HIGH);
            assessment.setEstimatedDowntime(30000);
            assessment.setSuccessProbability(0.70);
        }

        assessment.setPrerequisites(new String[]{
            "Backup available",
            "Sufficient disk space",
            "Service in stable state"
        });

        return assessment;
    }

    @Override
    public boolean isMigrationAllowed(MigrationOpportunity opportunity) {
        for (MigrationRule rule : rules) {
            if (rule.matches(opportunity)) {
                if (rule.getAction() == MigrationRule.Action.BLOCK) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<MigrationRule> getRules() {
        return new ArrayList<>(rules);
    }

    @Override
    public void addListener(MigrationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(MigrationListener listener) {
        listeners.remove(listener);
    }

    private void initDefaultRules() {
        MigrationRule criticalRiskRule = new MigrationRule();
        criticalRiskRule.setId("block-critical-risk");
        criticalRiskRule.setName("Block Critical Risk");
        criticalRiskRule.setDescription("Block migrations with critical risk level");
        criticalRiskRule.setCondition(opp ->
            opp.getAssessment() != null &&
            opp.getAssessment().getRiskLevel() == MigrationAssessment.RiskLevel.CRITICAL
        );
        criticalRiskRule.setAction(MigrationRule.Action.BLOCK);
        criticalRiskRule.setPriority(100);
        criticalRiskRule.setEnabled(true);
        addMigrationRule(criticalRiskRule);

        MigrationRule lowSuccessRule = new MigrationRule();
        lowSuccessRule.setId("require-approval-low-success");
        lowSuccessRule.setName("Require Approval for Low Success");
        lowSuccessRule.setDescription("Require approval when success probability is low");
        lowSuccessRule.setCondition(opp ->
            opp.getAssessment() != null &&
            opp.getAssessment().getSuccessProbability() < 0.5
        );
        lowSuccessRule.setAction(MigrationRule.Action.REQUIRE_APPROVAL);
        lowSuccessRule.setPriority(90);
        lowSuccessRule.setEnabled(true);
        addMigrationRule(lowSuccessRule);
    }

    private void checkRules(MigrationOpportunity opportunity) {
        for (MigrationRule rule : rules) {
            if (rule.matches(opportunity)) {
                notifyRuleMatched(opportunity, rule);
            }
        }
    }

    private long estimateDataSize(String skillId) {
        try {
            Path dataDir = Paths.get("data", skillId);
            if (!Files.exists(dataDir)) {
                return 0;
            }
            return Files.walk(dataDir)
                .filter(Files::isRegularFile)
                .mapToLong(p -> {
                    try {
                        return Files.size(p);
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .sum();
        } catch (Exception e) {
            return 0;
        }
    }

    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
            int p1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int p2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (p1 != p2) {
                return Integer.compare(p1, p2);
            }
        }
        return 0;
    }

    private void notifyOpportunityDetected(MigrationOpportunity opportunity) {
        for (MigrationListener listener : listeners) {
            try {
                listener.onOpportunityDetected(opportunity);
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    private void notifyRuleMatched(MigrationOpportunity opportunity, MigrationRule rule) {
        for (MigrationListener listener : listeners) {
            try {
                listener.onRuleMatched(opportunity, rule);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}
