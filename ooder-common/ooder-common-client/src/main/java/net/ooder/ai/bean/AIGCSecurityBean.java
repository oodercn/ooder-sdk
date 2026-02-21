/**
 * $RCSfile: AIGCSecurityBean.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.ai.bean;

import net.ooder.annotation.AIGCSecurity;
import net.ooder.annotation.AnnotationType;
import net.ooder.annotation.CustomBean;
import net.ooder.web.util.AnnotationUtil;

/**
 * AIGC安全注解对应的Bean类
 */
@AnnotationType(clazz = AIGCSecurity.class)
public class AIGCSecurityBean implements CustomBean {
    private boolean contentAudit = true;
    private boolean sensitiveFilter = true;
    private String[] filterGroups = {"default"};
    private boolean accessControl = true;
    private String[] allowedRoles = {};
    private int privacyLevel = 2;

    public boolean isContentAudit() {
        return contentAudit;
    }

    public void setContentAudit(boolean contentAudit) {
        this.contentAudit = contentAudit;
    }

    public boolean isSensitiveFilter() {
        return sensitiveFilter;
    }

    public void setSensitiveFilter(boolean sensitiveFilter) {
        this.sensitiveFilter = sensitiveFilter;
    }

    public String[] getFilterGroups() {
        return filterGroups;
    }

    public void setFilterGroups(String[] filterGroups) {
        this.filterGroups = filterGroups;
    }

    public boolean isAccessControl() {
        return accessControl;
    }

    public void setAccessControl(boolean accessControl) {
        this.accessControl = accessControl;
    }

    public String[] getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(String[] allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    public int getPrivacyLevel() {
        return privacyLevel;
    }

    public void setPrivacyLevel(int privacyLevel) {
        this.privacyLevel = privacyLevel;
    }

    @Override
    public String toAnnotationStr() {
        return AnnotationUtil.toAnnotationStr(this);
    }
}