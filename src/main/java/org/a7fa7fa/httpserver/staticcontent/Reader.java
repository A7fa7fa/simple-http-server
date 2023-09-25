package org.a7fa7fa.httpserver.staticcontent;

import org.a7fa7fa.httpserver.http.HttpParsingException;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Reader {
    private final static Logger LOGGER = LoggerFactory.getLogger(Reader.class);

    public static Path getFilePath(String webroot, String filePath ) throws HttpParsingException {
        Path path = Paths.get(webroot, filePath);

        if (Files.exists(path) && !Files.isDirectory(path)){
            return path;
        }
        path = Paths.get(webroot, filePath, "index.html");
        if (Files.exists(path)) {
            return path;
        }
        path = Paths.get(webroot, filePath, "index.htm");
        if (Files.exists(path)) {
            return path;
        }
        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_404_NOT_FOUND);
    }

    public static String probeContentType(Path path) {
        String fileType = null;
        try {
            fileType = Files.probeContentType(path);
        } catch (IOException e) {}
        return fileType;
    }

    public static long getFileSize(Path filePath) throws IOException {
        return Files.size(filePath);
    }

    public static byte[] readFile(Path filePath) throws IOException {
        return Files.readAllBytes(filePath);
    }

    public static FileInputStream readStreamFile(String filePath) throws IOException {
        return new FileInputStream(filePath);
    }
}
