/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

/**
 * $RCSfile: JDSMapPropertyAccessor.java,v $
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
package net.ooder.jds.core.esb.util;


import net.ooder.common.cache.CacheObject;
import net.ooder.esb.util.EsbFactory;
import ognl.MapPropertyAccessor;
import ognl.OgnlException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
/**
 * Implementation of PropertyAccessor that sets and gets properties by storing and looking
 * up values in Maps.
 *
 * @author Gabriel Zimmerman
 */
public class JDSMapPropertyAccessor extends MapPropertyAccessor {

    private static final Log _log = LogFactory.getLog(JDSMapPropertyAccessor.class);

    private static final String[] INDEX_ACCESS_PROPS = new String[]
            {"size", "isEmpty", "keys", "values"};

    public Object getProperty(Map context, Object target, Object name) throws OgnlException {

        if (_log.isDebugEnabled()) {
            _log.debug("Entering getProperty ("+context+","+target+","+name+")");
        }

        OgnlContextState.updateCurrentPropertyPath(context, name);
      
        if (name instanceof String && contains(INDEX_ACCESS_PROPS, (String) name)) {
            return super.getProperty(context, target, name);
        }

        Object result = null;

        try{
            result = super.getProperty(context, target, name);
           
        } catch(ClassCastException ex){
        }
      
        if (name instanceof String && ((String)name).startsWith("$")){
        	
        	if (result==null){
        		
        		result=	EsbFactory.par((String)name);
            }  
           // result = super.getProperty(context, target, name);             
        }
        if (result instanceof String && ((String)result).startsWith("$")){
        	result=		EsbFactory.par((String)name);
        }  

        
        if (result instanceof CacheObject ){
        	result=((CacheObject)result).object;
        }
        return result;
    }

    /**
     * @param array
     * @param name
     */
    private boolean contains(String[] array, String name) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(name)) {
                return true;
            }
        }

        return false;
    }

   
}

