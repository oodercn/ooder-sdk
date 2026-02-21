package net.ooder.scene.skills.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BusinessSkillImpl 业务技能实现
 * 
 * <p>默认实现，使用内存存储。</p>
 * 
 * @author Ooder Team
 * @version 0.7.3
 */
public class BusinessSkillImpl implements BusinessSkill {

    private static final String SKILL_ID = "skill-business-001";
    private static final String SKILL_NAME = "业务技能";
    private static final String SKILL_VERSION = "0.7.3";

    private Map<String, Map<String, Object>> categories = new ConcurrentHashMap<String, Map<String, Object>>();
    private Map<String, Map<String, Object>> scenes = new ConcurrentHashMap<String, Map<String, Object>>();
    private Map<String, List<Map<String, Object>>> sceneCapabilities = new ConcurrentHashMap<String, List<Map<String, Object>>>();

    public BusinessSkillImpl() {
        initDefaultData();
    }

    private void initDefaultData() {
        Map<String, Object> hrCategory = new HashMap<String, Object>();
        hrCategory.put("categoryId", "cat-hr");
        hrCategory.put("name", "人力资源");
        hrCategory.put("description", "人力资源管理相关场景");
        hrCategory.put("icon", "ri-team-line");
        hrCategory.put("sort", 1);
        categories.put("cat-hr", hrCategory);

        Map<String, Object> crmCategory = new HashMap<String, Object>();
        crmCategory.put("categoryId", "cat-crm");
        crmCategory.put("name", "客户管理");
        crmCategory.put("description", "客户关系管理相关场景");
        crmCategory.put("icon", "ri-user-star-line");
        crmCategory.put("sort", 2);
        categories.put("cat-crm", crmCategory);

        Map<String, Object> oaCategory = new HashMap<String, Object>();
        oaCategory.put("categoryId", "cat-oa");
        oaCategory.put("name", "办公协同");
        oaCategory.put("description", "办公协同相关场景");
        oaCategory.put("icon", "ri-file-list-3-line");
        oaCategory.put("sort", 3);
        categories.put("cat-oa", oaCategory);
    }

    @Override
    public String getSkillId() {
        return SKILL_ID;
    }

    @Override
    public String getSkillName() {
        return SKILL_NAME;
    }

    @Override
    public String getSkillVersion() {
        return SKILL_VERSION;
    }

    @Override
    public List<String> getCapabilities() {
        List<String> caps = new ArrayList<String>();
        caps.add("business.category.list");
        caps.add("business.category.tree");
        caps.add("business.category.create");
        caps.add("business.category.update");
        caps.add("business.category.delete");
        caps.add("business.scene.list");
        caps.add("business.scene.get");
        caps.add("business.scene.create");
        caps.add("business.scene.update");
        caps.add("business.scene.delete");
        caps.add("business.capability.list");
        caps.add("business.capability.add");
        caps.add("business.capability.remove");
        caps.add("business.capability.invoke");
        caps.add("business.capability.batchInvoke");
        return caps;
    }

    // ==================== 业务分类管理 ====================

    @Override
    public List<Map<String, Object>> getCategoryList() {
        return new ArrayList<Map<String, Object>>(categories.values());
    }

    @Override
    public List<Map<String, Object>> getCategoryTree() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> category : categories.values()) {
            Map<String, Object> node = new HashMap<String, Object>(category);
            node.put("children", new ArrayList<Map<String, Object>>());
            result.add(node);
        }
        return result;
    }

    @Override
    public Map<String, Object> getCategory(String categoryId) {
        return categories.get(categoryId);
    }

    @Override
    public Map<String, Object> createCategory(Map<String, Object> params) {
        String categoryId = "cat-" + UUID.randomUUID().toString().substring(0, 8);
        
        Map<String, Object> category = new HashMap<String, Object>();
        category.put("categoryId", categoryId);
        category.put("name", params.get("name"));
        category.put("description", params.get("description"));
        category.put("icon", params.get("icon"));
        category.put("parentId", params.get("parentId"));
        category.put("sort", params.getOrDefault("sort", 0));
        category.put("createTime", System.currentTimeMillis());
        
        categories.put(categoryId, category);
        return category;
    }

    @Override
    public Map<String, Object> updateCategory(String categoryId, Map<String, Object> params) {
        Map<String, Object> category = categories.get(categoryId);
        if (category == null) {
            return null;
        }
        
        if (params.containsKey("name")) {
            category.put("name", params.get("name"));
        }
        if (params.containsKey("description")) {
            category.put("description", params.get("description"));
        }
        if (params.containsKey("icon")) {
            category.put("icon", params.get("icon"));
        }
        if (params.containsKey("sort")) {
            category.put("sort", params.get("sort"));
        }
        category.put("updateTime", System.currentTimeMillis());
        
        return category;
    }

    @Override
    public boolean deleteCategory(String categoryId) {
        for (Map<String, Object> scene : scenes.values()) {
            if (categoryId.equals(scene.get("categoryId"))) {
                return false;
            }
        }
        return categories.remove(categoryId) != null;
    }

    // ==================== 业务场景管理 ====================

    @Override
    public List<Map<String, Object>> getSceneList(String category) {
        if (category == null || category.isEmpty()) {
            return new ArrayList<Map<String, Object>>(scenes.values());
        }
        
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> scene : scenes.values()) {
            if (category.equals(scene.get("categoryId"))) {
                result.add(scene);
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> getScene(String sceneId) {
        return scenes.get(sceneId);
    }

    @Override
    public Map<String, Object> createScene(Map<String, Object> params) {
        String sceneId = "scene-" + UUID.randomUUID().toString().substring(0, 8);
        
        Map<String, Object> scene = new HashMap<String, Object>();
        scene.put("sceneId", sceneId);
        scene.put("name", params.get("name"));
        scene.put("description", params.get("description"));
        scene.put("categoryId", params.get("categoryId"));
        scene.put("icon", params.get("icon"));
        scene.put("status", params.getOrDefault("status", "active"));
        scene.put("config", params.get("config"));
        scene.put("createTime", System.currentTimeMillis());
        
        scenes.put(sceneId, scene);
        sceneCapabilities.put(sceneId, new ArrayList<Map<String, Object>>());
        return scene;
    }

    @Override
    public Map<String, Object> updateScene(String sceneId, Map<String, Object> params) {
        Map<String, Object> scene = scenes.get(sceneId);
        if (scene == null) {
            return null;
        }
        
        if (params.containsKey("name")) {
            scene.put("name", params.get("name"));
        }
        if (params.containsKey("description")) {
            scene.put("description", params.get("description"));
        }
        if (params.containsKey("categoryId")) {
            scene.put("categoryId", params.get("categoryId"));
        }
        if (params.containsKey("icon")) {
            scene.put("icon", params.get("icon"));
        }
        if (params.containsKey("status")) {
            scene.put("status", params.get("status"));
        }
        if (params.containsKey("config")) {
            scene.put("config", params.get("config"));
        }
        scene.put("updateTime", System.currentTimeMillis());
        
        return scene;
    }

    @Override
    public boolean deleteScene(String sceneId) {
        scenes.remove(sceneId);
        sceneCapabilities.remove(sceneId);
        return true;
    }

    // ==================== 场景能力管理 ====================

    @Override
    public List<Map<String, Object>> getSceneCapabilities(String sceneId) {
        List<Map<String, Object>> caps = sceneCapabilities.get(sceneId);
        return caps != null ? new ArrayList<Map<String, Object>>(caps) : new ArrayList<Map<String, Object>>();
    }

    @Override
    public boolean addSceneCapability(String sceneId, Map<String, Object> capability) {
        List<Map<String, Object>> caps = sceneCapabilities.get(sceneId);
        if (caps == null) {
            return false;
        }
        
        String capabilityId = "cap-" + UUID.randomUUID().toString().substring(0, 8);
        capability.put("capabilityId", capabilityId);
        capability.put("sceneId", sceneId);
        capability.put("createTime", System.currentTimeMillis());
        
        caps.add(capability);
        return true;
    }

    @Override
    public boolean removeSceneCapability(String sceneId, String capabilityId) {
        List<Map<String, Object>> caps = sceneCapabilities.get(sceneId);
        if (caps == null) {
            return false;
        }
        
        for (int i = 0; i < caps.size(); i++) {
            if (capabilityId.equals(caps.get(i).get("capabilityId"))) {
                caps.remove(i);
                return true;
            }
        }
        return false;
    }

    // ==================== 能力调用 ====================

    @Override
    public Map<String, Object> invokeCapability(String sceneId, String capabilityId, Map<String, Object> params) {
        Map<String, Object> scene = scenes.get(sceneId);
        if (scene == null) {
            Map<String, Object> error = new HashMap<String, Object>();
            error.put("success", false);
            error.put("error", "Scene not found: " + sceneId);
            return error;
        }
        
        List<Map<String, Object>> caps = sceneCapabilities.get(sceneId);
        if (caps == null) {
            Map<String, Object> error = new HashMap<String, Object>();
            error.put("success", false);
            error.put("error", "No capabilities for scene: " + sceneId);
            return error;
        }
        
        Map<String, Object> capability = null;
        for (Map<String, Object> cap : caps) {
            if (capabilityId.equals(cap.get("capabilityId"))) {
                capability = cap;
                break;
            }
        }
        
        if (capability == null) {
            Map<String, Object> error = new HashMap<String, Object>();
            error.put("success", false);
            error.put("error", "Capability not found: " + capabilityId);
            return error;
        }
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("sceneId", sceneId);
        result.put("capabilityId", capabilityId);
        result.put("capabilityName", capability.get("name"));
        result.put("params", params);
        result.put("result", "Mock execution result for " + capability.get("name"));
        result.put("executeTime", System.currentTimeMillis());
        
        return result;
    }

    @Override
    public List<Map<String, Object>> batchInvoke(List<Map<String, Object>> requests) {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        
        for (Map<String, Object> request : requests) {
            String sceneId = (String) request.get("sceneId");
            String capabilityId = (String) request.get("capabilityId");
            @SuppressWarnings("unchecked")
            Map<String, Object> params = (Map<String, Object>) request.get("params");
            
            results.add(invokeCapability(sceneId, capabilityId, params));
        }
        
        return results;
    }

    @Override
    public Object invoke(String capability, Map<String, Object> params) {
        switch (capability) {
            case "business.category.list":
                return getCategoryList();
            case "business.category.tree":
                return getCategoryTree();
            case "business.category.get":
                return getCategory((String) params.get("categoryId"));
            case "business.category.create":
                return createCategory(params);
            case "business.category.update":
                return updateCategory((String) params.get("categoryId"), params);
            case "business.category.delete":
                return deleteCategory((String) params.get("categoryId"));
            case "business.scene.list":
                return getSceneList((String) params.get("category"));
            case "business.scene.get":
                return getScene((String) params.get("sceneId"));
            case "business.scene.create":
                return createScene(params);
            case "business.scene.update":
                return updateScene((String) params.get("sceneId"), params);
            case "business.scene.delete":
                return deleteScene((String) params.get("sceneId"));
            case "business.capability.list":
                return getSceneCapabilities((String) params.get("sceneId"));
            case "business.capability.add":
                @SuppressWarnings("unchecked")
                Map<String, Object> cap = (Map<String, Object>) params.get("capability");
                return addSceneCapability((String) params.get("sceneId"), cap);
            case "business.capability.remove":
                return removeSceneCapability(
                    (String) params.get("sceneId"),
                    (String) params.get("capabilityId")
                );
            case "business.capability.invoke":
                return invokeCapability(
                    (String) params.get("sceneId"),
                    (String) params.get("capabilityId"),
                    params
                );
            case "business.capability.batchInvoke":
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> requests = (List<Map<String, Object>>) params.get("requests");
                return batchInvoke(requests);
            default:
                throw new IllegalArgumentException("Unknown capability: " + capability);
        }
    }
}
