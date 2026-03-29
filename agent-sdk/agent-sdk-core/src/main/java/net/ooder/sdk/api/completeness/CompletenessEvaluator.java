package net.ooder.sdk.api.completeness;

import net.ooder.sdk.api.PublicAPI;
import net.ooder.sdk.api.procedure.EnterpriseProcedure;

import java.util.List;

/**
 * Completeness Evaluator Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface CompletenessEvaluator {

    CompletenessDetail evaluate(EnterpriseProcedure procedure);

    List<CompletenessDimensionConfig> getDimensionConfigs();

    void setDimensionConfigs(List<CompletenessDimensionConfig> configs);
}
