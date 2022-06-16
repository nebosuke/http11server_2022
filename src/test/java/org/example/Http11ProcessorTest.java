package org.example;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class Http11ProcessorTest {
    
    @Test
    public void testParseRequest() {
        String wireProtocol = """
                GET /top.htm HTTP/1.1
                Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
                Upgrade-Insecure-Requests: 1
                Host: abehiroshi.la.coocan.jp
                User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.5 Safari/605.1.15
                Accept-Language: en-US,en;q=0.9
                Accept-Encoding: gzip, deflate
                Connection: keep-alive
                """;

        try (ByteArrayInputStream in = new ByteArrayInputStream(
                wireProtocol.getBytes(StandardCharsets.UTF_8));
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Method readRequest = Http11Processor.class.getDeclaredMethod("readRequest");
            readRequest.setAccessible(true);

            var processor = new Http11Processor(in, out);

            Http11Processor.Http11Request request = (Http11Processor.Http11Request) readRequest.invoke(
                    processor);

            Assert.assertEquals("GET", request.method());
            Assert.assertEquals("/top.htm", request.path());
            Assert.assertEquals("HTTP/1.1", request.httpVersion());

            Assert.assertEquals("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                    request.headers().get("Accept"));
        } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Assert.fail(e.getMessage());
        }
    }
}
