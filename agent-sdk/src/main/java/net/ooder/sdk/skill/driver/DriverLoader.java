package net.ooder.sdk.skill.driver;

public interface DriverLoader {
    
    DriverPackage load(String skillId, String version);
    
    DriverPackage loadFromCache(String skillId);
    
    void cache(DriverPackage driver);
    
    boolean isCached(String skillId, String version);
}