package net.ooder.skill.knowledge.dto;

import java.util.List;

public class OrganizationKnowledgeBasesDTO {
    
    private KnowledgeOrgDTO organization;
    private List<String> knowledgeBases;
    private OrganizationStatsDTO stats;

    public KnowledgeOrgDTO getOrganization() { return organization; }
    public void setOrganization(KnowledgeOrgDTO organization) { this.organization = organization; }
    public List<String> getKnowledgeBases() { return knowledgeBases; }
    public void setKnowledgeBases(List<String> knowledgeBases) { this.knowledgeBases = knowledgeBases; }
    public OrganizationStatsDTO getStats() { return stats; }
    public void setStats(OrganizationStatsDTO stats) { this.stats = stats; }
}
