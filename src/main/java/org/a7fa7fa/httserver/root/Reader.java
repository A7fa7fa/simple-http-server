package org.a7fa7fa.httserver.root;

import org.a7fa7fa.httserver.http.HttpParsingException;
import org.a7fa7fa.httserver.http.HttpStatusCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Reader {

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

    public static long getFileSize(Path filePath) throws IOException {
        return Files.size(filePath);
    }

    public static byte[] readFile(Path filePath) throws IOException {
        byte[] data = Files.readAllBytes(filePath);
        return data;
    }
}
