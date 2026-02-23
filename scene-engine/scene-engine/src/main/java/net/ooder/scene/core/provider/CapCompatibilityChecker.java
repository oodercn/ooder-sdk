package net.ooder.scene.core.provider;

import net.ooder.scene.core.CapabilityInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapCompatibilityChecker {
    public boolean checkCompatibility(String version1, String version2) {
        // 检查两个版本是否兼容
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");

        // 主版本号不同，不兼容
        if (!v1[0].equals(v2[0])) {
            return false;
        }

        // 次版本号不同，检查是否向后兼容
        if (!v1[1].equals(v2[1])) {
            int v1Minor = Integer.parseInt(v1[1]);
            int v2Minor = Integer.parseInt(v2[1]);
            // 只检查是否是向后兼容（v2 >= v1）
            return v2Minor >= v1Minor;
        }

        // 补丁版本号不同，兼容
        return true;
    }

    public DependencyTree buildDependencyTree(CapabilityInfo info) {
        // 构建依赖树
        return new DependencyTree(info);
    }

    public class DependencyTree {
        private CapabilityInfo root;
        private Map<String, DependencyTree> children;

        public DependencyTree(CapabilityInfo root) {
            this.root = root;
            this.children = new ConcurrentHashMap<>();
        }

        public CapabilityInfo getRoot() {
            return root;
        }

        public void addDependency(CapabilityInfo dependency) {
            children.put(dependency.getCapId(), new DependencyTree(dependency));
        }

        public Map<String, DependencyTree> getChildren() {
            return children;
        }

        public boolean hasCycle() {
            // 检查是否有循环依赖
            return false; // 暂时简单实现
        }
    }
}
