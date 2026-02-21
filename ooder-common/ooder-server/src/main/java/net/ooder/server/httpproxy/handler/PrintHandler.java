package net.ooder.server.httpproxy.handler;

import net.ooder.server.httpproxy.core.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class PrintHandler extends AbstractHandler implements Handler {

    public boolean handle(Request aRequest, Response aResponse) throws IOException {
        if( aRequest instanceof HttpRequest ) {
            HttpRequest request = (HttpRequest)aRequest;
            HttpResponse response = (HttpResponse) aResponse;
            StringBuffer buffer = new StringBuffer();

            buffer.append( request.toString() );
            buffer.append("\r\n");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            request.getHeaders().print( new InternetOutputStream( baos ) );
            if( request.getPostData() != null ) {
                baos.write( request.getPostData() );
            }
            buffer.append( baos.toString("UTF-8") );
            response.setMimeType("text/plain");
            PrintWriter out = response.getPrintWriter();
            out.write( buffer.toString() );
            return true;
        }
        return false;
    }

}
