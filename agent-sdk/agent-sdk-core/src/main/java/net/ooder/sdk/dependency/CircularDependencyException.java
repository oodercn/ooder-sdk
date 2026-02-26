package net.ooder.sdk.dependency;

import java.util.Collections;
import java.util.List;

/**
 * 循环依赖异常
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class CircularDependencyException extends Exception {

    private final List<String> cyclePath;

    public CircularDependencyException(String message) {
        super(message);
        this.cyclePath = Collections.emptyList();
    }

    public CircularDependencyException(String message, List<String> cyclePath) {
        super(message);
        this.cyclePath = cyclePath != null ? cyclePath : Collections.emptyList();
    }

    public CircularDependencyException(String message, Throwable cause) {
        super(message, cause);
        this.cyclePath = Collections.emptyList();
    }

    /**
     * 获取循环依赖路径
     *
     * @return 循环路径
     */
    public List<String> getCyclePath() {
        return Collections.unmodifiableList(cyclePath);
    }

    /**
     * 获取格式化的循环路径
     *
     * @return 格式化的路径字符串
     */
    public String getFormattedCycle() {
        if (cyclePath.isEmpty()) {
            return "Unknown cycle";
        }
        return String.join(" -> ", cyclePath);
    }
}
