/**
 * $RCSfile: Sum.java,v $
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
package net.ooder.service;


import java.util.List;

import ognl.OgnlOps;

import net.ooder.common.expression.function.AbstractFunction;


public class Sum extends AbstractFunction{
	public Object  perform(List list) {
		Object sum=0;
		if (list.size()>0){
			 sum=list.get(0);
				if (sum==null){
					sum=0;
				}
				
				for(int k=1;k<list.size();k++){
					if (list.get(k)!=null){
						sum=OgnlOps.add(sum, list.get(k));
					}
					
				}
		}
	
		return sum;
		
	}

}
