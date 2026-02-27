package net.ooder.scene.core.provider;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.Result;
import net.ooder.scene.provider.model.user.Permission;
import net.ooder.scene.provider.model.user.SecurityLog;
import net.ooder.scene.provider.model.user.UserInfo;
import net.ooder.scene.provider.model.user.UserStatus;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserProviderTest {

    private UserProviderImpl userProvider;

    @BeforeEach
    public void setup() {
        userProvider = new UserProviderImpl();
        userProvider.initialize(null);
        userProvider.start();
    }

    @AfterEach
    public void teardown() {
        userProvider.stop();
    }

    @Test
    public void testProviderInitialization() {
        assertTrue(userProvider.isInitialized());
        assertTrue(userProvider.isRunning());
        assertEquals("user-provider", userProvider.getProviderName());
        assertEquals("1.0.0", userProvider.getVersion());
        assertEquals(100, userProvider.getPriority());
    }

    @Test
    public void testGetStatus() {
        Result<UserStatus> result = userProvider.getStatus();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        UserStatus status = result.getData();
        assertTrue(status.getTotalUsers() >= 1);
        assertTrue(status.getActiveUsers() >= 1);
        assertTrue(status.getLastUpdated() > 0);
    }

    @Test
    public void testListUsers() {
        Result<PageResult<UserInfo>> result = userProvider.listUsers(1, 10);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        PageResult<UserInfo> pageResult = result.getData();
        assertTrue(pageResult.getTotal() >= 1);
        assertFalse(pageResult.getItems().isEmpty());
    }

    @Test
    public void testCreateUser() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "testuser");
        userData.put("email", "test@example.com");
        userData.put("displayName", "Test User");
        userData.put("department", "Engineering");
        userData.put("roles", Arrays.asList("user", "developer"));
        
        Result<UserInfo> result = userProvider.createUser(userData);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        UserInfo user = result.getData();
        assertNotNull(user.getUserId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getDisplayName());
        assertEquals("Engineering", user.getDepartment());
        assertEquals("active", user.getStatus());
        assertTrue(user.getCreatedAt() > 0);
    }

    @Test
    public void testCreateUserWithNullData() {
        Result<UserInfo> result = userProvider.createUser(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testCreateUserWithoutUsername() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", "test@example.com");
        
        Result<UserInfo> result = userProvider.createUser(userData);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testCreateDuplicateUser() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "duplicateuser");
        
        userProvider.createUser(userData);
        
        Result<UserInfo> result = userProvider.createUser(userData);
        
        assertFalse(result.isSuccess());
    }

    @Test
    public void testGetUser() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "gettestuser");
        
        Result<UserInfo> createResult = userProvider.createUser(userData);
        String userId = createResult.getData().getUserId();
        
        Result<UserInfo> result = userProvider.getUser(userId);
        
        assertTrue(result.isSuccess());
        assertEquals("gettestuser", result.getData().getUsername());
    }

    @Test
    public void testGetUserNotFound() {
        Result<UserInfo> result = userProvider.getUser("non-existent-id");
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testGetUserWithNullId() {
        Result<UserInfo> result = userProvider.getUser(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testUpdateUser() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "updatetestuser");
        userData.put("email", "old@example.com");
        
        Result<UserInfo> createResult = userProvider.createUser(userData);
        String userId = createResult.getData().getUserId();
        
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("email", "new@example.com");
        updateData.put("displayName", "Updated Name");
        
        Result<UserInfo> result = userProvider.updateUser(userId, updateData);
        
        assertTrue(result.isSuccess());
        assertEquals("new@example.com", result.getData().getEmail());
        assertEquals("Updated Name", result.getData().getDisplayName());
        assertEquals("updatetestuser", result.getData().getUsername());
    }

    @Test
    public void testUpdateUserNotFound() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("email", "new@example.com");
        
        Result<UserInfo> result = userProvider.updateUser("non-existent-id", updateData);
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testUpdateUserWithNullData() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "updatetestuser2");
        
        Result<UserInfo> createResult = userProvider.createUser(userData);
        String userId = createResult.getData().getUserId();
        
        Result<UserInfo> result = userProvider.updateUser(userId, null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testDeleteUser() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "deletetestuser");
        
        Result<UserInfo> createResult = userProvider.createUser(userData);
        String userId = createResult.getData().getUserId();
        
        Result<Boolean> deleteResult = userProvider.deleteUser(userId);
        
        assertTrue(deleteResult.isSuccess());
        assertTrue(deleteResult.getData());
        
        Result<UserInfo> getResult = userProvider.getUser(userId);
        assertFalse(getResult.isSuccess());
        assertEquals(404, getResult.getCode());
    }

    @Test
    public void testDeleteUserNotFound() {
        Result<Boolean> result = userProvider.deleteUser("non-existent-id");
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testEnableUser() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "enabletestuser");
        
        Result<UserInfo> createResult = userProvider.createUser(userData);
        String userId = createResult.getData().getUserId();
        
        userProvider.disableUser(userId);
        
        Result<UserInfo> result = userProvider.enableUser(userId);
        
        assertTrue(result.isSuccess());
        assertEquals("active", result.getData().getStatus());
    }

    @Test
    public void testDisableUser() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "disabletestuser");
        
        Result<UserInfo> createResult = userProvider.createUser(userData);
        String userId = createResult.getData().getUserId();
        
        Result<UserInfo> result = userProvider.disableUser(userId);
        
        assertTrue(result.isSuccess());
        assertEquals("disabled", result.getData().getStatus());
    }

    @Test
    public void testEnableUserNotFound() {
        Result<UserInfo> result = userProvider.enableUser("non-existent-id");
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testDisableUserNotFound() {
        Result<UserInfo> result = userProvider.disableUser("non-existent-id");
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testListPermissions() {
        Result<PageResult<Permission>> result = userProvider.listPermissions(1, 10);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        PageResult<Permission> pageResult = result.getData();
        assertTrue(pageResult.getTotal() >= 6);
        assertFalse(pageResult.getItems().isEmpty());
    }

    @Test
    public void testSavePermissions() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "permtestuser");
        
        Result<UserInfo> createResult = userProvider.createUser(userData);
        String userId = createResult.getData().getUserId();
        
        List<String> permissions = Arrays.asList("user.read", "config.read");
        
        Result<Boolean> result = userProvider.savePermissions(userId, permissions);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    public void testSavePermissionsUserNotFound() {
        List<String> permissions = Arrays.asList("user.read");
        
        Result<Boolean> result = userProvider.savePermissions("non-existent-id", permissions);
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testSavePermissionsWithNullUserId() {
        List<String> permissions = Arrays.asList("user.read");
        
        Result<Boolean> result = userProvider.savePermissions(null, permissions);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testListSecurityLogs() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "logtestuser");
        userProvider.createUser(userData);
        
        Result<PageResult<SecurityLog>> result = userProvider.listSecurityLogs(1, 10);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().getTotal() > 0);
    }

    @Test
    public void testProviderLifecycle() {
        UserProviderImpl provider = new UserProviderImpl();
        
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
        UserProviderImpl provider = new UserProviderImpl();
        
        assertThrows(IllegalStateException.class, () -> provider.start());
    }

    @Test
    public void testDefaultAdminUser() {
        Result<PageResult<UserInfo>> result = userProvider.listUsers(1, 10);
        
        assertTrue(result.isSuccess());
        
        boolean hasAdmin = false;
        for (UserInfo user : result.getData().getItems()) {
            if ("admin".equals(user.getUsername())) {
                hasAdmin = true;
                assertTrue(user.getRoles().contains("admin"));
                break;
            }
        }
        
        assertTrue(hasAdmin, "Default admin user should exist");
    }
}
