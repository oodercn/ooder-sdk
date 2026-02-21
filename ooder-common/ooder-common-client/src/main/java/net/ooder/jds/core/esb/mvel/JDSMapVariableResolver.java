/**
 * MVEL (The MVFLEX Expression Language)
 *
 * Copyright (C) 2007 Christopher Brock, MVFLEX/Valhalla Project and the Codehaus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
/**
 * $RCSfile: JDSMapVariableResolver.java,v $
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
package net.ooder.jds.core.esb.mvel;

import static org.mvel2.DataConversion.canConvert;
import static org.mvel2.DataConversion.convert;

import java.util.Map;

import net.ooder.common.cache.CacheObject;
import net.ooder.esb.util.EsbFactory;

import org.mvel2.CompileException;
import org.mvel2.integration.VariableResolver;

public class JDSMapVariableResolver implements VariableResolver {
    private String name;
    private Class<?> knownType;
    private Map variableMap;

    private boolean cache = false;

    public JDSMapVariableResolver(Map variableMap, String name) {
        this.variableMap = variableMap;
        this.name = name;
    }

    public JDSMapVariableResolver(Map variableMap, String name, Class knownType) {
        this.name = name;
        this.knownType = knownType;
        this.variableMap = variableMap;
    }

    public JDSMapVariableResolver(Map variableMap, String name, boolean cache) {
        this.variableMap = variableMap;
        this.name = name;
        this.cache = cache;
    }

    public JDSMapVariableResolver(Map variableMap, String name, Class knownType, boolean cache) {
        this.name = name;
        this.knownType = knownType;
        this.variableMap = variableMap;
        this.cache = cache;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setStaticType(Class knownType) {
        this.knownType = knownType;
    }

    public void setVariableMap(Map variableMap) {
        this.variableMap = variableMap;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return knownType;
    }

    public void setValue(Object value) {
        if (knownType != null && value != null && value.getClass() != knownType) {
            if (!canConvert(knownType, value.getClass())) {
                throw new CompileException("cannot assign " + value.getClass().getName() + " to type: "
                        + knownType.getName(), null, 0);
            }
            try {
                value = convert(value, knownType);
            }
            catch (Exception e) {
                throw new CompileException("cannot convert value of " + value.getClass().getName()
                        + " to: " + knownType.getName(), null, 0, e);
            }
        }

        variableMap.put(name, value);
    }

    public Object getValue() {

        Object result = variableMap.get(name);

     
      
        if (name instanceof String && ((String)name).startsWith("$")){
        	
        	if (result==null){
        		result=EsbFactory.par((String)name);
        		//result=	ESBDateInit.getContextRootById((String)name);
            }  
           // result = super.getProperty(context, target, name);             
        }
        if (result instanceof String && ((String)result).startsWith("$")){
        	result=EsbFactory.par((String)name);
        }  

        
        if (result instanceof CacheObject ){
        	result=((CacheObject)result).object;
        }
      
        return result;
    }

    public int getFlags() {
        return 0;
    }


    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }
}
