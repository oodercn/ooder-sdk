
/**
 * $RCSfile: JDSClassTemplateLoader.java,v $
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

import net.ooder.jds.core.esb.util.ClassLoaderUtil;
import freemarker.cache.URLTemplateLoader;

import java.net.URL;

public class JDSClassTemplateLoader extends URLTemplateLoader {
    protected URL getURL(String name) {
        return ClassLoaderUtil.getResource(name, getClass());
    }
}
