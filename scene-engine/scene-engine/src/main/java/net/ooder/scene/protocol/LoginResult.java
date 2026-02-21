package net.ooder.scene.protocol;

public class LoginResult {
    private boolean success;
    private String message;
    private Session session;
    private String errorCode;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
}
