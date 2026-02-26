package net.ooder.sdk.core.driver.loader;

import net.ooder.sdk.core.driver.model.DriverPackage;

public interface SkillDriverLoader {
    
    DriverPackage load(String skillId, String version);
    
    DriverPackage loadFromCache(String skillId);
    
    void cache(DriverPackage driver);
    
    boolean isCached(String skillId, String version);
}
