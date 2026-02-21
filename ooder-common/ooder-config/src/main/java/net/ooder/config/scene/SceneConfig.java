package net.ooder.config.scene;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SceneConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String description;
    
    private OrgSceneConfig org;
    private VfsSceneConfig vfs;
    private MsgSceneConfig msg;
    private JdsSceneConfig jds;
    
    private Map<String, Object> extensions;
    
    public SceneConfig() {
        this.extensions = new HashMap<String, Object>();
    }
    
    public static SceneConfigBuilder builder() {
        return new SceneConfigBuilder();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public OrgSceneConfig getOrg() {
        return org;
    }
    
    public void setOrg(OrgSceneConfig org) {
        this.org = org;
    }
    
    public VfsSceneConfig getVfs() {
        return vfs;
    }
    
    public void setVfs(VfsSceneConfig vfs) {
        this.vfs = vfs;
    }
    
    public MsgSceneConfig getMsg() {
        return msg;
    }
    
    public void setMsg(MsgSceneConfig msg) {
        this.msg = msg;
    }
    
    public JdsSceneConfig getJds() {
        return jds;
    }
    
    public void setJds(JdsSceneConfig jds) {
        this.jds = jds;
    }
    
    public Map<String, Object> getExtensions() {
        return extensions;
    }
    
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }
    
    @Override
    public String toString() {
        return "SceneConfig{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
    
    public static class SceneConfigBuilder {
        private SceneConfig config = new SceneConfig();
        
        public SceneConfigBuilder id(String id) {
            config.id = id;
            return this;
        }
        
        public SceneConfigBuilder name(String name) {
            config.name = name;
            return this;
        }
        
        public SceneConfigBuilder description(String description) {
            config.description = description;
            return this;
        }
        
        public SceneConfigBuilder org(OrgSceneConfig org) {
            config.org = org;
            return this;
        }
        
        public SceneConfigBuilder vfs(VfsSceneConfig vfs) {
            config.vfs = vfs;
            return this;
        }
        
        public SceneConfigBuilder msg(MsgSceneConfig msg) {
            config.msg = msg;
            return this;
        }
        
        public SceneConfigBuilder jds(JdsSceneConfig jds) {
            config.jds = jds;
            return this;
        }
        
        public SceneConfigBuilder extensions(Map<String, Object> extensions) {
            config.extensions = extensions;
            return this;
        }
        
        public SceneConfig build() {
            return config;
        }
    }
}
