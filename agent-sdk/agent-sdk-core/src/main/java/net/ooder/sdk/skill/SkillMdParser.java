package net.ooder.sdk.skill;

import java.util.List;
import java.util.Map;

public interface SkillMdParser {

    SkillMdDocument parse(String markdownContent);

    SkillMdDocument parseFile(String filePath);

    boolean validate(String markdownContent);

    List<String> extractSections(String markdownContent);

    Map<String, String> extractMetadata(String markdownContent);

    List<SkillMdSection> parseSections(String markdownContent);
}
