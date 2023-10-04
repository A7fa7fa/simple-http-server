package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.parser.ByteProcessor;
import org.a7fa7fa.httpserver.staticcontent.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public class ResponseProcessor {

    private final static Logger LOGGER = LoggerFactory.getLogger(ResponseProcessor.class);

    private final HttpResponse httpResponse;
    private final OutputStream outputStream;


    public ResponseProcessor(HttpResponse httpResponse, OutputStream outputStream) {
        this.httpResponse = httpResponse;
        this.outputStream = outputStream;
    }

    HttpResponse getResponse() {
        return this.httpResponse;
    }

    void setContentTypeIfClientUnderstands(HttpRequest httpRequest, String contentType) throws HttpParsingException {
        LOGGER.debug("Content type : " + contentType);
        if (contentType != null) {
            if (httpRequest.clientNotUnderstandsType(contentType)) {
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_415_UNSUPPORTED_MEDIA_TYPE);
            }
        }
        this.httpResponse.setContentType(contentType);
    }

    byte[] readDataFromAbsoluteFile(HttpRequest httpRequest, String fileLocation) throws HttpParsingException, IOException {
        Path targetFilePath = Reader.getFilePath("", fileLocation);
        String contentType = Reader.probeContentType(targetFilePath);
        this.setContentTypeIfClientUnderstands(httpRequest, contentType);
        return Reader.readFile(targetFilePath);
    }

    byte[] readDataFromFile(HttpRequest httpRequest, String webroot) throws HttpParsingException, IOException {
        String requestTarget = httpRequest.getRequestTarget();
        Path targetFilePath = Reader.getFilePath(webroot, requestTarget);
        String contentType = Reader.probeContentType(targetFilePath);
        this.setContentTypeIfClientUnderstands(httpRequest, contentType);
        LOGGER.debug("Target file path : " + targetFilePath);
        return Reader.readFile(targetFilePath);
    }

    void prepareResponse(byte[] fileContent, HttpRequest httpRequest, int gzipMinFileSizeKb) throws IOException {

        if (this.httpResponse.getContentType() != null) {
            this.httpResponse.addHeader(new HttpHeader(HeaderName.CONTENT_TYPE, this.httpResponse.getContentType()));
        }

        // TODO You should not allow your web server to compress image files or PDF files
        // these files are already compressed and by compressing them again you’re not only wasting CPU resources but you can actually make the resulting file larger by compressing them again
        String encodingToken = "gzip";
        HttpHeader encodingHeader = httpRequest.getHeader(HeaderName.ACCEPT_ENCODING);
        if (encodingHeader != null && encodingHeader.getValue().contains(encodingToken) && fileContent.length / 1024 >  gzipMinFileSizeKb) {
            int sizeBeforeCompressing = fileContent.length;
            fileContent = ByteProcessor.compress(fileContent);
            this.httpResponse.addHeader(new HttpHeader(HeaderName.CONTENT_ENCODING, encodingToken));
            LOGGER.debug("Request encoded : {} - size kb before/after {}/{}", encodingToken, sizeBeforeCompressing, fileContent.length);
        }
        this.httpResponse.addBody(fileContent);
        this.httpResponse.addHeader(new HttpHeader(HeaderName.CONTENT_LENGTH, String.valueOf(fileContent.length)));
        LOGGER.debug("Request prepared and added to response");
    }

    void sendFullMessage() throws ClientDisconnectException {
        this.pipe(this.httpResponse.buildCompleteMessage());
    }

    void sendWithoutBody() throws ClientDisconnectException {
        this.pipe(this.httpResponse.buildStatusWithHeaders());
    }

    void sendChunk(byte[] data) throws ClientDisconnectException {
        byte[] chunk = this.httpResponse.createChunk(data);
        this.pipe(chunk);
    }

    public void endStream() throws ClientDisconnectException {
        byte[] chunk = this.createEndStreamChunk();
        this.pipe(chunk);
    }

    public void streamFromStream(InputStream inputStream, int chunkSize) throws ClientDisconnectException, IOException {
        byte[] out;
        try {
            while ((out = inputStream.readNBytes( chunkSize)).length > 0) {
                this.sendChunk(out);
            }
        } finally {
            this.endStream();
            try {
                inputStream.close();
            } catch (IOException e) {}
        }
    }

    public void pipe(byte[] data) throws ClientDisconnectException {
        this.httpResponse.setAlreadySend(true);
        if (this.httpResponse.getStatusCode() == null) {
            LOGGER.warn("Status code not set");
            throw new RuntimeException("Status code not set");
        }
        try {
            this.outputStream.write(data);
        } catch (IOException e) {
            throw new ClientDisconnectException(e);
        }
    }

    private byte[] createEndStreamChunk() {
        return this.httpResponse.createChunk(new byte[0]);
    }


}
