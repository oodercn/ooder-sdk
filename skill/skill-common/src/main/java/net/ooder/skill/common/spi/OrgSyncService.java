package net.ooder.skill.common.spi;

import net.ooder.skill.common.spi.orgsync.SyncResult;
import net.ooder.skill.common.spi.orgsync.OrgUserInfo;
import net.ooder.skill.common.spi.orgsync.OrgDepartmentInfo;

import java.util.List;

public interface OrgSyncService {
    
    SyncResult syncAll(String platform);
    
    SyncResult syncUsers(String platform);
    
    SyncResult syncDepartments(String platform);
    
    List<OrgUserInfo> getUsers(String platform);
    
    List<OrgUserInfo> getUsersByDepartment(String platform, String departmentId);
    
    OrgUserInfo getUser(String platform, String userId);
    
    List<OrgDepartmentInfo> getDepartments(String platform);
    
    OrgDepartmentInfo getDepartment(String platform, String departmentId);
    
    List<OrgDepartmentInfo> getOrgTree(String platform);
    
    void clearCache(String platform);
    
    List<String> getAvailablePlatforms();
}
