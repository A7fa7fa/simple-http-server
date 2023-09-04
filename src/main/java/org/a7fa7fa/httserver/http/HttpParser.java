package org.a7fa7fa.httserver.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpParser {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);

    private static final int SP = 0x20; // 32
    private static final int CR = 0x0D; // 13
    private static final int LF = 0x0A; // 10
    private static final int COLON = 0x3A; // 58
    private static final int QUOTATION = 0x22; // 34

    public HttpParser() {
    }

    public HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException {
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
        HttpRequest httpRequest = new HttpRequest();

        try {
            this.parseRequestLine(reader, httpRequest);
            this.parseHeaders(reader, httpRequest);
            this.parseBody(reader, httpRequest);
        } catch (IOException e) {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_500_INTERNAL_SEVER_ERROR);
        }

        return httpRequest;

    }

    private void parseRequestLine(InputStreamReader reader, HttpRequest httpRequest) throws IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder();

        boolean methodParsed = false;
        boolean targetParsed = false;

        int _byte;
        while ((_byte = reader.read()) >= 0) {
            if (_byte == CR) {
                _byte = reader.read();
                if (_byte != LF) {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }

                if (!methodParsed || !targetParsed) {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
                try {
                    LOGGER.debug("Request line VERSION to process : {}", processingDataBuffer.toString());
                    httpRequest.setHttpVersion(processingDataBuffer.toString());
                } catch (BadHttpVersionException e) {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
                processingDataBuffer.delete(0, processingDataBuffer.length());

                LOGGER.info("Request processed : {}", httpRequest.toString());
                return;
            }

            if (_byte == SP) {
                if (!methodParsed) {
                    LOGGER.debug("Request line METHOD to process : {}", processingDataBuffer.toString());
                    httpRequest.setMethod(processingDataBuffer.toString());
                    processingDataBuffer.delete(0, processingDataBuffer.length());
                    methodParsed = true;
                } else if (!targetParsed) {
                    LOGGER.debug("Request line REQUEST TARGET to process : {}", processingDataBuffer.toString());
                    httpRequest.setRequestTarget(processingDataBuffer.toString());
                    processingDataBuffer.delete(0, processingDataBuffer.length());
                    targetParsed = true;
                }  else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            } else {
                processingDataBuffer.append((char)_byte);
                if (!methodParsed) {
                    if (processingDataBuffer.length() > HttpMethod.MAX_LENGTH) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_501_NOT_IMPLEMENTED);
                    }
                }
            }
        }
    }

    private String parseQuote(InputStreamReader reader) throws IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder();

        int _byte;
        while ((_byte = reader.read()) >= 0) {
            processingDataBuffer.append((char)_byte);
            if (_byte == QUOTATION) {
                return processingDataBuffer.toString();
            }
        }
        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);

    }

    private void parseHeaders(InputStreamReader reader, HttpRequest httpRequest) throws IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder();

        HttpHeader httpHeader = new HttpHeader();
        boolean fieldNameFound = false;

        int _byte;
        while ((_byte = reader.read()) >= 0) {
            if (_byte == QUOTATION) {
                processingDataBuffer.append((char)_byte);
                processingDataBuffer.append(parseQuote(reader));
            } else if (_byte == CR) {
                _byte = reader.read();
                if (_byte != LF) {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }

                if (processingDataBuffer.isEmpty()){
                    //empty row. separates header from body
//                    LOGGER.debug("Header processed : {}", httpRequest.getHeaders().toString());
                    return;
                }

                if (processingDataBuffer.toString().trim().isEmpty()){
                    if (fieldNameFound) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }
                }

                LOGGER.debug("Request line HEADER-VALUE to process : {}", processingDataBuffer.toString());
                httpHeader.setValue(processingDataBuffer.toString());
                processingDataBuffer.delete(0, processingDataBuffer.length());
                httpRequest.addHeader(httpHeader);
                httpHeader = new HttpHeader();
                fieldNameFound = false;

            } else {
                boolean isLeadingSpace = processingDataBuffer.isEmpty() && (char)_byte == SP;
                if (!isLeadingSpace) {
                    processingDataBuffer.append((char)_byte);
                }
                if (_byte == COLON && !fieldNameFound) {

                    if (processingDataBuffer.length() > 1 && processingDataBuffer.charAt(processingDataBuffer.length()-2) == SP) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }
                    LOGGER.debug("Request line HEADER-FIELD-NAME to process : {}", processingDataBuffer.toString());
                    httpHeader.setName(processingDataBuffer.toString());
                    fieldNameFound = true;
                    processingDataBuffer.delete(0, processingDataBuffer.length());
                }
            }


        }

    }

    private void parseBody(InputStreamReader reader, HttpRequest httpRequest) throws IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder();
        int contentLength = 0;
        HttpHeader header;
        if ((header = httpRequest.getHeader(HeaderName.CONTENT_LENGTH)) != null){
            contentLength = Integer.parseInt(header.getValue());

            if ((header = httpRequest.getHeader(HeaderName.TRANSFER_ENCODING)) != null) {
                // If a message is received with both a Transfer-Encoding and a Content-Length header field
                // the Transfer-Encoding overrides the Content-Length.
                // Such a message might indicate an attempt to
                // perform request smuggling (Section 9.5) or response splitting
                // (Section 9.4) and ought to be handled as an error.
                // A sender MUST remove the received Content-Length field prior to forwarding such a message downstream.
                // TODO
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_501_NOT_IMPLEMENTED);
            }
        }
        if ((header = httpRequest.getHeader(HeaderName.TRANSFER_ENCODING)) != null) {
            // TODO: Chunking to be implemented.
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_501_NOT_IMPLEMENTED);
        }


        if (contentLength == 0){
            LOGGER.debug("No body");
            return;
        }

        int _byte;
        while ((_byte = reader.read()) >= 0) {
            processingDataBuffer.append((char)_byte);
            if (processingDataBuffer.length() == contentLength){
                LOGGER.debug("Body read : {}",processingDataBuffer.toString());
                LOGGER.debug("Content-Length according Header : {}", contentLength);
                LOGGER.debug("Content body read : {}", processingDataBuffer.length());
                return;
            }
        }
    }
}
