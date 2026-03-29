package net.ooder.scene.todo;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 待办状态枚举
 * 
 * <p>定义待办的生命周期状态。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public enum TodoStatus {
    
    PENDING("pending", "待处理"),
    ACCEPTED("accepted", "已接受"),
    REJECTED("rejected", "已拒绝"),
    COMPLETED("completed", "已完成"),
    APPROVED("approved", "已审批"),
    EXPIRED("expired", "已过期"),
    CANCELLED("cancelled", "已取消");
    
    private final String code;
    private final String displayName;
    
    TodoStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    @JsonValue
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static TodoStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (TodoStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
    
    public boolean isTerminal() {
        return this == COMPLETED || this == REJECTED || this == EXPIRED || this == CANCELLED;
    }
    
    public boolean canAccept() {
        return this == PENDING;
    }
    
    public boolean canReject() {
        return this == PENDING;
    }
    
    public boolean canComplete() {
        return this == ACCEPTED || this == APPROVED;
    }
    
    public boolean canApprove() {
        return this == PENDING && this != APPROVED;
    }
}
