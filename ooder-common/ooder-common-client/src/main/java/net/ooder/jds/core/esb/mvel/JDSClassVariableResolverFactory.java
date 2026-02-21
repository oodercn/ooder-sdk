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
 * $RCSfile: JDSClassVariableResolverFactory.java,v $
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

import java.util.HashMap;
import java.util.Map;

import net.ooder.common.util.ClassUtility;
import net.ooder.esb.config.manager.JDSExpressionParserManager;

import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.BaseVariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.integration.impl.SimpleValueResolver;

public class JDSClassVariableResolverFactory extends BaseVariableResolverFactory {

    public JDSClassVariableResolverFactory() {
        super();

        variableResolvers = new HashMap<String, VariableResolver>();
    }

    public JDSClassVariableResolverFactory( VariableResolverFactory nextFactory) {
        Map<String, Object> classes =JDSExpressionParserManager.getExpressionTypeMap();

        this.nextFactory = nextFactory;

        this.variableResolvers = new HashMap<String,VariableResolver>();
        for (String s : classes.keySet()) {
            variableResolvers.put(s, new JDSClassVariableResolver(s,(Class) classes.get(s)));
        }
    }
    
    public VariableResolver getVariableResolver(String name) {
    	
       	try {
       		
			Class clazz=ClassUtility.loadClass(name);
			return   variableResolvers.get(clazz.getSimpleName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        if (isResolveable(name)) {
            if (variableResolvers != null && variableResolvers.containsKey(name)) {
                return variableResolvers.get(name);
            }
            else if (nextFactory != null) {
                return nextFactory.getVariableResolver(name);
            }
        }
        return null;
    }


    public VariableResolver createVariable(String name, Object value) {
        if (nextFactory == null) {
            nextFactory = new MapVariableResolverFactory(new HashMap());
        }

        return nextFactory.createVariable(name, value);
    }


    public VariableResolver createVariable(String name, Object value, Class type) {
        if (nextFactory == null) {
            nextFactory = new MapVariableResolverFactory(new HashMap());
        }

        return nextFactory.createVariable(name, value);
    }

    public Class addClass(Class clazz) {
        variableResolvers.put(getSimpleClassName(clazz), new SimpleValueResolver(clazz));
        return clazz;
    }

    public boolean isTarget(String name) {
        return variableResolvers.containsKey(name);
    }

    public boolean isResolveable(String name) {
     
        return true;
    }

    public void clear() {
        variableResolvers.clear();
    }

    public void setImportedClasses(Map<String, Class> imports) {
        if (imports == null) return;
        for (String var : imports.keySet()) {
            variableResolvers.put(var, new SimpleValueResolver(imports.get(var)));
        }
    }

    public Map<String, Object> getImportedClasses() {
        Map<String, Object> imports = new HashMap<String, Object>();
        for (String var : variableResolvers.keySet()) {
            imports.put(var, variableResolvers.get(var).getValue());
        }

        return imports;
    }

    /**
     * REMOVE THIS WITH JDK1.4 COMPATIBILITY!  COMPENSATES FOR LACK OF getSimpleName IN java.lang.Class -- DIE 1.4!
     *
     * @param cls -- class reference
     * @return Simple name of class
     */
    public static String getSimpleClassName(Class cls) {
     
            return cls.getSimpleName();
 
    }

}
