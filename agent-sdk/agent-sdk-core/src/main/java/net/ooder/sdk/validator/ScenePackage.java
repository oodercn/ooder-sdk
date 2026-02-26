package net.ooder.sdk.validator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScenePackage {
    private String path;
    private File rootDir;
    
    public ScenePackage(String path) {
        this.path = path;
        this.rootDir = new File(path);
    }
    
    public boolean hasFile(String relativePath) {
        Path filePath = Paths.get(rootDir.getAbsolutePath(), relativePath);
        return Files.exists(filePath);
    }
    
    public String readFile(String relativePath) throws IOException {
        Path filePath = Paths.get(rootDir.getAbsolutePath(), relativePath);
        byte[] bytes = Files.readAllBytes(filePath);
        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
    }
    
    public String getPath() {
        return path;
    }
    
    public File getRootDir() {
        return rootDir;
    }
    
    public static ScenePackage load(String path) {
        return new ScenePackage(path);
    }
}