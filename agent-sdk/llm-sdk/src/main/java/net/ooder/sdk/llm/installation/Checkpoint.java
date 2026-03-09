package net.ooder.sdk.llm.installation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 检查点
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Checkpoint {

    /**
     * 检查点ID
     */
    private String checkpointId;

    /**
     * 步骤ID
     */
    private String stepId;

    /**
     * 检查点名称
     */
    private String name;

    /**
     * 创建时间戳
     */
    @Builder.Default
    private long timestamp = System.currentTimeMillis();

    /**
     * 状态数据
     */
    private Map<String, Object> state;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否自动创建
     */
    @Builder.Default
    private boolean autoCreated = false;

    /**
     * 创建检查点
     */
    public static Checkpoint create(String stepId, String name, Map<String, Object> state) {
        return Checkpoint.builder()
                .checkpointId(generateCheckpointId(stepId))
                .stepId(stepId)
                .name(name)
                .state(state)
                .build();
    }

    /**
     * 创建自动检查点
     */
    public static Checkpoint autoCreate(String stepId, Map<String, Object> state) {
        return Checkpoint.builder()
                .checkpointId(generateCheckpointId(stepId))
                .stepId(stepId)
                .name("Auto checkpoint for " + stepId)
                .state(state)
                .autoCreated(true)
                .build();
    }

    /**
     * 生成检查点ID
     */
    private static String generateCheckpointId(String stepId) {
        return stepId + "_cp_" + System.currentTimeMillis();
    }
}
