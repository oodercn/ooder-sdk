package net.ooder.scene.llm.config.layered;

import java.util.*;

/**
 * 配置合并策略接口
 *
 * @author ooder
 * @since 2.4
 */
public interface ConfigMergeStrategy {

    /**
     * 合并两个配置
     *
     * @param base 基础配置
     * @param overlay 覆盖配置
     * @return 合并后的配置
     */
    LlmConfigProperties merge(LlmConfigProperties base, LlmConfigProperties overlay);
}
