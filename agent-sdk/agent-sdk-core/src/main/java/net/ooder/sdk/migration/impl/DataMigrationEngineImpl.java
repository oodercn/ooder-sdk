package net.ooder.sdk.migration.impl;

import net.ooder.sdk.migration.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 数据迁移引擎实现
 */
public class DataMigrationEngineImpl implements DataMigrationEngine {

    private final Map<String, List<MigrationScript>> scriptRegistry = new ConcurrentHashMap<>();
    private final Map<String, List<MigrationResult>> migrationHistory = new ConcurrentHashMap<>();
    private final Path scriptBaseDir;
    private final Path backupBaseDir;

    public DataMigrationEngineImpl(Path scriptBaseDir, Path backupBaseDir) {
        this.scriptBaseDir = scriptBaseDir;
        this.backupBaseDir = backupBaseDir;
        loadScriptsFromDisk();
    }

    @Override
    public MigrationResult migrate(String skillId, String fromVersion, String toVersion, MigrationContext context) {
        LocalDateTime startTime = LocalDateTime.now();

        List<MigrationScript> scripts = findMigrationScripts(skillId, fromVersion, toVersion);
        if (scripts.isEmpty()) {
            return MigrationResult.success(skillId, fromVersion, toVersion, Collections.emptyList());
        }

        String backupLocation = null;
        List<String> executedScripts = new ArrayList<>();

        try {
            if (scripts.stream().anyMatch(MigrationScript::isBackupRequired)) {
                backupLocation = createBackup(skillId);
            }

            for (MigrationScript script : scripts) {
                if (context.isDryRun()) {
                    executedScripts.add(script.getId() + " (dry-run)");
                } else {
                    if (!executeScript(script, context)) {
                        if (context.isAutoRollback() && backupLocation != null) {
                            restoreFromBackup(skillId, backupLocation);
                        }
                        MigrationResult result = MigrationResult.failure(skillId,
                            "Script execution failed: " + script.getId(), null);
                        result.setStartTime(startTime);
                        result.setBackupLocation(backupLocation);
                        recordHistory(skillId, result);
                        return result;
                    }
                    executedScripts.add(script.getId());
                }
            }

            MigrationResult result = MigrationResult.success(skillId, fromVersion, toVersion, executedScripts);
            result.setStartTime(startTime);
            result.setBackupLocation(backupLocation);
            recordHistory(skillId, result);
            return result;

        } catch (Exception e) {
            if (context.isAutoRollback() && backupLocation != null) {
                restoreFromBackup(skillId, backupLocation);
            }
            MigrationResult result = MigrationResult.failure(skillId, "Migration failed: " + e.getMessage(), e);
            result.setStartTime(startTime);
            result.setBackupLocation(backupLocation);
            recordHistory(skillId, result);
            return result;
        }
    }

    @Override
    public boolean needsMigration(String skillId, String fromVersion, String toVersion) {
        return !findMigrationScripts(skillId, fromVersion, toVersion).isEmpty();
    }

    @Override
    public List<MigrationScript> findMigrationScripts(String skillId, String fromVersion, String toVersion) {
        List<MigrationScript> allScripts = scriptRegistry.getOrDefault(skillId, Collections.emptyList());

        return allScripts.stream()
            .filter(script -> compareVersions(script.getFromVersion(), fromVersion) >= 0)
            .filter(script -> compareVersions(script.getToVersion(), toVersion) <= 0)
            .sorted(Comparator.comparing(s -> s.getFromVersion()))
            .collect(Collectors.toList());
    }

    @Override
    public void registerScript(MigrationScript script) {
        scriptRegistry.computeIfAbsent(script.getSkillId(), k -> new ArrayList<>()).add(script);
    }

    @Override
    public boolean executeScript(MigrationScript script, MigrationContext context) {
        try {
            switch (script.getType()) {
                case SQL:
                    return executeSqlScript(script, context);
                case JS:
                    return executeJsScript(script, context);
                case GROOVY:
                    return executeGroovyScript(script, context);
                case JSON:
                    return executeJsonScript(script, context);
                case JAVA:
                    return executeJavaScript(script, context);
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String createBackup(String skillId) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupName = skillId + "_" + timestamp;
            Path backupDir = backupBaseDir.resolve(backupName);
            Files.createDirectories(backupDir);

            Path dataDir = scriptBaseDir.resolve("data").resolve(skillId);
            if (Files.exists(dataDir)) {
                copyDirectory(dataDir, backupDir);
            }

            return backupDir.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean restoreFromBackup(String skillId, String backupLocation) {
        try {
            Path backupDir = Paths.get(backupLocation);
            Path dataDir = scriptBaseDir.resolve("data").resolve(skillId);

            if (Files.exists(dataDir)) {
                deleteDirectory(dataDir);
            }

            copyDirectory(backupDir, dataDir);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<MigrationResult> getMigrationHistory(String skillId) {
        return migrationHistory.getOrDefault(skillId, new ArrayList<>());
    }

    @Override
    public MigrationResult rollback(String skillId) {
        List<MigrationResult> history = migrationHistory.get(skillId);
        if (history == null || history.isEmpty()) {
            return MigrationResult.failure(skillId, "No migration history found", null);
        }

        MigrationResult lastMigration = null;
        for (int i = history.size() - 1; i >= 0; i--) {
            if (history.get(i).isSuccess()) {
                lastMigration = history.get(i);
                break;
            }
        }

        if (lastMigration == null || lastMigration.getBackupLocation() == null) {
            return MigrationResult.failure(skillId, "No backup available for rollback", null);
        }

        if (restoreFromBackup(skillId, lastMigration.getBackupLocation())) {
            MigrationResult result = new MigrationResult();
            result.setSuccess(true);
            result.setSkillId(skillId);
            result.setStatus(MigrationResult.MigrationStatus.ROLLED_BACK);
            result.setMessage("Rolled back to backup: " + lastMigration.getBackupLocation());
            result.setEndTime(LocalDateTime.now());
            return result;
        }

        return MigrationResult.failure(skillId, "Rollback failed", null);
    }

    @Override
    public boolean validateScript(MigrationScript script) {
        if (script.getScriptPath() == null || !Files.exists(script.getScriptPath())) {
            return false;
        }

        try {
            String content = new String(Files.readAllBytes(script.getScriptPath()));
            String checksum = calculateChecksum(content);
            return checksum.equals(script.getChecksum());
        } catch (Exception e) {
            return false;
        }
    }

    private void loadScriptsFromDisk() {
        if (!Files.exists(scriptBaseDir)) {
            return;
        }

        try {
            Files.walk(scriptBaseDir)
                .filter(Files::isRegularFile)
                .forEach(this::loadScript);
        } catch (IOException e) {
            // Ignore
        }
    }

    private void loadScript(Path scriptPath) {
        try {
            String fileName = scriptPath.getFileName().toString();
            MigrationScript script = new MigrationScript();
            script.setId(fileName);
            script.setScriptPath(scriptPath);

            if (fileName.endsWith(".sql")) {
                script.setType(MigrationScript.ScriptType.SQL);
            } else if (fileName.endsWith(".js")) {
                script.setType(MigrationScript.ScriptType.JS);
            } else if (fileName.endsWith(".groovy")) {
                script.setType(MigrationScript.ScriptType.GROOVY);
            } else if (fileName.endsWith(".json")) {
                script.setType(MigrationScript.ScriptType.JSON);
            } else if (fileName.endsWith(".java")) {
                script.setType(MigrationScript.ScriptType.JAVA);
            }

            String content = new String(Files.readAllBytes(scriptPath));
            script.setChecksum(calculateChecksum(content));

            registerScript(script);
        } catch (Exception e) {
            // Ignore invalid scripts
        }
    }

    private boolean executeSqlScript(MigrationScript script, MigrationContext context) {
        return true;
    }

    private boolean executeJsScript(MigrationScript script, MigrationContext context) {
        return true;
    }

    private boolean executeGroovyScript(MigrationScript script, MigrationContext context) {
        return true;
    }

    private boolean executeJsonScript(MigrationScript script, MigrationContext context) {
        return true;
    }

    private boolean executeJavaScript(MigrationScript script, MigrationContext context) {
        return true;
    }

    private void recordHistory(String skillId, MigrationResult result) {
        migrationHistory.computeIfAbsent(skillId, k -> new ArrayList<>()).add(result);
    }

    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
            int p1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int p2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (p1 != p2) {
                return Integer.compare(p1, p2);
            }
        }
        return 0;
    }

    private String calculateChecksum(String content) {
        return String.valueOf(content.hashCode());
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source).forEach(path -> {
            try {
                Path targetPath = target.resolve(source.relativize(path));
                if (Files.isDirectory(path)) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                // Ignore
            }
        });
    }

    private void deleteDirectory(Path dir) throws IOException {
        Files.walk(dir)
            .sorted((a, b) -> -a.compareTo(b))
            .forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    // Ignore
                }
            });
    }
}
