
package net.ooder.skills.core.installer;

public class InstallException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public InstallException(String message) {
        super(message);
    }
    
    public InstallException(String message, Throwable cause) {
        super(message, cause);
    }
}
