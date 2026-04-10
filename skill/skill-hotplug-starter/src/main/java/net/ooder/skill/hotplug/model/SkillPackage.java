package net.ooder.skill.hotplug.model;

import net.ooder.skill.hotplug.CategoryResolver;
import net.ooder.skill.hotplug.SkillForm;
import net.ooder.skill.hotplug.SkillFormResolver;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Skill包封装
 * 表示一个已打包的Skill JAR文件
 */
public class SkillPackage {

    private final File file;
    private final SkillMetadata metadata;

    public SkillPackage(File file, SkillMetadata metadata) {
        this.file = file;
        this.metadata = metadata;
    }

    /**
     * 从文件加载Skill包
     */
    public static SkillPackage fromFile(File file) throws IOException {
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Invalid skill package file: " + file);
        }

        SkillMetadata metadata = loadMetadata(file);

        return new SkillPackage(file, metadata);
    }

    /**
     * 获取资源
     */
    public InputStream getResource(String path) throws IOException {
        try (JarFile jarFile = new JarFile(file)) {
            JarEntry entry = jarFile.getJarEntry(path);
            if (entry != null) {
                try (InputStream is = jarFile.getInputStream(entry)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, len);
                    }
                    return new ByteArrayInputStream(baos.toByteArray());
                }
            }
            
            String bootPath = "BOOT-INF/classes/" + path;
            entry = jarFile.getJarEntry(bootPath);
            if (entry != null) {
                try (InputStream is = jarFile.getInputStream(entry)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, len);
                    }
                    return new ByteArrayInputStream(baos.toByteArray());
                }
            }
        }
        return null;
    }

    /**
     * 获取资源URL
     */
    public URL getResourceUrl(String path) throws IOException {
        try (JarFile jarFile = new JarFile(file)) {
            JarEntry entry = jarFile.getJarEntry(path);
            if (entry != null) {
                return new URL("jar:file:" + file.getAbsolutePath() + "!/" + path);
            }
            
            String bootPath = "BOOT-INF/classes/" + path;
            entry = jarFile.getJarEntry(bootPath);
            if (entry != null) {
                return new URL("jar:file:" + file.getAbsolutePath() + "!/" + bootPath);
            }
        }
        return null;
    }

    /**
     * 列出所有资源
     */
    public java.util.List<String> listResources(String prefix) throws IOException {
        java.util.List<String> resources = new java.util.ArrayList<>();

        try (JarFile jarFile = new JarFile(file)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith(prefix)) {
                    resources.add(name);
                }
            }
        }

        return resources;
    }

    private static SkillMetadata loadMetadata(File jarFile) throws IOException {
        SkillPackage tempPackage = new SkillPackage(jarFile, null);
        
        try (InputStream is = tempPackage.getResource("skill.yaml")) {
            if (is != null) {
                return SkillMetadata.loadFromYaml(is);
            }
        }
        
        throw new IOException("skill.yaml not found in: " + jarFile);
    }

    public File getFile() {
        return file;
    }

    public SkillMetadata getMetadata() {
        return metadata;
    }

    public String getVersion() {
        return metadata.getVersion();
    }

    /**
     * 获取 Skill ID
     */
    public String getSkillId() {
        return metadata != null ? metadata.getId() : null;
    }

    /**
     * 获取 Skill 名称
     */
    public String getName() {
        return metadata != null ? metadata.getName() : null;
    }

    /**
     * 获取描述
     */
    public String getDescription() {
        return metadata != null ? metadata.getDescription() : null;
    }

    /**
     * 获取作者
     */
    public String getAuthor() {
        return metadata != null ? metadata.getAuthor() : null;
    }

    /**
     * 获取分类（自动推断）
     * 优先使用 metadata.category，如果为空则从 skillId 推断
     */
    public String getCategory() {
        try {
            if (metadata == null) {
                return "sys";
            }
            // 使用 CategoryResolver 进行推断
            String category = new CategoryResolver().resolve(metadata);
            return category != null ? category : "sys";
        } catch (Exception e) {
            // 捕获任何异常，返回默认值
            return "sys";
        }
    }

    /**
     * 获取 Skill 形态（自动推断）
     * 优先使用 metadata.form，如果为空则从 skillId 推断
     */
    public String getSkillForm() {
        try {
            if (metadata == null) {
                return SkillForm.PROVIDER.name();
            }
            // 使用 SkillFormResolver 进行推断
            SkillForm form = new SkillFormResolver().resolve(metadata);
            return form != null ? form.name() : SkillForm.PROVIDER.name();
        } catch (Exception e) {
            // 捕获任何异常，返回默认值
            return SkillForm.PROVIDER.name();
        }
    }

    /**
     * 判断是否为场景应用
     */
    public boolean isSceneCapability() {
        return SkillForm.SCENE.name().equals(getSkillForm());
    }

    @Override
    public String toString() {
        return "SkillPackage{" +
                "file=" + file.getName() +
                ", metadata=" + metadata +
                '}';
    }
}
