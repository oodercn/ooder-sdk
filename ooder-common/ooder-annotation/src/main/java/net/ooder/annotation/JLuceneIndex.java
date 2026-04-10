package net.ooder.annotation;

import java.io.Serializable;

public interface JLuceneIndex extends Serializable {
    
    String getUserId();

    String getUuid();

    void setUuid(String uuid);

    void setUserId(String userId);
    
    String getPath();
    
   
    
}
