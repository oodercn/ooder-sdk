package net.ooder.server.httpproxy.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ResponseDataList {
    List dataStreamList = new LinkedList();

    public void addResponse( ResponseData data ) {
        dataStreamList.add( data );
    }

    public void addResponse( InputStream stream, long length ) {
        addResponse( stream, 0, length );
    }

    public void addResponse( InputStream stream, long offset, long length ) {
        addResponse( new InputStreamResponseData( stream, offset, length ) );
    }

    public PrintWriter addPrintWriter() {
        PrintWriterResponseData data = new PrintWriterResponseData();
        addResponse( data );
        return data.getPrintWriter();
    }

    public long getTotalLength() {
        long total = 0;
        for (Iterator it = dataStreamList.iterator(); it.hasNext();) {
            ResponseData responseData = (ResponseData) it.next();
            long len = responseData.getLength();
            total = ( total >= 0 && len > 0 ) ? total + len : -1;
        }
        return total;
    }
    
    
    

    public void sendData(OutputStream os, boolean isChunkedOk ) throws IOException {
        try {
            if( getTotalLength() < 0 && isChunkedOk ) {
                os = new ChunkedEncodingOutputStream( os );
            }
            for (Iterator it = dataStreamList.iterator(); it.hasNext();) {
                ResponseData responseData = (ResponseData) it.next();
                responseData.send( os );
            }
        } finally {
            dataStreamList.clear();
            os.flush();
            os.close();
        }
    }

    public void reset() {
        dataStreamList.clear();
    }

}
