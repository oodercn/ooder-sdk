package net.ooder.server.httpproxy.handler;


import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.server.httpproxy.core.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.logging.Logger;

public class FileHandler extends AbstractHandler implements Handler {

    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, FileHandler.class);
    public static final ConfigOption ROOT_OPTION = new ConfigOption("root", true, "The path to the directory share files." );
    public static final ConfigOption DEFAULT_FILE_OPTION = new ConfigOption("default-file", "index.html", "The default file to send if no file is specified.");

    public static final String IF_MODIFIED = "If-Modified-Since";
    public static final String LAST_MODIFIED_KEY = "Last-Modified";
    public static final String RANGE_HEADER_KEY = "Range";

    private String root;
    private String defaultFile;

    public boolean initialize(String handlerName, Server server) {
        super.initialize(handlerName, server);
        root = ROOT_OPTION.getProperty( server, handlerName );
        defaultFile = DEFAULT_FILE_OPTION.getProperty( server, handlerName );
        return true;
    }

    protected boolean handleBody( HttpRequest request, HttpResponse response ) throws IOException {
        File file = Http.translatePath( root, request.getUrl().substring( getUrlPrefix().length() ) );
        if( !Http.isSecure( root, file ) ) {
            log.warn( "Access denied to " + file.getAbsolutePath() );
            return false;
        }
        request.putProperty( "file-path", file.getAbsolutePath() );
        if ( file.isDirectory() ) {
            file = new File( file, defaultFile );
        }
        if (file.exists() == false) {
            log.warn( "File " + file.getAbsolutePath() + " was not found." );
            return false;
        }
        String type = getMimeType( file.getName() );
        if( type != null ) {
            sendFile( request, response, file, type );
            return true;
        } else {
            log.warn( "Mime type for file " + file.getAbsolutePath() + " was not found." );
            return false;
        }
    }

    static public void sendFile( HttpRequest request, HttpResponse response, File file, String type ) throws IOException {
        if (file.isFile() == false) {
            response.sendError(HttpURLConnection.HTTP_NOT_FOUND, " not a normal file");
            return;
        }
        if (file.canRead() == false) {
            response.sendError(HttpURLConnection.HTTP_FORBIDDEN, " Permission Denied");
            return;
        }

        if( request.getRequestHeader(IF_MODIFIED) != null ) {
            try {
                long modified = Http.parseTime( request.getRequestHeader(IF_MODIFIED) );
                if( file.lastModified() <= modified ) {
                    response.setStatusCode( HttpURLConnection.HTTP_NOT_MODIFIED );
                    return;
                }
            } catch( ParseException ignore ) {
                // ignore the date.
            }
        }
        InputStream in = new BufferedInputStream( new FileInputStream(file) );
        response.addHeader(LAST_MODIFIED_KEY, Http.formatTime(file.lastModified()) );
        long[] range = getRange( request, file );
        response.setMimeType( type );
        response.sendResponse( in, range[0], range[1] );
    }

    private static long[] getRange( HttpRequest request, File file ) {
        long range[] = new long[2];
        range[0] = 0;
        range[1] = file.length();
        String rangeStr = request.getRequestHeader( RANGE_HEADER_KEY, "bytes=0-" );
        int equalSplit = rangeStr.indexOf("=") + 1;
        int split = rangeStr.indexOf("-");
        if( split < -1 ) {
            try {
                range[0] = Integer.parseInt( rangeStr.substring( equalSplit ) );
            } catch( NumberFormatException e ) {
            }
        } else {
            range[0] = Integer.parseInt( rangeStr.substring( equalSplit, split ) );
            if( split + 1 < rangeStr.length() ) {
                try {
                    range[1] = Integer.parseInt( rangeStr.substring( split + 1, rangeStr.length() ) );
                } catch( NumberFormatException e ) {
                }
            }
        }
        return range;
    }
}
