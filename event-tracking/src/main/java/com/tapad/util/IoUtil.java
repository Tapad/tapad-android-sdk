package com.tapad.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IoUtil {
    static final String CHARSET = "UTF-8"; // We only use UTF-8 content

    /**
     * Translates an input stream of some reasonable size to a String.
     *
     * @param input the input stream
     * @param contentLength the size of the content
     * @return the stream decoded as at UTF-8 string
     * @throws IOException
     */
    public static String inputStreamToString(InputStream input, int contentLength) throws IOException {
        byte[] buf = new byte[2048];
        ByteArrayOutputStream bout = new ByteArrayOutputStream(contentLength);
        int read = 0;
        while ( (read = input.read(buf, 0, buf.length)) != -1) {
            bout.write(buf, 0, read);
        }
        return bout.toString(CHARSET);
    }
}
