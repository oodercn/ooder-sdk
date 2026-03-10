package net.ooder.scene.llm.config.layered;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 深度合并策略
 *
 * <p>合并规则：</p>
 * <ul>
 *   <li>基础类型直接覆盖</li>
 *   <li>List 追加</li>
 *   <li>Map 深度合并</li>
 * </ul>
 *
 * @author ooder
 * @since 2.4
 */
public class DeepMergeStrategy implements ConfigMergeStrategy {

    @Override
    public LlmConfigProperties merge(LlmConfigProperties base, LlmConfigProperties overlay) {
        if (base == null) return overlay;
        if (overlay == null) return base;

        LlmConfigProperties result = new LlmConfigProperties();

        // 基础类型覆盖
        result.setProvider(overlay.getProvider() != null ? overlay.getProvider() : base.getProvider());
        result.setModel(overlay.getModel() != null ? overlay.getModel() : base.getModel());
        result.setApiKey(overlay.getApiKey() != null ? overlay.getApiKey() : base.getApiKey());
        result.setBaseUrl(overlay.getBaseUrl() != null ? overlay.getBaseUrl() : base.getBaseUrl());
        result.setTemperature(overlay.getTemperature() != null ? overlay.getTemperature() : base.getTemperature());
        result.setMaxTokens(overlay.getMaxTokens() != null ? overlay.getMaxTokens() : base.getMaxTokens());
        result.setTimeout(overlay.getTimeout() != null ? overlay.getTimeout() : base.getTimeout());
        result.setRetryCount(overlay.getRetryCount() != null ? overlay.getRetryCount() : base.getRetryCount());

        // Provider 列表合并
        List<LlmConfigProperties.ProviderConfig> providers = new ArrayList<>(base.getProviders());
        if (overlay.getProviders() != null) {
            providers.addAll(overlay.getProviders());
        }
        result.setProviders(providers);

        // Function Map 合并
        Map<String, LlmConfigProperties.FunctionConfig> functions = new LinkedHashMap<>(base.getFunctions());
        if (overlay.getFunctions() != null) {
            functions.putAll(overlay.getFunctions());
        }
        result.setFunctions(functions);

        // Prompt 合并
        if (overlay.getPrompts() != null) {
            result.setPrompts(overlay.getPrompts());
        } else if (base.getPrompts() != null) {
            result.setPrompts(base.getPrompts());
        }

        // Rule 列表合并
        List<LlmConfigProperties.RuleConfig> rules = new ArrayList<>(base.getRules());
        if (overlay.getRules() != null) {
            rules.addAll(overlay.getRules());
        }
        result.setRules(rules);

        return result;
    }
}
