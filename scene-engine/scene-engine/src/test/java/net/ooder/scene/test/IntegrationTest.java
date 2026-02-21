package net.ooder.scene.test;

import net.ooder.scene.core.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class IntegrationTest {

    @Test
    public void testHeartbeatResultModel() {
        HeartbeatResult result = new HeartbeatResult();
        result.setGroupId("group-123");
        result.setSuccess(true);
        result.setActiveMembers(new ArrayList<>());
        result.setInactiveMembers(new ArrayList<>());
        result.setPrimaryId("member-1");

        assertEquals("group-123", result.getGroupId());
        assertTrue(result.isSuccess());
        assertNotNull(result.getActiveMembers());
        assertNotNull(result.getInactiveMembers());
        assertEquals("member-1", result.getPrimaryId());
    }

    @Test
    public void testHeartbeatStatusModel() {
        HeartbeatStatus status = new HeartbeatStatus();
        status.setGroupId("group-123");
        status.setRunning(true);
        status.setLastHeartbeatTime(System.currentTimeMillis());
        status.setMissedCount(0);

        assertEquals("group-123", status.getGroupId());
        assertTrue(status.isRunning());
        assertNotNull(status.getLastHeartbeatTime());
        assertEquals(0, status.getMissedCount());
    }

    @Test
    public void testMemberRoleEnum() {
        assertEquals(4, MemberRole.values().length);
        assertEquals("PRIMARY", MemberRole.PRIMARY.name());
        assertEquals("BACKUP", MemberRole.BACKUP.name());
        assertEquals("OBSERVER", MemberRole.OBSERVER.name());
        assertEquals("MEMBER", MemberRole.MEMBER.name());
    }

    @Test
    public void testSceneMemberInfoWithRole() {
        SceneMemberInfo member = new SceneMemberInfo();
        member.setMemberId("member-123");
        member.setGroupId("group-456");
        member.setUserId("user-789");
        member.setMemberName("Test Member");
        member.setRole(MemberRole.PRIMARY);
        member.setStatus("ACTIVE");
        member.setJoinedAt(System.currentTimeMillis());
        member.setLastActiveAt(System.currentTimeMillis());
        member.setLastHeartbeat(System.currentTimeMillis());

        assertEquals("member-123", member.getMemberId());
        assertEquals("group-456", member.getGroupId());
        assertEquals("user-789", member.getUserId());
        assertEquals("Test Member", member.getMemberName());
        assertEquals(MemberRole.PRIMARY, member.getRole());
        assertEquals("ACTIVE", member.getStatus());
        assertTrue(member.isPrimary());
    }

    @Test
    public void testSceneMemberInfoWithNonPrimaryRole() {
        SceneMemberInfo member = new SceneMemberInfo();
        member.setRole(MemberRole.MEMBER);

        assertFalse(member.isPrimary());

        member.setRole(MemberRole.BACKUP);
        assertFalse(member.isPrimary());

        member.setRole(MemberRole.OBSERVER);
        assertFalse(member.isPrimary());
    }

    @Test
    public void testLastHeartbeatField() {
        SceneMemberInfo member = new SceneMemberInfo();
        long timestamp = System.currentTimeMillis();
        member.setLastHeartbeat(timestamp);
        assertEquals(timestamp, member.getLastHeartbeat());
    }
}
