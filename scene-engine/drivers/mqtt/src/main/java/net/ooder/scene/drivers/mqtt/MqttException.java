package net.ooder.scene.drivers.mqtt;

public class MqttException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private String errorCode;
    
    public MqttException(String message) {
        super(message);
    }
    
    public MqttException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public MqttException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
