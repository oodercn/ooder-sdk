package net.ooder.scene.core;

/**
 * Skill 查询条件
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public class SkillQuery {

    private String keyword;
    private String category;
    private String status;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
