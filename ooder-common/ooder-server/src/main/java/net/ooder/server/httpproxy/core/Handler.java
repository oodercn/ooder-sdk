package net.ooder.server.httpproxy.core;

import java.io.IOException;

public interface Handler {


    public static final String[] httpHeaderArr = new String[]{"origin", "referer", "cookie"};
    public static final String[] CKEY = new String[]{"preview", "public", "custom", "debug", "RAD", "root"};
    public static final String[] pattArr = new String[]{".js", ".cls", ".view", ".dyn", ".jsx", ".jsa", ".jsaa"};
    public static final String RefererHeard = "Referer";
    public static final String ProjectName = "projectName";
    public static final String ProjectVersionName = "projectVersionName";
    public static final String VVVERSION = "VVVERSION";

    public static final String CUSSCLASSNAME = "_currClassName_";
    public boolean initialize(String handlerName, Server server);

    public String getName();

    public boolean handle(Request request, Response response) throws IOException;

    public boolean shutdown(Server server);
}
