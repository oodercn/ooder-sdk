package net.ooder.scene.core.activation.executor;

import net.ooder.scene.core.spi.org.DepartmentInfo;
import net.ooder.scene.core.spi.org.OrganizationService;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.StepResult;
import net.ooder.sdk.api.agent.EnhancedActivationStepExecutor.ValidationResult;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SelectPushTargetsExecutor 单元测试
 */
public class SelectPushTargetsExecutorTest {

    private SelectPushTargetsExecutor executor;
    private MockOrganizationService mockOrgService;

    @BeforeEach
    public void setUp() {
        executor = new SelectPushTargetsExecutor();
        mockOrgService = new MockOrganizationService();
    }

    @Test
    public void testGetStepType() {
        assertEquals("select-push-targets", executor.getStepType());
    }

    @Test
    public void testTargetTypeConstants() {
        assertEquals("USER", SelectPushTargetsExecutor.TARGET_TYPE_USER);
        assertEquals("DEPARTMENT", SelectPushTargetsExecutor.TARGET_TYPE_DEPARTMENT);
    }

    @Test
    public void testCanExecuteWithTargetType() {
        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("targetType", "USER");
        
        assertTrue(executor.canExecute(stepConfig));
    }

    @Test
    public void testCannotExecuteWithoutTargetType() {
        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("other", "value");
        
        assertFalse(executor.canExecute(stepConfig));
    }

    @Test
    public void testExecuteWithUserType() {
        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("targetType", "USER");
        stepConfig.put("targetIds", Arrays.asList("user1", "user2"));
        Map<String, Object> process = new HashMap<>();
        process.put("sceneGroupId", "scene-1");
        Map<String, Object> context = new HashMap<>();
        
        StepResult result = executor.execute(stepConfig, process, context);
        
        assertTrue(result.isSuccess());
        assertEquals("USER", result.getData().get("targetType"));
        assertNotNull(result.getData().get("targetIds"));
        assertNotNull(result.getData().get("selectedAt"));
    }

    @Test
    public void testExecuteWithDepartmentType() {
        mockOrgService.addDepartment("dept-1", Arrays.asList("user1", "user2", "user3"));
        mockOrgService.addDepartment("dept-2", Arrays.asList("user4", "user5"));
        executor.setOrganizationService(mockOrgService);

        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("targetType", "DEPARTMENT");
        stepConfig.put("targetIds", Arrays.asList("dept-1", "dept-2"));
        Map<String, Object> process = new HashMap<>();
        process.put("sceneGroupId", "scene-1");
        Map<String, Object> context = new HashMap<>();
        
        StepResult result = executor.execute(stepConfig, process, context);
        
        assertTrue(result.isSuccess());
        assertEquals("DEPARTMENT", result.getData().get("targetType"));
        assertNotNull(result.getData().get("allMemberIds"));
        assertEquals(5, result.getData().get("totalMemberCount"));
    }

    @Test
    public void testExecuteWithInvalidDepartment() {
        executor.setOrganizationService(mockOrgService);

        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("targetType", "DEPARTMENT");
        stepConfig.put("targetIds", Arrays.asList("invalid-dept"));
        Map<String, Object> process = new HashMap<>();
        process.put("sceneGroupId", "scene-1");
        Map<String, Object> context = new HashMap<>();
        
        StepResult result = executor.execute(stepConfig, process, context);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains(ExecutorErrorCodes.DEPARTMENT_NOT_FOUND));
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("invalidDepartments"));
    }

    @Test
    public void testExecuteWithEmptyTargets() {
        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("targetType", "USER");
        stepConfig.put("targetIds", Collections.emptyList());
        Map<String, Object> process = new HashMap<>();
        process.put("sceneGroupId", "scene-1");
        Map<String, Object> context = new HashMap<>();
        
        StepResult result = executor.execute(stepConfig, process, context);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains(ExecutorErrorCodes.TARGETS_REQUIRED));
    }

    @Test
    public void testExecuteWithoutOrganizationService() {
        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("targetType", "DEPARTMENT");
        stepConfig.put("targetIds", Arrays.asList("dept-1"));
        Map<String, Object> process = new HashMap<>();
        process.put("sceneGroupId", "scene-1");
        Map<String, Object> context = new HashMap<>();
        
        StepResult result = executor.execute(stepConfig, process, context);
        
        assertTrue(result.isSuccess());
        assertNull(result.getData().get("allMemberIds"));
    }

    @Test
    public void testValidateInputWithValidTargetType() {
        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("targetType", "USER");
        Map<String, Object> input = new HashMap<>();
        
        ValidationResult result = executor.validateInput(stepConfig, input);
        
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateInputWithInvalidTargetType() {
        Map<String, Object> stepConfig = new HashMap<>();
        stepConfig.put("targetType", "INVALID");
        Map<String, Object> input = new HashMap<>();
        
        ValidationResult result = executor.validateInput(stepConfig, input);
        
        assertFalse(result.isValid());
        assertFalse(result.getErrors().isEmpty());
    }

    @Test
    public void testValidateInputWithoutTargetType() {
        Map<String, Object> stepConfig = new HashMap<>();
        Map<String, Object> input = new HashMap<>();
        
        ValidationResult result = executor.validateInput(stepConfig, input);
        
        assertFalse(result.isValid());
        assertFalse(result.getErrors().isEmpty());
    }

    @Test
    public void testSetGetOrganizationService() {
        assertNull(executor.getOrganizationService());
        executor.setOrganizationService(mockOrgService);
        assertEquals(mockOrgService, executor.getOrganizationService());
    }

    private static class MockOrganizationService implements OrganizationService {
        private final Map<String, DepartmentInfo> departments = new HashMap<>();
        private final Map<String, List<String>> departmentMembers = new HashMap<>();

        void addDepartment(String deptId, List<String> members) {
            departments.put(deptId, new MockDepartmentInfo(deptId, members.size()));
            departmentMembers.put(deptId, new ArrayList<>(members));
        }

        @Override
        public DepartmentInfo getDepartment(String departmentId) {
            return departments.get(departmentId);
        }

        @Override
        public List<String> getDepartmentMembers(String departmentId) {
            return departmentMembers.getOrDefault(departmentId, Collections.emptyList());
        }

        @Override
        public List<String> getAllDepartmentMembers(String departmentId) {
            return getDepartmentMembers(departmentId);
        }

        @Override
        public net.ooder.scene.core.spi.org.OrganizationInfo getOrganization(String organizationId) {
            return null;
        }

        @Override
        public List<String> getOrganizationDepartments(String organizationId) {
            return new ArrayList<>(departments.keySet());
        }
    }

    private static class MockDepartmentInfo implements DepartmentInfo {
        private final String departmentId;
        private final int memberCount;

        MockDepartmentInfo(String departmentId, int memberCount) {
            this.departmentId = departmentId;
            this.memberCount = memberCount;
        }

        @Override
        public String getDepartmentId() { return departmentId; }

        @Override
        public String getName() { return "Department " + departmentId; }

        @Override
        public String getParentId() { return null; }

        @Override
        public String getOrganizationId() { return "org-1"; }

        @Override
        public int getMemberCount() { return memberCount; }

        @Override
        public Map<String, Object> getAttributes() { return Collections.emptyMap(); }
    }
}
