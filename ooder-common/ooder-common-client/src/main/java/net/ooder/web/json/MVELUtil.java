/**
 * $RCSfile: MVELUtil.java,v $
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
package net.ooder.web.json;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.config.JDSUtil;
import net.ooder.context.JDSActionContext;
import net.ooder.jds.core.esb.EsbUtil;
import org.mvel2.templates.TemplateRuntime;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@EsbBeanAnnotation(id = "MVEL")
public class MVELUtil {

    public Object par(String experssion) {
        return EsbUtil.parExpression(experssion);
    }


    public Object eval(String experssion) {
        Map context = JDSActionContext.getActionContext().getContext();
        Object object = TemplateRuntime.eval(experssion, context);
        return object;
    }

    public Object evalFile(String fileName) {
        Object object = null;
        try {
            Path path = Paths.get(JDSUtil.getJdsRealPath(), fileName);
            object = TemplateRuntime.eval(new FileInputStream(path.toFile()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return object;
    }

}
