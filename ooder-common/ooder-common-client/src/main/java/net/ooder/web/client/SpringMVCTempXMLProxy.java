/**
 * $RCSfile: SpringMVCTempXMLProxy.java,v $
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
package net.ooder.web.client;


import java.util.Set;

import net.ooder.common.ContextType;
import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.esb.config.annotation.AbstractAnnotationtExpressionTempManager;
import net.ooder.esb.config.manager.EsbBean;
import net.ooder.esb.config.manager.ExpressionTempBean;

import net.ooder.web.util.AnnotationUtil;
import org.springframework.web.bind.annotation.RequestMapping;

public class SpringMVCTempXMLProxy extends AbstractAnnotationtExpressionTempManager {
	private static final Log logger = LogFactory.getLog(
			JDSConstants.CONFIG_KEY, SpringMVCTempXMLProxy.class);
	
	
	
	public SpringMVCTempXMLProxy(EsbBean esbBean) {
		this.esbBean=esbBean;
	}
 
	public void fillBean(Set<Class<?>> classList) {
		
		
		for (Class<?> clazz :classList) {
			RequestMapping mvcConsol =AnnotationUtil.getClassAnnotation(clazz,RequestMapping.class);
			if (mvcConsol != null) {
				ExpressionTempBean expressionTempBean = new ExpressionTempBean();
				String name =mvcConsol.value()[0]+"/";				
				String id =clazz.getSimpleName();
				expressionTempBean.setDataType(ContextType.Action);
				expressionTempBean.setExpressionArr("GetClientService(\""+clazz.getName()+"\",\""+esbBean.getServerUrl()+"\")");
				expressionTempBean.setClazz(GetClientService.class.getName());
				expressionTempBean.setId(id);
				expressionTempBean.setJspUrl(mvcConsol.value()[0]);
				expressionTempBean.setDesc(mvcConsol.value()[0]);
				expressionTempBean.setName(name);
				expressionTempBean.setServerUrl(esbBean.getServerUrl());
				expressionTempBean.setMainClass(clazz.getName());
				expressionTempBean.setReturntype(clazz.getSimpleName());
				this.nameMap.put(name, expressionTempBean);
				this.idMap.put(id, expressionTempBean);
				serviceBeanList.add(expressionTempBean);
				//System.out.println("$"+expressionTempBean.getId() + expressionTempBean.getExpressionArr());
			}

		}





	}



}