/**
 * $RCSfile: ExpressionTempBean.java,v $
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

import net.ooder.annotation.ServiceStatus;
import net.ooder.common.ContextType;
import net.ooder.common.EsbFlowType;
import net.ooder.common.FormulaType;
import net.ooder.common.TokenType;

import java.util.List;

public class ExpressionTempBean implements ServiceBean {

    private String name;
    private String id;
    private FormulaType type;
    private ServiceStatus status = ServiceStatus.normal;
    private String expressionArr;
    private List<ExpressionParameter> params;
    private String desc;
    private String returntype;
    private String filter;
    private EsbFlowType flowType = EsbFlowType.localAction;
    private String mainClass;
    private String clazz;
    private TokenType tokenType = TokenType.guest;
    private ContextType dataType;
    private Long creatTime;
    private String path;
    private String jspUrl;
    private String serverUrl;
    private String serverKey;
    int version = 1;


    public ExpressionTempBean() {
    }


    public String getJspUrl() {
        return jspUrl;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public void setJspUrl(String jspUrl) {

        this.jspUrl = jspUrl;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpressionArr() {
        return this.expressionArr;
    }

    public void setExpressionArr(String expressionArr) {
        this.expressionArr = expressionArr;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ContextType getDataType() {
        return dataType;
    }

    public void setDataType(ContextType dataType) {
        this.dataType = dataType;
    }

    public String getReturntype() {
        return returntype;
    }

    public void setReturntype(String returntype) {
        this.returntype = returntype;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public EsbFlowType getFlowType() {
        return flowType;
    }

    @Override
    public String getExpression() {
        return expressionArr;
    }

    @Override
    public List<ExpressionParameter> getParams() {
        return params;
    }

    public void setFlowType(EsbFlowType flowType) {
        this.flowType = flowType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Long creatTime) {
        this.creatTime = creatTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerKey() {
        return serverKey;
    }

    public void setServerKey(String serverKey) {
        this.serverKey = serverKey;
    }


    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    @Override
    public FormulaType getType() {
        return type;
    }

    public void setType(FormulaType type) {
        this.type = type;
    }

    public void setParams(List<ExpressionParameter> params) {
        this.params = params;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ExpressionTempBean) {
            if (this.getId().equals(((ExpressionTempBean) obj).getId()) &&
                    this.getVersion() == ((ExpressionTempBean) obj).getVersion()
                    ) {
                return true;
            }
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return this.getId() + "[" + this.getExpressionArr() + "]";
    }

}