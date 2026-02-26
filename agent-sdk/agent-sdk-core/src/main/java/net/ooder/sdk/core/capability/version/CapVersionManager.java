package net.ooder.sdk.core.capability.version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapVersionManager {
    
    private static final Logger log = LoggerFactory.getLogger(CapVersionManager.class);
    
    private final Map<String, List<String>> versionHistory = new ConcurrentHashMap<>();
    
    public boolean isCompatible(String capId, String requiredVersion, String actualVersion) {
        if (requiredVersion == null || actualVersion == null) {
            return false;
        }
        
        String[] required = parseVersion(requiredVersion);
        String[] actual = parseVersion(actualVersion);
        
        if (required.length != 3 || actual.length != 3) {
            return false;
        }
        
        if (required[0].equals(actual[0])) {
            if (Integer.parseInt(actual[1]) >= Integer.parseInt(required[1])) {
                return true;
            }
        }
        
        return false;
    }
    
    public int compareVersions(String version1, String version2) {
        String[] v1 = parseVersion(version1);
        String[] v2 = parseVersion(version2);
        
        for (int i = 0; i < 3; i++) {
            int diff = Integer.parseInt(v1[i]) - Integer.parseInt(v2[i]);
            if (diff != 0) {
                return diff;
            }
        }
        return 0;
    }
    
    public void recordVersion(String capId, String version) {
        versionHistory.computeIfAbsent(capId, k -> new ArrayList<>()).add(version);
        log.debug("Recorded version {} for CAP {}", version, capId);
    }
    
    public String getLatestVersion(String capId) {
        List<String> versions = versionHistory.get(capId);
        if (versions == null || versions.isEmpty()) {
            return null;
        }
        
        String latest = versions.get(0);
        for (String version : versions) {
            if (compareVersions(version, latest) > 0) {
                latest = version;
            }
        }
        return latest;
    }
    
    public List<String> getVersions(String capId) {
        return new ArrayList<>(versionHistory.getOrDefault(capId, new ArrayList<>()));
    }
    
    private String[] parseVersion(String version) {
        if (version == null || version.isEmpty()) {
            return new String[]{"0", "0", "0"};
        }
        
        String[] parts = version.split("\\.");
        if (parts.length != 3) {
            return new String[]{"0", "0", "0"};
        }
        
        return parts;
    }
    
    public boolean isNewerVersion(String currentVersion, String newVersion) {
        return compareVersions(newVersion, currentVersion) > 0;
    }
    
    public boolean isBreakingChange(String oldVersion, String newVersion) {
        String[] oldParts = parseVersion(oldVersion);
        String[] newParts = parseVersion(newVersion);
        
        return !oldParts[0].equals(newParts[0]);
    }
}
