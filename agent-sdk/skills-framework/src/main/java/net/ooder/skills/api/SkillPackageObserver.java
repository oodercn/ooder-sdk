
package net.ooder.skills.api;

import net.ooder.skills.api.InstallResult;

public interface SkillPackageObserver {
    
    void onInstalling(String skillId);
    
    void onInstalled(String skillId, InstallResult result);
    
    void onUninstalling(String skillId);
    
    void onUninstalled(String skillId);
    
    void onUpdateStarted(String skillId, String targetVersion);
    
    void onUpdateCompleted(String skillId, String newVersion);
    
    void onError(String skillId, String error);
}
