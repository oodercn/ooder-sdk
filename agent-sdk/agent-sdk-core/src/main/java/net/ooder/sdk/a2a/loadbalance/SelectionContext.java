package net.ooder.sdk.a2a.loadbalance;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 选择上下文
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectionContext {

    private String sceneId;
    private String commandType;
    private String userId;
    private Map<String, Object> metadata;
    private boolean preferLocal;

    public static SelectionContext defaultContext() {
        return SelectionContext.builder()
                .preferLocal(false)
                .build();
    }

    public static SelectionContext forScene(String sceneId) {
        return SelectionContext.builder()
                .sceneId(sceneId)
                .build();
    }
}
