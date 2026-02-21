package net.ooder.server.httpproxy.core;

import net.ooder.common.util.IOUtility;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputStreamResponseData implements ResponseData {
    InputStream theData;
    long offset = 0;
    long length = -1;

    private static final int SEND_BUFFER_SIZE = 4096;

    public InputStreamResponseData(InputStream theData, long length) {
        this.theData = theData;
        this.length = length;
    }

    public InputStreamResponseData(InputStream theData, long offset, long length) {
        this.theData = theData;
        this.offset = offset;
        this.length = length;
    }

    public long getLength() {
        if (offset < length) {
            return length - offset;
        } else {
            return -1;
        }
    }

    public void send(OutputStream os) throws IOException {
        theData.skip(offset);
        byte[] buffer = new byte[Math.min(SEND_BUFFER_SIZE, (int) (length > 0L ? length : Integer.MAX_VALUE))];
        int totalSent = 0;
        try {
//            startTransfer();
            while (true) {
                int bufLen = theData.read(buffer);
                if (bufLen < 0) {
                    break;
                }
//                notifyListeners( totalSent, length );
                os.write(buffer, 0, bufLen);
                totalSent += bufLen;
            }
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtility.shutdownStream(theData);
            // IOUtility.shutdownStream(os);
//            endTransfer();
        }

    }
}
