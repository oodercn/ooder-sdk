package net.ooder.scene.core.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.provider.model.share.ReceivedSkill;
import net.ooder.scene.provider.model.share.SharedSkill;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SkillShareProviderTest {

    private SkillShareProviderImpl skillShareProvider;

    @BeforeEach
    public void setup() {
        skillShareProvider = new SkillShareProviderImpl();
        skillShareProvider.initialize(null);
        skillShareProvider.start();
    }

    @AfterEach
    public void teardown() {
        skillShareProvider.stop();
    }

    @Test
    public void testProviderInitialization() {
        assertTrue(skillShareProvider.isInitialized());
        assertTrue(skillShareProvider.isRunning());
        assertEquals("skill-share-provider", skillShareProvider.getProviderName());
        assertEquals("1.0.0", skillShareProvider.getVersion());
        assertEquals(100, skillShareProvider.getPriority());
    }

    @Test
    public void testShareSkill() {
        Map<String, Object> skillData = new HashMap<>();
        skillData.put("skillId", "skill-001");
        skillData.put("name", "Test Skill");
        skillData.put("description", "A test skill");
        skillData.put("sharedWith", Arrays.asList("user1", "user2"));
        skillData.put("shareType", "user");
        
        Result<SharedSkill> result = skillShareProvider.shareSkill(skillData);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        SharedSkill skill = result.getData();
        assertNotNull(skill.getShareId());
        assertEquals("skill-001", skill.getSkillId());
        assertEquals("Test Skill", skill.getName());
        assertEquals("active", skill.getStatus());
    }

    @Test
    public void testShareSkillWithNullData() {
        Result<SharedSkill> result = skillShareProvider.shareSkill(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testShareSkillWithoutSkillId() {
        Map<String, Object> skillData = new HashMap<>();
        skillData.put("name", "Test Skill");
        
        Result<SharedSkill> result = skillShareProvider.shareSkill(skillData);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testShareSkillWithoutName() {
        Map<String, Object> skillData = new HashMap<>();
        skillData.put("skillId", "skill-001");
        
        Result<SharedSkill> result = skillShareProvider.shareSkill(skillData);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testGetSharedSkills() {
        Map<String, Object> skillData = new HashMap<>();
        skillData.put("skillId", "skill-001");
        skillData.put("name", "Test Skill");
        skillShareProvider.shareSkill(skillData);
        
        Result<List<SharedSkill>> result = skillShareProvider.getSharedSkills();
        
        assertTrue(result.isSuccess());
        assertFalse(result.getData().isEmpty());
    }

    @Test
    public void testGetSharedSkillsEmpty() {
        Result<List<SharedSkill>> result = skillShareProvider.getSharedSkills();
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testGetReceivedSkills() {
        Map<String, Object> skillData = new HashMap<>();
        skillData.put("skillId", "skill-001");
        skillData.put("name", "Test Skill");
        skillData.put("sharedWith", Arrays.asList("user1"));
        skillShareProvider.shareSkill(skillData);
        
        Result<List<ReceivedSkill>> result = skillShareProvider.getReceivedSkills();
        
        assertTrue(result.isSuccess());
        assertFalse(result.getData().isEmpty());
    }

    @Test
    public void testCancelShare() {
        Map<String, Object> skillData = new HashMap<>();
        skillData.put("skillId", "skill-001");
        skillData.put("name", "Test Skill");
        Result<SharedSkill> shareResult = skillShareProvider.shareSkill(skillData);
        String shareId = shareResult.getData().getShareId();
        
        Result<Boolean> result = skillShareProvider.cancelShare(shareId);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData());
        
        Result<List<SharedSkill>> sharedSkills = skillShareProvider.getSharedSkills();
        for (SharedSkill skill : sharedSkills.getData()) {
            assertNotEquals(shareId, skill.getShareId());
        }
    }

    @Test
    public void testCancelShareNotFound() {
        Result<Boolean> result = skillShareProvider.cancelShare("non-existent-id");
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testCancelShareWithNullId() {
        Result<Boolean> result = skillShareProvider.cancelShare(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testAcceptShare() {
        Map<String, Object> skillData = new HashMap<>();
        skillData.put("skillId", "skill-001");
        skillData.put("name", "Test Skill");
        skillData.put("sharedWith", Arrays.asList("user1"));
        Result<SharedSkill> shareResult = skillShareProvider.shareSkill(skillData);
        String shareId = shareResult.getData().getShareId();
        
        Result<Boolean> result = skillShareProvider.acceptShare(shareId);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData());
        
        Result<List<ReceivedSkill>> receivedSkills = skillShareProvider.getReceivedSkills();
        for (ReceivedSkill skill : receivedSkills.getData()) {
            if (shareId.equals(skill.getShareId())) {
                assertEquals("accepted", skill.getStatus());
            }
        }
    }

    @Test
    public void testAcceptShareNotFound() {
        Result<Boolean> result = skillShareProvider.acceptShare("non-existent-id");
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testRejectShare() {
        Map<String, Object> skillData = new HashMap<>();
        skillData.put("skillId", "skill-001");
        skillData.put("name", "Test Skill");
        skillData.put("sharedWith", Arrays.asList("user1"));
        Result<SharedSkill> shareResult = skillShareProvider.shareSkill(skillData);
        String shareId = shareResult.getData().getShareId();
        
        Result<Boolean> result = skillShareProvider.rejectShare(shareId);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData());
        
        Result<List<ReceivedSkill>> receivedSkills = skillShareProvider.getReceivedSkills();
        for (ReceivedSkill skill : receivedSkills.getData()) {
            if (shareId.equals(skill.getShareId())) {
                assertEquals("rejected", skill.getStatus());
            }
        }
    }

    @Test
    public void testRejectShareNotFound() {
        Result<Boolean> result = skillShareProvider.rejectShare("non-existent-id");
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testProviderLifecycle() {
        SkillShareProviderImpl provider = new SkillShareProviderImpl();
        
        assertFalse(provider.isInitialized());
        assertFalse(provider.isRunning());
        
        provider.initialize(null);
        assertTrue(provider.isInitialized());
        assertFalse(provider.isRunning());
        
        provider.start();
        assertTrue(provider.isRunning());
        
        provider.stop();
        assertFalse(provider.isRunning());
    }

    @Test
    public void testStartWithoutInitialize() {
        SkillShareProviderImpl provider = new SkillShareProviderImpl();
        
        assertThrows(IllegalStateException.class, () -> provider.start());
    }
}
