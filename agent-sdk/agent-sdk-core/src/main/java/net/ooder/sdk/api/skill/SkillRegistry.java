package net.ooder.sdk.api.skill;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.ooder.sdk.api.scene.store.SkillRegistration;

public interface SkillRegistry {

    CompletableFuture<String> register(SkillDefinition definition, SkillService service);

    CompletableFuture<Void> unregister(String skillId);

    SkillService getService(String skillId);

    SkillDefinition getDefinition(String skillId);

    SkillRegistration getRegistration(String skillId);

    List<SkillService> getServices(String sceneId, String groupId);

    List<SkillService> getServicesByType(String sceneId, String skillType);

    SkillService getServiceByType(String sceneId, String skillType);

    List<SkillDefinition> listDefinitions(String sceneId);

    List<SkillRegistration> listRegistrations(String sceneId, String groupId);

    Map<String, Object> getEndpoints(String skillId);

    void sendHeartbeat(String skillId);

    boolean isAlive(String skillId);

    boolean isAlive(String skillId, long timeoutMs);

    void start();

    void stop();

    /**
     * 获取所有已安装的 Skills
     */
    List<InstalledSkill> getInstalledSkills();

    /**
     * 获取指定已安装的 Skill
     */
    InstalledSkill getInstalledSkill(String skillId);

    /**
     * 获取 Skill 的最新版本
     */
    String getLatestVersion(String skillId);
}
