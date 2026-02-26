package net.ooder.sdk.skill.impl;

import net.ooder.sdk.skill.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SkillMdRegistryImpl implements SkillMdRegistry {

    private final Map<String, SkillMdDocument> skills = new ConcurrentHashMap<>();
    private final List<SkillRegistryListener> listeners = new ArrayList<>();
    private final Map<String, List<String>> validationErrors = new ConcurrentHashMap<>();

    @Override
    public void register(SkillMdDocument document) {
        if (document == null || document.getSkillId() == null) {
            throw new IllegalArgumentException("Skill document and skillId cannot be null");
        }

        String skillId = document.getSkillId();
        boolean isUpdate = skills.containsKey(skillId);

        skills.put(skillId, document);

        validateSkillInternal(skillId);

        if (isUpdate) {
            notifySkillUpdated(document);
        } else {
            notifySkillRegistered(document);
        }
    }

    @Override
    public void registerFromPath(String skillMdPath) {
        SkillMdParser parser = new SkillMdParserImpl();
        SkillMdDocument document = parser.parseFile(skillMdPath);
        register(document);
    }

    @Override
    public void unregister(String skillId) {
        if (skillId == null) {
            return;
        }

        SkillMdDocument removed = skills.remove(skillId);
        validationErrors.remove(skillId);

        if (removed != null) {
            notifySkillUnregistered(skillId);
        }
    }

    @Override
    public Optional<SkillMdDocument> getSkill(String skillId) {
        return Optional.ofNullable(skills.get(skillId));
    }

    @Override
    public List<SkillMdDocument> getAllSkills() {
        return new ArrayList<>(skills.values());
    }

    @Override
    public List<SkillMdDocument> getSkillsByDomain(String domain) {
        if (domain == null) {
            return Collections.emptyList();
        }

        return skills.values().stream()
                .filter(skill -> domain.equalsIgnoreCase(skill.getDomain()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SkillMdDocument> searchSkills(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSkills();
        }

        String lowerKeyword = keyword.toLowerCase();

        return skills.values().stream()
                .filter(skill -> matchesKeyword(skill, lowerKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<SkillMdDocument> getSkillsByTag(String tag) {
        if (tag == null) {
            return Collections.emptyList();
        }

        String lowerTag = tag.toLowerCase();

        return skills.values().stream()
                .filter(skill -> skill.getTags() != null &&
                        skill.getTags().stream()
                                .anyMatch(t -> t.toLowerCase().contains(lowerTag)))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasSkill(String skillId) {
        return skillId != null && skills.containsKey(skillId);
    }

    @Override
    public int getSkillCount() {
        return skills.size();
    }

    @Override
    public void clear() {
        List<String> skillIds = new ArrayList<>(skills.keySet());
        skills.clear();
        validationErrors.clear();

        for (String skillId : skillIds) {
            notifySkillUnregistered(skillId);
        }
    }

    @Override
    public void addRegistryListener(SkillRegistryListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeRegistryListener(SkillRegistryListener listener) {
        listeners.remove(listener);
    }

    @Override
    public Map<String, Object> getSkillMetadata(String skillId) {
        SkillMdDocument skill = skills.get(skillId);
        if (skill == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> metadata = new HashMap<>();
        if (skill.getMetadata() != null) {
            metadata.putAll(skill.getMetadata());
        }
        metadata.put("skillId", skill.getSkillId());
        metadata.put("name", skill.getName());
        metadata.put("version", skill.getVersion());
        metadata.put("author", skill.getAuthor());
        metadata.put("domain", skill.getDomain());
        metadata.put("tags", skill.getTags());

        return metadata;
    }

    @Override
    public List<String> getSkillInputNames(String skillId) {
        SkillMdDocument skill = skills.get(skillId);
        if (skill == null || skill.getInputs() == null) {
            return Collections.emptyList();
        }

        return skill.getInputs().stream()
                .map(SkillMdParameter::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getSkillOutputNames(String skillId) {
        SkillMdDocument skill = skills.get(skillId);
        if (skill == null || skill.getOutputs() == null) {
            return Collections.emptyList();
        }

        return skill.getOutputs().stream()
                .map(SkillMdParameter::getName)
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateSkill(String skillId) {
        return validateSkillInternal(skillId);
    }

    @Override
    public List<String> getValidationErrors(String skillId) {
        return validationErrors.getOrDefault(skillId, Collections.emptyList());
    }

    private boolean validateSkillInternal(String skillId) {
        SkillMdDocument skill = skills.get(skillId);
        if (skill == null) {
            validationErrors.put(skillId, Collections.singletonList("Skill not found"));
            return false;
        }

        List<String> errors = new ArrayList<>();

        if (skill.getName() == null || skill.getName().trim().isEmpty()) {
            errors.add("Skill name is required");
        }

        if (skill.getDescription() == null || skill.getDescription().trim().isEmpty()) {
            errors.add("Skill description is required");
        }

        if (skill.getInputs() != null) {
            for (SkillMdParameter param : skill.getInputs()) {
                if (param.getName() == null || param.getName().trim().isEmpty()) {
                    errors.add("Input parameter name is required");
                }
                if (param.getType() == null || param.getType().trim().isEmpty()) {
                    errors.add("Input parameter type is required for: " + param.getName());
                }
            }
        }

        if (skill.getExecutorClass() == null || skill.getExecutorClass().trim().isEmpty()) {
            errors.add("Executor class is required");
        }

        validationErrors.put(skillId, errors);
        return errors.isEmpty();
    }

    private boolean matchesKeyword(SkillMdDocument skill, String keyword) {
        if (skill.getName() != null && skill.getName().toLowerCase().contains(keyword)) {
            return true;
        }
        if (skill.getDescription() != null && skill.getDescription().toLowerCase().contains(keyword)) {
            return true;
        }
        if (skill.getSkillId() != null && skill.getSkillId().toLowerCase().contains(keyword)) {
            return true;
        }
        if (skill.getDomain() != null && skill.getDomain().toLowerCase().contains(keyword)) {
            return true;
        }
        if (skill.getTags() != null) {
            return skill.getTags().stream()
                    .anyMatch(tag -> tag.toLowerCase().contains(keyword));
        }
        return false;
    }

    private void notifySkillRegistered(SkillMdDocument document) {
        for (SkillRegistryListener listener : listeners) {
            try {
                listener.onSkillRegistered(document);
            } catch (Exception e) {
                // Log error but don't stop notification
            }
        }
    }

    private void notifySkillUnregistered(String skillId) {
        for (SkillRegistryListener listener : listeners) {
            try {
                listener.onSkillUnregistered(skillId);
            } catch (Exception e) {
                // Log error but don't stop notification
            }
        }
    }

    private void notifySkillUpdated(SkillMdDocument document) {
        for (SkillRegistryListener listener : listeners) {
            try {
                listener.onSkillUpdated(document);
            } catch (Exception e) {
                // Log error but don't stop notification
            }
        }
    }
}
