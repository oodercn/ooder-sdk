package net.ooder.scene.security.storage;

import net.ooder.sdk.api.security.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonKeyStorageService 单元测试
 *
 * @author ooder
 * @since 2.3.1
 */
class JsonKeyStorageServiceTest {

    @TempDir
    Path tempDir;

    private JsonKeyStorageService storageService;

    @BeforeEach
    void setUp() {
        storageService = new JsonKeyStorageService();
        storageService.storageRoot = tempDir.toString();
        storageService.init();
    }

    @Test
    void testSaveAndLoadKey() {
        KeyEntity key = new KeyEntity();
        key.setKeyId("test-key-001");
        key.setKeyValue("test-value-001");
        key.setKeyName("测试密钥");
        key.setKeyType(KeyType.SESSION_TOKEN);
        key.setStatus(KeyStatus.ACTIVE);
        key.setOwnerId("user-001");
        key.setOwnerType(OwnerType.USER);

        storageService.saveKey(key);

        KeyEntity loadedKey = storageService.loadKey("test-key-001");
        
        assertNotNull(loadedKey);
        assertEquals("test-key-001", loadedKey.getKeyId());
        assertEquals("test-value-001", loadedKey.getKeyValue());
        assertEquals("测试密钥", loadedKey.getKeyName());
        assertEquals(KeyType.SESSION_TOKEN, loadedKey.getKeyType());
        assertEquals(KeyStatus.ACTIVE, loadedKey.getStatus());
        assertEquals("user-001", loadedKey.getOwnerId());
        assertEquals(OwnerType.USER, loadedKey.getOwnerType());
    }

    @Test
    void testLoadKeyByValue() {
        KeyEntity key = new KeyEntity();
        key.setKeyId("test-key-002");
        key.setKeyValue("unique-value-002");
        key.setKeyName("测试密钥2");
        key.setKeyType(KeyType.API_KEY);
        key.setStatus(KeyStatus.ACTIVE);
        key.setOwnerId("user-002");
        key.setOwnerType(OwnerType.USER);

        storageService.saveKey(key);

        KeyEntity loadedKey = storageService.loadKeyByValue("unique-value-002");
        
        assertNotNull(loadedKey);
        assertEquals("test-key-002", loadedKey.getKeyId());
        assertEquals("unique-value-002", loadedKey.getKeyValue());
    }

    @Test
    void testDeleteKey() {
        KeyEntity key = new KeyEntity();
        key.setKeyId("test-key-003");
        key.setKeyValue("test-value-003");
        key.setKeyType(KeyType.SESSION_TOKEN);
        key.setOwnerId("user-003");
        key.setOwnerType(OwnerType.USER);

        storageService.saveKey(key);
        assertNotNull(storageService.loadKey("test-key-003"));

        storageService.deleteKey("test-key-003");
        assertNull(storageService.loadKey("test-key-003"));
    }

    @Test
    void testLoadKeysByOwner() {
        KeyEntity key1 = new KeyEntity();
        key1.setKeyId("key-001");
        key1.setKeyValue("value-001");
        key1.setKeyType(KeyType.SESSION_TOKEN);
        key1.setOwnerId("user-001");
        key1.setOwnerType(OwnerType.USER);

        KeyEntity key2 = new KeyEntity();
        key2.setKeyId("key-002");
        key2.setKeyValue("value-002");
        key2.setKeyType(KeyType.API_KEY);
        key2.setOwnerId("user-001");
        key2.setOwnerType(OwnerType.USER);

        KeyEntity key3 = new KeyEntity();
        key3.setKeyId("key-003");
        key3.setKeyValue("value-003");
        key3.setKeyType(KeyType.SESSION_TOKEN);
        key3.setOwnerId("user-002");
        key3.setOwnerType(OwnerType.USER);

        storageService.saveKey(key1);
        storageService.saveKey(key2);
        storageService.saveKey(key3);

        List<KeyEntity> keys = storageService.loadKeysByOwner("user-001", OwnerType.USER);
        
        assertEquals(2, keys.size());
        assertTrue(keys.stream().anyMatch(k -> "key-001".equals(k.getKeyId())));
        assertTrue(keys.stream().anyMatch(k -> "key-002".equals(k.getKeyId())));
    }

    @Test
    void testSaveAndLoadRequest() {
        NetworkJoinRequest request = new NetworkJoinRequest();
        request.setRequestId("req-001");
        request.setRequestType(RequestType.USER_JOIN);
        request.setStatus(RequestStatus.PENDING);
        request.setApplicantId("user-001");
        request.setSceneGroupId("scene-001");

        storageService.saveRequest(request);

        NetworkJoinRequest loadedRequest = storageService.loadRequest("req-001");
        
        assertNotNull(loadedRequest);
        assertEquals("req-001", loadedRequest.getRequestId());
        assertEquals(RequestType.USER_JOIN, loadedRequest.getRequestType());
        assertEquals(RequestStatus.PENDING, loadedRequest.getStatus());
        assertEquals("user-001", loadedRequest.getApplicantId());
        assertEquals("scene-001", loadedRequest.getSceneGroupId());
    }

    @Test
    void testLoadPendingRequests() {
        NetworkJoinRequest request1 = new NetworkJoinRequest();
        request1.setRequestId("req-001");
        request1.setRequestType(RequestType.USER_JOIN);
        request1.setStatus(RequestStatus.PENDING);
        request1.setApplicantId("user-001");

        NetworkJoinRequest request2 = new NetworkJoinRequest();
        request2.setRequestId("req-002");
        request2.setRequestType(RequestType.AGENT_JOIN);
        request2.setStatus(RequestStatus.APPROVED);
        request2.setApplicantId("agent-001");

        storageService.saveRequest(request1);
        storageService.saveRequest(request2);

        List<NetworkJoinRequest> pendingRequests = storageService.loadPendingRequests();
        
        assertEquals(1, pendingRequests.size());
        assertEquals("req-001", pendingRequests.get(0).getRequestId());
    }

    @Test
    void testSaveAndLoadRule() {
        KeyRule rule = new KeyRule();
        rule.setRuleId("rule-001");
        rule.setRuleName("默认规则");
        rule.setDefaultExpiresInSeconds(86400);
        rule.setDefaultMaxUseCount(1000);

        storageService.saveRule(rule);

        KeyRule loadedRule = storageService.loadRule("rule-001");
        
        assertNotNull(loadedRule);
        assertEquals("rule-001", loadedRule.getRuleId());
        assertEquals("默认规则", loadedRule.getRuleName());
        assertEquals(86400, loadedRule.getDefaultExpiresInSeconds());
        assertEquals(1000, loadedRule.getDefaultMaxUseCount());
    }

    @Test
    void testLoadAllRules() {
        KeyRule rule1 = new KeyRule();
        rule1.setRuleId("rule-001");
        rule1.setRuleName("规则1");

        KeyRule rule2 = new KeyRule();
        rule2.setRuleId("rule-002");
        rule2.setRuleName("规则2");

        storageService.saveRule(rule1);
        storageService.saveRule(rule2);

        List<KeyRule> rules = storageService.loadAllRules();
        
        assertEquals(2, rules.size());
        assertTrue(rules.stream().anyMatch(r -> "rule-001".equals(r.getRuleId())));
        assertTrue(rules.stream().anyMatch(r -> "rule-002".equals(r.getRuleId())));
    }
}
