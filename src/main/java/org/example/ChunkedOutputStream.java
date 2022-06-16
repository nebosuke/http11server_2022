package org.example;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ChunkedOutputStream extends OutputStream {

    private static final String CRLF = "\r\n";

    private final int maxChunkSize;

    public static final int DEFAULT_MAX_CHUNK_SIZE = 1280;

    private final OutputStream wrapped;

    private final byte[] buffer;

    private int numBytesInBuffer = 0;

    public ChunkedOutputStream(OutputStream wrapped) {
        this(wrapped, DEFAULT_MAX_CHUNK_SIZE);
    }

    public ChunkedOutputStream(OutputStream wrapped, int maxChunkSize) {
        this.wrapped = wrapped;
        this.maxChunkSize = maxChunkSize;
        this.buffer = new byte[maxChunkSize];
    }

    @Override
    public void write(int b) throws IOException {
        buffer[numBytesInBuffer] = (byte) b;
        numBytesInBuffer++;
        if (numBytesInBuffer >= maxChunkSize) {
            flushChunk();
        }
    }

    @Override
    public void close() throws IOException {
        if (numBytesInBuffer > 0) {
            flushChunk();
        }
        sendLastChunk();
        // wrapped は Keep-Alive で使い回すので close() してはならない
    }

    private void flushChunk() throws IOException {
        writeAsIso8859(Integer.toHexString(numBytesInBuffer) + CRLF);
        wrapped.write(buffer, 0, numBytesInBuffer);
        writeAsIso8859(CRLF);
        numBytesInBuffer = 0;
    }

    private void sendLastChunk() throws IOException {
        writeAsIso8859("0" + CRLF + CRLF);
    }

    private void writeAsIso8859(String s) throws IOException {
        wrapped.write(s.getBytes(StandardCharsets.ISO_8859_1));
    }
}
