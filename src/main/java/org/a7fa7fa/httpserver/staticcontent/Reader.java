package org.a7fa7fa.httpserver.staticcontent;

import org.a7fa7fa.httpserver.http.Context;
import org.a7fa7fa.httpserver.http.HttpParsingException;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;
import java.io.ByteArrayOutputStream;

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
        byte[] data = Files.readAllBytes(filePath);
        return data;
    }

    public static BufferedReader createBufferReader(String filePath) throws IOException {
        return new BufferedReader(new FileReader(filePath));
    }

    public static byte[] compress(byte[] file) throws IOException {

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        GZIPOutputStream zipStream = new GZIPOutputStream(byteStream);
        zipStream.write(file);
        zipStream.close();
        byte[] byteBody = byteStream.toByteArray();
        return byteBody;

    }
}
