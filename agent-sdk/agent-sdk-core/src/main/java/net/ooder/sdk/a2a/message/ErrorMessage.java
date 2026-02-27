package net.ooder.sdk.a2a.message;

/**
 * 错误消息（泛型版本）
 *
 * @param <T> 数据类型
 * @author Ooder Team
 * @version 2.3
 * @since 2.3
 */
public class ErrorMessage<T> extends A2AMessage<T> {

    private int errorCode;
    private String errorMessage;
    private String suggestion;

    public ErrorMessage() {
        super(A2AMessageType.ERROR);
    }
    
    /**
     * 创建通用错误消息（向后兼容）
     */
    public static ErrorMessage<Void> createGeneric() {
        return new ErrorMessage<>();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private ErrorMessage<T> message = new ErrorMessage<>();

        public Builder<T> skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder<T> errorCode(int errorCode) {
            message.setErrorCode(errorCode);
            return this;
        }

        public Builder<T> errorMessage(String errorMessage) {
            message.setErrorMessage(errorMessage);
            return this;
        }

        public Builder<T> suggestion(String suggestion) {
            message.setSuggestion(suggestion);
            return this;
        }
        
        public Builder<T> data(T data) {
            message.setData(data);
            return this;
        }

        public ErrorMessage<T> build() {
            return message;
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
