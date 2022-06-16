package org.example;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ChunkedOutputStreamTest {

    @Test
    public void test1() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ChunkedOutputStream chunkedOutputStream = new ChunkedOutputStream(out, 10);

        chunkedOutputStream.write("Hello World\r\nHello chunked world.".getBytes(StandardCharsets.UTF_8));

        chunkedOutputStream.close();

        var result = new String(out.toByteArray(), "UTF-8");

        Assert.assertEquals("", result);
    }
}
