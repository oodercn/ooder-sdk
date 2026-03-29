package net.ooder.scene.fusion;

import net.ooder.sdk.api.fusion.ProcedureMatchResult;

/**
 * 企业规范匹配结果实体实现
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class ProcedureMatchResultEntity implements ProcedureMatchResult {

    private static final long serialVersionUID = 1L;

    private String procedureId;
    private String procedureName;
    private int matchScore;
    private double roleMatchScore;
    private double capabilityMatchScore;
    private double stepMatchScore;
    private double categoryMatchScore;

    @Override
    public String getProcedureId() {
        return procedureId;
    }

    @Override
    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
    }

    @Override
    public String getProcedureName() {
        return procedureName;
    }

    @Override
    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    @Override
    public int getMatchScore() {
        return matchScore;
    }

    @Override
    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    @Override
    public double getRoleMatchScore() {
        return roleMatchScore;
    }

    @Override
    public void setRoleMatchScore(double roleMatchScore) {
        this.roleMatchScore = roleMatchScore;
    }

    @Override
    public double getCapabilityMatchScore() {
        return capabilityMatchScore;
    }

    @Override
    public void setCapabilityMatchScore(double capabilityMatchScore) {
        this.capabilityMatchScore = capabilityMatchScore;
    }

    @Override
    public double getStepMatchScore() {
        return stepMatchScore;
    }

    @Override
    public void setStepMatchScore(double stepMatchScore) {
        this.stepMatchScore = stepMatchScore;
    }

    @Override
    public double getCategoryMatchScore() {
        return categoryMatchScore;
    }

    @Override
    public void setCategoryMatchScore(double categoryMatchScore) {
        this.categoryMatchScore = categoryMatchScore;
    }
}
