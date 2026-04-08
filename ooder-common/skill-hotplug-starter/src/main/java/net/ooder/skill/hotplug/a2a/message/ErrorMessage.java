package net.ooder.skill.hotplug.a2a.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.ooder.skill.hotplug.a2a.error.A2AErrorCode;

/**
 * 错误消息
 * 对应Ooder-A2A规范v1.0 error类型
 */
public class ErrorMessage extends A2AMessage {

    /**
     * 错误码
     */
    @JsonProperty("errorCode")
    private Integer errorCode;

    /**
     * 错误消息
     */
    @JsonProperty("errorMessage")
    private String errorMessage;

    /**
     * 处理建议
     */
    @JsonProperty("suggestion")
    private String suggestion;

    /**
     * 详细错误信息（调试用）
     */
    @JsonProperty("detail")
    private String detail;

    public ErrorMessage() {
        super(MessageType.ERROR);
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public static ErrorMessage of(A2AErrorCode errorCode, String detail) {
        ErrorMessage message = new ErrorMessage();
        message.setErrorCode(errorCode.getCode());
        message.setErrorMessage(errorCode.getMessage());
        message.setSuggestion(errorCode.getSuggestion());
        message.setDetail(detail);
        return message;
    }

    public static ErrorMessage of(A2AErrorCode errorCode) {
        return of(errorCode, null);
    }
}
