/*
 * $Id: ScopesHashModel.java,v 1.1 2025/07/08 00:25:54 Administrator Exp $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 * $RCSfile: JDSScopesHashModel.java,v $
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
package net.ooder.template;


import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.context.JDSActionContext;
import net.ooder.esb.util.ESBConstants;
import net.ooder.esb.util.EsbFactory;
import net.ooder.jds.core.esb.util.ValueStack;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;


public class JDSScopesHashModel extends SimpleHash {
    private static final Log logger = LogFactory.getLog(ESBConstants.CONFIG_KEY, JDSScopesHashModel.class);
    private static final long serialVersionUID = 5551686380141886764L;

    private ValueStack stack;


    public JDSScopesHashModel(ObjectWrapper objectWrapper, ValueStack stack) {
        super(objectWrapper);
        this.stack = stack;
    }


    public TemplateModel get(String key) throws TemplateModelException {
        TemplateModel model = super.get(key);
        try {

            if (model != null) {
                return model;
            }
            Object obj = null;


            if (key.startsWith("$")) {
                obj = EsbFactory.par(key, JDSActionContext.getActionContext().getContext(), null);
                if (obj != null) {
                    return wrap(obj);
                }
            } else {

                if (stack != null) {
                    obj = stack.getContext().get(key);
                    if (obj == null) {
                        obj = stack.findValue(key);
                    }
                }


                if (obj == null) {
                    obj = JDSActionContext.getActionContext().getContext().get(key);
                }

                if (obj == null) {
                    obj = JDSActionContext.getActionContext().getParams(key);
                }

                if (obj != null) {
                    return wrap(obj);
                }


            }

        } catch (Throwable e) {
            logger.error(e);
        }
        return wrap("null");
    }
}
