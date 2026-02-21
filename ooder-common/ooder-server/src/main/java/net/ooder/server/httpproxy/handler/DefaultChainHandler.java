
package net.ooder.server.httpproxy.handler;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.server.httpproxy.core.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class DefaultChainHandler extends AbstractHandler implements Handler {
    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, DefaultChainHandler.class);


    public static String CHAIN = ".chain";

    public static final ConfigOption CHAIN_OPTION = new ConfigOption("chain", true, "A comma seperated list of handler names to chain together.");

    private List chain;

    public DefaultChainHandler() {
        super();
    }

    public boolean initialize(String handlerName, Server server) {
        super.initialize(handlerName, server);
        this.chain = new ArrayList();
        initializeChain(server);
        return true;
    }

    private void initializeChain(Server server) {
        StringTokenizer tokenizer = new StringTokenizer(CHAIN_OPTION.getProperty(server, handlerName), " ,");

        while (tokenizer.hasMoreTokens()) {
            String chainChildName = tokenizer.nextToken();
            try {
                Handler handler = (Handler) server.constructJdsObject(chainChildName);
                if (handler.initialize(chainChildName, server)) {
                    chain.add(handler);
                } else {
                    log.info(chainChildName + " was not initialized");
                }
            } catch (ClassCastException e) {
                log.error(chainChildName + " class does not implement the Handler interface.", e);
            }
        }

    }

    public boolean handle(Request request, Response response) throws IOException {
        boolean hasBeenHandled = false;
        long time = System.currentTimeMillis();
        for (Iterator i = chain.iterator(); i.hasNext() && !hasBeenHandled; ) {
            Handler handler = (Handler) i.next();
            hasBeenHandled = handler.handle(request, response);
        }
        if (!hasBeenHandled) {
            if (request instanceof HttpRequest) {
                log.warn("error  request  path" + ((HttpRequest) request).getPath());
            }
        }
        String path = ((HttpRequest) request).getPath();
        boolean isDefault = true;
        String[] pattens = new String[]{".js", ".css", ".jpg", ".gif"};
        for (String patten : pattens) {
            if (path.endsWith(patten)) {
                isDefault = false;
            }
        }
        if (isDefault){
            log.info(((HttpRequest) request).getPath() + " time=" + (System.currentTimeMillis() - time) + "ms");
        }

        return hasBeenHandled;
    }

    public boolean shutdown(Server server) {
        boolean success = true;
        if (chain != null) {
            for (Iterator i = chain.iterator(); i.hasNext(); ) {
                Handler current = (Handler) i.next();
                boolean currentSuccess = current.shutdown(server);
                success = success && currentSuccess;
            }
        }

        return success;
    }


}
