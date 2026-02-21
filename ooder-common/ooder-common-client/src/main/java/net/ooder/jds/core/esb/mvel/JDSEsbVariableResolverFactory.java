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
 * $RCSfile: JDSEsbVariableResolverFactory.java,v $
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

import org.mvel2.CompileException;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.BaseVariableResolverFactory;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class JDSEsbVariableResolverFactory extends BaseVariableResolverFactory {
    /**
     * Holds the instance of the variables.
     */
    protected Map<String, ? extends Object> variables=new HashMap();

    //   private VariableResolverFactory nextFactory;

    private boolean cachingSafe = false;

    public JDSEsbVariableResolverFactory(Map<String, ? extends Object> variables) {
        this.variables = variables;
    }


    public JDSEsbVariableResolverFactory(Map<String, Object> variables, VariableResolverFactory nextFactory) {
        this.variables = variables;
        this.nextFactory = nextFactory;
    }

    public JDSEsbVariableResolverFactory(Map<String, Object> variables, boolean cachingSafe) {
        this.variables = variables;
        this.cachingSafe = cachingSafe;
    }

    public VariableResolver createVariable(String name, Object value) {
        VariableResolver vr = getVariableResolver(name);
        if (vr != null) {
            vr.setValue(value);
            return vr;
        }
        else {
            (vr = new JDSMapVariableResolver(variables, name, cachingSafe)).setValue(value);
            return vr;
        }
    }

    public VariableResolver createVariable(String name, Object value, Class<?> type) {
        VariableResolver vr = getVariableResolver(name);
        if (vr != null && vr.getType() != null) {
            throw new CompileException("variable already defined within scope: " + vr.getType() + " " + name, null, 0);
        }
        else {
            addResolver(name, vr = new JDSMapVariableResolver(variables, name, type, cachingSafe));
            vr.setValue(value);
            return vr;
        }
    }

    public VariableResolver getVariableResolver(String name) {
    	
        if (variables.containsKey(name)) {
            return variableResolvers != null && variableResolvers.containsKey(name) ? variableResolvers.get(name) :
                    new JDSMapVariableResolver(variables, name, cachingSafe);
        }
        else if (nextFactory != null) {
            return nextFactory.getVariableResolver(name);
        }
        return null;
    }

    public boolean isResolveable(String name) {
        if (variableResolvers != null && variableResolvers.containsKey(name)) {
            return true;
        }
        else if (variables != null && variables.containsKey(name)) {
            return true;
        }
        else if (nextFactory != null) {
            return nextFactory.isResolveable(name);
        }
        return false;
    }

    private void addResolver(String name, VariableResolver vr) {
        if (variableResolvers == null) variableResolvers = new HashMap<String, VariableResolver>();
        variableResolvers.put(name, vr);
    }


    public boolean isTarget(String name) {
        return variableResolvers.containsKey(name);
    }


    public Set<String> getKnownVariables() {
        Set<String> knownVars = new LinkedHashSet<String>();

        if (nextFactory == null) {
            if (variables != null) knownVars.addAll(variables.keySet());
            return knownVars;
        }
        else {
            if (variables != null) knownVars.addAll(variables.keySet());
            knownVars.addAll(nextFactory.getKnownVariables());
            return knownVars;
        }
    }
}
