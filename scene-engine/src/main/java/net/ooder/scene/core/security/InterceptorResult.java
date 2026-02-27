package net.ooder.scene.core.security;

/**
 * 拦截器结果
 */
public class InterceptorResult {
    private boolean allowed;
    private String denyReason;
    private String requiredAction;

    public InterceptorResult() {}

    public static InterceptorResult allow() {
        InterceptorResult result = new InterceptorResult();
        result.setAllowed(true);
        return result;
    }

    public static InterceptorResult deny(String reason) {
        InterceptorResult result = new InterceptorResult();
        result.setAllowed(false);
        result.setDenyReason(reason);
        return result;
    }

    public static InterceptorResult requireAction(String action) {
        InterceptorResult result = new InterceptorResult();
        result.setAllowed(false);
        result.setRequiredAction(action);
        return result;
    }

    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }
    public String getDenyReason() { return denyReason; }
    public void setDenyReason(String denyReason) { this.denyReason = denyReason; }
    public String getRequiredAction() { return requiredAction; }
    public void setRequiredAction(String requiredAction) { this.requiredAction = requiredAction; }
}
