/**
 * $RCSfile: AIGCDataBean.java,v $
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


import net.ooder.annotation.*;
import net.ooder.web.util.AnnotationUtil;

/**
 * AIGCData注解对应的Bean类
 */
@AnnotationType(clazz = AIGCModel.AIGCData.class)
public class AIGCDataBean implements CustomBean {
private DataType type;
    private ProcessingType processing;
    private String validationRule;
    private int maxSize;
    private boolean persist;
    private int expireMinutes;

    // Getter和Setter方法
    public DataType getType() { return type; }
    public void setType(DataType type) { this.type = type; }

    public ProcessingType getProcessing() { return processing; }
    public void setProcessing(ProcessingType processing) { this.processing = processing; }

    public String getValidationRule() { return validationRule; }
    public void setValidationRule(String validationRule) { this.validationRule = validationRule; }

    public int getMaxSize() { return maxSize; }
    public void setMaxSize(int maxSize) { this.maxSize = maxSize; }

    public boolean isPersist() { return persist; }
    public void setPersist(boolean persist) { this.persist = persist; }

    public int getExpireMinutes() { return expireMinutes; }
    public void setExpireMinutes(int expireMinutes) { this.expireMinutes = expireMinutes; }

    @Override
    public String toAnnotationStr() {
        return AnnotationUtil.toAnnotationStr(this);
    }
}