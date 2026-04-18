package net.ooder.sdk.cli.security;

import java.util.*;

public class CliPermissionMatrix {

    public enum Role {
        INSTALLER,
        ADMIN,
        LEADER,
        COLLABORATOR
    }

    private static final Map<Role, Set<String>> ROLE_PERMISSIONS = new EnumMap<>(Role.class);

    static {
        Set<String> installerPerms = new HashSet<>(Arrays.asList(
            "skill:list", "skill:info", "skill:install", "skill:uninstall",
            "skill:start", "skill:stop", "skill:update", "skill:exec",
            "status", "help"
        ));
        ROLE_PERMISSIONS.put(Role.INSTALLER, installerPerms);

        Set<String> adminPerms = new HashSet<>(installerPerms);
        adminPerms.addAll(Arrays.asList(
            "scene:create", "scene:list", "scene:info", "scene:invoke", "scene:event",
            "nlp:convert", "nlp:skills", "nlp:execute",
            "llm:generate", "llm:intent", "llm:chat",
            "task:list", "task:status"
        ));
        ROLE_PERMISSIONS.put(Role.ADMIN, adminPerms);

        Set<String> leaderPerms = new HashSet<>(Arrays.asList(
            "skill:list", "skill:info",
            "scene:create", "scene:list", "scene:info", "scene:invoke", "scene:event",
            "nlp:convert", "nlp:skills",
            "llm:generate", "llm:intent",
            "task:list", "task:status",
            "status", "help"
        ));
        ROLE_PERMISSIONS.put(Role.LEADER, leaderPerms);

        Set<String> collaboratorPerms = new HashSet<>(Arrays.asList(
            "skill:list", "skill:info",
            "scene:list", "scene:info", "scene:invoke",
            "nlp:convert", "nlp:skills",
            "llm:generate", "llm:intent",
            "task:list",
            "status", "help"
        ));
        ROLE_PERMISSIONS.put(Role.COLLABORATOR, collaboratorPerms);
    }

    private Role currentRole = Role.ADMIN;

    public CliPermissionMatrix() {}

    public CliPermissionMatrix(Role role) {
        this.currentRole = role;
    }

    public void setRole(Role role) {
        this.currentRole = role;
    }

    public Role getRole() {
        return currentRole;
    }

    public boolean hasPermission(String command) {
        Set<String> perms = ROLE_PERMISSIONS.get(currentRole);
        if (perms == null) return false;

        if (perms.contains(command)) return true;

        String category = command.split(":")[0];
        return perms.contains(category + ":*");
    }

    public Set<String> getAllowedCommands() {
        return Collections.unmodifiableSet(ROLE_PERMISSIONS.getOrDefault(currentRole, Collections.emptySet()));
    }

    public Set<String> getDeniedCommands() {
        Set<String> all = new HashSet<>();
        for (Set<String> perms : ROLE_PERMISSIONS.values()) {
            all.addAll(perms);
        }
        Set<String> allowed = ROLE_PERMISSIONS.getOrDefault(currentRole, Collections.emptySet());
        Set<String> denied = new HashSet<>(all);
        denied.removeAll(allowed);
        return denied;
    }

    public static Set<String> getCommandsForRole(Role role) {
        return Collections.unmodifiableSet(ROLE_PERMISSIONS.getOrDefault(role, Collections.emptySet()));
    }

    public static Role resolveRole(String roleName) {
        if (roleName == null) return Role.COLLABORATOR;
        try {
            return Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.COLLABORATOR;
        }
    }
}
