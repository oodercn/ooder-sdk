/**
 * $RCSfile: ExpressionTempAnnotationProxy.java,v $
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
package net.ooder.esb.config.annotation;


import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.common.util.Constants;
import net.ooder.esb.config.manager.EsbBean;

import java.util.*;

public class ExpressionTempAnnotationProxy extends AbstractAnnotationtExpressionTempManager {
    private static final Log logger = LogFactory.getLog(
            Constants.CONFIG_KEY, ExpressionTempAnnotationProxy.class);


    public ExpressionTempAnnotationProxy(EsbBean esbBean) {
        this.esbBean = esbBean;

    }

    public void fillBean(Set<Class<?>> classList) {
        List<String> tableNameList = new ArrayList<String>();


        for (Class<?> clazz : classList) {
//
//            DBTable dbTable = clazz.getAnnotation(DBTable.class);
//            Map valueMap = new HashMap();
//            if (dbTable != null) {
//                valueMap.put("configKey", dbTable.configKey());
//                tableNameList.add(dbTable.tableName());
//            }
//            valueMap.put("tableNames", tableNameList);
            Map valueMap = new HashMap();
            fillBean(clazz, valueMap);
        }
        Map<String, Class> classNameMap = new HashMap();
        classNameMap.putAll(ClassUtility.getDynClassMap());
        Set<String> classNameSet = classNameMap.keySet();
        for (String className : classNameSet) {
            Map valueMap = new HashMap();
            fillBean(classNameMap.get(className), valueMap);
        }
    }

}