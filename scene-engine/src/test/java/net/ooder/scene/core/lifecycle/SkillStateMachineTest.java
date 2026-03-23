package net.ooder.scene.core.lifecycle;

import net.ooder.scene.core.lifecycle.SceneSkillLifecycle.SkillLifecycleState;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SkillStateMachine 单元测试
 */
public class SkillStateMachineTest {

    private SkillStateMachine stateMachine;

    @BeforeEach
    public void setUp() {
        stateMachine = new SkillStateMachine();
    }

    @Test
    public void testInitializeState() {
        SkillStateInfo stateInfo = stateMachine.initializeState("scene-1", "skill-1", "Test Skill");
        
        assertNotNull(stateInfo);
        assertEquals("scene-1", stateInfo.getSceneId());
        assertEquals("skill-1", stateInfo.getSkillId());
        assertEquals("Test Skill", stateInfo.getSkillName());
        assertEquals(SkillLifecycleState.DISCOVERED, stateInfo.getState());
    }

    @Test
    public void testValidStateTransition() {
        stateMachine.initializeState("scene-1", "skill-1", "Test Skill");
        
        SkillStateInfo stateInfo = stateMachine.transition(
            "scene-1", "skill-1", SkillLifecycleState.PREVIEWING);
        
        assertEquals(SkillLifecycleState.PREVIEWING, stateInfo.getState());
        assertEquals(SkillLifecycleState.DISCOVERED.name(), stateInfo.getPreviousState());
    }

    @Test
    public void testInvalidStateTransition() {
        stateMachine.initializeState("scene-1", "skill-1", "Test Skill");
        
        assertThrows(IllegalStateException.class, () -> {
            stateMachine.transition("scene-1", "skill-1", SkillLifecycleState.ACTIVATED);
        });
    }

    @Test
    public void testStateTransitionSequence() {
        stateMachine.initializeState("scene-1", "skill-1", "Test Skill");
        
        stateMachine.transition("scene-1", "skill-1", SkillLifecycleState.PREVIEWING);
        stateMachine.transition("scene-1", "skill-1", SkillLifecycleState.CONFIGURING);
        stateMachine.transition("scene-1", "skill-1", SkillLifecycleState.DEP_CHECKING);
        stateMachine.transition("scene-1", "skill-1", SkillLifecycleState.DEP_CONFIRMING);
        stateMachine.transition("scene-1", "skill-1", SkillLifecycleState.INSTALLING);
        stateMachine.transition("scene-1", "skill-1", SkillLifecycleState.INSTALLED);
        stateMachine.transition("scene-1", "skill-1", SkillLifecycleState.ACTIVATING);
        SkillStateInfo stateInfo = stateMachine.transition(
            "scene-1", "skill-1", SkillLifecycleState.ACTIVATED);
        
        assertEquals(SkillLifecycleState.ACTIVATED, stateInfo.getState());
        assertTrue(stateInfo.isActivated());
    }

    @Test
    public void testCanTransition() {
        stateMachine.initializeState("scene-1", "skill-1", "Test Skill");
        
        assertTrue(stateMachine.canTransition("scene-1", "skill-1", SkillLifecycleState.PREVIEWING));
        assertTrue(stateMachine.canTransition("scene-1", "skill-1", SkillLifecycleState.INSTALLING));
        assertFalse(stateMachine.canTransition("scene-1", "skill-1", SkillLifecycleState.ACTIVATED));
    }

    @Test
    public void testTryTransition() {
        stateMachine.initializeState("scene-1", "skill-1", "Test Skill");
        
        assertTrue(stateMachine.tryTransition("scene-1", "skill-1", SkillLifecycleState.PREVIEWING));
        assertFalse(stateMachine.tryTransition("scene-1", "skill-1", SkillLifecycleState.ACTIVATED));
    }

    @Test
    public void testTransitionToError() {
        stateMachine.initializeState("scene-1", "skill-1", "Test Skill");
        
        SkillStateInfo stateInfo = stateMachine.transition(
            "scene-1", "skill-1", SkillLifecycleState.ERROR, "Test error");
        
        assertEquals(SkillLifecycleState.ERROR, stateInfo.getState());
        assertEquals("Test error", stateInfo.getErrorMessage());
    }

    @Test
    public void testTransitionHistory() {
        stateMachine.initializeState("scene-1", "skill-1", "Test Skill");
        stateMachine.transition("scene-1", "skill-1", SkillLifecycleState.PREVIEWING);
        stateMachine.transition("scene-1", "skill-1", SkillLifecycleState.CONFIGURING);
        
        SkillStateMachine.StateTransitionHistory history = 
            stateMachine.getTransitionHistory("scene-1", "skill-1");
        
        assertNotNull(history);
        assertEquals(3, history.getRecords().size());
    }

    @Test
    public void testGetSkillsByState() {
        stateMachine.initializeState("scene-1", "skill-1", "Test Skill");
        stateMachine.initializeState("scene-2", "skill-2", "Test Skill 2");
        stateMachine.transition("scene-1", "skill-1", SkillLifecycleState.PREVIEWING);
        
        List<SkillStateInfo> discoveredSkills = 
            stateMachine.getSkillsByState(SkillLifecycleState.DISCOVERED);
        List<SkillStateInfo> previewingSkills = 
            stateMachine.getSkillsByState(SkillLifecycleState.PREVIEWING);
        
        assertEquals(1, discoveredSkills.size());
        assertEquals(1, previewingSkills.size());
    }

    @Test
    public void testRemoveState() {
        stateMachine.initializeState("scene-1", "skill-1", "Test Skill");
        
        assertNotNull(stateMachine.getState("scene-1", "skill-1"));
        
        stateMachine.removeState("scene-1", "skill-1");
        
        assertNull(stateMachine.getState("scene-1", "skill-1"));
    }

    @Test
    public void testStateInfoHelperMethods() {
        SkillStateInfo stateInfo = new SkillStateInfo();
        stateInfo.setState(SkillLifecycleState.ACTIVATED);
        
        assertTrue(stateInfo.isActivated());
        assertTrue(stateInfo.isActive());
        assertFalse(stateInfo.isInInstallFlow());
        assertFalse(stateInfo.isInActivationFlow());
        
        stateInfo.setState(SkillLifecycleState.PREVIEWING);
        assertFalse(stateInfo.isActivated());
        assertTrue(stateInfo.isInInstallFlow());
        
        stateInfo.setState(SkillLifecycleState.INSTALLED);
        assertTrue(stateInfo.isInstalled());
        assertTrue(stateInfo.isInActivationFlow());
    }

    @Test
    public void testStateTransitionListener() {
        final boolean[] listenerCalled = {false};
        
        stateMachine.subscribe("scene-1", event -> {
            listenerCalled[0] = true;
            assertEquals("scene-1", event.getSceneId());
            assertEquals("skill-1", event.getSkillId());
        });
        
        stateMachine.initializeState("scene-1", "skill-1", "Test Skill");
        stateMachine.transition("scene-1", "skill-1", SkillLifecycleState.PREVIEWING);
        
        assertTrue(listenerCalled[0]);
    }
}
