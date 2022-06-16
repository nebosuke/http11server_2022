package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Http11Processor {

    private final Logger LOGGER = LoggerFactory.getLogger(Http11Processor.class);

    private final String CRLF = "\r\n";

    public record Http11Request(String method, String path, String httpVersion, Map<String, String> headers) {
    }

    private final InputStream in;

    private final OutputStream out;

    public Http11Processor(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    public void process() throws IOException {
        Http11Request request = readRequest();

        sendResponse(new File("/Users/kensuke/tmp/htdocs/top.htm"));
    }

    private void sendResponse(File file) throws IOException {
        String contentType = "text/html"; // DEBUG

        // レスポンスヘッダーを構築する
        var responseHeaders = new StringBuilder();
        responseHeaders.append("HTTP/1.1 200 OK").append(CRLF);
        responseHeaders.append("Content-Type: ").append(contentType).append(CRLF);
        responseHeaders.append(CRLF);

        out.write(responseHeaders.toString().getBytes(StandardCharsets.UTF_8));

        byte[] buf = new byte[1280];
        try (FileInputStream fileIn = new FileInputStream(file)) {
            while (true) {
                int len = fileIn.read(buf);
                if (len < 0) {
                    break;
                } else if (len > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    private Http11Request readRequest() throws IOException {
        var reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));

        // 最初の1行を読み込みパースする
        String firstLine = reader.readLine();
        if (firstLine == null) {
            // 1バイトも読む前にソケットがクローズされたとき
            LOGGER.info("Cannot read any byte while reading request header");
            throw new IOException();
        }
        String[] tokens = firstLine.split(" ");
        if (tokens.length != 3) {
            LOGGER.info("Not a http request: {}", firstLine);
            throw new IOException();
        }

        var request = new Http11Request(tokens[0], tokens[1], tokens[2], new HashMap<>());

        // 2行目以降の Header: value の構造をパースして request.headers にセットする
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.length() == 0) {
                break;
            }
            int pos = line.indexOf(':');
            if (pos > 0 && pos < (line.length() - 1)) {
                String key = line.substring(0, pos);
                String val = line.substring(pos + 1).trim();
                request.headers.put(key, val);
            }
        }

        // Keep-Alive に対応するため reader をクローズしない

        return request;
    }
}
