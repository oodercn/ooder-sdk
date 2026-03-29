package net.ooder.scene.core.activation.executor;

import net.ooder.scene.core.spi.user.UserInfo;
import net.ooder.scene.core.spi.user.UserService;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.StepResult;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.ValidationResult;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConfirmParticipantsExecutor 单元测试
 */
public class ConfirmParticipantsExecutorTest {

    private ConfirmParticipantsExecutor executor;
    private MockUserService mockUserService;

    @BeforeEach
    public void setUp() {
        executor = new ConfirmParticipantsExecutor();
        mockUserService = new MockUserService();
    }

    @Test
    public void testGetStepType() {
        assertEquals("confirm-participants", executor.getStepType());
    }

    @Test
    public void testCanExecuteWithRoles() {
        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("roles", Arrays.asList("admin", "user"));
        
        assertTrue(executor.canExecute(stepConfig));
    }

    @Test
    public void testCanExecuteWithParticipantIds() {
        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("participantIds", Arrays.asList("user1", "user2"));
        
        assertTrue(executor.canExecute(stepConfig));
    }

    @Test
    public void testCannotExecuteWithoutParticipants() {
        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("other", "value");
        
        assertFalse(executor.canExecute(stepConfig));
    }

    @Test
    public void testExecuteWithoutUserService() {
        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("participantIds", Arrays.asList("user1", "user2"));
        Map<String, Object> process = new HashMap<>();
        process.put("sceneGroupId", "scene-1");
        Map<String, Object> context = new HashMap<>();
        context.put("userId", "admin-1");
        
        StepResult result = executor.execute(stepConfig, process, context);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("scene-1", result.getData().get("sceneGroupId"));
        assertEquals("admin-1", result.getData().get("confirmedBy"));
        assertNotNull(result.getData().get("confirmedAt"));
    }

    @Test
    public void testExecuteWithValidUsers() {
        mockUserService.addUser("user1", "User One");
        mockUserService.addUser("user2", "User Two");
        executor.setUserService(mockUserService);

        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("participantIds", Arrays.asList("user1", "user2"));
        Map<String, Object> process = new HashMap<>();
        process.put("sceneGroupId", "scene-1");
        Map<String, Object> context = new HashMap<>();
        context.put("userId", "admin-1");
        
        StepResult result = executor.execute(stepConfig, process, context);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData().get("participantIds"));
    }

    @Test
    public void testExecuteWithInvalidUsers() {
        mockUserService.addUser("user1", "User One");
        executor.setUserService(mockUserService);

        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("participantIds", Arrays.asList("user1", "invalidUser"));
        Map<String, Object> process = new HashMap<>();
        process.put("sceneGroupId", "scene-1");
        Map<String, Object> context = new HashMap<>();
        context.put("userId", "admin-1");
        
        StepResult result = executor.execute(stepConfig, process, context);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains(ExecutorErrorCodes.PARTICIPANTS_NOT_FOUND));
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("invalidUsers"));
    }

    @Test
    public void testExecuteWithEmptyParticipants() {
        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("participantIds", Collections.emptyList());
        Map<String, Object> process = new HashMap<>();
        process.put("sceneGroupId", "scene-1");
        Map<String, Object> context = new HashMap<>();
        context.put("userId", "admin-1");
        
        StepResult result = executor.execute(stepConfig, process, context);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains(ExecutorErrorCodes.PARTICIPANTS_REQUIRED));
    }

    @Test
    public void testValidateInputWithParticipantIds() {
        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("participantIds", Arrays.asList("user1"));
        Map<String, Object> input = new HashMap<>();
        
        ValidationResult result = executor.validateInput(stepConfig, input);
        
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateInputWithRoles() {
        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("roles", Arrays.asList("admin"));
        Map<String, Object> input = new HashMap<>();
        
        ValidationResult result = executor.validateInput(stepConfig, input);
        
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateInputWithoutRequiredField() {
        Map<String, Object> stepConfig = new HashMap<>();
        Map<String, Object> input = new HashMap<>();
        
        ValidationResult result = executor.validateInput(stepConfig, input);
        
        assertFalse(result.isValid());
        assertFalse(result.getErrors().isEmpty());
    }

    @Test
    public void testSupportsRollback() {
        assertTrue(executor.supportsRollback());
    }

    @Test
    public void testRollback() {
        Map<String, Object> stepConfig = new HashMap<>();
        Map<String, Object> process = new HashMap<>();
        process.put("sceneGroupId", "scene-1");
        Map<String, Object> context = new HashMap<>();
        
        assertDoesNotThrow(() -> executor.rollback(stepConfig, process, context));
    }

    @Test
    public void testSetGetUserService() {
        assertNull(executor.getUserService());
        executor.setUserService(mockUserService);
        assertEquals(mockUserService, executor.getUserService());
    }

    private static class MockUserService implements UserService {
        private final Map<String, UserInfo> users = new HashMap<>();

        void addUser(String userId, String displayName) {
            users.put(userId, new MockUserInfo(userId, displayName));
        }

        @Override
        public Map<String, UserInfo> getUsers(List<String> userIds) {
            Map<String, UserInfo> result = new HashMap<>();
            for (String userId : userIds) {
                if (users.containsKey(userId)) {
                    result.put(userId, users.get(userId));
                }
            }
            return result;
        }

        @Override
        public UserInfo getUser(String userId) {
            return users.get(userId);
        }

        @Override
        public boolean userExists(String userId) {
            return users.containsKey(userId);
        }

        @Override
        public List<String> validateUsers(List<String> userIds) {
            List<String> invalid = new ArrayList<>();
            for (String userId : userIds) {
                if (!users.containsKey(userId)) {
                    invalid.add(userId);
                }
            }
            return invalid;
        }
    }

    private static class MockUserInfo implements UserInfo {
        private final String userId;
        private final String displayName;

        MockUserInfo(String userId, String displayName) {
            this.userId = userId;
            this.displayName = displayName;
        }

        @Override
        public String getUserId() { return userId; }

        @Override
        public String getUsername() { return userId; }

        @Override
        public String getDisplayName() { return displayName; }

        @Override
        public String getEmail() { return userId + "@test.com"; }

        @Override
        public String getPhone() { return "1234567890"; }

        @Override
        public String getDepartmentId() { return "dept-1"; }

        @Override
        public String getOrganizationId() { return "org-1"; }

        @Override
        public Map<String, Object> getAttributes() { return Collections.emptyMap(); }
    }
}
