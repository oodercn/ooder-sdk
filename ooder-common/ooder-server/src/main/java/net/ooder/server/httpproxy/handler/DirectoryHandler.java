package net.ooder.server.httpproxy.handler;

import net.ooder.common.JDSConstants;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.server.httpproxy.core.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectoryHandler extends AbstractHandler implements Handler {
    private static final Log log = LogFactory.getLog(JDSConstants.CONFIG_KEY, DirectoryHandler.class);
    private File root;

    public static final ConfigOption ROOT_OPTION = new ConfigOption( "root", true, "Directory path to share." );
    public static final ConfigOption CSS_OPTION = new ConfigOption( "css", false, "A URL of the stylesheet for theme of the directory listing." );

    public boolean initialize(String handlerName, Server server) {
        super.initialize( handlerName, server );
        root = new File( ROOT_OPTION.getProperty( server, handlerName ) );
        return true;
    }

    protected boolean handleBody( HttpRequest request, HttpResponse response ) throws IOException {
        log.info( "DirectoryHandler called." );
        try {
            String absoluteDir = root.getAbsolutePath();
            File directory = Http.translatePath( absoluteDir, request.getUrl().substring( getUrlPrefix().length() ) );

            if( !directory.isDirectory() ) {
                log.info( "Not a directory.");
                return false;
            }
            if( !Http.isSecure( absoluteDir, directory ) ) {
                log.warn( "Access denied to " + directory.getAbsolutePath() );
                return false;
            }
            log.warn( "Processing directory listing for " + directory.getAbsolutePath() );
            StringBuffer templateHeader = new StringBuffer();

            String decodedUrl = java.net.URLDecoder.decode( request.getUrl(), "UTF-8" );
            addFolderNavigation( templateHeader, decodedUrl );
            addTableHeaders( templateHeader );
            addFilesAndFolders( request, directory.listFiles(), templateHeader);
            addTableFooter( templateHeader );

            response.setMimeType( "text/html" );
            PrintWriter out = response.getPrintWriter();
            out.write( addHtmlHeader( request ) );
            out.write("<body>\n");
            out.write( templateHeader.toString() );
            out.write( "</body>\n" );
            out.write( "</html>" );
            return true;
        } catch( Exception e ) {
            log.error( "Request failed due to Exception.", e );
            response.sendError( HttpURLConnection.HTTP_INTERNAL_ERROR, "Exception during processing directory" );
            return true;
        }
    }

    private void addTableFooter(StringBuffer templateHeader) {
        templateHeader.append("</table>\n");
        templateHeader.append("</div>\n");
    }

    private String addHtmlHeader( HttpRequest request ) throws IOException {
        StringBuffer templateHeader = new StringBuffer();
        templateHeader.append( "<html>\n");
        templateHeader.append( "<head>\n");
        String css = CSS_OPTION.getProperty( server, handlerName );
        if(  css != null ) {
            templateHeader.append( "<link rel=\"stylesheet\" type=\"text/css\" href=\"" );
            templateHeader.append( request.createUrl( css ) );
            templateHeader.append( "\">\n");
        } else {
            addStyleDefintion(templateHeader);

        }
        templateHeader.append( "</head>\n");
        return templateHeader.toString();
    }

    private void addStyleDefintion(StringBuffer templateHeader) {
        templateHeader.append("<style>\n");
        templateHeader.append("body,td,ul,li,a,div,p,pre,span  {color: #333333; font-family: Verdana, Arial, Helvetica; font-size: 10pt;}\n");
        templateHeader.append("th {color:#333333; font-family: Verdana, Arial, Helvetica; font-size:10pt; text-align:center; }\n");
        templateHeader.append(".navigationbar {background-color:#7094b8;color:#f6f6ee;border-bottom:1px #666 solid;border-right:1px #666 solid;font:bold 11px tahoma,verdana,sans-serif;padding:3px 2px 3px 4px;margin-top:10px;}\n");
        templateHeader.append(".box { background-color:#d1dde9; border:1px #369 solid; border-top:0; padding:4px 4px 4px 4px; background-color:#77AADD}\n");
        templateHeader.append(".directory {	padding-top: 2px;padding-right: 0px;padding-bottom: 0px;padding-left: 16px;background-image: url(/web/folder16.gif);background-repeat: no-repeat;background-position: left center;}\n");
        templateHeader.append(".topHeader { padding-top: 2px;padding-right: 0px;padding-bottom: 0px;padding-left: 16px;background-image: url(/web/folder16.gif);background-repeat: no-repeat;background-position: left center;}\n");
        templateHeader.append("tr.tableheader { background-color: #ffffe4; }\n");
        templateHeader.append("tr.fileentry { background-color: #EEEEEE; }\n");
        templateHeader.append("tr.altfileentry { background-color: #FFFFFF; }\n");
        templateHeader.append("td.nameColumn { text-align: left; }\n");
        templateHeader.append("td.typeColumn { text-align: center; }\n");
        templateHeader.append("td.sizeColumn { text-align: right; }\n");

        templateHeader.append("a {color: #0000A0;font-family: Verdana, Arial, Helvetica;text-decoration:none;}\n");
        templateHeader.append("a:active {color: #FFFFFF; text-decoration : none;}\n");
        templateHeader.append("a:link {color: #336699; text-decoration : none;}\n");
        templateHeader.append("a:visited {color: #336699; text-decoration : none;}\n");
        templateHeader.append("a:hover {color: #000000; text-decoration : none;}\n");

        templateHeader.append("a.whitelink {color: #FFFFFF; text-decoration: none;}\n");
        templateHeader.append("a.whitelink:visited {color: #FFFFFF;}\n");
        templateHeader.append("a.whitelink:hover {color: #AAAAAA; }\n");
        templateHeader.append("</style>");
    }

    private void addFolderNavigation( StringBuffer templateHeader, String decodedUrl ) {
        templateHeader.append( "<div class=\"navigationbar\">\n");
        templateHeader.append( "<span class=\"topHeader\">\n");
        StringTokenizer token = new StringTokenizer( decodedUrl, "/" );
        StringBuffer buf = new StringBuffer( decodedUrl.length() );
        templateHeader.append("&nbsp;<a href=\"/\" class=\"whitelink\">[home]</a>\n");
        while( token.hasMoreElements() ) {
            String path = token.nextToken();
            buf.append( "/" );
            buf.append( path );
            templateHeader.append( "/" );
            templateHeader.append( "<a href=\"" );
            templateHeader.append( buf.toString() );
            templateHeader.append( "\" class=\"whitelink\">");
            templateHeader.append( path );
            templateHeader.append( "</a>" );
        }
        templateHeader.append( "</span>\n</div>\n");
    }

    private void addTableHeaders(StringBuffer templateHeader) {
        templateHeader.append("<div class=\"box\">\n");
        templateHeader.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"2\">\n" );
        templateHeader.append("<tr class=\"tableheader\">\n");
        templateHeader.append("<th>Name</td>");
        templateHeader.append("<th>Type</td>");
        templateHeader.append("<th>Size</td>");
        templateHeader.append("\n</tr>\n");
    }

    private void addFilesAndFolders( HttpRequest request, File[] files, StringBuffer templateHeader ) throws IOException {
        URI rootUri = this.root.toURI();
        ComparableComparator comp = new ComparableComparator();
        TreeMap dirMap = new TreeMap( comp );
        TreeMap fileMap = new TreeMap( comp );
        StringBuffer fileBuffer = new StringBuffer();
        for( int i = 0; i < files.length; i++ ) {
            fileBuffer.delete( 0, fileBuffer.length() );
            if( files[i].isDirectory() ) {
                String name = files[i].getName();
                fileBuffer.append( "<td class=\"nameColumn\">&nbsp;<span class=\"directory\">" );
                fileBuffer.append( "<small><a href=\"");
                fileBuffer.append( getHttpHyperlink( request, rootUri, files[i]) );
                fileBuffer.append( "\">&nbsp;" );
                fileBuffer.append( name );
                fileBuffer.append( "</a></small></span></td>\n" );
                fileBuffer.append( "<td class=\"typeColumn\">&nbsp;<small>Folder</small></td>\n" );
                fileBuffer.append( "<td class=\"sizeColumn\">&nbsp;</td>\n" );
                fileBuffer.append( "</tr>\n");
                dirMap.put( name, fileBuffer.toString() );
            } else {
                String absolutePath = files[i].getAbsolutePath();
                String mimeType = getMimeType( absolutePath );
                if( mimeType != null ) {
                    String name = files[i].getName();
                    fileBuffer.append( "<td class=\"nameColumn\"><small>&nbsp;<a href=\"");
                    fileBuffer.append( getHttpHyperlink( request, rootUri, files[i] ) );
                    fileBuffer.append( "\">" );
                    fileBuffer.append( name );
                    fileBuffer.append( "</a></small></td>\n" );
                    fileBuffer.append( "<td class=\"typeColumn\"><small>&nbsp;");
                    fileBuffer.append( mimeType );
                    fileBuffer.append( "</small></td>\n" );
                    fileBuffer.append( "<td class=\"sizeColumn\">&nbsp;<small>" );
                    fileBuffer.append( NumberFormat.getIntegerInstance().format( files[i].length() ) );
                    fileBuffer.append( "</small>&nbsp;</td>\n");
                    fileMap.put( name, fileBuffer.toString() );
                }
            }
        }
        int count = 0;
        count = writeOutMap(templateHeader, dirMap, count );
        count = writeOutMap(templateHeader, fileMap, count );
    }

    private int writeOutMap(StringBuffer templateHeader, TreeMap dirMap, int count) {
        String[] styles = { "fileentry", "altfileentry" };
        for( Iterator i = dirMap.keySet().iterator(); i.hasNext(); ) {
            templateHeader.append( "<tr class=\"");
            templateHeader.append( styles[ count % 2] );
            templateHeader.append( "\">\n");
            templateHeader.append( (String)dirMap.get( i.next() ) );
            templateHeader.append( "</tr>\n");
            count++;
        }

        return count;
    }

    private String getHttpHyperlink( HttpRequest request, URI directory, File file ) throws IOException {
        String prefixRelative = directory.relativize( file.toURI() ).getPath();
        String urlPrefix = getUrlPrefix();
        if( urlPrefix.endsWith("/") ) {
            return urlPrefix + prefixRelative;
        } else if( prefixRelative.startsWith("/") ) {
            return urlPrefix + prefixRelative;
        } else {
            return request.createUrl( urlPrefix + "/" + prefixRelative );
        }
    }

    /**
     * This orders directories by calling compareTo() method.
     */
    public static class ComparableComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Comparable c1 = (Comparable)o1;
            Comparable c2 = (Comparable)o2;

            return c1.compareTo( c2 );
        }
    }
}
