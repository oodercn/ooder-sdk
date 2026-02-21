/**
 * $RCSfile: ServiceBean.java,v $
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
package net.ooder.esb.config.manager;

import net.ooder.annotation.Enumstype;
import net.ooder.common.ContextType;
import net.ooder.common.EsbFlowType;
import net.ooder.common.FormulaType;
import java.util.List;

public interface ServiceBean<T extends Enumstype> {

    public ContextType getDataType();

    public EsbFlowType getFlowType();

    public String getExpression();

    public List<ExpressionParameter> getParams();

    public FormulaType getType();

    public String getId();

    public String getName();

    public String getClazz();

    public int getVersion();

    public Long getCreatTime();

    public String getPath();

    public String getServerUrl();

}