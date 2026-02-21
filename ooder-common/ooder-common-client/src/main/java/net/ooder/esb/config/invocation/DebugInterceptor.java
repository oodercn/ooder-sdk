/**
 * $RCSfile: DebugInterceptor.java,v $
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
package net.ooder.esb.config.invocation;

import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.Constants;
import net.ooder.esb.util.DebugRun;
import org.mvel2.ast.ASTNode;
import org.mvel2.integration.Interceptor;
import org.mvel2.integration.VariableResolverFactory;

public class DebugInterceptor implements Interceptor {
    private static final Log logger = LogFactory.getLog(
            Constants.CONFIG_KEY, DebugInterceptor.class);

    DebugRun debug = new DebugRun();

    public int doBefore(ASTNode node, VariableResolverFactory factory) {
        logger.info("************************************************");
        logger.info("----- expression " + node.getName() + " par start");
        debug.setName(node.getName());
        debug.setStartTime(System.currentTimeMillis());
        return 0;
    }

    public int doAfter(Object val, ASTNode node, VariableResolverFactory factory) {
        debug.setEndTime(System.currentTimeMillis());
        logger.info("************************************************");
        long timeLoadOrg = System.currentTimeMillis() - debug.getStartTime();
        logger.info("----- par " + debug.getName() + " in " + timeLoadOrg
                / Constants.SECOND + "s:" + timeLoadOrg % Constants.SECOND
                + " -----");
        logger.info("************************************************");

        return 0;
    }

}
