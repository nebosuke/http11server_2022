package org.example;

import java.io.File;

public class DocumentRootFileResolver implements FileResolver {

    private final File documentRoot;

    public DocumentRootFileResolver(String documentRoot) {
        this.documentRoot = new File(documentRoot);
    }

    @Override
    public File findFile(String path) {
        if (path == null || path.length() == 0) {
            return null;
        }

        // パストラバーサル攻撃を受け取らない
        if (path.contains("../")) {
            return null;
        }

        if ("/".equals(path)) {
            return new File(documentRoot, "index.html");
        }

        while (path.startsWith("/") && path.length() > 0) {
            path = path.substring(1);
        }

        return new File(documentRoot, path);
    }
}
