package net.ooder.sdk.a2a.message;

/**
 * 错误消息
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class ErrorMessage extends A2AMessage {

    private int errorCode;
    private String errorMessage;
    private String suggestion;

    public ErrorMessage() {
        super(A2AMessageType.ERROR);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ErrorMessage message = new ErrorMessage();

        public Builder skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder errorCode(int errorCode) {
            message.setErrorCode(errorCode);
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            message.setErrorMessage(errorMessage);
            return this;
        }

        public Builder suggestion(String suggestion) {
            message.setSuggestion(suggestion);
            return this;
        }

        public ErrorMessage build() {
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
