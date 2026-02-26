package net.ooder.sdk.api.driver;

import java.util.Map;

public interface Driver {
    
    void init(DriverConfig config);
    
    void close();
    
    boolean isConnected();
    
    String getDriverName();
    
    String getDriverVersion();
    
    default String getDriverType() {
        return this.getClass().getSimpleName().replace("Driver", "").toLowerCase();
    }
}
