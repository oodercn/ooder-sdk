package net.ooder.scene.core.provider;

import net.ooder.sdk.api.capability.Capability;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapCompatibilityChecker {
    public boolean checkCompatibility(String version1, String version2) {
        if (version1 == null || version2 == null) {
            return false;
        }
        
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");

        if (v1.length == 0 || v2.length == 0) {
            return false;
        }

        if (!v1[0].equals(v2[0])) {
            return false;
        }

        if (v1.length > 1 && v2.length > 1 && !v1[1].equals(v2[1])) {
            int v1Minor = Integer.parseInt(v1[1]);
            int v2Minor = Integer.parseInt(v2[1]);
            return v2Minor >= v1Minor;
        }

        return true;
    }

    public DependencyTree buildDependencyTree(Capability capability) {
        return new DependencyTree(capability);
    }

    public class DependencyTree {
        private Capability root;
        private Map<String, DependencyTree> children;

        public DependencyTree(Capability root) {
            this.root = root;
            this.children = new ConcurrentHashMap<>();
        }

        public Capability getRoot() {
            return root;
        }

        public void addDependency(Capability dependency) {
            children.put(dependency.getCapabilityId(), new DependencyTree(dependency));
        }

        public Map<String, DependencyTree> getChildren() {
            return children;
        }

        public boolean hasCycle() {
            return false;
        }
    }
}
