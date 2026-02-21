package net.ooder.server.httpproxy.handler;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.server.httpproxy.core.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedirectHandler extends AbstractHandler {


    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, RedirectHandler.class);
    public static final ConfigOption RULE_OPTION = new ConfigOption("rule", true, "Regular expression for matching URLs." );
    public static final ConfigOption SUBST_OPTION = new ConfigOption( "subst", true, "The substiution expression to re-writing the new URL." );
    public static final ConfigOption INTERNAL_OPTION = new ConfigOption( "useInternal", "false", "Internal redirect without sending a response." );
    public static final ConfigOption REDIRECT_CODE_OPTION = new ConfigOption( "redirectCode", "302", "The HTTP code to send back to the client when the URL matches the rule." );

    Pattern rule;
    String substitution;
    boolean isInternalRedirect;
    int redirectHttpCode = HttpURLConnection.HTTP_MOVED_TEMP;

    public boolean initialize(String handlerName, Server server) {
        try {
            super.initialize(handlerName, server);
            String rulestr=RULE_OPTION.getProperty( server, handlerName );

            rule = Pattern.compile( RULE_OPTION.getProperty( server, handlerName ), Pattern.CASE_INSENSITIVE );
            substitution = SUBST_OPTION.getProperty( server, handlerName );
            isInternalRedirect = INTERNAL_OPTION.getBoolean(server, handlerName).booleanValue();
            try {
                redirectHttpCode = REDIRECT_CODE_OPTION.getInteger( server, handlerName ).intValue();
            } catch( NumberFormatException e ) {
                log.warn("redirectCode was not a number!  Defaulting to " + redirectHttpCode );
            }


                log.info( "Rule=" + rule.pattern() + ",subst=" + substitution + ",useInternal=" + isInternalRedirect + ",redirectCode=" + redirectHttpCode);

            return true;
        } catch( IllegalArgumentException e ) {
            log.error( e.toString() );
            return false;
        }
    }

    protected boolean isRequestdForHandler(HttpRequest request) {
        return !request.isInternal();
    }

    protected boolean handleBody(HttpRequest request, HttpResponse response) throws IOException {
        Matcher urlMatch = rule.matcher( request.getUrl() );
        StringBuffer buffer = null;
        if(urlMatch.find()) {
            if( buffer == null ) {
                buffer = new StringBuffer( substitution );
            }
            int lastIndex = 0;
            do {
                lastIndex = replaceGroupInSubst(buffer, urlMatch);
            } while( lastIndex < buffer.length() );

            if( isInternalRedirect ) {
                return server.post( new HttpRequest( buffer.toString(), server.getConfig(), true), response );
            } else {
                response.setStatusCode( redirectHttpCode );
                response.addHeader("Location", buffer.toString());
                return true;
            }
        } else {
            return false;
        }
    }

    private int replaceGroupInSubst(StringBuffer buffer, Matcher urlMatch) {
        int index = buffer.indexOf("${");
        if( index >= 0 ) {
            int endIndex = substitution.indexOf("}");
            String reference = substitution.substring( index + 2, endIndex );
            String subst = null;
            if( Character.isDigit( reference.charAt(0) ) ) {
                int group = Integer.parseInt( reference );
                subst = urlMatch.group( group );
            } else {
                subst = server.getProperty( subst );
            }
            if( subst != null ) {
                buffer.replace( index, endIndex+1, subst );
            }
            return endIndex + 1;
        } else {
            return buffer.length();
        }
    }
}
