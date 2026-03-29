package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;

/**
 * Procedure Match Result Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface ProcedureMatchResult extends Serializable {

    String getProcedureId();
    void setProcedureId(String procedureId);

    String getProcedureName();
    void setProcedureName(String procedureName);

    int getMatchScore();
    void setMatchScore(int matchScore);

    double getRoleMatchScore();
    void setRoleMatchScore(double roleMatchScore);

    double getCapabilityMatchScore();
    void setCapabilityMatchScore(double capabilityMatchScore);

    double getStepMatchScore();
    void setStepMatchScore(double stepMatchScore);

    double getCategoryMatchScore();
    void setCategoryMatchScore(double categoryMatchScore);
}
