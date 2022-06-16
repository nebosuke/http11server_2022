package org.example;

import java.io.File;

public class ContentTypeRegistry {

    public static String getContentType(File file) {
        String fileName = file.getName();
        int pos = fileName.lastIndexOf('.');
        if (pos < 0 || pos > (fileName.length() - 1)) {
            return "application/octet-stream";
        }
        String extension = fileName.substring(pos + 1).toLowerCase();
        switch (extension) {
        case "jpg":
        case "jpeg":
            return "image/jpeg";
        case "png":
            return "image/png";
        case "gif":
            return "image/gif";
        case "txt":
            return "text/plain";
        case "html":
        case "htm":
            return "text/html";
        }
        return "application/octet-stream";
    }
}
