package net.ooder.scene.core;

/**
 * Skill 绑定信息
 *
 * <p>表示一个 CAP 能力到 Skill 的绑定关系，包含绑定元数据和状态信息。</p>
 *
 * <h3>绑定属性：</h3>
 * <ul>
 *   <li>skillId - Skill 标识符</li>
 *   <li>capId - 能力标识符</li>
 *   <li>priority - 优先级（数值越大优先级越高）</li>
 *   <li>available - 是否可用</li>
 *   <li>load - 当前负载（0.0-1.0）</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SkillBinding {

    private final String skillId;
    private final String capId;
    private final int priority;
    private volatile boolean available = true;
    private volatile double load = 0.0;
    private volatile long lastInvokeTime = 0;
    private volatile int invokeCount = 0;

    public SkillBinding(String skillId, String capId) {
        this(skillId, capId, 0);
    }

    public SkillBinding(String skillId, String capId, int priority) {
        this.skillId = skillId;
        this.capId = capId;
        this.priority = priority;
    }

    public String getSkillId() {
        return skillId;
    }

    public String getCapId() {
        return capId;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = Math.max(0.0, Math.min(1.0, load));
    }

    public long getLastInvokeTime() {
        return lastInvokeTime;
    }

    public int getInvokeCount() {
        return invokeCount;
    }

    /**
     * 记录一次调用
     */
    public void recordInvoke() {
        this.lastInvokeTime = System.currentTimeMillis();
        this.invokeCount++;
    }

    /**
     * 增加负载
     */
    public void increaseLoad(double delta) {
        setLoad(this.load + delta);
    }

    /**
     * 减少负载
     */
    public void decreaseLoad(double delta) {
        setLoad(this.load - delta);
    }

    @Override
    public String toString() {
        return "SkillBinding{" +
                "skillId='" + skillId + '\'' +
                ", capId='" + capId + '\'' +
                ", priority=" + priority +
                ", available=" + available +
                ", load=" + load +
                '}';
    }
}
