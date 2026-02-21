package net.ooder.scene.skills.security;

import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.security.AuditExportResult;
import net.ooder.scene.core.security.AuditLog;
import net.ooder.scene.core.security.AuditLogQuery;
import net.ooder.scene.core.security.OperationResult;
import net.ooder.scene.provider.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Security Provider Implementation
 *
 * <p>Implements SecurityProvider interface, provides security management capabilities</p>
 */
public class SecurityProviderImpl implements SecurityProvider {

    private static final String PROVIDER_NAME = "security-provider";
    private static final String VERSION = "0.7.3";

    private SceneEngine engine;
    private boolean initialized = false;
    private boolean running = false;
    private boolean firewallEnabled = true;

    private final Map<String, SecurityPolicy> policies = new ConcurrentHashMap<>();
    private final Map<String, AccessControl> acls = new ConcurrentHashMap<>();
    private final Map<String, ThreatInfo> threats = new ConcurrentHashMap<>();
    private final Map<String, AuditLog> auditLogs = new ConcurrentHashMap<>();
    private final AtomicLong policyIdCounter = new AtomicLong(1);
    private final AtomicLong aclIdCounter = new AtomicLong(1);
    private final AtomicLong threatIdCounter = new AtomicLong(1);
    private final AtomicLong auditLogIdCounter = new AtomicLong(1);

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        initializeDefaultData();
        this.initialized = true;
    }

    @Override
    public void start() {
        if (!initialized) {
            throw new IllegalStateException("Provider not initialized");
        }
        this.running = true;
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private void initializeDefaultData() {
        createDefaultPolicies();
        createDefaultAcls();
        createDefaultThreats();
        createDefaultAuditLogs();
    }

    private void createDefaultPolicies() {
        String[] policyNames = {
            "Default Allow Policy",
            "Default Deny Policy",
            "Admin Access Policy",
            "Read-Only Policy",
            "Network Security Policy",
            "Data Protection Policy",
            "Authentication Policy",
            "Authorization Policy",
            "Audit Policy",
            "Encryption Policy"
        };

        String[] policyTypes = {
            "ALLOW", "DENY", "ALLOW", "ALLOW", "ALLOW",
            "ALLOW", "ALLOW", "ALLOW", "ALLOW", "ALLOW"
        };

        for (int i = 0; i < policyNames.length; i++) {
            SecurityPolicy policy = new SecurityPolicy();
            policy.setPolicyId("policy-" + policyIdCounter.getAndIncrement());
            policy.setPolicyName(policyNames[i]);
            policy.setPolicyType(policyTypes[i]);
            policy.setDescription("Default policy: " + policyNames[i]);
            policy.setStatus("ENABLED");
            policy.setPriority(100 - i);
            policy.setAction(policyTypes[i]);
            policy.setCreatedAt(System.currentTimeMillis() - 86400000 * (i + 1));
            policy.setUpdatedAt(System.currentTimeMillis());
            policies.put(policy.getPolicyId(), policy);
        }
    }

    private void createDefaultAcls() {
        String[] resources = {"storage", "network", "hosting", "org", "vfs", "mqtt"};
        String[] permissions = {"read", "write", "delete", "admin"};

        for (int i = 0; i < 20; i++) {
            AccessControl acl = new AccessControl();
            acl.setAclId("acl-" + aclIdCounter.getAndIncrement());
            acl.setResourceType(resources[i % resources.length]);
            acl.setResourceId("resource-" + i);
            acl.setPrincipalType("USER");
            acl.setPrincipalId("user-" + (i % 5 + 1));
            acl.setPermission(permissions[i % permissions.length]);
            acl.setStatus("ACTIVE");
            acl.setGrantedAt(System.currentTimeMillis() - 86400000 * (i + 1));
            acl.setGrantedBy("admin");
            acls.put(acl.getAclId(), acl);
        }
    }

    private void createDefaultThreats() {
        String[] threatTypes = {"MALWARE", "INTRUSION", "DDOS", "PHISHING", "VULNERABILITY"};
        String[] severities = {"HIGH", "MEDIUM", "LOW", "CRITICAL", "HIGH"};
        String[] statuses = {"ACTIVE", "RESOLVED", "INVESTIGATING", "ACTIVE", "RESOLVED"};

        for (int i = 0; i < 15; i++) {
            ThreatInfo threat = new ThreatInfo();
            threat.setThreatId("threat-" + threatIdCounter.getAndIncrement());
            threat.setThreatType(threatTypes[i % threatTypes.length]);
            threat.setSeverity(severities[i % severities.length]);
            threat.setSource("192.168.1." + (100 + i));
            threat.setDescription("Detected " + threatTypes[i % threatTypes.length] + " threat");
            threat.setStatus(statuses[i % statuses.length]);
            threat.setRecommendation("Review and take appropriate action");
            threat.setDetectedAt(System.currentTimeMillis() - 3600000 * (i + 1));
            if ("RESOLVED".equals(statuses[i % statuses.length])) {
                threat.setResolvedAt(System.currentTimeMillis() - 1800000 * (i + 1));
            }
            threats.put(threat.getThreatId(), threat);
        }
    }

    private void createDefaultAuditLogs() {
        String[] operations = {"LOGIN", "LOGOUT", "CREATE", "UPDATE", "DELETE", "READ", "INSTALL", "UNINSTALL"};
        String[] resources = {"skill", "scene", "user", "policy", "config", "instance"};
        String[] users = {"user-1", "user-2", "admin", "system"};

        for (int i = 0; i < 50; i++) {
            AuditLog log = new AuditLog();
            log.setLogId("audit-" + auditLogIdCounter.getAndIncrement());
            log.setUserId(users[i % users.length]);
            log.setUserName(users[i % users.length]);
            log.setSessionId("session-" + (i % 10));
            log.setIpAddress("192.168.1." + (100 + (i % 50)));
            log.setOperation(operations[i % operations.length]);
            log.setResource(resources[i % resources.length]);
            log.setResourceId("resource-" + i);
            log.setResult(i % 10 == 0 ? OperationResult.FAILURE : OperationResult.SUCCESS);
            log.setErrorMessage(i % 10 == 0 ? "Permission denied" : null);
            log.setDuration(50 + (i % 200));
            log.setTimestamp(System.currentTimeMillis() - 3600000 * (i + 1));
            Map<String, Object> details = new HashMap<>();
            details.put("source", "web");
            details.put("userAgent", "Mozilla/5.0");
            log.setDetails(details);
            auditLogs.put(log.getLogId(), log);
        }
    }

    @Override
    public SecurityStatus getStatus() {
        SecurityStatus status = new SecurityStatus();
        status.setStatus("HEALTHY");
        status.setSecurityLevel("MEDIUM");
        status.setActivePolicies((int) policies.values().stream()
            .filter(p -> "ENABLED".equals(p.getStatus())).count());
        status.setTotalPolicies(policies.size());
        status.setRecentAlerts((int) threats.values().stream()
            .filter(t -> "ACTIVE".equals(t.getStatus())).count());
        status.setBlockedAttempts(156);
        status.setThreatScore(25.5);
        status.setFirewallEnabled(firewallEnabled);
        status.setEncryptionEnabled(true);
        status.setAuditEnabled(true);
        status.setLastScanTime(System.currentTimeMillis() - 3600000);
        return status;
    }

    @Override
    public SecurityStats getStats() {
        SecurityStats stats = new SecurityStats();
        stats.setTotalPolicies(policies.size());
        stats.setActivePolicies((int) policies.values().stream()
            .filter(p -> "ENABLED".equals(p.getStatus())).count());
        stats.setTotalAcls(acls.size());
        stats.setTotalThreats(threats.size());
        stats.setResolvedThreats((int) threats.values().stream()
            .filter(t -> "RESOLVED".equals(t.getStatus())).count());
        stats.setBlockedAttempts(156);
        stats.setSecurityScans(24);
        return stats;
    }

    @Override
    public List<SecurityPolicy> listPolicies() {
        return new ArrayList<>(policies.values());
    }

    @Override
    public SecurityPolicy getPolicy(String policyId) {
        return policies.get(policyId);
    }

    @Override
    public SecurityPolicy createPolicy(SecurityPolicy policy) {
        if (policy.getPolicyId() == null || policy.getPolicyId().isEmpty()) {
            policy.setPolicyId("policy-" + policyIdCounter.getAndIncrement());
        }
        policy.setCreatedAt(System.currentTimeMillis());
        policy.setUpdatedAt(System.currentTimeMillis());
        policies.put(policy.getPolicyId(), policy);
        return policy;
    }

    @Override
    public boolean updatePolicy(SecurityPolicy policy) {
        if (!policies.containsKey(policy.getPolicyId())) {
            return false;
        }
        policy.setUpdatedAt(System.currentTimeMillis());
        policies.put(policy.getPolicyId(), policy);
        return true;
    }

    @Override
    public boolean deletePolicy(String policyId) {
        return policies.remove(policyId) != null;
    }

    @Override
    public boolean enablePolicy(String policyId) {
        SecurityPolicy policy = policies.get(policyId);
        if (policy == null) {
            return false;
        }
        policy.setStatus("ENABLED");
        policy.setUpdatedAt(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean disablePolicy(String policyId) {
        SecurityPolicy policy = policies.get(policyId);
        if (policy == null) {
            return false;
        }
        policy.setStatus("DISABLED");
        policy.setUpdatedAt(System.currentTimeMillis());
        return true;
    }

    @Override
    public PageResult<AccessControl> listAcls(int page, int size) {
        List<AccessControl> allAcls = new ArrayList<>(acls.values());
        int start = (page - 1) * size;
        int end = Math.min(start + size, allAcls.size());
        List<AccessControl> items = start < allAcls.size() 
            ? allAcls.subList(start, end) 
            : new ArrayList<>();
        return PageResult.of(items, allAcls.size(), page, size);
    }

    @Override
    public AccessControl createAcl(AccessControl acl) {
        if (acl.getAclId() == null || acl.getAclId().isEmpty()) {
            acl.setAclId("acl-" + aclIdCounter.getAndIncrement());
        }
        acl.setGrantedAt(System.currentTimeMillis());
        acls.put(acl.getAclId(), acl);
        return acl;
    }

    @Override
    public boolean deleteAcl(String aclId) {
        return acls.remove(aclId) != null;
    }

    @Override
    public boolean checkPermission(String userId, String resource, String action) {
        return acls.values().stream()
            .anyMatch(acl -> acl.getPrincipalId().equals(userId)
                && acl.getResourceType().equals(resource)
                && (acl.getPermission().equals(action) || acl.getPermission().equals("admin")));
    }

    @Override
    public PageResult<ThreatInfo> listThreats(int page, int size) {
        List<ThreatInfo> allThreats = new ArrayList<>(threats.values());
        int start = (page - 1) * size;
        int end = Math.min(start + size, allThreats.size());
        List<ThreatInfo> items = start < allThreats.size() 
            ? allThreats.subList(start, end) 
            : new ArrayList<>();
        return PageResult.of(items, allThreats.size(), page, size);
    }

    @Override
    public ThreatInfo getThreat(String threatId) {
        return threats.get(threatId);
    }

    @Override
    public boolean resolveThreat(String threatId) {
        ThreatInfo threat = threats.get(threatId);
        if (threat == null) {
            return false;
        }
        threat.setStatus("RESOLVED");
        threat.setResolvedAt(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean runSecurityScan() {
        return true;
    }

    @Override
    public boolean toggleFirewall() {
        firewallEnabled = !firewallEnabled;
        return true;
    }

    @Override
    public boolean isFirewallEnabled() {
        return firewallEnabled;
    }

    @Override
    public PageResult<AuditLog> listAuditLogs(AuditLogQuery query) {
        List<AuditLog> filteredLogs = new ArrayList<>(auditLogs.values());
        
        if (query != null) {
            if (query.getUserId() != null && !query.getUserId().isEmpty()) {
                filteredLogs = filteredLogs.stream()
                    .filter(log -> query.getUserId().equals(log.getUserId()))
                    .collect(Collectors.toList());
            }
            if (query.getOperation() != null && !query.getOperation().isEmpty()) {
                filteredLogs = filteredLogs.stream()
                    .filter(log -> query.getOperation().equals(log.getOperation()))
                    .collect(Collectors.toList());
            }
            if (query.getResource() != null && !query.getResource().isEmpty()) {
                filteredLogs = filteredLogs.stream()
                    .filter(log -> query.getResource().equals(log.getResource()))
                    .collect(Collectors.toList());
            }
            if (query.getResult() != null) {
                filteredLogs = filteredLogs.stream()
                    .filter(log -> query.getResult().equals(log.getResult()))
                    .collect(Collectors.toList());
            }
            if (query.getStartTime() > 0) {
                filteredLogs = filteredLogs.stream()
                    .filter(log -> log.getTimestamp() >= query.getStartTime())
                    .collect(Collectors.toList());
            }
            if (query.getEndTime() > 0) {
                filteredLogs = filteredLogs.stream()
                    .filter(log -> log.getTimestamp() <= query.getEndTime())
                    .collect(Collectors.toList());
            }
        }
        
        filteredLogs.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        
        int page = query != null && query.getPageNum() > 0 ? query.getPageNum() : 1;
        int size = query != null && query.getPageSize() > 0 ? query.getPageSize() : 20;
        int start = (page - 1) * size;
        int end = Math.min(start + size, filteredLogs.size());
        List<AuditLog> items = start < filteredLogs.size() 
            ? filteredLogs.subList(start, end) 
            : new ArrayList<>();
        
        return PageResult.of(items, filteredLogs.size(), page, size);
    }

    @Override
    public AuditExportResult exportAuditLogs(AuditLogQuery query) {
        PageResult<AuditLog> result = listAuditLogs(query);
        
        AuditExportResult exportResult = new AuditExportResult();
        exportResult.setExportId("export-" + UUID.randomUUID().toString());
        exportResult.setExportedAt(System.currentTimeMillis());
        exportResult.setTotalRecords(result.getTotal());
        exportResult.setRecords(result.getItems());
        exportResult.setFormat("JSON");
        exportResult.setStatus("COMPLETED");
        
        return exportResult;
    }
}
