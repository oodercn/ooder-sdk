package net.ooder.scene.core.spi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 扩展点注册中心
 * 
 * <p>管理所有 SPI 扩展点的注册、发现和获取。支持按类型、名称、优先级等多种方式检索扩展实现。</p>
 * 
 * <p>核心功能：</p>
 * <ul>
 *   <li>扩展点注册与注销</li>
 *   <li>按类型获取扩展实现列表</li>
 *   <li>按名称获取特定扩展实现</li>
 *   <li>优先级排序</li>
 *   <li>扩展点生命周期管理</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class ExtensionPointRegistry {
    
    // 扩展点存储：扩展点类型 -> 扩展实现列表
    private final Map<Class<?>, List<ExtensionHolder<?>>> extensions = new ConcurrentHashMap<>();
    
    // 命名扩展点存储：扩展点类型 -> (名称 -> 扩展实现)
    private final Map<Class<?>, Map<String, ExtensionHolder<?>>> namedExtensions = new ConcurrentHashMap<>();
    
    // 单例实例
    private static volatile ExtensionPointRegistry instance;
    private static final Object LOCK = new Object();
    
    /**
     * 获取注册中心单例
     */
    public static ExtensionPointRegistry getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ExtensionPointRegistry();
                }
            }
        }
        return instance;
    }
    
    /**
     * 重置注册中心（主要用于测试）
     */
    public static void reset() {
        synchronized (LOCK) {
            if (instance != null) {
                instance.extensions.clear();
                instance.namedExtensions.clear();
            }
            instance = null;
        }
    }
    
    // === 注册方法 ===
    
    /**
     * 注册扩展实现
     * 
     * @param extensionPoint 扩展点接口类型
     * @param implementation 扩展实现
     * @param <T> 扩展点类型
     */
    public <T> void register(Class<T> extensionPoint, T implementation) {
        register(extensionPoint, implementation, 0, null);
    }
    
    /**
     * 注册扩展实现（带优先级）
     * 
     * @param extensionPoint 扩展点接口类型
     * @param implementation 扩展实现
     * @param priority 优先级（数值越小优先级越高）
     * @param <T> 扩展点类型
     */
    public <T> void register(Class<T> extensionPoint, T implementation, int priority) {
        register(extensionPoint, implementation, priority, null);
    }
    
    /**
     * 注册命名扩展实现
     * 
     * @param extensionPoint 扩展点接口类型
     * @param implementation 扩展实现
     * @param name 扩展名称
     * @param <T> 扩展点类型
     */
    public <T> void register(Class<T> extensionPoint, T implementation, String name) {
        register(extensionPoint, implementation, 0, name);
    }
    
    /**
     * 注册扩展实现（完整参数）
     * 
     * @param extensionPoint 扩展点接口类型
     * @param implementation 扩展实现
     * @param priority 优先级
     * @param name 扩展名称
     * @param <T> 扩展点类型
     */
    public <T> void register(Class<T> extensionPoint, T implementation, int priority, String name) {
        if (extensionPoint == null || implementation == null) {
            throw new IllegalArgumentException("Extension point and implementation cannot be null");
        }
        
        if (!extensionPoint.isInstance(implementation)) {
            throw new IllegalArgumentException(
                "Implementation " + implementation.getClass().getName() + 
                " does not implement " + extensionPoint.getName()
            );
        }
        
        ExtensionHolder<T> holder = new ExtensionHolder<>(
            implementation, 
            priority, 
            name,
            implementation.getClass().getName()
        );
        
        // 添加到类型列表
        extensions.computeIfAbsent(extensionPoint, k -> new ArrayList<>()).add(holder);
        
        // 添加到命名映射
        if (name != null && !name.isEmpty()) {
            namedExtensions
                .computeIfAbsent(extensionPoint, k -> new ConcurrentHashMap<>())
                .put(name, holder);
        }
        
        // 按优先级排序
        sortByPriority(extensionPoint);
    }
    
    /**
     * 注销扩展实现
     * 
     * @param extensionPoint 扩展点接口类型
     * @param implementation 扩展实现
     * @param <T> 扩展点类型
     * @return 是否成功注销
     */
    public <T> boolean unregister(Class<T> extensionPoint, T implementation) {
        if (extensionPoint == null || implementation == null) {
            return false;
        }
        
        List<ExtensionHolder<?>> holders = extensions.get(extensionPoint);
        if (holders == null) {
            return false;
        }
        
        boolean removed = holders.removeIf(h -> h.getImplementation() == implementation);
        
        // 同时从命名映射中移除
        Map<String, ExtensionHolder<?>> namedMap = namedExtensions.get(extensionPoint);
        if (namedMap != null) {
            namedMap.values().removeIf(h -> h.getImplementation() == implementation);
        }
        
        return removed;
    }
    
    /**
     * 按名称注销扩展实现
     * 
     * @param extensionPoint 扩展点接口类型
     * @param name 扩展名称
     * @param <T> 扩展点类型
     * @return 是否成功注销
     */
    public <T> boolean unregisterByName(Class<T> extensionPoint, String name) {
        if (extensionPoint == null || name == null) {
            return false;
        }
        
        Map<String, ExtensionHolder<?>> namedMap = namedExtensions.get(extensionPoint);
        if (namedMap == null) {
            return false;
        }
        
        ExtensionHolder<?> holder = namedMap.remove(name);
        if (holder == null) {
            return false;
        }
        
        // 同时从列表中移除
        List<ExtensionHolder<?>> holders = extensions.get(extensionPoint);
        if (holders != null) {
            holders.remove(holder);
        }
        
        return true;
    }
    
    // === 查询方法 ===
    
    /**
     * 获取所有扩展实现
     * 
     * @param extensionPoint 扩展点接口类型
     * @param <T> 扩展点类型
     * @return 扩展实现列表（按优先级排序）
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getExtensions(Class<T> extensionPoint) {
        List<ExtensionHolder<?>> holders = extensions.get(extensionPoint);
        if (holders == null || holders.isEmpty()) {
            return Collections.emptyList();
        }
        
        return holders.stream()
                .map(h -> (T) h.getImplementation())
                .collect(Collectors.toList());
    }
    
    /**
     * 获取第一个扩展实现（最高优先级）
     * 
     * @param extensionPoint 扩展点接口类型
     * @param <T> 扩展点类型
     * @return 扩展实现，如果没有则返回 null
     */
    @SuppressWarnings("unchecked")
    public <T> T getFirstExtension(Class<T> extensionPoint) {
        List<ExtensionHolder<?>> holders = extensions.get(extensionPoint);
        if (holders == null || holders.isEmpty()) {
            return null;
        }
        return (T) holders.get(0).getImplementation();
    }
    
    /**
     * 按名称获取扩展实现
     * 
     * @param extensionPoint 扩展点接口类型
     * @param name 扩展名称
     * @param <T> 扩展点类型
     * @return 扩展实现，如果没有则返回 null
     */
    @SuppressWarnings("unchecked")
    public <T> T getExtensionByName(Class<T> extensionPoint, String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        
        Map<String, ExtensionHolder<?>> namedMap = namedExtensions.get(extensionPoint);
        if (namedMap == null) {
            return null;
        }
        
        ExtensionHolder<?> holder = namedMap.get(name);
        return holder != null ? (T) holder.getImplementation() : null;
    }
    
    /**
     * 获取支持特定类型的扩展实现
     * 
     * @param extensionPoint 扩展点接口类型
     * @param type 类型标识
     * @param <T> 扩展点类型
     * @return 扩展实现列表
     */
    public <T extends TypeSupportable> List<T> getExtensionsByType(Class<T> extensionPoint, String type) {
        return getExtensions(extensionPoint).stream()
                .filter(ext -> ext.supports(type))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取第一个支持特定类型的扩展实现
     * 
     * @param extensionPoint 扩展点接口类型
     * @param type 类型标识
     * @param <T> 扩展点类型
     * @return 扩展实现，如果没有则返回 null
     */
    public <T extends TypeSupportable> T getFirstExtensionByType(Class<T> extensionPoint, String type) {
        return getExtensions(extensionPoint).stream()
                .filter(ext -> ext.supports(type))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 检查是否存在扩展实现
     * 
     * @param extensionPoint 扩展点接口类型
     * @return 是否存在
     */
    public boolean hasExtension(Class<?> extensionPoint) {
        List<ExtensionHolder<?>> holders = extensions.get(extensionPoint);
        return holders != null && !holders.isEmpty();
    }
    
    /**
     * 检查是否存在指定名称的扩展实现
     * 
     * @param extensionPoint 扩展点接口类型
     * @param name 扩展名称
     * @return 是否存在
     */
    public boolean hasExtensionByName(Class<?> extensionPoint, String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        Map<String, ExtensionHolder<?>> namedMap = namedExtensions.get(extensionPoint);
        return namedMap != null && namedMap.containsKey(name);
    }
    
    /**
     * 获取所有注册的扩展点类型
     * 
     * @return 扩展点类型集合
     */
    public Set<Class<?>> getRegisteredExtensionPoints() {
        return new HashSet<>(extensions.keySet());
    }
    
    /**
     * 获取扩展实现数量
     * 
     * @param extensionPoint 扩展点接口类型
     * @return 扩展实现数量
     */
    public int getExtensionCount(Class<?> extensionPoint) {
        List<ExtensionHolder<?>> holders = extensions.get(extensionPoint);
        return holders != null ? holders.size() : 0;
    }
    
    // === 辅助方法 ===
    
    /**
     * 按优先级排序
     */
    private void sortByPriority(Class<?> extensionPoint) {
        List<ExtensionHolder<?>> holders = extensions.get(extensionPoint);
        if (holders != null) {
            holders.sort(Comparator.comparingInt(ExtensionHolder::getPriority));
        }
    }
    
    /**
     * 清空所有扩展点
     */
    public void clear() {
        extensions.clear();
        namedExtensions.clear();
    }
    
    /**
     * 扩展持有者
     */
    private static class ExtensionHolder<T> {
        private final T implementation;
        private final int priority;
        private final String name;
        private final String className;
        
        ExtensionHolder(T implementation, int priority, String name, String className) {
            this.implementation = implementation;
            this.priority = priority;
            this.name = name;
            this.className = className;
        }
        
        T getImplementation() {
            return implementation;
        }
        
        int getPriority() {
            return priority;
        }
        
        String getName() {
            return name;
        }
        
        String getClassName() {
            return className;
        }
    }
    
    /**
     * 类型支持接口
     * 扩展实现可以实现此接口以支持按类型检索
     */
    public interface TypeSupportable {
        /**
         * 检查是否支持指定类型
         * 
         * @param type 类型标识
         * @return 是否支持
         */
        boolean supports(String type);
    }
}
