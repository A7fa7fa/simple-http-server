package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.exceptions.BadHttpVersionException;
import org.a7fa7fa.httpserver.http.exceptions.HttpParsingException;
import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpMethod;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
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

    public HttpRequest parseHttpRequest(InputStream inputStream, int maxBodySize) throws HttpParsingException {
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
        HttpRequest httpRequest = new HttpRequest();

        try {
            this.parseRequestLine(reader, httpRequest);
            this.parseHeaders(reader, httpRequest);
            this.validateHeaders(httpRequest);
            this.parseBody(reader, httpRequest, maxBodySize);
        } catch (IOException e) {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_500_INTERNAL_SEVER_ERROR);
        }

        return httpRequest;

    }

    private void validateHeaders(HttpRequest httpRequest) throws HttpParsingException {
        HttpHeader mandatoryHeader = httpRequest.getHeader(HeaderName.HOST);
        if (mandatoryHeader == null) {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
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

                boolean isEmptyLinePriorRequestLine = !methodParsed && !targetParsed && processingDataBuffer.isEmpty();
                if (isEmptyLinePriorRequestLine) {
                    continue;
                }

                if (!methodParsed || !targetParsed) {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }


                try {
                    LOGGER.trace("Request line VERSION to process : {}", processingDataBuffer.toString());
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
                    LOGGER.trace("Request line METHOD to process : {}", processingDataBuffer.toString());
                    httpRequest.setMethod(processingDataBuffer.toString());
                    processingDataBuffer.delete(0, processingDataBuffer.length());
                    methodParsed = true;
                } else if (!targetParsed) {
                    LOGGER.trace("Request line REQUEST TARGET to process : {}", processingDataBuffer.toString());
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

    private int toLowercase(int _char) {
        if (_char > 64 && _char < 91) {
            return _char +32;
        }
        return _char;
    }

    private boolean isInHeaderNameCharacterSet(int _char) {
        return (_char > 31 && _char < 127);
    }

    private void parseHeaders(InputStreamReader reader, HttpRequest httpRequest) throws IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder();

        HttpHeader httpHeader = new HttpHeader();
        boolean headerNameFound = false;

        int _byte;
        while ((_byte = reader.read()) >= 0) {
            boolean startOfQuotation = _byte == QUOTATION;
            if (startOfQuotation) {
                processingDataBuffer.append((char) _byte);
                processingDataBuffer.append(parseQuote(reader));
                continue;
            }
            boolean isStartOfLineBreak = _byte == CR;
            if (isStartOfLineBreak) {
                _byte = reader.read();
                if (_byte != LF) {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }

                boolean isEmptyLineHeaderBlockIsOver = processingDataBuffer.isEmpty();
                if (isEmptyLineHeaderBlockIsOver) {
                    LOGGER.trace("Header processed : {}", httpRequest.getHeaders().size());
                    return;
                }

                boolean hasNameWithoutValue = processingDataBuffer.isEmpty() && headerNameFound;
                if (hasNameWithoutValue) {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }

                LOGGER.trace("Request line HEADER-VALUE to process : {}", processingDataBuffer.toString());
                httpHeader.setValue(processingDataBuffer.toString());
                processingDataBuffer.delete(0, processingDataBuffer.length());
                httpRequest.addHeader(httpHeader);
                LOGGER.trace(httpHeader.toString());
                httpHeader = new HttpHeader();
                headerNameFound = false;
                continue;
            }

            if (!this.isInHeaderNameCharacterSet(_byte)) {
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }

            boolean isLeadingSpace = processingDataBuffer.isEmpty() && (char) _byte == SP;
            if (!isLeadingSpace) {
                if (!headerNameFound) _byte = (char) this.toLowercase(_byte);
                processingDataBuffer.append((char) _byte);
            }

            boolean isSpacePrecededLine = isLeadingSpace && !headerNameFound;
            // TODO: Obsolete Line Folding
            if (isSpacePrecededLine) {
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }

            boolean headerNameContainsSpace = !processingDataBuffer.isEmpty() && (char) _byte == SP && !headerNameFound;
            if (headerNameContainsSpace) {
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
            }

            boolean endOfHeaderNameFound = _byte == COLON && !headerNameFound;
            if (endOfHeaderNameFound) {
                LOGGER.trace("Request line HEADER-FIELD-NAME to process : {}", processingDataBuffer.toString());
                httpHeader.setName(processingDataBuffer.toString());
                headerNameFound = true;
                processingDataBuffer.delete(0, processingDataBuffer.length());
            }
        }
    }


    private void parseBody(InputStreamReader reader, HttpRequest httpRequest, int maxBodySize) throws IOException, HttpParsingException {
        StringBuilder processingDataBuffer = new StringBuilder();
        int contentLength = 0;
        HttpHeader contentLengthHeader = httpRequest.getHeader(HeaderName.CONTENT_LENGTH);
        HttpHeader transferEncodingHeader = httpRequest.getHeader(HeaderName.TRANSFER_ENCODING);
        if (contentLengthHeader != null && transferEncodingHeader != null){
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
        if (contentLengthHeader != null){
            contentLength = Integer.parseInt(contentLengthHeader.getValue());
        }
        if (contentLengthHeader == null && transferEncodingHeader != null) {
            // If a message is received with both a Transfer-Encoding and a Content-Length header field
            // the Transfer-Encoding overrides the Content-Length.
            // Such a message might indicate an attempt to
            // perform request smuggling (Section 9.5) or response splitting
            // (Section 9.4) and ought to be handled as an error.
            // A sender MUST remove the received Content-Length field prior to forwarding such a message downstream.
            // TODO: Chunking to be implemented.
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_501_NOT_IMPLEMENTED);
        }

        if (contentLength == 0){
            LOGGER.trace("No body");
            return;
        }

        if (contentLength > maxBodySize){
            LOGGER.trace("To big Body.");
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }

        int _byte;
        while ((_byte = reader.read()) >= 0) {
            processingDataBuffer.append((char)_byte);
            if (processingDataBuffer.length() == contentLength){
                LOGGER.trace("Body read : {}",processingDataBuffer.toString());
                LOGGER.trace("Content-Length according Header : {}", contentLength);
                LOGGER.trace("Content body read : {}", processingDataBuffer.length());
                httpRequest.setBody(processingDataBuffer.toString());
                processingDataBuffer.delete(0, processingDataBuffer.length());
                return;
            }
        }
    }
}
