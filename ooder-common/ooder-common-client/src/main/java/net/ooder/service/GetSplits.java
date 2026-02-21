/**
 * $RCSfile: GetSplits.java,v $
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

import net.ooder.common.expression.ParseException;
import net.ooder.common.expression.function.AbstractFunction;
import net.ooder.common.util.StringUtility;

import java.util.ArrayList;
import java.util.List;

public class GetSplits extends AbstractFunction {
	
	public synchronized List perform(List<String> obj, String delim) throws ParseException
    {
    	List listkey= new ArrayList<String>();
    	if (obj==null||delim==null){
    		return listkey;
    	}
    		if (List.class.isAssignableFrom(obj.getClass())){
    			List<String> sub=(List)obj;
    			if (sub.size()==0){
    				listkey.add(new ArrayList<String>());
    			}else{
				  for(int k=0;k<sub.size();k++){
				    listkey.add(splitsStr(sub.get(k),delim));
				  }
				}
    		
    		}
    	
      	 return listkey;
    }
	public List splitsStr(String obj,String delim){
		List listkey= new ArrayList<String>();
		String[] values= StringUtility.split((String) obj, delim);
    	for(int i=0;i<values.length;i++){
  
    		 if (values[i]!=null && !values[i].equals("")){
    			listkey.add(values[i]);
    		 }
    		}
	   return listkey;
	}
	
	
}